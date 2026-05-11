import { defineStore } from 'pinia'
import { authApi, LoginPayload, UserVo } from '@/api/auth'

interface AuthState {
  token: string
  user: UserVo | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: '',
    user: null,
  }),

  getters: {
    isLoggedIn: (s) => !!s.token,
    displayName: (s) => s.user?.nickName || s.user?.username || '游客',
    roles: (s) =>
      (s.user?.roles || '')
        .split(',')
        .map((r) => r.trim())
        .filter(Boolean),
  },

  actions: {
    async login(payload: LoginPayload) {
      const res = await authApi.login(payload)
      this.token = res.token
      this.user = res.user
    },

    async fetchMe() {
      if (!this.token) return
      try {
        this.user = await authApi.me()
      } catch {
        // 401 会由 http 拦截器触发 logout
      }
    },

    logout() {
      this.token = ''
      this.user = null
    },
  },

  persist: {
    key: 'online-college-admin-auth',
    storage: localStorage,
    paths: ['token', 'user'],
  },
})
