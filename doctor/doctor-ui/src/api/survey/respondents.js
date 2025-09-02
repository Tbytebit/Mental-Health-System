import request from '@/utils/request'

// 查询答卷中心列表
export function listRespondents(query) {
  return request({
    url: '/survey/respondents/list',
    method: 'get',
    params: query
  })
}

// 查询答卷中心详细
export function getRespondents(questionnaireId) {
  return request({
    url: '/survey/respondents/' + questionnaireId,
    method: 'get'
  })
}

// 新增答卷中心
export function addRespondents(data) {
  return request({
    url: '/survey/respondents',
    method: 'post',
    data: data
  })
}

// 修改答卷中心
export function updateRespondents(data) {
  return request({
    url: '/survey/respondents',
    method: 'put',
    data: data
  })
}

// 删除答卷中心
export function delRespondents(questionnaireId) {
  return request({
    url: '/survey/respondents/del/' + questionnaireId,
    method: 'delete'
  })
}

export function getSurvey(respondentsId){
  return request({
    url:'/survey/respondents/survey/' + respondentsId,
    method:'get'
  })
}




