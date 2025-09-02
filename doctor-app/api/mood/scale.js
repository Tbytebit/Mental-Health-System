import request from '@/utils/request'

// 获取所有心理情绪量表
export function listAllScales() {
  return request({
    url: '/mood/scale/listAll',
    method: 'get'
  })
}

// 获取量表详情
export function getScaleDetail(scaleId) {
  return request({
    url: `/mood/scale/info/${scaleId}`,
    method: 'get',
    headers: {
      skipPermission: true  // 跳过权限验证
    }
  })
}

// 获取量表问题列表
export function getScaleQuestions(scaleId) {
  return request({
    url: `/mood/question/listByScale/${scaleId}`,
    method: 'get',
    headers: {
      skipPermission: true  // 跳过权限验证
    }
  })
}

// 提交量表填写记录
export function submitScaleRecord(data) {
  return request({
    url: '/mood/record',
    method: 'post',
    data: data,
    headers: {
      skipToken: false,  // 不跳过token验证
      skipPermission: true  // 跳过权限验证
    }
  })
}

// 获取用户的量表填写记录
export function getUserScaleRecords() {
  return request({
    url: '/mood/record/my',
    method: 'get',
    headers: {
      skipPermission: true  // 跳过权限验证
    }
  })
}

// 获取用户指定量表的填写记录
export function getUserScaleRecordsByScale(scaleId) {
  return request({
    url: `/mood/record/my/${scaleId}`,
    method: 'get',
    headers: {
      skipPermission: true  // 跳过权限验证
    }
  })
}

// 获取记录详情
export function getRecordDetail(recordId) {
  return request({
    url: `/mood/record/${recordId}`,
    method: 'get',
    headers: {
      skipPermission: true  // 跳过权限验证
    }
  })
} 