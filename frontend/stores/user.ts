import { defineStore } from 'pinia'
import { jwtDecode } from 'jwt-decode'

interface UserState {
  token: string | null
  user: {
    id: number
    email: string
    role: string
  } | null
  isAuthInitialized: boolean
}

interface JWTPayload {
  id: number
  sub: string
  role: string
  exp: number
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: null,
    user: null,
    isAuthInitialized: false
  }),
  
  getters: {
    isLoggedIn(): boolean {
      return !!this.token && !!this.user
    },
    isAdmin(): boolean {
      return this.user?.role === 'ADMIN'
    }
  },
  
  actions: {
    async initializeAuth() {
      console.log('开始初始化认证状态')
      if (!import.meta.client) {
        console.log('服务端环境，跳过初始化')
        this.isAuthInitialized = false
        return
      }

      const token = sessionStorage.getItem('access_token')
      console.log('获取到的 token:', token)
      if (token) {
        try {
          const decoded = jwtDecode<JWTPayload>(token)
          const fiveMinutes = 5 * 60 * 1000
          
          if (decoded.exp * 1000 - Date.now() < fiveMinutes) {
            await this.refreshToken()
          } else {
            this.token = token
            this.user = {
              id: decoded.id,
              email: decoded.sub,
              role: decoded.role,
            }
            console.log('初始化成功，当前状态：', this.user)
          }
        }
        catch (error) {
          console.log('清空 token jwt 解码失败', error)
          this.logout()
        }
      } else {
        console.log('未找到 token')
      }
      this.isAuthInitialized = true
    },

    logout() {
      this.token = null
      this.user = null
      this.isAuthInitialized = true
      if (import.meta.client) {
        sessionStorage.removeItem('access_token')
      }
    },
    
    async refreshToken() {
      try {
        const response = await ping()
        this.setAuthResponse(response)
        return true
      }
      catch (error) {
        console.error('Token 刷新失败:', error)
        this.logout()
        return false
      }
    },
    
    setAuthResponse(response: AuthResponse) {
      this.token = response.token
      const decoded = jwtDecode<JWTPayload>(response.token)
      this.user = {
        id: decoded.id,
        email: decoded.sub,
        role: decoded.role,
      }
      if (import.meta.client) {
        sessionStorage.setItem('access_token', response.token)
      }
      else {
        console.log('skip set access token')
      }
    }
  }
}) 