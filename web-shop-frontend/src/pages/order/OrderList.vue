<template>
  <div class="order-list-page">
    <div class="page-container">
      <h2 class="page-title">我的订单</h2>

      <!-- 订单状态筛选 -->
      <div class="filter-tabs">
        <div
          v-for="tab in statusTabs"
          :key="tab.value"
          class="filter-tab"
          :class="{ active: currentStatus === tab.value }"
          @click="changeStatus(tab.value)"
        >
          {{ tab.label }}
          <span v-if="tab.count > 0" class="tab-badge">{{ tab.count }}</span>
        </div>
      </div>

      <!-- 订单列表 -->
      <div v-loading="loading" class="order-list">
        <template v-if="displayOrders.length > 0">
          <template v-for="order in displayOrders" :key="order?.id || Math.random()">
            <div
              v-if="order"
              class="order-card"
              @click="goToOrderDetail(order.id)"
            >
            <!-- 订单头部 -->
            <div class="order-header">
              <div class="order-info">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-time">{{ formatDate(order.createdAt) }}</span>
              </div>
              <div class="order-status" :class="`status-${getStatusClass(order.status)}`">
                {{ order.statusText }}
              </div>
            </div>

            <!-- 商品列表 -->
            <div class="order-goods">
              <div
                v-for="item in order.items"
                :key="item.id"
                class="goods-item"
              >
                <img :src="item.image || 'https://via.placeholder.com/80'" :alt="item.productName" class="goods-image" />
                <div class="goods-info">
                  <div class="goods-name">{{ item.productName }}</div>
                  <div class="goods-specs" v-if="item.specs">{{ item.specs }}</div>
                  <div class="goods-quantity">x{{ item.quantity }}</div>
                </div>
                <div class="goods-price">¥{{ (item.price || 0).toFixed(2) }}</div>
              </div>
            </div>

            <!-- 订单底部 -->
            <div class="order-footer">
              <div class="order-total">
                <span>订单总额：</span>
                <span class="total-amount">¥{{ (order.totalAmount || 0).toFixed(2) }}</span>
              </div>
              <div class="order-actions" @click.stop>
                <el-button
                  v-if="order.status === 0"
                  type="danger"
                  size="small"
                  @click="handlePay(order)"
                >
                  去支付
                </el-button>
                <el-button
                  v-if="order.status === 0"
                  size="small"
                  @click="handleCancel(order.id)"
                >
                  取消订单
                </el-button>
                <el-button
                  v-if="order.status === 2"
                  type="success"
                  size="small"
                  @click="handleConfirmReceipt(order.id)"
                >
                  确认收货
                </el-button>
                <el-button
                  size="small"
                  @click.stop="goToOrderDetail(order.id)"
                >
                  查看详情
                </el-button>
              </div>
            </div>
            </div>
          </template>
        </template>

        <el-empty v-else description="暂无订单" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useOrderStore } from '@/stores/orderStore'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const orderStore = useOrderStore()
const userStore = useUserStore()

// 数据状态
const loading = ref(false)
const currentStatus = ref('all')

// 状态映射：前端字符串 -> 后端数字
const statusMap = {
  'pending': 0,    // 待支付
  'paid': 1,       // 待发货
  'processing': 2, // 待收货
  'completed': 3,  // 已完成
  'cancelled': 4,  // 已取消
  'refunding': 5,  // 退款中
  'refunded': 6    // 已退款
}

// 状态标签
const statusTabs = ref([
  { label: '全部', value: 'all', count: 0 },
  { label: '待支付', value: 'pending', count: 0 },
  { label: '待发货', value: 'paid', count: 0 },
  { label: '待收货', value: 'processing', count: 0 },
  { label: '已完成', value: 'completed', count: 0 },
  { label: '已取消', value: 'cancelled', count: 0 }
])

// 显示的订单列表（根据状态筛选）
const displayOrders = computed(() => {
  if (currentStatus.value === 'all') {
    return orderStore.orderList.filter(order => order != null)
  }
  // 后端返回的是数字状态，需要转换
  const statusCode = statusMap[currentStatus.value]
  return orderStore.orderList.filter(order => order != null && order.status === statusCode)
})

/**
 * 切换状态
 */
const changeStatus = async (status) => {
  currentStatus.value = status
  await fetchOrders()
}

/**
 * 获取订单列表
 */
const fetchOrders = async () => {
  try {
    loading.value = true
    const userId = userStore.userInfo?.id
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }
    
    const params = {
      userId,
      page: 1,
      pageSize: 100  // 获取更多数据用于统计
    }
    if (currentStatus.value !== 'all') {
      // 将前端状态字符串转换为后端数字
      params.status = statusMap[currentStatus.value]
    }
    
    await orderStore.fetchOrderList(params)
    
    // 更新状态标签的数量
    updateStatusCounts()
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 更新状态标签数量
 */
const updateStatusCounts = () => {
  const allOrders = orderStore.orderList
  
  statusTabs.value.forEach(tab => {
    if (tab.value === 'all') {
      tab.count = allOrders.length
    } else {
      // 后端返回的是数字状态，需要转换
      const statusCode = statusMap[tab.value]
      tab.count = allOrders.filter(order => order.status === statusCode).length
    }
  })
}

/**
 * 跳转到订单详情
 */
const goToOrderDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

/**
 * 支付订单
 */
const handlePay = async (order) => {
  try {
    await ElMessageBox.confirm('确认支付此订单？', '提示', {
      confirmButtonText: '确定支付',
      cancelButtonText: '取消',
      type: 'info'
    })

    // 模拟支付
    await orderStore.payOrder(order.id, { paymentMethod: order.paymentMethod })
    
    ElMessage.success('支付成功')
    await fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error(error.message || '支付失败')
    }
  }
}

/**
 * 取消订单
 */
const handleCancel = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认取消此订单？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await orderStore.cancelOrder(orderId)
    
    ElMessage.success('订单已取消')
    await fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error(error.message || '取消订单失败')
    }
  }
}

/**
 * 确认收货
 */
const handleConfirmReceipt = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认收货？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    await orderStore.confirmReceipt(orderId)
    
    ElMessage.success('确认收货成功')
    await fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error(error.message || '确认收货失败')
    }
  }
}

/**
 * 获取状态CSS类名
 */
const getStatusClass = (status) => {
  const classMap = {
    0: 'pending',      // 待支付
    1: 'paid',         // 待发货
    2: 'processing',   // 待收货
    3: 'completed',    // 已完成
    4: 'cancelled',    // 已取消
    5: 'refunding',    // 退款中
    6: 'refunded'      // 已退款
  }
  return classMap[status] || 'unknown'
}

/**
 * 格式化日期
 */
const formatDate = (dateString) => {
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

onMounted(async () => {
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({
      path: '/login',
      query: { redirect: '/orders' }
    })
    return
  }

  // 获取订单列表
  await fetchOrders()
})
</script>

<style scoped>
.order-list-page {
  min-height: calc(100vh - 60px);
  background-color: #f5f5f5;
  padding: 20px 0;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  gap: 10px;
  background: white;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.filter-tab {
  position: relative;
  padding: 8px 20px;
  border-radius: 20px;
  background-color: #f5f5f5;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  user-select: none;
}

.filter-tab:hover {
  background-color: #e0e0e0;
}

.filter-tab.active {
  background-color: #409eff;
  color: white;
}

.tab-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.filter-tab.active .tab-badge {
  background-color: #fff;
  color: #409eff;
}

/* 订单列表 */
.order-list {
  min-height: 400px;
}

.order-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

.order-info {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.order-no {
  font-weight: 600;
  color: #333;
}

.order-status {
  font-size: 14px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 4px;
}

.status-pending {
  color: #ff9800;
  background-color: #fff3e0;
}

.status-paid {
  color: #409eff;
  background-color: #e3f2fd;
}

.status-processing {
  color: #67c23a;
  background-color: #f0f9ff;
}

.status-completed {
  color: #909399;
  background-color: #f5f5f5;
}

.status-cancelled {
  color: #f56c6c;
  background-color: #fef0f0;
}

.status-refunding {
  color: #e6a23c;
  background-color: #fdf6ec;
}

.status-refunded {
  color: #909399;
  background-color: #f5f5f5;
}

/* 商品列表 */
.order-goods {
  padding: 10px 0;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.goods-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 15px;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-specs {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.goods-quantity {
  font-size: 12px;
  color: #666;
}

.goods-price {
  width: 120px;
  text-align: right;
  color: #333;
  font-size: 14px;
}

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
  margin-top: 15px;
}

.order-total {
  font-size: 14px;
  color: #666;
}

.total-amount {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: 600;
  margin-left: 10px;
}

.order-actions {
  display: flex;
  gap: 10px;
}
</style>

