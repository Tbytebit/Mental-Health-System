<template>
	<view>
		<view class="grid-body">
			<view v-if="list.length > 0" class="doctor" v-for="(item, index) in list" :key="index">
				<view class="content">

					<view class="info">
						<view class="ietm">
							<view style="flex: 1;"> 医生名称 </view>
							<view style="flex: 4;text-align: right;">{{item.doctorName}} </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 预约日期 </view>
							<view style="flex: 4;text-align: right;">{{item.appointmentDate}} </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 就诊时间 </view>
							<view style="flex: 4;text-align: right;">
								{{item.appointmentTime.slice(-28,-23)}}-{{item.appointmentTime.slice(-8,-3)}}
							</view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 患者姓名 </view>
							<view style="flex: 4;text-align: right;">{{item.trueName}} </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 联系号码 </view>
							<view style="flex: 4;text-align: right;">{{item.phone}} </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 当前状态 </view>
							<view v-if="item.appointmentStatus==1" style="flex: 1;text-align: right;"> 预约成果 </view>
							<view v-else-if="item.appointmentStatus==2" style="flex: 4;text-align: right;"> 已取消 </view>
							<view v-else-if="item.appointmentStatus==3" style="flex: 4;text-align: right;"> 已就诊 </view>
							<view v-else-if="item.appointmentStatus==4" style="flex: 4;text-align: right;"> 未就诊 </view>
							<view v-else-if="item.appointmentStatus==5" style="flex: 4;text-align: right;"> 已评论 </view>
							<view v-else style="flex: 4text-align: right;"> 错误状态 </view>
						</view>
						<view class="ietm">
							<view style="flex: 1;"> 你的评论 </view>
							<view style="flex: 4;text-align: right;color: #03a9f4;" 
								  v-if="item.appointmentStatus == '3'" @click="evaluate(item.doctorId,item.appointmentId)">立即评论</view>
							<view style="flex: 4;text-align: left;" v-else>你的评分：{{item.score}} 分，{{item.comment}} </view>
						</view>
					</view>
				</view>
			</view>
			<view v-else>
				<view style="text-align: center;margin-top: 200rpx;">暂无预约记录 </view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getUserCommentAppointment
	} from '@/api/maincode/appointment.js'
	export default {

		data() {
			return {
				list: [],
				noData : false,
				queryParams: {
					pageNum: 1,
					pageSize: 10
				},
			}
		},
		methods: {
			evaluate(doctorId,appointmentId) {
				uni.navigateTo({
					url: "./evaluate?doctorId=" + doctorId +"&appointmentId=" + appointmentId,
				});
			},
			getList() {
				getUserCommentAppointment(this.queryParams).then(res => {
					if(res.rows == null || res.rows.length < this.queryParams.pageSize ){
						this.noData = true;
						uni.showToast({   title: '已加载全部数据', duration: 2000 });
					}
					this.list.push(...res.rows)
				})
			},
			lookCode(code) {
				uni.showModal({
					title: '就诊码',
					content: code,
					showCancel: false, //参数设置为false，就可以取消掉模态提示框中的“取消”按钮。
					confirmColor: '#007aff', //设置“确定”按钮的颜色 
					success: function(res) {}
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
		}
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