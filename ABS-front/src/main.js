import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
import { createVuetify } from 'vuetify';
import 'vuetify/styles';
import '@mdi/font/css/materialdesignicons.css';
import api from '@/plugins/axios';

const pinia = createPinia()
const vuetify = createVuetify({
  // 테마 설정 등 추가 커스터마이징 가능
  theme: {
    defaultTheme: 'light',
  },
})
const app = createApp(App);
// 토큰이 localStorage에 있으면 헤더에 세팅
const token = localStorage.getItem('token')
if (token) {
  api.defaults.headers.common.Authorization = `Bearer ${token}`
}

app.use(pinia);
app.use(router);
app.use(vuetify);
app.mount('#app');
