<template>
  <v-app-bar app color="primary" dark>
    <v-toolbar-title @click="$router.push('/')"
                     style="cursor: pointer">
      👛 편한 가계부
    </v-toolbar-title>

    <v-spacer />

    <!-- 로그인 상태에 따라 다른 버튼 노출 -->
<!--    <template v-if="auth.user.userNo">-->
<!--      <v-btn text class="mr-4" disabled>-->
<!--        환영합니다, {{ auth.user.name || '회원' }}님-->
<!--      </v-btn>-->
<!--      <v-btn text @click="onLogout">로그아웃</v-btn>-->
<!--    </template>-->
<!--    <template v-else>-->
<!--      <v-btn text to="/login">로그인</v-btn>-->
<!--      <v-btn text to="/signup">회원가입</v-btn>-->
<!--    </template>-->
  </v-app-bar>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

// 새로고침해도 user를 복원하고 싶으면
onMounted(() => {
  if (!auth.user) {
    auth.fetchUser?.()
  }
})

function onLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.v-toolbar-title { font-weight: bold; }
</style>
