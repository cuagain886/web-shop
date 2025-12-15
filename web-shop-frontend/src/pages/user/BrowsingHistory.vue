<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <h3>浏览历史（{{ historyList.length }}）</h3>
        <el-button v-if="historyList.length > 0" text type="danger" @click="handleClearAll">
          清空历史
        </el-button>
      </div>
    </template>

    <div v-if="historyList.length > 0" class="history-list">
      <div v-for="item in historyList" :key="item.id" class="history-item">
        <el-image
          :src="item.image"
          fit="cover"
          class="product-image"
          @click="goToProduct(item.productId)"
        />
        <div class="product-info">
          <div class="product-name" @click="goToProduct(item.productId)">
            {{ item.productName }}
          </div>
          <div class="product-price">¥{{ item.price }}</div>
        </div>
        <div class="browse-time">
          {{ formatTime(item.browsedAt) }}
        </div>
        <div class="item-actions">
          <el-button size="small" type="primary" @click="goToProduct(item.productId)">
            再次查看
          </el-button>
          <el-button size="small" text type="danger" @click="handleRemove(item.id)">
            删除
          </el-button>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无浏览历史" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserHistory, deleteHistory, clearHistory } from '@/api/history'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()
const historyList = ref([])

const loadHistory = async () => {
  const userId = userStore.userInfo?.id
  if (!userId) {
    console.warn('用户未登录')
    return
  }

  try {
    const data = await getUserHistory(userId, 50)
    // 转换后端数据格式
    historyList.value = data.map(item => ({
      id: item.id,
      productId: item.productId,
      productName: item.product?.name || '商品名称',
      price: item.product?.price || 0,
      image: item.product?.coverImage || '',
      browsedAt: item.createdAt
    }))
    console.log('浏览历史加载成功:', historyList.value.length)
  } catch (error) {
    console.error('加载浏览历史失败:', error)
  }
}

const handleRemove = async (id) => {
  const userId = userStore.userInfo?.id
  if (!userId) return

  try {
    await deleteHistory(id, userId)
    historyList.value = historyList.value.filter(item => item.id !== id)
    ElMessage.success('已删除')
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

const handleClearAll = async () => {
  const userId = userStore.userInfo?.id
  if (!userId) return

  try {
    await ElMessageBox.confirm('确定要清空所有浏览历史吗？', '提示', {
      type: 'warning'
    })
    
    await clearHistory(userId)
    historyList.value = []
    ElMessage.success('已清空浏览历史')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空失败:', error)
      ElMessage.error('清空失败')
    }
  }
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

const formatTime = (timeStr) => {
  return timeStr
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.history-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  cursor: pointer;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 16px;
  color: #303133;
  margin-bottom: 10px;
  cursor: pointer;
}

.product-name:hover {
  color: #409eff;
}

.product-price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

.browse-time {
  font-size: 14px;
  color: #909399;
  min-width: 150px;
  text-align: center;
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>

