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
		<uni-section padding title="评论" type="line"></uni-section>
		<view>
			<view style="background-color: #FFF;margin: 20rpx;padding: 20rpx;">
				<form @submit="submit">
					
					<uni-section title="分数" type="line" padding>
						<uni-rate v-model="data.score" />
					</uni-section>
					<uni-section title="评论内容" type="line" padding>
						<textarea style="border-bottom: #CCC 1px solid;width: 100%; background: #F1F1F1; padding: 16rpx;" v-model="data.comment" placeholder="请输入" required />
					</uni-section>
					<view>
						<button form-type="submit" type="primary" style="margin: 20rpx;">提交评论</button>
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
		userComment
	} from '@/api/maincode/appointment'
	export default {

		data() {
			return {
				user: {}, 
				data: {
					score: 5
				}
			}
		},
		methods: {
			init(doctorId) {
				getUserInfo(doctorId).then(res => {
					this.user = res.data
				})
				 
			},
			submit(){
				if(this.data.comment == undefined){
					uni.showModal({ title: '错误信息', content: "请输入评论内容",  showCancel: false  })
					return
				}
				if(this.data.comment.length > 150){
					uni.showModal({ title: '错误信息', content: "请输入评论内容字数小于150",  showCancel: false  })
					return
				}
				userComment(this.data).then(res=>{
					uni.navigateBack({ delta: 1 }); // 回退一步
				})
			}
		},
		onLoad: function(option) {
			console.log("id",option.id);
			this.data.appointmentId = option.id
			this.init(option.doctorId)
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