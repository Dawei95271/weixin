<template>
  <div class="login-page">
    <div class="login-card">
      <p class="brand">HOTEL CATERING ADMIN</p>
      <h1>酒店二楼餐饮管理后台</h1>
      <p class="desc">先用默认账号登录，后面我们继续补订单处理、预约处理和经营看板。</p>
      <el-form :model="form">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" show-password placeholder="密码" />
        </el-form-item>
        <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">登录后台</el-button>
      </el-form>
      <p class="tips">默认账号：admin / admin123</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../services/api'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: 'admin123'
})

async function handleLogin() {
  try {
    loading.value = true
    const data = await login(form)
    localStorage.setItem('admin_token', data.token)
    localStorage.setItem('admin_real_name', data.realName)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}

.login-card {
  width: 440px;
  padding: 34px;
  border-radius: 24px;
  background: rgba(255, 249, 240, 0.92);
  box-shadow: 0 30px 80px rgba(84, 52, 26, 0.12);
}

.brand {
  margin: 0;
  color: #9a6b2f;
  font-size: 12px;
  letter-spacing: 0.24em;
}

h1 {
  margin: 10px 0 12px;
  font-size: 32px;
}

.desc,
.tips {
  color: #6b7280;
  line-height: 1.7;
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #c58a3a, #8d5524);
  border: 0;
}
</style>
