import request from '@/utils/request'

 
// 获取详情
export function getArticleInfo(id) {
  return request({
    'url': '/maincode/articles/getArticleInfo/' + id,
    'method': 'get'
  })
} 

