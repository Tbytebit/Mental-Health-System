<template>
  <view class="container">
    <!-- 标题栏 -->
    <view class="header">
      <view @click="goBack" class="header-back">
        <uni-icons type="back" size="24" color="#333"></uni-icons>
      </view>
      <view class="header-title">预约详情</view>
    </view>
    
    <!-- 状态栏 -->
    <view class="status-bar" :class="statusClass">
      <view class="status-icon">
        <uni-icons :type="statusIcon" size="30" color="#fff"></uni-icons>
      </view>
      <view class="status-info">
        <view class="status-text">{{ statusText }}</view>
        <view class="status-desc">{{ statusDesc }}</view>
      </view>
    </view>
    
    <!-- 预约信息 -->
    <view class="info-card">
      <view class="card-title">预约信息</view>
      
      <view class="info-item">
        <view class="item-label">预约编号</view>
        <view class="item-value">{{ appointmentInfo.appointmentId || '-' }}</view>
      </view>
      
      <view class="info-item">
        <view class="item-label">医生姓名</view>
        <view class="item-value">{{ appointmentInfo.doctorName || '-' }}</view>
      </view>
      
      <view class="info-item">
        <view class="item-label">预约日期</view>
        <view class="item-value">{{ appointmentInfo.appointmentDate || '-' }}</view>
      </view>
      
      <view class="info-item">
        <view class="item-label">预约时段</view>
        <view class="item-value">{{ appointmentInfo.appointmentTime || '-' }}</view>
      </view>
      
      <view class="info-item">
        <view class="item-label">预约状态</view>
        <view class="item-value status">{{ appointmentStatusText }}</view>
      </view>
      
      <view class="info-item" v-if="appointmentInfo.code && [1, 2].includes(appointmentInfo.appointmentStatus)">
        <view class="item-label">就诊码</view>
        <view class="item-value code">{{ appointmentInfo.code }}
          <text class="copy-btn" @click="copyCode">复制</text>
        </view>
      </view>
      
      <view class="info-item textarea-item" v-if="appointmentInfo.remark">
        <view class="item-label">问题描述</view>
        <view class="item-value textarea">{{ appointmentInfo.remark }}</view>
      </view>
    </view>
    
    <!-- 就诊提醒 -->
    <view class="notice-card" v-if="[1, 2].includes(appointmentInfo.appointmentStatus)">
      <view class="notice-title">
        <uni-icons type="info" size="18" color="#1989fa"></uni-icons>
        <text>就诊提醒</text>
      </view>
      <view class="notice-content">
        <view class="notice-item">1. 系统将在预约时间<text class="highlight">自动发送好友申请</text>给医生；</view>
        <view class="notice-item">2. 收到医生通过好友申请的通知后，请立即进入聊天页面与医生沟通；</view>
        <view class="notice-item">3. 医生完成就诊后，系统将自动结束本次会话并解除好友关系。</view>
      </view>
    </view>
    
    <!-- 操作按钮 -->
    <view class="action-container" v-if="showActions">
      <button class="action-btn cancel-btn" v-if="[1].includes(appointmentInfo.appointmentStatus)" @click="cancelAppointment">取消预约</button>
      
      <button class="action-btn comment-btn" v-if="[3, 4].includes(appointmentInfo.appointmentStatus) && !appointmentInfo.score" @click="goToComment">评价咨询</button>
      
      <button class="action-btn chat-btn" v-if="showChatButton" @click="goToChat">联系医生</button>
    </view>
    
    <!-- 提示弹窗 -->
    <uni-popup ref="messagePopup" type="dialog">
      <uni-popup-dialog
        :type="popupConfig.type"
        :title="popupConfig.title"
        :content="popupConfig.content"
        :before-close="false"
        @confirm="onPopupConfirm"
        @close="onPopupClose"
      ></uni-popup-dialog>
    </uni-popup>
  </view>
</template>

<script>
import { getAppointment, cancelAppointment } from '@/api/maincode/appointment';
import { formatDate, parseDate, isToday } from '@/utils/date';

export default {
  data() {
    return {
      appointmentId: null,
      appointmentInfo: {},
      popupConfig: {
        type: 'info',
        title: '提示',
        content: '',
        confirmAction: null
      }
    };
  },
  
  computed: {
    // 状态类和图标
    statusClass() {
      const status = this.appointmentInfo.appointmentStatus;
      if (status === 5) return 'status-bar-finished';
      if (status === 3 || status === 4) return 'status-bar-completed';
      if (status === 2) return 'status-bar-ongoing';
      if (status === 1) return 'status-bar-waiting';
      return 'status-bar-cancelled';
    },
    statusIcon() {
      const status = this.appointmentInfo.appointmentStatus;
      if (status === 5) return 'checkmarkempty';
      if (status === 3 || status === 4) return 'checkmarkempty';
      if (status === 2) return 'notification';
      if (status === 1) return 'calendar';
      return 'closeempty';
    },
    statusText() {
      return this.appointmentStatusText;
    },
    statusDesc() {
      const status = this.appointmentInfo.appointmentStatus;
      if (status === 5) return '感谢您的评价！';
      if (status === 4) return '请对本次咨询进行评价';
      if (status === 3) return '医生已完成就诊';
      if (status === 2) return '请准时参与咨询';
      if (status === 1) {
        // 计算距离预约还有多久
        if (!this.appointmentInfo.appointmentDate) return '';
        
        const appointmentDate = parseDate(this.appointmentInfo.appointmentDate);
        const today = new Date();
        
        if (appointmentDate.getTime() < today.getTime()) {
          return '预约即将开始';
        } else {
          // 计算日期差
          const diffTime = appointmentDate.getTime() - today.getTime();
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
          
          if (diffDays === 0) return '今天就医';
          if (diffDays === 1) return '明天就医';
          return `还有${diffDays}天就医`;
        }
      }
      if (status === 0) return '预约已取消';
      return '';
    },
    
    // 预约状态文本
    appointmentStatusText() {
      const status = this.appointmentInfo.appointmentStatus;
      const statusMap = {
        0: '已取消',
        1: '待就诊',
        2: '就诊中',
        3: '已就诊',
        4: '待评价',
        5: '已完成'
      };
      return statusMap[status] || '未知状态';
    },
    
    // 是否显示操作按钮
    showActions() {
      return [0, 1, 2, 3, 4].includes(this.appointmentInfo.appointmentStatus);
    },
    
    // 是否显示联系医生按钮
    showChatButton() {
      const status = this.appointmentInfo.appointmentStatus;
      return status === 2; // 就诊中状态才显示联系医生按钮
    }
  },
  
  onLoad(options) {
    this.appointmentId = options.id;
    if (!this.appointmentId) {
      uni.showToast({
        title: '未指定预约ID',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
      return;
    }
    
    this.loadAppointmentDetail();
  },
  
  methods: {
    // 加载预约详情
    async loadAppointmentDetail() {
      try {
        uni.showLoading({ title: '加载中' });
        
        const res = await getAppointment(this.appointmentId);
        
        if (res.code === 200 && res.data) {
          this.appointmentInfo = res.data;
          
          // 格式化日期
          if (this.appointmentInfo.appointmentDate) {
            this.appointmentInfo.appointmentDate = formatDate(this.appointmentInfo.appointmentDate);
          }
        } else {
          uni.showToast({
            title: res.msg || '获取预约详情失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取预约详情失败', error);
        uni.showToast({
          title: '获取预约详情失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 复制就诊码
    copyCode() {
      const code = this.appointmentInfo.code;
      if (!code) return;
      
      uni.setClipboardData({
        data: code,
        success: () => {
          uni.showToast({
            title: '已复制到剪贴板',
            icon: 'success'
          });
        }
      });
    },
    
    // 取消预约
    cancelAppointment() {
      this.popupConfig = {
        type: 'warning',
        title: '取消预约',
        content: '确定要取消此次预约吗？取消后无法恢复。',
        confirmAction: this.doCancel
      };
      this.$refs.messagePopup.open();
    },
    
    // 执行取消预约
    async doCancel() {
      try {
        uni.showLoading({ title: '取消中' });
        
        const res = await cancelAppointment(this.appointmentId);
        
        if (res.code === 200) {
          uni.showToast({
            title: '预约已取消',
            icon: 'success'
          });
          
          // 重新加载预约详情
          setTimeout(() => {
            this.loadAppointmentDetail();
          }, 1500);
        } else {
          uni.showToast({
            title: res.msg || '取消预约失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('取消预约失败', error);
        uni.showToast({
          title: '取消预约失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 前往评价页面
    goToComment() {
      uni.navigateTo({
        url: `/pages/mine/consultation/comment?id=${this.appointmentId}`
      });
    },
    
    // 前往聊天页面
    goToChat() {
      const doctorId = this.appointmentInfo.doctorId;
      const doctorName = this.appointmentInfo.doctorName || '医生';
      
      if (!doctorId) {
        uni.showToast({
          title: '无法获取医生信息',
          icon: 'none'
        });
        return;
      }
      
      // 构建会话ID
      const userId = uni.getStorageSync('userId');
      let conversationId = '';
      
      if (parseInt(userId) < parseInt(doctorId)) {
        conversationId = `${userId}_${doctorId}`;
      } else {
        conversationId = `${doctorId}_${userId}`;
      }
      
      // 导航到聊天页面
      uni.navigateTo({
        url: `/pages/chat/chat?friendId=${doctorId}&friendName=${encodeURIComponent(doctorName)}&conversationId=${conversationId}`
      });
    },
    
    // 弹窗确认回调
    onPopupConfirm() {
      if (typeof this.popupConfig.confirmAction === 'function') {
        this.popupConfig.confirmAction();
      }
      this.$refs.messagePopup.close();
    },
    
    // 弹窗关闭回调
    onPopupClose() {
      // 清空确认动作
      this.popupConfig.confirmAction = null;
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack();
    }
  }
};
</script>

<style lang="scss">
.container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f7f7f7;
  padding-bottom: 30rpx;
}

.header {
  display: flex;
  align-items: center;
  height: 88rpx;
  background-color: #ffffff;
  padding: 0 30rpx;
  border-bottom: 1rpx solid #eaeaea;
}

.header-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.header-title {
  flex: 1;
  text-align: center;
  font-size: 36rpx;
  font-weight: 500;
  margin-right: 60rpx;
}

.status-bar {
  display: flex;
  padding: 40rpx 30rpx;
  align-items: center;
  background-color: #1989fa;
  color: #ffffff;
}

.status-bar-waiting {
  background-color: #1989fa;
}

.status-bar-ongoing {
  background-color: #ff9500;
}

.status-bar-completed {
  background-color: #34c759;
}

.status-bar-finished {
  background-color: #5e5ce6;
}

.status-bar-cancelled {
  background-color: #8e8e93;
}

.status-icon {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 40rpx;
  margin-right: 20rpx;
}

.status-info {
  flex: 1;
}

.status-text {
  font-size: 36rpx;
  font-weight: 500;
  margin-bottom: 10rpx;
}

.status-desc {
  font-size: 26rpx;
  opacity: 0.9;
}

.info-card {
  margin: 20rpx;
  padding: 30rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
  padding-bottom: 20rpx;
}

.info-item {
  display: flex;
  margin-bottom: 20rpx;
}

.item-label {
  width: 180rpx;
  font-size: 28rpx;
  color: #666;
}

.item-value {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.item-value.status {
  color: #1989fa;
  font-weight: 500;
}

.item-value.code {
  font-weight: 500;
}

.copy-btn {
  display: inline-block;
  font-size: 24rpx;
  color: #1989fa;
  margin-left: 20rpx;
  padding: 4rpx 10rpx;
  border: 1rpx solid #1989fa;
  border-radius: 4rpx;
}

.textarea-item {
  align-items: flex-start;
}

.item-value.textarea {
  background-color: #f9f9f9;
  padding: 16rpx;
  border-radius: 8rpx;
  line-height: 1.5;
}

.notice-card {
  margin: 0 20rpx 20rpx;
  padding: 30rpx;
  background-color: #f0f8ff;
  border-radius: 12rpx;
  border-left: 8rpx solid #1989fa;
}

.notice-title {
  display: flex;
  align-items: center;
  font-size: 28rpx;
  font-weight: 500;
  color: #1989fa;
  margin-bottom: 20rpx;
}

.notice-title text {
  margin-left: 10rpx;
}

.notice-content {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
}

.notice-item {
  margin-bottom: 10rpx;
}

.highlight {
  color: #ff3b30;
  font-weight: 500;
}

.action-container {
  margin: 40rpx 20rpx 20rpx;
  display: flex;
  justify-content: center;
}

.action-btn {
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 30rpx;
  margin: 0 10rpx;
  text-align: center;
  padding: 0 40rpx;
}

.cancel-btn {
  background-color: #f2f2f7;
  color: #8e8e93;
}

.comment-btn {
  background-color: #ff9500;
  color: #ffffff;
}

.chat-btn {
  background-color: #1989fa;
  color: #ffffff;
}
</style> 