<template>
  <v-app>
    <!-- <v-app-bar color="primary" dark>
      <v-toolbar-title>내 가계부</v-toolbar-title>
      <v-spacer />
      <RouterLink to="/signup">
        <v-btn text>회원가입</v-btn>
      </RouterLink>
    </v-app-bar> -->
    
    <NavBar />

    <v-main class="pa-4">
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import NavBar from '@/components/common/NavBar.vue'
// Vuetify를 감싸주는 최상위 layout
</script>

<style>
/* 필요하면 전역 스타일 추가 */
</style>
