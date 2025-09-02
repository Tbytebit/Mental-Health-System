<template>
  <view class="add-friend-container">
    <!-- 顶部标题栏 -->
    <view class="header">
      <view class="header-left" @click="goBack">
        <uni-icons type="back" size="24" color="#333"></uni-icons>
      </view>
      <view class="header-title">添加好友</view>
      <view class="header-right"></view>
    </view>
    
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input">
        <uni-icons type="search" size="18" color="#999"></uni-icons>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索用户昵称" 
          @confirm="searchUser"
        />
      </view>
      <button class="search-button" @click="searchUser">搜索</button>
    </view>
    
    <!-- 搜索结果区域 -->
    <view class="search-result">
      <!-- 搜索前的提示 -->
      <view v-if="!hasSearched" class="search-tip">
        <image src="/static/images/search-tip.png" mode="aspectFit" class="tip-image"></image>
        <view class="tip-text">输入用户昵称搜索好友</view>
      </view>
      
      <!-- 搜索后无结果 -->
      <view v-else-if="hasSearched && userList.length === 0" class="empty-result">
        <image src="/static/images/empty-search.png" mode="aspectFit" class="empty-image"></image>
        <view class="empty-text">没有找到相关用户</view>
      </view>
      
      <!-- 搜索结果列表 -->
      <view v-else class="user-list">
        <view v-for="(item, index) in userList" :key="index" class="user-item" @click="viewUserDetail(item)">
          <image :src="item.avatar || '/static/images/default-avatar.png'" class="avatar"></image>
          <view class="user-info">
            <view class="user-name">{{ item.nickname || '用户' }}</view>
            <view class="user-signature">{{ item.signature || '这个人很懒，什么都没写~' }}</view>
          </view>
          <button 
            :class="['add-button', item.isFriend ? 'already-friend' : '']" 
            @click.stop="addFriend(item)"
          >
            {{ item.isFriend ? '已添加' : '添加' }}
          </button>
        </view>
      </view>
    </view>
    
    <!-- 添加好友弹窗 -->
    <uni-popup ref="addFriendPopup" type="dialog">
      <uni-popup-dialog
        title="添加好友"
        mode="input"
        placeholder="请输入验证信息"
        :value="verificationMessage"
        @confirm="sendFriendRequest"
        @close="cancelAddFriend"
        confirmText="发送"
        cancelText="取消"
      ></uni-popup-dialog>
    </uni-popup>
    
    <!-- 设置备注弹窗 -->
    <uni-popup ref="remarkPopup" type="dialog">
      <uni-popup-dialog
        title="设置备注"
        mode="input"
        placeholder="请输入好友备注名"
        :value="remarkName"
        @confirm="setFriendRemark"
        @close="cancelSetRemark"
        confirmText="确定"
        cancelText="取消"
      ></uni-popup-dialog>
    </uni-popup>
  </view>
</template>

<script>
import { sendFriendRequest, searchUsers } from '@/api/chat/index';

export default {
  data() {
    return {
      userId: null,
      searchKeyword: '',
      hasSearched: false,
      userList: [],
      verificationMessage: '请求添加您为好友',
      selectedUser: null,
      remarkName: '' // 添加备注名称字段
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
  },
  
  methods: {
    /**
     * 搜索用户
     */
    async searchUser() {
      if (!this.searchKeyword.trim()) {
        uni.showToast({
          title: '请输入搜索内容',
          icon: 'none'
        });
        return;
      }
      
      // 标记已搜索
      this.hasSearched = true;
      
      try {
        uni.showLoading({
          title: '搜索中...',
          mask: true
        });
        
        console.log('搜索关键词:', this.searchKeyword);
        console.log('用户ID:', this.userId);
        
        // 调用搜索用户API
        const res = await searchUsers(this.searchKeyword, this.userId);
        
        console.log('搜索结果:', res);
        
        if (res.code === 200) {
          this.userList = res.data || [];
        } else {
          this.userList = [];
          uni.showToast({
            title: res.message || '搜索失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('搜索用户失败', error);
        this.userList = [];
        uni.showToast({
          title: '搜索失败，请检查网络连接 ' + (error.message || ''),
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    /**
     * 添加好友
     */
    addFriend(user) {
      // 已经是好友，不处理
      if (user.isFriend) {
        return;
      }
      
      // 保存当前选中的用户
      this.selectedUser = user;
      
      // 初始化验证消息
      this.verificationMessage = '请求添加您为好友';
      
      // 显示弹窗
      this.$refs.addFriendPopup.open();
    },
    
    /**
     * 发送好友请求
     */
    async sendFriendRequest(message) {
      if (!this.selectedUser) return;
      
      try {
        uni.showLoading({
          title: '发送中...',
          mask: true
        });
        
        console.log('发送好友请求参数:', {
          fromUserId: this.userId,
          toUserId: this.selectedUser.userId,
          message
        });
        
        // 发送好友请求
        const res = await sendFriendRequest(this.userId, this.selectedUser.userId, message);
        
        console.log('发送好友请求响应:', res);
        
        if (res.code === 200) {
          // 请求成功后，显示设置备注弹窗
          this.$refs.addFriendPopup.close();
          this.remarkName = this.selectedUser.nickname || ''; // 默认使用昵称作为备注
          this.$refs.remarkPopup.open();
        } else {
          console.error('好友请求发送失败', res);
          this.$refs.addFriendPopup.close();
          uni.showToast({
            title: res.message || '发送失败，请稍后再试',
            icon: 'none',
            duration: 3000
          });
        }
      } catch (error) {
        console.error('发送好友请求失败', error);
        // 显示更详细的错误信息
        let errorMsg = '发送失败，请重试';
        if (error.response && error.response.data) {
          errorMsg = error.response.data.msg || error.response.data.message || errorMsg;
        } else if (error.message) {
          errorMsg = error.message;
        } else if (typeof error === 'string') {
          errorMsg = error;
        }
        
        this.$refs.addFriendPopup.close();
        uni.showToast({
          title: errorMsg,
          icon: 'none',
          duration: 3000
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    /**
     * 设置好友备注
     */
    async setFriendRemark(remark) {
      if (!this.selectedUser) return;
      
      try {
        uni.showLoading({
          title: '处理中...',
          mask: true
        });
        
        // 此时不需要再发送好友请求，因为在sendFriendRequest中已经发送过了
        // 仅需要更新好友备注
        uni.showToast({
          title: '好友请求已发送',
          icon: 'success'
        });
        
        // 更新用户状态
        const index = this.userList.findIndex(item => item.userId === this.selectedUser.userId);
        if (index > -1) {
          this.userList[index].isFriend = true;
        }
        
        this.$refs.remarkPopup.close();
      } catch (error) {
        console.error('设置好友备注失败', error);
        this.$refs.remarkPopup.close();
        
        // 获取错误信息
        let errorMsg = '操作失败，请重试';
        if (error.response && error.response.data) {
          errorMsg = error.response.data.msg || error.response.data.message || errorMsg;
        } else if (error.message) {
          errorMsg = error.message;
        }
        
        uni.showToast({
          title: errorMsg,
          icon: 'none',
          duration: 3000
        });
      } finally {
        uni.hideLoading();
        this.selectedUser = null;
      }
    },
    
    /**
     * 取消设置备注
     */
    cancelSetRemark() {
      // 即使不设置备注，好友请求也已发送成功
      uni.showToast({
        title: '好友请求已发送',
        icon: 'success'
      });
      
      // 更新用户状态
      const index = this.userList.findIndex(item => item.userId === this.selectedUser.userId);
      if (index > -1) {
        this.userList[index].isFriend = true;
      }
      
      this.selectedUser = null;
    },
    
    /**
     * 取消添加好友
     */
    cancelAddFriend() {
      this.selectedUser = null;
    },
    
    /**
     * 查看用户详情
     */
    viewUserDetail(user) {
      uni.navigateTo({
        url: `/pages/user/profile?userId=${user.userId}`
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
.add-friend-container {
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
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background-color: #ffffff;
  border-bottom: 1px solid #eaeaea;
}

.search-input {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 20px;
  padding: 5px 15px;
  margin-right: 10px;
}

.search-input input {
  flex: 1;
  height: 30px;
  margin-left: 10px;
  font-size: 14px;
}

.search-button {
  width: 60px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  background-color: #07c160;
  color: #ffffff;
  border-radius: 20px;
  font-size: 14px;
  padding: 0;
}

.search-result {
  flex: 1;
  width: 100%;
}

.search-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 100px;
}

.tip-image {
  width: 120px;
  height: 120px;
  margin-bottom: 15px;
}

.tip-text {
  color: #999999;
  font-size: 16px;
}

.empty-result {
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
}

.user-list {
  padding: 10px 0;
}

.user-item {
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

.user-info {
  flex: 1;
}

.user-name {
  font-size: 16px;
  font-weight: bold;
  color: #333333;
  margin-bottom: 5px;
}

.user-signature {
  font-size: 14px;
  color: #999999;
}

.add-button {
  width: 70px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  background-color: #07c160;
  color: #ffffff;
  border-radius: 20px;
  font-size: 14px;
  padding: 0;
}

.already-friend {
  background-color: #f5f5f5;
  color: #999999;
}
</style> 