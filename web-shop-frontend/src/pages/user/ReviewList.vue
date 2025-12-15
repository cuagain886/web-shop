<template>
  <el-card>
    <template #header>
      <h3>我的评价</h3>
    </template>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="待评价" name="pending">
        <div v-if="pendingReviews.length > 0" class="review-list">
          <div v-for="item in pendingReviews" :key="item.id" class="review-item">
            <el-image
              :src="item.image"
              fit="cover"
              style="width: 80px; height: 80px; border-radius: 4px;"
            />
            <div class="item-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="order-info">订单号：{{ item.orderNo }}</div>
            </div>
            <el-button type="primary" @click="handleReview(item)">
              立即评价
            </el-button>
          </div>
        </div>
        <el-empty v-else description="暂无待评价商品" />
      </el-tab-pane>

      <el-tab-pane label="已评价" name="reviewed">
        <div v-if="reviewedList.length > 0" class="review-list">
          <div v-for="item in reviewedList" :key="item.id" class="review-item reviewed">
            <el-image
              :src="item.image"
              fit="cover"
              style="width: 80px; height: 80px; border-radius: 4px;"
            />
            <div class="item-info">
              <div class="product-name">{{ item.productName }}</div>
              <el-rate v-model="item.rating" disabled />
              <div class="review-content">{{ item.content }}</div>
              <div class="review-time">{{ item.reviewTime }}</div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无评价记录" />
      </el-tab-pane>
    </el-tabs>

    <!-- 评价对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="商品评价" width="600px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="商品">
          <div class="review-product">
            <el-image
              :src="currentProduct?.image"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
            />
            <span>{{ currentProduct?.productName }}</span>
          </div>
        </el-form-item>

        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" show-text />
        </el-form-item>

        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="5"
            placeholder="请分享您的使用体验..."
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReview" :loading="submitting">提交评价</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'
import { getPendingReviews, getReviewedItems, addProductReview } from '@/api/product'

const activeTab = ref('pending')
const pendingReviews = ref([])
const reviewedList = ref([])
const reviewDialogVisible = ref(false)
const currentProduct = ref(null)
const submitting = ref(false)

const userStore = useUserStore()

const reviewForm = reactive({
  rating: 5,
  content: ''
})

const handleReview = (item) => {
  currentProduct.value = item
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewDialogVisible.value = true
}

const handleSubmitReview = async () => {
  if (!reviewForm.content || reviewForm.content.trim() === '') {
    ElMessage.warning('请输入评价内容')
    return
  }

  submitting.value = true
  try {
    await addProductReview({
      userId: userStore.userInfo.id,
      productId: currentProduct.value.productId,
      orderItemId: currentProduct.value.id,
      rating: reviewForm.rating,
      content: reviewForm.content.trim()
    })

    ElMessage.success('评价提交成功')
    reviewDialogVisible.value = false

    // 刷新列表
    await loadPendingReviews()
    await loadReviewedList()
  } catch (error) {
    console.error('提交评价失败:', error)
    ElMessage.error(error.message || '提交评价失败')
  } finally {
    submitting.value = false
  }
}

/**
 * 加载待评价商品
 */
const loadPendingReviews = async () => {
  try {
    const userId = userStore.userInfo?.id
    if (!userId) {
      return
    }

    const items = await getPendingReviews(userId)
    pendingReviews.value = items.map(item => ({
      id: item.id,
      productId: item.productId,
      productName: item.productName,
      image: item.productImage,
      orderNo: item.orderNo || `订单 #${item.orderId}`
    }))
  } catch (error) {
    console.error('加载待评价商品失败:', error)
  }
}

/**
 * 加载已评价商品
 */
const loadReviewedList = async () => {
  try {
    const userId = userStore.userInfo?.id
    if (!userId) {
      return
    }

    const reviews = await getReviewedItems(userId)
    reviewedList.value = reviews.map(review => ({
      id: review.id,
      productId: review.productId,
      productName: review.productName || '商品',
      image: review.productImage || '',
      rating: review.rating,
      content: review.content,
      reviewTime: formatTime(review.createdTime)
    }))
  } catch (error) {
    console.error('加载已评价商品失败:', error)
  }
}

/**
 * 格式化时间
 */
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadPendingReviews()
  loadReviewedList()
})
</script>

<style scoped>
h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.review-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.item-info {
  flex: 1;
}

.product-name {
  font-size: 16px;
  color: #303133;
  margin-bottom: 8px;
}

.order-info {
  font-size: 14px;
  color: #909399;
}

.review-content {
  margin: 10px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.review-time {
  font-size: 12px;
  color: #909399;
}

.review-product {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>

