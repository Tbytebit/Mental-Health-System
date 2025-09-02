import request from '@/utils/request'
import config from '@/config'
import { ossUpload } from '@/utils/upload'

// 用户密码重置
export function updateUserPwd(oldPassword, newPassword) {
  const data = {
    oldPassword,
    newPassword
  }
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    params: data
  })
}

// 查询用户个人信息
export function getUserProfile() {
  return request({
    url: '/system/user/profile',
    method: 'get'
  })
}

// 修改用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/system/user/profile',
    method: 'put',
    data: data
  })
}

// 搜索用户
export function searchUserByNameOrId(keyword) {
  return request({
    'url': '/system/user/search',
    'method': 'get',
    params: { keyword }
  });
}

// 获取用户信息
export function getUserInfo(userId, type = 'normal') {
  return request({
    'url': `/system/user/getUserInfo/${userId}`,
    'method': 'get',
    'params': { type }
  });
}

// 更新用户基本信息
export function updateUserInfo(data) {
  return request({
    'url': '/system/user/profile',
    'method': 'put',
    data: data
  });
}

// 上传用户头像
export function uploadAvatar(file) {
  return new Promise((resolve, reject) => {
    // 如果传入的是对象格式，需要适应文件路径和名称
    const filePath = file.filePath || file;
    const name = file.name || 'avatarfile';
    
    // 使用OSS上传，指定目录为avatar
    ossUpload({
      filePath: filePath,
      name: 'file', // 论坛上传接口使用'file'作为参数名
      directory: 'avatar'
    }).then(ossResult => {
      console.log('OSS上传成功:', ossResult);
      
      // 获取上传后的图片URL
      const avatarUrl = ossResult.data && ossResult.data.url 
        ? ossResult.data.url 
        : (ossResult.url || ossResult.imgUrl || '');
      
      if (!avatarUrl) {
        console.error('未能获取有效的头像URL:', ossResult);
        return reject({ code: 500, msg: '服务器返回的头像URL无效' });
      }
      
      // 调用更新用户头像的接口
      updateUserAvatar(avatarUrl).then(updateRes => {
        console.log('头像URL更新成功:', updateRes);
        resolve({
          code: 200,
          imgUrl: avatarUrl,
          msg: '头像上传成功'
        });
      }).catch(updateErr => {
        console.error('头像URL更新失败:', updateErr);
        // 检查是否是静默失败（OSS上传成功但更新接口失败）
        if (updateErr && updateErr.failSilently) {
          resolve({
            code: 200,
            imgUrl: avatarUrl,
            msg: '头像上传成功，但更新资料失败'
          });
        } else {
          // 即使更新失败，我们仍然返回OSS上传的URL
          resolve({
            code: 200,
            imgUrl: avatarUrl,
            msg: '头像上传成功，但更新资料失败'
          });
        }
      });
    }).catch(ossErr => {
      console.error('OSS上传失败:', ossErr);
      // 如果OSS上传失败，尝试使用原始的头像上传接口
      fallbackAvatarUpload(file).then(res => {
        resolve(res);
      }).catch(err => {
        reject(err);
      });
    });
  });
}

// 原始头像上传方法（备用方案）
function fallbackAvatarUpload(file) {
  return new Promise((resolve, reject) => {
    const formData = new FormData();
    const filePath = file.filePath || file;
    
    // 获取token
    let token = uni.getStorageSync('token');
    
    // 检查token是否有效
    if (!token) {
      console.error('上传失败: 缺少有效的认证Token');
      return reject({ code: 401, msg: '认证信息丢失，请重新登录' });
    }
    
    uni.uploadFile({
      url: config.baseUrl + '/system/user/profile/avatar',
      filePath: filePath,
      name: 'avatarfile',
      header: {
        'Authorization': token.startsWith('Bearer ') ? token : 'Bearer ' + token
      },
      success: (res) => {
        try {
          console.log('原始头像上传响应:', res);
          const result = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
          
          if (result.code === 401) {
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
        console.error('头像上传请求失败:', err);
        reject(err);
      }
    });
  });
}

// 直接更新用户头像URL（配合OSS使用）
export function updateUserAvatar(avatarUrl) {
  // 尝试使用自定义头像更新接口
  return request({
    'url': '/system/user/profile/avatar/update',
    'method': 'post',
    data: { avatarUrl }
  }).catch(error => {
    console.error('使用avatar/update接口更新头像失败:', error);
    
    // 如果自定义接口失败，尝试使用通用的用户信息更新接口
    return request({
      'url': '/system/user/profile',
      'method': 'put',
      data: { avatar: avatarUrl }
    }).catch(err => {
      console.error('使用profile接口更新头像也失败:', err);
      // 返回一个特殊的响应，以便上层函数能够处理
      return Promise.resolve({
        code: 500,
        msg: '更新头像URL失败，但OSS上传成功',
        success: false,
        failSilently: true  // 标记为静默失败，外层可以继续使用图片URL
      });
    });
  });
}
