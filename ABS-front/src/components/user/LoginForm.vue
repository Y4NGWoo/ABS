<template>
  <v-container class="fill-height pa-0" fluid>
    <v-row align="center" justify="center" class="ma-0">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card elevation="2" class="pa-6">
          <h2 class="text-h5 font-weight-medium mb-4 text-center">
            🔑 로그인
          </h2>
          <v-form @submit.prevent="onSubmit">
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
              required
            />

            <v-btn
              :loading="loading"
              color="primary"
              block
              class="mt-6"
              type="submit"
            >
              로그인
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
            아직 계정이 없나요?
            <RouterLink to="/signup" class="font-weight-medium">
              회원가입
            </RouterLink>
          </p>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const form = reactive({ email: '', password: '' })
const errors = reactive({ email: '', password: '' })
const serverError = ref('')
const loading = ref(false)

function validate() {
  errors.email = errors.password = ''
  let ok = true
  if (!/.+@.+\..+/.test(form.email)) {
    errors.email = '유효한 이메일을 입력해 주세요'
    ok = false
  }
  if (!form.password) {
    errors.password = '비밀번호를 입력해 주세요'
    ok = false
  }
  return ok
}

async function onSubmit() {
  serverError.value = ''
  if (!validate()) return
  loading.value = true
  try {
    await auth.login({ email: form.email, password: form.password })
    alert("로그인에 성공하였습니다!");
    router.push('/')  // 로그인 성공 후 홈으로
  } catch (e) {
    // err.response.data 에서 백엔드에서 보낸 메시지(문자열)를 꺼내서 띄워줌
    const errMsg = e.response?.data
    serverError.value = errMsg;
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.fill-height { height: 100vh; }
</style>
