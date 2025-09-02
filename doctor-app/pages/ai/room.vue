<template>
	<view class="ai-chat-room">
		<!-- 聊天头部 -->
		<view class="chat-header">
			<view class="back-btn" @click="goBack">
				<uni-icons type="left" size="18" color="#333"></uni-icons>
			</view>
			<view class="assistant-info">
				<image class="assistant-avatar" :src="aiAssistants[assistantType].avatar" mode="aspectFill"></image>
				<text class="assistant-name">{{ assistantNames[assistantType] }}</text>
			</view>
			<view class="actions-btn">
				<uni-icons type="more-filled" size="24" color="#333" @click="showActions"></uni-icons>
			</view>
		</view>
		
		<!-- 聊天内容区 -->
		<scroll-view 
			class="chat-content" 
			scroll-y="true" 
			:scroll-into-view="scrollIntoView"
			@scrolltoupper="loadMoreMessages"
			upper-threshold="50"
			:scroll-with-animation="true"
			ref="chatScroll"
		>
			<view class="loading" v-if="isLoading">
				<uni-load-more status="loading" :contentText="{contentdown: '加载更多',contentrefresh: '加载中...',contentnomore: '没有更多数据了'}"></uni-load-more>
			</view>
			
			<view class="message-list">
				<!-- AI欢迎信息 -->
				<view class="ai-welcome" v-if="messageList.length === 0">
					<image class="ai-avatar-large" :src="aiAssistants[assistantType].avatar" mode="aspectFill"></image>
					<view class="ai-welcome-title">{{aiAssistants[assistantType].name}}</view>
					<view class="ai-welcome-desc">{{aiAssistants[assistantType].description}}</view>
					
					<view class="suggestion-list">
						<view class="suggestion-title">可以问我这些问题</view>
						<view 
							class="suggestion-item" 
							v-for="(item, index) in suggestions" 
							:key="index"
							@click="useQuickQuestion(item)"
						>
							<text>{{item}}</text>
						</view>
					</view>
				</view>
				
				<!-- 消息列表 -->
				<view 
					v-for="(item, index) in messageList" 
					:key="item.id" 
					class="message-item" 
					:class="{'self': item.isSelf}"
					:id="'msg-' + item.id"
				>
					<view class="time" v-if="showTimeForMessage(item, index)">
						<text>{{formatTime(item.time)}}</text>
					</view>
					
					<view class="message-content">
						<view class="avatar" v-if="!item.isSelf">
							<image :src="aiAssistants[assistantType].avatar" mode="aspectFill"></image>
						</view>
						<view class="bubble" :class="{'ai-bubble': !item.isSelf, 'user-bubble': item.isSelf}">
							<view v-if="item.type === 'text'" class="text-content">
								<text>{{item.content}}</text>
							</view>
							<view v-else-if="item.type === 'image'" class="image-content">
								<image :src="item.content" mode="widthFix" @tap="previewImage(item.content)"></image>
							</view>
							<view v-else-if="item.type === 'markdown'" class="markdown-content">
								<rich-text :nodes="renderMarkdown(item.content)"></rich-text>
							</view>
							<!-- 添加删除按钮 -->
							<view class="message-actions" @longpress="showMessageActions(item)">
								<uni-icons v-if="item.showActions" type="more-filled" size="18" color="#999"></uni-icons>
							</view>
						</view>
						<view class="avatar" v-if="item.isSelf">
							<image :src="userInfo.avatar" mode="aspectFill"></image>
						</view>
					</view>
				</view>
				
				<!-- AI正在输入提示 -->
				<view class="ai-typing" v-if="isAITyping">
					<view class="avatar">
						<image :src="aiAssistants[assistantType].avatar" mode="aspectFill"></image>
					</view>
					<view class="typing-indicator">
						<view class="dot"></view>
						<view class="dot"></view>
						<view class="dot"></view>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 输入区域 -->
		<view class="chat-input-area">
			<view class="input-box">
				<view class="text-input">
					<textarea 
						v-model="inputContent" 
						auto-height 
						placeholder="输入问题..." 
						maxlength="1000"
						@focus="inputFocus"
						@blur="inputBlur"
						@confirm="sendMessage"
						confirm-type="send"
						:disabled="isAITyping"
						cursor-spacing="20"
					></textarea>
				</view>
				
				<view class="emoji-btn" @click="toggleEmoji">
					<uni-icons type="emotion" size="24" color="#666"></uni-icons>
				</view>
				
				<view class="send-btn" v-if="inputContent.trim().length > 0" @click="sendMessage" :class="{'disabled': isAITyping}">
					<text>发送</text>
				</view>
			</view>
			
			<!-- 表情面板 -->
			<view class="emoji-panel" v-if="showEmojiPanel">
				<view class="emoji-list">
					<view class="emoji-item" v-for="(emoji, index) in emojiList" :key="index" @click="selectEmoji(emoji)">
						<text>{{emoji}}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 添加消息操作弹出菜单 -->
		<uni-popup ref="messageActionsPopup" type="bottom">
			<view class="popup-content">
				<view class="popup-item" @click="deleteMessage">
					<text>删除消息</text>
				</view>
				<view class="popup-item" @click="copyMessage">
					<text>复制消息</text>
				</view>
				<view class="popup-item cancel" @click="closeMessageActions">
					<text>取消</text>
				</view>
			</view>
		</uni-popup>
		
		<!-- 添加页面操作按钮 -->
		<uni-popup ref="actionsPopup" type="bottom">
			<view class="popup-content">
				<view class="popup-item" @click="clearCurrentChatHistory">
					<text>清空当前对话</text>
				</view>
				<view class="popup-item" @click="newChat">
					<text>新建对话</text>
				</view>
				<view class="popup-item cancel" @click="closeActions">
					<text>取消</text>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	import { getAIChatHistory, sendMessageToAI, getAIAssistantList, deleteAIChat, clearChatHistory, deleteAIMessage } from '@/api/ai';
	import { chatWithAI, getAISystemPrompt, formatMessagesForAPI } from '@/api/ai/dashscope';
	import request from '@/utils/request';
	import MarkdownIt from 'markdown-it';
	
	export default {
		data() {
			return {
				chatId: null,
				assistantType: 0,
				inputContent: '',
				showEmojiPanel: false,
				scrollIntoView: '',
				isLoading: false,
				isAITyping: false,
				messageList: [],
				messageListCached: false,
				lastChatId: null,
				_refreshTimeout: null, // 用于跟踪刷新定时器
				md: null, // Markdown解析器
				aiAssistants: [
					{
						id: 1,
						name: '心理顾问',
						avatar: '/static/images/ai-advisor.svg',
						description: '我是专业的心理健康顾问，可以为您提供情绪管理和心理健康建议。请告诉我您目前的困扰，我会尽力帮助您。'
					},
					{
						id: 2,
						name: '冥想助手',
						avatar: '/static/images/ai-meditation.svg',
						description: '我是您的冥想助手，可以帮助您进行冥想练习，缓解压力，改善睡眠质量。我们可以一起开始一段放松之旅。'
					},
					{
						id: 3,
						name: '情绪分析师',
						avatar: '/static/images/ai-emotion.svg',
						description: '我是情绪分析师，可以帮助您分析和理解自己的情绪状态。分享您的感受，我会给您专业的分析和建议。'
					},
					{
						id: 4,
						name: '生活教练',
						avatar: '/static/images/ai-coach.svg',
						description: '我是您的生活教练，可以帮助您制定目标，培养健康的生活习惯。我们一起打造更好的生活方式。'
					}
				],
				emojiList: ['😊', '😂', '😍', '😭', '😘', '🤔', '😡', '😱', '👍', '👎', '❤️', '💔', '🎉', '🌹', '🍀'],
				userInfo: {
					id: 0,
					name: '',
					avatar: '/static/images/profile.svg'
				},
				suggestions: [],
				assistantTypes: ['advisor', 'meditation', 'emotion', 'coach'],
				assistantNames: ['心理顾问', '冥想助手', '情绪分析师', '生活教练'],
				userFailCount: 0,
				cooldownActive: false,
				cooldownTimeout: null,
				lastRequestTime: Date.now(),
				requestLimitPerMinute: 10, // 每分钟最大请求数
				requestCount: 0,
				requestCountResetTime: Date.now(),
				currentMessageId: null, // 当前选中的消息ID
			}
		},
		created() {
			// 初始化markdown解析器
			this.md = new MarkdownIt({
				html: true,
				breaks: true,
				linkify: true,
				typographer: true
			});
		},
		onLoad(options) {
			if (options.type) {
				this.assistantType = parseInt(options.type) || 0;
			}
			
			if (options.id) {
				this.chatId = options.id;
				this.lastChatId = options.id; // 保存最后加载的聊天ID
				console.log('加载聊天:', this.chatId, '助手类型:', this.assistantType);
			} else {
				console.warn('未提供聊天ID');
			}
			
			// 获取用户信息
			const userInfo = uni.getStorageSync('userInfo');
			if (userInfo) {
				try {
					this.userInfo = JSON.parse(userInfo);
				} catch (e) {
					console.error('解析用户信息失败:', e);
				}
			}
			
			// 根据助手类型设置建议问题
			this.setSuggestionsByType();
			
			// 从API获取AI助手列表
			getAIAssistantList().then(res => {
				if (res.code === 200 && res.data && res.data.length > 0) {
					this.aiAssistants = res.data;
				}
			}).catch(err => {
				console.error('获取AI助手列表失败', err);
			});
			
			// 确保队列中没有重复的刷新任务
			if (this._refreshTimeout) {
				clearTimeout(this._refreshTimeout);
				this._refreshTimeout = null;
			}
			
			// 加载聊天记录
			if (this.chatId) {
				// 强制从服务器加载历史记录，确保数据最新
				this.loadChatHistory(true);
			}
		},
		onShow() {
			// 检查是否有ID变更需要重新加载
			if (this.chatId && this.chatId !== this.lastChatId) {
				this.lastChatId = this.chatId;
				
				// 清空当前消息列表，避免重复加载
				this.messageList = [];
				this.messageListCached = false;
				
				// 加载新的聊天记录
				this.loadChatHistory(true);
			} else if (this.chatId && this.messageList.length > 0) {
				// 页面重新显示时，执行一次去重操作，确保没有重复消息
				const uniqueMessages = this._removeDuplicateMessages(this.messageList);
				const filteredMessages = this._filterRepeatedAIMessages(uniqueMessages);
				
				// 只有当过滤后的消息数量不同时才更新
				if (filteredMessages.length !== this.messageList.length) {
					console.log(`页面显示时移除了 ${this.messageList.length - filteredMessages.length} 条重复消息`);
					this.messageList = filteredMessages;
					this._checkAndRemoveAdjacentDuplicates();
					this._updateMessageCache();
				}
			}
		},
		onHide() {
			// 缓存当前消息列表，避免刷新页面导致数据丢失
			if (this.chatId && this.messageList.length > 0) {
				try {
					uni.setStorageSync('cachedMessages_' + this.chatId, JSON.stringify(this.messageList));
					console.log('已缓存消息列表', this.messageList.length + '条消息');
				} catch (e) {
					console.error('缓存消息列表失败:', e);
				}
			}
		},
		onUnload() {
			// 清除刷新定时器
			if (this._refreshTimeout) {
				clearTimeout(this._refreshTimeout);
				this._refreshTimeout = null;
			}
			
			// 清除冷却定时器
			if (this.cooldownTimeout) {
				clearTimeout(this.cooldownTimeout);
				this.cooldownTimeout = null;
			}
			
			// 在页面卸载时清理缓存中可能存在的重复消息
			if (this.chatId && this.messageList.length > 0) {
				try {
					// 对消息进行去重和过滤
					const uniqueMessages = this._removeDuplicateMessages(this.messageList);
					const filteredMessages = this._filterRepeatedAIMessages(uniqueMessages);
					
					// 如果过滤后的消息数量与原始数量不同，更新缓存
					if (filteredMessages.length !== this.messageList.length) {
						uni.setStorageSync('cachedMessages_' + this.chatId, JSON.stringify(filteredMessages));
						console.log('页面卸载时清理了重复消息', 
							this.messageList.length - filteredMessages.length + '条重复消息');
					}
				} catch (e) {
					console.error('页面卸载时清理缓存失败:', e);
				}
			}
		},
		onPullDownRefresh() {
			// 刷新聊天记录时清空现有消息，防止重复
			if (this.chatId) {
				this.loadChatHistory(true); // 强制刷新聊天记录
			}
			setTimeout(() => {
				uni.stopPullDownRefresh(); // 停止下拉刷新动画
			}, 500);
		},
		methods: {
			setSuggestionsByType() {
				// 根据助手类型设置建议问题
				switch(this.assistantType) {
					case 0: // 心理顾问
						this.suggestions = [
							'我最近感到很焦虑，有什么缓解方法吗？',
							'如何改善我的睡眠质量？',
							'我该如何与家人进行有效沟通？',
							'如何保持积极的心态？'
						];
						break;
					case 1: // 冥想助手
						this.suggestions = [
							'请带我进行一次5分钟的冥想',
							'如何通过冥想缓解压力？',
							'适合睡前的放松练习有哪些？',
							'如何培养冥想的习惯？'
						];
						break;
					case 2: // 情绪分析师
						this.suggestions = [
							'为什么我总是对小事感到愤怒？',
							'如何识别和管理我的情绪？',
							'我该如何处理负面情绪？',
							'为什么我会有情绪波动？'
						];
						break;
					case 3: // 生活教练
						this.suggestions = [
							'如何建立健康的生活习惯？',
							'怎样提高我的工作效率？',
							'如何设定并实现个人目标？',
							'如何保持工作和生活的平衡？'
						];
						break;
				}
			},
			goBack() {
				uni.navigateBack();
			},
			showActions() {
				this.$refs.actionsPopup.open('bottom');
			},
			closeActions() {
				this.$refs.actionsPopup.close();
			},
			showMessageActions(item) {
				// 设置当前操作的消息ID
				this.currentMessageId = item.id;
				this.$refs.messageActionsPopup.open('bottom');
			},
			closeMessageActions() {
				this.$refs.messageActionsPopup.close();
			},
			deleteMessage() {
				if (!this.currentMessageId) {
					uni.showToast({
						title: '无效的消息ID',
						icon: 'none'
					});
					this.closeMessageActions();
					return;
				}
				
				uni.showModal({
					title: '删除确认',
					content: '确定要删除这条消息吗？',
					success: (res) => {
						if (res.confirm) {
							uni.showLoading({
								title: '正在删除...'
							});
							
							deleteAIMessage(this.currentMessageId).then(res => {
								uni.hideLoading();
								this.closeMessageActions();
								
								if (res.code === 200) {
									// 从本地消息列表中删除
									this.messageList = this.messageList.filter(msg => msg.id !== this.currentMessageId);
									uni.showToast({
										title: '删除成功',
										icon: 'success'
									});
								} else {
									uni.showToast({
										title: res.msg || '删除失败',
										icon: 'none'
									});
									console.error('删除消息失败，后端返回:', res);
								}
							}).catch(err => {
								uni.hideLoading();
								this.closeMessageActions();
								console.error('删除消息失败:', err);
								uni.showToast({
									title: '删除失败',
									icon: 'none'
								});
							});
						} else {
							this.closeMessageActions();
						}
					}
				});
			},
			copyMessage() {
				const message = this.messageList.find(msg => msg.id === this.currentMessageId);
				if (message && message.content) {
					uni.setClipboardData({
						data: message.content,
						success: () => {
							uni.showToast({
								title: '复制成功',
								icon: 'success'
							});
						}
					});
				}
				this.closeMessageActions();
			},
			clearCurrentChatHistory() {
				if (!this.chatId) {
					uni.showToast({
						title: '无效的聊天ID',
						icon: 'none'
					});
					this.closeActions();
					return;
				}
				
				uni.showModal({
					title: '清空确认',
					content: '确定要清空当前对话的所有消息吗？此操作不可恢复。',
					success: (res) => {
						if (res.confirm) {
							uni.showLoading({
								title: '正在清空...'
							});
							
							clearChatHistory(this.chatId).then(res => {
								uni.hideLoading();
								this.closeActions();
								
								if (res.code === 200) {
									// 清空本地消息列表
									this.messageList = [];
									this.messageListCached = false;
									
									// 清除该聊天的本地缓存
									try {
										// 删除关联的缓存
										const cacheKey = 'cachedMessages_' + this.chatId;
										uni.removeStorageSync(cacheKey);
										console.log('已清除缓存:', cacheKey);
									} catch (e) {
										console.error('清除缓存失败:', e);
									}
									
									// 重新加载数据确保清空成功
									this.loadMessages(true);
									
									uni.showToast({
										title: '已清空对话',
										icon: 'success'
									});
								} else {
									uni.showToast({
										title: res.msg || '清空失败',
										icon: 'none'
									});
									console.error('清空对话失败，后端返回:', res);
								}
							}).catch(err => {
								uni.hideLoading();
								this.closeActions();
								console.error('清空对话失败:', err);
								uni.showToast({
									title: '清空失败',
									icon: 'none'
								});
							});
						} else {
							this.closeActions();
						}
					}
				});
			},
			newChat() {
				this.closeActions();
				uni.navigateBack({
					success: () => {
						// 延迟执行，确保导航完成
						setTimeout(() => {
							// 在上一页面触发新建对话
							uni.$emit('create_new_chat', { assistantType: this.assistantType });
						}, 300);
					}
				});
			},
			loadChatHistory(forceRefresh = false) {
				// 加载聊天记录
				if (!this.chatId) {
					this.messageList = [];
					return;
				}
				
				// 如果已有缓存且不强制刷新，则跳过
				if (this.messageListCached && !forceRefresh) {
					console.log('使用缓存的消息列表，跳过加载');
					return;
				}
				
				this.isLoading = true;
				
				// 根据chatId获取聊天记录
				getAIChatHistory({ chatId: this.chatId })
					.then(res => {
						if (res.code === 200 && res.data && res.data.messages) {
							// 先保存现有消息，防止多次刷新导致完全丢失
							const existingMessages = [...this.messageList];
							
							// 清空现有消息列表，避免重复
							this.messageList = [];
							
							// 转换API返回的数据为本地格式
							let messages = res.data.messages.map(item => {
								// 正确的类型判断和转换
								let isUserMessage = false;
								
								// 使用更健壮的方式检查是否是用户消息
								if (item.isUser === true || item.isUser === 'true' || item.isUser === '1' || item.isUser === 1) {
									isUserMessage = true;
								}
								
								// 转换日期
								let messageTime = new Date();
								try {
									messageTime = item.createTime ? new Date(item.createTime) : new Date();
									// 检查转换是否有效
									if (isNaN(messageTime.getTime())) {
										messageTime = new Date();
									}
								} catch (e) {
									console.error('日期转换错误:', e, item.createTime);
									messageTime = new Date();
								}
								
								// 设置消息类型
								let messageType = item.type || 'text';
								// 检查内容是否包含Markdown特征
								if (item.content && (
									item.content.includes('# ') || 
									item.content.includes('## ') || 
									item.content.includes('```') || 
									item.content.includes('- ') ||
									item.content.includes('1. '))) {
									messageType = 'markdown';
								}
								
								return {
									id: item.id,
									content: item.content,
									time: messageTime,
									isSelf: isUserMessage, // 使用增强判断的结果
									type: messageType,
									uniqueKey: `${item.id}_${isUserMessage ? 'user' : 'ai'}`
								};
							});
							
							// 如果有现有消息，合并并去重
							if (existingMessages.length > 0) {
								// 确保所有消息都有uniqueKey
								existingMessages.forEach(msg => {
									if (!msg.uniqueKey) {
										msg.uniqueKey = `${msg.id}_${msg.isSelf ? 'user' : 'ai'}`;
									}
								});
								
								// 合并现有消息和新加载的消息
								messages = [...messages, ...existingMessages];
							}
							
							// 按时间排序（确保升序排列：早的消息在前）
							messages.sort((a, b) => {
								return new Date(a.time).getTime() - new Date(b.time).getTime();
							});
							
							// 防止重复消息：先清空，再赋值
							this.messageList = [];
							this.$nextTick(() => {
								// 使用唯一标识符去重
								const uniqueMessages = this._removeDuplicateMessages(messages);
								
								// 去除可能存在的重复消息（包括用户和AI消息）
								const filteredMessages = this._filterRepeatedAIMessages(uniqueMessages);
								
								this.messageList = filteredMessages;
								
								// 额外检查相邻的相同消息，确保不会有重复消息显示
								this._checkAndRemoveAdjacentDuplicates();
								
								this.isLoading = false;
								
								// 滚动到最新消息
								this.$nextTick(() => {
									this.scrollToBottom();
								});
							});
							
							// 保存消息缓存标志
							this.messageListCached = true;
							
							// 缓存消息列表
							try {
								const optimizedMessages = this._filterRepeatedAIMessages(this.messageList);
								uni.setStorageSync('cachedMessages_' + this.chatId, JSON.stringify(optimizedMessages));
								console.log('已更新缓存消息列表', optimizedMessages.length + '条消息');
							} catch (e) {
								console.error('缓存消息列表失败:', e);
							}
						} else {
							// 空聊天记录
							this.messageList = [];
							this.isLoading = false;
							
							// 尝试加载缓存的消息
							this._tryLoadCachedMessages();
						}
					})
					.catch(err => {
						console.error('获取聊天记录失败', err);
						this.isLoading = false;
						
						// 尝试加载缓存的消息
						this._tryLoadCachedMessages();
					});
			},
			
			// 新增方法：尝试加载缓存的消息
			_tryLoadCachedMessages() {
				if (!this.chatId) return;
				
				try {
					const cachedMessages = uni.getStorageSync('cachedMessages_' + this.chatId);
					if (cachedMessages) {
						const parsedMessages = JSON.parse(cachedMessages);
						if (Array.isArray(parsedMessages) && parsedMessages.length > 0) {
							console.log('从缓存加载消息列表（API失败后的备用方案）', parsedMessages.length + '条消息');
							
							// 确保所有缓存消息都有正确的时间格式
							parsedMessages.forEach(msg => {
								if (msg.time && !(msg.time instanceof Date)) {
									try {
										msg.time = new Date(msg.time);
									} catch (e) {
										msg.time = new Date();
									}
								}
								
								// 确保所有消息都有唯一标识符
								if (!msg.uniqueKey) {
									msg.uniqueKey = `${msg.id}_${msg.isSelf ? 'user' : 'ai'}`;
								}
							});
							
							this.messageList = parsedMessages;
							this.messageListCached = true;
							
							// 滚动到底部
							this.$nextTick(() => {
								this.scrollToBottom();
							});
						}
					}
				} catch (e) {
					console.error('尝试加载缓存消息失败:', e);
					this.messageList = [];
				}
			},
			loadMoreMessages() {
				// 加载更多历史消息
				if (this.isLoading || !this.chatId) return;
				
				this.isLoading = true;
				
				// 获取当前最早消息的ID
				const earliestMsgId = this.messageList.length > 0 ? this.messageList[0].id : 0;
				
				getAIChatHistory({ 
					chatId: this.chatId,
					beforeId: earliestMsgId
				})
					.then(res => {
						this.isLoading = false;
						
						if (res.code === 200 && res.data && res.data.messages && res.data.messages.length > 0) {
							// 处理API返回的更多聊天记录
							const moreMessages = res.data.messages.map(item => {
								// 增强对isSelf属性的确定性判断
								let isUserMessage = false;
								if (item.isUser === '1' || item.isUser === true || item.isUser === 1) {
									isUserMessage = true;
								} else if (item.isUser === '0' || item.isUser === false || item.isUser === 0) {
									isUserMessage = false;
								} else {
									console.warn('无法确定消息类型，默认为AI消息:', item);
									isUserMessage = false;
								}
								
								// 修复时间处理
								let messageTime;
								try {
									if (item.createTime) {
										// 尝试将createTime转换为毫秒时间戳
										const timestamp = typeof item.createTime === 'string' ? 
											(item.createTime.indexOf('T') > -1 ? 
												new Date(item.createTime).getTime() : 
												parseInt(item.createTime)) : 
											item.createTime;
										
										messageTime = new Date(timestamp);
										
										// 检查是否为有效日期
										if (isNaN(messageTime.getTime())) {
											console.warn('无效的日期格式:', item.createTime);
											messageTime = new Date();
										}
									} else {
										messageTime = new Date();
									}
								} catch (e) {
									console.error('日期转换错误:', e, item.createTime);
									messageTime = new Date();
								}
								
								return {
									id: item.id,
									content: item.content,
									time: messageTime,
									isSelf: isUserMessage, // 使用增强判断的结果
									type: item.type || 'text',
									uniqueKey: `${item.id}_${isUserMessage ? 'user' : 'ai'}`
								};
							});
							
							// 合并现有消息和新加载的消息
							const allMessages = [...moreMessages, ...this.messageList];
							
							// 确保每条消息都有唯一标识符
							allMessages.forEach(msg => {
								if (!msg.uniqueKey) {
									msg.uniqueKey = `${msg.id}_${msg.isSelf ? 'user' : 'ai'}`;
								}
								
								// 确保所有时间字段都是Date对象
								if (msg.time && !(msg.time instanceof Date)) {
									try {
										msg.time = new Date(msg.time);
									} catch (e) {
										console.error('消息时间格式转换失败:', e);
										msg.time = new Date();
									}
								}
							});
							
							// 对合并后的消息进行去重和过滤
							const uniqueMessages = this._removeDuplicateMessages(allMessages);
							const filteredMessages = this._filterRepeatedAIMessages(uniqueMessages);
							
							// 更新消息列表
							this.messageList = filteredMessages;
							
							// 检查相邻重复
							this._checkAndRemoveAdjacentDuplicates();
							
							// 更新缓存
							this._updateMessageCache();
						} else {
							// 没有更多消息
							uni.showToast({
								title: '没有更多消息',
								icon: 'none'
							});
						}
					})
					.catch(err => {
						console.error('加载更多消息失败', err);
						this.isLoading = false;
					});
			},
			scrollToBottom() {
				if (this.messageList.length > 0) {
					const lastMessage = this.messageList[this.messageList.length - 1];
					this.scrollIntoView = 'msg-' + lastMessage.id;
				}
			},
			showTimeForMessage(item, index) {
				// 显示时间的逻辑：第一条消息或者与上一条消息间隔超过5分钟
				if (index === 0) return true;
				
				const prevMsg = this.messageList[index - 1];
				// 修复时间差计算，确保使用毫秒值进行比较
				const currentTime = typeof item.time === 'object' ? item.time.getTime() : new Date(item.time).getTime();
				const prevTime = typeof prevMsg.time === 'object' ? prevMsg.time.getTime() : new Date(prevMsg.time).getTime();
				
				const timeDiff = currentTime - prevTime;
				return timeDiff > 5 * 60 * 1000; // 5分钟
			},
			formatTime(time) {
				// 确保time是Date对象
				const now = new Date();
				const msgDate = time instanceof Date ? time : new Date(time);
				
				// 检查转换结果是否有效
				if (isNaN(msgDate.getTime())) {
					console.warn('格式化时间失败，无效的日期:', time);
					return '未知时间';
				}
				
				// 同一天显示时间
				if (now.toDateString() === msgDate.toDateString()) {
					return msgDate.getHours().toString().padStart(2, '0') + ':' + 
						msgDate.getMinutes().toString().padStart(2, '0');
				}
				
				// 昨天
				const yesterday = new Date(now);
				yesterday.setDate(now.getDate() - 1);
				if (yesterday.toDateString() === msgDate.toDateString()) {
					return '昨天 ' + msgDate.getHours().toString().padStart(2, '0') + ':' + 
						msgDate.getMinutes().toString().padStart(2, '0');
				}
				
				// 一周内
				const weekDays = ['日', '一', '二', '三', '四', '五', '六'];
				const dayDiff = Math.floor((now - msgDate) / (24 * 3600 * 1000));
				if (dayDiff < 7) {
					return '星期' + weekDays[msgDate.getDay()] + ' ' + 
						msgDate.getHours().toString().padStart(2, '0') + ':' + 
						msgDate.getMinutes().toString().padStart(2, '0');
				}
				
				// 更早
				return msgDate.getFullYear() + '/' + 
					(msgDate.getMonth() + 1).toString().padStart(2, '0') + '/' + 
					msgDate.getDate().toString().padStart(2, '0') + ' ' + 
					msgDate.getHours().toString().padStart(2, '0') + ':' + 
					msgDate.getMinutes().toString().padStart(2, '0');
			},
			toggleEmoji() {
				this.showEmojiPanel = !this.showEmojiPanel;
			},
			selectEmoji(emoji) {
				this.inputContent += emoji;
			},
			inputFocus() {
				this.showEmojiPanel = false;
				
				// 滚动到底部
				this.$nextTick(() => {
					this.scrollToBottom();
				});
			},
			inputBlur() {
				// 处理输入框失焦事件
			},
			useQuickQuestion(question) {
				this.inputContent = question;
				this.sendMessage();
			},
			previewImage(url) {
				uni.previewImage({
					urls: [url],
					current: url
				});
			},
			sendMessage() {
				if (this.inputContent.trim() === '' || this.isAITyping) return;
				
				// 检查是否处于冷却期
				if (this.cooldownActive) {
					uni.showToast({
						title: '系统冷却中，请稍后再试',
						icon: 'none',
						duration: 2000
					});
					return;
				}
				
				// 检查请求频率限制
				const now = Date.now();
				
				// 每分钟请求次数限制
				if (now - this.requestCountResetTime > 60000) {
					// 重置计数器
					this.requestCount = 0;
					this.requestCountResetTime = now;
				}
				
				if (this.requestCount >= this.requestLimitPerMinute) {
					uni.showToast({
						title: '请求频率过高，请稍后再试',
						icon: 'none',
						duration: 2000
					});
					return;
				}
				
				// 检查两次请求的最小间隔（3秒）
				if (now - this.lastRequestTime < 3000) {
					uni.showToast({
						title: '请求过于频繁，请稍等',
						icon: 'none',
						duration: 2000
					});
					return;
				}
				
				// 更新请求状态
				this.requestCount++;
				this.lastRequestTime = now;
				
				// 检查前一次发送的内容是否与当前内容相同
				const lastMessage = this.messageList.length > 0 ? 
					this.messageList[this.messageList.length - 1] : null;
				
				if (lastMessage && lastMessage.isSelf && 
					lastMessage.content === this.inputContent.trim()) {
					uni.showToast({
						title: '请不要发送重复内容',
						icon: 'none',
						duration: 2000
					});
					return;
				}
				
				// 添加用户消息到列表，确保isSelf设置为true
				const userMessage = {
					id: Date.now(),
					content: this.inputContent.trim(),
					time: new Date(),
					isSelf: true, // 明确设置为true，表示用户消息
					type: 'text',
					uniqueKey: `${Date.now()}_user` // 添加唯一标识符
				};
				
				// 检查是否已有相同的用户消息（在最近3条消息中）
				let hasDuplicateUserMessage = false;
				if (this.messageList.length > 0) {
					const recentMessages = this.messageList.slice(-3); // 获取最近3条消息
					for (const msg of recentMessages) {
						if (msg.isSelf && msg.content === userMessage.content) {
							hasDuplicateUserMessage = true;
							console.log('检测到重复的用户消息，跳过添加');
							break;
						}
					}
				}
				
				if (!hasDuplicateUserMessage) {
					this.messageList.push(userMessage);
					this.scrollToBottom();
					
					// 添加用户消息后执行去重
					this._checkAndRemoveAdjacentDuplicates();
					
					// 添加用户消息后立即更新缓存
					this._updateMessageCache();
				}
				
				// 清空输入框
				const message = this.inputContent;
				this.inputContent = '';
				
				// 显示AI正在输入
				this.isAITyping = true;
				
				// 获取系统提示词
				const systemPrompt = getAISystemPrompt(this.assistantType);
				
				// 通过后端API发送消息
				sendMessageToAI({
					content: message,
					chatId: this.chatId,
					assistantType: this.assistantType
				})
					.then(res => {
						if (res.code === 200 && res.data) {
							// 记录成功
							this.userFailCount = 0;
							
							// 添加AI回复到消息列表
							const aiMessage = {
								id: res.data.id || Date.now() + 1,
								content: res.data.content || '抱歉，我没有理解您的问题。',
								time: new Date(),
								isSelf: false, // AI消息
								type: res.data.type || 'text', // 使用后端返回的类型或默认为text
								uniqueKey: `${res.data.id || Date.now()}_ai` // 添加唯一标识符
							};
							
							// 检查是否已有相同的AI消息
							let hasSameAIMessage = false;
							if (this.messageList.length > 0) {
								const lastMessages = this.messageList.slice(-3); // 检查最近3条消息
								for (const msg of lastMessages) {
									if (!msg.isSelf && msg.content === aiMessage.content) {
										hasSameAIMessage = true;
										console.log('检测到重复的AI消息，跳过添加');
										break;
									}
								}
							}
							
							if (!hasSameAIMessage) {
								// 如果内容包含Markdown特征，将类型设为markdown
								if (aiMessage.content && (
									aiMessage.content.includes('# ') || 
									aiMessage.content.includes('## ') || 
									aiMessage.content.includes('```') || 
									aiMessage.content.includes('- ') ||
									aiMessage.content.includes('1. '))) {
									aiMessage.type = 'markdown';
								}
								
								this.messageList.push(aiMessage);
								
								// 执行去重和排序
								const uniqueMessages = this._removeDuplicateMessages(this.messageList);
								const filteredMessages = this._filterRepeatedAIMessages(uniqueMessages);
								this.messageList = filteredMessages;
								
								// 检查相邻重复
								this._checkAndRemoveAdjacentDuplicates();
								
								this.scrollToBottom();
							}
							
							// 如果是新聊天，保存chatId
							if (!this.chatId && res.data.chatId) {
								this.chatId = res.data.chatId;
								
								// 更新URL，但不刷新页面
								const url = '/pages/ai/room?id=' + res.data.chatId + '&type=' + this.assistantType;
								if (history && history.replaceState) {
									history.replaceState({}, '', url);
								}
							}
							
							// 检查响应是否包含限流信息
							if (res.data.content && (
								res.data.content.includes("请求暂时无法处理") ||
								res.data.content.includes("请求过于频繁") ||
								res.data.content.includes("系统当前请求量较大") ||
								res.data.content.includes("速率限制")
							)) {
								// 遇到限流提示，启动短暂冷却期
								this.activateCooldown(30000); // 30秒
							}
						} else {
							// 记录失败
							this.userFailCount++;
							
							// 添加错误消息
							const errorMessage = {
								id: Date.now() + 1,
								content: '抱歉，我遇到了一些问题，无法回答您的问题。请稍后再试。',
								time: new Date(),
								isSelf: false,
								type: 'text',
								uniqueKey: `error_${Date.now()}` // 添加唯一标识符确保不会重复
							};
							
							// 检查是否已有相同的错误消息
							let hasErrorMessage = false;
							if (this.messageList.length > 0) {
								const lastMsg = this.messageList[this.messageList.length - 1];
								if (!lastMsg.isSelf && lastMsg.content === errorMessage.content) {
									hasErrorMessage = true;
								}
							}
							
							if (!hasErrorMessage) {
								this.messageList.push(errorMessage);
								
								// 执行去重和排序
								const uniqueMessages = this._removeDuplicateMessages(this.messageList);
								const filteredMessages = this._filterRepeatedAIMessages(uniqueMessages);
								this.messageList = filteredMessages;
								this._checkAndRemoveAdjacentDuplicates();
								
								this.scrollToBottom();
							}
							
							// 更新缓存
							this._updateMessageCache();
						}
					})
					.catch(err => {
						console.error('发送消息失败', err);
						
						// 添加错误消息
						const errorMessage = {
							id: Date.now() + 1,
							content: '抱歉，我遇到了一些问题，无法回答您的问题。请稍后再试。',
							time: new Date(),
							isSelf: false,
							type: 'text',
							uniqueKey: `error_${Date.now()}` // 添加唯一标识符确保不会重复
						};
						
						// 检查是否已有相同的错误消息
						let hasErrorMessage = false;
						if (this.messageList.length > 0) {
							const lastMsg = this.messageList[this.messageList.length - 1];
							if (!lastMsg.isSelf && lastMsg.content === errorMessage.content) {
								hasErrorMessage = true;
							}
						}
						
						if (!hasErrorMessage) {
							this.messageList.push(errorMessage);
							this.scrollToBottom();
						}
						
						// 更新缓存
						this._updateMessageCache();
					})
					.finally(() => {
						this.isAITyping = false;
					});
			},
			activateCooldown(duration) {
				this.cooldownActive = true;
				
				// 清除之前的定时器（如果存在）
				if (this.cooldownTimeout) {
					clearTimeout(this.cooldownTimeout);
				}
				
				// 显示冷却提示
				uni.showToast({
					title: `系统限流，冷却${duration/1000}秒后再试`,
					icon: 'none',
					duration: 3000
				});
				
				// 设置冷却定时器
				this.cooldownTimeout = setTimeout(() => {
					this.cooldownActive = false;
					this.cooldownTimeout = null;
					
					uni.showToast({
						title: '系统已恢复，可以继续对话',
						icon: 'none',
						duration: 2000
					});
				}, duration);
			},
			simulateAIResponse(userContent) {
				let aiReply = '';
				
				// 根据用户输入和助手类型生成模拟回复
				switch(this.assistantType) {
					case 0: // 心理顾问
						if (userContent.includes('焦虑')) {
							aiReply = '焦虑是一种常见的情绪反应，当我们面对压力或不确定的情况时，会产生这种感觉。你可以尝试几种方法来缓解焦虑：\n\n1. 深呼吸练习：慢慢吸气5秒，然后慢慢呼气5秒，重复几分钟\n2. 正念冥想：专注于当下，观察自己的呼吸和身体感受\n3. 规律的锻炼：每天进行30分钟中等强度的运动\n4. 限制咖啡因和酒精的摄入\n5. 确保充足的睡眠\n\n你能告诉我更多关于你焦虑的情况吗？是什么引发了这种感觉？';
						} else if (userContent.includes('睡眠') || userContent.includes('失眠')) {
							aiReply = '改善睡眠质量对心理健康非常重要。以下是一些建议：\n\n1. 保持规律的作息时间，包括周末\n2. 创造一个舒适的睡眠环境：安静、黑暗、凉爽\n3. 睡前限制电子设备的使用\n4. 避免晚上摄入咖啡因和大量食物\n5. 睡前进行放松活动，如阅读或温水浴\n6. 如果20分钟内无法入睡，起床做些放松的事情，直到感到困倦\n\n你最近的睡眠模式是怎样的？有什么特别困扰你的问题吗？';
						} else {
							aiReply = '感谢你分享你的想法。心理健康是一个复杂而个人化的旅程，每个人的经历都是独特的。我想了解更多关于你的感受和经历，这样我才能提供更有针对性的建议。\n\n你能告诉我更多关于你目前面临的挑战吗？或者有什么特定的问题你希望探讨？';
						}
						break;
					case 1: // 冥想助手
						if (userContent.includes('冥想') && userContent.includes('5分钟')) {
							aiReply = '好的，让我们开始一个5分钟的简单冥想练习：\n\n1. 找一个安静、舒适的地方坐下\n2. 保持背部挺直，双手放在膝盖上\n3. 闭上眼睛，开始注意你的呼吸\n4. 深吸一口气，数到4，然后慢慢呼气，数到6\n5. 继续这样的呼吸模式，专注于空气进入和离开身体的感觉\n6. 如果你的thoughts开始漂移，轻轻地将注意力带回到呼吸上\n7. 继续5分钟，专注于当下，感受身体的放松\n\n完成后，慢慢睁开眼睛，注意你的感受变化。你感觉如何？';
						} else {
							aiReply = '冥想是一种强大的工具，可以帮助我们培养内心的平静和专注力。无论是应对压力、改善睡眠还是增强自我意识，定期的冥想练习都能带来显著的好处。\n\n你想尝试哪种类型的冥想？例如：\n- 呼吸冥想\n- 身体扫描\n- 慈悲冥想\n- 引导式想象\n\n或者你有特定的目标想通过冥想达成？';
						}
						break;
					case 2: // 情绪分析师
						aiReply = '你的情绪反应很有趣。从你的描述中，我注意到一些模式，这可能与你过去的经历和当前的环境有关。情绪是我们内心状态的体现，理解它们是自我成长的重要部分。\n\n你能描述一下这种情绪是在什么情况下产生的吗？有没有特定的触发因素？了解这些可以帮助我们更好地分析你的情绪状态。';
						break;
					case 3: // 生活教练
						aiReply = '建立健康的生活习惯需要时间和一致性。以下是一些建议：\n\n1. 从小目标开始，逐步构建\n2. 创建明确的例行程序\n3. 使用"如果-那么"策略来应对障碍\n4. 跟踪你的进度并庆祝小成就\n5. 寻找问责伙伴或支持系统\n\n你目前有什么特定的习惯想要培养或改变？确定一个具体的起点，我们可以一起制定实用的策略。';
						break;
				}
				
				// 模拟AI思考时间
				setTimeout(() => {
					// 创建AI回复消息
					const aiMsg = {
						id: Date.now() + 1,
						content: aiReply,
						time: new Date(),
						isSelf: false,
						type: 'text'
					};
					
					// 添加到消息列表
					this.messageList.push(aiMsg);
					
					// 滚动到底部
					this.$nextTick(() => {
						this.scrollToBottom();
					});
				}, 1000);
			},
			// 修改_removeDuplicateMessages方法以确保更强的去重能力
			_removeDuplicateMessages(messages) {
				// 使用Map来存储不重复的消息，以多种条件为键
				const messageMap = new Map();
				const contentTypeMap = new Map(); // 用于检测内容相同的消息
				
				messages.forEach(msg => {
					// 生成唯一标识符
					const uniqueKey = msg.uniqueKey || `${msg.id}_${msg.isSelf ? 'user' : 'ai'}`;
					
					// 生成内容+类型标识符，用于检测内容相同的消息
					const contentTypeKey = `${msg.content}_${msg.isSelf ? 'user' : 'ai'}`;
					
					// 如果已经存在相同内容和类型的消息，检查时间差
					if (contentTypeMap.has(contentTypeKey)) {
						const existingMsg = contentTypeMap.get(contentTypeKey);
						const existingTime = existingMsg.time instanceof Date ? 
							existingMsg.time.getTime() : new Date(existingMsg.time).getTime();
						const newTime = msg.time instanceof Date ? 
							msg.time.getTime() : new Date(msg.time).getTime();
						
						// 如果时间差小于30秒，认为是重复消息，保留最早的那条
						if (Math.abs(newTime - existingTime) < 30000) {
							// 如果新消息比旧消息更早，则替换
							if (newTime < existingTime) {
								contentTypeMap.set(contentTypeKey, msg);
								messageMap.delete(contentTypeMap.get(contentTypeKey).uniqueKey);
								messageMap.set(uniqueKey, msg);
							}
							return; // 否则跳过此消息
						}
					}
					
					if (!messageMap.has(uniqueKey)) {
						// 修复可能的非Date对象时间
						if (msg.time && !(msg.time instanceof Date)) {
							try {
								msg.time = new Date(msg.time);
							} catch (e) {
								console.error('消息时间格式转换失败:', e);
								msg.time = new Date();
							}
						}
						
						// 确保有唯一标识符
						if (!msg.uniqueKey) {
							msg.uniqueKey = uniqueKey;
						}
						
						messageMap.set(uniqueKey, msg);
						contentTypeMap.set(contentTypeKey, msg);
					}
				});
				
				// 按原顺序返回不重复的消息
				return Array.from(messageMap.values()).sort((a, b) => {
					// 确保使用毫秒时间戳进行比较
					const timeA = a.time instanceof Date ? a.time.getTime() : new Date(a.time).getTime();
					const timeB = b.time instanceof Date ? b.time.getTime() : new Date(b.time).getTime();
					return timeA - timeB;
				});
			},
			// 添加过滤连续重复AI消息的方法
			_filterRepeatedAIMessages(messages) {
				if (!messages || messages.length < 2) return messages;
				
				const result = [];
				let lastAIMessage = null;
				let lastUserMessage = null;
				
				for (let i = 0; i < messages.length; i++) {
					const msg = messages[i];
					
					// 用户消息去重处理
					if (msg.isSelf) {
						// 如果是第一条用户消息，直接添加
						if (!lastUserMessage) {
							result.push(msg);
							lastUserMessage = msg;
							continue;
						}
						
						// 检查是否与上一条用户消息内容相同且时间相近（30秒内）
						const timeDiff = Math.abs(
							(msg.time instanceof Date ? msg.time.getTime() : new Date(msg.time).getTime()) - 
							(lastUserMessage.time instanceof Date ? lastUserMessage.time.getTime() : new Date(lastUserMessage.time).getTime())
						);
						
						if (msg.content === lastUserMessage.content && timeDiff < 30000) {
							console.log('过滤掉重复的用户消息:', msg.content);
							continue; // 跳过这条重复消息
						}
						
						result.push(msg);
						lastUserMessage = msg;
						continue;
					}
					
					// AI消息去重处理
					if (!msg.isSelf) {
						// 如果是第一条AI消息，直接添加
						if (!lastAIMessage) {
							result.push(msg);
							lastAIMessage = msg;
							continue;
						}
						
						// 如果内容相同且时间相近（30秒内），则视为重复消息
						const timeDiff = Math.abs(
							(msg.time instanceof Date ? msg.time.getTime() : new Date(msg.time).getTime()) - 
							(lastAIMessage.time instanceof Date ? lastAIMessage.time.getTime() : new Date(lastAIMessage.time).getTime())
						);
						
						if (msg.content === lastAIMessage.content && timeDiff < 30000) {
							console.log('过滤掉重复的AI消息:', msg.content);
							continue; // 跳过这条重复消息
						}
						
						result.push(msg);
						lastAIMessage = msg;
					}
				}
				
				return result;
			},
			// 更新消息缓存方法
			_updateMessageCache() {
				if (this.chatId && this.messageList.length > 0) {
					try {
						// 先执行一次去重
						const uniqueMessages = this._removeDuplicateMessages(this.messageList);
						const filteredMessages = this._filterRepeatedAIMessages(uniqueMessages);
						
						// 再检查相邻重复
						this.messageList = filteredMessages;
						this._checkAndRemoveAdjacentDuplicates();
						
						// 缓存处理后的消息列表
						uni.setStorageSync('cachedMessages_' + this.chatId, JSON.stringify(this.messageList));
						console.log('已更新消息缓存', this.messageList.length + '条消息');
					} catch (e) {
						console.error('更新消息缓存失败:', e);
					}
				}
			},
			// 添加新方法：检查并移除相邻的重复消息
			_checkAndRemoveAdjacentDuplicates() {
				if (this.messageList.length < 2) return;
				
				const result = [];
				let prevMsg = null;
				
				for (const msg of this.messageList) {
					// 如果是第一条消息，直接添加
					if (!prevMsg) {
						result.push(msg);
						prevMsg = msg;
						continue;
					}
					
					// 检查是否与前一条消息内容相同且发送方相同
					if (msg.content === prevMsg.content && msg.isSelf === prevMsg.isSelf) {
						// 如果两条消息时间相近(1分钟内)，跳过此消息
						const timeDiff = Math.abs(
							(msg.time instanceof Date ? msg.time.getTime() : new Date(msg.time).getTime()) - 
							(prevMsg.time instanceof Date ? prevMsg.time.getTime() : new Date(prevMsg.time).getTime())
						);
						
						if (timeDiff < 60000) {
							console.log('移除相邻重复消息:', msg.content);
							continue;
						}
					}
					
					result.push(msg);
					prevMsg = msg;
				}
				
				if (result.length !== this.messageList.length) {
					console.log(`移除了 ${this.messageList.length - result.length} 条相邻重复消息`);
					this.messageList = result;
				}
			},
			renderMarkdown(content) {
				if (!content) return '';
				if (!this.md) {
					this.md = new MarkdownIt({
						html: true,
						breaks: true,
						linkify: true,
						typographer: true
					});
				}
				return this.md.render(content);
			}
		}
	}
</script>

<style lang="scss" scoped>
.ai-chat-room {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.chat-header {
	display: flex;
	align-items: center;
	padding: 10px 15px;
	background-color: #ffffff;
	box-shadow: 0 1px 2px rgba(0,0,0,0.1);
	z-index: 10;
}

.back-btn, .actions-btn {
	width: 40px;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: center;
}

.assistant-info {
	flex: 1;
	display: flex;
	align-items: center;
}

.assistant-avatar {
	width: 40px;
	height: 40px;
	border-radius: 50%;
	margin-right: 10px;
}

.assistant-name {
	font-size: 18px;
	font-weight: 600;
	color: #333;
}

.chat-content {
	flex: 1;
	padding: 10px 0;
	position: relative;
}

.loading {
	padding: 10px 0;
}

.message-list {
	padding: 0 15px;
}

.ai-welcome {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30px 20px;
	background-color: transparent;
}

.ai-avatar-large {
	width: 100px;
	height: 100px;
	border-radius: 50%;
	margin-bottom: 15px;
	border: 3px solid #fff;
	box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.ai-welcome-title {
	font-size: 20px;
	font-weight: bold;
	color: #333;
	margin-bottom: 10px;
}

.ai-welcome-desc {
	font-size: 14px;
	color: #666;
	text-align: center;
	line-height: 1.5;
	margin-bottom: 20px;
}

.suggestion-list {
	width: 100%;
	margin-top: 20px;
}

.suggestion-title {
	font-size: 14px;
	color: #999;
	text-align: center;
	margin-bottom: 15px;
}

.suggestion-item {
	background-color: #fff;
	padding: 12px 15px;
	border-radius: 8px;
	margin-bottom: 10px;
	font-size: 14px;
	color: #333;
	box-shadow: 0 1px 3px rgba(0,0,0,0.05);
	transition: all 0.3s;
}

.suggestion-item:active {
	background-color: #f0f0f0;
	transform: scale(0.98);
}

.time {
	text-align: center;
	margin: 15px 0;
}

.time text {
	font-size: 12px;
	color: #999;
	background-color: rgba(0,0,0,0.05);
	padding: 2px 8px;
	border-radius: 10px;
}

.message-item {
	margin-bottom: 15px;
}

.message-content {
	display: flex;
	align-items: flex-start;
}

.message-item.self .message-content {
	flex-direction: row-reverse;
}

.avatar {
	width: 40px;
	height: 40px;
	flex-shrink: 0;
}

.avatar image {
	width: 40px;
	height: 40px;
	border-radius: 20px;
}

.bubble {
	max-width: 70%;
	margin: 0 10px;
	padding: 10px 15px;
	border-radius: 18px;
	word-break: break-word;
}

.ai-bubble {
	background-color: #ffffff;
	border: 1px solid #e5e5e5;
	border-top-left-radius: 4px;
	color: #333;
}

.user-bubble {
	background-color: #4095E5;
	color: #ffffff;
	border-top-right-radius: 4px;
}

.text-content {
	font-size: 16px;
	line-height: 1.5;
}

.image-content {
	width: 150px;
	border-radius: 8px;
	overflow: hidden;
}

.image-content image {
	width: 100%;
	height: auto;
}

.ai-typing {
	display: flex;
	align-items: flex-start;
	margin-bottom: 15px;
}

.typing-indicator {
	display: flex;
	align-items: center;
	background-color: #ffffff;
	border: 1px solid #e5e5e5;
	border-radius: 18px;
	border-top-left-radius: 4px;
	padding: 15px;
	margin: 0 10px;
}

.dot {
	width: 10px;
	height: 10px;
	border-radius: 50%;
	background-color: #ccc;
	margin: 0 3px;
	animation: typing 1.4s infinite;
}

.dot:nth-child(1) {
	animation-delay: 0s;
}

.dot:nth-child(2) {
	animation-delay: 0.2s;
}

.dot:nth-child(3) {
	animation-delay: 0.4s;
}

@keyframes typing {
	0%, 100% {
		transform: translateY(0);
		background-color: #ccc;
	}
	50% {
		transform: translateY(-5px);
		background-color: #4095E5;
	}
}

.chat-input-area {
	background-color: #ffffff;
	padding: 10px 15px;
	box-shadow: 0 -1px 2px rgba(0,0,0,0.05);
}

.input-box {
	display: flex;
	align-items: center;
}

.emoji-btn {
	width: 40px;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: center;
}

.text-input {
	flex: 1;
	background-color: #f5f5f5;
	border-radius: 20px;
	padding: 8px 15px;
	max-height: 120px;
	margin: 0 10px 0 0;
}

.text-input textarea {
	width: 100%;
	min-height: 24px;
	max-height: 100px;
	font-size: 16px;
	line-height: 24px;
}

.send-btn {
	width: 60px;
	height: 36px;
	background-color: #4095E5;
	border-radius: 18px;
	display: flex;
	align-items: center;
	justify-content: center;
}

.send-btn.disabled {
	background-color: #ccc;
}

.send-btn text {
	color: #ffffff;
	font-size: 14px;
}

.emoji-panel {
	height: 200px;
	padding: 15px;
	background-color: #ffffff;
	border-top: 1px solid #f0f0f0;
}

.emoji-list {
	display: flex;
	flex-wrap: wrap;
}

.emoji-item {
	width: 50px;
	height: 50px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 24px;
}

/* 消息操作图标 */
.message-actions {
	position: absolute;
	right: 8px;
	top: 8px;
	opacity: 0;
	transition: opacity 0.2s;
}

.bubble:hover .message-actions {
	opacity: 1;
}

/* 弹出菜单样式 */
.popup-content {
	background-color: #fff;
	border-radius: 12px 12px 0 0;
	padding: 10px 0;
}

.popup-item {
	height: 50px;
	line-height: 50px;
	text-align: center;
	font-size: 16px;
	border-bottom: 1px solid #f5f5f5;
}

.popup-item:last-child {
	border-bottom: none;
}

.popup-item.cancel {
	margin-top: 8px;
	border-top: 8px solid #f5f5f5;
	color: #ff3b30;
}

.markdown-content {
  padding: 0;
  line-height: 1.5;
  
  :deep(rich-text) {
    display: block;
    width: 100%;
  }
  
  :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
    margin-top: 16px;
    margin-bottom: 8px;
    font-weight: bold;
  }
  
  :deep(h1) {
    font-size: 24px;
  }
  
  :deep(h2) {
    font-size: 20px;
  }
  
  :deep(h3) {
    font-size: 18px;
  }
  
  :deep(p) {
    margin-bottom: 8px;
  }
  
  :deep(ul), :deep(ol) {
    padding-left: 16px;
    margin-bottom: 8px;
  }
  
  :deep(li) {
    margin-bottom: 4px;
  }
  
  :deep(code) {
    background-color: rgba(0, 0, 0, 0.05);
    padding: 2px 4px;
    border-radius: 3px;
    font-family: monospace;
  }
  
  :deep(pre) {
    background-color: rgba(0, 0, 0, 0.05);
    padding: 8px;
    border-radius: 5px;
    overflow-x: auto;
    margin-bottom: 8px;
    
    code {
      background-color: transparent;
      padding: 0;
    }
  }
  
  :deep(blockquote) {
    border-left: 4px solid #ddd;
    padding-left: 8px;
    color: #666;
    margin-left: 0;
    margin-right: 0;
  }
  
  :deep(a) {
    color: #0066cc;
    text-decoration: underline;
  }
  
  :deep(img) {
    max-width: 100%;
    height: auto;
  }
  
  :deep(table) {
    border-collapse: collapse;
    width: 100%;
    margin-bottom: 8px;
    
    th, td {
      border: 1px solid #ddd;
      padding: 6px;
    }
    
    th {
      background-color: #f2f2f2;
    }
  }
}
</style>