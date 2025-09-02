// 应用全局配置
module.exports = {
  // 应用名称
  name: '心理健康应用',
  // 应用版本
  version: '1.0.0',
  // 应用logo
  logo: '/static/images/logo.png',
  // 基础URL
  baseUrl: 'http://localhost:6680',
  // 替代URL，用于Android环境
  androidBaseUrl: 'http://localhost:6680',
  // WebSocket URL
  wsUrl: 'ws://localhost:6680/ws/chat/',
  // 图片上传路径
  uploadImageUrl: 'http://localhost:6680/diary/uploadImage',
  // 带图片的日记上传路径
  uploadDiaryWithImageUrl: 'http://localhost:6680/diary/addUserDiaryWithImages',
  // 请求超时时间
  timeout: 30000,
  // 网络请求选项
  requestOptions: {
    // 是否验证SSL证书，Android环境建议关闭
    sslVerify: false,
    // 是否使用HTTP2
    enableHttp2: false,
    // 是否使用QuiC
    enableQuic: false,
    // 是否使用缓存
    enableCache: false,
    // 优先使用IPv4
    firstIpv4: true
  },
  // 阿里云OSS配置
  oss: {
    endpoint: 'oss-cn-beijing.aliyuncs.com',
    accessKeyId: '个人ID',
    accessKeySecret: '个人secret',
    bucketName: '桶名',
    region: 'oss-cn-beijing',
    cdnDomain: 'https://tbytebit.oss-cn-beijing.aliyuncs.com'
  },
  // 数据库配置
  database: {
    // 数据缓存过期时间 (毫秒)
    cacheExpire: 7 * 24 * 60 * 60 * 1000 // 7天
  },
  // 应用信息
  appInfo: {
    // 应用名称
    name: "心理健康应用",
    // 应用版本
    version: "1.1.0",
    // 应用logo
    logo: "/static/logo.png",
    // 官方网站
    site_url: "",
    // 政策协议
    agreements: [{
        title: "隐私政策",
        url: "http://localhost:8080/%E9%9A%90%E7%A7%81%E6%94%BF%E7%AD%96.html"
      },
      {
        title: "用户服务协议",
        url: "http://localhost:8080/%E7%94%A8%E6%88%B7%E5%8D%8F%E8%AE%AE.html"
      }
    ]
  }
}
