/**
 * 聊天消息本地存储工具 - 使用增强型存储方案
 */

// 缓存键前缀
const CHAT_MESSAGE_PREFIX = 'chat_msg_';
const CHAT_CONVERSATION_PREFIX = 'chat_conv_';
const CHAT_FRIEND_LIST_KEY = 'chat_friends';
const MESSAGE_HASH_PREFIX = 'msg_hash_';

// 消息存储池 - 内存缓存
const messagePool = new Map();
const hashPool = new Set();

/**
 * 生成消息唯一哈希值
 * @param {Object} message 消息对象
 * @returns {String} 哈希值
 */
function generateMessageHash(message) {
  const hashData = {
    senderId: message.senderId,
    receiverId: message.receiverId,
    content: message.content,
    type: message.type,
    // 时间戳精确到分钟，避免毫秒级差异
    timeMinute: Math.floor(new Date(message.createTime).getTime() / 60000)
  };
  
  // 使用更安全的编码方式处理中文字符
  try {
    const jsonStr = JSON.stringify(hashData);
    // 使用encodeURIComponent处理中文，然后转换为简单哈希
    const encoded = encodeURIComponent(jsonStr);
    let hash = 0;
    for (let i = 0; i < encoded.length; i++) {
      const char = encoded.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // 转换为32位整数
    }
    return Math.abs(hash).toString(36); // 转换为36进制字符串
  } catch (e) {
    console.error('生成消息哈希失败:', e);
    // 降级方案：使用简单的字符串拼接
    return `${message.senderId}_${message.receiverId}_${message.type}_${Math.floor(new Date(message.createTime).getTime() / 60000)}`;
  }
}

/**
 * 检查消息是否重复
 * @param {String} conversationId 会话ID
 * @param {Object} message 消息对象
 * @returns {Boolean} 是否重复
 */
function isDuplicateMessage(conversationId, message) {
  const hash = generateMessageHash(message);
  const hashKey = `${conversationId}_${hash}`;
  
  // 检查内存缓存
  if (hashPool.has(hashKey)) {
    console.log('消息重复(内存缓存):', message.content);
    return true;
  }
  
  // 检查本地存储的哈希
  try {
    const storedHash = uni.getStorageSync(MESSAGE_HASH_PREFIX + hashKey);
    if (storedHash) {
      console.log('消息重复(本地存储):', message.content);
      hashPool.add(hashKey); // 添加到内存缓存
      return true;
    }
  } catch (e) {
    console.error('检查消息哈希失败:', e);
  }
  
  return false;
}

/**
 * 保存聊天消息到本地 - 增强版
 * @param {String} conversationId 会话ID
 * @param {Object} message 消息对象
 */
export function saveMessage(conversationId, message) {
  if (!conversationId || !message) {
    console.warn('保存消息参数无效:', { conversationId, message });
    return;
  }
  
  // 检查消息重复
  if (isDuplicateMessage(conversationId, message)) {
    return;
  }
  
  // 获取已有消息
  const messages = getMessages(conversationId);
  
  // 额外的ID重复检查
  const existsByIds = messages.some(item => {
    if (item.messageId && message.messageId && item.messageId === message.messageId) {
      console.log('消息重复(messageId):', message.messageId);
      return true;
    }
    if (item.tempId && message.tempId && item.tempId === message.tempId) {
      console.log('消息重复(tempId):', message.tempId);
      return true;
    }
    return false;
  });
  
  if (existsByIds) {
    return;
  }
  
  // 生成消息哈希并保存
  const hash = generateMessageHash(message);
  const hashKey = `${conversationId}_${hash}`;
  
  try {
    // 保存哈希到本地存储
    uni.setStorageSync(MESSAGE_HASH_PREFIX + hashKey, Date.now());
    // 添加到内存缓存
    hashPool.add(hashKey);
  } catch (e) {
    console.error('保存消息哈希失败:', e);
  }
  
  // 添加新消息
  const enhancedMessage = {
    ...message,
    localId: Date.now() + '_' + Math.random().toString(36).substr(2, 9),
    saveTime: Date.now()
  };
  
  messages.push(enhancedMessage);
  
  // 按时间排序
  messages.sort((a, b) => {
    const timeA = new Date(a.createTime).getTime();
    const timeB = new Date(b.createTime).getTime();
    return timeB - timeA; // 降序，最新消息在前
  });
  
  // 只保留最近100条消息
  const MAX_MESSAGE_COUNT = 100;
  if (messages.length > MAX_MESSAGE_COUNT) {
    const removedMessages = messages.splice(MAX_MESSAGE_COUNT);
    // 清理被移除消息的哈希
    removedMessages.forEach(msg => {
      const oldHash = generateMessageHash(msg);
      const oldHashKey = `${conversationId}_${oldHash}`;
      try {
        uni.removeStorageSync(MESSAGE_HASH_PREFIX + oldHashKey);
        hashPool.delete(oldHashKey);
      } catch (e) {
        console.error('清理旧消息哈希失败:', e);
      }
    });
  }
  
  // 保存到本地存储
  try {
    const storageKey = CHAT_MESSAGE_PREFIX + conversationId;
    uni.setStorageSync(storageKey, JSON.stringify(messages));
    
    // 更新内存缓存
    messagePool.set(conversationId, messages);
    
    console.log('消息保存成功:', message.content, '总数:', messages.length);
  } catch (e) {
    console.error('保存消息到本地存储失败:', e);
  }
  
  // 更新会话列表
  updateConversation(conversationId, enhancedMessage);
}

/**
 * 获取指定会话的聊天消息 - 增强版
 * @param {String} conversationId 会话ID
 * @returns {Array} 消息列表
 */
export function getMessages(conversationId) {
  if (!conversationId) return [];
  
  // 优先从内存缓存获取
  if (messagePool.has(conversationId)) {
    const cachedMessages = messagePool.get(conversationId);
    console.log('从内存缓存获取消息:', conversationId, '数量:', cachedMessages.length);
    return [...cachedMessages]; // 返回副本避免外部修改
  }
  
  try {
    const storageKey = CHAT_MESSAGE_PREFIX + conversationId;
    const messagesStr = uni.getStorageSync(storageKey);
    const messages = messagesStr ? JSON.parse(messagesStr) : [];
    
    // 加载到内存缓存
    if (messages.length > 0) {
      messagePool.set(conversationId, messages);
      
      // 同时加载消息哈希到内存缓存
      messages.forEach(msg => {
        const hash = generateMessageHash(msg);
        const hashKey = `${conversationId}_${hash}`;
        hashPool.add(hashKey);
      });
    }
    
    console.log('从本地存储获取消息:', conversationId, '数量:', messages.length);
    return messages;
  } catch (e) {
    console.error('获取聊天记录失败', e);
    return [];
  }
}

/**
 * 更新本地消息（用于将临时消息更新为服务器返回的正式消息）
 * @param {String} conversationId 会话ID
 * @param {Object} serverMessage 服务器返回的消息对象
 */
export function updateLocalMessage(conversationId, serverMessage) {
  if (!conversationId || !serverMessage) return;
  
  const messages = getMessages(conversationId);
  let updated = false;
  
  // 查找对应的本地临时消息并更新
  for (let i = 0; i < messages.length; i++) {
    const localMsg = messages[i];
    
    // 通过内容、发送者、接收者、时间来匹配临时消息
    if (localMsg.tempId && !localMsg.messageId) {
      const timeDiff = Math.abs(new Date(localMsg.createTime).getTime() - new Date(serverMessage.createTime).getTime());
      if (localMsg.senderId === serverMessage.senderId && 
          localMsg.receiverId === serverMessage.receiverId && 
          localMsg.content === serverMessage.content && 
          localMsg.type === serverMessage.type &&
          timeDiff < 5000) { // 5秒内的相同内容消息认为是同一条
        
        // 更新消息，保留服务器的messageId，移除tempId
        messages[i] = {
          ...serverMessage,
          tempId: undefined // 移除临时ID
        };
        updated = true;
        console.log('更新本地临时消息为服务器消息:', serverMessage);
        break;
      }
    }
  }
  
  // 如果更新了消息，重新保存
  if (updated) {
    uni.setStorageSync(CHAT_MESSAGE_PREFIX + conversationId, JSON.stringify(messages));
  }
}

/**
 * 更新会话信息 - 增强版
 * @param {String} conversationId 会话ID
 * @param {Object} lastMessage 最后一条消息
 */
function updateConversation(conversationId, lastMessage) {
  if (!conversationId || !lastMessage) return;
  
  try {
    // 获取会话列表
    const conversationList = getConversationList();
    
    // 查找会话是否存在
    const index = conversationList.findIndex(item => item.conversationId === conversationId);
    
    if (index > -1) {
      // 已存在，更新最后一条消息
      conversationList[index].lastMessage = lastMessage;
      conversationList[index].updateTime = new Date();
    } else {
      // 不存在，创建新会话
      conversationList.push({
        conversationId,
        lastMessage,
        updateTime: new Date()
      });
    }
    
    // 按最后更新时间排序
    conversationList.sort((a, b) => {
      const timeA = new Date(a.updateTime).getTime();
      const timeB = new Date(b.updateTime).getTime();
      return timeB - timeA; // 降序，最新会话在前
    });
    
    // 保存到本地
    uni.setStorageSync(CHAT_CONVERSATION_PREFIX + 'list', JSON.stringify(conversationList));
  } catch (e) {
    console.error('更新会话信息失败:', e);
  }
}

/**
 * 获取会话列表 - 增强版
 * @returns {Array} 会话列表
 */
export function getConversationList() {
  try {
    const listStr = uni.getStorageSync(CHAT_CONVERSATION_PREFIX + 'list');
    const conversations = listStr ? JSON.parse(listStr) : [];
    console.log('获取会话列表:', conversations.length, '个会话');
    return conversations;
  } catch (e) {
    console.error('获取会话列表失败', e);
    return [];
  }
}

/**
 * 标记消息为已读 - 增强版
 * @param {String} conversationId 会话ID
 * @param {Number} senderId 发送者ID
 */
export function markMessagesAsRead(conversationId, senderId) {
  if (!conversationId) return;
  
  try {
    // 获取消息列表
    const messages = getMessages(conversationId);
    
    // 更新状态
    let updated = false;
    messages.forEach(msg => {
      if (msg.senderId === senderId && msg.status === 0) {
        msg.status = 1; // 标记为已读
        updated = true;
      }
    });
    
    // 保存更新后的消息
    if (updated) {
      const storageKey = CHAT_MESSAGE_PREFIX + conversationId;
      uni.setStorageSync(storageKey, JSON.stringify(messages));
      
      // 更新内存缓存
      messagePool.set(conversationId, messages);
      
      console.log('标记消息为已读:', conversationId, '发送者:', senderId);
    }
  } catch (e) {
    console.error('标记消息为已读失败:', e);
  }
}

/**
 * 保存好友列表
 * @param {Array} friendList 好友列表
 */
export function saveFriendList(friendList) {
  if (!friendList) return;
  
  // 确保每个好友对象中包含avatar字段
  const enhancedFriendList = friendList.map(friend => {
    // 如果已有avatar字段但为空，则保持原样
    if (!friend.avatar && friend.avatar !== '') {
      friend.avatar = ''; // 默认为空字符串，避免undefined
    }
    return friend;
  });
  
  uni.setStorageSync(CHAT_FRIEND_LIST_KEY, JSON.stringify(enhancedFriendList));
}

/**
 * 获取好友列表
 * @returns {Array} 好友列表
 */
export function getFriendList() {
  try {
    const listStr = uni.getStorageSync(CHAT_FRIEND_LIST_KEY);
    return listStr ? JSON.parse(listStr) : [];
  } catch (e) {
    console.error('获取好友列表失败', e);
    return [];
  }
}

/**
 * 清空指定会话的消息（增强版）
 * @param {String} conversationId 会话ID
 * @param {Date} clearTime 清空时间（可选）
 */
export function clearConversationMessages(conversationId, clearTime = null) {
  try {
    // 获取现有消息以清理哈希
    const existingMessages = getMessages(conversationId);
    
    // 清理消息哈希
    existingMessages.forEach(msg => {
      const hash = generateMessageHash(msg);
      const hashKey = `${conversationId}_${hash}`;
      try {
        uni.removeStorageSync(MESSAGE_HASH_PREFIX + hashKey);
        hashPool.delete(hashKey);
      } catch (e) {
        console.error('清理消息哈希失败:', e);
      }
    });
    
    // 清空消息存储
    const storageKey = CHAT_MESSAGE_PREFIX + conversationId;
    uni.removeStorageSync(storageKey);
    
    // 清空内存缓存
    messagePool.delete(conversationId);
    
    // 如果提供了清空时间，记录到本地
    if (clearTime) {
      uni.setStorageSync(`clear_time_${conversationId}`, clearTime);
    }
    
    // 更新会话列表
    const conversations = getConversationList();
    const updatedConversations = conversations.map(conv => {
      if (conv.conversationId === conversationId) {
        return {
          ...conv,
          lastMessage: '[聊天记录已清空]',
          lastMessageTime: clearTime || new Date(),
          unreadCount: 0
        };
      }
      return conv;
    });
    
    // 保存更新后的会话列表
    try {
      uni.setStorageSync(CHAT_CONVERSATION_PREFIX + 'list', JSON.stringify(updatedConversations));
    } catch (e) {
      console.error('保存会话列表失败:', e);
    }
    
    console.log('清空会话消息成功:', conversationId, '清理消息数量:', existingMessages.length);
  } catch (error) {
    console.error('清空会话消息失败:', error);
  }
}

/**
 * 获取会话的清空时间
 * @param {String} conversationId 会话ID
 * @returns {Date|null} 清空时间
 */
export function getClearTime(conversationId) {
  try {
    const clearTime = uni.getStorageSync(`clear_time_${conversationId}`);
    return clearTime ? new Date(clearTime) : null;
  } catch (error) {
    console.error('获取清空时间失败:', error);
    return null;
  }
}

/**
 * 清除聊天数据 - 增强版
 */
export function clearChatData() {
  try {
    // 清空内存缓存
    messagePool.clear();
    hashPool.clear();
    
    // 获取所有storage keys
    const keys = uni.getStorageInfoSync().keys;
    
    // 删除聊天相关数据
    keys.forEach(key => {
      if (key.startsWith(CHAT_MESSAGE_PREFIX) || 
          key.startsWith(CHAT_CONVERSATION_PREFIX) ||
          key.startsWith(MESSAGE_HASH_PREFIX) ||
          key === CHAT_FRIEND_LIST_KEY ||
          key.startsWith('clear_time_')) {
        try {
          uni.removeStorageSync(key);
        } catch (e) {
          console.error('删除存储键失败:', key, e);
        }
      }
    });
    
    console.log('清除聊天数据成功，清理键数量:', keys.filter(key => 
      key.startsWith(CHAT_MESSAGE_PREFIX) || 
      key.startsWith(CHAT_CONVERSATION_PREFIX) ||
      key.startsWith(MESSAGE_HASH_PREFIX) ||
      key === CHAT_FRIEND_LIST_KEY ||
      key.startsWith('clear_time_')
    ).length);
  } catch (e) {
    console.error('清除聊天数据失败', e);
  }
}

/**
 * 获取存储统计信息
 * @returns {Object} 存储统计
 */
export function getStorageStats() {
  try {
    const info = uni.getStorageInfoSync();
    const chatKeys = info.keys.filter(key => 
      key.startsWith(CHAT_MESSAGE_PREFIX) || 
      key.startsWith(CHAT_CONVERSATION_PREFIX) ||
      key.startsWith(MESSAGE_HASH_PREFIX) ||
      key === CHAT_FRIEND_LIST_KEY
    );
    
    return {
      totalKeys: info.keys.length,
      chatKeys: chatKeys.length,
      currentSize: info.currentSize,
      limitSize: info.limitSize,
      memoryCache: {
        conversations: messagePool.size,
        hashes: hashPool.size
      }
    };
  } catch (e) {
    console.error('获取存储统计失败:', e);
    return null;
  }
}