<template>
	<view class="work-container">
		<view style="padding: 20rpx; background: #f1f1f1;">
			<view style="font-size: 32rpx;margin: auto;width: 280rpx;padding: 14rpx;border: 1rpx #ccc solid;">
				<picker mode="date" :value="queryParams.time" @change="bindDateChange">
					<view style="text-align: center;"><uni-icons type="calendar-filled" style="margin-right: 6rpx;"
							size="16"></uni-icons>日期 {{queryParams.time}}</view>
				</picker>
			</view>
		</view>
		<!-- 列表 -->
		<uni-section title="治疗屋列表" type="line"></uni-section>
		<view class="grid-body">
			<view v-if="list.length > 0">
				<view class="doctor" v-for="(item, index) in list" :key="index" @click="toDoctor(item.scheduledId,item.doctorId)">
					<view class="ima">
						<image style="width: 100%;height: 100%;" src="../../static/room.png"></image>
					</view>
					<view class="content">
						<view class="name">{{item.doctorName}}</view>
						<view class="info">
							{{item.remark==null?"暂无":item.remark}}
						</view>
					</view>
				</view>
			</view>
			<view v-else>
				<view style="text-align: center;margin-top: 200rpx;">暂无医生可预约 </view>
			</view>
			
		</view>
	</view> 
</template>

<script>
	import {
		getRoomScheduledList
	} from '@/api/maincode/scheduled'
	export default {

		data() {
			return {
				noData: false,
				list: [],
				queryParams: {
					pageNum: 1,
					pageSize: 10,
					time: 0
				},
			}
		},
		methods: {
			getList() {
				getRoomScheduledList(this.queryParams).then(res => {
					if(res.rows == null || res.rows.length < this.queryParams.pageSize ){
						this.noData = true;
						if(this.queryParams.pageNum !=1 ){
							uni.showToast({   title: '已加载全部数据', duration: 2000 });
						}
					}
					this.list.push(...res.rows)
				})
			},
			bindDateChange: function(e) {
				this.queryParams.time = e.detail.value
				this.list = []
				this.getList()
			},
			toDoctor(scheduledId,doctorId) { 
				uni.navigateTo({ 
					url: "../mine/doctor/index?scheduledId=" + scheduledId + "&doctorId="+doctorId
				});

			},
		},
		onReachBottom() {
			if (!this.noData) {
				this.queryParams.pageNum++;
				this.getNews(); // 获取的数据列表
			}
		},
		onLoad: function() {
			//想要默认日期？是
			this.queryParams.time = new Date().toISOString().slice(0, 10);
			this.getList();
		}
	}
</script>

<style lang="scss">
	page {
		display: flex;
		flex-direction: column;
		box-sizing: border-box;
		background-color: #f1f1ff;
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
		background: #FFF;
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