import request from '@/utils/request'

// 查询新闻管理列表
export function listArticles(query) {
  return request({
    url: '/maincode/articles/list',
    method: 'get',
    params: query
  })
}

// 查询新闻管理详细
export function getArticles(articleId) {
  return request({
    url: '/maincode/articles/' + articleId,
    method: 'get'
  })
}

// 新增新闻管理
export function addArticles(data) {
  return request({
    url: '/maincode/articles',
    method: 'post',
    data: data
  })
}

// 修改新闻管理
export function updateArticles(data) {
  return request({
    url: '/maincode/articles',
    method: 'put',
    data: data
  })
}

// 删除新闻管理
export function delArticles(articleId) {
  return request({
    url: '/maincode/articles/' + articleId,
    method: 'delete'
  })
}
