import { defineStore } from 'pinia'
import api from '@/plugins/axios'

// 401 재시도 큐 처리 (동시 다발 요청 보호)
let isRefreshing = false
let queue = []

api.interceptors.response.use(
    res => res,
    async (error) => {
        const { config, response } = error
        if (!response || (response.status !== 401 && response.status !== 403) || config._retry) {
            return Promise.reject(error)
        }

        config._retry = true

        try {
            await api.post('/api/auth/refresh') // 쿠키 기반이므로 바디 불필요
            return api(config) // 토큰 재발급 성공 → 원요청 재시도
        } catch (e) {
            return Promise.reject(e)
        }
    }
)

export const useAuthStore = defineStore("auth", {
    state: () => ({
        user: null,         // { userNo, userEmail, userName ... } 서버 whoAmI 기준
        loading: false,
        error: null
    }),

    actions: {
        async signup({ userEmail, userPwd, userName }) {
            this.error = null
            this.loading = true
            try {
                await api.post("/api/auth/signup", { userEmail, userPwd, userName })
                return true
            } catch (e) {
                this.error = e.response?.data || "회원가입 실패"
                return false
            } finally {
                this.loading = false
            }
        },

        async login({ userEmail, userPwd }) {
            this.error = null
            this.loading = true
            try {
                // 성공 시 서버가 Set-Cookie(ACCESS_TOKEN/REFRESH_TOKEN) 내려줌
                await api.post("/api/auth/login", { userEmail, userPwd })
                await this.fetchUser()
                return true
            } catch (e) {
                this.error = e.response?.data || "로그인 실패"
                return false
            } finally {
                this.loading = false
            }
        },

        async fetchUser() {
            try {
                const { data } = await api.get("/api/auth/whoAmI")
                this.user = data
                console.log(data);
            } catch (e) {
                this.user = null
                console.log(e);
                // whoami가 401이면 인터셉터가 알아서 리프레시 시도함
            }
        },

        async logout() {
            try {
                await api.post("/api/auth/logout")
            } finally {
                this.user = null
            }
        }
    }
})
