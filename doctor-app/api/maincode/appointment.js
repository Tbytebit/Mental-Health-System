import request from '@/utils/request'

 
// 提交预约
export function userAppointment(data) {
  return request({
    'url': '/maincode/appointment/userAppointment',
    'method': 'post',
    'data' : data,
    'errorHandler': (error) => {
      console.error('创建预约失败', error);
      let errorMsg = '创建预约失败，请稍后再试';
      if (error.response && error.response.data) {
        errorMsg = error.response.data.msg || error.response.data.message || errorMsg;
      } else if (error.message) {
        errorMsg = error.message;
      }
      
      return {
        code: 500,
        message: errorMsg,
        success: false
      };
    }
  });
}

// 用户查询自己的预约记录
export function getUserAppointment(param) {
  return request({
    'url': '/maincode/appointment/getUserAppointment',
    'method': 'get',
    'params' : param
  });
} 

// 用户查询自己评论预约记录
export function getUserCommentAppointment(param) {
  return request({
    'url': '/maincode/appointment/getUserCommentAppointment',
    'method': 'get',
    'params' : param
  });
} 

// 获取预约详情
export function getAppointment(id) {
  return request({
    'url': '/maincode/appointment/getAppointment/' + id,
    'method': 'get'
  });
} 

// 评价
export function userComment(data) {
  return request({
    'url': '/maincode/appointment/userComment',
    'method': 'post',
    'data' : data,
  });
}

// 取消预约
export function cancelAppointment(appointmentId) {
  return request({
    'url': '/maincode/appointment/cancel/' + appointmentId,
    'method': 'post'
  });
}

