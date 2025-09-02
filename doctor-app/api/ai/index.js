import request from '@/utils/request'

// 取消令牌存储
const aiRequestCancelTokens = {};

// 获取AI聊天历史记录
export function getAIChatHistory(params) {
  return request({
    url: '/api/ai/history',
    method: 'get',
    params
  }).then(res => {
    // 规范化消息类型标识
    if (res.code === 200 && res.data) {
      // 确保聊天历史记录中的每个项目都有chatId字段
      if (Array.isArray(res.data)) {
        res.data.forEach(chat => {
          // 确保每个聊天记录都有chatId字段
          if (!chat.chatId && chat.id) {
            chat.chatId = chat.id;
          }
        });
      }
      
      // 处理消息列表
      if (res.data.messages && Array.isArray(res.data.messages)) {
        // 确保isUser字段值正确
        res.data.messages.forEach(msg => {
          // 后端使用字符串"1"表示用户消息，"0"表示AI消息
          // 前端使用布尔值true表示用户消息(isSelf=true)，false表示AI消息(isSelf=false)
          let isSelf = false; // 默认是AI消息
          if (msg.isUser === "1") {
            isSelf = true; // 用户消息
          } else if (msg.isUser === "0") {
            isSelf = false; // AI消息
          } else if (msg.isUser === true) {
            isSelf = true; // 已经是布尔类型
          } else if (msg.isUser === false) {
            isSelf = false; // 已经是布尔类型
          } else {
            // 未知类型或null/undefined，按AI消息处理
            console.warn('未知的消息来源标识，默认为AI消息:', msg);
            isSelf = false;
          }
          msg.isSelf = isSelf; // 统一使用 isSelf 属性给前端
          
          // 确保消息类型正确
          if (!msg.type) {
            // 如果没有类型，默认为text
            msg.type = 'text';
          } else if (msg.type === 'markdown' || 
                   (msg.content && 
                    (msg.content.includes('# ') || 
                     msg.content.includes('## ') || 
                     msg.content.includes('```') || 
                     msg.content.includes('- ') ||
                     msg.content.includes('1. ')))) {
            // 内容包含Markdown常见标记，将类型设为markdown
            msg.type = 'markdown';
          }
        });
      }
    }
    return res;
  });
}

// 发送消息给AI
export function sendMessageToAI(data) {
  // 取消之前未完成的请求（如果存在）
  if (aiRequestCancelTokens.sendMessage) {
    aiRequestCancelTokens.sendMessage('用户发起了新的请求');
    aiRequestCancelTokens.sendMessage = null;
  }
  
  // 处理data中可能存在的undefined值
  const cleanData = {};
  for (const key in data) {
    if (data[key] !== undefined && data[key] !== 'undefined') {
      cleanData[key] = data[key];
    } else if (key === 'chatId') {
      // 对于chatId，如果是undefined，设置为null
      cleanData[key] = null;
    }
  }
  
  // 复制用户消息，用于在请求失败时增强反馈
  const userQuery = cleanData.content;
  
  return request({
    url: '/api/ai/message/send',
    method: 'post',
    data: cleanData,
    timeout: 60000, // 延长超时时间到60秒
    cancelToken: function(cancel) {
      // 保存取消函数
      aiRequestCancelTokens.sendMessage = cancel;
    }
  }).then(res => {
    // 清除取消令牌
    aiRequestCancelTokens.sendMessage = null;
    
    // 确保AI回复的isUser字段正确
    if (res.code === 200 && res.data) {
      // 后端返回的AI消息应该有isUser字段，确保它是false (AI消息)
      if (res.data.isUser === "0") {
        res.data.isUser = false;
      } else if (res.data.isUser !== false) {
        // 如果不是false，则设置为false
        res.data.isUser = false;
      }
      
      // 检测消息类型，根据内容特征设置正确的类型
      if (!res.data.type) {
        // 如果没有类型，默认为text
        res.data.type = 'text';
      }
      
      // 检查内容是否包含Markdown特征
      if (res.data.content && (
          res.data.content.includes('# ') || 
          res.data.content.includes('## ') || 
          res.data.content.includes('```') || 
          res.data.content.includes('- ') ||
          res.data.content.includes('1. '))) {
        // 内容包含Markdown常见标记，将类型设为markdown
        res.data.type = 'markdown';
      }
    }
    
    // 成功返回结果
    return res;
  }).catch(error => {
    // 清除取消令牌
    aiRequestCancelTokens.sendMessage = null;
    
    // 检查是否是超时错误
    if (error && error.message && error.message.includes('timeout')) {
      // 构建友好的超时响应
      return {
        code: 200,
        data: {
          content: "请求处理时间较长，请稍后再试。以下是一些可能的原因：\n- 网络连接不稳定\n- 服务器负载过高\n- 您的问题较为复杂",
          type: "text",
          isUser: false, // 确保是AI消息
          chatId: data.chatId
        },
        msg: "请求超时"
      };
    }
    
    // 检查是否是主动取消
    if (error && error.__CANCEL__) {
      return Promise.reject(error);
    }
    
    // 其他错误，构建友好的错误响应
    return {
      code: 200,
      data: {
        content: "抱歉，处理您的请求时遇到了问题。请稍后再试。",
        type: "text",
        isUser: false, // 确保是AI消息
        chatId: data.chatId
      },
      msg: error.message || "请求失败"
    };
  });
}

// 获取AI助手列表
export function getAIAssistantList() {
  return request({
    url: '/api/ai/assistant/list',
    method: 'get'
  })
}

// 创建新的AI聊天
export function createAIChat(data) {
  return request({
    url: '/api/ai/create',
    method: 'post',
    data
  })
}

// 删除AI聊天
export function deleteAIChat(chatId) {
  // 确保chatId是有效值
  if (!chatId) {
    console.error('deleteAIChat: chatId不能为空');
    return Promise.reject('无效的chatId');
  }
  
  return request({
    url: '/api/ai/delete/' + chatId,
    method: 'delete'
  })
}

// 删除单个消息
export function deleteAIMessage(messageId) {
  // 确保messageId是有效值
  if (!messageId) {
    console.error('deleteAIMessage: messageId不能为空');
    return Promise.reject('无效的messageId');
  }
  
  return request({
    url: '/api/ai/message/' + messageId,
    method: 'delete'
  })
}

// 清空单个聊天历史
export function clearChatHistory(chatId) {
  // 确保chatId是有效值
  if (!chatId) {
    console.error('clearChatHistory: chatId不能为空');
    return Promise.reject('无效的chatId');
  }
  
  return request({
    url: '/api/ai/chat/' + chatId + '/clear',
    method: 'post'
  })
}

// 清空所有聊天历史
export function clearAllChatHistory() {
  return request({
    url: '/api/ai/chat/clear-all',
    method: 'post'
  })
}

// 更新AI聊天设置
export function updateAIChatSettings(data) {
  return request({
    url: '/api/ai/settings/update',
    method: 'put',
    data
  })
}

// 取消所有进行中的AI请求
export function cancelAllAIRequests(reason = '用户取消请求') {
  Object.values(aiRequestCancelTokens).forEach(cancelFn => {
    if (typeof cancelFn === 'function') {
      cancelFn(reason);
    }
  });
}