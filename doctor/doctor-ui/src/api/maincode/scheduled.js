import request from '@/utils/request'

// 查询预约排版列表
export function listScheduled(query) {
  return request({
    url: '/maincode/scheduled/list',
    method: 'get',
    params: query
  })
}

// 查询预约排版详细
export function getScheduled(scheduledId) {
  return request({
    url: '/maincode/scheduled/' + scheduledId,
    method: 'get'
  })
}

// 新增预约排版
export function addScheduled(data) {
  return request({
    url: '/maincode/scheduled',
    method: 'post',
    data: data
  })
}

// 修改预约排版
export function updateScheduled(data) {
  return request({
    url: '/maincode/scheduled',
    method: 'put',
    data: data
  })
}

// 删除预约排版
export function delScheduled(scheduledId) {
  return request({
    url: '/maincode/scheduled/' + scheduledId,
    method: 'delete'
  })
}
