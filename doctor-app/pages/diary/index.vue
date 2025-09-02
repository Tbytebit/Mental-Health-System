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
		
		<!-- 搜索和筛选区 -->
		<view class="search-filter-container">
			<view class="search-box">
				<input 
					class="search-input" 
					type="text" 
					v-model="searchText" 
					placeholder="搜索日记..." 
					confirm-type="search"
					@confirm="handleSearch"
				/>
				<text class="search-icon" @click="handleSearch">🔍</text>
			</view>
			<view class="filter-options">
				<view 
					class="filter-item" 
					:class="{ active: selectedMood === 'all' }" 
					@click="filterByMood('all')"
				>
					全部
				</view>
				<view 
					v-for="mood in moodTypes" 
					:key="mood" 
					class="filter-item" 
					:class="{ active: selectedMood === mood }" 
					@click="filterByMood(mood)"
				>
					<mood-icon-base64 :mood="mood" :size="24" />
				</view>
			</view>
		</view>
		
		<!-- 日记列表区 -->
		<view class="diary-list" v-if="filteredDiaries.length > 0">
			<view 
				class="diary-item" 
				v-for="(diary, index) in filteredDiaries" 
				:key="diary.id" 
				@click="viewDiary(diary.id)"
				hover-class="diary-item-hover"
				:data-mood="diary.mood"
			>
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
		
		<!-- 加载状态 -->
		<view class="loading-state" v-if="loading && diaryList.length === 0">
			<view class="loading-spinner"></view>
			<text class="loading-text">加载中...</text>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-else-if="filteredDiaries.length === 0">
			<image class="empty-image" :src="emptyImageSrc"></image>
			<text class="empty-text" v-if="searchText || selectedMood !== 'all'">没有找到符合条件的日记</text>
			<text class="empty-text" v-else>还没有日记，点击右上角开始记录吧</text>
			<view class="reset-filter-btn" v-if="searchText || selectedMood !== 'all'" @click="resetFilters">重置筛选</view>
		</view>
		
		<!-- 加载更多 -->
		<view class="load-more" v-if="filteredDiaries.length > 0 && hasMore && !loading">
			<text class="load-more-text" @click="loadMoreDiaries">加载更多</text>
		</view>
		<view class="load-more" v-else-if="filteredDiaries.length > 0 && loading">
			<view class="loading-spinner small"></view>
			<text class="loading-text small">加载中...</text>
		</view>
		
		<!-- 页脚说明 -->
		<view class="footer-tips" v-if="filteredDiaries.length > 0">
			<text class="tips-text">记录心情，提升心理健康</text>
		</view>
	</view>
</template>

<script>
	import { getDiaryList, mockGetDiaryList } from '@/api/diary/index';
	import MoodIconBase64 from '@/components/mood-icon-base64.vue';
	import config from '@/config';
	
	export default {
		components: {
			MoodIconBase64
		},
		data() {
			return {
				diaryList: [],
				total: 0,
				loading: false,
				searchText: '',
				selectedMood: 'all',
				moodTypes: ['happy', 'neutral', 'sad', 'angry', 'anxious'],
				pageNum: 1,
				pageSize: 10,
				hasMore: true,
				emptyImageSrc: '/static/images/empty-diary.png'
			}
		},
		computed: {
			filteredDiaries() {
				return this.diaryList.map(diary => {
					// 处理图片字段，确保正确显示
					if (diary.images) {
						let imageArray = [];
						if (typeof diary.images === 'string') {
							try {
								imageArray = JSON.parse(diary.images);
							} catch(e) {
								imageArray = diary.images ? [diary.images] : [];
							}
						} else if (Array.isArray(diary.images)) {
							imageArray = diary.images;
						}
						
						// 确保图片URL是完整的
						diary.processedImages = imageArray.map(img => {
							if (!img) return '';
							
							const baseUrl = config.baseUrl;
							
							// 如果已经是完整URL或base64，直接使用
							if (img.startsWith('http') || img.startsWith('data:')) {
								return img;
							}
							
							// 处理带有/profile前缀的图片路径
							if (img.startsWith('/profile/') || img === '/profile') {
								return baseUrl + img;
							}
							
							// 检查是否包含profile但不是以/profile开头
							if (img.includes('profile/') && !img.startsWith('/profile/')) {
								return baseUrl + '/profile/' + img.substring(img.indexOf('profile/') + 8);
							}
							
							// 如果是其他路径，确保添加/profile前缀
							if (img.startsWith('/')) {
								return baseUrl + '/profile' + img;
							}
							
							// 其他情况，确保路径正确
							return baseUrl + '/profile/' + img;
						});
						
						console.log('日记ID:', diary.id, '处理后的图片:', diary.processedImages);
					} else {
						diary.processedImages = [];
					}
					
					return diary;
				});
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
				
				// 获取星期几
				const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
				const weekDay = weekDays[date.getDay()];
				
				return `${year}-${month}-${day} ${weekDay}`;
			},
			formatSummary(text) {
				if (!text) return '无内容';
				// 移除HTML标签
				const plainText = text.replace(/<[^>]+>/g, '');
				return plainText.length > 60 ? plainText.substring(0, 60) + '...' : plainText;
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
				
				// 强制使用实际API而非模拟数据
				// const apiCall = process.env.NODE_ENV === 'development' 
				// 	? mockGetDiaryList 
				// 	: getDiaryList;
				const apiCall = getDiaryList;
				
				const params = {
					pageNum: this.pageNum,
					pageSize: this.pageSize,
					mood: this.selectedMood !== 'all' ? this.selectedMood : undefined,
					keyword: this.searchText || undefined
				};
				
				console.log('请求日记列表参数:', params);
				
				apiCall(params).then(res => {
					console.log('获取日记列表响应:', JSON.stringify(res));
					
					this.loading = false;
					uni.stopPullDownRefresh();
					
					if ((res.code === 0 || res.code === 200)) {
						let dataList = [];
						
						// 兼容不同的返回数据结构
						if (Array.isArray(res.rows)) {
							// 处理顶层rows字段（用户提供的数据结构）
							dataList = res.rows;
						} else if (res.data && Array.isArray(res.data)) {
							dataList = res.data;
						} else if (res.data && res.data.rows && Array.isArray(res.data.rows)) {
							dataList = res.data.rows;
						} else if (res.data && res.data.list && Array.isArray(res.data.list)) {
							dataList = res.data.list;
						} else if (res.data && res.data.content && typeof res.data.content === 'object') {
							const content = res.data.content;
							if (content.rows && Array.isArray(content.rows)) {
								dataList = content.rows;
							} else if (content.list && Array.isArray(content.list)) {
								dataList = content.list;
							} else if (Array.isArray(content)) {
								dataList = content;
							}
						}
						
						// 统一数据字段
						dataList = dataList.map(diary => {
							return {
								id: diary.diaryId || diary.id,
								name: diary.diaryName || diary.name,
								details: diary.diaryContent || diary.details || diary.content,
								mood: diary.mood || 'happy',
								createTime: diary.createTime || diary.updateTime || new Date(),
								// 处理图片
								images: diary.images ? (typeof diary.images === 'string' ? 
									JSON.parse(diary.images) : diary.images) : []
							};
						});
						
						if (dataList.length > 0) {
							this.diaryList = [...this.diaryList, ...dataList];
							this.pageNum++;
							this.hasMore = dataList.length === this.pageSize;
						} else {
							this.hasMore = false;
						}
					} else {
						this.hasMore = false;
						
						if (res.code !== 0 && res.code !== 200) {
							uni.showToast({
								title: res.msg || '获取日记列表失败',
								icon: 'none'
							});
						}
					}
				}).catch(err => {
					console.error('获取日记列表失败', err);
					this.loading = false;
					uni.stopPullDownRefresh();
					
					uni.showToast({
						title: '获取日记列表失败',
						icon: 'none'
					});
				});
			},
			refreshList() {
				this.pageNum = 1;
				this.diaryList = [];
				this.hasMore = true;
				this.loadDiaryList();
			},
			handleSearch() {
				this.refreshList();
			},
			filterByMood(mood) {
				this.selectedMood = mood;
				this.refreshList();
			},
			resetFilters() {
				this.searchText = '';
				this.selectedMood = 'all';
				this.refreshList();
			},
			loadMoreDiaries() {
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
	
	.search-filter-container {
		margin-bottom: 30rpx;
	}
	
	.search-box {
		position: relative;
		margin-bottom: 20rpx;
	}
	
	.search-input {
		width: 100%;
		height: 80rpx;
		background-color: #fff;
		border-radius: 40rpx;
		padding: 0 80rpx 0 30rpx;
		font-size: 28rpx;
		box-sizing: border-box;
	}
	
	.search-icon {
		position: absolute;
		right: 30rpx;
		top: 50%;
		transform: translateY(-50%);
		font-size: 32rpx;
		color: #999;
	}
	
	.filter-options {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
		margin-top: 20rpx;
	}
	
	.filter-item {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 60rpx;
		padding: 0 20rpx;
		background-color: #fff;
		border-radius: 30rpx;
		font-size: 26rpx;
		color: #666;
		border: 1px solid #eee;
	}
	
	.filter-item.active {
		background-color: #007AFF;
		color: #fff;
		border-color: #007AFF;
	}
	
	.diary-list {
		display: flex;
		flex-direction: column;
	}
	
	.diary-item {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx 30rpx 24rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
		transition: all 0.3s ease;
		border-left: 8rpx solid #007AFF;
	}
	
	.diary-item:active {
		transform: scale(0.98);
		box-shadow: 0 1rpx 5rpx rgba(0, 0, 0, 0.1);
	}
	
    #根据心情设置不同的边框颜色
	.diary-item[data-mood="happy"] {
		border-left-color: #FFD700; /* 金色 */
	}
	
	.diary-item[data-mood="neutral"] {
		border-left-color: #90EE90; /* 淡绿色 */
	}
	
	.diary-item[data-mood="sad"] {
		border-left-color: #87CEFA; /* 淡蓝色 */
	}
	
	.diary-item[data-mood="angry"] {
		border-left-color: #FF6347; /* 番茄色 */
	}
	
	.diary-item[data-mood="anxious"] {
		border-left-color: #BA55D3; /* 紫色 */
	}
	
	.diary-item-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 24rpx;
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
		flex: 1;
		overflow: hidden;
	}
	
	.diary-item-title {
		font-size: 36rpx;
		font-weight: 600;
		color: #222;
		margin-bottom: 20rpx;
		line-height: 1.4;
		display: flex;
		align-items: center;
		position: relative;
		padding-left: 16rpx;
	}
	
	.diary-item-title::before {
		content: '';
		position: absolute;
		left: 0;
		top: 50%;
		transform: translateY(-50%);
		width: 6rpx;
		height: 28rpx;
		background-color: #007AFF;
		border-radius: 3rpx;
	}
	
	.diary-title-text {
		display: inline-block;
		max-width: 100%;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		letter-spacing: 1rpx;
		transition: all 0.2s ease;
		padding: 4rpx 0;
	}
	
	.diary-item:hover .diary-title-text {
		color: #007AFF;
		transform: translateX(4rpx);
	}
	
	.diary-item-summary {
		font-size: 28rpx;
		color: #666;
		line-height: 1.4;
		overflow: hidden;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		padding-left: 16rpx;
		margin-top: 4rpx;
	}
	
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding-top: 100rpx;
		padding-bottom: 50rpx;
	}
	
	.empty-image {
		width: 240rpx;
		height: 240rpx;
		margin-bottom: 30rpx;
		opacity: 0.8;
	}
	
	.empty-text {
		font-size: 28rpx;
		color: #999;
		margin-bottom: 30rpx;
		text-align: center;
		line-height: 1.5;
	}
	
	.reset-filter-btn {
		padding: 16rpx 40rpx;
		background-color: #007AFF;
		color: #fff;
		font-size: 28rpx;
		border-radius: 30rpx;
	}
	
	.load-more {
		text-align: center;
		margin: 30rpx 0;
	}
	
	.load-more-text {
		font-size: 28rpx;
		color: #007AFF;
		padding: 10rpx 20rpx;
		border-radius: 30rpx;
		background-color: rgba(0, 122, 255, 0.1);
	}
	
	.footer-tips {
		text-align: center;
		margin: 40rpx 0;
		padding-bottom: 40rpx;
	}
	
	.tips-text {
		font-size: 24rpx;
		color: #999;
	}
</style>

// 在style部分添加以下样式
.diary-item-hover {
	transform: scale(0.98);
	background-color: #f9f9f9;
}

.loading-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
}

.loading-spinner {
	width: 60rpx;
	height: 60rpx;
	border: 4rpx solid #f3f3f3;
	border-top: 4rpx solid #007AFF;
	border-radius: 50%;
	animation: spin 1s linear infinite;
	margin-bottom: 20rpx;
}

.loading-spinner.small {
	width: 30rpx;
	height: 30rpx;
	border-width: 2rpx;
	margin-bottom: 0;
	margin-right: 10rpx;
}

.loading-text {
	font-size: 28rpx;
	color: #999;
}

.loading-text.small {
	font-size: 24rpx;
}

.load-more {
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 30rpx 0;
}

@keyframes spin {
	0% { transform: rotate(0deg); }
	100% { transform: rotate(360deg); }
}