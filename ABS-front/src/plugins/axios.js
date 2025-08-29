// ABS-FRONT/src/plugins/axios.js
// import axios from 'axios';
//
// const api = axios.create({
//   baseURL: import.meta.env.VITE_API_BASE_URL,
//   withCredentials: true,
//   xsrfCookieName: "XSRF-TOKEN",   // Spring이 뿌려주는 쿠키 이름
//   xsrfHeaderName: "X-XSRF-TOKEN", // 요청에 자동 첨부되는 헤더 이름
// });
//
// export default api;
// src/plugins/axios.js 또는 api 인스턴스 정의 파일
import axios from 'axios'

export const api = axios.create({
    baseURL: '/api',
    withCredentials: true,
    xsrfCookieName: "XSRF-TOKEN",   // Spring이 뿌려주는 쿠키 이름
    xsrfHeaderName: "X-XSRF-TOKEN", // 요청에 자동 첨부되는 헤더 이름
})

// 🔒 리프레시 전용 인스턴스(인터셉터 미적용)
const refreshApi = axios.create({
    baseURL: '/api',
    withCredentials: true,
})

let isRefreshing = false
let queue = []

const isAuthUrl = (url = '') =>
    url.includes('/api/auth/refresh') ||
    url.includes('/api/auth/login') ||
    url.includes('/api/auth/signup')

api.interceptors.response.use(
    res => res,
    async (error) => {
        const { config, response } = error
        if (!response) return Promise.reject(error)

        // 1) 인증 관련 URL이면 개입 금지 (무한루프 차단)
        if (isAuthUrl(config?.url)) {
            return Promise.reject(error)
        }

        // 2) 401/403 이외는 패스
        if (response.status !== 401 && response.status !== 403) {
            return Promise.reject(error)
        }

        // 3) 이미 재시도한 요청은 패스
        if (config._retry) {
            return Promise.reject(error)
        }

        // 4) 중복 요청 큐잉
        if (isRefreshing) {
            return new Promise((resolve, reject) => {
                queue.push({ resolve, reject, config })
            })
        }

        // 5) 실제 리프레시 1회만 실행
        isRefreshing = true
        config._retry = true
        try {
            // 🔑 쿠키 기반이므로 바디 불필요
            await refreshApi.post('/auth/refresh')

            // 대기 중이던 요청 재시도
            queue.forEach(({ resolve, config }) => resolve(api(config)))
            queue = []

            // 원 요청 재시도
            return api(config)
        } catch (e) {
            // 대기 요청 실패 처리
            queue.forEach(({ reject }) => reject(e))
            queue = []
            // 여기서 로그아웃 처리/리다이렉트 하면 됨
            return Promise.reject(e)
        } finally {
            isRefreshing = false
        }
    }
)

export default api;