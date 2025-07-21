// ABS-FRONT/src/stores/auth.js
import { defineStore } from 'pinia'
import api from '@/plugins/axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null,
  }),
  actions: {
    async signup(payload) {
      // payload = { email, password, nickname }
      const res = await api.post('/api/auth/signup', payload)
      return res.data
    },
    async login(payload) {
      // payload = { email, password }
      const res = await api.post('/api/auth/login', payload)
      this.token = res.data.accessToken
      localStorage.setItem('token', this.token)
      // 이후 모든 요청에 헤더에 붙도록
      api.defaults.headers.common.Authorization = `Bearer ${this.token}`

      return res.data
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      delete api.defaults.headers.common.Authorization
    }
  }
})
