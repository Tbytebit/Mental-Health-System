<template>
	<view class="history-container">
		<!-- 顶部标题区 -->
		<view class="page-header">
			<text class="page-title">测评历史</text>
		</view>
		
		<!-- 筛选选项 -->
		<view class="filter-section">
			<view class="filter-tabs">
				<view 
					class="filter-tab" 
					:class="{active: activeFilter === 'all'}"
					@click="setFilter('all')"
				>
					<text>全部</text>
				</view>
				<view 
					class="filter-tab" 
					:class="{active: activeFilter === 'depression'}"
					@click="setFilter('depression')"
				>
					<text>抑郁量表</text>
				</view>
				<view 
					class="filter-tab" 
					:class="{active: activeFilter === 'anxiety'}"
					@click="setFilter('anxiety')"
				>
					<text>焦虑量表</text>
				</view>
				<view 
					class="filter-tab" 
					:class="{active: activeFilter === 'other'}" 
					@click="setFilter('other')"
				>
					<text>其他</text>
				</view>
			</view>
		</view>
		
		<!-- 历史列表 -->
		<view class="history-list" v-if="mergedRecords.length > 0">
			<view 
				class="history-card" 
				v-for="(item, index) in mergedRecords" 
				:key="item.recordId || index"
				@click="goToDetail(item)"
			>
				<view class="history-main">
					<view class="history-icon" :class="getIconClass(item.evaluationResult)">
						<image :src="getIconSrc(item.evaluationResult)" mode="aspectFit"></image>
					</view>
					<view class="history-info">
						<text class="history-title">{{item.scaleName}}</text>
						<text class="history-date">{{formatDate(item.completeTime)}}</text>
						<text v-if="item.isLocal" class="status-tag">本地</text>
					</view>
					<view class="history-result" :class="getResultClass(item.evaluationResult)">
						<text>{{item.evaluationResult}}</text>
					</view>
				</view>
				<view class="history-detail">
					<view class="detail-item">
						<text class="detail-label">得分</text>
						<text class="detail-value">{{item.totalScore}}</text>
					</view>
					<view class="detail-action">
						<text>查看详情</text>
						<uni-icons type="right" size="14"></uni-icons>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<image class="empty-image" src="/static/images/empty-search.svg"></image>
			<text class="empty-text">暂无测评记录</text>
			<button class="start-test-btn" @click="startNewTest">开始测评</button>
		</view>
	</view>
</template>

<script>
import { getUserScaleRecords } from '@/api/mood/scale'

export default {
	data() {
		return {
			activeFilter: 'all',
			historyList: [
				{
					id: 101,
					scaleId: 2,
					scaleName: 'SAS焦虑自评量表',
					totalScore: 45,
					standardScore: 56,
					result: '轻度焦虑',
					completionDate: '2023-11-15T14:30:00',
					type: 'anxiety'
				},
				{
					id: 102,
					scaleId: 1,
					scaleName: 'SDS抑郁自评量表',
					totalScore: 38,
					standardScore: 48,
					result: '正常范围',
					completionDate: '2023-11-10T09:15:00',
					type: 'depression'
				},
				{
					id: 103,
					scaleId: 3,
					scaleName: 'SCL-90症状自评量表',
					totalScore: 120,
					standardScore: 65,
					result: '中度症状',
					completionDate: '2023-11-05T16:45:00',
					type: 'other'
				},
				{
					id: 104,
					scaleId: 1,
					scaleName: 'SDS抑郁自评量表',
					totalScore: 58,
					standardScore: 72,
					result: '中度抑郁',
					completionDate: '2023-10-25T10:20:00',
					type: 'depression'
				}
			],
			recordList: [],
			localRecords: [], // 本地记录
			loading: false
		}
	},
	computed: {
		filteredHistory() {
			if (this.activeFilter === 'all') {
				return this.historyList;
			} else {
				return this.historyList.filter(item => item.type === this.activeFilter);
			}
		},
		mergedRecords() {
			const all = [...this.recordList, ...this.localRecords]
			return all.sort((a, b) => {
				const dateA = new Date(a.completeTime)
				const dateB = new Date(b.completeTime)
				return dateB - dateA // 按时间倒序
			})
		}
	},
	methods: {
		setFilter(filter) {
			this.activeFilter = filter;
		},
		viewDetail(id) {
			uni.navigateTo({
				url: `/pages/mood/result?id=${id}`
			});
		},
		startNewTest() {
			uni.navigateTo({
				url: '/pages/mood/index'
			});
		},
		formatDate(dateStr) {
			const date = new Date(dateStr);
			const year = date.getFullYear();
			const month = (date.getMonth() + 1).toString().padStart(2, '0');
			const day = date.getDate().toString().padStart(2, '0');
			return `${year}-${month}-${day}`;
		},
		getResultClass(result) {
			if (result.includes('正常')) {
				return 'result-normal';
			} else if (result.includes('轻度')) {
				return 'result-mild';
			} else if (result.includes('中度')) {
				return 'result-moderate';
			} else {
				return 'result-severe';
			}
		},
		getIconClass(result) {
			if (result.includes('正常')) {
				return 'icon-normal';
			} else if (result.includes('轻度')) {
				return 'icon-mild';
			} else if (result.includes('中度')) {
				return 'icon-moderate';
			} else {
				return 'icon-severe';
			}
		},
		getIconSrc(result) {
			if (result.includes('正常')) {
				return '/static/images/mood/normal.svg';
			} else if (result.includes('轻度')) {
				return '/static/images/mood/mild.svg';
			} else if (result.includes('中度')) {
				return '/static/images/mood/moderate.svg';
			} else {
				return '/static/images/mood/severe.svg';
			}
		},
		// 获取本地记录
		getLocalRecords() {
			try {
				const localRecords = uni.getStorageSync('local_mood_records') || []
				this.localRecords = localRecords.map(record => {
					return {
						...record,
						isLocal: true // 标记为本地记录
					}
				})
			} catch (error) {
				console.error('获取本地记录失败', error)
			}
		},
		// 跳转到详情
		goToDetail(record) {
			// 区分本地记录和服务器记录
			if (record.isLocal) {
				uni.navigateTo({
					url: `/pages/mood/result?id=${record.recordId}&score=${record.totalScore}&result=${encodeURIComponent(record.evaluationResult)}&local=true`
				})
			} else {
				uni.navigateTo({
					url: `/pages/mood/result?id=${record.recordId}`
				})
			}
		},
		// 获取服务器记录列表
		async getRecordList() {
			try {
				this.loading = true
				const res = await getUserScaleRecords()
				if (res.code === 200) {
					this.recordList = res.data || []
				} else {
					console.error('获取记录失败:', res.msg)
				}
			} catch (error) {
				console.error('获取记录失败', error)
			} finally {
				this.loading = false
			}
		}
	},
	onShow() {
		this.getRecordList()
		this.getLocalRecords()
	}
}
</script>

<style lang="scss">
@import '@/static/scss/theme.scss';

.history-container {
	padding: 30rpx;
	background-color: #f8f8f8;
	min-height: 100vh;
}

.page-header {
	margin-bottom: 30rpx;
}

.page-title {
	font-size: 36rpx;
	font-weight: bold;
	color: $text-primary;
}

.filter-section {
	margin-bottom: 30rpx;
}

.filter-tabs {
	display: flex;
	background-color: #fff;
	border-radius: 12rpx;
	overflow: hidden;
	@include card-shadow;
}

.filter-tab {
	flex: 1;
	padding: 20rpx 0;
	text-align: center;
	@include transition-all;
	
	text {
		font-size: 28rpx;
		color: $text-secondary;
	}
	
	&.active {
		background-color: $primary-color;
		
		text {
			color: #fff;
			font-weight: 500;
		}
	}
}

.history-list {
	margin-bottom: 30rpx;
}

.history-card {
	background-color: #fff;
	border-radius: 16rpx;
	overflow: hidden;
	margin-bottom: 30rpx;
	@include card-shadow;
	@include transition-all;
	
	&:active {
		transform: scale(0.98);
	}
}

.history-main {
	padding: 24rpx;
	display: flex;
	align-items: center;
}

.history-icon {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	overflow: hidden;
	margin-right: 20rpx;
	
	image {
		width: 100%;
		height: 100%;
	}
}

.icon-normal {
	background-color: #e8f5e9;
}

.icon-mild {
	background-color: #fff8e1;
}

.icon-moderate {
	background-color: #fff3e0;
}

.icon-severe {
	background-color: #ffebee;
}

.history-info {
	flex: 1;
}

.history-title {
	font-size: 30rpx;
	color: $text-primary;
	font-weight: 500;
	margin-bottom: 8rpx;
	display: block;
}

.history-date {
	font-size: 24rpx;
	color: $text-secondary;
}

.history-result {
	padding: 8rpx 16rpx;
	border-radius: 30rpx;
	font-size: 24rpx;
}

.result-normal {
	background-color: #e8f5e9;
	color: #4caf50;
}

.result-mild {
	background-color: #fff8e1;
	color: #ffc107;
}

.result-moderate {
	background-color: #fff3e0;
	color: #ff9800;
}

.result-severe {
	background-color: #ffebee;
	color: #f44336;
}

.history-detail {
	padding: 20rpx 24rpx;
	background-color: #f9f9f9;
	display: flex;
	align-items: center;
	border-top: 1px solid #f0f0f0;
}

.detail-item {
	margin-right: 40rpx;
}

.detail-label {
	font-size: 24rpx;
	color: $text-secondary;
	margin-right: 10rpx;
}

.detail-value {
	font-size: 28rpx;
	color: $primary-color;
	font-weight: 500;
}

.detail-action {
	margin-left: auto;
	display: flex;
	align-items: center;
	
	text {
		font-size: 26rpx;
		color: $primary-color;
		margin-right: 6rpx;
	}
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
}

.empty-image {
	width: 200rpx;
	height: 200rpx;
	margin-bottom: 30rpx;
}

.empty-text {
	font-size: 28rpx;
	color: $text-secondary;
	margin-bottom: 40rpx;
}

.start-test-btn {
	background-color: $primary-color;
	color: #fff;
	font-size: 28rpx;
	padding: 20rpx 60rpx;
	border-radius: 40rpx;
}

.status-tag {
	padding: 4rpx 10rpx;
	font-size: 22rpx;
	color: #fff;
	background-color: #ff9900;
	border-radius: 6rpx;
	margin-left: 10rpx;
}
</style>