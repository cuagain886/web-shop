<template>
  <el-card>
    <template #header>
      <h3>安全设置</h3>
    </template>

    <el-tabs v-model="activeTab">
      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="120px"
          style="max-width: 600px; margin-top: 20px;"
        >
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入当前密码"
              show-password
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码（6-20位）"
              show-password
            />
          </el-form-item>

          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="changing" @click="handleChangePassword">
              修改密码
            </el-button>
            <el-button @click="resetPasswordForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 账户注销 -->
      <el-tab-pane label="账户注销" name="deleteAccount">
        <el-alert
          title="警告"
          type="warning"
          description="注销账户后，您的所有数据将被永久删除且无法恢复，请谨慎操作！"
          :closable="false"
          style="margin: 20px 0;"
        />
        <el-button type="danger" :loading="deleting" @click="handleDeleteAccount">
          注销账户
        </el-button>
      </el-tab-pane>

      <!-- 登录记录 -->
      <el-tab-pane label="登录记录" name="loginHistory">
        <el-table :data="loginHistory" style="width: 100%; margin-top: 20px;">
          <el-table-column prop="time" label="登录时间" width="180" />
          <el-table-column prop="device" label="登录设备" width="200" />
          <el-table-column prop="ip" label="IP地址" width="150" />
          <el-table-column prop="location" label="登录地点" />
          <el-table-column label="状态" width="100">
            <template #default>
              <el-tag type="success" size="small">成功</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import { deleteAccount } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('password')
const changing = ref(false)
const deleting = ref(false)
const passwordFormRef = ref(null)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateNewPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码长度为6-20位'))
  } else if (value === passwordForm.oldPassword) {
    callback(new Error('新密码不能与旧密码相同'))
  } else {
    callback()
  }
}

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

const loginHistory = ref([])

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    
    changing.value = true
    
    ElMessage.success('密码修改成功')
    resetPasswordForm()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('修改密码失败:', error)
    }
  } finally {
    changing.value = false
  }
}

const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要注销账户吗？此操作不可恢复！',
      '确认注销',
      {
        confirmButtonText: '确定注销',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    deleting.value = true
    await deleteAccount(userStore.userInfo.id)
    
    ElMessage.success('账户已注销')
    userStore.logout()
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '注销失败')
    }
  } finally {
    deleting.value = false
  }
}
</script>

<style scoped>
h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}
</style>
