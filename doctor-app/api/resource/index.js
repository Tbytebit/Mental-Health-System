import request from '@/utils/request'

// 获取心理资料库分类列表
export function getResourceCategories() {
  return request({
    url: '/resource/category/list',
    method: 'get'
  })
}

// 获取心理资料库文章列表
export function getResourceList(query) {
  return request({
    url: '/resource/article/list',
    method: 'get',
    params: query
  }).then(response => {
    // 兼容处理返回数据格式
    if (response.code === 200) {
      // 如果返回的是对象而不是数组，则提取rows数组
      if (response.data && !Array.isArray(response.data) && response.data.rows) {
        response.data = response.data.rows;
      }
    }
    return response;
  });
}

// 获取推荐文章列表
export function getFeaturedResourceList(query) {
  return request({
    url: '/resource/article/featured',
    method: 'get',
    params: query
  })
}

// 获取文章详情
export function getResourceDetail(articleId) {
  return request({
    url: '/resource/article/' + articleId,
    method: 'get'
  })
}

// 获取相关文章
export function getRelatedResources(articleId, categoryId) {
  return request({
    url: '/resource/article/related',
    method: 'get',
    params: {
      articleId: articleId,
      categoryId: categoryId
    }
  })
}

// 搜索文章
export function searchResources(query) {
  return request({
    url: '/resource/article/search',
    method: 'get',
    params: query
  })
}

// 增加文章浏览量
export function increaseViewCount(articleId) {
  if (!articleId) {
    return Promise.reject(new Error('文章ID不能为空'));
  }
  return request({
    url: '/resource/article/view',
    method: 'post',
    data: {
      id: articleId
    }
  })
}

// 上传文件
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/resource/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}