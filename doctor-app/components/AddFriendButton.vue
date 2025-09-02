<template>
  <view class="add-friend-btn" @tap="handleAddFriend" v-if="showButton">
    <text>+好友</text>
  </view>
</template>

<script>
import { mapGetters } from 'vuex';
import { checkIsFriend, sendFriendRequest } from '@/api/chat/index';
import { applyAddFriend } from '@/utils/chat';

export default {
  name: 'AddFriendButton',
  props: {
    targetId: {
      type: [String, Number],
      required: true
    },
    targetName: {
      type: String,
      default: '用户'
    },
    context: {
      type: String,
      default: '论坛'
    }
  },
  data() {
    return {
      isFriend: false,
      isChecking: false,
      showButton: false
    }
  },
  computed: {
    ...mapGetters(['userId', 'token'])
  },
  created() {
    this.checkIfFriend();
  },
  methods: {
    // 检查是否已经是好友
    checkIfFriend() {
      // 如果是自己，不显示按钮
      if (this.targetId == this.userId) {
        this.showButton = false;
        return;
      }
      
      if (this.isChecking) return;
      this.isChecking = true;
      
      checkIsFriend(this.userId, this.targetId).then(res => {
        if (res.code === 200 && res.data) {
          this.isFriend = res.data;
          // 如果不是好友，显示添加按钮
          this.showButton = !this.isFriend;
        } else {
          this.isFriend = false;
          this.showButton = true;
        }
      }).catch(err => {
        console.error('检查好友关系失败', err);
        this.isFriend = false;
        this.showButton = true;
      }).finally(() => {
        this.isChecking = false;
      });
    },
    
    // 处理添加好友
    handleAddFriend() {
      if (!this.token) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        return;
      }
      
      // 再次检查是否是好友，避免状态不一致
      checkIsFriend(this.userId, this.targetId).then(res => {
        if (res.code === 200 && res.data) {
          uni.showToast({
            title: '已经是好友了',
            icon: 'none'
          });
          this.isFriend = true;
          this.showButton = false;
          return;
        }
        
        // 显示确认对话框
        uni.showModal({
          title: '添加好友',
          content: `确定要添加"${this.targetName}"为好友吗？`,
          success: (res) => {
            if (res.confirm) {
              this.sendRequest();
            }
          }
        });
      });
    },
    
    // 发送好友请求
    sendRequest() {
      const remark = `我在${this.context}看到了您，想加您为好友`;
      
      // 使用applyAddFriend函数发送好友申请
      applyAddFriend(this.userId, this.targetId, remark).then(res => {
        if (res && res.code === 200) {
          uni.showToast({
            title: '好友请求已发送',
            icon: 'success'
          });
          // 隐藏按钮
          this.showButton = false;
        } else {
          uni.showToast({
            title: res.msg || '发送请求失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('发送好友请求失败', err);
        uni.showToast({
          title: '发送好友请求失败',
          icon: 'none'
        });
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.add-friend-btn {
  padding: 4rpx 20rpx;
  background-color: #f0f0f0;
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  
  text {
    font-size: 24rpx;
    color: #3e92ff;
  }
  
  &:active {
    opacity: 0.7;
  }
}
</style> 