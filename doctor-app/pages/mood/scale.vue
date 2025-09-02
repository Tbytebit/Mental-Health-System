<template>
	<view class="scale-container">
		<!-- 顶部进度条 -->
		<view class="progress-bar">
			<view class="progress-inner" :style="{width: `${progress}%`}"></view>
		</view>
		<view class="progress-text">
			<text>{{currentIndex + 1}}/{{questions.length}}</text>
		</view>
		
		<!-- 量表信息 -->
		<view class="scale-info">
			<text class="scale-title">{{scaleInfo.title}}</text>
			<text class="scale-desc">{{scaleInfo.description}}</text>
		</view>
		
		<!-- 问题区域 -->
		<view class="question-card">
			<view class="question-number">问题 {{currentIndex + 1}}</view>
			<view class="question-content">
				<text>{{currentQuestion.content}}</text>
			</view>
			
			<!-- 选项列表 -->
			<view class="options-list">
				<view 
					class="option-item" 
					:class="{selected: selectedOption === index}"
					v-for="(option, index) in currentQuestion.options" 
					:key="index"
					@click="selectOption(index)"
				>
					<view class="option-radio" :class="{checked: selectedOption === index}">
						<view class="radio-inner" v-if="selectedOption === index"></view>
					</view>
					<text class="option-text">{{option.text}}</text>
				</view>
			</view>
		</view>
		
		<!-- 操作按钮 -->
		<view class="action-buttons">
			<button 
				class="prev-btn" 
				:disabled="currentIndex === 0"
				@click="prevQuestion"
			>
				上一题
			</button>
			<button 
				class="next-btn" 
				:disabled="selectedOption === -1"
				@click="nextQuestion"
			>
				{{isLastQuestion ? '完成' : '下一题'}}
			</button>
		</view>
		
		<!-- 提示信息 -->
		<view class="tip-box" v-if="showTip">
			<text class="tip-text">请选择一个选项后继续</text>
		</view>
	</view>
</template>

<script>
import { getScaleDetail, getScaleQuestions, submitScaleRecord } from '@/api/mood/scale'

export default {
	data() {
		return {
			scaleId: null,
			scaleInfo: {},
			questions: [],
			currentIndex: 0,
			selectedOption: -1,
			answers: [],
			showTip: false,
			// 示例数据
			scaleData: {
				1: {
					title: 'SDS抑郁自评量表',
					description: '请根据您最近一周的实际感受选择最符合的选项',
					questions: [
						{
							content: '我感到情绪沮丧，郁闷',
							options: [
								{ text: '没有或很少时间', value: 1 },
								{ text: '小部分时间', value: 2 },
								{ text: '相当多时间', value: 3 },
								{ text: '绝大部分或全部时间', value: 4 }
							]
						},
						{
							content: '我感到早晨心情最好',
							options: [
								{ text: '没有或很少时间', value: 4 },
								{ text: '小部分时间', value: 3 },
								{ text: '相当多时间', value: 2 },
								{ text: '绝大部分或全部时间', value: 1 }
							]
						},
						{
							content: '我常常哭出来或想哭',
							options: [
								{ text: '没有或很少时间', value: 1 },
								{ text: '小部分时间', value: 2 },
								{ text: '相当多时间', value: 3 },
								{ text: '绝大部分或全部时间', value: 4 }
							]
						},
						{
							content: '我晚上睡眠不好',
							options: [
								{ text: '没有或很少时间', value: 1 },
								{ text: '小部分时间', value: 2 },
								{ text: '相当多时间', value: 3 },
								{ text: '绝大部分或全部时间', value: 4 }
							]
						},
						{
							content: '我吃得跟平常一样多',
							options: [
								{ text: '没有或很少时间', value: 4 },
								{ text: '小部分时间', value: 3 },
								{ text: '相当多时间', value: 2 },
								{ text: '绝大部分或全部时间', value: 1 }
							]
						}
					]
				},
				2: {
					title: 'SAS焦虑自评量表',
					description: '请根据您最近一周的实际感受选择最符合的选项',
					questions: [
						{
							content: '我感到比平常容易紧张或着急',
							options: [
								{ text: '没有或很少时间', value: 1 },
								{ text: '小部分时间', value: 2 },
								{ text: '相当多时间', value: 3 },
								{ text: '绝大部分或全部时间', value: 4 }
							]
						},
						{
							content: '我无缘无故地感到害怕',
							options: [
								{ text: '没有或很少时间', value: 1 },
								{ text: '小部分时间', value: 2 },
								{ text: '相当多时间', value: 3 },
								{ text: '绝大部分或全部时间', value: 4 }
							]
						},
						{
							content: '我容易心里烦乱或感到惊恐',
							options: [
								{ text: '没有或很少时间', value: 1 },
								{ text: '小部分时间', value: 2 },
								{ text: '相当多时间', value: 3 },
								{ text: '绝大部分或全部时间', value: 4 }
							]
						},
						{
							content: '我感到我可能将要发疯',
							options: [
								{ text: '没有或很少时间', value: 1 },
								{ text: '小部分时间', value: 2 },
								{ text: '相当多时间', value: 3 },
								{ text: '绝大部分或全部时间', value: 4 }
							]
						},
						{
							content: '我手脚发抖和颤动',
							options: [
								{ text: '没有或很少时间', value: 1 },
								{ text: '小部分时间', value: 2 },
								{ text: '相当多时间', value: 3 },
								{ text: '绝大部分或全部时间', value: 4 }
							]
						}
					]
				}
			}
		}
	},
	onLoad(options) {
		this.scaleId = parseInt(options.id) || 1;
		this.loadScaleData();
	},
	computed: {
		currentQuestion() {
			return this.questions[this.currentIndex] || {};
		},
		isLastQuestion() {
			return this.currentIndex === this.questions.length - 1;
		},
		progress() {
			return this.questions.length > 0 ? ((this.currentIndex + 1) / this.questions.length) * 100 : 0;
		}
	},
	methods: {
		async loadScaleData() {
			try {
				uni.showLoading({
					title: '加载量表...'
				});
				
				// 获取量表详情
				const scaleRes = await getScaleDetail(this.scaleId);
				if (scaleRes.code !== 200 || !scaleRes.data) {
					throw new Error('量表数据获取失败');
				}
				
				// 设置量表基本信息
				this.scaleInfo = {
					title: scaleRes.data.scaleName,
					description: scaleRes.data.scaleDesc || '请根据您的实际情况，选择最符合的选项。'
				};
				
				// 获取量表问题
				const questionsRes = await getScaleQuestions(this.scaleId);
				if (questionsRes.code !== 200 || !questionsRes.data) {
					throw new Error('量表问题获取失败');
				}
				
				// 格式化问题数据
				this.questions = questionsRes.data.map(q => {
					// 解析选项数据（从questionOptions字段获取）
					let options = [];
					try {
						if (q.questionOptions) {
							console.log(`问题${q.questionId}原始选项数据:`, q.questionOptions);
							options = JSON.parse(q.questionOptions);
							console.log(`问题${q.questionId}解析后选项:`, options);
						} else {
							console.warn(`问题${q.questionId}没有选项数据`);
						}
					} catch (e) {
						console.error(`解析问题${q.questionId}选项失败:`, e);
						// 默认选项
						options = [
							{ text: '完全不符合', value: 0 },
							{ text: '有点符合', value: 1 },
							{ text: '中度符合', value: 2 },
							{ text: '很符合', value: 3 },
							{ text: '完全符合', value: 4 }
						];
					}
					
					return {
						id: q.questionId,
						content: q.questionContent,
						orderNum: q.orderNum,
						options: options
					};
				});
				
				// 按顺序排序问题
				this.questions.sort((a, b) => a.orderNum - b.orderNum);
				
				// 初始化答案数组
				this.answers = new Array(this.questions.length).fill(-1);
				
				uni.hideLoading();
			} catch (error) {
				console.error('加载量表数据失败', error);
				uni.hideLoading();
				
				// 错误处理：尝试使用示例数据
				if (this.scaleData && this.scaleData[this.scaleId]) {
					uni.showToast({
						title: '使用本地数据',
						icon: 'none'
					});
					
					const scaleData = this.scaleData[this.scaleId];
					this.scaleInfo = {
						title: scaleData.title,
						description: scaleData.description
					};
					this.questions = scaleData.questions;
					this.answers = new Array(this.questions.length).fill(-1);
				} else {
					uni.showToast({
						title: '量表数据加载失败',
						icon: 'none'
					});
					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				}
			}
		},
		selectOption(index) {
			this.selectedOption = index;
			this.answers[this.currentIndex] = this.currentQuestion.options[index].value;
		},
		prevQuestion() {
			if (this.currentIndex > 0) {
				this.currentIndex--;
				this.selectedOption = this.answers[this.currentIndex] !== -1 ? 
					this.currentQuestion.options.findIndex(opt => opt.value === this.answers[this.currentIndex]) : -1;
			}
		},
		nextQuestion() {
			if (this.selectedOption === -1) {
				this.showTip = true;
				setTimeout(() => {
					this.showTip = false;
				}, 1500);
				return;
			}
			
			if (this.isLastQuestion) {
				this.submitAnswers();
			} else {
				this.currentIndex++;
				this.selectedOption = this.answers[this.currentIndex] !== -1 ? 
					this.currentQuestion.options.findIndex(opt => opt.value === this.answers[this.currentIndex]) : -1;
			}
		},
		submitAnswers() {
			// 计算总分
			const totalScore = this.answers.reduce((sum, value) => sum + value, 0);
			
			// 构建键值对格式的答题数据 - 与后端存储格式保持一致
			const answerObj = {};
			
			// 将每个问题ID和答案值构建成键值对
			for (let i = 0; i < this.questions.length; i++) {
				const question = this.questions[i];
				const questionId = question.id || (i + 1);
				const selectedValue = this.answers[i];
				
				// 确保值不为空且类型正确
				if (selectedValue !== undefined && selectedValue !== null && selectedValue !== -1) {
					// 将问题ID作为键，选择的值作为值
					answerObj[questionId.toString()] = selectedValue.toString();
				}
			}
			
			// 记录日志以便调试
			console.log('问题数据:', this.questions);
			console.log('答案数组:', this.answers);
			console.log('提交的答案对象:', answerObj);
			
			// 准备提交的记录数据
			const recordData = {
				scaleId: this.scaleId,
				totalScore: totalScore,
				answerContent: JSON.stringify(answerObj)
			};
			
			console.log('提交的数据:', recordData);
			
			// 显示加载提示
			uni.showLoading({
				title: '正在分析结果...'
			});
			
			// 提交到后端
			submitScaleRecord(recordData).then(res => {
				uni.hideLoading();
				
				if (res.code === 200) {
					// 提交成功，跳转到结果页面
					const recordId = res.data;
					uni.navigateTo({
						url: `/pages/mood/result?id=${recordId}`
					});
				} else {
					// 提交失败，显示错误信息
					uni.showToast({
						title: res.msg || '提交失败，请稍后再试',
						icon: 'none',
						duration: 3000
					});
					console.error('提交失败:', res);
				}
			}).catch(error => {
				uni.hideLoading();
				console.error('提交量表记录失败', error);
				
				// 本地测试模式：保存到本地并跳转
				if (process.env.NODE_ENV === 'development') {
					const localRecords = uni.getStorageSync('local_mood_records') || [];
					const newRecord = {
						recordId: 'local_' + Date.now(),
						scaleId: this.scaleId,
						scaleName: this.scaleInfo.title,
						totalScore: totalScore,
						evaluationResult: '测试环境，未连接到服务器。模拟结果：' + (totalScore < 50 ? '正常范围' : totalScore < 70 ? '轻度异常' : '严重异常'),
						completeTime: new Date().toISOString(),
						answerContent: JSON.stringify(answerObj) // 使用与后端一致的格式
					};
					
					localRecords.push(newRecord);
					uni.setStorageSync('local_mood_records', localRecords);
					
					uni.navigateTo({
						url: `/pages/mood/result?id=${newRecord.recordId}&local=true`
					});
				} else {
					uni.showToast({
						title: '提交失败，请稍后再试',
						icon: 'none',
						duration: 3000
					});
				}
			});
		}
	}
}
</script>

<style lang="scss">
@import '@/static/scss/theme.scss';

.scale-container {
	padding: 30rpx;
	background-color: #f8f8f8;
	min-height: 100vh;
}

.progress-bar {
	height: 10rpx;
	background-color: #e0e0e0;
	border-radius: 10rpx;
	overflow: hidden;
	margin-bottom: 10rpx;
}

.progress-inner {
	height: 100%;
	background-color: $primary-color;
	border-radius: 10rpx;
	transition: width 0.3s ease;
}

.progress-text {
	text-align: right;
	font-size: 24rpx;
	color: $text-secondary;
	margin-bottom: 30rpx;
}

.scale-info {
	margin-bottom: 30rpx;
}

.scale-title {
	font-size: 36rpx;
	font-weight: bold;
	color: $text-primary;
	margin-bottom: 10rpx;
	display: block;
}

.scale-desc {
	font-size: 28rpx;
	color: $text-secondary;
	line-height: 1.5;
}

.question-card {
	background-color: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 30rpx;
	@include card-shadow;
}

.question-number {
	font-size: 24rpx;
	color: $primary-color;
	margin-bottom: 20rpx;
}

.question-content {
	font-size: 32rpx;
	color: $text-primary;
	font-weight: 500;
	margin-bottom: 40rpx;
	line-height: 1.5;
}

.options-list {
	margin-top: 30rpx;
}

.option-item {
	display: flex;
	align-items: center;
	padding: 24rpx;
	border-radius: 12rpx;
	margin-bottom: 20rpx;
	background-color: #f9f9f9;
	@include transition-all;
	
	&.selected {
		background-color: rgba($primary-color, 0.1);
	}
	
	&:active {
		transform: scale(0.98);
	}
}

.option-radio {
	width: 40rpx;
	height: 40rpx;
	border-radius: 50%;
	border: 2rpx solid #ccc;
	margin-right: 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	
	&.checked {
		border-color: $primary-color;
	}
}

.radio-inner {
	width: 24rpx;
	height: 24rpx;
	border-radius: 50%;
	background-color: $primary-color;
}

.option-text {
	font-size: 28rpx;
	color: $text-primary;
	flex: 1;
}

.action-buttons {
	display: flex;
	justify-content: space-between;
	margin-top: 40rpx;
}

.prev-btn, .next-btn {
	width: 45%;
	height: 80rpx;
	line-height: 80rpx;
	text-align: center;
	border-radius: 40rpx;
	font-size: 28rpx;
}

.prev-btn {
	background-color: #f5f5f5;
	color: $text-secondary;
	
	&:disabled {
		opacity: 0.5;
	}
}

.next-btn {
	background-color: $primary-color;
	color: #fff;
	
	&:disabled {
		opacity: 0.5;
	}
}

.tip-box {
	position: fixed;
	bottom: 100rpx;
	left: 50%;
	transform: translateX(-50%);
	background-color: rgba(0, 0, 0, 0.7);
	padding: 20rpx 40rpx;
	border-radius: 40rpx;
	z-index: 999;
}

.tip-text {
	color: #fff;
	font-size: 28rpx;
}
</style>