import request from '@/utils/request'

 
// 获取广告轮播
export function getNoticeList() {
  return request({
    'url': '/system/notice/getNoticeList',
    'method': 'get'
  })
} 

