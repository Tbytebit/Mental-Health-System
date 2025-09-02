<template>
	<view>
		<view class="grid-body">
			<view v-if="list.length > 0" class="doctor" v-for="(item,index) in list" :key="index">
				<view class="content">
					<view class="info">
						<view class="ietm">
							<view style="flex: 1;"> 活动名称 </view>
							<view style="flex: 3;text-align: right;">{{item.name}} </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 开始时间 </view>
							<view style="flex: 3;text-align: right;">{{item.startTime}} </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 结束时间 </view>
							<view style="flex: 3;text-align: right;"> {{item.endTime}}</view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 活动地址 </view>
							<view style="flex: 3;text-align: right;">{{item.location}} </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 联系号码 </view>
							<view style="flex: 3;text-align: right;">{{item.phone}} </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 取消活动 </view>
							<view style="flex: 3;text-align: right;color: red;" @click="cancel(item.id)">取消活动 </view>
						</view>
					</view>
				</view>
			</view>
			<view v-else>
				<view style="text-align: center;margin-top: 200rpx;">暂无记录 </view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getReservationList,deleteActivityReservation
	} from '@/api/maincode/reservation'
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
				getReservationList(this.queryParams).then(res => {
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
			cancel(id) {
				uni.showModal({
					title: '提示',
					content: '您确定要取消本次活动吗？',
					success: (res) => {
						if (res.confirm) {
							deleteActivityReservation(id).then(res=>{
								this.list = [],
								this.noData = false,
								this.queryParams =  { pageNum: 1, pageSize: 10 }, 
								this.getList()
							})
						}  
					}
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
		flex: 3;
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
		padding: 8rpx;
	}
</style>