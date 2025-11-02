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

    <div v-if="favoriteList.length > 0" class="favorite-grid">
      <div v-for="item in favoriteList" :key="item.id" class="favorite-item">
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
          <div class="product-actions">
            <el-button size="small" type="primary" @click="goToProduct(item.productId)">
              查看详情
            </el-button>
            <el-button size="small" text type="danger" @click="handleRemove(item.id)">
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

const router = useRouter()
const favoriteList = ref([])

// Mock数据
const mockFavorites = [
  {
    id: 1,
    productId: 1,
    productName: 'iPhone 15 Pro Max 256GB 深空黑色',
    price: 9999,
    image: 'https://via.placeholder.com/200',
    createdAt: '2024-03-15'
  }
]

const loadFavorites = () => {
  const saved = localStorage.getItem('user_favorites')
  favoriteList.value = saved ? JSON.parse(saved) : mockFavorites
}

const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      type: 'warning'
    })
    
    favoriteList.value = favoriteList.value.filter(item => item.id !== id)
    localStorage.setItem('user_favorites', JSON.stringify(favoriteList.value))
    ElMessage.success('已取消收藏')
  } catch (error) {
    // 取消操作
  }
}

const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有收藏吗？', '提示', {
      type: 'warning'
    })
    
    favoriteList.value = []
    localStorage.setItem('user_favorites', JSON.stringify([]))
    ElMessage.success('已清空收藏')
  } catch (error) {
    // 取消操作
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

