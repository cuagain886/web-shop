<template>
  <div class="admin-profile">
    <el-row :gutter="20">
      <!-- 个人信息 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <h3>个人信息</h3>
          </template>

          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="120px"
          >
            <el-form-item label="商家名称" prop="nickname">
              <el-input
                v-model="profileForm.nickname"
                placeholder="请输入商家名称"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="联系电话" prop="phone">
              <el-input
                v-model="profileForm.phone"
                placeholder="请输入联系电话"
                maxlength="11"
              />
            </el-form-item>

            <el-form-item label="店铺简介">
              <el-input
                v-model="profileForm.description"
                type="textarea"
                :rows="4"
                placeholder="请输入店铺简介"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 账户信息 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <h3>账户信息</h3>
          </template>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">
              {{ userStore.userInfo?.username }}
            </el-descriptions-item>
            <el-descriptions-item label="角色">
              商家管理员
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">
              {{ new Date().toLocaleDateString() }}
            </el-descriptions-item>
          </el-descriptions>

          <el-button
            type="primary"
            plain
            style="margin-top: 20px; width: 100%;"
            @click="goToSecurity"
          >
            修改密码
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const saving = ref(false)
const profileFormRef = ref(null)

const profileForm = reactive({
  nickname: '',
  phone: '',
  description: ''
})

const profileRules = {
  nickname: [
    { required: true, message: '请输入商家名称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

/**
 * 初始化表单
 */
const initForm = () => {
  if (userStore.userInfo) {
    profileForm.nickname = userStore.userInfo.nickname || ''
    profileForm.phone = userStore.userInfo.phone || ''
    profileForm.description = userStore.userInfo.description || ''
  }
}

/**
 * 保存修改
 */
const handleSave = async () => {
  try {
    await profileFormRef.value.validate()
    
    saving.value = true
    
    // 模拟保存延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 更新用户信息
    const updatedInfo = {
      ...userStore.userInfo,
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      description: profileForm.description
    }
    
    userStore.setUserInfo(updatedInfo)
    
    ElMessage.success('保存成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('保存失败:', error)
      ElMessage.error('保存失败')
    }
  } finally {
    saving.value = false
  }
}

/**
 * 跳转到安全设置
 */
const goToSecurity = () => {
  router.push('/admin/security')
}

onMounted(() => {
  initForm()
})
</script>

<style scoped>
.admin-profile {
  padding: 0;
}

h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}
</style>


