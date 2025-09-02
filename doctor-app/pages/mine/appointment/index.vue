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
		<uni-section title="预约填写" type="line"></uni-section>
		<view>
			<view style="background-color: #FFF;margin: 20rpx;padding: 20rpx;">
				<form @submit="submit">
					<view style="margin: 20rpx;">
						<view>姓名</view>
						<input style="border-bottom: #CCC 1px solid;" v-model="data.trueName" placeholder="请输入姓名" required />
					</view>
					<view style="margin: 20rpx;">
						<view>号码</view>
						<input style="border-bottom: #CCC 1px solid;"  v-model="data.phone" placeholder="请输入号码" required/>
					</view>
					<view style="margin: 20rpx;">
						<view>医生</view>
						<view style="border-bottom: #CCC 1px solid;">{{user.nickName}} </view>
					</view>
					<view style="margin: 20rpx;">
						<view>治疗时间</view>
						<view style="border-bottom: #CCC 1px solid;">{{doctorScheduled.startTime}} - {{doctorScheduled.endTime}} </view>
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
		getUserInfo
	} from '@/api/system/user'
	import {
		getDoctorScheduled
	} from '@/api/maincode/doctor'
	import {
		userAppointment
	} from '@/api/maincode/appointment'
	export default {

		data() {
			return {
				user: {},
				doctorScheduled: {
					phone:""
				},
				data: {}
			}
		},
		methods: {
			init() {
				getUserInfo(this.data.doctorId, 'appointment').then(res => {
					this.user = res.data
				})
				getDoctorScheduled(this.data.doctorScheduledId).then(res => {
					this.doctorScheduled = res.data
				})
			},
			submit(){
				if(this.data.trueName == undefined){
					uni.showModal({ title: '错误信息', content: "请输入姓名",  showCancel: false  })
					return
				}
				if(this.data.phone == undefined || this.data.phone.length !=11){
					uni.showModal({ title: '错误信息', content: "请输入11位号码",  showCancel: false  })
					return
				}  
				
				this.data.doctorName = this.user.nickName
				this.data.appointmentDate = this.doctorScheduled.time
				this.data.doctorScheduledId = this.doctorScheduled.id 
				this.data.appointmentTime = this.doctorScheduled.startTime + "-" + this.doctorScheduled.endTime
				userAppointment(this.data).then(res=>{
					uni.navigateTo({
						url: "./ok"
					});
				})
			}
		},
		onLoad: function(option) {
			this.data.doctorScheduledId = option.id
			this.data.doctorId = option.doctorId
			this.init()
		}
	}
</script>

<style lang="scss">
	page {
		display: flex;
		flex-direction: column;
		box-sizing: border-box;
		background-color: #Ff1f1;
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