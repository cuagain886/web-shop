<template>
  <div class="order-detail-page">
    <div class="page-container">
      <!-- 返回按钮 -->
      <div class="back-bar">
        <el-button @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回订单列表
        </el-button>
      </div>

      <div v-if="order" class="order-detail">
        <!-- 订单状态跟踪 -->
        <div class="status-section">
          <h3 class="section-title">订单状态</h3>
          
          <el-steps :active="currentStep" align-center finish-status="success">
            <el-step title="提交订单" :description="formatDate(order.createdAt)" />
            <el-step 
              title="支付成功" 
              :description="order.paidAt ? formatDate(order.paidAt) : ''" 
            />
            <el-step 
              title="商品发货" 
              :description="order.shippedAt ? formatDate(order.shippedAt) : ''" 
            />
            <el-step 
              title="确认收货" 
              :description="order.completedAt ? formatDate(order.completedAt) : ''" 
            />
          </el-steps>

          <div class="current-status">
            <span class="status-label">当前状态：</span>
            <span class="status-text" :class="`status-${order.status}`">
              {{ order.statusText }}
            </span>
          </div>
        </div>

        <!-- 收货信息 -->
        <div class="info-section">
          <h3 class="section-title">收货信息</h3>
          <div class="address-info">
            <div class="info-row">
              <span class="info-label">收货人：</span>
              <span class="info-value">{{ order.address.receiverName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">联系电话：</span>
              <span class="info-value">{{ order.address.phone }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">收货地址：</span>
              <span class="info-value">
                {{ order.address.province }} {{ order.address.city }} 
                {{ order.address.district }} {{ order.address.detail }}
              </span>
            </div>
          </div>
        </div>

        <!-- 商品明细 -->
        <div class="goods-section">
          <h3 class="section-title">商品明细</h3>
          <div class="goods-list">
            <div
              v-for="item in order.items"
              :key="item.id"
              class="goods-item"
            >
              <img :src="item.image" :alt="item.productName" class="goods-image" />
              <div class="goods-info">
                <div class="goods-name">{{ item.productName }}</div>
                <div class="goods-specs" v-if="item.specs">{{ item.specs }}</div>
              </div>
              <div class="goods-price">¥{{ item.price.toFixed(2) }}</div>
              <div class="goods-quantity">x{{ item.quantity }}</div>
              <div class="goods-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
            </div>
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="info-section">
          <h3 class="section-title">订单信息</h3>
          <div class="order-info">
            <div class="info-row">
              <span class="info-label">订单号：</span>
              <span class="info-value">{{ order.orderNo }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">下单时间：</span>
              <span class="info-value">{{ formatDate(order.createdAt) }}</span>
            </div>
            <div class="info-row" v-if="order.paidAt">
              <span class="info-label">支付时间：</span>
              <span class="info-value">{{ formatDate(order.paidAt) }}</span>
            </div>
            <div class="info-row" v-if="order.shippedAt">
              <span class="info-label">发货时间：</span>
              <span class="info-value">{{ formatDate(order.shippedAt) }}</span>
            </div>
            <div class="info-row" v-if="order.trackingNo">
              <span class="info-label">物流单号：</span>
              <span class="info-value">{{ order.trackingNo }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">支付方式：</span>
              <span class="info-value">{{ order.paymentMethodText }}</span>
            </div>
          </div>
        </div>

        <!-- 费用明细 -->
        <div class="price-section">
          <div class="price-row">
            <span class="price-label">商品总价：</span>
            <span class="price-value">¥{{ order.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="price-row">
            <span class="price-label">运费：</span>
            <span class="price-value">¥0.00</span>
          </div>
          <div class="price-row total-row">
            <span class="price-label">实付款：</span>
            <span class="price-value total-amount">¥{{ order.totalAmount.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-section">
          <el-button
            v-if="order.status === 0"
            type="danger"
            size="large"
            :loading="paying"
            @click="handlePay"
          >
            立即支付
          </el-button>
          <el-button
            v-if="order.status === 0"
            size="large"
            :loading="cancelling"
            @click="handleCancel"
          >
            取消订单
          </el-button>
          <el-button
            v-if="order.status === 2"
            type="success"
            size="large"
            :loading="confirming"
            @click="handleConfirmReceipt"
          >
            确认收货
          </el-button>
          <el-button
            v-if="order.status === 1 || order.status === 2 || order.status === 3"
            type="warning"
            size="large"
            :loading="refunding"
            @click="handleRefund"
          >
            申请退款
          </el-button>
        </div>
      </div>

      <!-- 加载中 -->
      <el-skeleton v-else :rows="10" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useOrderStore } from '@/stores/orderStore'
import { applyRefund } from '@/api/order'

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()

// 数据状态
const order = ref(null)
const paying = ref(false)
const cancelling = ref(false)
const confirming = ref(false)
const refunding = ref(false)

// 当前步骤（根据订单状态计算）
const currentStep = computed(() => {
  if (!order.value) return 0
  
  const statusSteps = {
    0: 1,  // 待支付
    1: 2,  // 待发货(已支付)
    2: 3,  // 待收货(已发货)
    3: 4,  // 已完成
    4: 0,  // 已取消
    5: 2,  // 退款中
    6: 2   // 已退款
  }
  
  return statusSteps[order.value.status] || 0
})

/**
 * 获取订单详情
 */
const fetchOrderDetail = async () => {
  try {
    const orderId = route.params.id
    const orderData = await orderStore.fetchOrderDetail(orderId)
    order.value = orderData
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
    router.push('/orders')
  }
}

/**
 * 支付订单
 */
const handlePay = async () => {
  try {
    await ElMessageBox.confirm('确认支付此订单？', '提示', {
      confirmButtonText: '确定支付',
      cancelButtonText: '取消',
      type: 'info'
    })

    paying.value = true
    
    // 模拟支付
    const updatedOrder = await orderStore.payOrder(order.value.id, {
      paymentMethod: order.value.paymentMethod
    })
    
    order.value = updatedOrder
    ElMessage.success('支付成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error(error.message || '支付失败')
    }
  } finally {
    paying.value = false
  }
}

/**
 * 取消订单
 */
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确认取消此订单？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    cancelling.value = true
    
    const updatedOrder = await orderStore.cancelOrder(order.value.id)
    order.value = updatedOrder
    
    ElMessage.success('订单已取消')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error(error.message || '取消订单失败')
    }
  } finally {
    cancelling.value = false
  }
}

/**
 * 确认收货
 */
const handleConfirmReceipt = async () => {
  try {
    await ElMessageBox.confirm('确认收货？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    confirming.value = true
    
    const updatedOrder = await orderStore.confirmReceipt(order.value.id)
    order.value = updatedOrder
    
    ElMessage.success('确认收货成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error(error.message || '确认收货失败')
    }
  } finally {
    confirming.value = false
  }
}

/**
 * 申请退款
 */
const handleRefund = async () => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入退款原因', '申请退款', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入退款原因'
    })

    refunding.value = true
    
    await applyRefund({
      orderId: order.value.id,
      refundAmount: order.value.totalAmount,
      reason: reason
    })
    
    ElMessage.success('退款申请已提交，请等待审核')
    await fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('申请退款失败:', error)
      ElMessage.error(error.message || '申请退款失败')
    }
  } finally {
    refunding.value = false
  }
}

/**
 * 返回订单列表
 */
const goBack = () => {
  router.push('/orders')
}

/**
 * 格式化日期
 */
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
  min-height: calc(100vh - 60px);
  background-color: #f5f5f5;
  padding: 20px 0;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-bar {
  margin-bottom: 20px;
}

.order-detail > div {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

/* 订单状态跟踪 */
.status-section {
  padding: 30px 20px;
}

.current-status {
  margin-top: 30px;
  text-align: center;
  font-size: 16px;
}

.status-label {
  color: #666;
  margin-right: 10px;
}

.status-text {
  font-size: 18px;
  font-weight: 600;
  padding: 4px 16px;
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

.status-shipped {
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

/* 信息展示 */
.address-info,
.order-info {
  padding: 10px 0;
}

.info-row {
  display: flex;
  margin-bottom: 15px;
  font-size: 14px;
}

.info-label {
  width: 120px;
  color: #666;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  color: #333;
  word-break: break-all;
}

/* 商品明细 */
.goods-list {
  border-top: 1px solid #eee;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.goods-item:last-child {
  border-bottom: none;
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
}

.goods-price {
  width: 120px;
  text-align: right;
  color: #333;
  font-size: 14px;
}

.goods-quantity {
  width: 80px;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.goods-subtotal {
  width: 120px;
  text-align: right;
  color: #ff4d4f;
  font-size: 16px;
  font-weight: 600;
}

/* 费用明细 */
.price-section {
  text-align: right;
  padding: 20px;
}

.price-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
}

.price-label {
  color: #666;
  margin-right: 20px;
}

.price-value {
  color: #333;
  min-width: 100px;
}

.total-row {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
  font-size: 16px;
  font-weight: 600;
}

.total-amount {
  color: #ff4d4f;
  font-size: 24px;
}

/* 操作按钮 */
.action-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 30px 20px;
}
</style>
