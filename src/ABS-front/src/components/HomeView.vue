<template>
  <v-container class="fill-height pa-0" fluid>
    <v-row align="center" justify="center" class="ma-0">
      <v-col cols="12" md="8" lg="6">
        <v-card elevation="2" class="pa-8 text-center">
          <v-icon size="64" color="primary">mdi-finance</v-icon>
          <h1 class="display-1 font-weight-bold my-4">👛 편한 가계부</h1>
          <p class="subtitle-1 mb-6">
            지출·수입을 손쉽게 기록하고<br/>
            한눈에 보는 통계로 똑똑하게 관리하세요!
          </p>
          <v-btn color="primary" class="mr-4" large @click="$router.push('/signup')">
            지금 가입하기
          </v-btn>
          <v-btn outlined large @click="$router.push('/login')">
            로그인하기
          </v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
// None
</script>

<style scoped>
.fill-height { height: 100vh; }
</style>
