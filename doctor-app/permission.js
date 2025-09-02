import { getToken } from '@/utils/auth'

// 登录页面
const loginPage = "/pages/login"
  
// 页面白名单
const whiteList = [
  '/pages/login', '/pages/register', '/pages/common/webview/index'
]

// 检查地址白名单
function checkWhite(url) {
  const path = url.split('?')[0]
  return whiteList.indexOf(path) !== -1
}

// 页面跳转验证拦截器
let list = ["navigateTo", "redirectTo", "reLaunch", "switchTab"]
list.forEach(item => {
  uni.addInterceptor(item, {
    invoke(to) {
      if (getToken()) {
        if (to.url === loginPage) {
          uni.reLaunch({ url: "/" })
        }
        return true
      } else {
        if (checkWhite(to.url)) {
          return true
        }
        uni.reLaunch({ url: loginPage })
        return false
      }
    },
    fail(err) {
      // 详细记录导航错误
      console.error(`导航方式 ${item} 失败:`, err);
      
      // 检查是否是"页面不存在"错误
      if (err.errMsg && err.errMsg.indexOf('not found') > -1) {
        console.error(`页面 ${err.errMsg.split(' ')[1]} 不存在`);
      }
      
      // 检查是否是"无法navigateTo到tabBar页面"错误
      if (err.errMsg && err.errMsg.indexOf('can not navigateTo a tabbar page') > -1) {
        console.error('尝试使用navigateTo跳转到tabBar页面，应该使用switchTab');
        // 尝试使用switchTab修复
        const url = err.errMsg.split(' ')[1].replace(/[`']/g, '');
        if (url) {
          setTimeout(() => {
            uni.switchTab({
              url: url,
              fail: (e) => {
                console.error('尝试自动switchTab修复失败:', e);
              }
            });
          }, 100);
        }
      }
    }
  })
})
