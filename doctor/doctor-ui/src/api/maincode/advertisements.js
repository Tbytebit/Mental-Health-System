import request from '@/utils/request'

// 查询广告列表
export function listAdvertisements(query) {
  return request({
    url: '/maincode/advertisements/list',
    method: 'get',
    params: query
  })
}

// 查询广告详细
export function getAdvertisements(adId) {
  return request({
    url: '/maincode/advertisements/' + adId,
    method: 'get'
  })
}

// 新增广告
export function addAdvertisements(data) {
  return request({
    url: '/maincode/advertisements',
    method: 'post',
    data: data
  })
}

// 修改广告
export function updateAdvertisements(data) {
  return request({
    url: '/maincode/advertisements',
    method: 'put',
    data: data
  })
}

// 删除广告
export function delAdvertisements(adId) {
  return request({
    url: '/maincode/advertisements/' + adId,
    method: 'delete'
  })
}
