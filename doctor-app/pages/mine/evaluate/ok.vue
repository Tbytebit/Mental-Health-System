<template>
  <view class="success-page">
    <view class="header">评价成功</view>
    
    <image class="icon" src="/static/images/success.png" mode="widthFix"></image>

    <view class="content"> 
	  感谢您的评论
    </view>

    <button class="btn" type="primary" @tap="redirectToHome">返回首页</button>
  </view>
</template>

<script>
export default {
  methods: {
    redirectToHome() {
      uni.switchTab({
        url: '../../index' // 替换为你的首页路径
      });
    }
  }
};
</script>

<style scoped>
.success-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  padding: 30px;
}

.header {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.icon {
  width: 200px;
  height: 200px;
  margin-bottom: 30px;
}

.content {
  font-size: 16px;
  color: #666;
  text-align: center;
  line-height: 1.5;
  margin-bottom: 30px;
}

.btn {
  width: 80%;
  margin-top: 30px;
}
</style>
