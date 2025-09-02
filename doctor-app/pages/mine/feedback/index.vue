<template>
	<view>
		<view class="grid-body">
			<view v-if="list.length > 0" class="doctor" v-for="(item,index) in list" :key="index">
				<view class="content">
					<view class="info">
						<view  >
							<h2 style="padding: 16rpx;font-size: 32rpx;font-weight: 600;"> {{item.feedbackType==1?"系统问题":"其他问题"}} </h2>
							
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 反馈内容 </view>
							<view style="flex: 4;text-align: right;">{{item.feedbackContent}} </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 反馈时间 </view>
							<view style="flex: 4;text-align: right;">{{item.feedbackTime}} </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 联系号码 </view>
							<view style="flex: 4;text-align: right;"> {{item.phone}}</view>
						</view>
						
						<view class="ietm">
							<view style="flex: 1;"> 系统回复 </view>
							<view style="flex: 4;text-align: right;">{{item.feedbackReply == null ? "等待回复中":item.feedbackReply}} </view>
						</view>
					</view>
				</view>
			</view>
			<view v-else>
				<view style="text-align: center;margin-top: 200rpx;">暂无记录 </view>
			</view>
			<view  style="position:fixed;z-index:1;top:1000rpx;left: 600rpx;background-color: #00aaff;
			width: 80rpx;height: 80rpx;border-radius: 50%;line-height: 80rpx;text-align: center;color: #FFF;" @click="feedback">反馈</view>
		</view>
	</view>
</template>

<script>
	import {
		getFeedbackList
	} from '@/api/maincode/feedback'
	export default {
		data() {
			return {
				list: [],
				noData: false,
				queryParams: {
					pageNum: 1,
					pageSize: 10
				},
			}
		},
		methods: {
			getList() {
				getFeedbackList(this.queryParams).then(res => {
					console.log(this.queryParams);
					if (res.rows == null || res.rows.length < this.queryParams.pageSize) {
						this.noData = true;
						uni.showToast({
							title: '已加载全部数据',
							duration: 2000
						});
					}
					this.list.push(...res.rows)
				})
			},
			feedback() {
				uni.navigateTo({
					url: "./feedback"
				});
			}
		},

		onReachBottom() {
			if (!this.noData) {
				this.queryParams.pageNum++;
				this.getList(); // 获取的数据列表
			}
		},

		onLoad: function() {
			this.getList();
		},
	}
</script>

<style lang="scss">
	page {
		display: flex;
		flex-direction: column;
		box-sizing: border-box;
		background-color: #F1f1f1;
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
		border: 1rpx #CCC solid;
		background: #fff;
	}

	.doctor .ima {
		flex: 0.6;
		padding: 20rpx;
		width: 160rpx;
		display: grid;
		place-items: center;
	}

	.doctor .content {
		flex: 4;
		padding: 20rpx
	}

	.doctor .content .name {
		font-size: 28rpx;
		font-weight: 600;
		margin: 5rpx 0;
	}

	.doctor .content .info {
		font-size: 24rpx;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 3;
		overflow: hidden;
		text-overflow: ellipsis;
		line-height: 1.5em;
	}

	.doctor .content .info .ietm {
		display: flex;
		padding: 16rpx;
		border-bottom: #CCC 1px solid;
	}
</style>