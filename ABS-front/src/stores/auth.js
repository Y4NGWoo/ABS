import { defineStore } from 'pinia'
import api from '@/plugins/axios'

// 401 재시도 큐 처리 (동시 다발 요청 보호)
let isRefreshing = false
let queue = []

api.interceptors.response.use(
    res => res,
    async (error) => {
        const { config, response } = error
        if (!response || response.status !== 401 || config._retry) {
            return Promise.reject(error)
        }

        config._retry = true

        const retryOriginal = () => api(config)

        if (isRefreshing) {
            return new Promise((resolve, reject) => {
                queue.push({ resolve, reject, retryOriginal })
            })
        }

        isRefreshing = true
        try {
            // 쿠키 기반이라 바디 불필요
            await api.post("/api/auth/refresh")
            // 대기중이던 요청 재시도
            queue.forEach(({ resolve, retryOriginal }) => resolve(retryOriginal()))
            queue = []
            return retryOriginal()
        } catch (e) {
            // 모두 실패 → 로그인 페이지로
            queue.forEach(({ reject }) => reject(e))
            queue = []
            window.location.href = "/login"
            return Promise.reject(e)
        } finally {
            isRefreshing = false
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
        async signup({ email, password, nickname }) {
            this.error = null
            this.loading = true
            try {
                await api.post("/api/auth/signup", { email, password, nickname })
                return true
            } catch (e) {
                this.error = e.response?.data || "회원가입 실패"
                return false
            } finally {
                this.loading = false
            }
        },

        async login({ email, password }) {
            this.error = null
            this.loading = true
            try {
                // 성공 시 서버가 Set-Cookie(ACCESS_TOKEN/REFRESH_TOKEN) 내려줌
                await api.post("/api/auth/login", { email, password })
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
