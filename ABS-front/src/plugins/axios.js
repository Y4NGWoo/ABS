// ABS-FRONT/src/plugins/axios.js
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
  xsrfCookieName: "XSRF-TOKEN",   // Spring이 뿌려주는 쿠키 이름
  xsrfHeaderName: "X-XSRF-TOKEN", // 요청에 자동 첨부되는 헤더 이름
});

export default api;
