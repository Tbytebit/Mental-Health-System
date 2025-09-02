<template>
	<view class="container">
		<!-- 顶部导航 -->
		<view class="header">
			<text class="title">心理情绪评估</text>
			<view class="tabs">
				<view class="tab" :class="{ active: activeTab === 'scales' }" @click="switchTab('scales')">量表测试</view>
				<view class="tab" :class="{ active: activeTab === 'records' }" @click="switchTab('records')">历史记录</view>
			</view>
		</view>

		<!-- 量表列表 -->
		<view v-if="activeTab === 'scales'" class="content">
			<view class="empty-tip" v-if="scaleList.length === 0">
				<text>暂无可用的心理量表</text>
			</view>
			<view v-else class="scale-list">
				<view class="scale-item" v-for="item in scaleList" :key="item.scaleId" @click="goToScaleDetail(item)">
					<view class="scale-info">
						<text class="scale-name">{{ item.scaleName }}</text>
						<text class="scale-desc">{{ item.scaleDescription }}</text>
					</view>
					<view class="scale-type">
						<text :class="'scale-tag type' + item.scaleType">{{ getScaleTypeName(item.scaleType) }}</text>
						<uni-icons type="right" size="18" color="#999"></uni-icons>
					</view>
				</view>
			</view>
		</view>

		<!-- 历史记录 -->
		<view v-if="activeTab === 'records'" class="content">
			<view class="empty-tip" v-if="recordList.length === 0">
				<text>暂无历史测评记录</text>
			</view>
			<view v-else class="record-list">
				<view class="record-item" v-for="item in recordList" :key="item.recordId" @click="goToRecordDetail(item)">
					<view class="record-info">
						<text class="record-name">{{ item.scaleName || '未知量表' }}</text>
						<text class="record-time">{{ formatDate(item.completeTime) }}</text>
					</view>
					<view class="record-result">
						<text class="record-score">{{ item.totalScore }}分</text>
						<text class="record-eval" :class="getResultTagClass(item.evaluationResult)">{{ getShortResult(item.evaluationResult) }}</text>
						<uni-icons type="right" size="18" color="#999"></uni-icons>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { listAllScales, getUserScaleRecords } from '@/api/mood/scale'

export default {
	data() {
		return {
			activeTab: 'scales',
			scaleList: [],
			recordList: [],
			loading: false
		}
	},
	onLoad(options) {
		// 检查是否有标签页参数
		if (options && options.tab) {
			this.activeTab = options.tab
			if (options.tab === 'records') {
				this.getRecordList()
			} else {
				this.getScaleList()
			}
		} else {
			this.getScaleList()
		}
	},
	methods: {
		// 切换标签页
		switchTab(tab) {
			this.activeTab = tab
			if (tab === 'scales') {
				this.getScaleList()
			} else {
				this.getRecordList()
			}
		},
		
		// 获取量表列表
		async getScaleList() {
			try {
				this.loading = true
				const res = await listAllScales()
				if (res.code === 200) {
					this.scaleList = res.data || []
				} else {
					uni.showToast({
						title: '获取量表失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('获取量表失败', error)
				uni.showToast({
					title: '获取量表失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},
		
		// 获取记录列表
		async getRecordList() {
			try {
				this.loading = true
				const res = await getUserScaleRecords()
				if (res.code === 200) {
					this.recordList = res.data || []
					
					// 处理量表名称
					if (this.recordList.length > 0 && this.scaleList.length > 0) {
						this.recordList.forEach(record => {
							const scale = this.scaleList.find(s => s.scaleId === record.scaleId)
							if (scale) {
								record.scaleName = scale.scaleName
							}
						})
					}
				} else {
					uni.showToast({
						title: '获取记录失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('获取记录失败', error)
				uni.showToast({
					title: '获取记录失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},
		
		// 跳转到量表详情
		goToScaleDetail(scale) {
			uni.navigateTo({
				url: `/pages/mood/detail?id=${scale.scaleId}`
			})
		},
		
		// 跳转到记录详情
		goToRecordDetail(record) {
			uni.navigateTo({
				url: `/pages/mood/result?id=${record.recordId}`
			})
		},
		
		// 获取量表类型名称
		getScaleTypeName(type) {
			const types = {
				'1': '情绪评估',
				'2': '心理测试',
				'3': '状态检测'
			}
			return types[type] || '未知类型'
		},
		
		// 获取结果简短显示
		getShortResult(result) {
			if (!result) return '未知';
			
			if (result.includes('良好') || result.includes('健康') || result.includes('正常')) {
				return '正常';
			} else if (result.includes('轻度')) {
				return '轻度';
			} else if (result.includes('中度')) {
				return '中度';
			} else if (result.includes('严重')) {
				return '严重';
			}
			
			// 如果结果太长，截取前面一部分
			return result.length > 6 ? result.substring(0, 6) + '...' : result;
		},
		
		// 结果标签样式
		getResultTagClass(result) {
			if (!result) return '';
			
			if (result.includes('良好') || result.includes('健康') || result.includes('正常')) {
				return 'result-tag-normal';
			} else if (result.includes('轻度')) {
				return 'result-tag-mild';
			} else if (result.includes('中度')) {
				return 'result-tag-moderate';
			} else if (result.includes('严重')) {
				return 'result-tag-severe';
			}
			
			return '';
		},
		
		// 格式化日期
		formatDate(dateString) {
			if (!dateString) return '未知时间'
			const date = new Date(dateString)
			return `${date.getFullYear()}-${this.padZero(date.getMonth() + 1)}-${this.padZero(date.getDate())} ${this.padZero(date.getHours())}:${this.padZero(date.getMinutes())}`
		},
		
		padZero(num) {
			return num < 10 ? `0${num}` : num
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	padding: 20rpx;
	background-color: #f8f8f8;
	min-height: 100vh;
	
	.header {
		padding: 30rpx 20rpx;
		background-color: #ffffff;
		border-radius: 12rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		
		.title {
			font-size: 36rpx;
			font-weight: bold;
			margin-bottom: 30rpx;
			display: block;
		}
		
		.tabs {
			display: flex;
			border-bottom: 1rpx solid #eee;
			
			.tab {
				flex: 1;
				text-align: center;
				padding: 20rpx 0;
				font-size: 30rpx;
				color: #666;
				position: relative;
				
				&.active {
					color: #2979ff;
					font-weight: bold;
					
					&::after {
						content: '';
						position: absolute;
						bottom: -2rpx;
						left: 25%;
						width: 50%;
						height: 4rpx;
						background-color: #2979ff;
						border-radius: 2rpx;
					}
				}
			}
		}
	}
	
	.content {
		background-color: #ffffff;
		border-radius: 12rpx;
		padding: 20rpx;
		min-height: 400rpx;
		
		.empty-tip {
			display: flex;
			justify-content: center;
			align-items: center;
			min-height: 400rpx;
			color: #999;
			font-size: 28rpx;
		}
		
		.scale-list {
			.scale-item {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 30rpx 0;
				border-bottom: 1rpx solid #f5f5f5;
				
				&:last-child {
					border-bottom: none;
				}
				
				.scale-info {
					flex: 1;
					padding-right: 20rpx;
					
					.scale-name {
						font-size: 32rpx;
						font-weight: bold;
						color: #333;
						margin-bottom: 10rpx;
						display: block;
					}
					
					.scale-desc {
						font-size: 26rpx;
						color: #999;
						display: -webkit-box;
						-webkit-box-orient: vertical;
						-webkit-line-clamp: 2;
						overflow: hidden;
					}
				}
				
				.scale-type {
					display: flex;
					align-items: center;
					
					.scale-tag {
						font-size: 24rpx;
						padding: 6rpx 16rpx;
						border-radius: 20rpx;
						margin-right: 10rpx;
						
						&.type1 {
							background-color: #ecf5ff;
							color: #409eff;
						}
						
						&.type2 {
							background-color: #f0f9eb;
							color: #67c23a;
						}
						
						&.type3 {
							background-color: #fdf6ec;
							color: #e6a23c;
						}
					}
				}
			}
		}
		
		.record-list {
			.record-item {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 30rpx 0;
				border-bottom: 1rpx solid #f5f5f5;
				
				&:last-child {
					border-bottom: none;
				}
				
				.record-info {
					flex: 1;
					
					.record-name {
						font-size: 32rpx;
						font-weight: bold;
						color: #333;
						margin-bottom: 10rpx;
						display: block;
					}
					
					.record-time {
						font-size: 26rpx;
						color: #999;
					}
				}
				
				.record-result {
					display: flex;
					align-items: center;
					
					.record-score {
						font-size: 32rpx;
						font-weight: bold;
						color: #409eff;
						margin-right: 10rpx;
					}
					
					.record-eval {
						font-size: 24rpx;
						color: #fff;
						margin-right: 10rpx;
						padding: 2rpx 12rpx;
						border-radius: 12rpx;
						
						&.result-tag-normal {
							background-color: #67c23a;
						}
						
						&.result-tag-mild {
							background-color: #e6a23c;
						}
						
						&.result-tag-moderate {
							background-color: #f56c6c;
						}
						
						&.result-tag-severe {
							background-color: #dd3b3b;
							font-weight: bold;
						}
					}
				}
			}
		}
	}
}
</style>