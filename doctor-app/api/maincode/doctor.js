import request from '@/utils/request'

 
// 获取医生个人排班列表
export function getDoctorScheduledList(param) {
  return request({
    'url': '/maincode/doctorScheduled/getDoctorScheduledList',
    'method': 'get',
	'params' : param
  })
} 
// 获取排班信息
export function getDoctorScheduled(id) {
  return request({
    'url': '/maincode/doctorScheduled/getDoctorScheduled/' + id,
    'method': 'get'
  })
} 

