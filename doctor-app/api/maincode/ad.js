import request from '@/utils/request'

 
// 获取广告轮播
export function getAdCarousel() {
  return request({
    'url': '/maincode/advertisements/getAdCarousel',
    'method': 'get'
  })
} 

