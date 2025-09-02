import request from '@/utils/request'

 
// 获取用户预约列表
export function getReservationList(param) {
  return request({
    'url': '/maincode/reservation/getReservationList',
    'method': 'get',
	'params' : param
  })
} 

// 立即预约
export function addActivityReservation(data) {
  return request({
    'url': '/maincode/reservation/addActivityReservation',
    'method': 'post',
    'data' : data
  })
} 

 
// 取消预约
export function deleteActivityReservation(id) {
  return request({
    'url': '/maincode/reservation/deleteActivityReservation/' + id,
    'method': 'get'
  })
} 