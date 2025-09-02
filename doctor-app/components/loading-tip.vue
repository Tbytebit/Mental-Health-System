<template>
	<view class="loading-container" :class="{'loading-fullscreen': fullscreen}">
		<view class="loading-content">
			<view class="loading-icon">
				<view class="loading-circle"></view>
			</view>
			<text class="loading-text">{{text || '加载中...'}}</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'LoadingTip',
	props: {
		text: {
			type: String,
			default: ''
		},
		fullscreen: {
			type: Boolean,
			default: false
		}
	}
}
</script>

<style lang="scss" scoped>
.loading-container {
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 20rpx 0;
	
	&.loading-fullscreen {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(255, 255, 255, 0.8);
		z-index: 9999;
	}
}

.loading-content {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.loading-icon {
	width: 60rpx;
	height: 60rpx;
	position: relative;
	margin-bottom: 10rpx;
}

.loading-circle {
	width: 100%;
	height: 100%;
	border: 4rpx solid #f5f5f5;
	border-top-color: #3B7AFF;
	border-radius: 50%;
	animation: loading-rotate 0.9s linear infinite;
}

.loading-text {
	font-size: 24rpx;
	color: #999;
}

@keyframes loading-rotate {
	0% {
		transform: rotate(0deg);
	}
	100% {
		transform: rotate(360deg);
	}
}
</style> 