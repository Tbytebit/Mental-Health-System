<template>
  <view class="container">
    <!-- 标题栏 -->
    <view class="header">
      <view @click="goBack" class="header-back">
        <uni-icons type="back" size="24" color="#333"></uni-icons>
      </view>
      <view class="header-title">预约心理咨询</view>
    </view>
    
    <!-- 医生信息 -->
    <view class="doctor-info" v-if="doctorInfo">
      <image class="doctor-avatar" :src="doctorInfo.avatar || '/static/images/default-avatar.png'"></image>
      <view class="doctor-detail">
        <view class="doctor-name">{{ doctorInfo.doctorName || '医生' }} <text class="doctor-title">{{ doctorInfo.title || '心理医生' }}</text></view>
        <view class="doctor-specialty">{{ doctorInfo.specialty || '心理健康咨询' }}</view>
      </view>
    </view>
    
    <!-- 预约表单 -->
    <view class="form-container">
      <!-- 预约日期 -->
      <view class="form-item">
        <view class="form-label required">预约日期</view>
        <picker mode="date" :value="formData.appointmentDate" :start="startDate" :end="endDate" @change="onDateChange">
          <view class="picker-value">
            <text>{{ formData.appointmentDate || '请选择预约日期' }}</text>
            <uni-icons type="right" size="16" color="#666"></uni-icons>
          </view>
        </picker>
      </view>
      
      <!-- 预约时段 -->
      <view class="form-item">
        <view class="form-label required">预约时段</view>
        <picker :disabled="!timeSlots.length" :value="timeSlotIndex" :range="timeSlots" range-key="label" @change="onTimeSlotChange">
          <view class="picker-value">
            <text>{{ formData.appointmentTime || '请先选择日期' }}</text>
            <uni-icons type="right" size="16" color="#666"></uni-icons>
          </view>
        </picker>
        <view class="time-slots-info" v-if="timeSlots.length === 0 && formData.appointmentDate">
          该日期没有可预约时段
        </view>
      </view>
      
      <!-- 问题描述 -->
      <view class="form-item">
        <view class="form-label">问题描述</view>
        <textarea
          class="textarea"
          v-model="formData.remark"
          placeholder="请简述您的问题，有助于医生提前了解您的情况"
          maxlength="300"
        ></textarea>
        <view class="word-count">{{ formData.remark.length }}/300</view>
      </view>
      
      <!-- 预约须知 -->
      <view class="notice">
        <view class="notice-title">预约须知</view>
        <view class="notice-item">1. 请至少提前24小时预约，以便医生充分准备；</view>
        <view class="notice-item">2. 预约成功后，系统将在预约时间自动发送好友申请给医生；</view>
        <view class="notice-item">3. 请在预约时间准时上线，通过聊天功能与医生沟通；</view>
        <view class="notice-item">4. 咨询结束后，医生将结束此次就诊，并自动解除好友关系；</view>
        <view class="notice-item">5. 如需取消预约，请至少提前12小时操作，以免影响他人。</view>
      </view>
    </view>
    
    <!-- 同意条款 -->
    <view class="agreement">
      <checkbox-group @change="onAgreementChange">
        <label class="agreement-label">
          <checkbox :checked="formData.agreement" color="#1989fa" style="transform:scale(0.7);" />
          <text>我已阅读并同意</text>
          <text class="agreement-link" @click="viewAgreement">《预约服务协议》</text>
        </label>
      </checkbox-group>
    </view>
    
    <!-- 提交按钮 -->
    <view class="submit-container">
      <button class="submit-btn" :disabled="!isFormValid" @click="submitAppointment">提交预约</button>
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
import { userAppointment } from '@/api/maincode/appointment';
import { getDoctorScheduled, getDoctorScheduledList } from '@/api/maincode/doctor';
import { formatDate, addDays } from '@/utils/date';

export default {
  data() {
    return {
      doctorScheduledId: null,
      doctorId: null,
      doctorInfo: null,
      startDate: formatDate(new Date()),
      endDate: formatDate(addDays(new Date(), 30)),
      timeSlots: [],
      timeSlotIndex: 0,
      formData: {
        appointmentDate: '',
        appointmentTime: '',
        timeStart: '',
        timeEnd: '',
        doctorScheduledId: null,
        doctorId: null,
        doctorName: '',
        remark: '',
        agreement: false
      },
      popupConfig: {
        type: 'success',
        title: '提示',
        content: ''
      }
    };
  },
  
  computed: {
    isFormValid() {
      return (
        this.formData.appointmentDate &&
        this.formData.appointmentTime &&
        this.formData.doctorScheduledId &&
        this.formData.agreement
      );
    }
  },
  
  onLoad(options) {
    this.doctorScheduledId = options.id || null;
    this.doctorId = options.doctorId || null;
    
    if (this.doctorScheduledId) {
      // 从排班详情加载
      this.loadDoctorScheduled();
    } else if (this.doctorId) {
      // 从医生详情加载
      this.loadDoctorInfo();
    } else {
      uni.showToast({
        title: '参数错误',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  
  methods: {
    // 加载医生排班详情
    async loadDoctorScheduled() {
      try {
        uni.showLoading({ title: '加载中' });
        const res = await getDoctorScheduled(this.doctorScheduledId);
        
        if (res.code === 200) {
          const data = res.data;
          this.doctorInfo = {
            doctorId: data.doctorId,
            doctorName: data.doctorName,
            title: data.title,
            specialty: data.specialty,
            avatar: data.avatar
          };
          
          // 设置默认值
          this.formData.doctorScheduledId = data.id;
          this.formData.doctorId = data.doctorId;
          this.formData.doctorName = data.doctorName;
          
          // 设置预约时段
          this.timeSlots = [{
            label: data.time,
            value: data.id,
            start: data.startTime,
            end: data.endTime
          }];
          
          if (this.timeSlots.length > 0) {
            this.onTimeSlotChange({ detail: { value: 0 } });
          }
        } else {
          uni.showToast({
            title: res.msg || '获取排班信息失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取排班信息失败', error);
        uni.showToast({
          title: '获取排班信息失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 加载医生信息
    async loadDoctorInfo() {
      try {
        // 这里可以添加加载医生基本信息的API调用
        // 简化处理，假设医生ID就是医生信息
        this.doctorInfo = {
          doctorId: this.doctorId,
          doctorName: '医生',
          title: '心理医生',
          specialty: '心理健康咨询'
        };
        
        this.formData.doctorId = this.doctorId;
      } catch (error) {
        console.error('获取医生信息失败', error);
        uni.showToast({
          title: '获取医生信息失败',
          icon: 'none'
        });
      }
    },
    
    // 加载可预约时段
    async loadTimeSlots(date) {
      if (!date || !this.doctorId) return;
      
      try {
        uni.showLoading({ title: '加载中' });
        
        const params = {
          doctorId: this.doctorId,
          time: date
        };
        
        const res = await getDoctorScheduledList(params);
        
        if (res.code === 200) {
          // 过滤出还有剩余名额的时段
          this.timeSlots = (res.data || [])
            .filter(slot => slot.remainder > 0)
            .map(slot => ({
              label: slot.time,
              value: slot.id,
              start: slot.startTime,
              end: slot.endTime
            }));
          
          // 重置选择的时段
          this.formData.appointmentTime = '';
          this.formData.doctorScheduledId = null;
          this.timeSlotIndex = 0;
          
          // 如果有可用时段，默认选择第一个
          if (this.timeSlots.length > 0) {
            this.onTimeSlotChange({ detail: { value: 0 } });
          }
        } else {
          this.timeSlots = [];
          uni.showToast({
            title: res.msg || '获取可预约时段失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取可预约时段失败', error);
        this.timeSlots = [];
        uni.showToast({
          title: '获取可预约时段失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 日期选择变更
    onDateChange(e) {
      const date = e.detail.value;
      this.formData.appointmentDate = date;
      
      // 如果是从医生详情进入的，需要根据日期加载可预约时段
      if (!this.doctorScheduledId && this.doctorId) {
        this.loadTimeSlots(date);
      }
    },
    
    // 时段选择变更
    onTimeSlotChange(e) {
      const index = e.detail.value;
      if (this.timeSlots.length === 0) return;
      
      const selectedSlot = this.timeSlots[index];
      this.timeSlotIndex = index;
      this.formData.appointmentTime = selectedSlot.label;
      this.formData.doctorScheduledId = selectedSlot.value;
      this.formData.timeStart = selectedSlot.start;
      this.formData.timeEnd = selectedSlot.end;
    },
    
    // 同意协议变更
    onAgreementChange(e) {
      this.formData.agreement = e.detail.value.length > 0;
    },
    
    // 查看协议
    viewAgreement() {
      uni.navigateTo({
        url: '/pages/common/agreement?type=appointment'
      });
    },
    
    // 提交预约
    async submitAppointment() {
      if (!this.isFormValid) {
        uni.showToast({
          title: '请完成表单填写并同意协议',
          icon: 'none'
        });
        return;
      }
      
      try {
        uni.showLoading({ title: '提交中' });
        
        // 构建请求数据
        const requestData = {
          doctorScheduledId: this.formData.doctorScheduledId,
          doctorId: this.formData.doctorId,
          doctorName: this.formData.doctorName || this.doctorInfo.doctorName,
          appointmentDate: this.formData.appointmentDate,
          appointmentTime: this.formData.appointmentTime,
          remark: this.formData.remark
        };
        
        console.log('提交预约数据:', requestData);
        
        const res = await userAppointment(requestData);
        
        if (res.code === 200) {
          // 预约成功
          this.popupConfig = {
            type: 'success',
            title: '预约成功',
            content: '您已成功预约心理咨询，系统将在预约时间自动为您发送好友申请给医生，请准时上线与医生沟通。'
          };
          this.$refs.messagePopup.open();
        } else {
          // 预约失败
          uni.showToast({
            title: res.msg || res.message || '预约失败，请重试',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('提交预约失败', error);
        uni.showToast({
          title: error.message || '预约失败，请重试',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 弹窗确认回调
    onPopupConfirm() {
      this.$refs.messagePopup.close();
      uni.reLaunch({
        url: '/pages/mine/index'
      });
    },
    
    // 弹窗关闭回调
    onPopupClose() {
      if (this.popupConfig.type === 'success') {
        uni.reLaunch({
          url: '/pages/mine/index'
        });
      }
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

.doctor-info {
  display: flex;
  align-items: center;
  background-color: #ffffff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.doctor-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  margin-right: 20rpx;
}

.doctor-detail {
  flex: 1;
}

.doctor-name {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 10rpx;
}

.doctor-title {
  font-size: 24rpx;
  color: #666;
  background-color: #f0f0f0;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  margin-left: 10rpx;
}

.doctor-specialty {
  font-size: 26rpx;
  color: #666;
}

.form-container {
  background-color: #ffffff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 20rpx;
  font-weight: 500;
}

.required::after {
  content: '*';
  color: #ff4d4f;
  margin-left: 4rpx;
}

.picker-value {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 80rpx;
  padding: 0 20rpx;
  border: 1rpx solid #e5e5e5;
  border-radius: 8rpx;
  font-size: 28rpx;
  color: #333;
}

.time-slots-info {
  margin-top: 20rpx;
  font-size: 26rpx;
  color: #ff4d4f;
}

.textarea {
  width: 100%;
  height: 200rpx;
  padding: 20rpx;
  border: 1rpx solid #e5e5e5;
  border-radius: 8rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.word-count {
  text-align: right;
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.notice {
  margin-top: 40rpx;
  border-top: 1rpx dashed #e5e5e5;
  padding-top: 30rpx;
}

.notice-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 20rpx;
}

.notice-item {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 10rpx;
}

.agreement {
  padding: 30rpx;
  display: flex;
  justify-content: center;
}

.agreement-label {
  display: flex;
  align-items: center;
  font-size: 26rpx;
  color: #666;
}

.agreement-link {
  color: #1989fa;
}

.submit-container {
  padding: 0 30rpx;
}

.submit-btn {
  height: 88rpx;
  line-height: 88rpx;
  background-color: #1989fa;
  color: #ffffff;
  font-size: 32rpx;
  border-radius: 44rpx;
}

.submit-btn[disabled] {
  background-color: #cccccc;
  color: #ffffff;
}
</style> 