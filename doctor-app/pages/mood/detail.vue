<template>
  <view class="container">
    <!-- 量表基本信息 -->
    <view class="scale-header">
      <text class="scale-name">{{ scale.scaleName }}</text>
      <text class="scale-type">{{ getScaleTypeName(scale.scaleType) }}</text>
      <text class="scale-desc">{{ scale.scaleDescription }}</text>
    </view>
    
    <!-- 量表问题列表 -->
    <view class="scale-content">
      <view class="tips">
        <text>请仔细阅读每个问题，选择最符合您情况的选项</text>
      </view>
      
      <view class="question-list">
        <view class="question-item" v-for="(question, index) in questions" :key="question.questionId">
          <view class="question-header">
            <text class="question-index">{{ index + 1 }}</text>
            <text class="question-title">{{ question.questionContent }}</text>
          </view>
          
          <!-- 单选题 -->
          <view class="options" v-if="question.questionType === '1'">
            <view 
              class="option-item" 
              v-for="(option, optIndex) in parseOptions(question.questionOptions)" 
              :key="optIndex"
              :class="{ active: answers[question.questionId] === option.value }"
              @tap.stop="selectOption(question.questionId, option.value, question.questionType)"
              @click.stop="selectOption(question.questionId, option.value, question.questionType)"
              hover-class="option-hover"
              hover-stay-time="50"
              hover-start-time="0"
            >
              <view class="option-radio" :class="{ checked: answers[question.questionId] === option.value }">
                <view class="radio-inner" v-if="answers[question.questionId] === option.value"></view>
              </view>
              <text class="option-label">{{ option.label }}</text>
            </view>
          </view>
          
          <!-- 多选题 -->
          <view class="options" v-else-if="question.questionType === '2'">
            <view 
              class="option-item" 
              v-for="(option, optIndex) in parseOptions(question.questionOptions)" 
              :key="optIndex"
              :class="{ active: isOptionSelected(question.questionId, option.value) }"
              @tap.stop="selectOption(question.questionId, option.value, question.questionType)"
              @click.stop="selectOption(question.questionId, option.value, question.questionType)"
              hover-class="option-hover"
              hover-stay-time="50"
              hover-start-time="0"
            >
              <view class="option-checkbox" :class="{ checked: isOptionSelected(question.questionId, option.value) }">
                <view class="checkbox-inner" v-if="isOptionSelected(question.questionId, option.value)">✓</view>
              </view>
              <text class="option-label">{{ option.label }}</text>
            </view>
          </view>
          
          <!-- 填空题 -->
          <view class="input-wrapper" v-else-if="question.questionType === '3'">
            <input 
              type="text" 
              v-model="answers[question.questionId]" 
              placeholder="请输入您的回答" 
              class="answer-input"
            />
          </view>
        </view>
      </view>
    </view>
    
    <!-- 提交按钮 -->
    <view class="footer">
      <button class="submit-btn" @click="submitAnswers" :disabled="loading || !isCompleted">{{ loading ? '提交中...' : '提交测评' }}</button>
      <text class="tips-text" v-if="!isCompleted">请完成所有问题后提交</text>
    </view>
  </view>
</template>

<script>
import { getScaleDetail, getScaleQuestions, submitScaleRecord } from '@/api/mood/scale'

export default {
  data() {
    return {
      scaleId: null,
      scale: {},
      questions: [],
      answers: {},  // 存储答案，格式：{ questionId: answer }
      multiAnswers: {}, // 多选题答案，格式：{ questionId: [answer1, answer2] }
      loading: false,
      isNormalUser: false // 是否为普通用户标志
    }
  },
  computed: {
    isCompleted() {
      if (!this.questions || this.questions.length === 0) {
        return false
      }
      
      for (const question of this.questions) {
        // 单选和填空
        if (question.questionType === '1' || question.questionType === '3') {
          if (!this.answers[question.questionId]) {
            return false
          }
        } 
        // 多选
        else if (question.questionType === '2') {
          const selected = this.multiAnswers[question.questionId] || []
          if (selected.length === 0) {
            return false
          }
        }
      }
      
      return true
    }
  },
  onLoad(options) {
    if (options.id) {
      this.scaleId = options.id
      this.getScaleInfo()
      this.getQuestions()
      
      // 获取当前用户角色
      this.checkUserRole()
    } else {
      uni.showToast({
        title: '参数错误',
        icon: 'none'
      })
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    }
  },
  methods: {
    // 检查用户角色
    checkUserRole() {
      try {
        const userInfo = uni.getStorageSync('userInfo') || {}
        // 允许所有用户填写量表，将isNormalUser设置为false
        this.isNormalUser = false
      } catch (error) {
        console.error('获取用户信息失败', error)
        // 默认也允许填写
        this.isNormalUser = false
      }
    },
    async getScaleInfo() {
      try {
        const res = await getScaleDetail(this.scaleId)
        if (res.code === 200) {
          this.scale = res.data || {}
        } else {
          uni.showToast({
            title: '获取量表信息失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('获取量表信息失败', error)
        uni.showToast({
          title: '获取量表信息失败',
          icon: 'none'
        })
      }
    },
    
    async getQuestions() {
      try {
        const res = await getScaleQuestions(this.scaleId)
        if (res.code === 200) {
          this.questions = res.data || []
          
          // 初始化多选题答案
          this.questions.forEach(question => {
            if (question.questionType === '2') {
              this.multiAnswers[question.questionId] = []
            }
          })
        } else {
          uni.showToast({
            title: '获取问题列表失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('获取问题列表失败', error)
        uni.showToast({
          title: '获取问题列表失败',
          icon: 'none'
        })
      }
    },
    
    // 解析选项JSON
    parseOptions(optionsStr) {
      try {
        if (!optionsStr) {
          return []
        }
        return JSON.parse(optionsStr)
      } catch (error) {
        console.error('解析选项失败', error)
        return []
      }
    },
    
    // 选择选项
    selectOption(questionId, value, type) {
      console.log('选项被点击', questionId, value, type);
      
      try {
        if (type === '1') {
          // 单选题
          this.$set(this.answers, questionId, value);
          console.log('单选题答案已更新', this.answers);
        } else if (type === '2') {
          // 多选题
          if (!this.multiAnswers[questionId]) {
            this.$set(this.multiAnswers, questionId, []);
          }
          
          const index = this.multiAnswers[questionId].indexOf(value);
          if (index === -1) {
            // 添加选项
            this.multiAnswers[questionId].push(value);
          } else {
            // 移除选项
            this.multiAnswers[questionId].splice(index, 1);
          }
          console.log('多选题答案已更新', this.multiAnswers);
        }
        
        // 触发视图更新
        this.$forceUpdate();
      } catch (error) {
        console.error('选择选项出错:', error);
      }
    },
    
    // 判断多选题选项是否被选中
    isOptionSelected(questionId, value) {
      const selected = this.multiAnswers[questionId] || []
      return selected.includes(value)
    },
    
    // 计算得分
    calculateScore() {
      let totalScore = 0
      
      this.questions.forEach(question => {
        if (question.questionType === '1' || question.questionType === '2') {
          try {
            const scoreRules = JSON.parse(question.scoreRules || '{}')
            
            if (question.questionType === '1') {
              // 单选题得分
              const answer = this.answers[question.questionId]
              if (answer && scoreRules[answer]) {
                totalScore += parseInt(scoreRules[answer]) || 0
              }
            } else if (question.questionType === '2') {
              // 多选题得分
              const selectedOptions = this.multiAnswers[question.questionId] || []
              selectedOptions.forEach(opt => {
                if (scoreRules[opt]) {
                  totalScore += parseInt(scoreRules[opt]) || 0
                }
              })
            }
          } catch (error) {
            console.error('计算得分出错', error)
          }
        }
      })
      
      return totalScore
    },
    
    // 获取评估结果
    getEvaluationResult(score) {
      // 根据量表类型和分数评估结果
      if (this.scale.scaleType === '1') {  // 情绪评估
        if (score < 30) {
          return '情绪状态良好'
        } else if (score < 50) {
          return '轻度情绪波动'
        } else if (score < 70) {
          return '中度情绪困扰'
        } else {
          return '严重情绪问题'
        }
      } else if (this.scale.scaleType === '2') {  // 心理测试
        if (score < 40) {
          return '心理状态健康'
        } else if (score < 60) {
          return '轻度心理压力'
        } else if (score < 80) {
          return '中度心理问题'
        } else {
          return '严重心理困扰'
        }
      } else {  // 状态检测
        if (score < 35) {
          return '状态良好'
        } else if (score < 55) {
          return '状态轻度异常'
        } else if (score < 75) {
          return '状态中度异常'
        } else {
          return '状态严重异常'
        }
      }
    },
    
    // 提交答案
    async submitAnswers() {
      if (!this.isCompleted) {
        uni.showToast({
          title: '请完成所有问题',
          icon: 'none'
        })
        return
      }
      
      try {
        this.loading = true
        
        // 合并多选题答案
        const answerContent = {}
        for (const questionId in this.answers) {
          answerContent[questionId] = this.answers[questionId]
        }
        
        for (const questionId in this.multiAnswers) {
          if (this.multiAnswers[questionId] && this.multiAnswers[questionId].length > 0) {
            answerContent[questionId] = this.multiAnswers[questionId]
          }
        }
        
        // 计算得分
        const totalScore = this.calculateScore()
        
        // 获取评估结果
        const evaluationResult = this.getEvaluationResult(totalScore)
        
        const data = {
          scaleId: this.scaleId,
          totalScore: totalScore,
          answerContent: JSON.stringify(answerContent),
          evaluationResult: evaluationResult
        }
        
        // 尝试提交到服务器
        try {
          const res = await submitScaleRecord(data)
          if (res.code === 200 && res.data) {
            uni.showToast({
              title: '提交成功',
              icon: 'success'
            })
            
            // 跳转到结果页
            setTimeout(() => {
              uni.redirectTo({
                url: `/pages/mood/result?id=${res.data}&score=${totalScore}&result=${encodeURIComponent(evaluationResult)}`
              })
            }, 1500)
            return
          } 
        } catch (serverError) {
          console.error('服务器提交失败，使用本地存储', serverError)
        }
        
        // 服务器提交失败，使用本地存储记录
        try {
          // 生成本地记录ID
          const localRecordId = 'local_' + Date.now()
          // 创建本地记录数据
          const localRecord = {
            recordId: localRecordId,
            scaleId: this.scaleId,
            scaleName: this.scale.scaleName,
            totalScore: totalScore,
            evaluationResult: evaluationResult,
            completeTime: new Date().toISOString(),
            answerContent: JSON.stringify(answerContent)
          }
          
          // 获取已有记录
          let localRecords = uni.getStorageSync('local_mood_records') || []
          localRecords.push(localRecord)
          
          // 保存到本地
          uni.setStorageSync('local_mood_records', localRecords)
          
          uni.showToast({
            title: '已保存到本地',
            icon: 'success'
          })
          
          // 跳转到结果页，使用本地标记
          setTimeout(() => {
            uni.redirectTo({
              url: `/pages/mood/result?id=${localRecordId}&score=${totalScore}&result=${encodeURIComponent(evaluationResult)}&local=true`
            })
          }, 1500)
        } catch (storageError) {
          console.error('本地保存失败', storageError)
          
          // 即使本地存储失败，也尝试直接显示结果
          setTimeout(() => {
            uni.redirectTo({
              url: `/pages/mood/result?score=${totalScore}&result=${encodeURIComponent(evaluationResult)}`
            })
          }, 1000)
          
          uni.showToast({
            title: '保存失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('提交答案失败', error)
        uni.showToast({
          title: '提交失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    
    // 获取量表类型名称
    getScaleTypeName(type) {
      const types = {
        '1': '情绪评估',
        '2': '心理测试',
        '3': '状态检测'
      }
      return types[type] || '未知类型'
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  padding: 20rpx;
  background-color: #f8f8f8;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.scale-header {
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .scale-name {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 15rpx;
    display: block;
  }
  
  .scale-type {
    display: inline-block;
    font-size: 24rpx;
    padding: 6rpx 16rpx;
    background-color: #ecf5ff;
    color: #409eff;
    border-radius: 20rpx;
    margin-bottom: 20rpx;
  }
  
  .scale-desc {
    font-size: 28rpx;
    color: #666;
    line-height: 1.5;
  }
}

.scale-content {
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  flex: 1;
  
  .tips {
    padding: 20rpx;
    background-color: #f0f9eb;
    border-radius: 8rpx;
    margin-bottom: 30rpx;
    
    text {
      font-size: 26rpx;
      color: #67c23a;
    }
  }
  
  .question-list {
    .question-item {
      margin-bottom: 40rpx;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .question-header {
        display: flex;
        align-items: flex-start;
        margin-bottom: 20rpx;
        
        .question-index {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 40rpx;
          height: 40rpx;
          background-color: #409eff;
          color: #ffffff;
          border-radius: 50%;
          font-size: 22rpx;
          margin-right: 15rpx;
          flex-shrink: 0;
        }
        
        .question-title {
          font-size: 30rpx;
          color: #333;
          line-height: 1.5;
          flex: 1;
        }
      }
      
      .options {
        padding-left: 55rpx;
        
        .option-item {
          display: flex;
          align-items: center;
          padding: 28rpx 24rpx;
          border: 2rpx solid #e0e0e0;
          border-radius: 12rpx;
          margin-bottom: 24rpx;
          transition: all 0.2s;
          position: relative;
          z-index: 1;
          cursor: pointer;
          -webkit-tap-highlight-color: transparent;
          box-shadow: 0 2rpx 6rpx rgba(0,0,0,0.03);
          
          &::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            z-index: 2;
          }
          
          &:last-child {
            margin-bottom: 0;
          }
          
          &.active {
            border-color: #409eff;
            background-color: #ecf5ff;
            box-shadow: 0 2rpx 8rpx rgba(64,158,255,0.2);
          }
          
          &:active {
            opacity: 0.85;
            transform: scale(0.99);
          }
          
          .option-label {
            font-size: 30rpx;
            color: #333;
            flex: 1;
            padding-left: 10rpx;
          }
        }
        
        .option-radio, .option-checkbox {
          width: 44rpx;
          height: 44rpx;
          border-radius: 50%;
          border: 2rpx solid #ddd;
          margin-right: 20rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
          background-color: #fff;
          
          &.checked {
            border-color: #409eff;
            background-color: #fff;
          }
        }
        
        .option-checkbox {
          border-radius: 6rpx;
        }
        
        .radio-inner {
          width: 28rpx;
          height: 28rpx;
          border-radius: 50%;
          background-color: #409eff;
        }
        
        .checkbox-inner {
          color: #409eff;
          font-size: 28rpx;
          line-height: 1;
          font-weight: bold;
        }
      }
      
      .input-wrapper {
        padding-left: 55rpx;
        
        .answer-input {
          width: 100%;
          height: 80rpx;
          border: 2rpx solid #e0e0e0;
          border-radius: 8rpx;
          padding: 0 20rpx;
          font-size: 28rpx;
        }
      }
    }
  }
}

.footer {
  padding: 30rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .submit-btn {
    width: 90%;
    height: 90rpx;
    background-color: #409eff;
    color: #ffffff;
    border-radius: 45rpx;
    font-size: 32rpx;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .tips-text {
    font-size: 24rpx;
    color: #999;
    margin-top: 10rpx;
  }
}

.option-hover {
  background-color: #f0f9ff !important;
  border-color: #a0cfff !important;
  opacity: 0.9;
}
</style> 