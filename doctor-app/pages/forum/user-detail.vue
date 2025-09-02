<template>
	<view class="user-detail-container">
		<!-- 导航栏 -->
		<view class="header">
			<view class="back-btn" @click="goBack">
				<uni-icons type="left" size="18" color="#333"></uni-icons>
			</view>
			<view class="title">用户详情</view>
			<view class="right-placeholder"></view>
		</view>
		
		<!-- 用户信息 -->
		<view class="user-info-section">
			<view class="user-basic-info">
				<image class="avatar" :src="userInfo.avatar || '/static/images/profile.jpg'"></image>
				<view class="info-content">
					<text class="username">{{ userInfo.nickname || userInfo.username }}</text>
					<text class="user-role">{{ userInfo.roleName || '普通用户' }}</text>
				</view>
			</view>
			
			<!-- 用户统计信息 -->
			<view class="user-stats">
				<view class="stat-item">
					<text class="stat-count">{{ userInfo.postCount || 0 }}</text>
					<text class="stat-label">帖子</text>
				</view>
				<view class="stat-item">
					<text class="stat-count">{{ userInfo.followCount || 0 }}</text>
					<text class="stat-label">关注</text>
				</view>
				<view class="stat-item">
					<text class="stat-count">{{ userInfo.fansCount || 0 }}</text>
					<text class="stat-label">粉丝</text>
				</view>
			</view>
			
			<!-- 操作按钮区 -->
			<view class="action-buttons">
				<add-friend-button 
					:target-id="userId" 
					:target-name="userInfo.nickname || userInfo.username"
				></add-friend-button>
				
				<view class="follow-btn" @click="toggleFollow">
					<uni-icons :type="isFollowing ? 'star-filled' : 'star'" size="18" :color="isFollowing ? '#ff9500' : '#666'"></uni-icons>
					<text>{{ isFollowing ? '已关注' : '关注' }}</text>
				</view>
			</view>
		</view>
		
		<!-- 用户简介 -->
		<view class="user-bio-section">
			<view class="section-title">个人简介</view>
			<text class="bio-content">{{ userInfo.bio || '这个人很懒，还没有填写个人简介' }}</text>
		</view>
		
		<!-- 用户帖子列表 -->
		<view class="user-posts-section">
			<view class="section-title">发布的帖子</view>
			
			<view class="post-list" v-if="postList.length > 0">
				<view class="post-item" v-for="(post, index) in postList" :key="post.id" @click="navigateToPost(post)">
					<view class="post-title">{{ post.title }}</view>
					<view class="post-info">
						<text class="post-time">{{ formatTime(post.createTime) }}</text>
						<view class="post-stats">
							<text class="view-count">{{ post.viewCount || 0 }}阅读</text>
							<text class="comment-count">{{ post.commentCount || 0 }}评论</text>
						</view>
					</view>
				</view>
			</view>
			
			<view class="empty-state" v-else>
				<image class="empty-image" src="/static/images/empty-posts.svg"></image>
				<text class="empty-text">暂无发布的帖子</text>
			</view>
		</view>
	</view>
</template>

<script>
import { mapGetters } from 'vuex'
import AddFriendButton from '@/components/AddFriendButton.vue'

export default {
	components: {
		AddFriendButton
	},
	data() {
		return {
			userId: '',
			userInfo: {},
			postList: [],
			isFollowing: false,
			loading: false
		}
	},
	computed: {
		...mapGetters(['currentUser'])
	},
	onLoad(options) {
		if (options.userId) {
			this.userId = options.userId
			this.loadUserInfo()
			this.loadUserPosts()
			this.checkFollowStatus()
		} else {
			uni.showToast({
				title: '用户ID不能为空',
				icon: 'none'
			})
			setTimeout(() => {
				this.goBack()
			}, 1500)
		}
	},
	methods: {
		// 加载用户信息
		loadUserInfo() {
			this.loading = true
			// 这里应该调用API获取用户信息
			// 模拟API调用
			setTimeout(() => {
				this.userInfo = {
					id: this.userId,
					username: '用户' + this.userId,
					nickname: '昵称' + this.userId,
					avatar: '/static/images/profile.jpg',
					roleName: '医生会员',
					postCount: 15,
					followCount: 20,
					fansCount: 10,
					bio: '专注于心理健康研究，希望帮助更多人解决心理问题。'
				}
				this.loading = false
			}, 500)
		},
		
		// 加载用户帖子
		loadUserPosts() {
			// 这里应该调用API获取用户帖子
			// 模拟API调用
			setTimeout(() => {
				this.postList = [
					{
						id: 1,
						title: '如何缓解焦虑情绪的有效方法',
						createTime: new Date(Date.now() - 24 * 60 * 60 * 1000),
						viewCount: 120,
						commentCount: 15
					},
					{
						id: 2,
						title: '失眠问题的自我调节技巧分享',
						createTime: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
						viewCount: 85,
						commentCount: 7
					},
					{
						id: 3,
						title: '抑郁症的早期识别与预防',
						createTime: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
						viewCount: 240,
						commentCount: 32
					}
				]
			}, 800)
		},
		
		// 检查关注状态
		checkFollowStatus() {
			// 这里应该调用API检查关注状态
			// 模拟API调用
			setTimeout(() => {
				this.isFollowing = Math.random() > 0.5
			}, 300)
		},
		
		// 切换关注状态
		toggleFollow() {
			// 这里应该调用API关注/取消关注
			this.isFollowing = !this.isFollowing
			uni.showToast({
				title: this.isFollowing ? '已关注' : '已取消关注',
				icon: 'success'
			})
		},
		
		// 导航到帖子详情
		navigateToPost(post) {
			uni.navigateTo({
				url: `/pages/forum/post-detail?id=${post.id}`
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
			
			// 今天的消息只显示时间
			if (date.toDateString() === now.toDateString()) {
				return '今天 ' + date.getHours().toString().padStart(2, '0') + ':' + date.getMinutes().toString().padStart(2, '0')
			}
			
			// 昨天的消息显示"昨天"
			const yesterday = new Date()
			yesterday.setDate(now.getDate() - 1)
			if (date.toDateString() === yesterday.toDateString()) {
				return '昨天 ' + date.getHours().toString().padStart(2, '0') + ':' + date.getMinutes().toString().padStart(2, '0')
			}
			
			// 今年的消息显示月-日
			if (date.getFullYear() === now.getFullYear()) {
				return (date.getMonth() + 1) + '-' + date.getDate() + ' ' + date.getHours().toString().padStart(2, '0') + ':' + date.getMinutes().toString().padStart(2, '0')
			}
			
			// 其他消息显示年-月-日
			return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate()
		}
	}
}
</script>

<style>
.user-detail-container {
	background-color: #f5f5f5;
	min-height: 100vh;
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

.back-btn, .right-placeholder {
	width: 40px;
	height: 40px;
	display: flex;
	align-items: center;
}

.title {
	font-size: 16px;
	font-weight: 500;
	color: #333;
}

.user-info-section {
	background-color: #fff;
	padding: 20px 15px;
	margin-bottom: 10px;
}

.user-basic-info {
	display: flex;
	align-items: center;
	margin-bottom: 15px;
}

.avatar {
	width: 70px;
	height: 70px;
	border-radius: 50%;
	background-color: #eee;
}

.info-content {
	margin-left: 15px;
}

.username {
	font-size: 18px;
	font-weight: 500;
	color: #333;
	margin-bottom: 5px;
	display: block;
}

.user-role {
	font-size: 14px;
	color: #666;
	background-color: #f0f0f0;
	padding: 2px 8px;
	border-radius: 10px;
}

.user-stats {
	display: flex;
	margin-bottom: 15px;
}

.stat-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.stat-count {
	font-size: 18px;
	font-weight: 500;
	color: #333;
	margin-bottom: 5px;
}

.stat-label {
	font-size: 12px;
	color: #999;
}

.action-buttons {
	display: flex;
}

.follow-btn {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #f8f8f8;
	border-radius: 20px;
	padding: 6px 15px;
	margin: 5px 0;
	margin-left: 10px;
}

.follow-btn text {
	font-size: 14px;
	margin-left: 5px;
	color: #333;
}

.user-bio-section, .user-posts-section {
	background-color: #fff;
	padding: 15px;
	margin-bottom: 10px;
}

.section-title {
	font-size: 16px;
	font-weight: 500;
	color: #333;
	margin-bottom: 10px;
	padding-left: 10px;
	border-left: 3px solid #07c160;
}

.bio-content {
	font-size: 14px;
	color: #666;
	line-height: 1.5;
}

.post-item {
	padding: 10px 0;
	border-bottom: 1px solid #f0f0f0;
}

.post-title {
	font-size: 16px;
	color: #333;
	margin-bottom: 5px;
}

.post-info {
	display: flex;
	justify-content: space-between;
	font-size: 12px;
	color: #999;
}

.post-stats {
	display: flex;
}

.view-count, .comment-count {
	margin-left: 10px;
}

.empty-state {
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	padding: 30px 0;
}

.empty-image {
	width: 100px;
	height: 100px;
	margin-bottom: 15px;
}

.empty-text {
	font-size: 14px;
	color: #999;
}
</style> 