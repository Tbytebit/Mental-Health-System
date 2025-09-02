<template>
	<view class="ai-chat-container">
		<!-- 页面标题 -->
		<view class="header">
			<view class="title">AI助手</view>
		</view>
		
		<!-- AI助手类型选择器 -->
		<view class="ai-types">
			<scroll-view class="ai-type-scroll" scroll-x="true" show-scrollbar="false">
				<view 
					v-for="(item, index) in aiAssistants" 
					:key="index" 
					class="ai-type-item" 
					:class="{ active: currentAssistant === index }"
					@click="selectAssistant(index)"
				>
					<image class="ai-avatar" :src="item.avatar" mode="aspectFill"></image>
					<text class="ai-name">{{ item.name }}</text>
				</view>
			</scroll-view>
		</view>
		
		<!-- 当前AI助手简介 -->
		<view class="ai-intro">
			<view class="ai-profile">
				<image class="ai-big-avatar" :src="aiAssistants[currentAssistant].avatar" mode="aspectFill"></image>
				<view class="ai-info">
					<text class="ai-title">{{ aiAssistants[currentAssistant].name }}</text>
					<text class="ai-desc">{{ aiAssistants[currentAssistant].description }}</text>
				</view>
			</view>
			<view class="ai-actions">
				<button class="start-chat-btn" @click="startNewChat">开始对话</button>
			</view>
		</view>
		
		<!-- 聊天历史记录 -->
		<view class="chat-history" v-if="chatHistory.length > 0">
			<view class="section-title">
				<text>历史对话</text>
				<text class="clear-history" @click="confirmClearHistory">清空历史</text>
			</view>
			<view class="history-list">
				<view 
					class="history-item" 
					v-for="(item, index) in chatHistory" 
					:key="index"
				>
					<view class="history-left" @click="openChat(item)">
						<image class="history-avatar" :src="aiAssistants[item.assistantType].avatar" mode="aspectFill"></image>
						<view class="history-info">
							<text class="history-title">{{ item.title || '新对话' }}</text>
							<text class="history-time">{{ formatTime(item.lastMessageTime) }}</text>
						</view>
					</view>
					<view class="history-actions" @click.stop="deleteHistory(item)">
						<uni-icons type="trash" size="20" color="#999"></uni-icons>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<image class="empty-image" :src="require('@/static/images/ai-empty-state.png')" mode="aspectFit"></image>
			<text class="empty-text">开始与AI助手对话，获取心理健康支持</text>
		</view>
	</view>
</template>

<script>
	import { getAIAssistantList, getAIChatHistory, createAIChat, deleteAIChat, clearAllChatHistory } from '@/api/ai';
	import request from '@/utils/request';
	
	export default {
		data() {
			return {
				currentAssistant: 0,
				aiAssistants: [
					{
						id: 1,
						name: '心理顾问',
						avatar: require('@/static/images/ai-advisor.svg'),
						description: '专业的心理健康顾问，可以为您提供情绪管理和心理健康建议。'
					},
					{
						id: 2,
						name: '冥想助手',
						avatar: require('@/static/images/ai-meditation.svg'),
						description: '帮助您进行冥想练习，缓解压力，改善睡眠质量。'
					},
					{
						id: 3,
						name: '情绪分析师',
						avatar: require('@/static/images/ai-emotion.svg'),
						description: '分析您的情绪状态，帮助您更好地理解自己的感受。'
					},
					{
						id: 4,
						name: '生活教练',
						avatar: require('@/static/images/ai-coach.svg'),
						description: '为您提供生活建议和目标设定，帮助您建立健康的生活方式。'
					}
				],
				chatHistory: []
			}
		},
		onShow() {
			this.loadAIAssistants();
			this.loadChatHistory();
			
			// 添加事件监听，处理从聊天室传递过来的新建聊天请求
			uni.$on('create_new_chat', this.handleCreateNewChat);
		},
		onHide() {
			// 取消事件监听
			uni.$off('create_new_chat', this.handleCreateNewChat);
		},
		methods: {
			loadAIAssistants() {
				// 从API获取AI助手列表
				getAIAssistantList().then(res => {
					if (res.code === 200 && res.data && res.data.length > 0) {
						// 替换示例数据
						this.aiAssistants = res.data;
					}
				}).catch(err => {
					console.error('获取AI助手列表失败', err);
					// 保留示例数据作为备用
				});
			},
			loadChatHistory() {
				// 从API获取聊天历史
				getAIChatHistory().then(res => {
					if (res.code === 200 && res.data) {
						this.chatHistory = res.data;
					} else {
						// 示例数据
						this.chatHistory = [
							{
								id: 1,
								assistantType: 0,
								title: '如何缓解焦虑',
								lastMessageTime: new Date(new Date().getTime() - 3600000 * 2)
							},
							{
								id: 2,
								assistantType: 1,
								title: '睡前冥想指导',
								lastMessageTime: new Date(new Date().getTime() - 3600000 * 24)
							}
						];
					}
				}).catch(err => {
					console.error('获取聊天历史失败', err);
					// 示例数据
					this.chatHistory = [
						{
							id: 1,
							assistantType: 0,
							title: '如何缓解焦虑',
							lastMessageTime: new Date(new Date().getTime() - 3600000 * 2)
						},
						{
							id: 2,
							assistantType: 1,
							title: '睡前冥想指导',
							lastMessageTime: new Date(new Date().getTime() - 3600000 * 24)
						}
					];
				});
			},
			selectAssistant(index) {
				this.currentAssistant = index;
			},
			startNewChat() {
				// 创建新聊天
				const assistantId = this.aiAssistants[this.currentAssistant].id;
				
				createAIChat({
					assistantId: assistantId,
					assistantType: this.currentAssistant
				}).then(res => {
					if (res.code === 200 && res.data) {
						uni.navigateTo({
							url: `/pages/ai/room?id=${res.data.chatId}&type=${this.currentAssistant}`
						});
					} else {
						// 如果API失败，直接跳转，后端处理创建
						uni.navigateTo({
							url: `/pages/ai/room?type=${this.currentAssistant}`
						});
					}
				}).catch(err => {
					console.error('创建聊天失败', err);
					// 如果API失败，直接跳转，后端处理创建
					uni.navigateTo({
						url: `/pages/ai/room?type=${this.currentAssistant}`
					});
				});
			},
			openChat(item) {
				// 使用chatId字段，如果不存在则回退到id字段
				const chatId = item.chatId || item.id;
				
				// 确保chatId有效
				if (!chatId) {
					uni.showToast({
						title: '无效的聊天ID',
						icon: 'none'
					});
					return;
				}
				
				// 先清除本地缓存，确保页面能加载最新数据
				try {
					uni.removeStorageSync('cachedMessages_' + chatId);
					console.log('已清除缓存:', 'cachedMessages_' + chatId);
				} catch (e) {
					console.error('清除缓存失败:', e);
				}
				
				// 导航到聊天页面
				uni.navigateTo({
					url: `/pages/ai/room?id=${chatId}&type=${item.assistantType}`
				});
			},
			deleteHistory(item) {
				// 阻止冒泡，防止点击事件传递到父元素
				event && event.stopPropagation();
				
				uni.showModal({
					title: '删除确认',
					content: '确定要删除这个对话吗？',
					success: (res) => {
						if (res.confirm) {
							// 确保ID有效，使用chatId或id字段
							const chatId = item.chatId || item.id;
							if (!chatId) {
								uni.showToast({
									title: '无效的会话ID',
									icon: 'none'
								});
								return;
							}
							
							// 添加加载状态提示
							uni.showLoading({
								title: '正在删除...'
							});
							
							// 调用删除API
							deleteAIChat(chatId).then(res => {
								uni.hideLoading();
								
								if (res.code === 200) {
									// 成功后从本地列表中删除
									this.chatHistory = this.chatHistory.filter(chat => (chat.chatId || chat.id) !== chatId);
									
									// 清除该聊天的本地缓存
									try {
										// 删除关联的缓存
										const cacheKey = 'cachedMessages_' + chatId;
										uni.removeStorageSync(cacheKey);
										console.log('已清除缓存:', cacheKey);
									} catch (e) {
										console.error('清除缓存失败:', e);
									}
									
									uni.showToast({
										title: '删除成功',
										icon: 'success'
									});
								} else {
									uni.showToast({
										title: res.msg || '删除失败',
										icon: 'none'
									});
									console.error('删除失败，后端返回:', res);
								}
							}).catch(err => {
								uni.hideLoading();
								console.error('删除失败:', err);
								uni.showToast({
									title: '删除失败',
									icon: 'none'
								});
							});
						}
					}
				});
			},
			confirmClearHistory() {
				uni.showModal({
					title: '清空确认',
					content: '确定要清空所有对话历史吗？此操作不可恢复。',
					success: (res) => {
						if (res.confirm) {
							uni.showLoading({
								title: '正在清空...'
							});
							
							// 调用清空所有历史记录API
							clearAllChatHistory().then(res => {
								uni.hideLoading();
								
								if (res.code === 200) {
									// 清空本地列表
									this.chatHistory = [];
									
									// 清除所有聊天的本地缓存
									try {
										// 获取所有本地存储的key
										uni.getStorageInfo({
											success: (storageInfo) => {
												const keys = storageInfo.keys;
												// 删除所有缓存的消息
												keys.forEach(key => {
													if (key.startsWith('cachedMessages_')) {
														uni.removeStorageSync(key);
														console.log('已清除缓存:', key);
													}
												});
											}
										});
									} catch (e) {
										console.error('清除缓存失败:', e);
									}
									
									uni.showToast({
										title: '已清空所有历史',
										icon: 'success'
									});
									
									// 强制刷新页面，确保数据被正确清空
									setTimeout(() => {
										this.loadChatHistory();
									}, 500);
								} else {
									uni.showToast({
										title: res.msg || '清空失败',
										icon: 'none'
									});
									console.error('清空历史失败，后端返回:', res);
								}
							}).catch(err => {
								uni.hideLoading();
								console.error('清空历史失败:', err);
								uni.showToast({
									title: '清空历史失败',
									icon: 'none'
								});
							});
						}
					}
				});
			},
			formatTime(time) {
				const date = new Date(time);
				const now = new Date();
				
				// 今天
				if (date.toDateString() === now.toDateString()) {
					return this.formatHourMinute(date);
				}
				
				// 昨天
				const yesterday = new Date(now);
				yesterday.setDate(now.getDate() - 1);
				if (date.toDateString() === yesterday.toDateString()) {
					return '昨天 ' + this.formatHourMinute(date);
				}
				
				// 一周内
				const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
				const dayDiff = Math.floor((now - date) / (24 * 3600 * 1000));
				if (dayDiff < 7) {
					return weekDays[date.getDay()];
				}
				
				// 更早
				return `${date.getMonth() + 1}月${date.getDate()}日`;
			},
			formatHourMinute(date) {
				return date.getHours().toString().padStart(2, '0') + ':' + 
					date.getMinutes().toString().padStart(2, '0');
			},
			// 处理从聊天室传递过来的新建聊天请求
			handleCreateNewChat(data) {
				if (data && data.assistantType !== undefined) {
					this.currentAssistant = data.assistantType;
					this.$nextTick(() => {
						this.startNewChat();
					});
				}
			}
		}
	}
</script>

<style>
.ai-chat-container {
	padding: 0 0 30rpx 0;
	background-color: #f8f8f8;
	min-height: 100vh;
}

.header {
	padding: 20rpx 30rpx;
	background-color: #ffffff;
}

.title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
}

.ai-types {
	background-color: #ffffff;
	padding: 20rpx 0;
	margin-bottom: 20rpx;
}

.ai-type-scroll {
	white-space: nowrap;
	padding: 0 20rpx;
}

.ai-type-item {
	display: inline-flex;
	flex-direction: column;
	align-items: center;
	margin-right: 30rpx;
	padding: 10rpx;
	opacity: 0.7;
	transition: all 0.3s;
}

.ai-type-item.active {
	opacity: 1;
	transform: scale(1.05);
}

.ai-avatar {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	border: 4rpx solid #f0f0f0;
	box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.1);
}

.ai-type-item.active .ai-avatar {
	border-color: #4095E5;
}

.ai-name {
	font-size: 24rpx;
	margin-top: 10rpx;
	color: #333;
}

.ai-intro {
	background-color: #ffffff;
	padding: 30rpx;
	border-radius: 20rpx;
	margin: 0 20rpx 30rpx;
	box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
}

.ai-profile {
	display: flex;
	margin-bottom: 30rpx;
}

.ai-big-avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 60rpx;
	border: 4rpx solid #4095E5;
	box-shadow: 0 4rpx 12rpx rgba(64,149,229,0.2);
}

.ai-info {
	margin-left: 20rpx;
	flex: 1;
}

.ai-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.ai-desc {
	font-size: 26rpx;
	color: #666;
	line-height: 1.5;
}

.ai-actions {
	display: flex;
	justify-content: center;
}

.start-chat-btn {
	background-color: #4095E5;
	color: #ffffff;
	font-size: 30rpx;
	border-radius: 40rpx;
	padding: 15rpx 80rpx;
	border: none;
}

.chat-history {
	margin: 0 20rpx;
}

.section-title {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 10rpx;
	font-size: 28rpx;
	color: #666;
}

.clear-history {
	font-size: 24rpx;
	color: #999;
}

.history-list {
	background-color: #ffffff;
	border-radius: 20rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
}

.history-item {
	background-color: #ffffff;
	padding: 20rpx 30rpx;
	margin-bottom: 20rpx;
	border-radius: 12rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
	box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
}

.history-left {
	display: flex;
	align-items: center;
	flex: 1;
}

.history-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 40rpx;
	margin-right: 20rpx;
}

.history-info {
	display: flex;
	flex-direction: column;
}

.history-title {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	margin-bottom: 6rpx;
	max-width: 450rpx;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.history-time {
	font-size: 24rpx;
	color: #999;
}

.history-actions {
	height: 80rpx;
	width: 80rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 10;
	position: relative;
}

.history-actions .uni-icons {
	width: 80rpx;
	height: 80rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	background-color: #eaeaea;
	border-radius: 40rpx;
	padding: 15rpx;
}

.history-actions .uni-icons:active {
	background-color: #d5d5d5;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 50rpx;
}

.empty-image {
	width: 300rpx;
	height: 300rpx;
	margin-bottom: 30rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
	text-align: center;
}
</style>