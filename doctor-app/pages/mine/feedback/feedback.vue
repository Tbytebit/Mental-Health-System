<template>
	<view class="work-container">
		<view>
			<view style="background-color: #FFF;margin: 20rpx;padding: 20rpx;">
				<form @submit="submit">
					<view style="margin: 20rpx;">
						<view style="margin: 10rpx 0;">反馈类型</view>
						<uni-data-select v-model="data.feedbackType" :localdata="range"></uni-data-select>
					</view>
					<view style="margin: 20rpx;">
						<view  style="margin: 10rpx 0;">联系号码</view>
						<input style="padding: 10rpx; width: 100%;height: 60rpx;line-height: 60rpx;border: #f1f1f1 1px solid;border-radius: 6rpx;" v-model="data.phone"  placeholder="请输入"
							required />
					</view>
					<view style="margin: 20rpx;">
						<view style="margin: 10rpx 0;">反馈问题</view>
						<textarea style="padding: 10rpx; width: 100%;border: #f1f1f1 1px solid;border-radius: 6rpx;" v-model="data.feedbackContent"
							placeholder="请输入" required />
					</view>
					<view>
						<button form-type="submit" type="primary" style="margin: 20rpx;">提交</button>
					</view>
				</form>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		addFeedback
	} from '@/api/maincode/feedback'
	export default {

		data() {
			return {
				range: [{ value: 1, text: "系统问题" }, { value: 2, text: "其他问题"}], 
				data: {feedbackType:1}
			}
		},
		methods: {
			init() {
				this.data.feedbackType = 1
			},
			submit() {
				if (this.data.feedbackContent == undefined) {
					uni.showModal({
						title: '错误信息',
						content: "请输入反馈内容",
						showCancel: false
					})
					return
				}
				if (this.data.feedbackContent.length > 200) {
					uni.showModal({
						title: '错误信息',
						content: "反馈内容不能超200字。",
						showCancel: false
					})
					return
				}
				if (this.data.phone == undefined || this.data.phone.length != 11) {
					uni.showModal({
						title: '错误信息',
						content: "请输入11位号码",
						showCancel: false
					})
					return
				}
				if (this.data.phone == undefined || this.data.phone.length != 11) {
					uni.showModal({
						title: '错误信息',
						content: "请输入11位号码",
						showCancel: false
					})
					return
				}
				addFeedback(this.data).then(res => {
					uni.showToast({
						title: '提交成功',
						duration: 2000
					}); 
					uni.navigateTo({
						url: "./index"
					});
				})
			}
		},
		onLoad: function(option) {
			this.init()
		}
	}
</script>

<style lang="scss">
	page {
		display: flex;
		flex-direction: column;
		box-sizing: border-box;
		background-color: #f1f1f1;
		min-height: 100%;
		height: auto;
	}

	view {
		font-size: 14px;
		line-height: inherit;
	}

	.doctor {
		display: flex;
		margin: 20rpx;
		background-color: #FFF;
		border-radius: 6rpx;
	}

	.doctor .ima {
		flex: 1;
		padding: 30rpx;
		width: 160rpx;
		height: 220rpx;
	}

	.doctor .content {
		flex: 3;
		padding: 20rpx
	}

	.doctor .content .name {
		font-size: 28rpx;
		font-weight: 600;
	}

	.doctor .content .info {
		font-size: 24rpx;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 4;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.title {}
</style>