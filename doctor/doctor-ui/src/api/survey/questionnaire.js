import request from '@/utils/request'

// 查询问卷中心列表
export function listQuestionnaire(query) {
  return request({
    url: '/survey/questionnaire/list/',
    method: 'get',
    params: query
  })
}

// 查询问卷中心详细
export function getQuestionnaire(questionnaireId) {
  return request({
    url: '/survey/questionnaire/' + questionnaireId,
    method: 'get'
  })
}

// 新增问卷中心
export function addQuestionnaire(data) {
  return request({
    url: '/survey/questionnaire',
    method: 'post',
    data: data
  })
}

// 修改问卷中心
export function updateQuestionnaire(data) {
  return request({
    url: '/survey/questionnaire',
    method: 'put',
    data: data
  })
}

// 删除问卷中心
export function delQuestionnaire(questionnaireId) {
  return request({
    url: '/survey/questionnaire/' + questionnaireId,
    method: 'delete'
  })
}

export function publishQuestionnaire(questionnaireId){
  return request({
    url:'/survey/questionnaire/publish/' + questionnaireId,
    method:'put'
  })
}

export function answerQuestionnaire(respondents){
  return request({
    url:'/survey/respondents/add',
    method:'post',
    data:respondents
  })
}

export function updateStatus(data){
  return request({
    url:'/survey/questionnaire/status',
    method:'put',
    params:data
  })
}

export function getReport(questionnaireId){
  return request({
    url:'/survey/questionnaire/stats/' + questionnaireId,
    method:'get'
  })
}

// 查询问卷已分配用户列表
export function asUserList(query) {
  return request({
    url: '/survey/questionnaire/asUser/asList/'+ query.questionnaireId,
    method: 'get',
    params: query
  })
}

// 查询问卷未分配用户列表
export function unasUserList(query) {
  return request({
    url: '/survey/questionnaire/asUser/unasList/'+query.questionnaireId,
    method: 'get',
    params: query
  })
}

// 取消用户分配问卷
export function asUserCancel(data) {
  return request({
    url: '/survey/questionnaire/asUser/cancel',
    method: 'put',
    data: data
  })
}

// 批量取消用户分配问卷
export function asUserCancelAll(data) {
  return request({
    url: '/survey/questionnaire/asUser/cancelAll',
    method: 'put',
    params: data
  })
}

// 授权用户选择
export function asUserSelectAll(data) {
  return request({
    url: '/survey/questionnaire/asUser/selectAll',
    method: 'put',
    params: data
  })
}

