<template>
	<view class="post-detail-container">
		<!-- 导航栏 -->
		<view class="header">
			<view class="back-btn" @click="goBack">
				<uni-icons type="left" size="18" color="#333"></uni-icons>
			</view>
			<view class="title">帖子详情</view>
			<view class="more-btn" @click="showMoreOptions">
				<uni-icons type="more-filled" size="20" color="#333"></uni-icons>
			</view>
		</view>
		
		<!-- 帖子内容 -->
		<view class="post-content-section">
			<!-- 帖子标题 -->
			<view class="post-title">{{ postInfo.title }}</view>
			
			<!-- 作者信息 -->
			<view class="post-header">
				<view class="post-author" @tap="showAuthorMenu">
					<image :src="postInfo.authorAvatar || '/static/images/avatar.png'" mode="aspectFill" class="author-avatar"></image>
					<view class="author-name-wrap">
						<view class="author-name">{{postInfo.authorName}}</view>
						<text class="post-date">{{formatTime(postInfo.createTime)}}</text>
					</view>
				</view>
				
				<!-- 添加好友按钮 -->
				<add-friend-button 
					:target-id="postInfo.authorId" 
					:target-name="postInfo.authorName"
				></add-friend-button>
			</view>
			
			<!-- 帖子正文 -->
			<view class="post-body">
				<rich-text :nodes="postInfo.content"></rich-text>
			</view>
			
			<!-- 帖子图片 -->
			<view class="post-images" v-if="postInfo.images && postInfo.images.length > 0">
				<image 
					v-for="(img, index) in postInfo.images" 
					:key="index" 
					:src="img" 
					mode="widthFix" 
					class="post-image"
					@click="previewImage(index)"
				></image>
			</view>
			
			<!-- 标签 -->
			<view class="post-tags" v-if="postInfo.tags && postInfo.tags.length > 0">
				<view class="tag-item" v-for="(tag, index) in postInfo.tags" :key="index">
					{{ tag }}
				</view>
			</view>
			
			<!-- 操作栏 -->
			<view class="action-bar">
				<view class="action-item" @click="toggleLike">
					<uni-icons :type="isLiked ? 'heart-filled' : 'heart'" size="20" :color="isLiked ? '#ff4d4f' : '#666'"></uni-icons>
					<text>{{ postInfo.likeCount || 0 }}</text>
				</view>
				<view class="action-item" @click="focusCommentInput">
					<uni-icons type="chatbubble" size="20" color="#666"></uni-icons>
					<text>{{ postInfo.commentCount || 0 }}</text>
				</view>
				<view class="action-item" @click="toggleFavorite">
					<uni-icons :type="isFavorited ? 'star-filled' : 'star'" size="20" :color="isFavorited ? '#ff9500' : '#666'"></uni-icons>
					<text>{{ postInfo.favoriteCount || 0 }}</text>
				</view>
				<view class="action-item" @click="sharePost">
					<uni-icons type="redo" size="20" color="#666"></uni-icons>
					<text>分享</text>
				</view>
			</view>
		</view>
		
		<!-- 评论区 -->
		<view class="comments-section">
			<view class="section-title">评论 ({{ commentList.length }})</view>
			
			<!-- 评论列表 -->
			<view class="comment-list" v-if="commentList.length > 0">
				<view class="comment-item" v-for="(comment, index) in commentList" :key="comment.id">
					<view class="comment-user" @click="navigateToUser(comment.userId)">
						<image class="avatar" :src="comment.userAvatar || '/static/images/profile.jpg'"></image>
					</view>
					<view class="comment-content">
						<view class="comment-header">
							<text class="username">{{ comment.userName }}</text>
							<text class="time">{{ formatTime(comment.createTime) }}</text>
						</view>
						<view class="comment-body">
							{{ comment.content }}
						</view>
						<view class="comment-actions">
							<view class="comment-action" @click="toggleCommentLike(index)">
								<uni-icons :type="comment.isLiked ? 'heart-filled' : 'heart'" size="14" :color="comment.isLiked ? '#ff4d4f' : '#999'"></uni-icons>
								<text>{{ comment.likeCount || 0 }}</text>
							</view>
							<view class="comment-action" @click="replyComment(comment)">
								<uni-icons type="chatbubble" size="14" color="#999"></uni-icons>
								<text>回复</text>
							</view>
						</view>
						
						<!-- 回复列表 -->
						<view class="reply-list" v-if="comment.replies && comment.replies.length > 0">
							<view class="reply-item" v-for="(reply, replyIndex) in comment.replies" :key="replyIndex">
								<text class="reply-username">{{ reply.userName }}</text>
								<text class="reply-text">{{ reply.content }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 空评论状态 -->
			<view class="empty-comment" v-else>
				<image class="empty-image" src="/static/images/empty-comment.svg"></image>
				<text class="empty-text">暂无评论，快来发表您的看法吧</text>
			</view>
		</view>
		
		<!-- 评论输入框 -->
		<view class="comment-input-section">
			<input 
				type="text" 
				v-model="commentContent" 
				:placeholder="replyTo ? `回复 ${replyTo.userName}` : '发表评论...'" 
				confirm-type="send"
				@confirm="submitComment"
				ref="commentInput"
			/>
			<view class="send-btn" @click="submitComment">发送</view>
		</view>
		
		<!-- 发布者菜单 -->
		<uni-popup ref="authorOptionsPopup" type="bottom">
			<view class="author-options" v-if="showAuthorOptions">
				<view class="author-info">
					<image class="avatar" :src="postInfo.authorAvatar || '/static/images/avatar.png'"></image>
					<text class="name">{{ postInfo.authorName }}</text>
				</view>
				<view class="option-list">
					<view class="option-item" @tap="viewAuthorProfile">
						<text class="option-text">查看个人资料</text>
					</view>
					<view class="option-item" @tap="addFriend">
						<text class="option-text">添加好友</text>
					</view>
				</view>
				<view class="cancel-btn" @tap="closeAuthorOptions">取消</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
import { mapGetters } from 'vuex'
import AddFriendButton from '@/components/AddFriendButton.vue'
import { applyAddFriend } from '@/utils/chat'
import { sendFriendRequest } from '@/api/chat/index'

export default {
	components: {
		AddFriendButton
	},
	data() {
		return {
			postId: null,
			postInfo: {
				title: '这是一个示例帖子标题',
				authorId: '100001',
				authorName: '医生用户',
				authorAvatar: '/static/images/profile.jpg',
				content: '这是帖子内容，可以包含丰富的文本和格式。这里是示例内容，实际应从API获取。',
				images: ['/static/images/sample1.jpg', '/static/images/sample2.jpg'],
				tags: ['心理健康', '医学常识'],
				createTime: new Date(Date.now() - 2 * 60 * 60 * 1000),
				likeCount: 42,
				commentCount: 13,
				favoriteCount: 8
			},
			commentList: [
				{
					id: 1,
					userId: '100002',
					userName: '评论用户1',
					userAvatar: '/static/images/profile.jpg',
					content: '这是一条评论内容，表达对帖子的看法。',
					createTime: new Date(Date.now() - 30 * 60 * 1000),
					likeCount: 5,
					isLiked: false,
					replies: [
						{
							userId: '100001',
							userName: '医生用户',
							content: '感谢您的评论和支持！',
							createTime: new Date(Date.now() - 20 * 60 * 1000)
						}
					]
				},
				{
					id: 2,
					userId: '100003',
					userName: '评论用户2',
					userAvatar: '/static/images/profile.jpg',
					content: '非常有用的帖子，学到了很多知识。',
					createTime: new Date(Date.now() - 40 * 60 * 1000),
					likeCount: 3,
					isLiked: false,
					replies: []
				}
			],
			isLiked: false,
			isFavorited: false,
			commentContent: '',
			replyTo: null,
			loading: false,
			showAuthorOptions: false
		}
	},
	computed: {
		...mapGetters(['userId', 'userInfo'])
	},
	onLoad(options) {
		if (options.id) {
			this.postId = options.id
			// 加载帖子详情
			this.loadPostDetail()
			// 加载评论列表
			this.loadComments()
		} else {
			uni.showToast({
				title: '帖子ID不能为空',
				icon: 'none'
			})
			setTimeout(() => {
				this.goBack()
			}, 1500)
		}
	},
	methods: {
		// 加载帖子详情
		loadPostDetail() {
			// 模拟API调用，实际应该请求服务器
			// 已在data中初始化了示例数据
		},
		
		// 加载评论
		loadComments() {
			// 模拟API调用，实际应该请求服务器
			// 已在data中初始化了示例数据
		},
		
		// 点赞帖子
		toggleLike() {
			this.isLiked = !this.isLiked
			this.postInfo.likeCount += this.isLiked ? 1 : -1
			
			// 这里应该调用API更新点赞状态
			uni.showToast({
				title: this.isLiked ? '已点赞' : '已取消点赞',
				icon: 'success'
			})
		},
		
		// 收藏帖子
		toggleFavorite() {
			this.isFavorited = !this.isFavorited
			this.postInfo.favoriteCount += this.isFavorited ? 1 : -1
			
			// 这里应该调用API更新收藏状态
			uni.showToast({
				title: this.isFavorited ? '已收藏' : '已取消收藏',
				icon: 'success'
			})
		},
		
		// 分享帖子
		sharePost() {
			uni.showActionSheet({
				itemList: ['分享到微信', '复制链接', '分享到朋友圈'],
				success: (res) => {
					uni.showToast({
						title: '分享功能开发中',
						icon: 'none'
					})
				}
			})
		},
		
		// 点赞评论
		toggleCommentLike(index) {
			const comment = this.commentList[index]
			comment.isLiked = !comment.isLiked
			comment.likeCount += comment.isLiked ? 1 : -1
			
			// 这里应该调用API更新评论点赞状态
		},
		
		// 回复评论
		replyComment(comment) {
			this.replyTo = {
				id: comment.id,
				userName: comment.userName
			}
			this.focusCommentInput()
		},
		
		// 聚焦评论输入框
		focusCommentInput() {
			this.$nextTick(() => {
				this.$refs.commentInput.focus()
			})
		},
		
		// 提交评论
		submitComment() {
			if (!this.commentContent.trim()) {
				return
			}
			
			if (!this.userId) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				})
				return
			}
			
			// 模拟提交评论
			const newComment = {
				id: Date.now(),
				userId: this.userId,
				userName: this.userInfo.nickname || this.userInfo.username,
				userAvatar: this.userInfo.avatar,
				content: this.commentContent,
				createTime: new Date(),
				likeCount: 0,
				isLiked: false,
				replies: []
			}
			
			if (this.replyTo) {
				// 回复评论
				const targetComment = this.commentList.find(c => c.id === this.replyTo.id)
				if (targetComment) {
					targetComment.replies.push({
						userId: this.userId,
						userName: this.userInfo.nickname || this.userInfo.username,
						content: this.commentContent,
						createTime: new Date()
					})
				}
			} else {
				// 新评论
				this.commentList.unshift(newComment)
				this.postInfo.commentCount++
			}
			
			// 重置输入框
			this.commentContent = ''
			this.replyTo = null
			
			// 这里应该调用API提交评论
			uni.showToast({
				title: '评论成功',
				icon: 'success'
			})
		},
		
		// 预览图片
		previewImage(index) {
			uni.previewImage({
				current: index,
				urls: this.postInfo.images
			})
		},
		
		// 导航到用户页面
		navigateToUser(userId) {
			uni.navigateTo({
				url: `/pages/forum/user-detail?userId=${userId}`
			})
		},
		
		// 显示更多选项
		showMoreOptions() {
			uni.showActionSheet({
				itemList: ['举报', '分享', '不感兴趣'],
				success: (res) => {
					uni.showToast({
						title: '操作功能开发中',
						icon: 'none'
					})
				}
			})
		},
		
		// 返回上一页
		goBack() {
			uni.navigateBack()
		},
		
		// 格式化时间
		formatTime(time) {
			if (!time) return ''
			
			const date = new Date(time)
			const now = new Date()
			const diff = now - date
			
			// 一分钟内
			if (diff < 60 * 1000) {
				return '刚刚'
			}
			
			// 一小时内
			if (diff < 60 * 60 * 1000) {
				return Math.floor(diff / (60 * 1000)) + '分钟前'
			}
			
			// 一天内
			if (diff < 24 * 60 * 60 * 1000) {
				return Math.floor(diff / (60 * 60 * 1000)) + '小时前'
			}
			
			// 一周内
			if (diff < 7 * 24 * 60 * 60 * 1000) {
				return Math.floor(diff / (24 * 60 * 60 * 1000)) + '天前'
			}
			
			// 超过一周显示日期
			return date.getFullYear() + '/' + (date.getMonth() + 1) + '/' + date.getDate()
		},
		
		// 查看作者资料
		viewAuthorProfile() {
			if (!this.postInfo.authorId) {
				uni.showToast({
					title: '用户信息不存在',
					icon: 'none'
				});
				return;
			}
			
			uni.navigateTo({
				url: `/pages/forum/user-detail?userId=${this.postInfo.authorId}`
			});
			
			this.closeAuthorOptions();
		},
		
		// 显示作者菜单选项
		showAuthorMenu() {
			this.showAuthorOptions = true;
			this.$refs.authorOptionsPopup.open();
		},
		
		// 关闭作者菜单弹窗
		closeAuthorOptions() {
			this.showAuthorOptions = false;
			this.$refs.authorOptionsPopup.close();
		},
		
		// 添加好友
		addFriend() {
			if (!this.userId) {
				// 未登录，跳转到登录页
				uni.navigateTo({
					url: '/pages/user/login'
				});
				return;
			}
			
			if (this.userId === this.postInfo.authorId) {
				uni.showToast({
					title: '不能添加自己为好友',
					icon: 'none'
				});
				return;
			}
			
			// 显示确认对话框
			uni.showModal({
				title: '添加好友',
				content: `确定要添加"${this.postInfo.authorName}"为好友吗？`,
				success: (res) => {
					if (res.confirm) {
						// 发送好友申请
						const remark = `我在论坛看到了您的帖子，想加您为好友`;
						uni.showLoading({ title: '发送中...' });
						
						// 使用applyAddFriend函数发送好友申请
						applyAddFriend(this.userId, this.postInfo.authorId, remark)
							.then(res => {
								uni.hideLoading();
								if (res && res.code === 200) {
									uni.showToast({
										title: '好友请求已发送',
										icon: 'success'
									});
								} else {
									uni.showToast({
										title: res.msg || '发送请求失败',
										icon: 'none'
									});
								}
							})
							.catch(err => {
								uni.hideLoading();
								console.error('发送好友请求失败', err);
								uni.showToast({
									title: '发送好友请求失败',
									icon: 'none'
								});
							});
					}
				}
			});
			
			this.closeAuthorOptions();
		}
	}
}
</script>

<style>
.post-detail-container {
	background-color: #f5f5f5;
	min-height: 100vh;
	padding-bottom: 60px; /* 为底部评论框留出空间 */
}

.header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 10px 15px;
	background-color: #fff;
	border-bottom: 1px solid #ececec;
	height: 50px;
}

.back-btn, .more-btn {
	width: 40px;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: center;
}

.title {
	font-size: 16px;
	font-weight: 500;
	color: #333;
}

.post-content-section {
	background-color: #fff;
	padding: 15px;
	margin-bottom: 10px;
}

.post-title {
	font-size: 18px;
	font-weight: 600;
	color: #333;
	margin-bottom: 15px;
}

.post-header {
	display: flex;
	align-items: center;
	margin-bottom: 15px;
}

.post-author {
	display: flex;
	align-items: center;
}

.author-avatar {
	width: 40px;
	height: 40px;
	border-radius: 50%;
	background-color: #eee;
	margin-right: 10px;
}

.author-name-wrap {
	flex: 1;
}

.author-name {
	font-size: 14px;
	font-weight: 500;
	color: #333;
	display: block;
}

.post-date {
	font-size: 12px;
	color: #999;
}

.post-body {
	font-size: 15px;
	color: #333;
	line-height: 1.6;
	margin-bottom: 15px;
}

.post-images {
	display: flex;
	flex-wrap: wrap;
	margin-bottom: 15px;
}

.post-image {
	width: 100%;
	margin-bottom: 10px;
	border-radius: 4px;
}

.post-tags {
	display: flex;
	flex-wrap: wrap;
	margin-bottom: 15px;
}

.tag-item {
	background-color: #f0f0f0;
	color: #666;
	padding: 2px 8px;
	border-radius: 10px;
	font-size: 12px;
	margin-right: 8px;
	margin-bottom: 8px;
}

.action-bar {
	display: flex;
	justify-content: space-around;
	border-top: 1px solid #f0f0f0;
	padding-top: 10px;
}

.action-item {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.action-item text {
	font-size: 12px;
	color: #666;
	margin-top: 5px;
}

.comments-section {
	background-color: #fff;
	padding: 15px;
}

.section-title {
	font-size: 16px;
	font-weight: 500;
	color: #333;
	margin-bottom: 15px;
}

.comment-item {
	display: flex;
	margin-bottom: 20px;
}

.comment-user {
	margin-right: 10px;
}

.comment-content {
	flex: 1;
}

.comment-header {
	display: flex;
	justify-content: space-between;
	margin-bottom: 5px;
}

.username {
	font-size: 14px;
	font-weight: 500;
	color: #333;
}

.time {
	font-size: 12px;
	color: #999;
}

.comment-body {
	font-size: 14px;
	color: #333;
	margin-bottom: 8px;
}

.comment-actions {
	display: flex;
	margin-bottom: 10px;
}

.comment-action {
	display: flex;
	align-items: center;
	margin-right: 15px;
}

.comment-action text {
	font-size: 12px;
	color: #999;
	margin-left: 5px;
}

.reply-list {
	background-color: #f9f9f9;
	padding: 8px;
	border-radius: 4px;
}

.reply-item {
	font-size: 12px;
	margin-bottom: 5px;
	color: #666;
}

.reply-username {
	color: #007AFF;
	margin-right: 5px;
}

.empty-comment {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30px 0;
}

.empty-image {
	width: 100px;
	height: 100px;
	margin-bottom: 10px;
}

.empty-text {
	font-size: 14px;
	color: #999;
}

.comment-input-section {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	display: flex;
	align-items: center;
	padding: 10px 15px;
	background-color: #fff;
	border-top: 1px solid #ececec;
}

.comment-input-section input {
	flex: 1;
	height: 36px;
	background-color: #f0f0f0;
	border-radius: 18px;
	padding: 0 15px;
	font-size: 14px;
}

.send-btn {
	width: 60px;
	height: 36px;
	background-color: #07c160;
	color: #fff;
	border-radius: 18px;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-left: 10px;
	font-size: 14px;
}

.author-options {
	padding: 15px;
}

.author-info {
	display: flex;
	align-items: center;
	margin-bottom: 15px;
}

.author-info image {
	width: 40px;
	height: 40px;
	border-radius: 50%;
	background-color: #eee;
	margin-right: 10px;
}

.author-info text {
	font-size: 14px;
	font-weight: 500;
	color: #333;
}

.option-list {
	display: flex;
	justify-content: space-between;
	margin-bottom: 15px;
}

.option-item {
	padding: 8px 15px;
	background-color: #f0f0f0;
	border-radius: 10px;
}

.option-item text {
	font-size: 14px;
	color: #333;
}

.cancel-btn {
	width: 100%;
	padding: 8px 15px;
	background-color: #f0f0f0;
	border-radius: 10px;
	text-align: center;
}
</style> 