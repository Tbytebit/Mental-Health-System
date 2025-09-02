<template>
  <view class="friend-request-container">
    <!-- 顶部标题栏 -->
    <view class="header">
      <view class="header-left" @click="goBack">
        <uni-icons type="back" size="24" color="#333"></uni-icons>
      </view>
      <view class="header-title">好友申请</view>
      <view class="header-right"></view>
    </view>
    
    <!-- Tab 切换 -->
    <view class="tab-bar">
      <view 
        :class="['tab-item', activeTab === 'received' ? 'active' : '']" 
        @click="switchTab('received')"
      >收到的</view>
      <view 
        :class="['tab-item', activeTab === 'sent' ? 'active' : '']" 
        @click="switchTab('sent')"
      >发出的</view>
    </view>
    
    <!-- 好友申请列表 -->
    <scroll-view 
      scroll-y="true" 
      class="request-list"
      :refresher-triggered="refreshing"
      refresher-enabled
      @refresherrefresh="refreshData"
    >
      <!-- 空状态 -->
      <view v-if="requestList.length === 0" class="empty-state">
        <image src="/static/images/empty-request.png" mode="aspectFit" class="empty-image"></image>
        <view class="empty-text">
          {{ activeTab === 'received' ? '暂无收到的好友申请' : '暂无发出的好友申请' }}
        </view>
        <button v-if="activeTab === 'received'" class="empty-button" @click="toAddFriend">添加好友</button>
      </view>
      
      <!-- 申请列表 -->
      <view v-else>
        <view v-for="(item, index) in requestList" :key="index" class="request-item">
          <image :src="item.avatar || '/static/images/default-avatar.png'" class="avatar"></image>
          <view class="request-info">
            <view class="request-name">{{ item.remark || item.nickname || '用户' }}</view>
            <view class="request-message">{{ item.message || '请求添加您为好友' }}</view>
            <view class="request-time">{{ formatTime(item.createTime) }}</view>
          </view>
          
          <!-- 操作按钮 -->
          <view class="request-actions" v-if="activeTab === 'received'">
            <!-- 待处理状态 -->
            <template v-if="item.status === 0">
              <button class="action-btn reject-btn" @click.stop="rejectRequest(item)">拒绝</button>
              <button class="action-btn accept-btn" @click.stop="acceptRequest(item)">接受</button>
            </template>
            <!-- 已处理状态 -->
            <view v-else class="request-status">
              {{ item.status === 1 ? '已接受' : '已拒绝' }}
            </view>
          </view>
          
          <!-- 发出申请的状态 -->
          <view class="request-status" v-else>
            {{ item.status === 0 ? '等待验证' : (item.status === 1 ? '已接受' : '已拒绝') }}
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { 
  getReceivedFriendRequests, 
  getSentFriendRequests, 
  acceptFriendRequest, 
  rejectFriendRequest,
  getUserInfo
} from '@/api/chat/index';

export default {
  data() {
    return {
      userId: null,
      activeTab: 'received', // 当前激活的标签页：received-收到的，sent-发出的
      requestList: [],
      refreshing: false
    };
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
    
    // 加载好友申请列表
    this.loadRequestList();
  },
  
  methods: {
    /**
     * 切换标签页
     */
    switchTab(tab) {
      if (this.activeTab !== tab) {
        this.activeTab = tab;
        this.requestList = []; // 清空列表
        this.loadRequestList(); // 重新加载数据
      }
    },
    
    /**
     * 加载好友申请列表
     */
    async loadRequestList() {
      try {
        uni.showLoading({
          title: '加载中...',
          mask: true
        });
        
        // 根据当前标签页加载不同的数据
        if (this.activeTab === 'received') {
          const res = await getReceivedFriendRequests(this.userId);
          if (res.code === 200 && res.data) {
            this.requestList = res.data;
            // 获取用户详细信息以显示昵称而非ID
            await this.enrichUserInfo(this.requestList, 'fromUserId');
          }
        } else {
          const res = await getSentFriendRequests(this.userId);
          if (res.code === 200 && res.data) {
            this.requestList = res.data;
            // 获取用户详细信息以显示昵称而非ID
            await this.enrichUserInfo(this.requestList, 'toUserId');
          }
        }
      } catch (error) {
        console.error('获取好友申请列表失败', error);
        uni.showToast({
          title: '获取列表失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    /**
     * 丰富用户信息，获取昵称和头像
     * @param {Array} list 用户列表
     * @param {String} userIdKey 用户ID的键名
     */
    async enrichUserInfo(list, userIdKey) {
      if (!list || list.length === 0) return;
      
      const promises = list.map(async (item) => {
        // 如果已有昵称和头像，则不需要再次获取
        if (item.nickname && item.avatar) return item;
        
        try {
          const userId = item[userIdKey];
          const res = await getUserInfo(userId);
          if (res.code === 200 && res.data) {
            item.nickname = res.data.nickname;
            item.avatar = res.data.avatar;
          }
        } catch (error) {
          console.error('获取用户信息失败', error);
        }
        
        return item;
      });
      
      await Promise.all(promises);
    },
    
    /**
     * 刷新数据
     */
    async refreshData() {
      this.refreshing = true;
      await this.loadRequestList();
      this.refreshing = false;
    },
    
    /**
     * 接受好友申请
     */
    async acceptRequest(request) {
      try {
        uni.showLoading({
          title: '处理中...',
          mask: true
        });
        
        // 确保请求对象中有requestId
        if (!request || !request.requestId) {
          uni.showToast({
            title: '申请信息不完整',
            icon: 'none'
          });
          return;
        }
        
        const res = await acceptFriendRequest(request.requestId);
        if (res.code === 200) {
          uni.showToast({
            title: '已添加好友',
            icon: 'success'
          });
          
          // 更新本地状态
          const index = this.requestList.findIndex(item => item.requestId === request.requestId);
          if (index > -1) {
            this.requestList[index].status = 1;
          }
          
          // 刷新好友列表
          uni.$emit('refreshFriendList');
          
          // 同时刷新聊天会话列表，确保新好友能立即出现在聊天列表中
          uni.$emit('refreshChatList');
          
          // 可以考虑直接跳转到与该好友的聊天界面
          if (request.fromUserId) {
            setTimeout(() => {
              uni.navigateTo({
                url: `/pages/chat/chat?friendId=${request.fromUserId}`
              });
            }, 500);
          }
        } else {
          uni.showToast({
            title: res.message || '操作失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('接受好友申请失败', error);
        uni.showToast({
          title: '操作失败，请重试',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    /**
     * 拒绝好友申请
     */
    async rejectRequest(request) {
      try {
        uni.showLoading({
          title: '处理中...',
          mask: true
        });
        
        // 确保请求对象中有requestId
        if (!request || !request.requestId) {
          uni.showToast({
            title: '申请信息不完整',
            icon: 'none'
          });
          return;
        }
        
        const res = await rejectFriendRequest(request.requestId);
        if (res.code === 200) {
          uni.showToast({
            title: '已拒绝申请',
            icon: 'success'
          });
          
          // 更新本地状态
          const index = this.requestList.findIndex(item => item.requestId === request.requestId);
          if (index > -1) {
            this.requestList[index].status = 2;
          }
        } else {
          uni.showToast({
            title: res.message || '操作失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('拒绝好友申请失败', error);
        uni.showToast({
          title: '操作失败，请重试',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    /**
     * 跳转到添加好友页面
     */
    toAddFriend() {
      uni.navigateTo({
        url: '/pages/chat/add-friend'
      });
    },
    
    /**
     * 返回上一页
     */
    goBack() {
      uni.navigateBack();
    },
    
    /**
     * 格式化时间
     */
    formatTime(timestamp) {
      if (!timestamp) return '';
      
      const date = new Date(timestamp);
      const now = new Date();
      
      // 今天
      if (date.toDateString() === now.toDateString()) {
        return date.getHours().toString().padStart(2, '0') + ':' + 
               date.getMinutes().toString().padStart(2, '0');
      }
      
      // 昨天
      const yesterday = new Date(now);
      yesterday.setDate(now.getDate() - 1);
      if (date.toDateString() === yesterday.toDateString()) {
        return '昨天';
      }
      
      // 其他日期
      return date.getFullYear() + '/' + 
             (date.getMonth() + 1).toString().padStart(2, '0') + '/' + 
             date.getDate().toString().padStart(2, '0');
    }
  }
};
</script>

<style lang="scss">
.friend-request-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f7f7f7;
}

.header {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  height: 50px;
  background-color: #ffffff;
  border-bottom: 1px solid #eaeaea;
  box-sizing: border-box;
}

.header-left, .header-right {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-title {
  flex: 1;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  color: #333333;
}

.tab-bar {
  display: flex;
  background-color: #ffffff;
  border-bottom: 1px solid #eaeaea;
}

.tab-item {
  flex: 1;
  height: 44px;
  line-height: 44px;
  text-align: center;
  font-size: 16px;
  color: #666666;
  position: relative;
}

.tab-item.active {
  color: #07c160;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background-color: #07c160;
  border-radius: 3px;
}

.request-list {
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

.request-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #ffffff;
  margin-bottom: 10px;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  background-color: #f0f0f0;
  margin-right: 15px;
}

.request-info {
  flex: 1;
}

.request-name {
  font-size: 16px;
  font-weight: bold;
  color: #333333;
  margin-bottom: 5px;
}

.request-message {
  font-size: 14px;
  color: #666666;
  margin-bottom: 5px;
}

.request-time {
  font-size: 12px;
  color: #999999;
}

.request-actions {
  display: flex;
}

.action-btn {
  margin-left: 10px;
  padding: 6px 12px;
  font-size: 14px;
  border-radius: 20px;
}

.reject-btn {
  background-color: #f5f5f5;
  color: #666666;
}

.accept-btn {
  background-color: #07c160;
  color: #ffffff;
}

.request-status {
  font-size: 14px;
  color: #999999;
  padding: 6px 12px;
}
</style> 