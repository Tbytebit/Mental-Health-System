// 配置文件
export default {
  // API基础URL，固定地址
  baseUrl: 'http://localhost:6680', // 服务器地址
  
  // 超时时间
  timeout: 30000,
  
  // Token键名
  tokenKey: 'token',
  
  // 默认请求头
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
}