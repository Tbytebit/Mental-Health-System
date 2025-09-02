<template>
  <view class="chat-page">
    <!-- 顶部导航栏 -->
    <view class="chat-header">
      <view class="header-left" @click="goBack">
        <uni-icons type="back" size="24" color="#333"></uni-icons>
      </view>
      <view class="header-title">{{ chatTitle }}</view>
      <view class="header-right" @click="showMoreActions">
        <uni-icons type="more-filled" size="24" color="#333"></uni-icons>
      </view>
    </view>
    
    <!-- 聊天内容区域 -->
    <scroll-view 
      scroll-y="true" 
      class="chat-content" 
      :scroll-top="scrollTop"
      :refresher-triggered="refreshing"
      refresher-enabled
      @refresherrefresh="loadMoreMessages"
      @refresherpulling="onPulling"
      ref="chatScroll"
    >
      <!-- 空状态 -->
      <view v-if="messageList.length === 0" class="empty-state">
        <image src="/static/images/empty-message.png" mode="aspectFit" class="empty-image"></image>
        <view class="empty-text">暂无消息，开始聊天吧</view>
      </view>
      
      <!-- 消息列表 -->
      <view v-else class="message-list">
        <!-- 日期分割线 -->
        <view v-for="(item, index) in messageList" :key="index">
          <view v-if="showDateDivider(index)" class="date-divider">
            <text>{{ formatDate(item.createTime) }}</text>
          </view>
          
          <!-- 消息项 -->
          <view class="msg-row" :class="{'msg-right': isSelfMessage(item), 'msg-left': !isSelfMessage(item)}">
            <image
              v-if="!isSelfMessage(item)"
              class="avatar"
              :src="friendInfo.avatar"
              @error="handleAvatarError('friend')"
            />
            
            <view class="msg-content" :class="{'msg-content-right': isSelfMessage(item), 'msg-content-left': !isSelfMessage(item)}">
              <view v-if="item.type === 0" class="text-message">{{ item.content }}</view>
              <view v-else-if="item.type === 1" class="image-message-container">
                <view class="image-error">
                  <text>图片功能已禁用</text>
                </view>
              </view>
              <view v-else class="unsupported-message">
                <uni-icons type="info" size="16" color="#999"></uni-icons>
                <text>{{ getTypeText(item.type) }}</text>
              </view>
              
              <!-- 消息状态 -->
              <view v-if="isSelfMessage(item) && item.status === -1" class="msg-status">
                <text class="status-failed">发送失败</text>
              </view>
            </view>
            
            <image
              v-if="isSelfMessage(item)"
              class="avatar"
              :src="selfAvatar"
              @error="handleAvatarError('self')"
            />
          </view>
        </view>
      </view>
    </scroll-view>
    
    <!-- 底部输入区域 -->
    <view class="chat-footer">
      <view class="footer-input-area">
        <textarea 
          class="message-input" 
          v-model="inputMessage" 
          placeholder="请输入消息..." 
          confirm-type="send"
          :adjust-position="false"
          :cursor-spacing="10"
          :show-confirm-bar="false"
          :auto-height="true"
          :maxlength="-1"
          @confirm="sendMessage"
        ></textarea>
        <view class="send-button" @click="sendMessage">发送</view>
      </view>
      <view class="footer-tools">
        <!-- 移除图片选择按钮 -->
      </view>
    </view>
  </view>
</template>

<script>
import { getMessages, saveMessage, markMessagesAsRead, clearConversationMessages, getFriendList as getLocalFriendList, saveFriendList } from '@/utils/chat-storage';
import { getChatHistory, markMessageRead, sendMessage as sendHttpMessage, getUserStatusV2 as getUserStatus, getFriendList, getUserInfo } from '@/api/chat/index';
import wsChat from '@/utils/ws-chat';

export default {
  data() {
    return {
      userId: null,
      friendId: null,
      conversationId: '',
      friendName: '',
      friendAvatar: '',
      selfAvatar: '',
      messageList: [],
      inputMessage: '',
      scrollTop: 0,
      refreshing: false,
      loadingMore: false,
      pageSize: 20,
      currentTimestamp: Date.now(), // 用于日期分割线
      friendStatus: '0', // 好友在线状态，默认离线
      statusCheckTimer: null, // 状态检查定时器
      lastHeartbeatTime: Date.now(), // 上次心跳时间戳
      heartbeatTimer: null, // 心跳定时器
      maxHeartbeatMissTime: 60000, // 最大心跳丢失时间（60秒）
      connectionLostTimer: null, // 连接丢失检测定时器
    friendInfo: { avatar: '' }, // 添加好友信息对象
    chatTitle: '聊天', // 默认标题
  };
},
  
  onLoad(options) {
    // 获取参数
    this.friendId = options.friendId;
    this.friendName = options.friendName ? decodeURIComponent(options.friendName) : '';
    this.conversationId = options.conversationId;
    this.userId = uni.getStorageSync('userId');
    
    if (!this.friendId || !this.userId) {
      uni.showToast({
        title: '参数错误',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
      return;
    }
    
    // 确保会话ID正确
    if (!this.conversationId) {
      // 构建会话ID
      if (parseInt(this.userId) < parseInt(this.friendId)) {
        this.conversationId = `${this.userId}_${this.friendId}`;
      } else {
        this.conversationId = `${this.friendId}_${this.userId}`;
      }
    }
    
    // 检查好友关系
    this.checkFriendRelation();
    
    // 获取好友信息
    this.loadFriendInfo();
    
    // 获取自己的头像
    this.loadSelfAvatar();
    
    // 加载消息
    this.loadLocalMessages();
    this.loadRemoteMessages();
    
    // 标记消息为已读
    this.markAsRead();
    
    // 监听新消息
    this.addMessageListener();
    
    // 监听已读回执
    uni.$on('message-read-receipt', this.handleReadReceipt);
    
    // 监听WebSocket连接事件
    uni.$on('websocket-connected', this.handleWebSocketConnected);
    uni.$on('websocket-closed', this.handleWebSocketClosed);
    uni.$on('websocket-error', this.handleWebSocketError);
    
    // 检查好友在线状态
    this.checkFriendStatus();
    
    // 定时检查好友状态
    this.statusCheckTimer = setInterval(() => {
      this.checkFriendStatus();
    }, 30000); // 每30秒检查一次
    
    // 启动客户端心跳
    this.startHeartbeat();
    
    // 启动连接监测
    this.startConnectionMonitor();
  },
  
  onShow() {
    console.log('聊天页面显示，重新初始化消息监听');
    console.log('当前WebSocket状态:', wsChat ? wsChat.isConnected : '未初始化');
    
    // 确保WebSocket已初始化并连接
    if (!wsChat) {
      console.log('WebSocket未初始化，尝试初始化');
      // 这里可能需要重新初始化WebSocket
      const token = uni.getStorageSync('token');
      if (token) {
        wsChat.init({
          url: 'ws://localhost:6680/ws',
          heartbeatInterval: 30000,
          userId: this.userId
        });
        wsChat.connect(token);
      }
    } else if (!wsChat.isConnected) {
      console.log('WebSocket未连接，尝试重连');
      wsChat.reconnect();
    }
    
    // 重新添加消息监听器
    this.addMessageListener();
    
    // 重新加载消息，确保显示最新内容
    this.loadLocalMessages();
    
    // 标记消息为已读
    this.markAsRead();
    
    // 重新检查好友状态
    this.checkFriendStatus();
  },
  
  onHide() {
    console.log('聊天页面隐藏');
    // 页面隐藏时标记消息为已读
    this.markAsRead();
  },

  onUnload() {
    console.log('聊天页面卸载，清理资源');
    
    // 移除消息监听器
    wsChat.removeMessageListener(3, this.handleNewMessage);
    
    // 移除已读回执监听器
    uni.$off('message-read-receipt', this.handleReadReceipt);
    
    // 移除WebSocket事件监听
    uni.$off('websocket-connected', this.handleWebSocketConnected);
    uni.$off('websocket-closed', this.handleWebSocketClosed);
    uni.$off('websocket-error', this.handleWebSocketError);
    
    // 清除状态检查定时器
    if (this.statusCheckTimer) {
      clearInterval(this.statusCheckTimer);
      this.statusCheckTimer = null;
    }
    
    // 标记消息为已读
    this.markAsRead();
    
    // 清除心跳定时器
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
    
    // 清除连接丢失检测定时器
    if (this.connectionLostTimer) {
      clearInterval(this.connectionLostTimer);
      this.connectionLostTimer = null;
    }
  },
  
  methods: {
    /**
     * 检查是否仍然是好友关系
     */
    async checkFriendRelation() {
      try {
        // 从本地获取好友列表
        const localFriendList = getLocalFriendList();
        const isFriend = localFriendList.some(friend => friend.friendId == this.friendId);
        
        if (!isFriend) {
          // 从服务器获取最新好友列表
          const res = await getFriendList(this.userId);
          if (res.code === 200 && res.data) {
            // 检查是否在好友列表中
            const stillFriend = res.data.some(friend => friend.friendId == this.friendId);
            if (!stillFriend) {
              uni.showModal({
                title: '提示',
                content: '该用户已不是您的好友',
                showCancel: false,
                success: () => {
                  uni.navigateBack();
                }
              });
              return false;
            }
            // 更新本地好友列表
            saveFriendList(res.data);
          }
        }
        return true;
      } catch (error) {
        console.error('检查好友关系失败', error);
        return true; // 出错时默认允许继续
      }
    },
    
    /**
     * 加载好友信息
     */
    async loadFriendInfo() {
      // 先尝试从本地获取好友列表
      const friendList = getLocalFriendList();
      const friend = friendList.find(f => f.userId == this.friendId);
      
      if (friend) {
        this.friendInfo = friend;
        this.friendAvatar = friend.avatar;
        // 设置聊天页面标题：优先使用备注名
        this.chatTitle = friend.remark || friend.nickname || '聊天';
      } else {
        // 如果本地没有，则从服务器获取
        try {
          // 先获取详细的用户信息
          const userInfo = await getUserInfo(this.friendId);
          if (userInfo.code === 200 && userInfo.data) {
            this.friendInfo = userInfo.data;
            this.friendAvatar = userInfo.data.avatar;
            
            // 再获取好友列表，查找是否有备注
            const res = await getFriendList(this.userId);
            if (res.code === 200 && res.data) {
              const friendWithRemark = res.data.find(f => f.userId == this.friendId);
              if (friendWithRemark && friendWithRemark.remark) {
                this.friendInfo.remark = friendWithRemark.remark;
                this.chatTitle = friendWithRemark.remark;
              } else {
                this.chatTitle = userInfo.data.nickname || '聊天';
              }
            } else {
              this.chatTitle = userInfo.data.nickname || '聊天';
            }
          } else {
            this.chatTitle = this.friendName || '聊天';
          }
        } catch (error) {
          console.error('获取好友信息失败', error);
          this.chatTitle = this.friendName || '聊天';
        }
      }
    },
    
    /**
     * 加载自己的头像
     */
    loadSelfAvatar() {
      try {
        // 尝试从本地获取用户信息
        const userInfo = uni.getStorageSync('userInfo');
        if (userInfo) {
          // 如果本地存储有用户信息且包含avatar，使用它
          const parsedUserInfo = typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo;
          if (parsedUserInfo.avatar) {
            this.selfAvatar = parsedUserInfo.avatar;
          }
        }
        
        // 如果本地没有，可以考虑从服务器获取
        if (!this.selfAvatar) {
          // 这里可以添加从服务器获取用户头像的逻辑
          this.fetchCurrentUserInfo();
        }
      } catch (error) {
        console.error('加载自己的头像失败', error);
      }
    },
    
    /**
     * 获取当前用户信息
     */
    async fetchCurrentUserInfo() {
      try {
        // 调用获取用户信息的API
        const res = await getUserInfo(this.userId);
        if (res.code === 200 && res.data) {
          this.selfAvatar = res.data.avatar;
          
          // 保存到本地
          uni.setStorageSync('userInfo', JSON.stringify(res.data));
        }
      } catch (error) {
        console.error('获取用户信息失败', error);
      }
    },
    
    /**
     * 处理头像加载错误
     */
    handleAvatarError(type) {
      console.error(`${type}头像加载失败`);
      if (type === 'friend') {
        // 好友头像加载失败，使用默认头像
        this.friendInfo.avatar = '/static/images/default-avatar.png';
      } else if (type === 'self') {
        // 自己的头像加载失败，使用默认头像
        this.selfAvatar = '/static/images/default-avatar.png';
      }
    },
    
    /**
     * 显示更多操作
     */
    showMoreActions() {
      uni.showActionSheet({
        itemList: ['查看好友信息', '清空聊天记录', '删除好友'],
        success: (res) => {
          switch (res.tapIndex) {
            case 0:
              // 查看好友信息
              this.viewFriendInfo();
              break;
            case 1:
              // 清空聊天记录
              this.confirmClearMessages();
              break;
            case 2:
              // 删除好友
              this.confirmDeleteFriend();
              break;
          }
        }
      });
    },
    
    /**
     * 确认删除好友
     */
    confirmDeleteFriend() {
      uni.showModal({
        title: '删除好友',
        content: `确定要删除好友"${this.chatTitle}"吗？删除后将清空聊天记录。`,
        success: (res) => {
          if (res.confirm) {
            this.deleteFriend();
          }
        }
      });
    },
    
    /**
     * 删除好友
     */
    async deleteFriend() {
      try {
        uni.showLoading({
          title: '删除中...',
          mask: true
        });
        
        console.log('开始删除好友:', {
          userId: this.userId,
          friendId: this.friendId
        });
        
        // 导入删除好友API
        const { deleteFriend, clearChatMessages } = require('@/api/chat/index');
        
        // 调用删除好友API
        const res = await deleteFriend(this.userId, this.friendId);
        
        console.log('删除好友API响应:', res);
        
        if (res.code === 200) {
          // 清理聊天记录
          try {
            await clearChatMessages(this.userId, this.friendId);
          } catch (error) {
            console.error('清理聊天记录失败', error);
          }
          
          // 更新本地好友列表
          const localFriendList = getLocalFriendList();
          const updatedList = localFriendList.filter(friend => friend.friendId != this.friendId);
          saveFriendList(updatedList);
          
          // 发送刷新会话列表事件
          uni.$emit('refreshConversationList');
          uni.$emit('refreshFriendList');
          
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          });
          
          // 返回上一页
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          uni.showToast({
            title: res.message || res.msg || '删除失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('删除好友失败', error);
        uni.showToast({
          title: '删除好友失败，请稍后重试',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    /**
     * 查看好友信息
     */
    viewFriendInfo() {
      uni.navigateTo({
        url: `/pages/chat/friend-detail?friendId=${this.friendId}`
      });
    },
    
    /**
     * 加载本地消息
     */
    loadLocalMessages() {
      if (!this.conversationId) {
        // 构建会话ID
        if (this.userId < this.friendId) {
          this.conversationId = `${this.userId}_${this.friendId}`;
        } else {
          this.conversationId = `${this.friendId}_${this.userId}`;
        }
      }
      
      // 从本地存储获取消息
      let messages = getMessages(this.conversationId);
      
      // 对消息进行排序（确保按时间顺序排列，最新的消息在底部）
      messages.sort((a, b) => {
        const timeA = new Date(a.createTime).getTime();
        const timeB = new Date(b.createTime).getTime();
        return timeA - timeB;
      });
      
      // 处理每条消息，标记图片消息为"已禁用"
      messages.forEach(message => {
        // 为图片类型的消息添加禁用标记
        if (message.type === 1) {
          message.loadError = true; // 标记所有图片消息为不可用
        }
      });
      
      this.messageList = messages;
      
      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom();
      });
    },
    
    /**
     * 加载远程消息
     */
    /*
    async loadRemoteMessages() {
      try {
        const res = await getChatHistory(this.userId, this.friendId, 50);
        if (res.code === 200 && res.data) {
          // 后端已经过滤掉清空时间之前的消息
          // 保存消息到本地
          res.data.forEach(message => {
            saveMessage(this.conversationId, message);
          });
          
          // 重新加载本地消息
          this.loadLocalMessages();
        }
      } catch (error) {
        console.error('加载远程消息失败', error);
      }
    },
    */
    /**
 * 加载远程消息
 */
async loadRemoteMessages() {
  try {
    // 先获取最近一次清空时间
    const { getLastClearTime } = require('@/api/chat/index');
    const clearTimeResult = await getLastClearTime(this.userId, this.friendId);
    
    // 获取聊天历史
    const res = await getChatHistory(this.userId, this.friendId, 50);
    if (res.code === 200 && res.data) {
      // 后端已经根据清空时间过滤了消息，直接使用
      // 但为了确保一致性，前端也进行一次过滤
      let filteredMessages = res.data;
      
      if (clearTimeResult.code === 200 && clearTimeResult.data) {
        const clearTime = new Date(clearTimeResult.data).getTime();
        filteredMessages = res.data.filter(message => {
          const messageTime = new Date(message.createTime).getTime();
          return messageTime > clearTime;
        });
      }
      
      // 导入存储函数
      const { clearConversationMessages } = require('@/utils/chat-storage');
      
      // 完全清空本地消息，避免重复
      clearConversationMessages(this.conversationId);
      
      // 重新保存所有消息
      filteredMessages.forEach(message => {
        saveMessage(this.conversationId, message);
      });
      
      // 重新加载本地消息
      this.loadLocalMessages();
    }
  } catch (error) {
    console.error('加载远程消息失败', error);
  }
},
    /**
     * 加载更多消息（下拉刷新）
     */
    async loadMoreMessages() {
      try {
        // 设置加载状态
        this.loadingMore = true;
        
        // 获取当前最早的消息时间，用于分页
        const oldestMessage = this.messageList[this.messageList.length - 1];
        const timestamp = oldestMessage ? oldestMessage.createTime : new Date();
        
        // 加载更早的消息
        const res = await getChatHistory(this.userId, this.friendId, this.pageSize);
        if (res.code === 200 && res.data && res.data.length > 0) {
          // 保存消息到本地
          res.data.forEach(message => {
            saveMessage(this.conversationId, message);
          });
          
          // 重新加载本地消息
          this.loadLocalMessages();
        }
      } catch (error) {
        console.error('加载更多消息失败', error);
      } finally {
        // 结束刷新状态
        this.refreshing = false;
        this.loadingMore = false;
      }
    },
    
    /**
     * 下拉中
     */
    onPulling(e) {
      // 可以在这里处理下拉的状态
    },
    
    /**
     * 发送消息
     */
    async sendMessage() {
      if (!this.inputMessage.trim()) return;
      
      // 构建消息对象
      const message = {
        conversationId: this.conversationId,
        senderId: this.userId,
        receiverId: this.friendId,
        content: this.inputMessage,
        type: 0, // 文本消息
        status: 1, // 直接设置为已发送状态
        createTime: Date.now(),
        tempId: 'temp_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9) // 临时ID，用于本地去重
      };
      
      // 清空输入框
      const inputContent = this.inputMessage;
      this.inputMessage = '';
      
      // 保存到本地
      saveMessage(this.conversationId, message);
      
      // 重新加载本地消息以确保显示正确
      this.loadLocalMessages();
      
      // 检查好友在线状态，决定发送方式
      if (this.friendStatus === '1') {
        // 好友在线，使用WebSocket发送
        wsChat.sendChatMessage(message)
          .then(() => {
            console.log('WebSocket消息发送成功');
          })
          .catch(err => {
            console.error('WebSocket消息发送失败', err);
            // 失败时使用HTTP发送
            this.sendMessageByHttp(message, inputContent);
          });
      } else {
        // 好友离线，使用HTTP发送
        this.sendMessageByHttp(message, inputContent);
      }
    },
    
    /**
     * 通过HTTP发送消息
     */
    sendMessageByHttp(message, originalContent) {
      // 使用API发送消息
      sendHttpMessage(message)
        .then(res => {
          if (res.code !== 200) {
            // 发送失败
            message.status = -1;
            // 更新本地存储
            saveMessage(this.conversationId, message);
            
            uni.showToast({
              title: '消息发送失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('HTTP发送消息失败', err);
          // 更新消息状态为发送失败
          message.status = -1;
          // 更新本地存储
          saveMessage(this.conversationId, message);
          
          uni.showToast({
            title: '消息发送失败',
            icon: 'none'
          });
        });
    },
    
    /**
     * 添加消息监听器
     */
    addMessageListener() {
      console.log('添加WebSocket消息监听器');
      console.log('当前WebSocket实例:', wsChat);
      console.log('handleNewMessage方法:', typeof this.handleNewMessage);
      
      // 确保WebSocket已初始化
      if (!wsChat) {
        console.error('WebSocket实例未初始化');
        return;
      }
      
      wsChat.addMessageListener(3, this.handleNewMessage); // 3 是 CHAT 消息类型
      console.log('消息监听器添加完成');
    },
    
    /**
     * 处理新消息
     */
    handleNewMessage(message) {
      console.log('handleNewMessage被调用，收到新消息:', message);
      console.log('当前friendId:', this.friendId);
      console.log('消息senderId:', message.senderId, '消息receiverId:', message.receiverId);
      
      // 判断是否是当前会话的消息
      const isCurrentConversation = (
        (message.senderId == this.friendId && message.receiverId == this.userId) ||
        (message.senderId == this.userId && message.receiverId == this.friendId)
      );
      
      console.log('是否属于当前会话:', isCurrentConversation);
      
      if (isCurrentConversation) {
        console.log('消息属于当前会话，开始处理');
        
        try {
          // 保存新消息到本地
          saveMessage(this.conversationId, message);
          console.log('消息已保存到本地存储');
          
          // 立即更新消息列表，避免重新加载延迟
          this.addMessageToList(message);
          console.log('消息已添加到UI列表');
          
          // 滚动到底部
          this.$nextTick(() => {
            console.log('执行滚动到底部');
            this.scrollToBottom();
          });
          
          // 发送已读回执（仅对接收到的消息）
          if (message.senderId == this.friendId) {
            console.log('发送已读回执');
            wsChat.sendReadReceipt(this.friendId, this.userId);
            
            // 标记消息为已读
            this.markAsRead();
          }
          
          console.log('新消息处理完成');
        } catch (error) {
          console.error('处理新消息失败:', error);
          // 出错时重新加载消息列表
          this.loadLocalMessages();
        }
      } else {
        console.log('消息不属于当前会话，忽略处理');
      }
    },
    
    /**
     * 添加消息到列表（避免重复）
     */
    addMessageToList(message) {
      // 检查消息是否已存在（基于ID或时间戳）
      const exists = this.messageList.some(msg => 
        (msg.id && msg.id === message.id) ||
        (msg.tempId && msg.tempId === message.tempId) ||
        (msg.createTime === message.createTime && 
         msg.senderId === message.senderId && 
         msg.content === message.content)
      );
      
      if (!exists) {
        // 插入消息到正确位置（按时间排序）
        const insertIndex = this.messageList.findIndex(msg => 
          new Date(msg.createTime) > new Date(message.createTime)
        );
        
        if (insertIndex === -1) {
          // 添加到末尾
          this.messageList.push(message);
        } else {
          // 插入到指定位置
          this.messageList.splice(insertIndex, 0, message);
        }
        
        console.log('消息已添加到列表，当前消息数:', this.messageList.length);
      } else {
        console.log('消息已存在，跳过添加');
      }
    },
    
    /**
     * 标记消息为已读
     */
    async markAsRead() {
      try {
        // 本地标记
        markMessagesAsRead(this.conversationId, this.friendId);
        
        // 确保ID为数字类型
        const senderId = parseInt(this.friendId);
        const receiverId = parseInt(this.userId);
        
        // 检查参数是否有效
        if (isNaN(senderId) || isNaN(receiverId)) {
          console.error('标记消息已读失败: 无效的ID', { senderId, receiverId });
          return;
        }
        
        // 服务器标记 - 添加重试机制
        let retries = 2;
        let success = false;
        
        while (retries > 0 && !success) {
          try {
            const res = await markMessageRead(senderId, receiverId);
            if (res && res.code === 200) {
              success = true;
            } else {
              console.warn(`标记已读尝试失败(还剩${retries}次重试): `, res);
              retries--;
            }
          } catch (err) {
            console.error(`标记已读尝试失败(还剩${retries}次重试): `, err);
            retries--;
          }
          
          // 如果失败且还有重试次数，等待一段时间
          if (!success && retries > 0) {
            await new Promise(resolve => setTimeout(resolve, 500));
          }
        }
      } catch (error) {
        console.error('标记消息已读失败', error);
        // 错误不影响用户体验，不需要提示用户
      }
    },
    
    /**
     * 返回上一页
     */
    goBack() {
      uni.navigateBack();
    },
    
    /**
     * 滚动到底部
     */
    scrollToBottom() {
      this.$nextTick(() => {
        // 重置滚动位置以触发滚动
        this.scrollTop = 0;
        
        // 设置延迟以确保视图更新
        setTimeout(() => {
          // 模拟一个很大的值来滚动到底部
          this.scrollTop = 999999;
        }, 100);
      });
    },
    
    /**
     * 判断是否需要显示日期分割线
     */
    showDateDivider(index) {
      if (index === 0) return true;
      
      const currentMsg = this.messageList[index];
      const prevMsg = this.messageList[index - 1];
      
      // 如果与前一条消息时间相差超过30分钟，显示日期分割线
      const currentTime = new Date(currentMsg.createTime).getTime();
      const prevTime = new Date(prevMsg.createTime).getTime();
      
      return (currentTime - prevTime) > 30 * 60 * 1000;
    },
    
    /**
     * 格式化日期
     */
    formatDate(timestamp) {
      if (!timestamp) return '';
      
      const date = new Date(timestamp);
      const now = new Date();
      
      // 今天
      if (date.toDateString() === now.toDateString()) {
        return '今天 ' + date.getHours().toString().padStart(2, '0') + ':' + 
               date.getMinutes().toString().padStart(2, '0');
      }
      
      // 昨天
      const yesterday = new Date(now);
      yesterday.setDate(now.getDate() - 1);
      if (date.toDateString() === yesterday.toDateString()) {
        return '昨天 ' + date.getHours().toString().padStart(2, '0') + ':' + 
               date.getMinutes().toString().padStart(2, '0');
      }
      
      // 其他日期
      return date.getFullYear() + '/' + 
             (date.getMonth() + 1).toString().padStart(2, '0') + '/' + 
             date.getDate().toString().padStart(2, '0') + ' ' + 
             date.getHours().toString().padStart(2, '0') + ':' + 
             date.getMinutes().toString().padStart(2, '0');
    },
    
    /**
     * 判断是否是自己发的消息
     */
    isSelfMessage(message) {
      return message.senderId === this.userId;
    },
    
    /**
     * 获取消息类型文本
     */
    getTypeText(type) {
      switch (type) {
        case 0: return '文本消息';
        case 1: return '图片消息';
        case 2: return '语音消息';
        case 3: return '视频消息';
        case 4: return '文件消息';
        default: return '未知类型消息';
      }
    },
    
    /**
     * 启动客户端心跳
     */
    startHeartbeat() {
      // 清除可能存在的心跳定时器
      if (this.heartbeatTimer) {
        clearInterval(this.heartbeatTimer);
      }
      
      // 设置心跳定时器，每30秒发送一次
      this.heartbeatTimer = setInterval(() => {
        // 只有在WebSocket连接正常时发送心跳
        if (wsChat.isConnected) {
          this.sendHeartbeat();
        } else {
          console.warn('WebSocket未连接，无法发送心跳');
          // 尝试重新连接
          wsChat.reconnect();
        }
      }, 30000);
    },
    
    /**
     * 发送心跳包
     */
    sendHeartbeat() {
      this.lastHeartbeatTime = Date.now();
      wsChat.send({
        type: 2, // 心跳消息类型
        data: JSON.stringify({
          userId: this.userId,
          timestamp: this.lastHeartbeatTime
        })
      }).then(() => {
        console.log('聊天页面心跳包发送成功');
      }).catch(error => {
        console.error('聊天页面心跳包发送失败', error);
      });
    },
    
    /**
     * 启动连接监测
     */
    startConnectionMonitor() {
      // 清除可能存在的连接丢失检测定时器
      if (this.connectionLostTimer) {
        clearInterval(this.connectionLostTimer);
      }
      
      // 设置连接丢失检测定时器，每10秒检查一次
      this.connectionLostTimer = setInterval(() => {
        const currentTime = Date.now();
        const timeSinceLastHeartbeat = currentTime - this.lastHeartbeatTime;
        
        // 如果超过最大心跳丢失时间，认为连接已丢失
        if (timeSinceLastHeartbeat > this.maxHeartbeatMissTime) {
          console.warn(`超过${this.maxHeartbeatMissTime / 1000}秒未收到心跳响应，可能已断开连接`);
          
          // 尝试重新建立WebSocket连接
          if (!wsChat.isConnected) {
            console.log('WebSocket未连接，尝试重新连接');
            wsChat.reconnect();
          }
        }
      }, 10000); // 每10秒检查一次
    },
    
    /**
     * 检查好友在线状态
     */
    async checkFriendStatus() {
      try {
        const res = await getUserStatus(this.friendId);
        if (res && res.code === 200) {
          this.friendStatus = res.data;
        }
      } catch (error) {
        console.error('获取好友状态失败', error);
        this.friendStatus = '0'; // 出错时默认离线
      }
    },
    
    /**
     * 处理已读回执
     */
    handleReadReceipt(data) {
      if (data && data.conversationId === this.conversationId) {
        // 更新本地消息显示
        this.loadLocalMessages();
      }
    },
    
    /**
     * 处理WebSocket连接成功事件
     */
    handleWebSocketConnected(data) {
      console.log('WebSocket已连接', data);
      // 连接成功后检查好友状态
      this.checkFriendStatus();
    },
    
    /**
     * 处理WebSocket连接关闭事件
     */
    handleWebSocketClosed(data) {
      console.log('WebSocket已关闭', data);
      // 连接关闭时可以显示连接断开提示
      uni.showToast({
        title: '聊天服务连接已断开，正在重连...',
        icon: 'none',
        duration: 2000
      });
    },
    
    /**
     * 处理WebSocket连接错误事件
     */
    handleWebSocketError(data) {
      console.error('WebSocket连接错误', data);
      // 连接错误时可以显示错误提示
      uni.showToast({
        title: '聊天服务连接出错，正在重连...',
        icon: 'none',
        duration: 2000
      });
    },
    
    /**
     * 确认清空消息
     */
    confirmClearMessages() {
      uni.showModal({
        title: '清空聊天记录',
        content: '确定要清空全部聊天记录吗？',
        success: (res) => {
          if (res.confirm) {
            this.clearMessages();
          }
        }
      });
    },
    
    /**
     * 清空消息
     */
    /**
 * 清空消息
 */
async clearMessages() {
  try {
    uni.showLoading({
      title: '清空中...',
      mask: true
    });
    
    // 先调用服务器清空接口，记录清空时间
    const { clearChatMessages } = require('@/api/chat/index');
    const clearResult = await clearChatMessages(this.userId, this.friendId);
    
    if (clearResult.code === 200) {
      // 服务器清空成功后，再清空本地消息
      clearConversationMessages(this.conversationId);
      
      // 清空当前消息列表
      this.messageList = [];
      
      // 刷新会话列表
      uni.$emit('refreshConversationList');
      
      uni.showToast({
        title: '已清空',
        icon: 'success'
      });
    } else {
      throw new Error(clearResult.message || '清空失败');
    }
  } catch (error) {
    console.error('清空消息失败', error);
    uni.showToast({
      title: '清空失败',
      icon: 'none'
    });
  } finally {
    uni.hideLoading();
  }
},
  }
};
</script>

<style lang="scss">
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f1f1f1;
}

/* 顶部导航栏 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  background-color: #ffffff;
  border-bottom: 1px solid #eeeeee;
}

.header-left, .header-right {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-title {
  flex: 1;
  text-align: center;
  font-size: 17px;
  font-weight: 500;
  color: #333333;
}

.chat-content {
  flex: 1;
  position: relative;
  padding: 5px 0 10px;
  background-color: #f1f1f1;
}

.empty-state {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0 30px;
}

.empty-image {
  width: 120px;
  height: 120px;
  margin-bottom: 15px;
  opacity: 0.8;
}

.empty-text {
  color: #999999;
  font-size: 16px;
}

.message-list {
  width: 100%;
  padding: 0 10px;
}

.date-divider {
  text-align: center;
  margin: 10px 0;
}

.date-divider text {
  background-color: rgba(0, 0, 0, 0.05);
  color: #999999;
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 10px;
}

.msg-row {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.msg-left {
  justify-content: flex-start;
}

.msg-right {
  justify-content: flex-end;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #f0f0f0;
  flex-shrink: 0;
}

.msg-content {
  max-width: 65%;
  margin: 0 8px;
  padding: 8px 12px;
  border-radius: 16px;
  word-break: break-all;
}

.msg-content-left {
  background-color: #ffffff;
  color: #333333;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.msg-content-right {
  background-color: #9EEA6A;
  color: #333333;
  border-top-right-radius: 4px;
}

.text-message {
  font-size: 15px;
  line-height: 1.5;
}

.image-message-container {
  position: relative;
  width: 100%;
  padding: 20px;
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}

.unsupported-message {
  display: flex;
  align-items: center;
  color: #999999;
}

.unsupported-message text {
  margin-left: 5px;
  font-size: 14px;
}

.msg-status {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 4px;
}

.status-failed {
  font-size: 12px;
  color: #ff5a5f;
}

/* 底部输入区域 */
.chat-footer {
  width: 100%;
  background-color: #f8f8f8;
  border-top: 1px solid #eaeaea;
  padding: 10px 12px;
  box-sizing: border-box;
}

.footer-input-area {
  display: flex;
  align-items: center;
}

.message-input {
  flex: 1;
  background-color: #ffffff;
  border-radius: 20px;
  padding: 8px 15px;
  font-size: 15px;
  min-height: 20px;
  max-height: 80px;
  border: 1px solid #e5e5e5;
  box-sizing: border-box;
}

.send-button {
  width: 64px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  background-color: #07c160;
  color: #ffffff;
  border-radius: 18px;
  margin-left: 10px;
  font-size: 15px;
  font-weight: 500;
}

.footer-tools {
  display: flex;
  align-items: center;
  margin-top: 8px;
}

.tool-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>