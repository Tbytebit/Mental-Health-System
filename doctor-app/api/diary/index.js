import request from '@/utils/request'
import config from '@/config'

// 获取日记列表
export function getDiaryList(params) {
  console.log('调用获取日记列表API, 参数:', params);
  return request({
    url: '/diary/getUserDiaries',
    method: 'get',
    params: {
      pageNum: params.pageNum || params.page || 1,
      pageSize: params.pageSize || 10,
      mood: params.mood,
      keyword: params.keyword
    }
  })
}

// 获取日记详情
export function getDiary(id) {
  console.log('调用获取日记详情API, ID:', id);
  return request({
    url: `/diary/${id}`,
    method: 'get'
  }).then(res => {
    // 确保图片字段正确处理
    if (res.data && res.data.images) {
      try {
        console.log('原始图片数据:', res.data.images);
        
        // 尝试解析JSON字符串，确保前端接收到的是正确格式
        if (typeof res.data.images === 'string' && res.data.images.trim() !== '') {
          // 尝试解析JSON
          try {
            res.data.images = JSON.parse(res.data.images);
            
            // 确保解析后的是数组
            if (!Array.isArray(res.data.images)) {
              res.data.images = [res.data.images];
            }
          } catch (e) {
            console.error('解析JSON失败，尝试作为单个URL处理:', e);
            // 如果解析失败，可能是单个URL，转为数组
            res.data.images = [res.data.images];
          }
        } else if (!res.data.images) {
          // 确保空值是数组
          res.data.images = [];
        } else if (!Array.isArray(res.data.images)) {
          // 如果不是数组但又不是字符串，转为数组
          res.data.images = [res.data.images];
        }
        
        // 过滤无效的图片项
        res.data.images = res.data.images.filter(img => img && img !== 'null' && img !== 'undefined');
        
        // 检查并过滤blob URL（这些URL无法跨会话访问）
        const blobUrls = res.data.images.filter(img => img && typeof img === 'string' && img.startsWith('blob:'));
        if (blobUrls.length > 0) {
          console.warn('检测到blob URL图片，这些图片无法正常显示:', blobUrls);
          
          // 从图片数组中移除blob URL
          res.data.images = res.data.images.filter(img => !img.startsWith('blob:'));
          
          // 如果移除后没有图片了，确保是空数组
          if (res.data.images.length === 0) {
            res.data.images = [];
          }
        }
        
        // 处理图片URL，确保完整路径
        res.data.images = res.data.images.map(img => {
          if (!img) return '';
          
          // 去除可能的多余引号和空格
          const cleanedImg = img.replace(/^["']|["']$/g, '').trim();
          
          // 如果是OSS图片链接，直接返回完整URL
          if (cleanedImg.includes('oss-cn-beijing.aliyuncs.com')) {
            console.log('处理OSS图片URL:', cleanedImg);
            return cleanedImg;
          }
          
          const baseUrl = config.baseUrl;
          
          // 如果已经是完整URL或base64，直接使用
          if (cleanedImg.startsWith('http') || cleanedImg.startsWith('data:')) {
            return cleanedImg;
          }
          
          // 处理带有/profile前缀的图片路径
          if (cleanedImg.startsWith('/profile/') || cleanedImg === '/profile') {
            return baseUrl + cleanedImg;
          }
          
          // 检查是否包含profile但不是以/profile开头
          if (cleanedImg.includes('profile/') && !cleanedImg.startsWith('/profile/')) {
            return baseUrl + '/profile/' + cleanedImg.substring(cleanedImg.indexOf('profile/') + 8);
          }
          
          // 如果是其他路径，确保添加/profile前缀
          if (cleanedImg.startsWith('/')) {
            return baseUrl + '/profile' + cleanedImg;
          }
          
          // 其他情况，确保路径正确
          return baseUrl + '/profile/' + cleanedImg;
        });
        
        console.log('处理后的图片数据:', res.data.images);
      } catch (error) {
        console.error('处理图片数据出错:', error);
        res.data.images = [];
      }
    } else if (res.data) {
      // 如果没有图片字段，设为空数组
      res.data.images = [];
    }
    return res;
  });
}

// 新增日记
export function addDiary(data) {
  console.log('调用新增日记API, 数据:', data);
  // 处理图片数据
  const processedData = {...data};
  
  // 确保images是字符串格式
  if (processedData.images) {
    if (typeof processedData.images !== 'string') {
      processedData.images = JSON.stringify(processedData.images);
    }
  } else {
    processedData.images = '[]';
  }
  
  return request({
    url: '/diary/addUserDiary',
    method: 'post',
    data: {
      diaryName: processedData.title || processedData.name || processedData.diaryName,
      diaryContent: processedData.content || processedData.details || processedData.diaryContent,
      mood: processedData.mood,
      images: processedData.images
    }
  });
}

// 新增带图片的日记（使用uni.uploadFile方式提交，支持直接上传图片）
export function addDiaryWithImages(data, files) {
  console.log('调用新增带图片的日记API, 数据:', data, '图片:', files ? files.length : 0);
  
  // 准备formData对象（普通对象，不是FormData实例）
  const formData = {
    diaryName: data.title || data.name || data.diaryName,
    diaryContent: data.content || data.details || data.diaryContent,
    mood: data.mood
  };
  
  // 如果没有文件，使用普通请求
  if (!files || files.length === 0) {
    console.log('没有文件，使用普通addDiary API');
    return addDiary(data);
  }
  
  // 有文件，使用upload方法
  return new Promise((resolve, reject) => {
    // 获取token
    let token = uni.getStorageSync('token');
    console.log('上传文件Token:', token ? '已获取' : '未获取');
    
    // 如果没有token，尝试从localStorage获取
    if (!token) {
      try {
        token = localStorage.getItem('token') || '';
        if (token) {
          uni.setStorageSync('token', token);
          console.log('从localStorage获取并设置token成功');
        }
      } catch (e) {
        console.error('获取localStorage token失败:', e);
      }
    }
    
    // 过滤有效文件
    const validFiles = Array.isArray(files) 
      ? files.filter(f => f && typeof f === 'string' && f.length > 0) 
      : (files ? [files] : []);
    
    console.log('有效文件数量:', validFiles.length);
    
    if (validFiles.length === 0) {
      console.log('没有有效文件，使用普通API');
      return addDiary(data).then(resolve).catch(reject);
    }
    
    // 检查Token是否有效
    if (!token) {
      console.error('上传失败: 缺少有效的认证Token');
      return reject({ code: 401, msg: '认证信息丢失，请重新登录' });
    }
    
    // 显示上传中状态
    uni.showLoading({ title: '正在上传中...' });
    
    // 单个文件上传
    if (validFiles.length === 1) {
      console.log('单文件上传模式');
      
      // 检查文件是否存在
      try {
        const fs = uni.getFileSystemManager && uni.getFileSystemManager();
        if (fs) {
          try {
            const fileInfo = fs.statSync(validFiles[0]);
            console.log(`文件存在，大小: ${fileInfo.size}字节`);
          } catch (e) {
            console.warn(`无法获取文件信息: ${e.message}`);
          }
        }
      } catch (e) {
        console.error(`文件系统检查出错: ${e.message}`);
      }
      
      uni.uploadFile({
        url: config.baseUrl + '/diary/addUserDiaryWithImages',
        filePath: validFiles[0],
        name: 'file',
        header: {
          'Authorization': 'Bearer ' + token,
          // 不要设置Content-Type，uploadFile会自动设置正确的multipart/form-data
        },
        formData: formData,
        success: (uploadRes) => {
          uni.hideLoading();
          try {
            console.log('原始上传响应:', uploadRes);
            const result = typeof uploadRes.data === 'string' ? JSON.parse(uploadRes.data) : uploadRes.data;
            console.log('文件上传成功:', result);
            
            if (result.code === 401) {
              console.error('认证失败:', result);
              reject({ code: 401, msg: '认证失败，请重新登录' });
              return;
            }
            
            if (result.code === 200 || result.code === 0) {
              uni.showToast({ title: '上传成功', icon: 'success' });
              resolve(result);
            } else {
              console.error('文件上传返回错误:', result);
              uni.showToast({ title: result.msg || '上传失败', icon: 'none' });
              reject(result);
            }
          } catch (e) {
            console.error('解析上传响应失败:', e, '原始响应:', uploadRes.data);
            uni.showToast({ title: '处理响应失败', icon: 'none' });
            reject(e);
          }
        },
        fail: (err) => {
          uni.hideLoading();
          console.error('文件上传失败:', err);
          uni.showToast({ title: '上传失败：' + (err.errMsg || '网络错误'), icon: 'none' });
          reject(err);
        }
      });
    } else {
      // 多文件上传，由于uni-app的限制，需要单独上传每个文件
      console.log('多文件上传模式，文件数量:', validFiles.length);
      const tempImages = [];
      let uploadingIndex = 0;
      
      const uploadSingleFile = (index) => {
        if (index >= validFiles.length) {
          // 所有文件上传完成，组合数据提交
          console.log('所有文件上传完成，更新images字段:', tempImages);
          uni.hideLoading();
          
          if (tempImages.length === 0) {
            console.error('所有图片上传失败');
            uni.showToast({ title: '图片上传失败', icon: 'none' });
            reject({ msg: '所有图片上传失败' });
            return;
          }
          
          const finalData = {...data};
          finalData.images = tempImages;
          
          // 转换为字符串格式
          if (Array.isArray(finalData.images)) {
            finalData.images = JSON.stringify(finalData.images);
          }
          
          addDiary(finalData).then(resolve).catch(reject);
          return;
        }
        
        uploadingIndex = index;
        uni.showLoading({ title: `上传图片 ${index+1}/${validFiles.length}...` });
        console.log(`上传第${index+1}/${validFiles.length}个文件`);
        
        // 检查文件是否存在
        try {
          const fs = uni.getFileSystemManager && uni.getFileSystemManager();
          if (fs) {
            try {
              const fileInfo = fs.statSync(validFiles[index]);
              console.log(`文件${index+1}存在，大小: ${fileInfo.size}字节`);
            } catch (e) {
              console.warn(`无法获取文件${index+1}信息: ${e.message}`);
            }
          }
        } catch (e) {
          console.error(`文件${index+1}系统检查出错: ${e.message}`);
        }
        
        uni.uploadFile({
          url: config.baseUrl + '/diary/uploadImage',
          filePath: validFiles[index],
          name: 'file',
          header: {
            'Authorization': 'Bearer ' + token
          },
          formData: { type: 'diary' },
          success: (res) => {
            try {
              const result = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
              console.log(`第${index+1}个文件上传响应:`, result);
              
              if (result.code === 401) {
                console.error('认证失败:', result);
                uni.hideLoading();
                reject({ code: 401, msg: '认证失败，请重新登录' });
                return;
              }
              
              if (result.code === 0 || result.code === 200) {
                // 提取图片URL
                let imageUrl = '';
                if (result.url) {
                  imageUrl = result.url;
                } else if (result.data && result.data.url) {
                  imageUrl = result.data.url;
                } else if (result.fileName) {
                  imageUrl = result.fileName;
                } else if (result.data && result.data.fileName) {
                  imageUrl = result.data.fileName;
                }
                
                if (imageUrl) {
                  tempImages.push(imageUrl);
                  console.log(`已上传${tempImages.length}/${validFiles.length}个文件`);
                  
                  // 继续上传下一个文件
                  uploadSingleFile(index + 1);
                } else {
                  console.error('无法获取上传图片URL:', result);
                  
                  // 继续尝试上传其他图片
                  uploadSingleFile(index + 1);
                }
              } else {
                console.error(`第${index+1}个文件上传失败:`, result);
                
                // 继续尝试上传其他图片
                uploadSingleFile(index + 1);
              }
            } catch (e) {
              console.error(`解析第${index+1}个文件响应失败:`, e, '原始响应:', res.data);
              
              // 继续尝试上传其他图片
              uploadSingleFile(index + 1);
            }
          },
          fail: (err) => {
            console.error(`第${index+1}个文件上传请求失败:`, err);
            
            // 继续尝试上传其他图片
            uploadSingleFile(index + 1);
          }
        });
      };
      
      // 开始上传第一个文件
      uploadSingleFile(0);
    }
  });
}

// 修改日记
export function updateDiary(id, data) {
  console.log('调用修改日记API, ID:', id, '数据:', data);
  
  // 处理图片数据
  const processedData = {...data};
  
  // 确保images是字符串格式
  if (processedData.images) {
    if (typeof processedData.images !== 'string') {
      processedData.images = JSON.stringify(processedData.images);
    }
  } else {
    processedData.images = '[]';
  }
  
  return request({
    url: `/diary/${id}`,
    method: 'put',
    data: {
      diaryName: processedData.title || processedData.name || processedData.diaryName,
      diaryContent: processedData.content || processedData.details || processedData.diaryContent,
      mood: processedData.mood,
      images: processedData.images
    }
  });
}

// 修改带图片的日记
export function updateDiaryWithImages(id, data, files, keepOldImages = false) {
  console.log('调用修改带图片的日记API, ID:', id, '数据:', data, '图片:', files, '保留原图:', keepOldImages);
  
  // 如果没有文件，并且不需要保留旧图片，使用普通请求
  if ((!files || files.length === 0) && !keepOldImages) {
    return updateDiary(id, data);
  }
  
  // 准备formData
  const formData = {
    diaryName: data.title || data.name || data.diaryName,
    diaryContent: data.content || data.details || data.diaryContent,
    mood: data.mood,
    keepOldImages: keepOldImages
  };
  
  // 如果需要保留旧图片，添加旧图片数据
  if (keepOldImages && data.images) {
    // 确保旧图片是字符串格式
    formData.oldImages = typeof data.images === 'string' ? data.images : JSON.stringify(data.images);
  }
  
  // 使用upload方法
  return new Promise((resolve, reject) => {
    // 获取token
    let token = uni.getStorageSync('token');
    console.log('上传文件Token:', token ? '已获取' : '未获取');
    
    // 如果没有token，尝试从localStorage获取
    if (!token) {
      try {
        token = localStorage.getItem('token') || '';
        if (token) {
          uni.setStorageSync('token', token);
          console.log('从localStorage获取并设置token成功');
        }
      } catch (e) {
        console.error('获取localStorage token失败:', e);
      }
    }
    
    // 检查Token是否有效
    if (!token) {
      console.error('上传失败: 缺少有效的认证Token');
      // 不使用提示，直接返回错误让调用方处理
      return reject({ code: 401, msg: '认证信息丢失，请重新登录' });
    }
    
    // 如果没有新文件，但需要保留旧图片
    if (!files || files.length === 0) {
      uni.request({
        url: config.baseUrl + '/diary/updateWithImages/' + id,
        method: 'POST',
        header: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Authorization': 'Bearer ' + token
        },
        data: formData,
        success: (res) => {
          if (res.data.code === 401) {
            reject({ code: 401, msg: '认证失败，请重新登录' });
            return;
          }
          resolve(res.data);
        },
        fail: (err) => {
          reject(err);
        }
      });
      return;
    }
    
    // 有新文件，使用uploadFile
    uni.uploadFile({
      url: config.baseUrl + '/diary/updateWithImages/' + id,
      files: files.map(file => ({
        name: 'file',
        uri: file
      })),
      filePath: files.length === 1 ? files[0] : undefined,
      name: 'file',
      header: {
        'Authorization': 'Bearer ' + token
      },
      formData: formData,
      success: (uploadRes) => {
        try {
          const result = typeof uploadRes.data === 'string' ? JSON.parse(uploadRes.data) : uploadRes.data;
          console.log('修改日记上传成功:', result);
          
          if (result.code === 401) {
            console.error('认证失败:', result);
            reject({ code: 401, msg: '认证失败，请重新登录' });
            return;
          }
          
          resolve(result);
        } catch (e) {
          console.error('解析上传响应失败:', e);
          reject(e);
        }
      },
      fail: (err) => {
        console.error('修改日记上传失败:', err);
        reject(err);
      }
    });
  });
}

// 删除日记
export function deleteDiary(id) {
  console.log('调用删除日记API, ID:', id);
  return request({
    url: `/diary/${id}`,
    method: 'delete'
  });
}

// 以下是模拟API，用于开发测试

// 模拟日记数据
const mockDiaries = [
  {
    id: 1,
    userId: 1,
    name: '今天心情很好',
    details: '今天天气很好，和朋友一起出去玩，心情愉悦。',
    mood: 'happy',
    createTime: '2023-05-01 12:00:00',
    images: ['/static/images/diary/sample1.jpg', '/static/images/diary/sample2.jpg']
  },
  {
    id: 2,
    userId: 1,
    name: '工作有点压力',
    details: '项目进度有点紧张，需要加班完成。感觉有点累，但还能应付。',
    mood: 'neutral',
    createTime: '2023-05-02 18:30:00',
    images: []
  },
  {
    id: 3,
    userId: 1,
    name: '考试失利',
    details: '今天考试没考好，心情低落。需要调整状态，好好准备下次考试。',
    mood: 'sad',
    createTime: '2023-05-03 20:15:00',
    images: ['/static/images/diary/sample3.jpg']
  },
  {
    id: 4,
    userId: 1,
    name: '与同事争执',
    details: '今天和同事有些争执，对方不理解我的想法，感到很生气。',
    mood: 'angry',
    createTime: '2023-05-04 10:45:00',
    images: []
  },
  {
    id: 5,
    userId: 1,
    name: '担心明天的演讲',
    details: '明天有个重要演讲，有点紧张和担心，希望能顺利完成。',
    mood: 'anxious',
    createTime: '2023-05-05 22:00:00',
    images: ['/static/images/diary/sample4.jpg', '/static/images/diary/sample5.jpg']
  }
];

// 模拟获取日记列表
export function mockGetDiaryList(params) {
  console.log('模拟获取日记列表, 参数:', params);
  
  // 过滤心情
  let filteredDiaries = [...mockDiaries];
  if (params.mood && params.mood !== 'all') {
    filteredDiaries = filteredDiaries.filter(diary => diary.mood === params.mood);
  }
  
  // 关键词搜索
  if (params.keyword) {
    const keyword = params.keyword.toLowerCase();
    filteredDiaries = filteredDiaries.filter(diary => 
      diary.name.toLowerCase().includes(keyword) || 
      diary.details.toLowerCase().includes(keyword)
    );
  }
  
  // 分页
  const pageNum = params.pageNum || 1;
  const pageSize = params.pageSize || 10;
  const startIndex = (pageNum - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const pagedDiaries = filteredDiaries.slice(startIndex, endIndex);
  
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: 'success',
        rows: pagedDiaries,
        total: filteredDiaries.length
      });
    }, 300);
  });
}

// 模拟获取日记详情
export function mockGetDiary(id) {
  console.log('模拟获取日记详情, ID:', id);
  const diary = mockDiaries.find(item => item.id === parseInt(id));
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: 'success',
        data: diary || null
      });
    }, 300);
  });
}

// 模拟保存日记
export function mockSaveDiary(data) {
  console.log('模拟保存日记, 数据:', data);
  let result;
  
  // 如果有ID则是更新，否则是新增
  if (data.id) {
    // 更新
    const index = mockDiaries.findIndex(item => item.id === parseInt(data.id));
    if (index !== -1) {
      mockDiaries[index] = {
        ...mockDiaries[index],
        name: data.name || data.title,
        details: data.details || data.content,
        mood: data.mood,
        images: data.images || []
      };
      result = { ...mockDiaries[index] };
    } else {
      result = null;
    }
  } else {
    // 新增
    const newId = Math.max(...mockDiaries.map(d => d.id)) + 1;
    const newDiary = {
      id: newId,
      userId: 1,
      name: data.name || data.title,
      details: data.details || data.content,
      mood: data.mood,
      createTime: new Date().toISOString().replace('T', ' ').substr(0, 19),
      images: data.images || []
    };
    mockDiaries.unshift(newDiary);
    result = { ...newDiary };
  }
  
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: 'success',
        data: result
      });
    }, 300);
  });
}

// 模拟删除日记
export function mockDeleteDiary(id) {
  console.log('模拟删除日记, ID:', id);
  const index = mockDiaries.findIndex(item => item.id === parseInt(id));
  if (index !== -1) {
    mockDiaries.splice(index, 1);
  }
  
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: 'success'
      });
    }, 300);
  });
}

// 单独上传图片
export function uploadDiaryImage(file) {
  console.log('调用上传图片API');
  
  return new Promise((resolve, reject) => {
    // 获取token
    let token = uni.getStorageSync('token');
    console.log('上传图片Token状态:', token ? '已获取' : '未获取');
    
    // 检查token是否有效
    if (!token) {
      console.error('上传失败: 缺少有效的认证Token');
      return reject({ code: 401, msg: '认证信息丢失，请重新登录' });
    }
    
    // 确保token格式正确
    if (!token.startsWith('Bearer ')) {
      token = 'Bearer ' + token;
    }
    
    uni.uploadFile({
      url: config.baseUrl + '/diary/uploadImage',
      filePath: file,
      name: 'file',
      header: {
        'Authorization': token
      },
      success: (uploadRes) => {
        try {
          console.log('图片上传原始响应:', uploadRes);
          const result = typeof uploadRes.data === 'string' ? JSON.parse(uploadRes.data) : uploadRes.data;
          console.log('图片上传解析后结果:', result);
          
          if (result.code === 401) {
            console.error('认证失败:', result);
            reject({ code: 401, msg: '认证失败，请重新登录' });
            return;
          }
          
          if (result.code === 200 || result.code === 0) {
            resolve(result);
          } else {
            reject(result);
          }
        } catch (e) {
          console.error('解析上传响应失败:', e);
          reject(e);
        }
      },
      fail: (err) => {
        console.error('图片上传请求失败:', err);
        reject(err);
      }
    });
  });
}