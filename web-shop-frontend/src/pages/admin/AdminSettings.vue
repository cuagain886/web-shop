<template>
  <div class="admin-settings">
    <el-row :gutter="20">
      <!-- 系统设置 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <h3>系统设置</h3>
          </template>

          <el-form
            ref="settingsFormRef"
            :model="settingsForm"
            label-width="120px"
          >
            <el-divider content-position="left">基本设置</el-divider>
            
            <el-form-item label="网站名称">
              <el-input
                v-model="settingsForm.siteName"
                placeholder="请输入网站名称"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="网站描述">
              <el-input
                v-model="settingsForm.siteDescription"
                type="textarea"
                :rows="3"
                placeholder="请输入网站描述"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="联系电话">
              <el-input
                v-model="settingsForm.contactPhone"
                placeholder="请输入联系电话"
              />
            </el-form-item>

            <el-form-item label="联系邮箱">
              <el-input
                v-model="settingsForm.contactEmail"
                placeholder="请输入联系邮箱"
              />
            </el-form-item>

            <el-divider content-position="left">订单设置</el-divider>

            <el-form-item label="自动取消时间">
              <el-input-number
                v-model="settingsForm.orderCancelTime"
                :min="1"
                :max="72"
              />
              <span style="margin-left: 10px; color: #909399;">小时（未支付订单自动取消）</span>
            </el-form-item>

            <el-form-item label="自动确认收货">
              <el-input-number
                v-model="settingsForm.orderConfirmTime"
                :min="1"
                :max="30"
              />
              <span style="margin-left: 10px; color: #909399;">天（发货后自动确认收货）</span>
            </el-form-item>

            <el-divider content-position="left">商品设置</el-divider>

            <el-form-item label="库存预警">
              <el-input-number
                v-model="settingsForm.stockWarning"
                :min="0"
                :max="100"
              />
              <span style="margin-left: 10px; color: #909399;">件（低于此数量提醒）</span>
            </el-form-item>

            <el-form-item label="默认运费">
              <el-input-number
                v-model="settingsForm.defaultShipping"
                :min="0"
                :precision="2"
                :step="1"
              />
              <span style="margin-left: 10px; color: #909399;">元</span>
            </el-form-item>

            <el-divider content-position="left">功能开关</el-divider>

            <el-form-item label="用户注册">
              <el-switch
                v-model="settingsForm.allowRegister"
                active-text="开启"
                inactive-text="关闭"
              />
            </el-form-item>

            <el-form-item label="商品评价">
              <el-switch
                v-model="settingsForm.allowComment"
                active-text="开启"
                inactive-text="关闭"
              />
            </el-form-item>

            <el-form-item label="维护模式">
              <el-switch
                v-model="settingsForm.maintenanceMode"
                active-text="开启"
                inactive-text="关闭"
              />
              <div style="color: #f56c6c; font-size: 12px; margin-top: 5px;">
                开启后，普通用户将无法访问网站
              </div>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">
                保存设置
              </el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 系统信息 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <h3>系统信息</h3>
          </template>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统版本">
              v1.0.0
            </el-descriptions-item>
            <el-descriptions-item label="开发框架">
              Vue 3 + Vite
            </el-descriptions-item>
            <el-descriptions-item label="UI框架">
              Element Plus
            </el-descriptions-item>
            <el-descriptions-item label="运行环境">
              Development
            </el-descriptions-item>
            <el-descriptions-item label="最后更新">
              {{ new Date().toLocaleDateString() }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card style="margin-top: 20px;">
          <template #header>
            <h3>快捷操作</h3>
          </template>

          <el-space direction="vertical" style="width: 100%;" :size="10">
            <el-button type="primary" plain style="width: 100%;" @click="handleClearCache">
              <el-icon><Delete /></el-icon>
              清除缓存
            </el-button>
            
            <el-button type="warning" plain style="width: 100%;" @click="handleExportData">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
            
            <el-button type="danger" plain style="width: 100%;" @click="handleResetSystem">
              <el-icon><RefreshRight /></el-icon>
              重置系统
            </el-button>
          </el-space>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Download, RefreshRight } from '@element-plus/icons-vue'
import { getSettings, updateSettings } from '@/api/settings'

const saving = ref(false)
const loading = ref(false)
const settingsFormRef = ref(null)

const settingsForm = reactive({
  siteName: '购物平台',
  siteDescription: '一个基于Vue3的现代化购物平台',
  contactPhone: '400-888-8888',
  contactEmail: 'contact@example.com',
  orderCancelTime: 24,
  orderConfirmTime: 7,
  stockWarning: 10,
  defaultShipping: 10.00,
  allowRegister: true,
  allowComment: true,
  maintenanceMode: false
})

const defaultSettings = { ...settingsForm }

/**
 * 保存设置
 */
const handleSave = async () => {
  try {
    saving.value = true
    
    await updateSettings(settingsForm)
    
    ElMessage.success('设置已保存')
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error(error.message || '保存设置失败')
  } finally {
    saving.value = false
  }
}

/**
 * 重置
 */
const handleReset = () => {
  Object.assign(settingsForm, defaultSettings)
  ElMessage.info('已重置为默认设置')
}

/**
 * 清除缓存
 */
const handleClearCache = async () => {
  try {
    await ElMessageBox.confirm('确定要清除系统缓存吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('缓存已清除')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清除缓存失败:', error)
    }
  }
}

/**
 * 导出数据
 */
const handleExportData = () => {
  ElMessage.info('数据导出功能开发中...')
}

/**
 * 重置系统
 */
const handleResetSystem = async () => {
  try {
    await ElMessageBox.confirm(
      '此操作将重置所有系统数据，是否继续？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    ElMessage.warning('系统重置功能仅供演示，未实际执行')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置系统失败:', error)
    }
  }
}

/**
 * 加载设置
 */
const loadSettings = async () => {
  try {
    loading.value = true
    const settings = await getSettings()
    Object.assign(settingsForm, settings)
    Object.assign(defaultSettings, settings)
  } catch (error) {
    console.error('加载设置失败:', error)
    ElMessage.error('加载设置失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.admin-settings {
  padding: 0;
}

h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}
</style>

