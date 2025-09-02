<template>
	<view class="container">
		<!-- 测评结果卡片 -->
		<view class="result-card">
			<view class="card-header">
				<text class="title">测评结果</text>
				<text class="subtitle">{{ scale.scaleName }}</text>
			</view>
			<view class="card-content">
				<view class="result-section">
					<rich-text :nodes="htmlContent" class="markdown-content"></rich-text>
				</view>
			</view>
		</view>
		
		<!-- 测评时间 -->
		<view class="time-card">
			<text class="time-text">测评时间：{{ formatDate(record.completeTime) }}</text>
		</view>
		
		<!-- 底部按钮 -->
		<view class="button-group">
			<button class="btn primary" @click="goToScales">返回量表列表</button>
			<button class="btn secondary" @click="goToMyRecords">查看历史记录</button>
		</view>
	</view>
</template>

<script>
import { getScaleDetail, getRecordDetail } from '@/api/mood/scale'
import MarkdownIt from 'markdown-it'

export default {
	data() {
		return {
			recordId: null,
			score: 0,
			result: '',
			record: {},
			scale: {},
			loading: false,
			md: null
		}
	},
	computed: {
		htmlContent() {
			if (!this.record.evaluationResult) return '';
			
			if (!this.md) {
				// 初始化markdown解析器
				this.md = new MarkdownIt({
					html: true,
					breaks: true,
					linkify: true,
					typographer: true
				});
			}
			
			// 将markdown转换为HTML
			return this.md.render(this.record.evaluationResult);
		}
	},
	onLoad(options) {
		// 初始化markdown解析器
		this.md = new MarkdownIt({
			html: true,
			breaks: true,
			linkify: true,
			typographer: true
		});
		
		if (options.id) {
			this.recordId = options.id
			this.score = options.score || 0
			this.result = decodeURIComponent(options.result || '')
			
			// 检查是否是本地记录
			if (options.local === 'true') {
				this.loadLocalRecord(this.recordId)
			} else {
				this.getRecordInfo()
			}
		} else {
			uni.showToast({
				title: '参数错误',
				icon: 'none'
			})
			setTimeout(() => {
				uni.navigateBack()
			}, 1500)
		}
	},
	methods: {
		async getRecordInfo() {
			try {
				this.loading = true
				
				// 先验证recordId是否有效
				if (!this.recordId) {
					console.error('记录ID无效', this.recordId)
					
					// 如果有分数和结果，创建一个临时记录
					if (this.score && this.result) {
						this.record = {
							totalScore: this.score,
							evaluationResult: this.result,
							completeTime: new Date().toISOString()
						}
						
						// 使用URL参数构建一个基本的显示
						this.scale = {
							scaleName: '心理评估结果',
							scaleType: '1'
						}
						
						this.loading = false
						return
					}
					
					uni.showToast({
						title: '记录ID无效',
						icon: 'none'
					})
					
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
					return
				}
				
				const res = await getRecordDetail(this.recordId)
				if (res.code === 200) {
					this.record = res.data || {}
					
					// 如果URL中有传分数和结果，优先使用
					if (this.score) {
						this.record.totalScore = this.score
					}
					if (this.result) {
						this.record.evaluationResult = this.result
					}
					
					// 获取量表信息
					if (this.record.scaleId) {
						this.getScaleInfo(this.record.scaleId)
					}
				} else {
					// 请求失败，使用URL参数构建一个基本的显示
					if (this.score && this.result) {
						this.record = {
							totalScore: this.score,
							evaluationResult: this.result,
							completeTime: new Date().toISOString()
						}
						
						this.scale = {
							scaleName: '心理评估结果',
							scaleType: '1'
						}
					} else {
						uni.showToast({
							title: '获取记录信息失败',
							icon: 'none'
						})
						
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					}
				}
			} catch (error) {
				console.error('获取记录信息失败', error)
				
				// 出错时，如果有分数和结果，也创建一个临时记录
				if (this.score && this.result) {
					this.record = {
						totalScore: this.score,
						evaluationResult: this.result,
						completeTime: new Date().toISOString()
					}
					
					this.scale = {
						scaleName: '心理评估结果',
						scaleType: '1'
					}
				} else {
					uni.showToast({
						title: '获取记录信息失败',
						icon: 'none'
					})
				}
			} finally {
				this.loading = false
			}
		},
		
		async getScaleInfo(scaleId) {
			try {
				const res = await getScaleDetail(scaleId)
				if (res.code === 200) {
					this.scale = res.data || {}
				}
			} catch (error) {
				console.error('获取量表信息失败', error)
			}
		},
		
		// 跳转到量表列表
		goToScales() {
			uni.redirectTo({
				url: '/pages/mood/index'
			})
		},
		
		// 查看历史记录
		goToMyRecords() {
			uni.redirectTo({
				url: '/pages/mood/index?tab=records'
			})
		},
		
		// 格式化日期
		formatDate(dateString) {
			if (!dateString) return '未知时间'
			const date = new Date(dateString)
			return `${date.getFullYear()}-${this.padZero(date.getMonth() + 1)}-${this.padZero(date.getDate())} ${this.padZero(date.getHours())}:${this.padZero(date.getMinutes())}`
		},
		
		padZero(num) {
			return num < 10 ? `0${num}` : num
		},
		
		// 加载本地记录
		loadLocalRecord(recordId) {
			try {
				const localRecords = uni.getStorageSync('local_mood_records') || []
				const record = localRecords.find(r => r.recordId === recordId)
				
				if (record) {
					this.record = record
					// 如果URL中有传分数和结果，优先使用
					if (this.score) {
						this.record.totalScore = this.score
					}
					if (this.result) {
						this.record.evaluationResult = this.result
					}
					
					// 设置量表信息
					this.scale = {
						scaleName: record.scaleName || '心理情绪量表',
						scaleType: record.scaleType || '1'
					}
				} else {
					uni.showToast({
						title: '记录不存在',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('获取本地记录失败', error)
				uni.showToast({
					title: '获取记录失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	padding: 20rpx;
	background-color: #f8f8f8;
	min-height: 100vh;
}

.result-card {
	background-color: #ffffff;
	border-radius: 12rpx;
	margin-bottom: 20rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	
	.card-header {
		padding: 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
		
		.title {
			font-size: 32rpx;
			font-weight: bold;
			color: #333;
			display: block;
		}
		
		.subtitle {
			font-size: 26rpx;
			color: #666;
			margin-top: 10rpx;
			display: block;
		}
	}
	
	.card-content {
		padding: 30rpx;
	}
}

.result-section {
	display: flex;
	flex-direction: column;
}

.markdown-content {
	font-size: 28rpx;
	color: #333;
	line-height: 1.8;
	text-align: justify;
	
	:deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
		font-weight: bold;
		margin: 20rpx 0 10rpx;
	}
	
	:deep(h1) {
		font-size: 36rpx;
	}
	
	:deep(h2) {
		font-size: 32rpx;
	}
	
	:deep(h3) {
		font-size: 30rpx;
	}
	
	:deep(p) {
		margin-bottom: 16rpx;
	}
	
	:deep(ul), :deep(ol) {
		padding-left: 40rpx;
		margin: 16rpx 0;
	}
	
	:deep(li) {
		margin-bottom: 8rpx;
	}
	
	:deep(a) {
		color: #409eff;
		text-decoration: underline;
	}
	
	:deep(blockquote) {
		border-left: 8rpx solid #f0f0f0;
		padding: 10rpx 20rpx;
		color: #666;
		margin: 16rpx 0;
	}
	
	:deep(code) {
		background-color: #f5f5f5;
		padding: 4rpx 8rpx;
		border-radius: 4rpx;
		font-family: monospace;
	}
	
	:deep(pre) {
		background-color: #f5f5f5;
		padding: 16rpx;
		border-radius: 8rpx;
		overflow-x: auto;
		margin: 16rpx 0;
		
		:deep(code) {
			background-color: transparent;
			padding: 0;
		}
	}
	
	:deep(table) {
		width: 100%;
		border-collapse: collapse;
		margin: 16rpx 0;
	}
	
	:deep(th), :deep(td) {
		border: 1rpx solid #ddd;
		padding: 12rpx;
		text-align: left;
	}
	
	:deep(th) {
		background-color: #f5f5f5;
		font-weight: bold;
	}
	
	:deep(strong) {
		font-weight: bold;
	}
	
	:deep(em) {
		font-style: italic;
	}
}

.time-card {
	background-color: #ffffff;
	border-radius: 12rpx;
	padding: 20rpx;
	margin-bottom: 40rpx;
	text-align: center;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	
	.time-text {
		font-size: 26rpx;
		color: #999;
	}
}

.button-group {
	display: flex;
	justify-content: space-between;
	margin-top: 20rpx;
	padding: 0 20rpx;
	
	.btn {
		width: 45%;
		height: 80rpx;
		border-radius: 40rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 28rpx;
		
		&.primary {
			background-color: #409eff;
			color: #ffffff;
		}
		
		&.secondary {
			background-color: #ffffff;
			color: #409eff;
			border: 1px solid #409eff;
		}
	}
}
</style>