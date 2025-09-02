<template>
	<view class="user-profile-container">
		<!-- 用户基本信息 -->
		<view class="user-info-section">
			<view class="user-header">
				<image class="user-avatar" :src="userInfo.avatar || '/static/images/profile.jpg'"></image>
				<view class="user-details">
					<text class="username">{{ userInfo.username }}</text>
					<text class="user-id">ID: {{ userInfo.id }}</text>
				</view>
			</view>
			
			<view class="user-introduction">
				{{ userInfo.introduction || '这个用户很懒，还没有写自我介绍' }}
			</view>
			
			<view class="user-stats">
				<view class="stat-item">
					<text class="stat-value">{{ userInfo.postCount || 0 }}</text>
					<text class="stat-label">帖子</text>
				</view>
				<view class="stat-item">
					<text class="stat-value">{{ userInfo.followCount || 0 }}</text>
					<text class="stat-label">关注</text>
				</view>
				<view class="stat-item">
					<text class="stat-value">{{ userInfo.fansCount || 0 }}</text>
					<text class="stat-label">粉丝</text>
				</view>
			</view>
			
			<view class="user-actions">
				<button class="action-btn add-friend-btn" @click="sendFriendRequest">
					发送好友申请
				</button>
			</view>
		</view>
		
		<!-- 用户帖子列表 -->
		<view class="user-posts-section">
			<view class="section-title">
				发布的帖子
			</view>
			
			<view class="post-list" v-if="userPosts.length > 0">
				<view 
					class="post-item" 
					v-for="(post, index) in userPosts" 
					:key="post.id"
					@click="viewPostDetail(post.id)"
				>
					<view class="post-header">
						<view class="post-info">
							<text class="post-title">{{ post.title }}</text>
							<text class="post-time">{{ formatTime(post.createTime) }}</text>
						</view>
					</view>
					<view class="post-summary">
						{{ post.content }}
					</view>
					<view class="post-footer">
						<view class="post-stat">
							<uni-icons type="chat" size="14" color="#999"></uni-icons>
							<text>{{ post.commentCount || 0 }}</text>
						</view>
						<view class="post-stat">
							<uni-icons type="heart" size="14" color="#999"></uni-icons>
							<text>{{ post.likeCount || 0 }}</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-else>
				<image class="empty-image" src="/static/images/empty-post.svg"></image>
				<text class="empty-text">该用户暂未发布任何帖子</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getUserProfile, getUserPosts } from '@/api/forum/forum.js';
import { sendFriendRequest } from '@/api/chat/index.js';

export default {
	data() {
		return {
			userId: null,
			userInfo: {
				id: '',
				username: '',
				avatar: '',
				introduction: '',
				postCount: 0,
				followCount: 0,
				fansCount: 0
			},
			userPosts: [],
			loading: false
		}
	},
	
	onLoad(option) {
		if (option.id) {
			this.userId = option.id;
			this.loadUserProfile();
			this.loadUserPosts();
		} else {
			uni.showToast({
				title: '用户ID不存在',
				icon: 'none',
				duration: 2000,
				success: () => {
					setTimeout(() => {
						uni.navigateBack();
					}, 2000);
				}
			});
		}
	},
	
	methods: {
		// 加载用户资料
		loadUserProfile() {
			uni.showLoading({
				title: '加载中...'
			});
			
			getUserProfile(this.userId).then(response => {
				uni.hideLoading();
				
				const { data } = response;
				if (data) {
					console.log('获取到用户资料:', data);
					
					// 处理可能的头像路径问题
					if (data.avatar && !data.avatar.startsWith('http') && !data.avatar.startsWith('/')) {
						data.avatar = '/' + data.avatar;
					}
					
					this.userInfo = data;
				} else {
					this.handleLoadError('获取用户资料失败');
				}
			}).catch(error => {
				uni.hideLoading();
				console.error('获取用户资料失败:', error);
				this.handleLoadError('获取用户资料失败');
			});
		},
		
		// 加载用户帖子列表
		loadUserPosts() {
			// 调用获取用户帖子列表的API
			uni.showLoading({
				title: '加载中...'
			});
			
			const query = {
				pageNum: 1,
				pageSize: 10
			};
			
			getUserPosts(this.userId, query).then(response => {
				uni.hideLoading();
				
				if (response && response.rows) {
					const rows = response.rows;
					if (rows && rows.length > 0) {
						// 处理帖子数据
						this.userPosts = rows.map(post => {
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
							
							return post;
						});
					} else {
						this.userPosts = [];
					}
				} else {
					this.userPosts = [];
					console.error('获取用户帖子失败:', response);
				}
			}).catch(error => {
				uni.hideLoading();
				console.error('获取用户帖子请求失败:', error);
				this.userPosts = [];
			});
		},
		
		// 发送好友申请按钮点击
		sendFriendRequest() {
			if (!this.currentUserId) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				return;
			}
			
			if (this.currentUserId === this.userId) {
				uni.showToast({
					title: '不能添加自己为好友',
					icon: 'none'
				});
				return;
			}
			
			// 显示确认对话框让用户输入备注
			this.showFriendRequestConfirm();
		},
		
		// 显示好友申请确认对话框
		showFriendRequestConfirm() {
			uni.showModal({
				title: '添加好友',
				content: `请填写好友备注，将发送申请给"${this.userInfo.nickname || this.userInfo.username || '该用户'}"`,
				editable: true,
				placeholderText: '请输入备注名称',
				success: (res) => {
					if (res.confirm) {
						// 确认后发送好友申请，传入用户输入的备注
						this.doSendFriendRequest(res.content);
					}
				}
			});
		},
		
		// 执行发送好友申请
		doSendFriendRequest(contactRemark = '') {
			uni.showLoading({
				title: '发送申请...'
			});
			
			// 参数检查
			if (!this.userId) {
				uni.hideLoading();
				uni.showToast({
					title: '用户ID不能为空',
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
			
			// 使用新的API发送好友申请，使用正确的参数顺序：当前用户ID作为userId，目标用户ID作为friendId
			sendFriendRequest(currentUserId, this.userId, remark, contactRemark)
				.then(res => {
					uni.hideLoading();
					console.log('发送好友申请响应:', res);
					
					if (res.code === 200) {
						uni.showToast({
							title: res.msg || '申请已发送',
							icon: 'success'
						});
						
						// 设置标记，告知聊天页面有新的好友申请
						uni.setStorageSync('has_new_friend_request', true);
					} else if (res.code === 400) {
						// 处理参数错误等情况
						uni.showToast({
							title: res.msg || '申请参数错误',
							icon: 'none'
						});
					} else if (res.code === 401) {
						// 处理未登录情况
						uni.showToast({
							title: '请先登录后再操作',
							icon: 'none'
						});
						// 可以跳转到登录页
						setTimeout(() => {
							uni.navigateTo({
								url: '/pages/login/index'
							});
						}, 1500);
					} else {
						// 处理其他错误
						uni.showToast({
							title: res.msg || '申请失败',
							icon: 'none'
						});
					}
				})
				.catch(error => {
					uni.hideLoading();
					console.error('发送好友申请失败:', error);
					
					// 尝试从错误对象中获取更详细的错误信息
					let errorMsg = '发送申请失败，请稍后再试';
					if (error && error.response && error.response.data && error.response.data.msg) {
						errorMsg = error.response.data.msg;
					} else if (typeof error === 'string') {
						errorMsg = error;
					} else if (error && error.message) {
						errorMsg = error.message;
					}
					
					uni.showToast({
						title: errorMsg,
						icon: 'none'
					});
				});
		},
		
		// 查看帖子详情
		viewPostDetail(postId) {
			uni.navigateTo({
				url: `/pages/forum/post/detail?id=${postId}`
			});
		},
		
		// 格式化时间
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
		
		// 处理加载失败
		handleLoadError(message) {
			uni.showToast({
				title: message,
				icon: 'none',
				duration: 2000,
				success: () => {
					setTimeout(() => {
						uni.navigateBack();
					}, 2000);
				}
			});
		}
	}
}
</script>

<style lang="scss" scoped>
.user-profile-container {
	min-height: 100vh;
	padding-bottom: 30rpx;
	background-color: #f5f5f5;
}

.user-info-section {
	background-color: #fff;
	padding: 40rpx 30rpx;
	margin-bottom: 20rpx;
}

.user-header {
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;
}

.user-avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	margin-right: 30rpx;
}

.user-details {
	flex: 1;
}

.username {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
}

.user-id {
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
}

.user-introduction {
	font-size: 28rpx;
	color: #666;
	line-height: 1.5;
	margin-bottom: 30rpx;
	padding: 0 10rpx;
}

.user-stats {
	display: flex;
	justify-content: space-around;
	padding: 30rpx 0;
	border-top: 1px solid #eee;
	border-bottom: 1px solid #eee;
	margin-bottom: 30rpx;
}

.stat-item {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.stat-value {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.stat-label {
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
}

.user-actions {
	display: flex;
	justify-content: center;
	margin-top: 30rpx;
}

.action-btn {
	width: 80%;
	height: 80rpx;
	line-height: 80rpx;
	font-size: 28rpx;
	text-align: center;
	border-radius: 40rpx;
}

.add-friend-btn {
	background-color: #2196f3;
	color: #fff;
}

.user-posts-section {
	background-color: #fff;
	padding: 30rpx;
	border-radius: 12rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
	padding-left: 10rpx;
	border-left: 4rpx solid #ff6b6b;
}

.post-list {
	padding: 0 10rpx;
}

.post-item {
	padding: 20rpx 0;
	border-bottom: 1px solid #eee;
}

.post-header {
	display: flex;
	justify-content: space-between;
	margin-bottom: 10rpx;
}

.post-info {
	flex: 1;
}

.post-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}

.post-time {
	font-size: 24rpx;
	color: #999;
	margin-left: 20rpx;
}

.post-summary {
	font-size: 28rpx;
	color: #666;
	line-height: 1.5;
	margin-bottom: 10rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.post-footer {
	display: flex;
	justify-content: flex-start;
}

.post-stat {
	display: flex;
	align-items: center;
	margin-right: 30rpx;
}

.post-stat text {
	font-size: 24rpx;
	color: #999;
	margin-left: 4rpx;
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
	margin-bottom: 20rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}
</style>