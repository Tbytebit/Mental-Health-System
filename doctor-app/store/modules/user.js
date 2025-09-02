import config from '@/config'
import storage from '@/utils/storage'
import constant from '@/utils/constant'
import { login, logout, getInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'

const baseUrl = config.baseUrl

const user = {
  state: {
    token: getToken(),
    userId: uni.getStorageSync('userId') || null,
    name: storage.get(constant.name),
    avatar: storage.get(constant.avatar),
    roles: storage.get(constant.roles),
    permissions: storage.get(constant.permissions)
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_USER_ID: (state, userId) => {
      state.userId = userId
      // 同时存储到本地，便于其他页面使用
      uni.setStorageSync('userId', userId)
    },
    SET_NAME: (state, name) => {
      state.name = name
      storage.set(constant.name, name)
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
      storage.set(constant.avatar, avatar)
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
      storage.set(constant.roles, roles)
    },
    SET_PERMISSIONS: (state, permissions) => {
      state.permissions = permissions
      storage.set(constant.permissions, permissions)
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      const code = userInfo.code
      const uuid = userInfo.uuid
      return new Promise((resolve, reject) => {
        login(username, password, code, uuid).then(res => {
          setToken(res.token)
          commit('SET_TOKEN', res.token)
          
          // 如果返回了用户ID，直接存储
          if (res.userId) {
            commit('SET_USER_ID', res.userId)
          }
          
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        getInfo().then(res => {
          const user = res.user
          const avatar = (user.avatar == "" || user.avatar == null) ? require("@/static/images/profile.jpg") : user.avatar;
          
          // 存储用户ID
          commit('SET_USER_ID', user.userId)
          commit('SET_NAME', user.userName)
          commit('SET_AVATAR', avatar)
          commit('SET_ROLES', res.roles)
          commit('SET_PERMISSIONS', res.permissions)
          
          // 保存用户信息到本地存储，方便其他页面使用
          const userInfo = {
            id: user && user.userId ? user.userId : '',
            username: user.userName,
            avatar: avatar,
            roles: res.roles || ['ROLE_DEFAULT'],
            permissions: res.permissions || [],
            nickName: user.nickName || user.userName
          }
          uni.setStorageSync('userInfo', userInfo)
          
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 退出系统
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        logout(state.token).then(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          commit('SET_PERMISSIONS', [])
          commit('SET_USER_ID', null)
          removeToken()
          storage.clean()
          
          // 清除本地存储的用户信息
          uni.removeStorageSync('userInfo')
          
          // 清除本地存储的用户ID
          uni.removeStorageSync('userId')
          
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    }
  }
}

export default user
