<template>
	<view class="prototype-container">
		<view class="header">
			<text class="title">心理日记模块原型</text>
		</view>
		
		<swiper class="prototype-swiper" :indicator-dots="true" :autoplay="false" :interval="3000" :duration="500">
			<!-- 日记列表原型图 -->
			<swiper-item>
				<view class="swiper-item">
					<view class="prototype-frame">
						<!-- 模拟手机顶栏 -->
						<view class="phone-header">
							<text class="phone-time">14:25</text>
							<view class="phone-status">
								<view class="status-icon"></view>
								<view class="status-icon"></view>
								<view class="status-icon"></view>
							</view>
						</view>
						
						<!-- 应用导航栏 -->
						<view class="app-navbar">
							<text class="navbar-title">心理日记</text>
						</view>
						
						<!-- 日记列表内容 -->
						<view class="diary-container">
							<view class="diary-header">
								<text class="diary-title">我的心理日记</text>
								<view class="add-diary-btn">
									<text class="add-icon">+</text>
								</view>
							</view>
							
							<view class="diary-list">
								<!-- 日记项目1 -->
								<view class="diary-item">
									<view class="diary-date">
										<text class="day">15</text>
										<text class="month">10月</text>
									</view>
									<view class="diary-content">
										<view class="diary-item-title">今天的心理咨询很有收获</view>
										<view class="diary-item-preview">今天和心理医生讨论了我最近的焦虑问题，医生建议我尝试正念冥想来缓解压力。我回家后就试着练习了15分钟，感觉确实有些效果...</view>
										<view class="diary-item-footer">
											<text class="diary-time">14:30</text>
											<view class="diary-mood">
												<view class="mood-happy"></view>
											</view>
										</view>
									</view>
								</view>
								
								<!-- 日记项目2 -->
								<view class="diary-item">
									<view class="diary-date">
										<text class="day">12</text>
										<text class="month">10月</text>
									</view>
									<view class="diary-content">
										<view class="diary-item-title">工作压力好大</view>
										<view class="diary-item-preview">这周的工作任务特别多，感觉压力很大。尤其是今天下午的会议，我的提案被否决了，心情非常低落...</view>
										<view class="diary-item-footer">
											<text class="diary-time">19:45</text>
											<view class="diary-mood">
												<view class="mood-sad"></view>
											</view>
										</view>
									</view>
								</view>
								
								<!-- 日记项目3 -->
								<view class="diary-item">
									<view class="diary-date">
										<text class="day">08</text>
										<text class="month">10月</text>
									</view>
									<view class="diary-content">
										<view class="diary-item-title">学习了一套新的放松技巧</view>
										<view class="diary-item-preview">今天在线上课程中学习了一套新的放松技巧，主要是通过渐进式肌肉放松来缓解身体紧张...</view>
										<view class="diary-item-footer">
											<text class="diary-time">21:20</text>
											<view class="diary-mood">
												<view class="mood-neutral"></view>
											</view>
										</view>
									</view>
								</view>
							</view>
						</view>
						
						<!-- 底部导航栏 -->
						<view class="app-tabbar">
							<view class="tabbar-item">
								<view class="tabbar-icon home-icon"></view>
								<text class="tabbar-text">首页</text>
							</view>
							<view class="tabbar-item">
								<view class="tabbar-icon appointment-icon"></view>
								<text class="tabbar-text">预约</text>
							</view>
							<view class="tabbar-item active">
								<view class="tabbar-icon mine-icon"></view>
								<text class="tabbar-text">我的</text>
							</view>
						</view>
					</view>
					<text class="prototype-label">日记列表页面</text>
				</view>
			</swiper-item>
			
			<!-- 日记详情/编辑原型图 -->
			<swiper-item>
				<view class="swiper-item">
					<view class="prototype-frame">
						<!-- 模拟手机顶栏 -->
						<view class="phone-header">
							<text class="phone-time">14:26</text>
							<view class="phone-status">
								<view class="status-icon"></view>
								<view class="status-icon"></view>
								<view class="status-icon"></view>
							</view>
						</view>
						
						<!-- 应用导航栏 -->
						<view class="app-navbar">
							<text class="navbar-title">日记详情</text>
						</view>
						
						<!-- 日记详情内容 -->
						<view class="diary-detail-container">
							<!-- 日记标题 -->
							<view class="diary-title-box">
								<view class="diary-title-input">今天的心理咨询很有收获</view>
							</view>
							
							<!-- 日期和心情 -->
							<view class="diary-date-box">
								<text class="diary-date">2023年10月15日 星期日</text>
								
								<!-- 心情展示 -->
								<view class="selected-mood">
									<text class="mood-label">今日心情：</text>
									<view class="mood-happy-small"></view>
									<text class="mood-text">开心</text>
								</view>
							</view>
							
							<!-- 日记内容 -->
							<view class="diary-content-box">
								<view class="diary-content">
									今天和心理医生讨论了我最近的焦虑问题，医生建议我尝试正念冥想来缓解压力。我回家后就试着练习了15分钟，感觉确实有些效果，呼吸变得平稳了许多。希望坚持下去能有更好的改善。

医生还推荐了一本关于认知行为疗法的书，我打算这周末去买来看看。我觉得了解更多的心理学知识对自我调节会很有帮助。
								</view>
							</view>
							
							<!-- 底部按钮 -->
							<view class="button-area">
								<view class="diary-btn diary-edit">编辑</view>
								<view class="diary-btn diary-delete">删除</view>
							</view>
						</view>
					</view>
					<text class="prototype-label">日记详情页面</text>
				</view>
			</swiper-item>
			
			<!-- 编辑日记原型图 -->
			<swiper-item>
				<view class="swiper-item">
					<view class="prototype-frame">
						<!-- 模拟手机顶栏 -->
						<view class="phone-header">
							<text class="phone-time">14:27</text>
							<view class="phone-status">
								<view class="status-icon"></view>
								<view class="status-icon"></view>
								<view class="status-icon"></view>
							</view>
						</view>
						
						<!-- 应用导航栏 -->
						<view class="app-navbar">
							<text class="navbar-title">编辑日记</text>
						</view>
						
						<!-- 日记编辑内容 -->
						<view class="diary-detail-container">
							<!-- 日记标题 -->
							<view class="diary-title-box">
								<view class="diary-title-input diary-input-active">今天的心理咨询很有收获</view>
							</view>
							
							<!-- 日期和心情 -->
							<view class="diary-date-box">
								<text class="diary-date">2023年10月15日 星期日</text>
								
								<!-- 心情选择器 -->
								<view class="mood-selector">
									<text class="mood-label">今日心情：</text>
									<view class="mood-options">
										<view class="mood-option mood-selected">
											<view class="mood-happy-small"></view>
											<text class="mood-text">开心</text>
										</view>
										<view class="mood-option">
											<view class="mood-neutral-small"></view>
											<text class="mood-text">平静</text>
										</view>
										<view class="mood-option">
											<view class="mood-sad-small"></view>
											<text class="mood-text">难过</text>
										</view>
										<view class="mood-option">
											<view class="mood-angry-small"></view>
											<text class="mood-text">生气</text>
										</view>
										<view class="mood-option">
											<view class="mood-anxious-small"></view>
											<text class="mood-text">焦虑</text>
										</view>
									</view>
								</view>
							</view>
							
							<!-- 日记内容 -->
							<view class="diary-content-box">
								<view class="diary-content diary-input-active">
									今天和心理医生讨论了我最近的焦虑问题，医生建议我尝试正念冥想来缓解压力。我回家后就试着练习了15分钟，感觉确实有些效果，呼吸变得平稳了许多。希望坚持下去能有更好的改善。

医生还推荐了一本关于认知行为疗法的书，我打算这周末去买来看看。我觉得了解更多的心理学知识对自我调节会很有帮助。
								</view>
							</view>
							
							<!-- 底部按钮 -->
							<view class="button-area">
								<view class="diary-btn diary-cancel">取消</view>
								<view class="diary-btn diary-save">保存</view>
							</view>
						</view>
					</view>
					<text class="prototype-label">编辑日记页面</text>
				</view>
			</swiper-item>
			
			<!-- 空状态原型图 -->
			<swiper-item>
				<view class="swiper-item">
					<view class="prototype-frame">
						<!-- 模拟手机顶栏 -->
						<view class="phone-header">
							<text class="phone-time">14:28</text>
							<view class="phone-status">
								<view class="status-icon"></view>
								<view class="status-icon"></view>
								<view class="status-icon"></view>
							</view>
						</view>
						
						<!-- 应用导航栏 -->
						<view class="app-navbar">
							<text class="navbar-title">心理日记</text>
						</view>
						
						<!-- 空状态内容 -->
						<view class="diary-container">
							<view class="diary-header">
								<text class="diary-title">我的心理日记</text>
								<view class="add-diary-btn">
									<text class="add-icon">+</text>
								</view>
							</view>
							
							<view class="empty-state">
								<view class="empty-image"></view>
								<text class="empty-text">还没有日记，开始记录您的心情吧</text>
								<view class="start-btn">开始写日记</view>
							</view>
						</view>
						
						<!-- 底部导航栏 -->
						<view class="app-tabbar">
							<view class="tabbar-item">
								<view class="tabbar-icon home-icon"></view>
								<text class="tabbar-text">首页</text>
							</view>
							<view class="tabbar-item">
								<view class="tabbar-icon appointment-icon"></view>
								<text class="tabbar-text">预约</text>
							</view>
							<view class="tabbar-item active">
								<view class="tabbar-icon mine-icon"></view>
								<text class="tabbar-text">我的</text>
							</view>
						</view>
					</view>
					<text class="prototype-label">空状态页面</text>
				</view>
			</swiper-item>
		</swiper>
		
		<view class="description">
			<text class="desc-title">设计说明</text>
			<text class="desc-text">此高保真原型展示了"心理日记"模块的主要界面设计，包括日记列表、详情查看、编辑界面和空状态界面。设计采用现代简约风格，突出内容展示，使用柔和的色彩和圆角元素，提升整体视觉体验。</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {}
		},
		methods: {
			
		}
	}
</script>

<style>
	.prototype-container {
		padding: 30rpx;
		background-color: #f8f8f8;
		min-height: 100vh;
	}
	
	.header {
		margin-bottom: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1px solid #e0e0e0;
	}
	
	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}
	
	.prototype-swiper {
		height: 1100rpx;
		background-color: #fff;
		border-radius: 16rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
	}
	
	.swiper-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 30rpx;
	}
	
	.prototype-frame {
		width: 600rpx;
		height: 900rpx;
		background-color: #fff;
		border-radius: 40rpx;
		overflow: hidden;
		box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.15);
		position: relative;
		border: 2rpx solid #e0e0e0;
	}
	
	.prototype-label {
		font-size: 28rpx;
		color: #666;
		margin-top: 20rpx;
		text-align: center;
	}
	
	.phone-header {
		height: 60rpx;
		background-color: #f5f5f5;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0 30rpx;
	}
	
	.phone-time {
		font-size: 24rpx;
		color: #333;
		font-weight: 500;
	}
	
	.phone-status {
		display: flex;
		align-items: center;
	}
	
	.status-icon {
		width: 24rpx;
		height: 24rpx;
		background-color: #666;
		margin-left: 10rpx;
		border-radius: 50%;
	}
	
	.app-navbar {
		height: 80rpx;
		background-color: #fff;
		display: flex;
		justify-content: center;
		align-items: center;
		border-bottom: 1rpx solid #e5e5e5;
	}
	
	.navbar-title {
		font-size: 32rpx;
		color: #333;
		font-weight: 500;
	}
	
	.app-tabbar {
		height: 100rpx;
		background-color: #fff;
		display: flex;
		justify-content: space-around;
		align-items: center;
		border-top: 1rpx solid #e5e5e5;
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
	}
	
	.tabbar-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 10rpx 0;
	}
	
	.tabbar-icon {
		width: 50rpx;
		height: 50rpx;
		border-radius: 50%;
		margin-bottom: 6rpx;
	}
	
	.home-icon {
		background-color: #e0e0e0;
	}
	
	.appointment-icon {
		background-color: #e0e0e0;
	}
	
	.mine-icon {
		background-color: #4A7CFF;
	}
	
	.tabbar-text {
		font-size: 22rpx;
		color: #999;
	}
	
	.active .tabbar-text {
		color: #4A7CFF;
	}
	
	.diary-container {
		padding: 20rpx;
		height: 640rpx;
		overflow-y: auto;
	}
	
	.diary-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
	}
	
	.diary-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}
	
	.add-diary-btn {
		width: 60rpx;
		height: 60rpx;
		border-radius: 50%;
		background-color: #4A7CFF;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	
	.add-icon {
		color: #fff;
		font-size: 40rpx;
		font-weight: 300;
		line-height: 1;
	}
	
	.diary-list {
		
	}
	
	.diary-item {
		display: flex;
		background-color: #ffffff;
		border-radius: 12rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	}
	
	.diary-date {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		width: 80rpx;
		padding: 10rpx 0;
		background-color: #f0f7ff;
		border-radius: 8rpx;
		margin-right: 20rpx;
	}
	
	.day {
		font-size: 32rpx;
		font-weight: bold;
		color: #4A7CFF;
	}
	
	.month {
		font-size: 20rpx;
		color: #666;
		margin-top: 4rpx;
	}
	
	.diary-content {
		flex: 1;
	}
	
	.diary-item-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 8rpx;
		line-height: 1.4;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	
	.diary-item-preview {
		font-size: 22rpx;
		color: #666;
		line-height: 1.4;
		margin-bottom: 8rpx;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	
	.diary-item-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		font-size: 20rpx;
		color: #999;
	}
	
	.diary-time {
		
	}
	
	.diary-mood {
		
	}
	
	.mood-happy, .mood-happy-small {
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
		background-color: #FFD54F;
	}
	
	.mood-happy-small {
		width: 24rpx;
		height: 24rpx;
	}
	
	.mood-sad, .mood-sad-small {
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
		background-color: #90CAF9;
	}
	
	.mood-sad-small {
		width: 24rpx;
		height: 24rpx;
	}
	
	.mood-neutral, .mood-neutral-small {
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
		background-color: #A5D6A7;
	}
	
	.mood-neutral-small {
		width: 24rpx;
		height: 24rpx;
	}
	
	.mood-angry, .mood-angry-small {
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
		background-color: #EF5350;
	}
	
	.mood-angry-small {
		width: 24rpx;
		height: 24rpx;
	}
	
	.mood-anxious, .mood-anxious-small {
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
		background-color: #9575CD;
	}
	
	.mood-anxious-small {
		width: 24rpx;
		height: 24rpx;
	}
	
	.diary-detail-container {
		padding: 20rpx;
		height: 640rpx;
		overflow-y: auto;
	}
	
	.diary-title-box {
		margin-bottom: 20rpx;
	}
	
	.diary-title-input {
		font-size: 32rpx;
		font-weight: bold;
		padding: 10rpx 0;
		border-bottom: 1px solid #e5e5e5;
	}
	
	.diary-input-active {
		background-color: #f8f8f8;
		border-radius: 8rpx;
		padding: 10rpx;
	}
	
	.diary-date-box {
		display: flex;
		flex-direction: column;
		margin-bottom: 20rpx;
	}
	
	.diary-date {
		font-size: 22rpx;
		color: #999;
		margin-bottom: 10rpx;
	}
	
	.mood-selector {
		margin-top: 10rpx;
	}
	
	.mood-label {
		font-size: 24rpx;
		color: #666;
	}
	
	.selected-mood {
		display: flex;
		align-items: center;
		margin-top: 10rpx;
	}
	
	.mood-text {
		font-size: 20rpx;
		color: #666;
		margin-left: 8rpx;
	}
	
	.mood-options {
		display: flex;
		flex-wrap: wrap;
		margin-top: 10rpx;
	}
	
	.mood-option {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-right: 20rpx;
		margin-bottom: 10rpx;
		opacity: 0.6;
	}
	
	.mood-selected {
		opacity: 1;
	}
	
	.diary-content-box {
		margin-top: 20rpx;
		flex: 1;
	}
	
	.diary-content {
		width: 100%;
		font-size: 24rpx;
		color: #333;
		line-height: 1.6;
		padding: 10rpx;
		background-color: #f8f8f8;
		border-radius: 8rpx;
		box-sizing: border-box;
		min-height: 400rpx;
	}
	
	.button-area {
		display: flex;
		justify-content: space-between;
		margin-top: 30rpx;
		padding-bottom: 20rpx;
	}
	
	.diary-btn {
		width: 45%;
		height: 70rpx;
		line-height: 70rpx;
		border-radius: 35rpx;
		font-size: 24rpx;
		font-weight: 500;
		text-align: center;
	}
	
	.diary-cancel {
		background-color: #f5f5f5;
		color: #666;
	}
	
	.diary-save {
		background-color: #4A7CFF;
		color: #fff;
	}
	
	.diary-edit {
		background-color: #4A7CFF;
		color: #fff;
	}
	
	.diary-delete {
		background-color: #ff4a4a;
		color: #fff;
	}
	
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 80rpx 0;
	}
	
	.empty-image {
		width: 160rpx;
		height: 160rpx;
		background-color: #f0f7ff;
		border-radius: 16rpx;
		margin-bottom: 30rpx;
	}
	
	.empty-text {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 30rpx;
	}
	
	.start-btn {
		background-color: #4A7CFF;
		color: #fff;
		border-radius: 30rpx;
		font-size: 24rpx;
		padding: 10rpx 40rpx;
	}
	
	.description {
		margin-top: 30rpx;
		padding: 20rpx;
		background-color: #fff;
		border-radius: 16rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
	}
	
	.desc-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 16rpx;
		display: block;
	}
	
	.desc-text {
		font-size: 28rpx;
		color: #666;
		line-height: 1.6;
	}
</style>

export default {
  data() {
    return {
      selectedMood: 'happy',
      moodList: [
        { value: 'happy', label: '开心' },
        { value: 'neutral', label: '平静' },
        { value: 'sad', label: '难过' },
        { value: 'angry', label: '生气' },
        { value: 'anxious', label: '焦虑' }
      ]
    }
  },
  methods: {
    handleMoodSelect(value) {
      this.selectedMood = value
      uni.vibrateShort()
    }
  }
}

.mood-options {
  <view 
    v-for="(mood, index) in moodList" 
    :key="index"
    class="mood-option"
    :class="{ 'mood-selected': selectedMood === mood.value }"
    @click="handleMoodSelect(mood.value)"
  >
    <mood-icon-base64 
      :mood="mood.value" 
      :size="$mood-icon-size"
      class="mood-icon"
    />
    <text class="mood-text">{{ mood.label }}</text>
  </view>
</view>

.mood-option {
  @include transition-all;
  padding: 16rpx;
  border-radius: 24rpx;
  background: rgba($primary-color, 0.1);

  &.mood-selected {
    background: $primary-color;
    transform: scale($mood-icon-active-scale);

    .mood-text {
      color: white;
    }
  }
}

.mood-icon {
  margin-bottom: 8rpx;
}