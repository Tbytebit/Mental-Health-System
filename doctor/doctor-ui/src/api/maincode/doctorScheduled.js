import request from '@/utils/request'

// 查询医生预约排版列表
export function listDoctorScheduled(query) {
  return request({
    url: '/maincode/doctorScheduled/list',
    method: 'get',
    params: query
  })
}

// 查询医生预约排版详细
export function getDoctorScheduled(id) {
  return request({
    url: '/maincode/doctorScheduled/' + id,
    method: 'get'
  })
}

// 新增医生预约排版
export function addDoctorScheduled(data) {
  return request({
    url: '/maincode/doctorScheduled',
    method: 'post',
    data: data
  })
}

// 修改医生预约排版
export function updateDoctorScheduled(data) {
  return request({
    url: '/maincode/doctorScheduled',
    method: 'put',
    data: data
  })
}

// 删除医生预约排版
export function delDoctorScheduled(id) {
  return request({
    url: '/maincode/doctorScheduled/' + id,
    method: 'delete'
  })
}
