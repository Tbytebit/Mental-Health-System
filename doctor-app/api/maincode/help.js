import request from '@/utils/request'

 
// 获取广告轮播
export function getHelpList() {
  return request({
    'url': '/maincode/help/getHelpList',
    'method': 'get'
  })
} 

