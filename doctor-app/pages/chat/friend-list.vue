<template>
  <view class="friend-list-container">
    <!-- 顶部标题栏 -->
    <view class="header">
      <view class="header-left" @click="goBack">
        <uni-icons type="back" size="24" color="#333"></uni-icons>
      </view>
      <view class="header-title">好友列表</view>
      <view class="header-right" @click="toSearch">
        <uni-icons type="search" size="24" color="#333"></uni-icons>
      </view>
    </view>
    
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input">
        <uni-icons type="search" size="18" color="#999"></uni-icons>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索好友" 
          @confirm="onSearch"
        />
      </view>
    </view>
    
    <!-- 好友列表 -->
    <scroll-view 
      scroll-y="true" 
      class="friend-list"
      :refresher-triggered="refreshing"
      refresher-enabled
      @refresherrefresh="refreshData"
    >
      <!-- 空状态 -->
      <view v-if="friendList.length === 0" class="empty-state">
        <image src="/static/images/empty-friend.png" mode="aspectFit" class="empty-image"></image>
        <view class="empty-text">暂无好友</view>
        <button class="empty-button" @click="toAddFriend">添加好友</button>
      </view>
      
      <!-- 好友列表 -->
      <view v-else>
        <view v-for="(item, index) in filteredFriendList" :key="index" class="friend-item">
          <view class="friend-content" @click="enterChat(item)">
            <view class="avatar-wrapper">
              <image :src="item.avatar || '/static/images/default-avatar.png'" class="avatar"></image>
              <view :class="['online-status', item.isOnline ? 'online' : 'offline']"></view>
            </view>
            <view class="friend-info">
              <view class="friend-name">{{ item.remark || item.nickname || '用户' }}</view>
              <view class="friend-signature">{{ item.signature || '这个人很懒，什么都没写~' }}</view>
            </view>
          </view>
          <view class="friend-actions">
            <uni-icons type="more-filled" size="24" color="#999" @click="showActionSheet(item)"></uni-icons>
          </view>
        </view>
      </view>
    </scroll-view>
    
    <!-- 底部菜单 -->
    <view class="footer">
      <button class="footer-button" @click="toAddFriend">
        <uni-icons type="personadd" size="24" color="#07c160"></uni-icons>
        <text>添加好友</text>
      </button>
    </view>
    
    <!-- 编辑备注弹窗 -->
    <uni-popup ref="remarkPopup" type="dialog">
      <uni-popup-dialog
        title="编辑备注"
        mode="input"
        placeholder="请输入好友备注名"
        :value="currentRemark"
        @confirm="updateRemark"
        confirmText="确定"
        cancelText="取消"
      ></uni-popup-dialog>
    </uni-popup>
  </view>
</template>

<script>
import { getFriendList as apiGetFriendList, updateFriendRemark, deleteFriend, clearChatMessages } from '@/api/chat/index';
import { getFriendList as getLocalFriendList, saveFriendList } from '@/utils/chat-storage';
import wsChat from '@/utils/ws-chat';

export default {
  data() {
    return {
      userId: null,
      friendList: [],
      searchKeyword: '',
      refreshing: false,
      currentFriend: null,
      currentRemark: '',
      friendStatusMap: {} // 好友在线状态缓存
    };
  },
  
  computed: {
    /**
     * 过滤后的好友列表
     */
    filteredFriendList() {
      if (!this.searchKeyword) {
        // 添加在线状态到好友列表
        return this.friendList.map(friend => {
          return {
            ...friend,
            isOnline: this.friendStatusMap[friend.friendId] === '1'
          };
        });
      }
      
      const keyword = this.searchKeyword.toLowerCase();
      return this.friendList.filter(friend => {
        const remark = (friend.remark || '').toLowerCase();
        const nickname = (friend.nickname || '').toLowerCase();
        
        return remark.includes(keyword) || nickname.includes(keyword);
      }).map(friend => {
        return {
          ...friend,
          isOnline: this.friendStatusMap[friend.friendId] === '1'
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
    
    // 加载好友列表
    this.loadFriendList();
    
    // 监听好友列表刷新事件
    uni.$on('refreshFriendList', this.loadFriendList);
    
    // 定时获取好友在线状态
    setInterval(() => {
      this.fetchFriendsStatus();
    }, 30000); // 每30秒更新一次
  },
  
  onUnload() {
    // 移除事件监听
    uni.$off('refreshFriendList', this.loadFriendList);
  },
  
  methods: {
    /**
     * 加载好友列表
     */
    async loadFriendList() {
      // 先从本地加载
      const localList = getLocalFriendList();
      if (localList.length > 0) {
        this.friendList = localList;
        // 获取在线状态
        this.fetchFriendsStatus();
      }
      
      try {
        // 从服务器获取最新列表
        const res = await apiGetFriendList(this.userId);
        if (res.code === 200 && res.data) {
          this.friendList = res.data;
          // 保存到本地
          saveFriendList(res.data);
          // 获取在线状态
          this.fetchFriendsStatus();
        }
      } catch (error) {
        console.error('获取好友列表失败', error);
        uni.showToast({
          title: '获取好友列表失败',
          icon: 'none'
        });
      }
    },
    
    /**
     * 获取好友在线状态
     */
    async fetchFriendsStatus() {
      if (!this.friendList || this.friendList.length === 0) return;
      
      for (const friend of this.friendList) {
        const friendId = friend.friendId;
        if (!friendId) continue;
        
        try {
          const status = await wsChat.getFriendStatus(friendId);
          this.$set(this.friendStatusMap, friendId, status);
        } catch (error) {
          console.error(`获取好友${friendId}状态失败`, error);
        }
      }
    },
    
    /**
     * 刷新数据
     */
    async refreshData() {
      this.refreshing = true;
      await this.loadFriendList();
      this.refreshing = false;
    },
    
    /**
     * 搜索
     */
    onSearch() {
      // 已通过计算属性实现
    },
    
    /**
     * 显示操作菜单
     */
    showActionSheet(friend) {
      this.currentFriend = friend;
      uni.showActionSheet({
        itemList: ['编辑备注', '删除好友'],
        success: (res) => {
          switch (res.tapIndex) {
            case 0:
              // 编辑备注
              this.showEditRemarkPopup(friend);
              break;
            case 1:
              // 删除好友
              this.confirmDeleteFriend(friend);
              break;
          }
        }
      });
    },
    
    /**
     * 显示编辑备注弹窗
     */
    showEditRemarkPopup(friend) {
      uni.navigateTo({
        url: `/pages/chat/edit-remark?userId=${this.userId}&friendId=${friend.friendId || friend.userId}&remark=${encodeURIComponent(friend.remark || '')}`
      });
    },
    
    /**
     * 更新好友备注
     */
    async updateRemark(remark) {
      if (!this.currentFriend) return;
      
      try {
        uni.showLoading({
          title: '保存中...',
          mask: true
        });
        
        console.log('更新备注参数:', {
          userId: this.userId,
          friendId: this.currentFriend.friendId,
          remark: remark
        });
        
        const res = await updateFriendRemark(this.userId, this.currentFriend.friendId, remark);
        
        console.log('更新备注响应:', res);
        
        if (res.code === 200) {
          uni.showToast({
            title: '备注已更新',
            icon: 'success'
          });
          
          // 更新列表中的备注
          const index = this.friendList.findIndex(item => item.friendId === this.currentFriend.friendId);
          if (index > -1) {
            this.friendList[index].remark = remark;
            // 更新本地存储
            saveFriendList(this.friendList);
          }
        } else {
          uni.showToast({
            title: res.message || '更新失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('更新备注失败', error);
        uni.showToast({
          title: '更新备注失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
        this.currentFriend = null;
      }
    },
    
    /**
     * 确认删除好友
     */
    confirmDeleteFriend(friend) {
      uni.showModal({
        title: '删除好友',
        content: `确定要删除好友 ${friend.remark || friend.nickname || ''}？`,
        success: (res) => {
          if (res.confirm) {
            this.deleteFriend(friend);
          }
        }
      });
    },
    
    /**
     * 删除好友
     */
    async deleteFriend(friend) {
      try {
        uni.showLoading({
          title: '删除中...',
          mask: true
        });
        
        console.log('开始删除好友:', {
          userId: this.userId,
          friendId: friend.friendId
        });
        
        // 调用删除好友API
        const res = await deleteFriend(this.userId, friend.friendId);
        
        console.log('删除好友API响应:', res);
        
        if (res.code === 200) {
          // 从列表中移除
          const index = this.friendList.findIndex(item => item.friendId === friend.friendId);
          if (index > -1) {
            this.friendList.splice(index, 1);
            // 更新本地存储
            saveFriendList(this.friendList);
          }
          
          // 构建会话ID
          let conversationId = '';
          if (this.userId < friend.friendId) {
            conversationId = `${this.userId}_${friend.friendId}`;
          } else {
            conversationId = `${friend.friendId}_${this.userId}`;
          }
          
          // 清理聊天记录
          try {
            await clearChatMessages(this.userId, friend.friendId);
          } catch (error) {
            console.error('清理聊天记录失败', error);
          }
          
          // 发送刷新会话列表事件
          uni.$emit('refreshConversationList');
          
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          });
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
     * 进入聊天页面
     */
    enterChat(friend) {
      const friendId = friend.friendId || friend.userId;
      
      if (!friendId) {
        uni.showToast({
          title: '无法获取好友信息',
          icon: 'none'
        });
        return;
      }
      
      // 构建会话ID
      let conversationId = '';
      if (parseInt(this.userId) < parseInt(friendId)) {
        conversationId = `${this.userId}_${friendId}`;
      } else {
        conversationId = `${friendId}_${this.userId}`;
      }
      
      // 导航到聊天页面，优先使用备注名
      uni.navigateTo({
        url: `/pages/chat/chat?friendId=${friendId}&friendName=${encodeURIComponent(friend.remark || friend.nickname || '用户')}&conversationId=${conversationId}`
      });
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
     * 跳转到搜索页
     */
    toSearch() {
      uni.navigateTo({
        url: '/pages/chat/search-friend'
      });
    },
    
    /**
     * 返回上一页
     */
    goBack() {
      uni.navigateBack();
    }
  }
};
</script>

<style lang="scss">
.friend-list-container {
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

.search-bar {
  padding: 10px 15px;
  background-color: #ffffff;
  border-bottom: 1px solid #eaeaea;
}

.search-input {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 20px;
  padding: 5px 15px;
}

.search-input input {
  flex: 1;
  height: 30px;
  margin-left: 10px;
  font-size: 14px;
}

.friend-list {
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

.friend-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  background-color: #ffffff;
  border-bottom: 1px solid #eaeaea;
}

.friend-content {
  flex: 1;
  display: flex;
  align-items: center;
}

.friend-actions {
  padding: 0 5px;
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
  position: absolute;
  right: -5px;
  top: 50%;
  transform: translateY(-50%);
  width: 10px;
  height: 10px;
  border-radius: 50%;
  box-shadow: 0 0 2px rgba(0, 0, 0, 0.2);
  border: 1px solid #ffffff;
}

.online {
  background-color: #07c160;
}

.offline {
  background-color: #999999;
}

.friend-info {
  flex: 1;
}

.friend-name {
  font-size: 16px;
  font-weight: bold;
  color: #333333;
  margin-bottom: 5px;
}

.friend-signature {
  font-size: 14px;
  color: #999999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.footer {
  padding: 15px;
  background-color: #ffffff;
  border-top: 1px solid #eaeaea;
}

.footer-button {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  color: #07c160;
  border-radius: 20px;
  padding: 10px 0;
}

.footer-button text {
  margin-left: 5px;
  font-size: 16px;
}
</style> 