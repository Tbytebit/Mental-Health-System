import store from '@/store'
import config from '@/config'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import { toast, showConfirm, tansParams } from '@/utils/common'

let timeout = config.timeout || 30000
const baseUrl = config.baseUrl

// 阿里云OSS上传函数
const ossUpload = ({filePath, name = 'file', directory = 'avatar', formData = {}}) => {
  return new Promise((resolve, reject) => {
    // 获取token
    let token = getToken();
    console.log('上传图片Token状态:', token ? '已获取' : '未获取');
    
    // 检查token是否有效
    if (!token) {
      console.error('上传失败: 缺少有效的认证Token');
      toast('请先登录后再操作');
      return reject({ code: 401, msg: '认证信息丢失，请重新登录' });
    }
    
    // 确保token格式正确
    if (!token.startsWith('Bearer ')) {
      token = 'Bearer ' + token;
    }
    
    console.log('===== OSS文件上传开始 =====');
    console.log('上传文件路径:', filePath);
    console.log('目标目录:', directory);
    
    // 检查文件路径是否有效
    if (!filePath) {
      console.error('上传失败: 文件路径无效');
      toast('文件路径无效');
      return reject({ code: 400, msg: '文件路径无效' });
    }
    
    // 根据目录选择不同的上传端点
    let uploadUrl = '';
    if (directory === 'forum') {
      uploadUrl = '/api/forum/uploadImage';
    } else if (directory === 'diary') {
      uploadUrl = '/diary/uploadImage';
    } else if (directory === 'chat') {
      uploadUrl = '/chat/message/upload/image';
      // 确保传递正确的目录参数
      formData.directory = 'doctor-chat';
    } else {
      // 默认使用论坛的图片上传（用于头像等）
      uploadUrl = '/api/forum/uploadImage';
    }
    
    const finalFormData = {
      ...formData,
      directory: directory // 仍然传递目录参数，服务端可能会用来分类存储
    };
    
    // 检查文件是否是本地临时文件路径
    const isLocalFile = filePath.indexOf('tmp') !== -1 || 
                        filePath.indexOf('wxfile') !== -1 || 
                        filePath.indexOf('_doc') !== -1 ||
                        filePath.startsWith('file://') ||
                        filePath.startsWith('/storage/');
    
    if (!isLocalFile) {
      console.error('上传失败: 文件路径不是本地文件');
      toast('不支持的文件路径格式');
      return reject({ code: 400, msg: '不支持的文件路径格式' });
    }
    
    console.log('最终上传URL:', config.baseUrl + uploadUrl);
    console.log('最终表单数据:', finalFormData);
    
    uni.uploadFile({
      url: config.baseUrl + uploadUrl,
      filePath: filePath,
      name: name,
      formData: finalFormData,
      header: {
        'Authorization': token
      },
      timeout: timeout,
      success: (uploadRes) => {
        try {
          console.log('OSS上传原始响应:', uploadRes);
          let result;
          
          // 尝试解析响应数据
          try {
            result = typeof uploadRes.data === 'string' ? JSON.parse(uploadRes.data) : uploadRes.data;
          } catch (parseError) {
            console.error('解析响应数据失败:', parseError, uploadRes.data);
            toast('服务器返回的数据格式错误');
            reject({ code: 500, msg: '服务器返回的数据格式错误' });
            return;
          }
          
          console.log('OSS上传解析后结果:', result);
          
          if (!result) {
            toast('上传结果为空');
            reject({ code: 500, msg: '上传结果为空' });
            return;
          }
          
          // 验证URL是否存在于结果中
          if (result.code === 200 || result.code === 0) {
            // 确保返回的URL是可用的
            if (!result.data || !result.data.url) {
              if (result.url) {
                // 如果顶层有url字段，将其添加到data中
                result.data = { url: result.url };
              } else if (result.imgUrl) {
                // 如果有imgUrl字段，使用它
                result.data = { url: result.imgUrl };
              } else {
                console.warn('上传成功但未返回图片URL:', result);
                toast('服务器未返回有效的图片URL');
                reject({ code: 500, msg: '服务器未返回有效的图片URL' });
                return;
              }
            }
            
            // 对URL进行规范化
            if (result.data && result.data.url) {
              // 确保URL是完整的
              let url = result.data.url;
              if (url && !url.startsWith('http')) {
                url = 'https://' + url;
                result.data.url = url;
              }
            }
            
            resolve(result);
          } else if (result.code === 401) {
            console.error('认证失败:', result);
            toast('认证失败，请重新登录');
            reject({ code: 401, msg: '认证失败，请重新登录' });
          } else {
            toast(result.msg || '上传失败');
            reject(result);
          }
        } catch (e) {
          console.error('处理上传响应失败:', e);
          toast('处理上传响应失败');
          reject({ code: 500, msg: e.message || '处理上传响应失败' });
        }
      },
      fail: (err) => {
        console.error('OSS上传请求失败:', err);
        
        let errorMsg = '上传失败';
        if (err.errMsg) {
          if (err.errMsg.includes('timeout')) {
            errorMsg = '上传超时，请检查网络';
          } else if (err.errMsg.includes('fail')) {
            errorMsg = '网络连接失败，请检查网络设置';
          }
        }
        
        toast(errorMsg);
        reject(err);
      }
    });
  });
};

const upload = config => {
  // 是否需要设置 token
  const isToken = (config.headers || {}).isToken === false
  config.header = config.header || {}
  if (getToken() && !isToken) {
    // 确保token格式正确
    const token = getToken();
    if (token) {
      if (token.startsWith('Bearer ')) {
        config.header['Authorization'] = token;
      } else {
        config.header['Authorization'] = 'Bearer ' + token;
      }
    }
  }
  
  // get请求映射params参数
  if (config.params) {
    let url = config.url + '?' + tansParams(config.params)
    url = url.slice(0, -1)
    config.url = url
  }
  
  console.log('===== 文件上传开始 =====');
  console.log('上传URL:', baseUrl + config.url);
  console.log('文件路径:', config.filePath);
  console.log('表单数据:', config.formData);
  console.log('请求头:', config.header);
  
  return new Promise((resolve, reject) => {
    // 检查文件路径是否有效
    if (!config.filePath) {
      console.error('上传失败: 文件路径无效');
      toast('文件路径无效');
      return reject('文件路径无效');
    }
    
    uni.uploadFile({
      timeout: config.timeout || timeout,
      url: baseUrl + config.url,
      filePath: config.filePath,
      name: config.name || 'file',
      header: config.header,
      formData: config.formData || {},
      sslVerify: false, // 关闭SSL证书验证以防止Android上的证书问题
      success: (res) => {
        console.log('===== 上传响应 =====');
        console.log('响应状态码:', res.statusCode);
        console.log('原始响应:', res.data);
        
        // 确保响应是JSON格式
        let result;
        try {
          result = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
        } catch (e) {
          console.error('解析上传响应失败:', e, res.data);
          toast('服务器返回的数据格式错误');
          reject('服务器返回的数据格式错误');
          return;
        }
        
        const code = result.code || 200
        const msg = errorCode[code] || result.msg || errorCode['default']
        
        if (code === 200 || code === 0) {
          console.log('上传成功:', result);
          resolve(result)
        } else if (code == 401) {
          showConfirm("登录状态已过期，您可以继续留在该页面，或者重新登录?").then(res => {
            if (res.confirm) {
              store.dispatch('LogOut').then(res => {
                uni.reLaunch({ url: '/pages/login/login' })
              })
            }
          })
          reject('无效的会话，或者会话已过期，请重新登录。')
        } else if (code === 500) {
          console.error('服务器错误:', result);
          toast(msg)
          reject('500')
        } else if (code !== 200) {
          console.error('上传失败:', code, msg);
          toast(msg)
          reject(code)
        }
      },
      fail: (error) => {
        console.error('===== 上传失败 =====');
        console.error('上传异常:', error);
        
        let message = '未知错误';
        if (typeof error === 'string') {
          message = error;
        } else if (error && error.errMsg) {
          if (error.errMsg.includes('timeout')) {
            message = '上传超时，请检查网络';
          } else if (error.errMsg.includes('fail')) {
            message = '网络连接失败，请检查网络设置';
          } else {
            message = error.errMsg;
          }
        } else if (error && error.message) {
          message = error.message;
          
          if (message == 'Network Error') {
            message = '后端接口连接异常';
          } else if (message.includes('timeout')) {
            message = '系统接口请求超时';
          } else if (message.includes('Request failed with status code')) {
            message = '系统接口' + message.substr(message.length - 3) + '异常';
          }
        }
        
        console.error('错误消息:', message);
        toast(message);
        reject(error);
      }
    });
  });
}

export default upload

export { ossUpload }
