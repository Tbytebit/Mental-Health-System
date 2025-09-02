import request from '@/utils/request'

// 查询帮助内容列表
export function listHelp(query) {
  return request({
    url: '/maincode/help/list',
    method: 'get',
    params: query
  })
}

// 查询帮助内容详细
export function getHelp(helpId) {
  return request({
    url: '/maincode/help/' + helpId,
    method: 'get'
  })
}

// 新增帮助内容
export function addHelp(data) {
  return request({
    url: '/maincode/help',
    method: 'post',
    data: data
  })
}

// 修改帮助内容
export function updateHelp(data) {
  return request({
    url: '/maincode/help',
    method: 'put',
    data: data
  })
}

// 删除帮助内容
export function delHelp(helpId) {
  return request({
    url: '/maincode/help/' + helpId,
    method: 'delete'
  })
}
