<template>
  <view class="edit-remark-container">
    <!-- 顶部标题栏 -->
    <view class="header">
      <view class="header-left" @click="goBack">
        <uni-icons type="back" size="24" color="#333"></uni-icons>
      </view>
      <view class="header-title">编辑备注</view>
      <view class="header-right"></view>
    </view>
    
    <!-- 编辑区域 -->
    <view class="edit-area">
      <view class="form-item">
        <view class="form-label">备注名</view>
        <input 
          type="text" 
          v-model="remark" 
          placeholder="请输入备注名" 
          maxlength="30"
          class="form-input"
          focus
        />
      </view>
      
      <button class="save-button" @click="saveRemark">保存</button>
    </view>
  </view>
</template>

<script>
import { updateFriendRemark } from '@/api/chat/index';

export default {
  data() {
    return {
      userId: null,
      friendId: null,
      remark: '',
      originalRemark: ''
    };
  },
  
  onLoad(options) {
    this.userId = options.userId || uni.getStorageSync('userId');
    this.friendId = options.friendId;
    this.remark = options.remark || '';
    this.originalRemark = options.remark || '';
    
    if (!this.userId || !this.friendId) {
      uni.showToast({
        title: '参数错误',
        icon: 'none'
      });
      
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  
  methods: {
    /**
     * 保存备注
     */
    async saveRemark() {
      // 如果没有修改，直接返回
      if (this.remark === this.originalRemark) {
        uni.navigateBack();
        return;
      }
      
      try {
        uni.showLoading({
          title: '保存中...',
          mask: true
        });
        
        console.log('保存备注参数:', {
          userId: this.userId,
          friendId: this.friendId,
          remark: this.remark
        });
        
        const res = await updateFriendRemark(this.userId, this.friendId, this.remark);
        
        console.log('保存备注响应:', res);
        
        if (res.code === 200) {
          uni.showToast({
            title: '备注已更新',
            icon: 'success'
          });
          
          // 触发好友列表刷新
          uni.$emit('refreshFriendList');
          
          // 延迟返回
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
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
      }
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
.edit-remark-container {
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

.edit-area {
  padding: 20px;
}

.form-item {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.form-label {
  font-size: 16px;
  color: #333333;
  margin-bottom: 10px;
}

.form-input {
  width: 100%;
  height: 40px;
  font-size: 16px;
  border-bottom: 1px solid #eaeaea;
}

.save-button {
  margin-top: 30px;
  background-color: #07c160;
  color: #ffffff;
  border-radius: 5px;
  font-size: 16px;
  padding: 10px;
}
</style> 