import request from '@/utils/request'

// 查询活动预约列表
export function listReservation(query) {
  return request({
    url: '/maincode/reservation/list',
    method: 'get',
    params: query
  })
}

// 查询活动预约详细
export function getReservation(id) {
  return request({
    url: '/maincode/reservation/' + id,
    method: 'get'
  })
}

// 新增活动预约
export function addReservation(data) {
  return request({
    url: '/maincode/reservation',
    method: 'post',
    data: data
  })
}

// 修改活动预约
export function updateReservation(data) {
  return request({
    url: '/maincode/reservation',
    method: 'put',
    data: data
  })
}

// 删除活动预约
export function delReservation(id) {
  return request({
    url: '/maincode/reservation/' + id,
    method: 'delete'
  })
}
