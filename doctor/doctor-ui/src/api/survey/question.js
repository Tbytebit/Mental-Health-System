import request from '@/utils/request'

// 保存问题信息
export function saveQuestion(data) {
  return request({
    url: '/survey/question/save',
    method: 'post',
    data: data
  })
}


//获取问题信息
export function listQuestion(questionnaireId){
  return request({
    url: '/survey/question/list/' + questionnaireId,
    method: 'get',
  })
}

export function delQuestion(questionId){
  return request({
    url: '/survey/question/del/'+questionId,
    method:'delete',
  })
}

export function answerQuestion(answer) {
  return request({
    url: '/survey/answer/add',
    method: 'post',
    data: answer
  })
}