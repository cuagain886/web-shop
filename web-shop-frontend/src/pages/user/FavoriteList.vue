<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <h3>我的收藏（{{ favoriteList.length }}）</h3>
        <el-button v-if="favoriteList.length > 0" text type="danger" @click="handleClearAll">
          清空收藏
        </el-button>
      </div>
    </template>

    <el-skeleton v-if="loading" :rows="5" animated />

    <div v-else-if="favoriteList.length > 0" class="favorite-grid">
      <div v-for="item in favoriteList" :key="item.id" class="favorite-item">
        <el-image
          :src="item.product?.coverImage || item.productImage"
          fit="cover"
          class="product-image"
          @click="goToProduct(item.productId)"
        />
        <div class="product-info">
          <div class="product-name" @click="goToProduct(item.productId)">
            {{ item.product?.name || item.productName }}
          </div>
          <div class="product-price">¥{{ item.product?.price || item.productPrice }}</div>
          <div class="product-actions">
            <el-button size="small" type="primary" @click="goToProduct(item.productId)">
              查看详情
            </el-button>
            <el-button size="small" text type="danger" @click="handleRemove(item.productId)">
              取消收藏
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无收藏商品" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/userStore'
import { getFavoriteList as fetchFavoriteList, removeFavorite, clearFavorites } from '@/api/favorite'

const router = useRouter()
const userStore = useUserStore()
const favoriteList = ref([])
const loading = ref(false)

// 加载收藏列表
const loadFavoriteList = async () => {
  try {
    const data = await fetchFavoriteList(userStore.userInfo?.id)
    favoriteList.value = data || []
    console.log('✅ 收藏列表加载成功:', favoriteList.value)
  } catch (error) {
    console.error('❌ 加载收藏列表失败:', error)
    ElMessage.error('加载收藏列表失败')
  }
}

const loadFavorites = async () => {
  loading.value = true
  try {
    const userId = userStore.userInfo?.id
    if (!userId) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }
    
    await loadFavoriteList()
  } catch (error) {
    console.error('❌ 加载收藏列表失败:', error)
    ElMessage.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

const handleRemove = async (productId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      type: 'warning'
    })
    
    // 调用API取消收藏
    await removeFavorite(productId)
    
    // 从列表中移除
    favoriteList.value = favoriteList.value.filter(item => item.productId !== productId)
    ElMessage.success('已取消收藏')
    console.log('✅ 取消收藏成功')
  } catch (error) {
    if (error.message !== 'Request canceled.') {
      console.error('❌ 取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有收藏吗？', '提示', {
      type: 'warning'
    })
    
    const userId = userStore.userInfo?.id
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }
    
    // 调用API清空收藏
    await clearFavorites(userId)
    
    favoriteList.value = []
    ElMessage.success('已清空收藏')
    console.log('✅ 清空收藏成功')
  } catch (error) {
    if (error.message !== 'Request canceled.') {
      console.error('❌ 清空收藏失败:', error)
      ElMessage.error('清空收藏失败')
    }
  }
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

onMounted(() => {
  loadFavorites()
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

.favorite-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.favorite-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.favorite-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.product-image {
  width: 100%;
  height: 200px;
  cursor: pointer;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 10px;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-name:hover {
  color: #409eff;
}

.product-price {
  font-size: 20px;
  font-weight: 600;
  color: #f56c6c;
  margin-bottom: 10px;
}

.product-actions {
  display: flex;
  gap: 10px;
}
</style>
