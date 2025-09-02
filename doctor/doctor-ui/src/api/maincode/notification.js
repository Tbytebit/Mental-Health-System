import request from '@/utils/request'

// 查询消息通知列表
export function listNotification(query) {
  return request({
    url: '/maincode/notification/list',
    method: 'get',
    params: query
  })
}

// 查询消息通知详细
export function getNotification(messageId) {
  return request({
    url: '/maincode/notification/' + messageId,
    method: 'get'
  })
}

// 新增消息通知
export function addNotification(data) {
  return request({
    url: '/maincode/notification',
    method: 'post',
    data: data
  })
}

// 修改消息通知
export function updateNotification(data) {
  return request({
    url: '/maincode/notification',
    method: 'put',
    data: data
  })
}

// 删除消息通知
export function delNotification(messageId) {
  return request({
    url: '/maincode/notification/' + messageId,
    method: 'delete'
  })
}
