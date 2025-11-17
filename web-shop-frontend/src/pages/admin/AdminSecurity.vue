<template>
  <div class="admin-security">
    <el-card>
      <template #header>
        <h3>安全设置</h3>
      </template>

      <div class="security-content">
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="120px"
          style="max-width: 600px;"
        >
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入当前密码"
              show-password
              autocomplete="off"
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码（6-20位）"
              show-password
              autocomplete="off"
            />
          </el-form-item>

          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
              autocomplete="off"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="changing" @click="handleChangePassword">
              修改密码
            </el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button @click="goBack">返回</el-button>
          </el-form-item>
        </el-form>

        <!-- 密码提示 -->
        <el-alert
          title="密码规则"
          type="info"
          :closable="false"
          style="max-width: 600px; margin-top: 20px;"
        >
          <ul style="margin: 0; padding-left: 20px;">
            <li>密码长度为6-20位</li>
            <li>建议使用字母、数字和符号的组合</li>
            <li>不要使用过于简单的密码</li>
            <li>定期修改密码以保证账户安全</li>
          </ul>
        </el-alert>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const changing = ref(false)
const passwordFormRef = ref(null)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 验证新密码
const validateNewPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码长度为6-20位'))
  } else if (value === passwordForm.oldPassword) {
    callback(new Error('新密码不能与旧密码相同'))
  } else {
    if (passwordForm.confirmPassword !== '') {
      passwordFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

/**
 * 修改密码
 */
const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    
    changing.value = true
    
    ElMessage.success('密码修改成功，请重新登录')
    
    // 清空表单
    handleReset()
    
    // 延迟后退出登录
    setTimeout(() => {
      userStore.logout()
      router.push('/admin/login')
    }, 1500)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('修改密码失败:', error)
      ElMessage.error('修改密码失败')
    }
  } finally {
    changing.value = false
  }
}

/**
 * 重置表单
 */
const handleReset = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

/**
 * 返回
 */
const goBack = () => {
  router.back()
}
</script>

<style scoped>
.admin-security {
  padding: 0;
}

h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.security-content {
  padding: 20px 0;
}
</style>

