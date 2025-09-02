const TokenKey = 'token'

export function getToken() {
  let token = uni.getStorageSync(TokenKey);
  
  // 如果找不到token，尝试其他可能的key
  if (!token) {
    const possibleKeys = ['App-Token', 'Authorization', 'auth_token'];
    for (const key of possibleKeys) {
      token = uni.getStorageSync(key);
      if (token) {
        console.log(`从${key}中找到token，同步到标准key`);
        // 同步到标准key
        uni.setStorageSync(TokenKey, token);
        break;
      }
    }
    
    // 如果还是没有，尝试从H5 localStorage获取
    if (!token && typeof localStorage !== 'undefined') {
      try {
        token = localStorage.getItem(TokenKey) || 
                localStorage.getItem('App-Token') || 
                localStorage.getItem('Authorization');
        if (token) {
          uni.setStorageSync(TokenKey, token);
          console.log('从localStorage恢复token成功');
        }
      } catch (e) {
        console.error('从localStorage获取token失败:', e);
      }
    }
  }
  
  return token;
}

export function setToken(token) {
  return uni.setStorageSync(TokenKey, token)
}

export function removeToken() {
  return uni.removeStorageSync(TokenKey)
}

export function refreshToken() {
  // Implement token refresh logic here
  // Example: request new token from server and update storage
  const newToken = 'newTokenValue'; // Replace with actual logic
  setToken(newToken);
}
