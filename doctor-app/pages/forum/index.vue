<template>
	<view class="forum-container">
		<!-- 顶部搜索栏 -->
		<view class="search-section">
			<view class="search-box">
				<uni-icons type="search" size="18" color="#999"></uni-icons>
				<input type="text" v-model="searchKeyword" placeholder="搜索帖子" @confirm="searchPosts" />
			</view>
		</view>
		
		<!-- 分类标签 -->
		<scroll-view class="category-scroll" scroll-x="true" show-scrollbar="false">
			<view class="category-list">
				<view 
					class="category-item" 
					:class="{active: activeCategory === 'all'}"
					@click="selectCategory('all')"
				>
					全部
				</view>
				<view 
					v-for="(category, index) in categories" 
					:key="index"
					class="category-item"
					:class="{active: activeCategory === category.id}"
					@click="selectCategory(category.id)"
				>
					{{ category.name }}
				</view>
			</view>
		</scroll-view>
		
		<!-- 帖子列表 -->
		<view class="post-list" v-if="posts.length > 0">
			<view 
				class="post-item" 
				v-for="(post, index) in posts" 
				:key="post.id"
				@click="viewPostDetail(post.id)"
			>
				<view class="post-header">
					<view class="user-info" @click.stop="viewUserProfile(post.userId, post.username)">
						<image class="avatar" :src="post.avatar || '/static/images/profile.jpg'"></image>
						<view class="user-details">
							<text class="username">{{ post.username }}</text>
							<text class="post-time">{{ formatTime(post.createTime) }}</text>
						</view>
					</view>
					<view class="category-tag" v-if="post.categoryName">
						{{ post.categoryName }}
					</view>
				</view>
				<view class="post-content">
					<text class="post-title">{{ post.title }}</text>
					<text class="post-summary">{{ post.content }}</text>
				</view>
				<view class="post-image" v-if="post.images && post.images.length > 0">
					<image 
						v-if="post.images.length === 1" 
						class="single-image" 
						:src="post.images[0]" 
						mode="aspectFill"
					></image>
					<view v-else class="image-grid">
						<image 
							v-for="(img, imgIndex) in post.images.slice(0, 3)" 
							:key="imgIndex" 
							class="grid-image" 
							:src="img" 
							mode="aspectFill"
						></image>
						<view class="more-images" v-if="post.images.length > 3">
							+{{ post.images.length - 3 }}
						</view>
					</view>
				</view>
				<view class="post-footer">
					<view class="action-item">
						<uni-icons type="chat" size="18" color="#666"></uni-icons>
						<text>{{ post.commentCount || 0 }}</text>
					</view>
					<view class="action-item" @click.stop="toggleLike(post, index)">
						<uni-icons :type="post.isLiked ? 'heart-filled' : 'heart'" size="18" :color="post.isLiked ? '#ff6b6b' : '#666'"></uni-icons>
						<text :class="{active: post.isLiked}">{{ post.likeCount || 0 }}</text>
					</view>
					<view class="action-item">
						<uni-icons type="eye" size="18" color="#666"></uni-icons>
						<text>{{ post.viewCount || 0 }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<image class="empty-image" src="/static/images/empty-forum.svg"></image>
			<text class="empty-text">暂无帖子，快来发布第一个帖子吧</text>
		</view>
		
		<!-- 发布按钮 -->
		<view class="publish-btn" @click="createPost">
			<uni-icons type="plusempty" size="24" color="#fff"></uni-icons>
		</view>
	</view>
</template>

<script>
import request from '@/utils/request'
import { getForumPostList, getForumCategories, likeForumPost, unlikeForumPost } from '@/api/forum/forum.js'


export default {
	data() {
		return {
			searchKeyword: '',
			activeCategory: 'all',
			categories: [],
			posts: [],
			loading: false,
			pagination: {
				pageNum: 1,
				pageSize: 10,
				total: 0
			},
			noMoreData: false
		};
	},
	methods: {
		selectCategory(categoryId) {
			this.activeCategory = categoryId;
			// 重置分页并重新加载数据
			this.resetAndLoadPosts();
		},
		getCategoryName(categoryId) {
			const category = this.categories.find(item => item.id === categoryId);
			return category ? category.name : '';
		},
		searchPosts() {
			if (!this.searchKeyword.trim()) {
				return;
			}
			// 重置分页并重新加载数据
			this.resetAndLoadPosts();
		},
		resetAndLoadPosts() {
			this.posts = [];
			this.pagination.pageNum = 1;
			this.noMoreData = false;
			this.loadPosts();
		},
		loadPosts() {
			if (this.loading || this.noMoreData) {
				return;
			}
			
			this.loading = true;
			uni.showLoading({
				title: '加载中...'
			});
			
			const params = {
				pageNum: this.pagination.pageNum,
				pageSize: this.pagination.pageSize
			};
			
			// 设置分类条件
			if (this.activeCategory !== 'all') {
				params.categoryId = this.activeCategory;
			}
			
			// 设置搜索关键词
			if (this.searchKeyword.trim()) {
				params.keyword = this.searchKeyword.trim();
			}
			
			console.log('获取帖子列表参数:', params);
			
			getForumPostList(params).then(response => {
				uni.hideLoading();
				
				// 检查response对象
				console.log('获取论坛帖子完整响应:', response);
				
				// 处理空数据情况
				if (!response) {
					console.log('无响应数据，标记为无更多数据');
					this.noMoreData = true;
					this.loading = false;
					uni.stopPullDownRefresh();
					return;
				}
				
				let posts = [];
				let total = 0;
				
				// 处理数据格式 - 首先尝试获取标准格式数据
				if (response.rows && Array.isArray(response.rows)) {
					console.log('处理标准格式数据，包含rows和total：', response.rows.length, response.total);
					posts = response.rows;
					total = response.total || posts.length;
				} 
				// 兼容旧数据格式
				else if (Array.isArray(response)) {
					console.log('处理数组格式数据：', response.length);
					posts = response;
					total = response.length;
				}
				// 检查是否在data字段中
				else if (response.data) {
					if (Array.isArray(response.data)) {
						console.log('从data字段获取数组格式数据：', response.data.length);
						posts = response.data;
						total = response.data.length;
					} else if (response.data.rows && Array.isArray(response.data.rows)) {
						console.log('从data字段获取标准格式数据：', response.data.rows.length);
						posts = response.data.rows;
						total = response.data.total || posts.length;
					}
				}
				
				console.log('最终处理的帖子数组:', posts);
				
				if (posts.length > 0) {
					// 格式化数据
					posts.forEach(post => {
						// 处理可能的头像路径问题
						if (post.avatar && !post.avatar.startsWith('http') && !post.avatar.startsWith('/')) {
							post.avatar = '/' + post.avatar;
						}
						
						// 处理图片路径
						if (post.images && post.images.length > 0) {
							post.images = post.images.map(img => {
								if (!img.startsWith('http') && !img.startsWith('/')) {
									return '/' + img;
								}
								return img;
							});
						}
					});
					
					// 当页码为1时，替换数据，否则追加数据
					if (this.pagination.pageNum === 1) {
						console.log('第一页数据，替换帖子列表');
						this.posts = posts;
					} else {
						console.log('非第一页数据，追加到帖子列表');
						this.posts = [...this.posts, ...posts];
					}
					
					this.pagination.total = total;
					this.pagination.pageNum++;
					
					if (posts.length < this.pagination.pageSize) {
						console.log('返回的帖子数量小于每页大小，标记为无更多数据');
						this.noMoreData = true;
					} else {
						this.noMoreData = false;
					}
				} else {
					console.log('处理后的帖子数组为空，标记为无更多数据');
					this.noMoreData = true;
				}
				
				// 检查点赞状态
				this.checkLikedPosts();
			}).catch(error => {
				uni.hideLoading();
				console.error('获取帖子列表失败', error);
				uni.showToast({
					title: '获取数据失败',
					icon: 'none'
				});
			}).finally(() => {
				this.loading = false;
				uni.stopPullDownRefresh();
			});
		},
		loadCategories() {
			getForumCategories().then(response => {
				const { data } = response;
				if (data && data.length > 0) {
					this.categories = data;
					console.log('成功加载分类:', this.categories);
				} else {
					console.warn('返回的分类数据为空');
					// 使用默认分类
					this.loadDefaultCategories();
				}
			}).catch(error => {
				console.error('获取分类失败', error);
				// 使用默认分类
				this.loadDefaultCategories();
			});
		},
		loadDefaultCategories() {
			this.categories = [
				{ id: "anxiety", name: '焦虑情绪' },
				{ id: "depression", name: '抑郁症状' },
				{ id: "stress", name: '压力管理' },
				{ id: "relationship", name: '人际关系' },
				{ id: "career", name: '职业发展' },
				{ id: "family", name: '家庭问题' },
				{ id: "other", name: '其他话题' }
			];
			console.log('使用默认分类:', this.categories);
		},
		viewPostDetail(postId) {
			uni.navigateTo({
				url: `/pages/forum/post/detail?id=${postId}`
			});
		},
		createPost() {
			uni.navigateTo({
				url: '/pages/forum/post/create'
			});
		},
		formatTime(timeString) {
			const now = new Date();
			const postTime = new Date(timeString);
			const diff = now - postTime;
			
			// 小于1分钟
			if (diff < 60 * 1000) {
				return '刚刚';
			}
			// 小于1小时
			if (diff < 60 * 60 * 1000) {
				return Math.floor(diff / (60 * 1000)) + '分钟前';
			}
			// 小于1天
			if (diff < 24 * 60 * 60 * 1000) {
				return Math.floor(diff / (60 * 60 * 1000)) + '小时前';
			}
			// 小于30天
			if (diff < 30 * 24 * 60 * 60 * 1000) {
				return Math.floor(diff / (24 * 60 * 60 * 1000)) + '天前';
			}
			
			// 大于30天，显示具体日期
			const year = postTime.getFullYear();
			const month = (postTime.getMonth() + 1).toString().padStart(2, '0');
			const day = postTime.getDate().toString().padStart(2, '0');
			return `${year}-${month}-${day}`;
		},
		toggleLike(post, index) {
			if (!post.isLiked) {
				// 立即更新UI状态
				this.$set(this.posts[index], 'isLiked', true);
				this.$set(this.posts[index], 'likeCount', (post.likeCount || 0) + 1);
				
				// 更新本地存储
				const likedPosts = uni.getStorageSync('likedPosts') || [];
				if (!likedPosts.includes(post.id)) {
					likedPosts.push(post.id);
					uni.setStorageSync('likedPosts', likedPosts);
				}
				
				// 调用API
				likeForumPost(post.id).then(response => {
					const { data } = response;
					if (data && data.success) {
						// 如果服务器返回了点赞数，以服务器为准
						if (data.likeCount !== undefined) {
							this.$set(this.posts[index], 'likeCount', data.likeCount);
						}
					}
				}).catch(error => {
					console.error('点赞失败', error);
					// 回滚UI状态
					this.$set(this.posts[index], 'isLiked', false);
					this.$set(this.posts[index], 'likeCount', Math.max(0, post.likeCount));
					
					// 回滚本地存储
					let likedPosts = uni.getStorageSync('likedPosts') || [];
					likedPosts = likedPosts.filter(id => id !== post.id);
					uni.setStorageSync('likedPosts', likedPosts);
					
					uni.showToast({
						title: '点赞失败',
						icon: 'none'
					});
				});
			} else {
				// 检查本地存储，避免重复操作
				let likedPosts = uni.getStorageSync('likedPosts') || [];
				if (!likedPosts.includes(post.id)) {
					console.log('本地存储中没有该帖子的点赞记录，无需取消');
					return;
				}
				
				// 立即更新UI状态
				this.$set(this.posts[index], 'isLiked', false);
				this.$set(this.posts[index], 'likeCount', Math.max(0, post.likeCount - 1));
				
				// 更新本地存储
				likedPosts = likedPosts.filter(id => id !== post.id);
				uni.setStorageSync('likedPosts', likedPosts);
				
				// 调用API
				unlikeForumPost(post.id).then(response => {
					const { data } = response;
					if (data && data.success) {
						// 如果服务器返回了点赞数，以服务器为准
						if (data.likeCount !== undefined) {
							this.$set(this.posts[index], 'likeCount', data.likeCount);
						}
					}
				}).catch(error => {
					console.error('取消点赞失败', error);
					
					// 只有当错误不是唯一键冲突时才回滚UI状态
					if (!error || !error.message || !(
						error.message.includes('Duplicate entry') || 
						error.message.includes('DuplicateKeyException') ||
						error.message.includes('uk_type_target_user')
					)) {
						// 回滚UI状态
						this.$set(this.posts[index], 'isLiked', true);
						this.$set(this.posts[index], 'likeCount', post.likeCount + 1);
						
						// 回滚本地存储
						const likedPosts = uni.getStorageSync('likedPosts') || [];
						if (!likedPosts.includes(post.id)) {
							likedPosts.push(post.id);
							uni.setStorageSync('likedPosts', likedPosts);
						}
						
						uni.showToast({
							title: '取消点赞失败',
							icon: 'none'
						});
					}
				});
			}
		},
		checkLikedPosts() {
			const likedPosts = uni.getStorageSync('likedPosts') || [];
			this.posts.forEach((post, index) => {
				this.$set(this.posts[index], 'isLiked', likedPosts.includes(post.id));
			});
		},
		viewUserProfile(userId, username) {
			if (!userId) {
				uni.showToast({
					title: '用户ID不存在',
					icon: 'none'
				});
				return;
			}
			
			// 显示确认对话框
			this.showFriendRequestConfirm(userId, username);
		},
		showFriendRequestConfirm(userId, username) {
			// 使用 uni.prompt 实现输入框
			uni.showModal({
				title: '添加好友',
				content: `请填写好友备注，将发送申请给"${username}"`,
				editable: true,
				placeholderText: '请输入备注名称',
				success: (res) => {
					if (res.confirm) {
						// 确认后发送好友申请，传入用户输入的备注
						this.doSendFriendRequest(userId, username, res.content);
					}
				}
			});
		},
		doSendFriendRequest(userId, username, contactRemark = '') {
			uni.showLoading({
				title: '发送申请...'
			});
			
			// An important check before proceeding
			if (!userId) {
				uni.hideLoading();
				uni.showToast({
					title: '用户ID不存在',
					icon: 'none'
				});
				return;
			}
			
			// 获取当前用户信息
			const currentUserInfo = uni.getStorageSync('userInfo') || {};
			const currentUserId = uni.getStorageSync('userId') || this.currentUserId;
			// 优先使用nickName，如果没有则使用username
			const remark = currentUserInfo.nickName || currentUserInfo.username || '';
			
			// 记录用于调试
			console.log('发送好友申请，当前用户信息:', currentUserInfo);
			console.log('使用的备注名称:', remark);
			console.log('被添加好友的备注:', contactRemark);
			
			// 使用新的API发送好友申请 - 使用正确的参数顺序：当前用户ID作为userId，目标用户ID作为friendId
			sendFriendRequest(currentUserId, userId, remark, contactRemark)
				.then(res => {
					uni.hideLoading();
					console.log('好友申请响应:', res);
					
					if (res.code === 200) {
						uni.showToast({
							title: res.msg || '申请已发送',
							icon: 'success'
						});
						
						// 设置标记，告知聊天页面有新的好友申请
						uni.setStorageSync('has_new_friend_request', true);
					} else {
						uni.showToast({
							title: res.msg || '申请失败',
							icon: 'none'
						});
					}
				})
				.catch(error => {
					uni.hideLoading();
					console.error('发送好友申请失败:', error);
					uni.showToast({
						title: '发送申请失败，请稍后再试',
						icon: 'none'
					});
				});
		},
		onLoad() {
			// 加载分类
			this.loadCategories();
			// 加载帖子列表
			this.loadPosts();
		},
		onShow() {
			console.log('论坛首页显示，检查是否需要刷新');
			
			// 检查是否需要刷新列表
			try {
				const needRefresh = uni.getStorageSync('forum_need_refresh');
				console.log('刷新标记状态:', needRefresh);
				
				if (needRefresh === 'true') {
					console.log('检测到需要刷新论坛列表');
					// 重置并刷新数据
					this.resetAndLoadPosts();
					// 清除刷新标记
					uni.removeStorageSync('forum_need_refresh');
				} else {
					// 如果是第一次加载页面且没有帖子数据，也进行刷新
					if (this.posts.length === 0) {
						console.log('未检测到刷新标记，但页面没有数据，执行加载');
						this.resetAndLoadPosts();
					} else {
						console.log('无需刷新论坛列表');
					}
				}
			} catch (e) {
				console.error('检查刷新标记失败:', e);
			}
		},
		onPullDownRefresh() {
			// 下拉刷新
			this.resetAndLoadPosts();
		},
		onReachBottom() {
			// 上拉加载更多
			if (!this.noMoreData) {
				this.loadPosts();
			}
		}
	}
}
</script>

<style>
	.forum-container {
		background-color: #f8f8f8;
		min-height: 100vh;
		padding-bottom: 120rpx;
	}
	
	.search-section {
		padding: 30rpx;
		background-color: #fff;
	}
	
	.search-box {
		display: flex;
		align-items: center;
		background-color: #f5f5f5;
		border-radius: 36rpx;
		padding: 15rpx 30rpx;
	}
	
	.search-box input {
		flex: 1;
		margin-left: 15rpx;
		font-size: 28rpx;
		height: 60rpx;
		line-height: 60rpx;
	}
	
	.category-scroll {
		white-space: nowrap;
		background-color: #fff;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #eee;
	}
	
	.category-list {
		display: flex;
		padding: 0 20rpx;
	}
	
	.category-item {
		display: inline-block;
		padding: 12rpx 30rpx;
		margin-right: 20rpx;
		border-radius: 30rpx;
		font-size: 28rpx;
		color: #666;
		background-color: #f5f5f5;
		transition: all 0.3s;
	}
	
	.category-item.active {
		background-color: #007AFF;
		color: #fff;
	}
	
	.post-list {
		padding: 20rpx;
	}
	
	.post-item {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.post-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}
	
	.user-info {
		display: flex;
		align-items: center;
	}
	
	.avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		background-color: #f0f0f0;
	}
	
	.user-details {
		margin-left: 20rpx;
	}
	
	.username {
		font-size: 28rpx;
		font-weight: 500;
		color: #333;
		display: block;
	}
	
	.post-time {
		font-size: 24rpx;
		color: #999;
		display: block;
		margin-top: 4rpx;
	}
	
	.category-tag {
		padding: 6rpx 20rpx;
		background-color: #f0f8ff;
		color: #007AFF;
		border-radius: 20rpx;
		font-size: 24rpx;
	}
	
	.post-content {
		margin-bottom: 20rpx;
	}
	
	.post-title {
		font-size: 32rpx;
		font-weight: 600;
		color: #333;
		margin-bottom: 10rpx;
		display: block;
	}
	
	.post-summary {
		font-size: 28rpx;
		color: #666;
		line-height: 1.5;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 3;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	
	.post-image {
		margin: 20rpx 0;
	}
	
	.single-image {
		width: 100%;
		height: 360rpx;
		border-radius: 8rpx;
	}
	
	.image-grid {
		display: flex;
		flex-wrap: wrap;
		gap: 10rpx;
		position: relative;
	}
	
	.grid-image {
		width: calc((100% - 20rpx) / 3);
		height: 200rpx;
		border-radius: 8rpx;
		object-fit: cover;
	}
	
	.more-images {
		position: absolute;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.5);
		color: #fff;
		padding: 10rpx 20rpx;
		border-radius: 0 0 8rpx 0;
		font-size: 24rpx;
	}
	
	.post-footer {
		display: flex;
		justify-content: space-around;
		padding-top: 20rpx;
		border-top: 1rpx solid #f5f5f5;
	}
	
	.action-item {
		display: flex;
		align-items: center;
		color: #666;
		font-size: 24rpx;
	}
	
	.action-item text {
		margin-left: 8rpx;
	}
	
	.action-item text.active {
		color: #ff6b6b;
	}
	
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;
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
	
	.publish-btn {
		position: fixed;
		right: 40rpx;
		bottom: 120rpx;
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		background-color: #007AFF;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 4rpx 16rpx rgba(0, 122, 255, 0.3);
		z-index: 99;
	}
</style>