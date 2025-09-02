import request from '@/utils/request';

// 基础API路径
const baseUrl = '/chat';

/**
 * 获取聊天消息历史
 * @param {Number} userId1 用户ID1
 * @param {Number} userId2 用户ID2
 * @param {Number} limit 限制条数，默认50
 * @returns {Promise} Promise对象
 */
/*
export function getChatHistory(userId1, userId2, limit = 50) {
  return request({
    url: `${baseUrl}/message/history`,
    method: 'get',
    params: { userId1, userId2, limit }
  });
}
*/
export function getChatHistory(userId1, userId2, limit = 50) {
  return request({
    url: `${baseUrl}/message/history`,
    method: 'get',
    params: { userId1, userId2, requesterId: userId1, limit }
  });
}

/**
 * 根据会话ID获取聊天记录
 * @param {String} conversationId 会话ID
 * @param {Number} limit 限制条数，默认50
 * @returns {Promise} Promise对象
 */
export function getMessagesByConversationId(conversationId, limit = 50) {
  return request({
    url: `${baseUrl}/message/conversation/${conversationId}`,
    method: 'get',
    params: { limit }
  });
}

/**
 * 发送消息（HTTP方式，非WebSocket）
 * @param {Object} message 消息对象
 * @returns {Promise} Promise对象
 */
export function sendMessage(message) {
  return request({
    url: `${baseUrl}/message/send`,
    method: 'post',
    data: message
  });
}

/**
 * 标记消息为已读
 * @param {Number} senderId 发送者ID
 * @param {Number} receiverId 接收者ID
 * @returns {Promise} Promise对象
 */
export function markMessageRead(senderId, receiverId) {
  return request({
    url: `${baseUrl}/message/read`,
    method: 'post',
    data: { 
      senderId: Number(senderId), 
      receiverId: Number(receiverId) 
    },
    errorHandler: (error) => {
      console.error('标记消息已读失败', error);
      // 返回一个统一的错误对象，避免前端解析错误
      return {
        code: 500,
        message: error.message || '标记消息已读失败',
        success: false
      };
    }
  });
}

/**
 * 获取未读消息数量
 * @param {Number} receiverId 接收者ID
 * @returns {Promise} Promise对象
 */
export function getUnreadCount(receiverId) {
  return request({
    url: `${baseUrl}/message/unread`,
    method: 'get',
    params: { receiverId }
  });
}

/**
 * 获取好友列表
 * @param {Number} userId 用户ID
 * @returns {Promise} Promise对象
 */
export function getFriendList(userId) {
  return request({
    url: `${baseUrl}/friend/list`,
    method: 'get',
    params: { userId }
  });
}

/**
 * 添加好友关系（直接添加，不需要申请）
 * @param {Number} userId 用户ID
 * @param {Number} friendId 好友ID
 * @returns {Promise} Promise对象
 */
export function addFriend(userId, friendId) {
  return request({
    url: `${baseUrl}/friend/add`,
    method: 'post',
    params: { userId, friendId }
  });
}

/**
 * 删除好友关系
 * @param {Number} userId 用户ID
 * @param {Number} friendId 好友ID
 * @returns {Promise} Promise对象
 */
export function deleteFriend(userId, friendId) {
  return request({
    url: `${baseUrl}/friend/delete`,
    method: 'post',
    params: { userId, friendId }
  });
}

/**
 * 修改好友备注
 * @param {Number} userId 用户ID
 * @param {Number} friendId 好友ID
 * @param {String} remark 备注
 * @returns {Promise} Promise对象
 */
export function updateFriendRemark(userId, friendId, remark) {
  return request({
    url: `${baseUrl}/friend/remark`,
    method: 'put',
    data: { userId, friendId, remark }
  });
}

/**
 * 发送好友申请
 * @param {Number} fromUserId 申请者ID
 * @param {Number} toUserId 接收者ID
 * @param {String} message 申请消息
 * @param {String} remark 好友备注
 * @returns {Promise} Promise对象
 */
export function sendFriendRequest(fromUserId, toUserId, message, remark = '') {
  return request({
    url: `${baseUrl}/friend/request/send`,
    method: 'post',
    data: { fromUserId, toUserId, message, remark },
    errorHandler: (error) => {
      console.error('发送好友请求失败', error);
      let errorMsg = '发送失败，请稍后再试';
      if (error.response && error.response.data && error.response.data.msg) {
        errorMsg = error.response.data.msg;
      } else if (error.message) {
        errorMsg = error.message;
      }
      
      return {
        code: 500,
        message: errorMsg,
        success: false
      };
    }
  });
}

/**
 * 获取收到的好友申请列表
 * @param {Number} userId 用户ID
 * @returns {Promise} Promise对象
 */
export function getReceivedFriendRequests(userId) {
  return request({
    url: `${baseUrl}/friend/request/received`,
    method: 'get',
    params: { userId }
  });
}

/**
 * 获取发送的好友申请列表
 * @param {Number} userId 用户ID
 * @returns {Promise} Promise对象
 */
export function getSentFriendRequests(userId) {
  return request({
    url: `${baseUrl}/friend/request/sent`,
    method: 'get',
    params: { userId }
  });
}

/**
 * 接受好友申请
 * @param {Number} requestId 申请ID
 * @returns {Promise} Promise对象
 */
export function acceptFriendRequest(requestId) {
  return request({
    url: `${baseUrl}/friend/request/accept`,
    method: 'post',
    params: { requestId }
  });
}

/**
 * 拒绝好友申请
 * @param {Number} requestId 申请ID
 * @returns {Promise} Promise对象
 */
export function rejectFriendRequest(requestId) {
  return request({
    url: `${baseUrl}/friend/request/reject`,
    method: 'post',
    params: { requestId }
  });
}

/**
 * 搜索用户
 * @param {String} keyword 搜索关键词
 * @param {Number} userId 当前用户ID
 * @returns {Promise} Promise对象
 */
export function searchUsers(keyword, userId) {
  return request({
    url: `${baseUrl}/friend/search`,
    method: 'get',
    params: { keyword, userId }
  });
}

/**
 * 获取用户在线状态
 * @param {Number} userId 用户ID
 * @returns {Promise} Promise对象
 */
export function getUserStatus(userId) {
  return request({
    url: `${baseUrl}/user/status/${userId}`,
    method: 'get',
    errorHandler: (error) => {
      console.error('获取用户状态失败', error);
      // 出错时返回一个默认状态，避免前端处理错误
      return {
        code: 200,  // 返回成功码但使用默认值
        msg: 'error handled',
        data: '0'  // 默认离线
      };
    }
  });
}

/**
 * 获取用户在线状态（V2版本）
 * @param {Number} userId 用户ID
 * @returns {Promise} Promise对象
 */
export function getUserStatusV2(userId) {
  return request({
    url: `${baseUrl}/user/v2/status/${userId}`,
    method: 'get',
    errorHandler: (error) => {
      console.error('获取用户状态失败', error);
      // 出错时返回一个默认状态，避免前端处理错误
      return {
        code: 200,
        msg: 'error handled',
        data: '0'  // 默认离线
      };
    }
  });
}

/**
 * 批量获取用户在线状态
 * @param {Array} userIds 用户ID数组
 * @returns {Promise} Promise对象
 */
export function getBatchUserStatus(userIds) {
  return request({
    url: `${baseUrl}/user/v2/status/batch`,
    method: 'post',
    data: { userIds },
    errorHandler: (error) => {
      console.error('批量获取用户状态失败', error);
      return {
        code: 200,
        msg: 'error handled',
        data: {}  // 返回空对象
      };
    }
  });
}

/**
 * 获取用户所有未读消息数量
 * @param {Number} userId 用户ID
 * @returns {Promise} Promise对象
 */
export function getAllUnreadCount(userId) {
  return request({
    url: `${baseUrl}/user/unread/${userId}`,
    method: 'get',
    errorHandler: (error) => {
      console.error('获取未读消息数量失败', error);
      return {
        code: 500,
        msg: error.message || '获取未读消息数量失败',
        data: 0
      };
    }
  });
}

/**
 * 获取特定会话的未读消息数量
 * @param {Number} userId 用户ID
 * @param {Number} friendId 好友ID
 * @returns {Promise} Promise对象
 */
export function getConversationUnreadCount(userId, friendId) {
  return request({
    url: `${baseUrl}/user/unread/${userId}/${friendId}`,
    method: 'get'
  });
}

/**
 * 更新用户在线状态（心跳）
 * @param {Number} userId 用户ID
 * @returns {Promise} Promise对象
 */
export function updateUserStatus(userId) {
  return request({
    url: `${baseUrl}/user/v2/status/update`,
    method: 'post',
    data: { 
      userId,
      timestamp: Date.now() 
    },
    errorHandler: (error) => {
      console.error('更新用户状态失败', error);
      return {
        code: 500,
        msg: error.message || '更新用户状态失败',
        success: false
      };
    }
  });
}

/**
 * 清空特定好友间的聊天记录
 * @param {Number} userId 用户ID
 * @param {Number} friendId 好友ID
 * @returns {Promise} Promise对象
 */
export function clearChatMessages(userId, friendId) {
  return request({
    url: `${baseUrl}/message/clear/${userId}/${friendId}`,
    method: 'delete',
    errorHandler: (error) => {
      console.error('清空聊天记录失败', error);
      // 返回一个统一的错误对象，避免前端解析错误
      return {
        code: 500,
        message: error.message || '清空聊天记录失败',
        success: false
      };
    }
  });
}

/**
 * 获取最近一次清空聊天记录的时间
 * @param {Number} userId 用户ID
 * @param {Number} friendId 好友ID
 * @returns {Promise} Promise对象
 */
export function getLastClearTime(userId, friendId) {
  return request({
    url: `${baseUrl}/message/clear/time/${userId}/${friendId}`,
    method: 'get',
    errorHandler: (error) => {
      console.error('获取清空记录时间失败', error);
      // 返回一个统一的错误对象，避免前端解析错误
      return {
        code: 200,  // 返回成功码但使用默认值
        message: '获取清空记录时间失败',
        data: null
      };
    }
  });
}

/**
 * 获取用户信息
 * @param {Number} userId 用户ID
 * @returns {Promise} Promise对象
 */
export function getUserInfo(userId) {
  return request({
    url: `${baseUrl}/user/info/${userId}`,
    method: 'get',
    errorHandler: (error) => {
      console.error('获取用户信息失败', error);
      return {
        code: 500,
        msg: error.message || '获取用户信息失败',
        data: null
      };
    }
  });
}

/**
 * 上传聊天图片
 * @param {Object} file 文件对象
 * @returns {Promise} Promise对象
 */
export function uploadChatImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: `${baseUrl}/message/upload/image`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    errorHandler: (error) => {
      console.error('上传聊天图片失败', error);
      return {
        code: 500,
        msg: error.message || '上传聊天图片失败',
        data: null
      };
    },
    transformResponse: (response) => {
      // 尝试解析响应
      let result;
      try {
        if (typeof response === 'string') {
          result = JSON.parse(response);
        } else {
          result = response;
        }
        
        // 如果响应包含图片URL，确保URL格式正确
        if (result && result.code === 200 && result.data && result.data.url) {
          let imageUrl = result.data.url;
          
          // 确保URL有http前缀
          if (!imageUrl.startsWith('http')) {
            imageUrl = 'https://' + imageUrl;
            result.data.url = imageUrl;
          }
          
          // 确保OSS域名格式正确
          if (imageUrl.includes('tbytebit')) {
            // 处理不同的域名格式
            if (imageUrl.includes('.com/')) {
              if (!imageUrl.includes('oss-cn-beijing.aliyuncs.com')) {
                imageUrl = imageUrl.replace(/tbytebit\..*?\.com\//, 'tbytebit.oss-cn-beijing.aliyuncs.com/');
                result.data.url = imageUrl;
              }
            } else if (!imageUrl.includes('.')) {
              imageUrl = imageUrl.replace('tbytebit', 'tbytebit.oss-cn-beijing.aliyuncs.com');
              result.data.url = imageUrl;
            }
          }
          
          // 确保doctor-chat路径存在
          if (imageUrl.includes('oss-cn-beijing.aliyuncs.com/') && !imageUrl.includes('/doctor-chat/')) {
            imageUrl = imageUrl.replace('oss-cn-beijing.aliyuncs.com/', 'oss-cn-beijing.aliyuncs.com/doctor-chat/');
            result.data.url = imageUrl;
          }
          
          // 处理双斜杠问题
          imageUrl = imageUrl.replace(/([^:])\/\//g, '$1/');
          result.data.url = imageUrl;
          
          console.log('处理后的图片URL:', imageUrl);
        }
      } catch (e) {
        console.error('解析响应失败', e);
        return {
          code: 500,
          msg: '解析服务器响应失败',
          data: null
        };
      }
      
      return result;
    }
  });
}

