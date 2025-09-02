import request from '@/utils/request'

 
// 获取反馈列表
export function getFeedbackList(param) {
  return request({
    'url': '/maincode/feedback/getFeedbackList',
    'method': 'get',
	'params' : param
  })
} 

// 提交
export function addFeedback(data) {
  return request({
    'url': '/maincode/feedback/addFeedback',
    'method': 'post',
	'data' : data,
	})
}