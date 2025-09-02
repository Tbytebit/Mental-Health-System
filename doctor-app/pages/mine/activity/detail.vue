<template>
	<view class="diary-container">
		<!-- 心情选择区 -->
		<view class="mood-selection">
			<view class="mood-title">今天的心情</view>
			<view class="mood-icons">
				<mood-icon-base64 
					v-for="moodType in moodTypes" 
					:key="moodType" 
					:mood="moodType" 
					:selected="mood === moodType"
					:showLabel="true"
					:size="60"
					@click="selectMood(moodType)"
				/>
			</view>
		</view>
		
		<!-- 日记标题 -->
		<view class="diary-title-box" :class="{'diary-title-active': titleFocused}">
			<input
				class="diary-title-input"
				v-model="title"
				placeholder="请输入日记标题"
				@focus="titleFocused = true"
				@blur="titleFocused = false"
			/>
		</view>
		
		<!-- 日记内容 -->
		<view class="diary-content-box">
			<textarea
				class="diary-content-input"
				v-model="content"
				placeholder="今天发生了什么..."
				:auto-height="true"
				:show-confirm-bar="false"
			></textarea>
		</view>
		
		<!-- 底部操作栏 -->
		<view class="diary-footer">
			<view class="diary-date">{{ formattedDate }}</view>
			<view class="diary-save-btn" @click="saveDiary">保存</view>
		</view>
	</view>
</template>

<script>
	import MoodIconBase64 from '../../../components/mood-icon-base64.vue';
	import { getActivity, saveActivity } from '@/api/maincode/activity';
	
	export default {
		components: {
			MoodIconBase64
		},
		data() {
			return {
				id: null,
				title: '',
				content: '',
				mood: 'happy',
				date: new Date(),
				titleFocused: false,
				moodTypes: ['happy', 'neutral', 'sad', 'angry', 'anxious']
			};
		},
		computed: {
			formattedDate() {
				const date = new Date(this.date);
				return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
			}
		},
		onLoad(options) {
			// 如果是编辑现有日记
			if (options.id) {
				this.id = options.id;
				this.loadDiary(options.id);
			}
		},
		methods: {
			selectMood(mood) {
				this.mood = mood;
				// 添加触觉反馈
				uni.vibrateShort({
					success: function() {
						console.log('振动成功');
					}
				});
				// 添加提示
				uni.showToast({
					title: '已选择' + this.moodLabel(mood),
					icon: 'none',
					duration: 500
				});
			},
			
			moodLabel(mood) {
				const labels = {
					happy: '开心',
					neutral: '平静',
					sad: '难过',
					angry: '生气',
					anxious: '焦虑'
				};
				return labels[mood] || '开心';
			},
			loadDiary(id) {
				// 从API加载数据
				getActivity(id).then(res => {
					if (res.code === 0 && res.data) {
						const diary = res.data;
						this.title = diary.name;
						this.content = diary.details;
						this.mood = diary.mood || 'happy';
						this.date = new Date(diary.createTime);
					} else if (res.data) {
						// 处理旧格式
						const diary = res.data;
						this.title = diary.name;
						this.content = diary.details;
						this.mood = diary.mood || 'happy';
						this.date = new Date(diary.createTime);
					}
				}).catch(err => {
					console.error('加载日记失败', err);
					uni.showToast({
						title: '加载日记失败',
						icon: 'none'
					});
				});
			},
			saveDiary() {
				const diary = {
					id: this.id || Date.now().toString(),
					name: this.title,
					details: this.content,
					mood: this.mood,
					createTime: this.date.toISOString()
				};
				
				// 调用API保存
				saveActivity(diary).then(res => {
					if (res.code === 0) {
						uni.showToast({
							title: '保存成功',
							icon: 'success'
						});
						
						// 返回列表页
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					} else {
						uni.showToast({
							title: '保存失败',
							icon: 'none'
						});
					}
				}).catch(err => {
					console.error('保存日记失败', err);
					uni.showToast({
						title: '保存失败',
						icon: 'none'
					});
				});
			}
		}
	}
</script>

<style>
	.diary-container {
		padding: 30rpx;
		background-color: #f8f8f8;
		min-height: 100vh;
	}
	
	.mood-selection {
		margin-bottom: 40rpx;
	}
	
	.mood-title {
		font-size: 28rpx;
		color: #666;
		margin-bottom: 20rpx;
	}
	
	.mood-icons {
		display: flex;
		justify-content: space-between;
		padding: 0 40rpx;
	}
	
	.diary-title-box {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 20rpx 30rpx;
		margin-bottom: 30rpx;
		border: 2rpx solid #eee;
	}
	
	.diary-title-active {
		border-color: #007AFF;
		background-color: #f0f8ff;
	}
	
	.diary-title-input {
		font-size: 36rpx;
		font-weight: 500;
		color: #333;
		width: 100%;
	}
	
	.diary-content-box {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 40rpx;
		min-height: 300rpx;
		border: 2rpx solid #eee;
	}
	
	.diary-content-input {
		font-size: 30rpx;
		color: #333;
		width: 100%;
		line-height: 1.6;
	}
	
	.diary-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-top: 60rpx;
	}
	
	.diary-date {
		font-size: 28rpx;
		color: #999;
	}
	
	.diary-save-btn {
		background-color: #007AFF;
		color: #fff;
		font-size: 28rpx;
		padding: 16rpx 40rpx;
		border-radius: 30rpx;
	}
</style>