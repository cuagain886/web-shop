<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-box">
        <h1 class="login-title">用户登录</h1>
        
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
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <div class="form-footer">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <el-link type="primary" :underline="false" disabled>
                忘记密码？
              </el-link>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="danger"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>

          <el-form-item>
            <div class="register-tip">
              还没有账号？
              <el-link type="primary" @click="goToRegister">
                立即注册
              </el-link>
            </div>
          </el-form-item>
        </el-form>

        <!-- 商家登录入口 -->
        <div class="merchant-login-link">
          <el-button text type="primary" @click="goToMerchantLogin">
            <el-icon><Shop /></el-icon>
            商家登录 →
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
import { Shop } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'

console.log('登录页面加载')

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 表单引用
const loginFormRef = ref(null)

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 记住我
const rememberMe = ref(false)

// 加载状态
const loading = ref(false)

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // 验证表单
    await loginFormRef.value.validate()

    loading.value = true
    console.log('开始登录...', loginForm.username)

    // 调用登录接口
    await userStore.login({
      username: loginForm.username,
      password: loginForm.password
    })

    ElMessage.success('登录成功！')
    console.log('登录成功')

    // 获取重定向地址
    const redirect = route.query.redirect || '/'
    
    // 延迟跳转，让用户看到成功提示
    setTimeout(() => {
      router.push(redirect)
    }, 500)

  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

// 跳转到注册页
const goToRegister = () => {
  const redirect = route.query.redirect
  router.push({
    path: '/register',
    query: redirect ? { redirect } : {}
  })
}

// 跳转到商家登录
const goToMerchantLogin = () => {
  router.push('/admin/login')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-box {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-title {
  font-size: 28px;
  color: #333;
  text-align: center;
  margin: 0 0 30px;
}

.login-form {
  margin-top: 20px;
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
}

.register-tip {
  text-align: center;
  font-size: 14px;
  color: #666;
}

/* Mock提示 */
.mock-tip {
  margin-top: 20px;
}

.mock-accounts {
  font-size: 13px;
  line-height: 1.8;
}

.mock-accounts p {
  margin: 0;
  color: #666;
}

/* 商家登录链接 */
.merchant-login-link {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
  text-align: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .login-box {
    padding: 30px 20px;
  }

  .login-title {
    font-size: 24px;
  }
}
</style>

