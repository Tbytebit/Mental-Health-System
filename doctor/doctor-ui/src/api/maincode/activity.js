import request from '@/utils/request'

// 查询活动管理列表
export function listActivity(query) {
  return request({
    url: '/maincode/activity/list',
    method: 'get',
    params: query
  })
}

// 查询活动管理详细
export function getActivity(id) {
  return request({
    url: '/maincode/activity/' + id,
    method: 'get'
  })
}

// 新增活动管理
export function addActivity(data) {
  return request({
    url: '/maincode/activity',
    method: 'post',
    data: data
  })
}

// 修改活动管理
export function updateActivity(data) {
  return request({
    url: '/maincode/activity',
    method: 'put',
    data: data
  })
}

// 删除活动管理
export function delActivity(id) {
  return request({
    url: '/maincode/activity/' + id,
    method: 'delete'
  })
}
