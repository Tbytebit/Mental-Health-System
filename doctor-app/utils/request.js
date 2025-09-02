import store from '@/store'
import config from '@/config'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import { toast, showConfirm, tansParams } from '@/utils/common'

// 设置基础配置
let timeout = config.timeout || 10000

// 根据平台选择不同的基础URL
let baseUrl = config.baseUrl || 'http://localhost:6680'

// 检测是否为Android环境，使用替代URL
const platform = uni.getSystemInfoSync().platform
if (platform === 'android' && config.androidBaseUrl) {
  baseUrl = config.androidBaseUrl
  console.log('检测到Android环境，使用androidBaseUrl:', baseUrl);
}

console.log('API基础URL:', baseUrl);

// 获取网络请求选项
const requestOptions = config.requestOptions || {
  sslVerify: false,
  enableHttp2: false,
  enableQuic: false,
  enableCache: false,
  firstIpv4: true
}

const request = config => {
  // 是否需要设置 token
  const isToken = (config.headers || {}).isToken === false
  config.header = config.header || {}
  if (getToken() && !isToken) {
    // 确保token格式正确，添加Bearer前缀
    const token = getToken();
    if (token) {
      console.log('请求添加Authorization头, token长度:', token.length);
      // 如果token已经包含Bearer前缀，则不再添加
      if (token.startsWith('Bearer ')) {
        config.header['Authorization'] = token;
      } else {
        config.header['Authorization'] = 'Bearer ' + token;
      }
    }
  }
  
  // 添加当前用户ID到X-User-Id请求头
  const userId = store.state.user.userId || uni.getStorageSync('userId');
  if (userId) {
    console.log('添加用户ID到请求头:', userId);
    config.header['X-User-Id'] = userId;
    
    // 特别处理好友相关请求，确保userId有效
    if (config.url && (config.url.includes('/chat/friend/') || config.url.includes('/chat/forum/'))) {
      console.log('检测到好友相关请求，确保userId有效:', userId);
    }
  } else if (!config.header['X-User-Id']) {
    console.warn('未找到用户ID，请求可能会失败');
    
    // 如果是好友相关请求但没有userId，直接提示用户
    if (config.url && (config.url.includes('/chat/friend/') || config.url.includes('/chat/forum/'))) {
      uni.showToast({
        title: '请先登录后再操作',
        icon: 'none',
        duration: 2000
      });
      
      return Promise.reject('未登录');
    }
  }
  
  // 处理权限验证标志
  if (config.headers && config.headers.skipPermission) {
    // 添加跳过权限验证的头
    config.header['X-Skip-Permission'] = 'true';
    console.log('添加跳过权限验证头');
  }
  
  // 添加请求头
  if (!config.headers || !config.headers['Content-Type']) {
    // 设置默认的Content-Type
    config.header['Content-Type'] = 'application/json;charset=UTF-8';
  } else {
    // 使用自定义Content-Type
    config.header['Content-Type'] = config.headers['Content-Type'];
  }
  
  // 将headers中的其他自定义头部添加到header中
  if (config.headers) {
    for (const key in config.headers) {
      if (key !== 'Content-Type' && key !== 'isToken' && key !== 'skipPermission') {
        config.header[key] = config.headers[key];
      }
    }
  }
  
  // 处理请求参数
  let url = config.url;
  
  // 处理请求的params参数，适用于所有方法
  if (config.params) {
    // 处理params中的undefined值
    for (const key in config.params) {
      if (config.params[key] === undefined || config.params[key] === 'undefined') {
        config.params[key] = null;
      }
    }
    
    // 拼接URL参数
    const paramsStr = tansParams(config.params);
    url = url + '?' + paramsStr.slice(0, -1);
    config.url = url;
  }
  
  // 特别处理POST请求的data
  if (config.method && config.method.toLowerCase() === 'post' && config.data) {
    // 确保没有undefined值导致序列化问题
    if (typeof config.data === 'object') {
      for (const key in config.data) {
        if (config.data[key] === undefined) {
          config.data[key] = null;
        }
      }
    }
  }
  
  return new Promise((resolve, reject) => {
    // 取消令牌处理
    let requestTask;
    
    // 增强日志记录，帮助调试
    const methodLog = config.method ? config.method.toUpperCase() : 'GET';
    console.log(`===== ${methodLog} 请求开始 =====`);
    console.log('请求URL:', (config.baseUrl || baseUrl) + config.url);
    console.log('请求方法:', methodLog);
    console.log('请求数据:', JSON.stringify(config.data));
    console.log('请求头:', JSON.stringify(config.header));
    
    // 设置请求超时时间
    const requestTimeout = config.timeout || timeout;
    
    // 处理文件上传请求
    if (config.isUpload) {
      requestTask = uni.uploadFile({
        url: config.baseUrl || (baseUrl + config.url),
        files: config.files,
        filePath: config.filePath,
        name: config.name || 'file',
        formData: config.formData || {},
        header: config.header,
        timeout: requestTimeout,
        sslVerify: config.sslVerify !== undefined ? config.sslVerify : requestOptions.sslVerify,
        success: (res) => {
          console.log('文件上传响应:', res);
          
          let data = res.data;
          try {
            if (typeof data === 'string') {
              data = JSON.parse(data);
            }
            
            const code = data.code || 200;
            const msg = errorCode[code] || data.msg || errorCode['default'];
            
            if (code === 401) {
              showConfirm('登录状态已过期，您可以继续留在该页面，或者重新登录?').then(res => {
                if (res.confirm) {
                  store.dispatch('LogOut').then(() => {
                    uni.reLaunch({ url: '/pages/login/login' })
                  })
                }
              })
              reject('无效的会话，或者会话已过期，请重新登录。')
            } else if (code === 500) {
              toast(msg);
              console.error('服务器错误:', data);
              resolve({
                code: 500,
                data: {
                  success: false,
                  message: msg || data.msg || '服务器错误'
                }
              });
            } else if (code !== 200 && code !== 0) {
              toast(msg);
              console.error('业务错误:', code, msg);
              resolve({
                code: code,
                data: {
                  success: false,
                  message: msg || data.msg || '业务处理失败'
                }
              });
            } else {
              console.log('上传请求成功:', data);
              resolve(data);
            }
          } catch (e) {
            console.error('处理上传响应失败:', e);
            reject(e);
          }
        },
        fail: (error) => {
          console.error('上传请求失败:', error);
          
          // 检查是否为网络连接问题
          if (error.errMsg && (
              error.errMsg.includes('fail') || 
              error.errMsg.includes('timeout') || 
              error.errMsg.includes('network error'))) {
            console.error('网络连接错误');
            toast('网络连接失败，请检查网络设置');
          }
          
          reject(error);
        },
        complete: () => {
          requestTask = null;
        }
      });
    } else {
      // 普通请求处理
      requestTask = uni.request({
        method: config.method || 'GET',
        timeout: requestTimeout,
        url: config.baseUrl || (baseUrl + config.url),
        data: config.data,
        header: config.header,
        dataType: 'json',
        responseType: config.responseType || 'text',
        // 网络请求配置选项
        sslVerify: config.sslVerify !== undefined ? config.sslVerify : requestOptions.sslVerify,
        withCredentials: false, // 确保跨域请求不带凭证
        firstIpv4: requestOptions.firstIpv4,
        enableHttp2: requestOptions.enableHttp2,
        enableQuic: requestOptions.enableQuic,
        enableCache: requestOptions.enableCache,
        success: (res) => {
          console.log(`===== ${methodLog} 响应开始 =====`);
          console.log('响应状态码:', res.statusCode);
          console.log('响应头:', JSON.stringify(res.header));
          console.log('原始响应数据:', JSON.stringify(res.data));
          
          // 处理HTTP状态码
          if (res.statusCode === 401) {
            showConfirm('登录状态已过期，您可以继续留在该页面，或者重新登录?').then(res => {
              if (res.confirm) {
                store.dispatch('LogOut').then(() => {
                  uni.reLaunch({ url: '/pages/login/login' })
                })
              }
            })
            reject('无效的会话，或者会话已过期，请重新登录。')
            return;
          }
          
          if (res.statusCode === 500) {
            let msg = '服务器错误';
            if (res.data && res.data.msg) {
              msg = res.data.msg;
            } else if (res.data && res.data.message) {
              msg = res.data.message;
            }
            toast(msg);
            console.error('服务器错误:', res.data);
            resolve({
              code: 500,
              data: {
                success: false,
                message: msg
              }
            });
            return;
          }
          
          if (res.statusCode !== 200) {
            let errorMsg = `请求失败，状态码: ${res.statusCode}`;
            console.error(errorMsg, res);
            toast(errorMsg);
            reject(errorMsg);
            return;
          }
          
          // 处理业务状态码
          const data = res.data;
          const code = data.code || 200;
          const msg = errorCode[code] || data.msg || errorCode['default'];
          
          if (code === 401) {
            showConfirm('登录状态已过期，您可以继续留在该页面，或者重新登录?').then(res => {
              if (res.confirm) {
                store.dispatch('LogOut').then(() => {
                  uni.reLaunch({ url: '/pages/login/login' })
                })
              }
            })
            reject('无效的会话，或者会话已过期，请重新登录。')
          } else if (code === 500) {
            toast(msg);
            console.error('服务器错误:', res.data);
            resolve({
              code: 500,
              data: {
                success: false,
                message: msg || res.data.msg || '服务器错误'
              }
            });
          } else if (code !== 200 && code !== 0) {
            toast(msg);
            console.error('业务错误:', code, msg);
            resolve({
              code: code,
              data: {
                success: false,
                message: msg || res.data.msg || '业务处理失败'
              }
            });
          } else {
            console.log(`${methodLog} 请求成功:`, res.data);
            resolve(res.data);
          }
        },
        fail: (error) => {
          console.error(`===== ${methodLog} 请求失败 =====`);
          console.error('错误信息:', error);
          
          // 检查是否为网络连接问题
          if (error.errMsg && (
              error.errMsg.includes('fail') || 
              error.errMsg.includes('timeout') || 
              error.errMsg.includes('network error'))) {
            console.error('网络连接错误');
            
            // 对特定请求进行特殊处理
            if (config.url && (config.url.includes('/chat/friend/add') || config.url.includes('/chat/forum/add'))) {
              console.error('添加相关请求失败');
              
              // 返回一个格式化错误给调用者，而不是reject
              resolve({
                code: 500,
                success: false,
                message: '网络连接错误，请检查网络后重试'
              });
              return;
            }
            
            // 显示网络错误提示
            toast('网络连接失败，请检查网络设置');
          }
          
          // 检查是否有自定义错误处理函数
          if (typeof config.errorHandler === 'function') {
            try {
              // 使用自定义错误处理函数
              const result = config.errorHandler(error);
              if (result) {
                // 如果自定义处理返回结果，则使用该结果
                resolve(result);
                return;
              }
            } catch (e) {
              console.error('自定义错误处理失败:', e);
            }
          }
          
          reject(error);
        },
        complete: () => {
          requestTask = null; // 清除任务引用
        }
      });
    }
    
    // 配置取消令牌
    if (typeof config.cancelToken === 'function') {
      config.cancelToken(function(reason) {
        if (requestTask) {
          requestTask.abort();
        }
      });
    }
  });
}

// 上传文件
request.upload = (url, file, formData = {}, options = {}) => {
  return request({
    url: url,
    isUpload: true,
    filePath: typeof file === 'string' ? file : undefined,
    files: Array.isArray(file) ? file : undefined,
    formData: formData,
    name: options.name || 'file',
    header: options.header || {},
    timeout: options.timeout
  });
}

// 通用方法
request.get = (url, params, options) => {
  return request({
    url: url,
    method: 'get',
    params,
    ...options
  })
}

request.post = (url, data, options) => {
  return request({
    url: url,
    method: 'post',
    data,
    ...options
  })
}

request.put = (url, data, options) => {
  return request({
    url: url,
    method: 'put',
    data,
    ...options
  })
}

request.delete = (url, data, options) => {
  return request({
    url: url,
    method: 'delete',
    data,
    ...options
  })
}

export default request
