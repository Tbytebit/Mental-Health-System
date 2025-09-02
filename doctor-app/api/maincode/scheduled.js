import request from '@/utils/request'

 
// 获取医生排班
export function getScheduledList(param) {
  return request({
    'url': '/maincode/scheduled/getScheduledList',
    'method': 'get',
	'params' : param
  })
} 
// 获取治疗屋排班
export function getRoomScheduledList(param) {
  return request({
    'url': '/maincode/scheduled/getRoomScheduledList',
    'method': 'get',
	'params' : param
  })
} 

