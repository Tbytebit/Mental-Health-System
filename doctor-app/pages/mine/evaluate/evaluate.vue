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
		<view>
			<view style="background-color: #f1f1f1;margin: 20rpx;padding: 20rpx;">
				<view>
					<view style="margin: 20rpx;">
						<view>姓名</view>
						<view style="border-bottom: #CCC 1px solid;">{{appointment.trueName}} </view>
					</view>
					<view style="margin: 20rpx;">
						<view>号码</view>
						<view style="border-bottom: #CCC 1px solid;">{{appointment.phone}} </view>
					</view>
					<view style="margin: 20rpx;">
						<view>医生</view>
						<view style="border-bottom: #CCC 1px solid;">{{user.nickName}} </view>
					</view>
					<view style="margin: 20rpx;">
						<view>就诊时间</view>
						<view style="border-bottom: #CCC 1px solid;">{{appointment.appointmentTime}} </view>
					</view>
				</view> 
			</view>
			
			<view style="background-color: #f1f1f1;margin: 20rpx;padding: 20rpx;">
				<view>
					<view style="margin: 20rpx;display: flex;">
						<view style="flex: 1;">评分</view>
						<view><uni-rate v-model="appointment.score" @change="onChange" /></view>
					</view>
					<view style="margin: 20rpx;display: flex;">
						<view style="flex: 1;">评价</view>
						
						<view style="flex: 4;">
							<textarea class="textarea" placeholder="请填写你的评价" maxlength="200"  v-model="appointment.comment" style="background: #f1f1f1;" />
						</view>
					</view> 
					<view>
						<button  @click="submit()" type="primary" style="margin: 20rpx;">提交</button>
					</view>
				</view>
			</view>
			
		</view>
	</view>
</template>

<script>
	import {
		getUserInfo
	} from '@/api/system/user' 
	import {
		getAppointment,
		userComment
	} from '@/api/maincode/appointment'
	export default {

		data() {
			return {
				appointmentId: '',
				doctorId: '',
				user: {},
				appointment: {
					score:5,
					comment:''
				},
			}
		},
		methods: {
			init() {
				getUserInfo(this.doctorId).then(res => {
					this.user = res.data
				})
				getAppointment(this.appointmentId).then(res => {
					this.appointment = res.data
					console.log(this.appointment);
				})
			},
			onChange(e) {
				console.log('rate发生改变:' + JSON.stringify(e))
				this.appointment.score = e.value
			},
			submit(){
				if(this.appointment.comment == undefined){
					uni.showModal({ title: '错误信息', content: "请输入你的评价",  showCancel: false  })
					return
				}
				userComment(this.appointment).then(res=>{
					uni.navigateTo({
						url: "./ok"
					});
				})
			}
		},
		onLoad: function(option) {
			this.doctorId = option.doctorId
			this.appointmentId = option.appointmentId
			this.init()
		}
	}
</script>

<style lang="scss">
	page {
		display: flex;
		flex-direction: column;
		box-sizing: border-box;
		background-color: #fff;
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
		background-color: #f1f1f1;
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