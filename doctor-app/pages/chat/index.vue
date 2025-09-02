<template>
  <view class="chat-container">
    <!-- 顶部标题栏 -->
    <view class="header">
      <view class="header-title">消息</view>
      <view class="header-actions">
        <view class="action-btn" @click="toFriendList">
          <uni-icons type="chat" size="24" color="#07c160"></uni-icons>
        </view>
        <view class="action-btn" @click="toFriendRequests">
          <uni-icons type="personadd" size="24" color="#07c160"></uni-icons>
          <view v-if="unreadRequestCount > 0" class="badge">{{ unreadRequestCount }}</view>
        </view>
      </view>
    </view>
    
    <!-- 会话列表 -->
    <scroll-view scroll-y="true" class="conversation-list" :refresher-triggered="refreshing" 
      refresher-enabled="true" @refresherrefresh="refreshData" @refresherpulling="onPulling">
      <!-- 空状态 -->
      <view v-if="conversationList.length === 0" class="empty-state">
        <image src="/static/images/empty-chat.png" mode="aspectFit" class="empty-image"></image>
        <view class="empty-text">暂无聊天记录</view>
        <button class="empty-button" @click="toFriendList">添加好友开始聊天</button>
      </view>
      
      <!-- 会话列表项 -->
      <view v-else>
        <view v-for="(item, index) in formattedConversationList" :key="index" class="conversation-item"
          @click="enterChat(item)">
          <!-- 头像 -->
          <view class="avatar-wrapper">
            <image :src="item.avatar" class="avatar"></image>
            <!-- 在线状态指示 -->
            <view :class="['online-status', item.isOnline ? 'online' : 'offline']"></view>
            <view v-if="item.unreadCount > 0" class="unread-badge">{{ item.unreadCount > 99 ? '99+' : item.unreadCount }}</view>
          </view>
          
          <!-- 内容 -->
          <view class="conversation-content">
            <view class="conversation-top">
              <view class="conversation-name">{{ item.friendName || '用户' + item.friendId }}</view>
              <view class="conversation-time">{{ item.lastTime }}</view>
            </view>
            <view class="conversation-bottom">
              <view class="conversation-message">{{ item.lastContent }}</view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getConversationList } from '@/utils/chat-storage';
import { getUnreadCount, getFriendList, getReceivedFriendRequests } from '@/api/chat/index';
import wsChat from '@/utils/ws-chat';

export default {
  data() {
    return {
      conversationList: [],
      friendMap: {}, // 好友ID到好友信息的映射
      userId: null,
      refreshing: false,
      unreadRequestCount: 0,
      friendStatusMap: {} // 新增：好友在线状态缓存
    };
  },
  
  computed: {
    // 格式化会话列表
    formattedConversationList() {
      return this.conversationList.map(item => {
        // 获取对话方信息
        const conversationId = item.conversationId;
        const ids = conversationId.split('_');
        const otherId = ids[0] == this.userId ? ids[1] : ids[0];
        
        // 查找对应的好友信息
        const friend = this.friendMap[otherId];
        const friendName = friend ? friend.remark || friend.nickname : `用户${otherId}`;
        const avatar = friend && friend.avatar ? friend.avatar : '/static/images/default-avatar.png';
        
        // 获取最后一条消息
        const lastMessage = item.lastMessage || {};
        
        // 计算未读消息数
        let unreadCount = 0;
        if (item.unreadCount !== undefined) {
          unreadCount = item.unreadCount;
        }
        
        // 获取在线状态
        const isOnline = this.friendStatusMap[otherId] === '1';
        
        return {
          ...item,
          friendId: otherId,
          friendName,
          avatar,
          lastMessage,
          lastContent: this.getMessageContent(lastMessage),
          lastTime: this.formatTime(lastMessage.createTime),
          unreadCount,
          isOnline
        };
      });
    }
  },
  
  onLoad() {
    this.userId = uni.getStorageSync('userId');
    if (!this.userId) {
      // 未登录，跳转到登录页
      uni.navigateTo({
        url: '/pages/login'
      });
      return;
    }
    
    // 初始化数据
    this.initData();
    
    // 初始化WebSocket连接
    this.initWebSocket();
    
    // 监听好友列表刷新事件
    uni.$on('refreshFriendList', this.loadFriendList);
    
    // 监听会话列表刷新事件
    uni.$on('refreshConversationList', this.loadConversations);
    
    // 监听好友状态变化事件
    uni.$on('user-status-change', this.handleUserStatusChange);
  },
  
  onShow() {
    // 重新加载数据
    this.initData();
    
    // 清除TabBar角标
    if (uni.removeTabBarBadge) {
      uni.removeTabBarBadge({
        index: 2 // 消息图标的TabBar索引
      });
    }
    
    // 检查未读消息
    wsChat.checkUnreadMessages();
  },
  
  onUnload() {
    // 移除事件监听
    uni.$off('refreshFriendList', this.loadFriendList);
    uni.$off('refreshConversationList', this.loadConversations);
    uni.$off('user-status-change', this.handleUserStatusChange);
  },
  
  methods: {
    /**
     * 初始化数据
     */
    async initData() {
      // 加载会话列表
      this.loadConversations();
      
      // 加载好友列表
      await this.loadFriendList();
      
      // 加载好友请求
      this.loadFriendRequests();
    },
    
    /**
     * 下拉刷新
     */
    async refreshData(e) {
      this.refreshing = true;
      await this.initData();
      this.refreshing = false;
    },
    
    /**
     * 下拉中
     */
    onPulling(e) {
      // 可以根据下拉距离做一些视觉效果
    },
    
    /**
     * 加载会话列表
     */
    async loadConversations() {
      // 从本地存储获取会话列表
      const list = getConversationList();
      
      // 过滤掉已删除好友的会话
      const filteredList = [];
      
      for (const item of list) {
        // 解析会话ID，格式为 userId1_userId2
        const [id1, id2] = item.conversationId.split('_');
        const friendId = parseInt(id1) === parseInt(this.userId) ? parseInt(id2) : parseInt(id1);
        
        // 检查是否还是好友
        const isFriend = Object.keys(this.friendMap).includes(friendId.toString());
        
        if (isFriend) {
          // 获取好友信息
          const friendInfo = this.friendMap[friendId] || {};
          
          filteredList.push({
            ...item,
            friendId: friendId,
            friendName: friendInfo.remark || friendInfo.nickname,
            friendAvatar: friendInfo.avatar
          });
        }
      }
      
      this.conversationList = filteredList;
      
      // 获取未读消息数
      this.updateUnreadCounts();
    },
    
    /**
     * 更新未读消息数量
     */
    async updateUnreadCounts() {
      try {
        const res = await getUnreadCount(this.userId);
        
        if (res.code === 200 && res.data) {
          const unreadMap = res.data;
          
          // 更新会话列表中的未读消息数
          this.conversationList = this.conversationList.map(item => {
            const friendId = item.friendId;
            const unreadCount = unreadMap[friendId] || 0;
            
            return {
              ...item,
              unreadCount
            };
          });
          
          // 计算总未读消息数
          const totalUnread = Object.values(unreadMap).reduce((sum, count) => sum + count, 0);
          
          // 更新TabBar未读数
          if (totalUnread > 0) {
            uni.setTabBarBadge({
              index: 2, // 消息图标的TabBar索引
              text: totalUnread > 99 ? '99+' : totalUnread.toString()
            });
          } else {
            uni.removeTabBarBadge({
              index: 2 // 消息图标的TabBar索引
            });
          }
        }
      } catch (error) {
        console.error('获取未读消息数量失败', error);
      }
    },
    
    /**
     * 加载好友列表
     */
    async loadFriendList() {
      try {
        const response = await getFriendList(this.userId);
        if (response.code === 200 && response.data) {
          // 将好友列表转换为以ID为key的映射表
          const friendMap = {};
          response.data.forEach(friend => {
            friendMap[friend.friendId] = friend;
          });
          this.friendMap = friendMap;
          
          // 更新会话列表中的好友信息（头像和姓名）
          this.updateConversationsWithFriendInfo();
          
          // 获取每个好友的在线状态
          this.fetchFriendsStatus();
        }
      } catch (error) {
        console.error('获取好友列表失败', error);
      }
    },
    
    /**
     * 更新会话列表中的好友信息
     */
    updateConversationsWithFriendInfo() {
      if (this.conversationList.length === 0) return;
      
      // 使用好友信息更新会话列表
      this.conversationList = this.conversationList.map(item => {
        // 获取对话方ID
        const conversationId = item.conversationId;
        const ids = conversationId.split('_');
        const friendId = ids[0] == this.userId ? ids[1] : ids[0];
        
        // 从好友映射表中获取好友信息
        const friend = this.friendMap[friendId];
        
        // 如果找到好友信息，更新会话项
        if (friend) {
          return {
            ...item,
            friendId: friendId,
            friendName: friend.remark || friend.nickname,
            friendAvatar: friend.avatar
          };
        }
        
        return item;
      });
    },
    
    /**
     * 加载好友请求
     */
    async loadFriendRequests() {
      try {
        const res = await getReceivedFriendRequests(this.userId);
        if (res.code === 200 && res.data) {
          // 计算未处理的好友请求数量
          const pendingRequests = res.data.filter(req => req.status === 0);
          this.unreadRequestCount = pendingRequests.length;
        }
      } catch (error) {
        console.error('获取好友请求失败', error);
      }
    },
    
    /**
     * 初始化WebSocket连接
     */
    initWebSocket() {
      wsChat.init({
        url: 'ws://localhost:6680/ws/chat/',
        userId: this.userId
      });
      
      // 添加消息监听器
      wsChat.addMessageListener(3, this.handleChatMessage); // 聊天消息
      wsChat.addMessageListener(5, this.handleFriendRequest); // 好友请求
      wsChat.addMessageListener(8, this.handleStatusChangeMessage); // 好友状态变化
    },
    
    /**
     * 处理接收到的聊天消息
     */
    handleChatMessage(message) {
      // 刷新会话列表
      this.loadConversations();
    },
    
    /**
     * 处理接收到的好友请求
     */
    handleFriendRequest(request) {
      // 刷新好友请求数量
      this.loadFriendRequests();
      
      // 显示通知
      uni.showToast({
        title: '收到新的好友请求',
        icon: 'none'
      });
    },
    
    /**
     * 进入聊天页面
     */
    enterChat(conversation) {
      // 获取对话好友ID
      const friendId = conversation.friendId;
      const friendName = conversation.friendName;
      const conversationId = conversation.conversationId;
      
      if (!friendId) {
        uni.showToast({
          title: '无法识别对话用户',
          icon: 'none'
        });
        return;
      }
      
      // 导航到聊天页面
      uni.navigateTo({
        url: `/pages/chat/chat?friendId=${friendId}&friendName=${encodeURIComponent(friendName)}&conversationId=${conversationId}`
      });
    },
    
    /**
     * 跳转到好友列表
     */
    toFriendList() {
      uni.navigateTo({
        url: '/pages/chat/friend-list'
      });
    },
    
    /**
     * 跳转到好友请求页面
     */
    toFriendRequests() {
      uni.navigateTo({
        url: '/pages/chat/friend-request'
      });
    },
    
    /**
     * 格式化最后一条消息
     */
    formatLastMessage(message) {
      if (!message) return '';
      
      // 根据消息类型格式化
      switch (message.type) {
        case 0: // 文本
          return message.content || '';
        case 1: // 图片
          return '[图片]';
        case 2: // 语音
          return '[语音]';
        case 3: // 视频
          return '[视频]';
        case 4: // 文件
          return '[文件]';
        default:
          return '';
      }
    },
    
    /**
     * 格式化时间
     */
    formatTime(timestamp) {
      if (!timestamp) return '';
      
      const date = new Date(timestamp);
      const now = new Date();
      
      // 今天的消息只显示时间
      if (date.toDateString() === now.toDateString()) {
        return date.getHours().toString().padStart(2, '0') + ':' + 
               date.getMinutes().toString().padStart(2, '0');
      }
      
      // 昨天的消息显示"昨天"
      const yesterday = new Date(now);
      yesterday.setDate(now.getDate() - 1);
      if (date.toDateString() === yesterday.toDateString()) {
        return '昨天';
      }
      
      // 本周的其他日期显示星期几
      const dayDiff = Math.floor((now - date) / (24 * 60 * 60 * 1000));
      if (dayDiff < 7) {
        const weekdays = ['日', '一', '二', '三', '四', '五', '六'];
        return '星期' + weekdays[date.getDay()];
      }
      
      // 其他日期显示具体日期
      return (date.getMonth() + 1) + '月' + date.getDate() + '日';
    },
    
    // 格式化消息内容显示
    getMessageContent(message) {
      if (!message || !message.type) return '';
      
      switch (message.type) {
        case 0: // 文本消息
          return message.content;
        case 1: // 图片消息
          return '[图片]';
        case 2: // 语音消息
          return '[语音]';
        case 3: // 视频消息
          return '[视频]';
        case 4: // 文件消息
          return '[文件]';
        default:
          return '[未知类型消息]';
      }
    },
    
    // 加载好友在线状态
    async fetchFriendsStatus() {
      const friendIds = Object.keys(this.friendMap);
      
      for (const friendId of friendIds) {
        try {
          const status = await wsChat.getFriendStatus(friendId);
          this.$set(this.friendStatusMap, friendId, status);
        } catch (error) {
          console.error(`获取好友${friendId}状态失败`, error);
        }
      }
    },
    
    /**
     * 处理用户状态变化事件
     */
    handleUserStatusChange(data) {
      const { userId, status } = data;
      if (userId && status !== undefined) {
        console.log(`更新好友${userId}状态为${status}`);
        this.$set(this.friendStatusMap, userId, status);
      }
    },
    
    /**
     * 处理WebSocket状态变化消息
     */
    handleStatusChangeMessage(data) {
      if (data && data.userId && data.status !== undefined) {
        console.log(`收到WebSocket好友${data.userId}状态变化消息：${data.status}`);
        this.$set(this.friendStatusMap, data.userId, data.status);
      }
    }
  }
};



</script>

<style lang="scss">
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f7f7f7;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  height: 50px;
  background-color: #ffffff;
  border-bottom: 1px solid #eaeaea;
  box-sizing: border-box;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
}

.header-actions {
  display: flex;
  align-items: center;
}

.action-btn {
  margin-left: 20px;
  position: relative;
}

.badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background-color: #ff0000;
  color: #ffffff;
  font-size: 12px;
  height: 18px;
  min-width: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 9px;
  padding: 0 4px;
  box-sizing: border-box;
}

.conversation-list {
  flex: 1;
  width: 100%;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 100px;
}

.empty-image {
  width: 120px;
  height: 120px;
  margin-bottom: 15px;
}

.empty-text {
  color: #999999;
  font-size: 16px;
  margin-bottom: 20px;
}

.empty-button {
  background-color: #07c160;
  color: #ffffff;
  font-size: 16px;
  padding: 8px 20px;
  border-radius: 20px;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #ffffff;
  border-bottom: 1px solid #f0f0f0;
}

.avatar-wrapper {
  position: relative;
  margin-right: 15px;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  background-color: #f0f0f0;
}

.online-status {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  position: absolute;
  right: -5px;
  top: 50%;
  transform: translateY(-50%);
  border: 1px solid #ffffff;
  box-shadow: 0 0 2px rgba(0, 0, 0, 0.2);
}

.online {
  background-color: #07c160;
}

.offline {
  background-color: #bbbbbb;
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #ff5a5f;
  color: #ffffff;
  font-size: 12px;
  padding: 0 6px;
  height: 18px;
  min-width: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}

.conversation-content {
  flex: 1;
  width: 0; // 确保内容能够被截断
}

.conversation-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.conversation-name {
  font-size: 16px;
  font-weight: bold;
  color: #333333;
  max-width: 60%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-time {
  font-size: 12px;
  color: #999999;
}

.conversation-bottom {
  display: flex;
  align-items: center;
}

.conversation-message {
  font-size: 14px;
  color: #666666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}
</style>