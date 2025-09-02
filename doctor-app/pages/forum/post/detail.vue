<template>
	<view class="post-detail-container">
		<!-- 帖子内容区 -->
		<view class="post-content-section">
			<view class="post-header">
				<view class="user-info" @click="viewUserProfile(post.userId, post.username)">
					<image class="avatar" :src="post.avatar || '../../../static/images/profile.jpg'"></image>
					<view class="user-details">
						<text class="username">{{ post.username }}</text>
						<text class="post-time">{{ formatTime(post.createTime) }}</text>
					</view>
				</view>
				<view class="category-tag" v-if="post.categoryName">
					{{ post.categoryName }}
				</view>
			</view>
			
			<view class="post-main-content">
				<text class="post-title">{{ post.title }}</text>
				<text class="post-text">{{ post.content }}</text>
				
				<!-- 图片展示 -->
				<view class="post-images" v-if="post.images && post.images.length > 0">
					<image 
						v-for="(img, index) in post.images" 
						:key="index" 
						class="content-image" 
						:src="img" 
						mode="widthFix"
						@click="previewImage(index)"
					></image>
				</view>
			</view>
			
			<view class="post-actions">
				<view class="action-item" @click="toggleLike">
					<uni-icons :type="isLiked ? 'heart-filled' : 'heart'" size="20" :color="isLiked ? '#ff6b6b' : '#666'"></uni-icons>
					<text :class="{active: isLiked}">{{ post.likeCount || 0 }}</text>
				</view>
				<view class="action-item" @click="focusCommentInput">
					<uni-icons type="chat" size="20" color="#666"></uni-icons>
					<text>{{ post.commentCount || 0 }}</text>
				</view>
				<view class="action-item">
					<uni-icons type="eye" size="20" color="#666"></uni-icons>
					<text>{{ post.viewCount || 0 }}</text>
				</view>
				<view class="action-item" @click="sharePost">
					<uni-icons type="redo" size="20" color="#666"></uni-icons>
					<text>分享</text>
				</view>
			</view>
		</view>
		
		<!-- 评论区 -->
		<view class="comments-section">
			<view class="section-title">
				评论 ({{ comments.length }})
			</view>
			
			<!-- 评论列表 -->
			<view class="comment-list" v-if="comments.length > 0">
				<view class="comment-item" v-for="(comment, index) in comments" :key="index">
					<view class="comment-user" @click="viewCommentUserProfile(comment.userId, comment.username)">
						<image class="comment-avatar" :src="comment.avatar || '../../../static/images/profile.jpg'"></image>
						<view class="comment-user-info">
							<text class="comment-username">{{ comment.username }}</text>
							<text class="comment-time">{{ formatTime(comment.createTime) }}</text>
						</view>
					</view>
					<view class="comment-content">
						<text>{{ comment.content }}</text>
					</view>
					<view class="comment-actions">
						<view class="comment-action" @click="likeComment(index)">
							<uni-icons :type="comment.isLiked ? 'heart-filled' : 'heart'" size="16" :color="comment.isLiked ? '#ff6b6b' : '#999'"></uni-icons>
							<text :class="{active: comment.isLiked}">{{ comment.likeCount || 0 }}</text>
						</view>
						<view class="comment-action" @click="replyComment(comment)">
							<uni-icons type="chat" size="16" color="#999"></uni-icons>
							<text>回复</text>
						</view>
					</view>
					
					<!-- 回复列表 -->
					<view class="reply-list" v-if="comment.replies && comment.replies.length > 0">
						<view class="reply-item" v-for="(reply, replyIndex) in comment.replies" :key="replyIndex">
							<view class="reply-user" @click="viewCommentUserProfile(reply.userId, reply.username)">
								<image class="reply-avatar" :src="reply.avatar || '../../../static/images/profile.jpg'"></image>
								<view class="reply-user-info">
									<text class="reply-username">{{ reply.username }}</text>
									<text class="reply-time">{{ formatTime(reply.createTime) }}</text>
								</view>
							</view>
							<view class="reply-content">
								<text v-if="reply.replyTo">回复 <text class="reply-to">@{{ reply.replyTo }}</text>：</text>
								<text>{{ reply.content }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 空评论状态 -->
			<view class="empty-comments" v-else>
				<image class="empty-icon" src="../../../static/images/empty-comment.svg"></image>
				<text class="empty-text">暂无评论，快来发表第一条评论吧</text>
			</view>
		</view>
		
		<!-- 评论输入框 -->
		<view class="comment-input-section">
			<view class="input-container" :class="{focused: isInputFocused}">
				<input 
					type="text" 
					v-model="commentText" 
					:placeholder="replyTarget ? `回复 @${replyTarget.username}` : '写下你的评论...'"
					@focus="isInputFocused = true"
					@blur="isInputFocused = false"
					confirm-type="send"
					@confirm="submitComment"
					ref="commentInput"
				/>
				<view class="send-btn" :class="{active: commentText.trim().length > 0}" @click="submitComment">
					发送
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getForumPostDetail, getForumComments, likeForumPost, unlikeForumPost, 
        likeForumComment, unlikeForumComment, createForumComment, replyForumComment } from '@/api/forum/forum.js';


export default {
	data() {
		return {
			postId: null,
			post: {
				id: '',
				username: '',
				avatar: '',
				title: '',
				content: '',
				categoryName: '',
				createTime: '',
				likeCount: 0,
				commentCount: 0,
				viewCount: 0,
				images: []
			},
			comments: [],
			isLiked: false,
			commentText: '',
			isInputFocused: false,
			replyTarget: null,
			loading: false,
			pagination: {
				pageNum: 1,
				pageSize: 20
			},
			noMoreComments: false
		}
	},
	
	onLoad(option) {
		if (option.id) {
			this.postId = option.id;
			this.fetchPostDetail();
			this.fetchComments();
		}
	},
	
	methods: {
		// 获取帖子详情
		fetchPostDetail() {
			uni.showLoading({
				title: '加载中...'
			});
			
			getForumPostDetail(this.postId).then(response => {
				uni.hideLoading();
				const { data } = response;
				
				if (data) {
					console.log('获取到帖子详情:', data);
					
					// 处理可能的头像路径问题
					if (data.avatar && !data.avatar.startsWith('http') && !data.avatar.startsWith('/')) {
						data.avatar = '/' + data.avatar;
					}
					
					// 处理图片路径
					if (data.images && data.images.length > 0) {
						data.images = data.images.map(img => {
							if (!img.startsWith('http') && !img.startsWith('/')) {
								return '/' + img;
							}
							return img;
						});
					}
					
					this.post = data;
					
					// 检查当前用户是否已点赞
					this.checkIfLiked();
					
					// 设置标记，表示查看过新帖子，返回列表时需要刷新
					try {
						uni.setStorageSync('forum_need_refresh', 'true');
					} catch (e) {
						console.error('设置刷新标记失败:', e);
					}
				} else {
					this.handleFetchError('帖子不存在或已被删除');
				}
			}).catch(error => {
				uni.hideLoading();
				console.error('获取帖子详情失败:', error);
				this.handleFetchError('获取帖子详情失败');
			});
		},
		
		// 获取评论列表
		fetchComments() {
			if (this.loading || this.noMoreComments) {
				return;
			}
			
			this.loading = true;
			
			const params = {
				pageNum: this.pagination.pageNum,
				pageSize: this.pagination.pageSize
			};
			
			getForumComments(this.postId, params).then(response => {
				const { data } = response;
				
				if (data && data.length > 0) {
					// 获取已点赞的评论ID列表，用于标记点赞状态
					const likedComments = uni.getStorageSync('likedComments') || [];
					
					// 处理可能的头像路径问题
					data.forEach(comment => {
						if (comment.avatar && !comment.avatar.startsWith('http') && !comment.avatar.startsWith('/')) {
							comment.avatar = '/' + comment.avatar;
						}
						
						// 设置评论的点赞状态
						comment.isLiked = likedComments.includes(comment.id);
						
						// 处理children字段，将其映射到replies字段
						if (comment.children && comment.children.length > 0) {
							comment.replies = comment.children;
							comment.replies.forEach(reply => {
								if (reply.avatar && !reply.avatar.startsWith('http') && !reply.avatar.startsWith('/')) {
									reply.avatar = '/' + reply.avatar;
								}
							});
						} else if (comment.replies && comment.replies.length > 0) {
							// 兼容已有的replies字段
							comment.replies.forEach(reply => {
								if (reply.avatar && !reply.avatar.startsWith('http') && !reply.avatar.startsWith('/')) {
									reply.avatar = '/' + reply.avatar;
								}
							});
						}
					});
					
					// 如果是第一页，直接替换评论数组
					if (this.pagination.pageNum === 1) {
						this.comments = data;
					} else {
						// 否则追加到评论数组
						this.comments = [...this.comments, ...data];
					}
					
					this.pagination.pageNum++;
					
					if (data.length < this.pagination.pageSize) {
						this.noMoreComments = true;
					}
				} else {
					this.noMoreComments = true;
				}
			}).catch(error => {
				console.error('获取评论失败', error);
				uni.showToast({
					title: '获取评论失败',
					icon: 'none'
				});
			}).finally(() => {
				this.loading = false;
			});
		},
		
		// 检查当前用户是否已点赞帖子
		checkIfLiked() {
			// 实际中应该从服务器获取该信息
			// 这里暂时使用本地存储模拟
			const likedPosts = uni.getStorageSync('likedPosts') || [];
			this.isLiked = likedPosts.includes(this.postId);
		},
		
		// 切换点赞状态
		toggleLike() {
			if (!this.isLiked) {
				// 立即更新UI状态
				this.isLiked = true;
				this.post.likeCount += 1;
				
				// 更新本地存储
				const likedPosts = uni.getStorageSync('likedPosts') || [];
				if (!likedPosts.includes(this.postId)) {
					likedPosts.push(this.postId);
					uni.setStorageSync('likedPosts', likedPosts);
				}
				
				// 点赞
				likeForumPost(this.postId).then(response => {
					const { data } = response;
					if (data && data.success) {
						// 如果服务器返回了点赞数，以服务器为准
						if (data.likeCount !== undefined) {
							this.post.likeCount = data.likeCount;
						}
						
						uni.showToast({
							title: '点赞成功',
							icon: 'none'
						});
					}
				}).catch(error => {
					console.error('点赞失败', error);
					// 回滚UI状态
					this.isLiked = false;
					this.post.likeCount -= 1;
					
					// 回滚本地存储
					let likedPosts = uni.getStorageSync('likedPosts') || [];
					likedPosts = likedPosts.filter(id => id !== this.postId);
					uni.setStorageSync('likedPosts', likedPosts);
					
					uni.showToast({
						title: '点赞失败',
						icon: 'none'
					});
				});
			} else {
				// 检查本地存储，避免重复操作
				const likedPosts = uni.getStorageSync('likedPosts') || [];
				if (!likedPosts.includes(this.postId)) {
					console.log('本地存储中没有该帖子的点赞记录，无需取消');
					return;
				}
				
				// 立即更新UI状态
				this.isLiked = false;
				this.post.likeCount = Math.max(0, this.post.likeCount - 1);
				
				// 更新本地存储
				let updatedLikedPosts = likedPosts.filter(id => id !== this.postId);
				uni.setStorageSync('likedPosts', updatedLikedPosts);
				
				// 取消点赞
				unlikeForumPost(this.postId).then(response => {
					const { data } = response;
					if (data && data.success) {
						// 如果服务器返回了点赞数，以服务器为准
						if (data.likeCount !== undefined) {
							this.post.likeCount = data.likeCount;
						}
						
						uni.showToast({
							title: '已取消点赞',
							icon: 'none'
						});
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
						this.isLiked = true;
						this.post.likeCount += 1;
						
						// 回滚本地存储
						const likedPosts = uni.getStorageSync('likedPosts') || [];
						if (!likedPosts.includes(this.postId)) {
							likedPosts.push(this.postId);
							uni.setStorageSync('likedPosts', likedPosts);
						}
						
						// 显示错误提示
						uni.showToast({
							title: '取消点赞失败',
							icon: 'none'
						});
					}
				});
			}
		},
		
		// 评论点赞
		likeComment(index) {
			const comment = this.comments[index];
			
			if (comment.isLiked) {
				// 保存评论ID，用于判断是否真正需要取消点赞
				const commentId = comment.id;
				
				// 获取已点赞的评论ID列表
				const likedComments = uni.getStorageSync('likedComments') || [];
				if (!likedComments.includes(commentId)) {
					console.log('本地存储中没有该评论的点赞记录，无需取消');
					return;
				}
				
				// 立即更新UI状态
				this.$set(this.comments[index], 'isLiked', false);
				this.$set(this.comments[index], 'likeCount', Math.max(0, comment.likeCount - 1));
				
				// 更新本地存储
				const updatedLikedComments = likedComments.filter(id => id !== commentId);
				uni.setStorageSync('likedComments', updatedLikedComments);
				
				// 取消点赞
				unlikeForumComment(commentId).then(response => {
					const { data } = response;
					if (data && data.success) {
						// 如果服务器返回了点赞数，以服务器为准
						if (data.likeCount !== undefined) {
							this.$set(this.comments[index], 'likeCount', data.likeCount);
						}
					}
				}).catch(error => {
					console.error('取消点赞评论失败', error);
					
					// 只有当错误不是唯一键冲突时才回滚UI状态
					if (!error || !error.message || !(
						error.message.includes('Duplicate entry') || 
						error.message.includes('DuplicateKeyException') ||
						error.message.includes('uk_type_target_user')
					)) {
						// 回滚UI状态
						this.$set(this.comments[index], 'isLiked', true);
						this.$set(this.comments[index], 'likeCount', comment.likeCount + 1);
						
						// 回滚本地存储
						const likedComments = uni.getStorageSync('likedComments') || [];
						if (!likedComments.includes(commentId)) {
							likedComments.push(commentId);
							uni.setStorageSync('likedComments', likedComments);
						}
						
						// 显示错误提示
						uni.showToast({
							title: '取消点赞失败',
							icon: 'none'
						});
					}
				});
			} else {
				// 立即更新UI状态
				this.$set(this.comments[index], 'isLiked', true);
				this.$set(this.comments[index], 'likeCount', (comment.likeCount || 0) + 1);
				
				// 更新本地存储
				const likedComments = uni.getStorageSync('likedComments') || [];
				const commentId = comment.id;
				if (!likedComments.includes(commentId)) {
					likedComments.push(commentId);
					uni.setStorageSync('likedComments', likedComments);
				}
				
				// 点赞
				likeForumComment(comment.id).then(response => {
					const { data } = response;
					if (data && data.success) {
						// 如果服务器返回了点赞数，以服务器为准
						if (data.likeCount !== undefined) {
							this.$set(this.comments[index], 'likeCount', data.likeCount);
						}
					}
				}).catch(error => {
					console.error('点赞评论失败', error);
					// 回滚UI状态
					this.$set(this.comments[index], 'isLiked', false);
					this.$set(this.comments[index], 'likeCount', Math.max(0, comment.likeCount));
					
					// 回滚本地存储
					const likedComments = uni.getStorageSync('likedComments') || [];
					const updatedLikedComments = likedComments.filter(id => id !== commentId);
					uni.setStorageSync('likedComments', updatedLikedComments);
					
					// 显示错误提示
					uni.showToast({
						title: '点赞失败',
						icon: 'none'
					});
				});
			}
		},
		
		// 回复评论
		replyComment(comment) {
			this.replyTarget = comment;
			this.focusCommentInput();
		},
		
		// 聚焦评论输入框
		focusCommentInput() {
			this.$nextTick(() => {
				this.$refs.commentInput.focus();
			});
		},
		
		// 提交评论
		submitComment() {
			if (!this.commentText.trim()) {
				uni.showToast({
					title: '评论内容不能为空',
					icon: 'none'
				});
				return;
			}
			
			// 获取当前用户信息
			const userInfo = uni.getStorageSync('userInfo') || {};
			
			const commentData = {
				postId: this.postId,
				content: this.commentText,
				userId: userInfo.id || 1, // 默认用户ID为1，实际使用时应获取当前登录用户ID
				username: userInfo.username || '用户' + Math.floor(Math.random() * 10000), // 随机用户名，实际使用时应获取当前登录用户名
				avatar: userInfo.avatar || '/static/images/profile.jpg'
			};
			
			if (this.replyTarget) {
				// 回复评论
				commentData.parentId = this.replyTarget.id;
				commentData.replyUserId = this.replyTarget.userId;
				commentData.replyTo = this.replyTarget.username;
				
				replyForumComment(commentData).then(response => {
					this.handleCommentSubmitSuccess(response, true);
				}).catch(this.handleCommentSubmitError);
			} else {
				// 发表评论
				createForumComment(commentData).then(response => {
					this.handleCommentSubmitSuccess(response, false);
				}).catch(this.handleCommentSubmitError);
			}
		},
		
		// 处理评论提交成功
		handleCommentSubmitSuccess(response, isReply) {
			const { data } = response;
			if (data && data.success) {
				uni.showToast({
					title: isReply ? '回复成功' : '评论成功',
					icon: 'success'
				});
				
				// 清空评论文本和回复目标
				this.commentText = '';
				this.replyTarget = null;
				
				// 重新加载评论
				this.pagination.pageNum = 1;
				this.noMoreComments = false;
				this.fetchComments();
				
				// 更新帖子评论数
				this.post.commentCount++;
			}
		},
		
		// 处理评论提交失败
		handleCommentSubmitError(error) {
			console.error('提交评论失败', error);
			uni.showToast({
				title: '提交失败，请重试',
				icon: 'none'
			});
		},
		
		// 预览图片
		previewImage(index) {
			uni.previewImage({
				urls: this.post.images,
				current: this.post.images[index]
			});
		},
		
		// 分享帖子
		sharePost() {
			uni.share({
				provider: 'weixin',
				scene: 'WXSceneSession',
				type: 0,
				title: this.post.title,
				summary: this.post.content.substring(0, 100) + '...',
				imageUrl: this.post.images && this.post.images.length > 0 ? this.post.images[0] : '',
				href: 'pages/forum/post/detail?id=' + this.postId,
				success: function(res) {
					console.log('分享成功', res);
				},
				fail: function(err) {
					console.error('分享失败', err);
				}
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
		
		// 处理获取详情失败
		handleFetchError(message) {
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
		},
		
		// 查看用户个人资料（发帖人）
		viewUserProfile(userId, username) {
			if (!userId) {
				uni.showToast({
					title: '用户ID不存在',
					icon: 'none'
				});
				return;
			}
			
			// 直接发起好友申请
			this.doSendFriendRequest(userId, username);
		},
		
		// 查看评论用户个人资料
		viewCommentUserProfile(userId, username) {
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
		
		// 显示好友申请确认对话框
		showFriendRequestConfirm(userId, username) {
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
		
		// 发送好友申请
		doSendFriendRequest(userId, username, contactRemark = '') {
			uni.showLoading({
				title: '发送申请...'
			});
			
			// 检查目标用户 ID 是否有效
			if (!userId) {
				uni.hideLoading();
				uni.showToast({
					title: '无效的目标用户ID',
					icon: 'none'
				});
				return; // 阻止继续执行
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
			
			// 使用更新后的API函数，传入正确的参数顺序：当前用户ID作为userId，目标用户ID作为friendId
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
		
		// 添加好友操作
		addFriendAction(userId, username) {
			// 显示确认对话框
			this.showFriendRequestConfirm(userId, username);
		},
		
		// 发起聊天（已弃用）
		startChat(userId, username) {
			// 现在需要先添加好友，所以这个方法不再使用
			uni.showToast({
				title: '请先添加好友',
				icon: 'none'
			});
		}
	},
	
	onReachBottom() {
		// 加载更多评论
		if (!this.noMoreComments) {
			this.fetchComments();
		}
	}
}
</script>

<style lang="scss" scoped>
.post-detail-container {
	padding-bottom: 100rpx;
	background-color: #f5f5f5;
}

.post-content-section {
	background-color: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
	border-radius: 12rpx;
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
	margin-right: 20rpx;
}

.user-details {
	display: flex;
	flex-direction: column;
}

.username {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.post-time {
	font-size: 24rpx;
	color: #999;
	margin-top: 4rpx;
}

.category-tag {
	padding: 6rpx 16rpx;
	background-color: #e6f7ff;
	color: #1890ff;
	border-radius: 20rpx;
	font-size: 24rpx;
}

.post-main-content {
	margin-bottom: 30rpx;
}

.post-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 20rpx;
	display: block;
}

.post-text {
	font-size: 30rpx;
	color: #333;
	line-height: 1.6;
	margin-bottom: 20rpx;
	display: block;
	white-space: pre-wrap;
}

.post-images {
	display: flex;
	flex-wrap: wrap;
	margin: 0 -10rpx;
}

.content-image {
	width: calc(33.33% - 20rpx);
	margin: 10rpx;
	border-radius: 8rpx;
}

.post-actions {
	display: flex;
	justify-content: space-around;
	padding-top: 20rpx;
	border-top: 1px solid #eee;
}

.action-item {
	display: flex;
	align-items: center;
	padding: 10rpx 20rpx;
}

.action-item text {
	margin-left: 8rpx;
	font-size: 26rpx;
	color: #666;
}

.action-item text.active {
	color: #ff6b6b;
}

.comments-section {
	background-color: #fff;
	padding: 30rpx;
	border-radius: 12rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
}

.comment-list {
	padding-bottom: 20rpx;
}

.comment-item {
	padding: 20rpx 0;
	border-bottom: 1px solid #eee;
}

.comment-user {
	display: flex;
	align-items: center;
	margin-bottom: 10rpx;
}

.comment-avatar {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	margin-right: 16rpx;
}

.comment-user-info {
	display: flex;
	flex-direction: column;
}

.comment-username {
	font-size: 26rpx;
	font-weight: bold;
	color: #333;
}

.comment-time {
	font-size: 22rpx;
	color: #999;
	margin-top: 4rpx;
}

.comment-content {
	font-size: 28rpx;
	color: #333;
	line-height: 1.5;
	margin: 10rpx 0;
	padding-left: 76rpx;
}

.comment-actions {
	display: flex;
	padding-left: 76rpx;
}

.comment-action {
	display: flex;
	align-items: center;
	margin-right: 30rpx;
}

.comment-action text {
	margin-left: 6rpx;
	font-size: 24rpx;
	color: #999;
}

.comment-action text.active {
	color: #ff6b6b;
}

.reply-list {
	margin-top: 20rpx;
	padding: 20rpx;
	background-color: #f9f9f9;
	border-radius: 8rpx;
	margin-left: 76rpx;
}

.reply-item {
	padding: 10rpx 0;
}

.reply-item:not(:last-child) {
	border-bottom: 1px solid #eee;
	padding-bottom: 20rpx;
	margin-bottom: 20rpx;
}

.reply-user {
	display: flex;
	align-items: center;
	margin-bottom: 10rpx;
}

.reply-avatar {
	width: 50rpx;
	height: 50rpx;
	border-radius: 50%;
	margin-right: 12rpx;
}

.reply-user-info {
	display: flex;
	flex-direction: column;
}

.reply-username {
	font-size: 24rpx;
	font-weight: bold;
	color: #333;
}

.reply-time {
	font-size: 20rpx;
	color: #999;
	margin-top: 2rpx;
}

.reply-content {
	font-size: 26rpx;
	color: #333;
	line-height: 1.5;
	margin-left: 62rpx;
}

.reply-to {
	color: #1890ff;
	font-weight: bold;
}

.empty-comments {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 60rpx 0;
}

.empty-icon {
	width: 200rpx;
	height: 200rpx;
	margin-bottom: 20rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}

.comment-input-section {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background-color: #fff;
	padding: 20rpx 30rpx;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	z-index: 100;
}

.input-container {
	display: flex;
	align-items: center;
	background-color: #f5f5f5;
	border-radius: 36rpx;
	padding: 0 20rpx;
	transition: all 0.3s;
}

.input-container.focused {
	background-color: #fff;
	border: 1px solid #1890ff;
}

.input-container input {
	flex: 1;
	height: 72rpx;
	font-size: 28rpx;
	color: #333;
}

.send-btn {
	padding: 0 20rpx;
	height: 60rpx;
	line-height: 60rpx;
	font-size: 28rpx;
	color: #999;
}

.send-btn.active {
	color: #1890ff;
	font-weight: bold;
}
</style>