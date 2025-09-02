import request from '@/utils/request'

// 模拟数据
const mockDiaries = [
  {
    id: '1',
    name: '今天心情很好',
    details: '今天是个阳光明媚的日子，我感觉心情特别舒畅。早上散步时遇到了几位老朋友，我们一起聊天，分享彼此的近况。下午我开始读一本新书，内容很有启发性。',
    mood: 'happy',
    createTime: '2023-06-10T10:30:00Z'
  },
  {
    id: '2',
    name: '工作压力有点大',
    details: '最近工作任务比较重，感觉有些焦虑。需要调整一下自己的状态，也许可以尝试一些放松的方法，比如冥想或者瑜伽。希望明天能够好转。',
    mood: 'anxious',
    createTime: '2023-06-08T15:45:00Z'
  },
  {
    id: '3',
    name: '和家人的美好时光',
    details: '周末和家人一起去了郊外野餐，天气很好，孩子们玩得很开心。这样的家庭时光真的很珍贵，让我忘记了工作中的烦恼。',
    mood: 'happy',
    createTime: '2023-06-05T18:20:00Z'
  }
];

// 获取社区活动列表
export function getActivityList(query) {
  // 使用模拟数据
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 0,
        message: 'success',
        data: mockDiaries
      });
    }, 300);
  });
  
  // 实际API调用（暂时注释掉）
  // return request({
  //   url: '/activity/list',
  //   method: 'get',
  //   params: query
  // })
}

// 获取社区活动详情
export function getActivity(id) {
  // 使用模拟数据
  return new Promise((resolve) => {
    setTimeout(() => {
      const diary = mockDiaries.find(item => item.id === id) || mockDiaries[0];
      resolve({
        code: 0,
        message: 'success',
        data: diary
      });
    }, 300);
  });
  
  // 实际API调用（暂时注释掉）
  // return request({
  //   url: '/activity/' + id,
  //   method: 'get'
  // })
}

// 保存社区活动
export function saveActivity(data) {
  // 使用模拟数据
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 0,
        message: 'success'
      });
    }, 300);
  });
  
  // 实际API调用（暂时注释掉）
  // return request({
  //   url: '/activity',
  //   method: 'post',
  //   data: data
  // })
}

// 删除社区活动
export function deleteActivity(id) {
  // 使用模拟数据
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 0,
        message: 'success'
      });
    }, 300);
  });
  
  // 实际API调用（暂时注释掉）
  // return request({
  //   url: '/activity/' + id,
  //   method: 'delete'
  // })
}
