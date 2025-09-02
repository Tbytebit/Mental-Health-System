<template>
  <view class="help-container">
    <view v-for="(item, findex) in list" :key="findex" :title="item.title" class="list-title">
      <view class="text-title">
        <view :class="item.icon"></view>{{ item.title }}
      </view>
      <view class="childList">
        <view v-for="(child, zindex) in item.childList" :key="zindex" class="question" hover-class="hover"
          @click="handleText(child)">
          <view class="text-item">{{ child.noticeTitle }}</view>
          <view class="line" v-if="zindex !== item.childList.length - 1"></view>
        </view>
      </view>
    </view>
  </view>

</template>

<script>
		import { getNoticeList } from '@/api/maincode/notice'
  export default {
    data() {
      return {
        list: [{
            icon: 'iconfont ',
            title: '公告列表',
            childList: []
          }
        ]
      }
    },
    methods: {
      handleText(item) { 
        this.$tab.navigateTo(`/pages/common/textview/index?title=${item.noticeTitle}&content=${item.noticeContent}`)
      },
	  getNotice(){
		  getNoticeList().then(res =>{ 
			 this.list[0].childList = res.data
		  })
	  },
	  newsDetails(item){
		  this.$tab.navigateTo(`/pages/common/textview/index?title=${item.title}&content=${item.content}`)
	  }
    },
	onLoad: function() {
		this.getNotice();
	}
  }
</script>

<style lang="scss" scoped>
  page {
    background-color: #f8f8f8;
  }

  .help-container {
    margin-bottom: 100rpx;
    padding: 30rpx;
  }

  .list-title {
    margin-bottom: 30rpx;
  }

  .childList {
    background: #ffffff;
    box-shadow: 0px 0px 10rpx rgba(193, 193, 193, 0.2);
    border-radius: 16rpx;
    margin-top: 10rpx;
  }

  .line {
    width: 100%;
    height: 1rpx;
    background-color: #F5F5F5;
  }

  .text-title {
    color: #303133;
    font-size: 32rpx;
    font-weight: bold;
    margin-left: 10rpx;

    .iconfont {
      font-size: 16px;
      margin-right: 10rpx;
    }
  }

  .text-item {
    font-size: 28rpx;
    padding: 24rpx;
  }

  .question {
    color: #606266;
    font-size: 28rpx;
  }
</style>
