<script>
  import config from './config'
  import store from '@/store'
  import { getToken } from '@/utils/auth'
  import wsChat from '@/utils/ws-chat'

  export default {
    onLaunch: function() {
      console.log('App Launch');
      this.initApp()
    },
    
    onShow: function() {
      console.log('App Show');
      // 应用切回前台，尝试重新连接WebSocket
      this.reconnectWebSocket();
    },
    
    onHide: function() {
      console.log('App Hide');
    },
    methods: {
      // 初始化应用
      initApp() {
        // 初始化应用配置
        this.initConfig()
        // 检查用户登录状态
        //#ifdef H5
        this.checkLogin()
        //#endif
        
        // 初始化聊天存储
        this.initChatStorage()
        
        // 初始化WebSocket聊天
        this.initWebSocketChat()
      },
      initConfig() {
        this.globalData = this.globalData || {};
        this.globalData.config = config
      },
      checkLogin() {
        if (!getToken()) {
          this.$tab.reLaunch('/pages/login') 
        }
      },
      
      // 初始化聊天存储
      async initChatStorage() {
        try {
          // 初始化聊天数据
          // await chatDB.init(); 删除此行，因为chatDB未定义
          console.log('聊天数据初始化完成');
          
          // 确保有一个默认用户ID，防止未登录时的错误
          const userId = uni.getStorageSync('userId');
          const currentUserId = uni.getStorageSync('currentUserId');
          
          if (!userId || (typeof userId === 'string' && userId.startsWith('guest_'))) {
            // 检查是否有用户信息
            let newUserId = null;
            
            // 尝试从用户信息获取
            try {
              const userInfoStr = uni.getStorageSync('userInfo');
              if (userInfoStr) {
                const userInfo = typeof userInfoStr === 'string' ? JSON.parse(userInfoStr) : userInfoStr;
                newUserId = userInfo.userId || userInfo.id;
                console.log('从用户信息获取到userId:', newUserId);
              }
            } catch (e) {
              console.error('解析用户信息失败:', e);
            }
            
            // 如果没有，尝试从token获取
            if (!newUserId) {
              try {
                const token = getToken();
                if (token) {
                  // 尝试解析JWT token
                  const parts = token.split('.');
                  if (parts.length === 3) {
                    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
                    const jsonPayload = decodeURIComponent(
                      atob(base64)
                        .split('')
                        .map(c => {
                          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                        })
                        .join('')
                    );
                    
                    try {
                      const payload = JSON.parse(jsonPayload);
                      newUserId = payload.userId || payload.user_id || payload.id || payload.sub;
                      console.log('从token获取到userId:', newUserId);
                    } catch (e) {
                      console.error('解析token内容失败:', e);
                    }
                  }
                }
              } catch (e) {
                console.error('解析token获取用户ID失败:', e);
              }
            }
            
            // 如果获取到了有效ID，更新存储
            if (newUserId && (typeof newUserId !== 'string' || !newUserId.startsWith('guest_'))) {
              uni.setStorageSync('userId', newUserId);
              uni.setStorageSync('currentUserId', newUserId);
              store.commit('SET_USER_ID', newUserId);
              console.log('已更新用户ID:', newUserId);
            } else if (!currentUserId) {
              // 仅当完全没有userId时才使用临时ID
              const tempId = 'guest_' + Date.now();
              uni.setStorageSync('currentUserId', tempId);
              console.log('使用临时访客ID:', tempId, '请尽快登录获取真实ID');
            }
          } else if (userId && currentUserId !== userId) {
            // 如果userId和currentUserId不一致，以userId为准
            uni.setStorageSync('currentUserId', userId);
            console.log('同步currentUserId与userId:', userId);
          }
        } catch (error) {
          console.error('初始化聊天数据失败:', error);
        }
      },
      
      // 初始化WebSocket聊天
      initWebSocketChat() {
        const userId = uni.getStorageSync('userId');
        const token = getToken();
        
        if (userId && token) {
          console.log('初始化WebSocket聊天');
          // 使用配置的后端地址或默认地址
          const wsUrl = config.wsUrl || 'ws://localhost:6680/ws/chat/';
          
          // 初始化WebSocket聊天
          wsChat.init({
            url: wsUrl,
            userId: userId,
            heartbeatInterval: 30000, // 30秒心跳
            checkUnreadInterval: 60000 // 1分钟检查未读消息
          });
        } else {
          console.log('用户未登录，跳过WebSocket聊天初始化');
        }
      },
      
      // 重新连接WebSocket
      reconnectWebSocket() {
        const userId = uni.getStorageSync('userId');
        const token = getToken();
        
        if (userId && token && !wsChat.isConnected) {
          console.log('尝试重新连接WebSocket');
          wsChat.connect();
        }
      }
    }
  }
</script>

<style lang="scss">
  @import '@/static/scss/index.scss'
</style>
