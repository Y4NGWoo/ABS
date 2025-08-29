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

app.use(pinia);
app.use(router);
app.use(vuetify);
app.mount('#app');
