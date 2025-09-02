<template>
	<view class="work-container">
		<view class="grid-body">
			<view class="doctor">
				<view class="ima">
					<image style="width: 100%;height: 100%;" src="../../../static/doctor.png"></image>
				</view>
				<view class="content">
					<view class="name">{{user.nickName}}</view>
					<view class="info">
						{{user.remark==null?"暂无":user.remark}}
					</view>
				</view>
			</view>
		</view>
		<!-- 列表 -->
		<!-- <uni-section title="预约列表" type="line"></uni-section> -->
		<view style="border-radius: 8rpx;background: #f1f1f1;margin: 28rpx;padding: 10rpx 0;background: #FFF;">
			<view style="display: flex;margin: 20rpx;border-bottom: #CCC solid 1px;padding: 20rpx 0;"
				v-for="(item, index) in list" :key="index">
				<view style="font-size: 28rpx;flex: 1;text-align: center;"> {{item.startTime.slice(-8, -3)}} ~
					{{item.endTime.slice(-8, -3)}} </view>
				<view style="margin-left: 40rpx;color: seagreen;flex: 1;text-align: center;"> 余号：{{item.remainder}}
				</view>
				<view style="margin-left: 40rpx;color: seagreen;flex: 1;text-align: center;" v-if="item.remainder>0"
				@click="app(item.id,item.doctorId)"> 立即预约 </view>
				<view style="margin-left: 40rpx;color: seagreen;flex: 1;text-align: center;" v-else> 不可预约 </view>
			</view>

		</view>

	</view>
</template>

<script>
	import {
		getDoctorScheduledList
	} from '@/api/maincode/doctor'
	import {
		getUserInfo
	} from '@/api/system/user'
	export default {

		data() {
			return {
				list: [],
				user: {},
				queryParams: {
					scheduledId: ""
				},
			}
		},
		methods: {
			getList() {
				getDoctorScheduledList(this.queryParams).then(res => {
					this.list = res.data
				})
			},

			bindDateChange: function(e) {
				this.queryParams.time = e.detail.value
				this.getList()
			},
			getUser(userId) {
				getUserInfo(userId, 'appointment').then(res => {
					this.user = res.data
				})
			},
			app(id, doctorId) {
				uni.navigateTo({ 
					url: "../../mine/appointment/index?id=" + id + "&doctorId=" + doctorId
				});
			}
		},
		onLoad: function(option) {
			this.queryParams.scheduledId = option.scheduledId
			this.queryParams.doctorId = option.doctorId
			this.getUser(option.doctorId)
			this.getList();
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
		padding: 20rpx;
		width: 160rpx;
		height: 200rpx;
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
</style>