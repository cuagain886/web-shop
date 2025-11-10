<template>
  <div class="admin-login-page">
    <div class="login-container">
      <div class="login-card">
        <!-- Logo区域 -->
        <div class="login-header">
          <div class="logo">
            <span class="logo-icon">🏪</span>
            <h2>商家管理后台</h2>
          </div>
          <p class="subtitle">欢迎登录商家管理系统</p>
        </div>

        <!-- 登录表单 -->
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入商家账号"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              class="login-button"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 返回用户端 -->
        <div class="back-to-user">
          <el-button text @click="goToUserSite">
            ← 返回用户端
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import { login } from '@/api/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入商家账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

/**
 * 登录
 */
const handleLogin = async () => {
  try {
    // 表单验证
    await loginFormRef.value.validate()
    
    loading.value = true
    
    // 调试：打印请求数据
    console.log('🔍 登录请求数据:', {
      username: loginForm.username,
      password: loginForm.password
    })
    
    // 调用登录API（商家和用户使用同一个接口）
    const { token, userInfo } = await login({
      username: loginForm.username,
      password: loginForm.password
    })
    
    console.log('✅ 登录响应:', { token, userInfo })
    
    // 检查是否为管理员账号（role可能是字符串或数字）
    const role = String(userInfo.role).toLowerCase()
    if (role !== 'admin' && role !== 'merchant' && role !== '1') {
      ElMessage.error('该账号无管理员权限')
      return
    }
    
    // 存储token和用户信息
    userStore.setToken(token)
    userStore.setUserInfo(userInfo)
    
    ElMessage.success('登录成功')
    
    // 跳转到之前访问的页面或管理端首页
    const redirect = route.query.redirect
    if (redirect && redirect.startsWith('/admin') && redirect !== '/admin/login') {
      router.push(redirect)
    } else {
      router.push('/admin/dashboard')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

/**
 * 返回用户端
 */
const goToUserSite = () => {
  router.push('/')
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 450px;
}

.login-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.logo-icon {
  font-size: 48px;
}

.logo h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.subtitle {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.login-form {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

.login-tips {
  margin-bottom: 20px;
}

.login-tips :deep(.el-alert__content) {
  font-size: 13px;
  line-height: 1.8;
}

.login-tips p {
  margin: 5px 0;
}

.back-to-user {
  text-align: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}
</style>


