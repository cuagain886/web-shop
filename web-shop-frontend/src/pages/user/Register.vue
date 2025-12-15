<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-box">
        <h1 class="register-title">用户注册</h1>
        
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          @submit.prevent="handleRegister"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名（3-20个字符）"
              prefix-icon="User"
              size="large"
              clearable
            />
          </el-form-item>

          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              prefix-icon="Phone"
              size="large"
              clearable
              maxlength="11"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="请输入邮箱"
              prefix-icon="Message"
              size="large"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码（6-20个字符）"
              prefix-icon="Lock"
              size="large"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
              clearable
              @keyup.enter="handleRegister"
            />
          </el-form-item>

          <el-form-item prop="agreement">
            <el-checkbox v-model="registerForm.agreement">
              我已阅读并同意
              <el-link type="primary" :underline="false" disabled>
                《用户协议》
              </el-link>
              和
              <el-link type="primary" :underline="false" disabled>
                《隐私政策》
              </el-link>
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
              type="danger"
              size="large"
              class="register-btn"
              :loading="loading"
              @click="handleRegister"
            >
              注册
            </el-button>
          </el-form-item>

          <el-form-item>
            <div class="login-tip">
              已有账号？
              <el-link type="primary" @click="goToLogin">
                立即登录
              </el-link>
            </div>
          </el-form-item>
        </el-form>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

console.log('注册页面加载')

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 表单引用
const registerFormRef = ref(null)

// 注册表单
const registerForm = reactive({
  username: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

// 加载状态
const loading = ref(false)

// 验证手机号
const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 验证协议
const validateAgreement = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请阅读并同意用户协议'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreement: [
    { required: true, validator: validateAgreement, trigger: 'change' }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    // 验证表单
    await registerFormRef.value.validate()

    loading.value = true
    console.log('开始注册...', registerForm.username)

    // 调用注册接口
    await userStore.register({
      username: registerForm.username,
      phone: registerForm.phone,
      email: registerForm.email,
      password: registerForm.password
    })

    ElMessage.success('注册成功！即将跳转到登录页...')
    console.log('注册成功')

    // 延迟跳转到登录页
    setTimeout(() => {
      goToLogin()
    }, 1500)

  } catch (error) {
    console.error('注册失败:', error)
    console.error('错误详情:', JSON.stringify(error, null, 2))
    
    // 提取错误信息
    let errorMsg = '注册失败，请稍后重试'
    if (error.response?.data?.message) {
      errorMsg = error.response.data.message
    } else if (error.message) {
      errorMsg = error.message
    }
    
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

// 跳转到登录页
const goToLogin = () => {
  const redirect = route.query.redirect
  router.push({
    path: '/login',
    query: redirect ? { redirect } : {}
  })
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-container {
  width: 100%;
  max-width: 420px;
}

.register-box {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.register-title {
  font-size: 28px;
  color: #333;
  text-align: center;
  margin: 0 0 30px;
}

.register-form {
  margin-top: 20px;
}

.register-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
}

.login-tip {
  text-align: center;
  font-size: 14px;
  color: #666;
}

/* Mock提示 */
.mock-tip {
  margin-top: 20px;
}

.mock-tip p {
  margin: 0;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

/* 响应式 */
@media (max-width: 768px) {
  .register-box {
    padding: 30px 20px;
  }

  .register-title {
    font-size: 24px;
  }
}
</style>

