<template>
  <v-container class="fill-height pa-0" fluid>
    <v-row align="center" justify="center" class="ma-0">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card elevation="2" class="pa-6">
          <h2 class="text-h5 font-weight-medium mb-4 text-center">
            ✨ 무료로 시작하는 가계부
          </h2>
          <v-form ref="formRef" @submit.prevent="onSubmit">
            <v-text-field
              v-model="form.email"
              label="E-mail"
              type="email"
              :error-messages="errors.email"
              required
            />
            <v-text-field
              v-model="form.password"
              label="비밀번호"
              type="password"
              :error-messages="errors.password"
              hint="최소 8자"
              persistent-hint
              required
            />
            <v-text-field
              v-model="form.confirm"
              label="비밀번호 확인"
              type="password"
              :error-messages="errors.confirm"
              required
            />
            <v-text-field
              v-model="form.nickname"
              label="닉네임"
              :error-messages="errors.nickname"
              required
            />

            <v-btn
              :loading="loading"
              color="primary"
              block
              class="mt-6"
              type="submit"
            >
              가입할래요!
            </v-btn>
            <v-alert
              v-if="serverError"
              type="error"
              dense
              text
              class="mt-4"
            >
              {{ serverError }}
            </v-alert>
          </v-form>
          <p class="mt-4 text-center text-caption">
            이미 가입하셨나요?
            <RouterLink to="/login" class="font-weight-medium">
              로그인하기
            </RouterLink>
          </p>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { reactive, ref } from 'vue'
import api from '@/plugins/axios'

const formRef = ref(null)
const form = reactive({ email: '', password: '', confirm: '', nickname: '' })
const errors = reactive({ email: '', password: '', confirm: '', nickname: '' })
const serverError = ref('')
const loading = ref(false)

function validate() {
  errors.email = errors.password = errors.confirm = errors.nickname = ''
  let ok = true

  if (!/.+@.+\..+/.test(form.email)) {
    errors.email = '유효한 이메일을 입력해 주세요'
    ok = false
  }
  if (form.password.length < 8) {
    errors.password = '비밀번호는 8자 이상이어야 해요'
    ok = false
  }
  if (form.confirm !== form.password) {
    errors.confirm = '비밀번호가 일치하지 않아요'
    ok = false
  }
  if (!form.nickname) {
    errors.nickname = '닉네임을 입력해 주세요'
    ok = false
  }
  return ok
}

async function onSubmit() {
  serverError.value = ''
  if (!validate()) return
  loading.value = true
  try {
    const res = await api.post('/api/auth/signup', {
      email: form.email,
      password: form.password,
      nickname: form.nickname
    })
    // TODO: 성공 메시지, 로그인 페이지 이동 등
    console.log('signup ok', res.data)
  } catch (e) {
    serverError.value = e.response?.data?.message || '서버 오류가 났어요'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.fill-height { height: 100vh; }
</style>
