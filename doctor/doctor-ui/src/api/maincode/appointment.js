import request from '@/utils/request'

// 查询预约记录列表
export function listAppointment(query) {
  return request({
    url: '/maincode/appointment/list',
    method: 'get',
    params: query
  })
}

// 查询预约记录详细
export function getAppointment(appointmentId) {
  return request({
    url: '/maincode/appointment/' + appointmentId,
    method: 'get'
  })
}

// 新增预约记录
export function addAppointment(data) {
  return request({
    url: '/maincode/appointment',
    method: 'post',
    data: data
  })
}

// 修改预约记录
export function updateAppointment(data) {
  return request({
    url: '/maincode/appointment',
    method: 'put',
    data: data
  })
}


// 删除预约记录
export function delAppointment(appointmentId) {
  return request({
    url: '/maincode/appointment/' + appointmentId,
    method: 'delete'
  })
}

// 就诊
export function outpatients(data) {
  return request({
    url: '/maincode/appointment/outpatients',
    method: 'put',
    data: data
  })
}
