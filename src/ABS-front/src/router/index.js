import { createRouter, createWebHistory } from 'vue-router'
import SignupForm from '@/components/user/SignupForm.vue'
import HomeView   from '@/components/HomeView.vue'
import LoginForm   from '@/components/user/LoginForm.vue'

const routes = [
  // 필요하면 추가 라우트 더
  { path: '/',          name: 'Home',   component: HomeView },
  { path: '/signup',    name: 'Signup', component: SignupForm },
  { path: '/login',   name: 'Login',  component: LoginForm },
  // 혹시 다른 경로가 들어오면 홈으로 리다이렉트
  { path: '/:catchAll(.*)*', redirect: '/'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
