/**
 * WebSocket聊天工具
 */
import { saveMessage, markMessagesAsRead } from './chat-storage.js';
import { getToken } from './auth.js';
import { getUserStatusV2 as getUserStatus, getAllUnreadCount, updateUserStatus } from '@/api/chat/index';

// WebSocket连接状态
const SOCKET_STATE = {
  CONNECTING: 0, // 连接中
  OPEN: 1,       // 已连接
  CLOSING: 2,    // 关闭中
  CLOSED: 3      // 已关闭
};

// 消息类型
const MESSAGE_TYPE = {
  CONNECT: 1,    // 建立连接
  HEARTBEAT: 2,  // 心跳消息
  CHAT: 3,       // 聊天消息
  READ_RECEIPT: 4, // 已读回执
  FRIEND_REQUEST: 5, // 好友请求
  FRIEND_RESPONSE: 6, // 好友请求响应
  OFFLINE_MESSAGE: 7,  // 离线消息
  STATUS_CHANGE: 8   // 好友状态变化
};

class WebSocketChat {
  constructor() {
    this.socket = null;
    this.url = '';
    this.isConnected = false;
    this.reconnectCount = 0;
    this.maxReconnectCount = 5;
    this.reconnectTimer = null;
    this.heartbeatTimer = null;
    this.heartbeatTimeout = null;  // 心跳超时检测定时器
    this.heartbeatInterval = 15000; // 心跳间隔，优化为15秒
    this.heartbeatTimeoutInterval = 5000; // 心跳超时时间，默认5秒
    this.messageCallbacks = {};
    this.userId = null;
    this.friendStatusMap = {}; // 好友在线状态缓存
    this.lastCheckedTime = 0; // 上次检查未读消息的时间
    this.checkUnreadInterval = 60000; // 检查未读消息间隔，默认1分钟
    this.checkUnreadTimer = null;
    this.lastHeartbeatTime = 0; // 上次心跳时间
    this.missedHeartbeats = 0; // 连续丢失心跳次数
    this.maxMissedHeartbeats = 2; // 最大允许丢失心跳次数
  }
  
  /**
   * 初始化WebSocket连接
   * @param {Object} config 配置
   */
  init(config = {}) {
    // 读取配置
    this.url = config.url || 'ws://localhost:6680/ws/chat/';
    this.heartbeatInterval = config.heartbeatInterval || 15000;
    this.userId = config.userId || uni.getStorageSync('userId');
    this.checkUnreadInterval = config.checkUnreadInterval || 60000; // 检查未读消息间隔，默认1分钟
    
    if (!this.userId) {
      console.error('未找到用户ID，WebSocket无法初始化');
      return false;
    }
    
    // 确保没有正在进行的连接
    this.close();
    
    // 建立新连接
    this.connect();
    
    // 启动未读消息检查定时器
    this.startCheckUnreadTimer();
    
    return true;
  }
  
  /**
   * 建立WebSocket连接
   */
  connect() {
    try {
      // 获取token
      const token = getToken();
      if (!token) {
        console.error('未找到认证令牌，WebSocket无法连接');
        return;
      }
      
      // 构建WebSocket URL
      const wsUrl = `${this.url}${this.userId}?token=${token}`;
      
      // 创建WebSocket对象
      this.socket = uni.connectSocket({
        url: wsUrl,
        complete: () => {}
      });
      
      // 监听事件
      this.addSocketListeners();
      
    } catch (error) {
      console.error('WebSocket连接失败', error);
      this.reconnect();
    }
  }
  
  /**
   * 添加WebSocket事件监听
   */
  addSocketListeners() {
    // 连接成功
    uni.onSocketOpen(() => {
      console.log('WebSocket连接成功');
      this.isConnected = true;
      this.reconnectCount = 0;
      this.missedHeartbeats = 0;
      
      // 发送连接消息
      this.sendConnectMessage();
      
      // 启动心跳
      this.startHeartbeat();
      
      // 立即发送一次心跳
      this.sendHeartbeat();
      
      // 触发连接成功事件
      uni.$emit('websocket-connected', { userId: this.userId });
    });
    
    // 连接关闭
    uni.onSocketClose((res) => {
      console.log('WebSocket连接关闭', res);
      this.isConnected = false;
      
      // 清除心跳
      this.stopHeartbeat();
      
      // 触发连接关闭事件
      uni.$emit('websocket-closed', { 
        userId: this.userId, 
        code: res.code, 
        reason: res.reason || '连接关闭' 
      });
      
      // 尝试重连
      this.reconnect();
    });
    
    // 连接错误
    uni.onSocketError((error) => {
      console.error('WebSocket连接错误', error);
      this.isConnected = false;
      
      // 清除心跳
      this.stopHeartbeat();
      
      // 触发连接错误事件
      uni.$emit('websocket-error', { 
        userId: this.userId, 
        error: error
      });
      
      // 尝试重连
      this.reconnect();
    });
    
    // 收到消息
    uni.onSocketMessage((res) => {
      try {
        const message = JSON.parse(res.data);
        this.handleMessage(message);
      } catch (error) {
        console.error('WebSocket消息解析失败', error);
      }
    });
  }
  
  /**
   * 发送连接消息
   */
  sendConnectMessage() {
    this.send({
      type: MESSAGE_TYPE.CONNECT,
      data: JSON.stringify({ userId: this.userId })
    });
  }
  
  /**
   * 启动心跳检测
   */
  startHeartbeat() {
    // 清除可能存在的心跳计时器
    this.stopHeartbeat();
    
    // 记录心跳开始时间
    this.lastHeartbeatTime = Date.now();
    this.missedHeartbeats = 0;
    
    // 启动新的心跳计时器
    this.heartbeatTimer = setInterval(() => {
      // 发送心跳
      this.sendHeartbeat();
      
      // 设置心跳响应超时检测
      this.heartbeatTimeout = setTimeout(() => {
        this.missedHeartbeats++;
        console.warn(`心跳响应超时，已丢失 ${this.missedHeartbeats} 次心跳`);
        
        // 超过最大丢失次数，认为连接已断开
        if (this.missedHeartbeats >= this.maxMissedHeartbeats) {
          console.error('心跳检测失败，连接可能已断开，尝试重新连接');
          this.close(); // 关闭连接，触发重连
        }
      }, this.heartbeatTimeoutInterval);
    }, this.heartbeatInterval);
  }
  
  /**
   * 发送心跳消息
   */
  sendHeartbeat() {
    console.log('发送心跳...');
    
    // 更新心跳时间
    this.lastHeartbeatTime = Date.now();
    
    // 通过WebSocket发送心跳
    const result = this.send({
      type: MESSAGE_TYPE.HEARTBEAT,
      data: JSON.stringify({
        userId: this.userId,
        timestamp: this.lastHeartbeatTime
      })
    });
    
    return result;
  }
  
  /**
   * 停止心跳检测
   */
  stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
    
    if (this.heartbeatTimeout) {
      clearTimeout(this.heartbeatTimeout);
      this.heartbeatTimeout = null;
    }
  }
  
  /**
   * 断线重连
   */
  reconnect() {
    // 清除可能存在的重连计时器
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    
    // 超过最大重连次数，不再重连
    if (this.reconnectCount >= this.maxReconnectCount) {
      console.error('已达到最大重连次数，不再重连');
      return;
    }
    
    // 递增重连次数
    this.reconnectCount++;
    
    // 计算重连等待时间（指数退避）
    const waitTime = Math.min(1000 * Math.pow(2, this.reconnectCount), 30000);
    
    console.log(`将在 ${waitTime}ms 后第 ${this.reconnectCount} 次重连`);
    
    // 设置重连定时器
    this.reconnectTimer = setTimeout(() => {
      console.log(`正在进行第 ${this.reconnectCount} 次重连...`);
      this.connect();
    }, waitTime);
  }
  
  /**
   * 关闭连接
   */
  close() {
    // 停止心跳
    this.stopHeartbeat();
    
    // 清除重连计时器
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    
    // 关闭连接
    if (this.isConnected) {
      try {
        uni.closeSocket({
          success: () => {
            console.log('WebSocket连接已主动关闭');
            this.isConnected = false;
          }
        });
      } catch (error) {
        console.error('关闭WebSocket连接失败', error);
      }
    }
  }
  
  /**
   * 检查连接状态
   */
  checkConnection() {
    const isConnected = this.socket && this.socket.readyState === WebSocket.OPEN;
    console.log('WebSocket连接状态检查:', isConnected ? '已连接' : '未连接');
    
    if (!isConnected && this.autoReconnect) {
      console.log('检测到连接断开，尝试重连');
      this.reconnect();
    }
    
    return isConnected;
  }
  
  /**
   * 发送消息
   * @param {Object} message 消息对象
   * @returns {Promise} Promise对象
   */
  send(message) {
    return new Promise((resolve, reject) => {
      // 检查连接状态
      if (!this.isConnected) {
        console.error('WebSocket未连接，无法发送消息');
        reject(new Error('WebSocket未连接'));
        return;
      }
      
      // 转换为JSON字符串
      const messageStr = typeof message === 'string' ? message : JSON.stringify(message);
      
      // 发送消息
      uni.sendSocketMessage({
        data: messageStr,
        success: () => {
          resolve(true);
        },
        fail: (err) => {
          console.error('发送消息失败', err);
          reject(err);
        }
      });
    });
  }
  
  /**
   * 发送聊天消息
   * @param {Object} chatMessage 聊天消息
   * @returns {Boolean} 是否发送成功
   */
  sendChatMessage(chatMessage) {
    // 确保消息包含所有必要字段
    if (!chatMessage.conversationId) {
      const senderId = chatMessage.senderId;
      const receiverId = chatMessage.receiverId;
      
      if (senderId < receiverId) {
        chatMessage.conversationId = `${senderId}_${receiverId}`;
      } else {
        chatMessage.conversationId = `${receiverId}_${senderId}`;
      }
    }
    
    return this.send({
      type: MESSAGE_TYPE.CHAT,
      data: JSON.stringify(chatMessage)
    });
  }
  
  /**
   * 发送已读回执
   * @param {Number} senderId 发送者ID
   * @param {Number} receiverId 接收者ID
   * @returns {Boolean} 是否发送成功
   */
  sendReadReceipt(senderId, receiverId) {
    return this.send({
      type: MESSAGE_TYPE.READ_RECEIPT,
      data: JSON.stringify({ senderId, receiverId })
    });
  }
  
  /**
   * 处理收到的消息
   * @param {Object} message 消息对象
   */
  handleMessage(message) {
    console.log('WebSocket收到原始消息:', message);
    
    const { type, data } = message;
    console.log('消息类型:', type, '消息数据:', data);
    
    switch (type) {
      case MESSAGE_TYPE.CONNECT:
        // 连接消息处理
        console.log('收到连接确认消息');
        // 连接成功后，获取好友状态
        this.refreshFriendsStatus();
        break;
        
      case MESSAGE_TYPE.HEARTBEAT:
        // 心跳消息处理
        console.log('收到心跳响应');
        // 重置心跳计数
        this.missedHeartbeats = 0;
        // 更新最后心跳时间
        this.lastHeartbeatTime = Date.now();
        // 清除心跳响应超时检测
        if (this.heartbeatTimeout) {
          clearTimeout(this.heartbeatTimeout);
          this.heartbeatTimeout = null;
        }
        break;
        
      case MESSAGE_TYPE.CHAT:
        // 聊天消息处理
        console.log('处理聊天消息');
        this.handleChatMessage(data);
        break;
        
      case MESSAGE_TYPE.READ_RECEIPT:
        // 已读回执处理
        this.handleReadReceipt(data);
        break;
        
      case MESSAGE_TYPE.FRIEND_REQUEST:
        // 好友请求处理
        this.handleFriendRequest(data);
        break;
        
      case MESSAGE_TYPE.FRIEND_RESPONSE:
        // 好友请求响应处理
        this.handleFriendResponse(data);
        break;
        
      case MESSAGE_TYPE.OFFLINE_MESSAGE:
        // 离线消息处理
        this.handleOfflineMessage(data);
        break;
        
      case MESSAGE_TYPE.STATUS_CHANGE:
        // 好友状态变化处理
        this.handleStatusChange(data);
        break;
        
      default:
        console.warn('收到未知类型的消息', type, data);
    }
    
    // 触发回调
    if (this.messageCallbacks[type]) {
      this.messageCallbacks[type].forEach(callback => {
        try {
          callback(JSON.parse(data));
        } catch (error) {
          console.error('消息回调执行失败', error);
        }
      });
    }
  }
  
  /**
   * 刷新好友状态
   */
  async refreshFriendsStatus() {
    // 可以实现获取所有好友的状态
    // 这里简单实现，仅作为示例
    try {
      // 从本地缓存获取好友列表
      const friendListStr = uni.getStorageSync('friendList');
      if (friendListStr) {
        const friendList = JSON.parse(friendListStr);
        for (const friend of friendList) {
          await this.getFriendStatus(friend.userId);
        }
      }
    } catch (error) {
      console.error('刷新好友状态失败', error);
    }
  }
  
  /**
   * 获取好友在线状态
   * @param {String|Number} userId 用户ID
   * @returns {Promise<String>} 状态：0离线 1在线
   */
  async getFriendStatus(userId) {
    try {
      // 检查缓存，避免频繁请求
      const now = Date.now();
      const cachedStatus = this.friendStatusMap[userId];
      if (cachedStatus && now - cachedStatus.timestamp < 10000) { // 优化为10秒内的缓存有效
        return cachedStatus.status;
      }
      
      // 请求服务器获取最新状态
      const response = await getUserStatus(userId);
      if (response && response.code === 200) {
        const status = response.data || '0';
        
        // 更新缓存
        this.friendStatusMap[userId] = {
          status,
          timestamp: now
        };
        
        // 通知状态更新
        uni.$emit('user-status-change', {
          userId: userId,
          status: status
        });
        
        return status;
      }
      return '0'; // 默认离线
    } catch (error) {
      console.error('获取用户状态失败', error);
      return '0'; // 出错时返回离线
    }
  }
  
  /**
   * 启动未读消息检查定时器
   */
  startCheckUnreadTimer() {
    // 清除可能存在的定时器
    this.stopCheckUnreadTimer();
    
    // 先立即检查一次
    this.checkUnreadMessages();
    
    // 设置定时检查
    this.checkUnreadTimer = setInterval(() => {
      this.checkUnreadMessages();
    }, this.checkUnreadInterval);
  }
  
  /**
   * 停止未读消息检查定时器
   */
  stopCheckUnreadTimer() {
    if (this.checkUnreadTimer) {
      clearInterval(this.checkUnreadTimer);
      this.checkUnreadTimer = null;
    }
  }
  
  /**
   * 检查未读消息
   */
  async checkUnreadMessages() {
    try {
      if (!this.isConnected) {
        // 未连接状态，获取未读消息数
        const response = await getAllUnreadCount(this.userId);
        if (response && response.code === 200) {
          const unreadCount = response.data || 0;
          if (unreadCount > 0) {
            // 设置角标
            this.setUnreadBadge(unreadCount);
          } else {
            // 清除角标
            this.clearUnreadBadge();
          }
        } else if (response && response.code === 500) {
          // API错误，尝试备用方案
          console.error('获取未读消息数API错误，使用本地缓存');
          this.checkLocalUnreadMessages();
        }
      }
      
      this.lastCheckedTime = Date.now();
    } catch (error) {
      console.error('检查未读消息失败', error);
      // 出错时，尝试使用本地缓存计算未读消息
      this.checkLocalUnreadMessages();
    }
  }
  
  /**
   * 从本地缓存检查未读消息
   */
  checkLocalUnreadMessages() {
    try {
      // 尝试从本地存储获取所有会话
      const conversations = uni.getStorageSync('conversations') || '[]';
      const conversationList = JSON.parse(conversations);
      
      // 计算总未读消息数
      let totalUnread = 0;
      conversationList.forEach(conv => {
        if (conv.unreadCount) {
          totalUnread += conv.unreadCount;
        }
      });
      
      // 显示未读消息数
      if (totalUnread > 0) {
        this.setUnreadBadge(totalUnread);
      } else {
        this.clearUnreadBadge();
      }
    } catch (error) {
      console.error('从本地缓存检查未读消息失败', error);
    }
  }
  
  /**
   * 设置未读消息角标
   * @param {Number} count 未读消息数
   */
  setUnreadBadge(count) {
    if (typeof count !== 'number' || count <= 0) {
      this.clearUnreadBadge();
      return;
    }
    
    // 设置应用角标
    if (uni.setTabBarBadge) {
      uni.setTabBarBadge({
        index: 2, // 消息图标的TabBar索引，根据pages.json配置
        text: count.toString()
      });
    }
    
    // 设置应用图标角标（仅支持iOS）
    if (uni.setAppBadge) {
      uni.setAppBadge({
        badge: count
      });
    }
  }
  
  /**
   * 清除未读消息角标
   */
  clearUnreadBadge() {
    // 移除TabBar角标
    if (uni.removeTabBarBadge) {
      uni.removeTabBarBadge({
        index: 2 // 消息图标的TabBar索引
      });
    }
    
    // 清除应用图标角标
    if (uni.setAppBadge) {
      uni.setAppBadge({
        badge: 0
      });
    }
  }
  
  /**
   * 处理聊天消息
   * @param {String} data 消息数据（JSON字符串）
   */
  handleChatMessage(data) {
    try {
      const message = JSON.parse(data);
      
      console.log('WebSocket收到聊天消息:', message);
      
      // 构建会话ID
      let conversationId = '';
      const senderId = message.senderId;
      const receiverId = message.receiverId;
      
      if (senderId < receiverId) {
        conversationId = `${senderId}_${receiverId}`;
      } else {
        conversationId = `${receiverId}_${senderId}`;
      }
      
      // 保存消息到本地
      saveMessage(conversationId, message);
      console.log('消息已保存到本地存储，会话ID:', conversationId);
      
      // 立即触发消息回调，确保UI更新
      console.log('开始触发消息回调，回调数量:', this.messageCallbacks[MESSAGE_TYPE.CHAT] ? this.messageCallbacks[MESSAGE_TYPE.CHAT].length : 0);
      
      if (this.messageCallbacks[MESSAGE_TYPE.CHAT]) {
        this.messageCallbacks[MESSAGE_TYPE.CHAT].forEach((callback, index) => {
          try {
            console.log(`执行第${index + 1}个消息回调`);
            callback(message);
            console.log(`第${index + 1}个消息回调执行成功`);
          } catch (error) {
            console.error(`第${index + 1}个消息回调执行失败:`, error);
          }
        });
      } else {
        console.warn('没有注册聊天消息回调函数');
      }
      
      // 发送来自其他人的消息通知
      if (message.senderId != this.userId) {
        console.log('收到来自其他用户的消息，检查是否需要通知');
        
        // 使用uniapp API获取当前页面路径
        const pages = getCurrentPages();
        const currentPage = pages[pages.length - 1];
        const currentPath = currentPage ? currentPage.route : '';
        
        console.log('当前页面路径:', currentPath);
        
        // 判断是否在聊天页面且是当前聊天的好友发来的消息
        const isChatPage = currentPath.includes('/pages/chat/chat');
        const isChatWithSender = isChatPage && currentPage.options && 
                                 currentPage.options.friendId == message.senderId;
        
        console.log('是否在聊天页面:', isChatPage, '是否当前聊天对象:', isChatWithSender);
        
        // 如果不是当前聊天的好友发来的消息，则显示通知
        if (!isChatWithSender) {
          console.log('显示新消息通知');
          uni.showToast({
            title: '收到新消息',
            icon: 'none'
          });
          
          // 播放提示音
          const innerAudioContext = uni.createInnerAudioContext();
          innerAudioContext.autoplay = true;
          innerAudioContext.src = '/static/audio/message.mp3';
          
          // 检查未读消息数
          this.checkUnreadMessages();
        }
      }
    } catch (error) {
      console.error('处理聊天消息失败', error);
    }
  }
  
  /**
   * 处理已读回执
   * @param {String} data 消息数据（JSON字符串）
   */
  handleReadReceipt(data) {
    try {
      const receipt = JSON.parse(data);
      console.log('收到已读回执', receipt);
      
      // 获取参数
      const senderId = receipt.senderId;
      const receiverId = receipt.receiverId;
      
      if (!senderId || !receiverId) {
        console.warn('已读回执参数不完整', receipt);
        return;
      }
      
      // 检查当前用户是否是发送者
      const userId = uni.getStorageSync('userId');
      if (senderId != userId) {
        return; // 只处理自己发送的消息的已读回执
      }
      
      // 构建会话ID
      let conversationId;
      if (parseInt(senderId) < parseInt(receiverId)) {
        conversationId = `${senderId}_${receiverId}`;
      } else {
        conversationId = `${receiverId}_${senderId}`;
      }
      
      // 更新本地消息状态
      markMessagesAsRead(conversationId, senderId);
      
      // 可以更新UI或发送事件通知UI组件更新
      uni.$emit('message-read-receipt', {
        senderId,
        receiverId,
        conversationId
      });
    } catch (error) {
      console.error('处理已读回执失败', error);
    }
  }
  
  /**
   * 处理好友请求
   * @param {String} data 消息数据（JSON字符串）
   */
  handleFriendRequest(data) {
    try {
      const request = JSON.parse(data);
      console.log('收到好友请求', request);
      
      // 显示通知
      uni.showToast({
        title: '收到新的好友请求',
        icon: 'none'
      });
      
      // TODO: 显示好友请求通知或更新好友请求列表
    } catch (error) {
      console.error('处理好友请求失败', error);
    }
  }
  
  /**
   * 处理好友请求响应
   * @param {String} data 消息数据（JSON字符串）
   */
  handleFriendResponse(data) {
    try {
      const response = JSON.parse(data);
      console.log('收到好友请求响应', response);
      
      // 显示通知
      const status = response.status;
      if (status === 1) {
        uni.showToast({
          title: '好友请求已被接受',
          icon: 'success'
        });
      } else if (status === 2) {
        uni.showToast({
          title: '好友请求已被拒绝',
          icon: 'none'
        });
      }
      
      // TODO: 更新好友列表
    } catch (error) {
      console.error('处理好友请求响应失败', error);
    }
  }
  
  /**
   * 处理离线消息
   * @param {String} data 消息数据（JSON字符串）
   */
  handleOfflineMessage(data) {
    try {
      // 增加判断，确保data不为undefined或null
      if (!data) {
        console.log('离线消息数据为空');
        return;
      }
      
      const offlineMessages = JSON.parse(data);
      console.log('收到离线消息', offlineMessages);
      
      // 处理每一条离线消息
      if (Array.isArray(offlineMessages)) {
        offlineMessages.forEach(message => {
          // 构建会话ID
          let conversationId = '';
          const senderId = message.senderId;
          const receiverId = message.receiverId;
          
          if (senderId < receiverId) {
            conversationId = `${senderId}_${receiverId}`;
          } else {
            conversationId = `${receiverId}_${senderId}`;
          }
          
          // 保存消息到本地
          saveMessage(conversationId, message);
          
          // 为每条消息触发回调
          if (this.messageCallbacks[MESSAGE_TYPE.CHAT]) {
            this.messageCallbacks[MESSAGE_TYPE.CHAT].forEach(callback => {
              try {
                callback(message);
              } catch (error) {
                console.error('离线消息回调执行失败', error);
              }
            });
          }
        });
        
        if (offlineMessages.length > 0) {
          uni.showToast({
            title: `收到${offlineMessages.length}条离线消息`,
            icon: 'none'
          });
          
          // TODO: 处理通知和声音提示
        }
      }
    } catch (error) {
      console.error('处理离线消息失败', error);
    }
  }
  
  /**
   * 处理好友状态变化
   * @param {String} data 状态变化数据（JSON字符串）
   */
  handleStatusChange(data) {
    try {
      if (!data) {
        console.log('状态变化数据为空');
        return;
      }
      
      const statusData = JSON.parse(data);
      console.log('收到好友状态变化', statusData);
      
      const { userId, status, timestamp } = statusData;
      
      if (userId && status !== undefined) {
        // 更新本地缓存
        this.friendStatusMap[userId] = {
          status: status,
          timestamp: timestamp || Date.now()
        };
        
        console.log(`用户${userId}状态更新为${status === '1' ? '在线' : '离线'}`);
        
        // 触发状态变化事件，通知UI更新
        uni.$emit('user-status-change', {
          userId: userId,
          status: status,
          timestamp: timestamp
        });
        
        // 触发状态变化回调
        if (this.messageCallbacks[MESSAGE_TYPE.STATUS_CHANGE]) {
          this.messageCallbacks[MESSAGE_TYPE.STATUS_CHANGE].forEach(callback => {
            try {
              callback(statusData);
            } catch (error) {
              console.error('状态变化回调执行失败', error);
            }
          });
        }
      }
    } catch (error) {
      console.error('处理好友状态变化失败', error);
    }
  }
}

/**
 * 添加消息回调
 * @param {Number} type 消息类型
 * @param {Function} callback 回调函数
 */
WebSocketChat.prototype.addMessageListener = function(type, callback) {
  console.log('注册消息监听器，类型:', type, '回调函数:', typeof callback);
  
  if (!this.messageCallbacks[type]) {
    this.messageCallbacks[type] = [];
    console.log('创建新的消息回调数组，类型:', type);
  }
  
  this.messageCallbacks[type].push(callback);
  console.log('消息监听器注册成功，当前类型', type, '的回调数量:', this.messageCallbacks[type].length);
};

/**
 * 移除消息回调
 * @param {Number} type 消息类型
 * @param {Function} callback 回调函数，不传则移除该类型的所有回调
 */
WebSocketChat.prototype.removeMessageListener = function(type, callback) {
  console.log('移除消息监听器，类型:', type, '回调函数:', typeof callback);
  
  if (!this.messageCallbacks[type]) {
    console.warn('该消息类型没有注册的回调函数:', type);
    return;
  }
  
  if (!callback) {
    // 移除该类型的所有回调
    this.messageCallbacks[type] = [];
    console.log('已移除该类型的所有回调函数:', type);
  } else {
    // 移除指定回调
    const index = this.messageCallbacks[type].indexOf(callback);
    if (index > -1) {
      this.messageCallbacks[type].splice(index, 1);
      console.log('消息监听器移除成功，剩余回调数量:', this.messageCallbacks[type].length);
    } else {
      console.warn('未找到要移除的回调函数');
    }
  }
};

// 创建单例实例
const wsChat = new WebSocketChat();

// 导出实例
export default wsChat;