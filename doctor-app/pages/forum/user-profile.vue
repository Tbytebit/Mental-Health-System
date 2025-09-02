<template>
  <view class="user-profile-container">
    <!-- 顶部导航栏 -->
    <view class="header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="back" size="24"></uni-icons>
      </view>
      <view class="title">用户信息</view>
    </view>
    
    <!-- 用户基本信息 -->
    <view class="user-info">
      <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
      <view class="info-content">
        <text class="username">{{ userInfo.nickName || userInfo.userName || '未知用户' }}</text>
        <text class="user-id">ID: {{ userId }}</text>
        <text class="bio">{{ userInfo.signature || '这个用户很懒，什么都没有留下' }}</text>
      </view>
    </view>
    
    <!-- 操作按钮区 -->
    <view class="action-btns" v-if="userId && profileData.userId !== userId">
      <button 
        class="primary-btn" 
        :class="{'disabled-btn': isFriend === 1 || isFriend === 0}"
        @click="handleFriendAction"
      >
        {{ getFriendActionText() }}
      </button>
      <button class="secondary-btn" @click="navigateToChat" v-if="isFriend === 1">发送消息</button>
    </view>
    
    <!-- 用户统计信息 -->
    <view class="user-stats">
      <view class="stat-item" @click="viewPosts">
        <text class="stat-value">{{ userStats.postCount || 0 }}</text>
        <text class="stat-label">帖子</text>
      </view>
      <view class="stat-item" @click="viewFollowers">
        <text class="stat-value">{{ userStats.followerCount || 0 }}</text>
        <text class="stat-label">粉丝</text>
      </view>
      <view class="stat-item" @click="viewFollowing">
        <text class="stat-value">{{ userStats.followingCount || 0 }}</text>
        <text class="stat-label">关注</text>
      </view>
    </view>
    
    <!-- 用户帖子列表 -->
    <view class="user-posts">
      <view class="section-title">
        <text>最近发布</text>
        <text class="view-all" @click="viewPosts">查看全部</text>
      </view>
      
      <view class="post-list" v-if="posts.length > 0">
        <view class="post-item" v-for="(post, index) in posts" :key="index" @click="viewPostDetail(post.id)">
          <view class="post-content">
            <text class="post-title">{{ post.title }}</text>
            <text class="post-summary">{{ post.content }}</text>
          </view>
          <view class="post-meta">
            <text class="post-time">{{ formatTime(post.createTime) }}</text>
            <view class="post-stats">
              <view class="stat">
                <uni-icons type="eye" size="16" color="#999999"></uni-icons>
                <text>{{ post.viewCount || 0 }}</text>
              </view>
              <view class="stat">
                <uni-icons type="chat" size="16" color="#999999"></uni-icons>
                <text>{{ post.commentCount || 0 }}</text>
              </view>
              <view class="stat">
                <uni-icons type="heart" size="16" color="#999999"></uni-icons>
                <text>{{ post.likeCount || 0 }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
      
      <view class="empty-posts" v-else>
        <image src="/static/images/empty-content.png" mode="aspectFit"></image>
        <text>该用户暂未发布任何内容</text>
      </view>
    </view>
    
    <!-- 好友申请弹窗 -->
    <uni-popup ref="friendRequestPopup" type="bottom">
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">添加好友</text>
          <view class="popup-close" @click="closeFriendRequestPopup">
            <uni-icons type="closeempty" size="18" color="#999"></uni-icons>
          </view>
        </view>
        <view class="popup-body">
          <view class="form-item">
            <text class="form-label">备注</text>
            <input class="form-input" v-model="friendRemark" placeholder="请输入备注名" />
          </view>
          <view class="form-item">
            <text class="form-label">验证信息</text>
            <input class="form-input" v-model="friendRequestMsg" placeholder="请输入验证信息" />
          </view>
        </view>
        <view class="popup-footer">
          <button class="primary-btn" @click="sendFriendRequest">发送</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { mapState } from 'vuex';
import { getUserInfo, getUserPosts, followUser, unfollowUser } from '@/api/forum'; 
import { sendFriendRequest, checkIsFriend, addForumFriend } from '@/api/chat/index';

export default {
  data() {
    return {
      userId: '', // 查看的用户ID
      userInfo: {}, // 用户信息
      userStats: {
        postCount: 0,
        followerCount: 0,
        followingCount: 0
      },
      posts: [], // 用户帖子
      isFollowing: false, // 是否已关注
      isFriend: -1, // -1 未查询，0 申请中，1 已是好友，2 不是好友
      isPending: false, // 是否有好友申请待处理
      friendRemark: '',
      friendRequestMsg: '',
    };
  },
  computed: {
    ...mapState(['user']),
    currentUserId() {
      return this.user?.userId || '';
    }
  },
  onLoad(options) {
    if (options.id) {
      this.userId = options.id;
      this.loadUserInfo();
      this.loadUserPosts();
      this.checkRelationship();
    } else {
      uni.showToast({
        title: '用户ID不存在',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  methods: {
    // 返回上一页
    goBack() {
      uni.navigateBack();
    },
    
    // 加载用户信息
    loadUserInfo() {
      uni.showLoading({
        title: '加载中...'
      });
      
      getUserInfo(this.userId)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.userInfo = res.data;
            // 可能需要从其他接口获取统计信息
            this.loadUserStats();
            this.checkFriendRelation();
          } else {
            uni.showToast({
              title: '获取用户信息失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取用户信息失败:', err);
          uni.showToast({
            title: '获取用户信息失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
    // 加载用户统计信息
    loadUserStats() {
      // 这里可能需要单独调用一个API获取用户的统计信息
      // 或者统计信息已包含在用户信息接口中
    },
    
    // 加载用户帖子
    loadUserPosts() {
      getUserPosts(this.userId, 1, 5)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.posts = res.data.records || [];
          }
        })
        .catch(err => {
          console.error('获取用户帖子失败:', err);
        });
    },
    
    // 检查关系状态
    checkRelationship() {
      // 检查是否已经是好友
      checkIsFriend(this.userId)
        .then(res => {
          if (res && res.isFriend !== undefined) {
            this.isFriend = res.isFriend ? 1 : 2;
          } else {
            this.isFriend = 2;
          }
        })
        .catch(err => {
          console.error('检查好友关系失败:', err);
          this.isFriend = 2;
        });
    },
    
    // 检查是否是好友关系
    checkFriendRelation() {
      if (!this.userId || !this.profileData.userId || this.userId === this.profileData.userId) {
        return;
      }
      
      checkIsFriend(this.userId, this.profileData.userId).then(response => {
        if (response && response.code === 200) {
          this.isFriend = response.data ? 1 : 2;
        } else {
          this.isFriend = 2;
        }
      }).catch(error => {
        console.error('检查好友关系失败', error);
        this.isFriend = 2;
      });
    },
    
    // 处理好友操作
    handleFriendAction() {
      if (this.isFriend === 1) {
        // 已是好友，不做操作
        return;
      } else if (this.isFriend === 0) {
        // 申请中，不做操作
        return;
      } else if (this.isFriend === 2) {
        // 不是好友，显示添加好友弹窗
        this.friendRemark = this.profileData.nickname || this.profileData.username;
        this.friendRequestMsg = `你好，我是${this.userInfo.nickname || this.userInfo.username}`;
        this.$refs.friendRequestPopup.open();
      }
    },
    
    // 关闭好友申请弹窗
    closeFriendRequestPopup() {
      this.$refs.friendRequestPopup.close();
    },
    
    // 发送好友申请
    sendFriendRequest() {
      if (!this.currentUserId || !this.userId) {
        uni.showToast({
          title: '用户ID不能为空',
          icon: 'none'
        });
        return;
      }
      
      if (!this.friendRequestMsg.trim()) {
        uni.showToast({
          title: '请输入验证信息',
          icon: 'none'
        });
        return;
      }
      
      uni.showLoading({
        title: '发送中...'
      });
      
      // 存储当前用户ID到本地存储，确保请求头中包含X-User-Id
      uni.setStorageSync('userId', this.currentUserId);
      
      console.log('发送好友申请，当前用户ID:', this.currentUserId, '目标ID:', this.userId, '消息:', this.friendRequestMsg);
      
      // 这里改为使用addForumFriend，因为是从论坛场景发起的好友申请
      addForumFriend(this.userId, this.friendRequestMsg).then(response => {
        uni.hideLoading();
        console.log('好友申请响应:', response);
        
        if (response && response.success) {
          uni.showToast({
            title: '好友申请已发送',
            icon: 'success'
          });
          
          // 更新好友状态为申请中
          this.isFriend = 0;
          
          // 关闭弹窗
          this.closeFriendRequestPopup();
        } else {
          uni.showToast({
            title: response?.message || '发送失败，请重试',
            icon: 'none'
          });
        }
      }).catch(error => {
        uni.hideLoading();
        console.error('发送好友申请失败', error);
        uni.showToast({
          title: '发送好友申请失败',
          icon: 'none'
        });
      });
    },
    
    // 获取好友操作按钮文本
    getFriendActionText() {
      if (this.isFriend === 1) {
        return '已是好友';
      } else if (this.isFriend === 0) {
        return '申请中';
      } else {
        return '添加好友';
      }
    },
    
    // 导航到聊天页面
    navigateToChat() {
      if (this.isFriend !== 1) {
        uni.showToast({
          title: '请先添加对方为好友',
          icon: 'none'
        });
        return;
      }
      
      uni.navigateTo({
        url: `/pages/chat/chat?friendId=${this.userId}&nickname=${encodeURIComponent(this.userInfo.nickName || this.userInfo.userName || '未知用户')}`
      });
    },
    
    // 切换关注状态
    toggleFollow() {
      if (!this.currentUserId) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        return;
      }
      
      uni.showLoading({
        title: this.isFollowing ? '取消关注...' : '关注中...'
      });
      
      const action = this.isFollowing ? unfollowUser : followUser;
      
      action(this.userId)
        .then(res => {
          if (res.code === 200) {
            this.isFollowing = !this.isFollowing;
            
            // 更新关注者数量
            if (this.isFollowing) {
              this.userStats.followerCount++;
            } else {
              this.userStats.followerCount = Math.max(0, this.userStats.followerCount - 1);
            }
            
            uni.showToast({
              title: this.isFollowing ? '已关注' : '已取消关注',
              icon: 'success'
            });
          } else {
            uni.showToast({
              title: res.msg || '操作失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('关注操作失败:', err);
          uni.showToast({
            title: '操作失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
    // 查看用户帖子
    viewPosts() {
      uni.navigateTo({
        url: `/pages/forum/user-posts?id=${this.userId}`
      });
    },
    
    // 查看用户粉丝
    viewFollowers() {
      uni.navigateTo({
        url: `/pages/forum/user-followers?id=${this.userId}`
      });
    },
    
    // 查看用户关注的人
    viewFollowing() {
      uni.navigateTo({
        url: `/pages/forum/user-following?id=${this.userId}`
      });
    },
    
    // 查看帖子详情
    viewPostDetail(postId) {
      uni.navigateTo({
        url: `/pages/forum/post-detail?id=${postId}`
      });
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      
      const date = new Date(time);
      const now = new Date();
      const diff = now - date;
      
      // 小于1分钟
      if (diff < 60 * 1000) {
        return '刚刚';
      }
      
      // 小于1小时
      if (diff < 60 * 60 * 1000) {
        const minutes = Math.floor(diff / (60 * 1000));
        return `${minutes}分钟前`;
      }
      
      // 小于1天
      if (diff < 24 * 60 * 60 * 1000) {
        const hours = Math.floor(diff / (60 * 60 * 1000));
        return `${hours}小时前`;
      }
      
      // 小于1周
      if (diff < 7 * 24 * 60 * 60 * 1000) {
        const days = Math.floor(diff / (24 * 60 * 60 * 1000));
        return `${days}天前`;
      }
      
      // 大于1周
      return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
    }
  }
};
</script>

<style lang="scss">
.user-profile-container {
  background-color: #f5f5f5;
  min-height: 100vh;
  
  .header {
    display: flex;
    align-items: center;
    height: 90rpx;
    padding: 0 30rpx;
    background-color: #ffffff;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
    position: relative;
    
    .back-btn {
      padding: 20rpx;
      margin-left: -20rpx;
    }
    
    .title {
      flex: 1;
      text-align: center;
      font-size: 36rpx;
      font-weight: 500;
    }
  }
  
  .user-info {
    display: flex;
    padding: 40rpx 30rpx;
    background-color: #ffffff;
    
    .avatar {
      width: 160rpx;
      height: 160rpx;
      border-radius: 50%;
      margin-right: 30rpx;
      box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.1);
    }
    
    .info-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      
      .username {
        font-size: 40rpx;
        font-weight: 600;
        margin-bottom: 10rpx;
      }
      
      .user-id {
        font-size: 26rpx;
        color: #999999;
        margin-bottom: 20rpx;
      }
      
      .bio {
        font-size: 28rpx;
        color: #666666;
        line-height: 1.5;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
      }
    }
  }
  
  .action-btns {
    display: flex;
    padding: 15px;
    justify-content: space-between;
  }
  
  .primary-btn {
    flex: 1;
    height: 40px;
    background-color: #07c160;
    color: #fff;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
  }
  
  .secondary-btn {
    flex: 1;
    height: 40px;
    background-color: #f8f8f8;
    color: #333;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    border: 1px solid #ddd;
    margin-left: 10px;
  }
  
  .disabled-btn {
    background-color: #ccc;
  }
  
  .popup-content {
    background-color: #fff;
    border-radius: 10px 10px 0 0;
    padding: 15px;
  }
  
  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
  }
  
  .popup-title {
    font-size: 16px;
    font-weight: 500;
    color: #333;
  }
  
  .popup-body {
    margin-bottom: 15px;
  }
  
  .form-item {
    margin-bottom: 15px;
  }
  
  .form-label {
    font-size: 14px;
    color: #333;
    margin-bottom: 5px;
    display: block;
  }
  
  .form-input {
    width: 100%;
    height: 40px;
    border: 1px solid #eee;
    border-radius: 5px;
    padding: 0 10px;
    font-size: 14px;
  }
  
  .popup-footer {
    padding-top: 10px;
  }
  
  .user-stats {
    display: flex;
    justify-content: space-around;
    padding: 30rpx 0;
    background-color: #ffffff;
    margin-bottom: 20rpx;
    
    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .stat-value {
        font-size: 36rpx;
        font-weight: 600;
        margin-bottom: 6rpx;
      }
      
      .stat-label {
        font-size: 26rpx;
        color: #999999;
      }
    }
  }
  
  .user-posts {
    background-color: #ffffff;
    padding: 30rpx;
    
    .section-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30rpx;
      font-size: 32rpx;
      font-weight: 600;
      
      .view-all {
        font-size: 26rpx;
        color: #4095E5;
        font-weight: normal;
      }
    }
    
    .post-list {
      .post-item {
        padding: 20rpx 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .post-content {
          margin-bottom: 15rpx;
          
          .post-title {
            font-size: 32rpx;
            font-weight: 500;
            margin-bottom: 10rpx;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 1;
            overflow: hidden;
          }
          
          .post-summary {
            font-size: 28rpx;
            color: #666666;
            line-height: 1.5;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
            overflow: hidden;
          }
        }
        
        .post-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .post-time {
            font-size: 24rpx;
            color: #999999;
          }
          
          .post-stats {
            display: flex;
            
            .stat {
              display: flex;
              align-items: center;
              margin-left: 20rpx;
              
              uni-icons {
                margin-right: 5rpx;
              }
              
              text {
                font-size: 24rpx;
                color: #999999;
              }
            }
          }
        }
      }
    }
    
    .empty-posts {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 60rpx 0;
      
      image {
        width: 200rpx;
        height: 200rpx;
        margin-bottom: 20rpx;
      }
      
      text {
        font-size: 28rpx;
        color: #999999;
      }
    }
  }
}
</style> 