import request from '@/utils/request'

// 保存问题信息
export function saveOption(data) {
    return request({
      url: '/survey/option/save',
      method: 'post',
      data: data
    })
  }

export function listOption(questionId){
  return request({
    url:'/survey/option/list/' + questionId,
    method:'get'
  })
}

export function delOption(option){
  return request({
    url:'/survey/option/del/'+option.id,
    method:'delete'
  })
}