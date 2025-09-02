<template>
	<view class="diary-list-container">
		<!-- 顶部标题区 -->
		<view class="page-header">
			<text class="page-title">心理日记</text>
			<view class="add-diary-btn" @click="createDiary">
				<text class="add-icon">+</text>
				<text class="add-text">写日记</text>
			</view>
		</view>
		
		<!-- 日记列表区 -->
		<view class="diary-list" v-if="diaryList.length > 0">
			<view class="diary-item" v-for="(diary, index) in diaryList" :key="diary.id" @click="viewDiary(diary.id)">
				<view class="diary-item-header">
					<text class="diary-item-date">{{formatDate(diary.createTime)}}</text>
					<view class="diary-item-mood">
						<mood-icon-base64 :mood="diary.mood || 'happy'" :size="40" />
					</view>
				</view>
				<view class="diary-item-content">
					<text class="diary-item-title">
						<text class="diary-title-text">{{diary.name}}</text>
					</text>
					<text class="diary-item-summary">{{formatSummary(diary.details)}}</text>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<image class="empty-image" :src="emptyImageSrc"></image>
			<text class="empty-text">还没有日记，点击右上角开始记录吧</text>
		</view>
	</view>
</template>

<script>
	import { getActivityList } from '@/api/maincode/activity'
	import MoodIconBase64 from '../../../components/mood-icon-base64.vue';
	
	export default {
		components: {
			MoodIconBase64
		},
		data() {
			return {
				diaryList: [],
				page: 1,
				pageSize: 10,
				hasMore: true,
				loading: false,
				// 使用内联Base64图像作为空状态图
				emptyImageSrc: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDIwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHBhdGggZD0iTTE2MCA1MEgxMzBWMzBIMTYwVjUwWiIgZmlsbD0iI0VERURFRCIvPjxwYXRoIGQ9Ik0xNjAgMTYwTDQwIDE2MEw0MCA2MEwxNjAgNjBMMTYwIDE2MFoiIGZpbGw9IiNGOEY4RjgiLz48cGF0aCBkPSJNMTUwIDcwSDUwVjkwSDE1MFY3MFoiIGZpbGw9IiNFMUUxRTEiLz48cGF0aCBkPSJNMTUwIDEwMEg1MFYxMDVIMTUwVjEwMFoiIGZpbGw9IiNFMUUxRTEiLz48cGF0aCBkPSJNMTUwIDExNUg1MFYxMjBIMTUwVjExNVoiIGZpbGw9IiNFMUUxRTEiLz48cGF0aCBkPSJNMTIwIDEzMEg1MFYxMzVIMTIwVjEzMFoiIGZpbGw9IiNFMUUxRTEiLz48cGF0aCBkPSJNMTYwIDE2MEw0MCAxNjBMMzUgMTY1TDM1IDU1TDQwIDYwTDE2MCA2MEwxNjUgNjVMMTY1IDE1NUwxNjAgMTYwWiIgZmlsbD0iI0U1RTVFNSIgc3Ryb2tlPSIjQ0NDQ0NDIiBzdHJva2Utd2lkdGg9IjIiLz48cGF0aCBkPSJNMTMwIDUwTDQwIDUwTDM1IDU1TDM1IDI1TDQwIDMwTDEzMCAzMEwxMzUgMzVMMTM1IDQ1TDEzMCA1MFoiIGZpbGw9IiNFNUU1RTUiIHN0cm9rZT0iI0NDQ0NDQyIgc3Ryb2tlLXdpZHRoPSIyIi8+PC9zdmc+'
			}
		},
		onLoad() {
			this.loadDiaryList();
		},
		onPullDownRefresh() {
			this.refreshList();
		},
		methods: {
			formatDate(dateStr) {
				const date = new Date(dateStr);
				const year = date.getFullYear();
				const month = (date.getMonth() + 1).toString().padStart(2, '0');
				const day = date.getDate().toString().padStart(2, '0');
				return `${year}-${month}-${day}`;
			},
			formatSummary(text) {
				if (!text) return '无内容';
				return text.length > 50 ? text.substring(0, 50) + '...' : text;
			},
			createDiary() {
				uni.navigateTo({
					url: './detail'
				});
			},
			viewDiary(id) {
				uni.navigateTo({
					url: `./detail?id=${id}`
				});
			},
			loadDiaryList() {
				if (this.loading || !this.hasMore) return;
			 
				this.loading = true;
				
				getActivityList({
					page: this.page,
					pageSize: this.pageSize
				}).then(res => {
					if (res.code === 0 && res.data) {
						if (res.data.length > 0) {
							this.diaryList = [...this.diaryList, ...res.data];
							this.page++;
							this.hasMore = res.data.length === this.pageSize;
						} else {
							this.hasMore = false;
						}
					} else if (res.rows) {
						// 处理旧格式的返回数据
						if (res.rows.length > 0) {
							this.diaryList = [...this.diaryList, ...res.rows];
							this.page++;
							this.hasMore = res.rows.length === this.pageSize;
						} else {
							this.hasMore = false;
						}
					}
					this.loading = false;
					uni.stopPullDownRefresh();
				}).catch(() => {
					this.loading = false;
					uni.stopPullDownRefresh();
				});
			},
			refreshList() {
				this.page = 1;
				this.diaryList = [];
				this.hasMore = true;
				this.loadDiaryList();
			}
		},
		onReachBottom() {
			this.loadDiaryList();
		}
	}
</script>

<style>
	.diary-list-container {
		min-height: 100vh;
		background-color: #f5f5f5;
		padding: 30rpx;
	}
	
	.page-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 40rpx;
	}
	
	.page-title {
		font-size: 40rpx;
		font-weight: bold;
		color: #333;
	}
	
	.add-diary-btn {
		display: flex;
		align-items: center;
		background-color: #007AFF;
		padding: 14rpx 24rpx;
		border-radius: 30rpx;
	}
	
	.add-icon {
		font-size: 32rpx;
		color: #fff;
		margin-right: 8rpx;
	}
	
	.add-text {
		font-size: 28rpx;
		color: #fff;
	}
	
	.diary-list {
		display: flex;
		flex-direction: column;
	}
	
	.diary-item {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.diary-item-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}
	
	.diary-item-date {
		font-size: 26rpx;
		color: #999;
	}
	
	.diary-item-mood {
		display: flex;
		align-items: center;
	}
	
	.diary-item-content {
		display: flex;
		flex-direction: column;
	}
	
	.diary-item-title {
		font-size: 32rpx;
		font-weight: 500;
		color: #333;
		margin-bottom: 16rpx;
		line-height: 1.4;
	}
	
	.diary-title-text {
		display: inline-block;
		max-width: 100%;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
	}
	
	.diary-item-summary {
		font-size: 28rpx;
		color: #666;
		line-height: 1.4;
	}
	
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding-top: 100rpx;
	}
	
	.empty-image {
		width: 240rpx;
		height: 240rpx;
		margin-bottom: 30rpx;
	}
	
	.empty-text {
		font-size: 28rpx;
		color: #999;
	}
</style>