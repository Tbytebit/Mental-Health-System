import request from '@/utils/request'

 
// 获取新闻
export function getNewsArticlesList(param) {
  return request({
    'url': '/maincode/articles/getNewsArticlesList',
    'method': 'get',
	'params' : param
  })
} 


