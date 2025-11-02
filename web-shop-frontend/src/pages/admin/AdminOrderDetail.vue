<template>
  <div class="admin-order-detail">
    <el-card class="page-card">
      <!-- 返回按钮 -->
      <div class="back-bar">
        <el-button @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回订单列表
        </el-button>
      </div>

      <div v-if="order" class="order-content">
        <!-- 订单状态 -->
        <div class="status-section">
          <div class="status-header">
            <h3>订单状态</h3>
            <el-tag :type="getStatusType(order.status)" size="large">
              {{ order.statusText }}
            </el-tag>
          </div>
          
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
        </div>

        <!-- 收货信息 -->
        <div class="info-section">
          <h3 class="section-title">收货信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="收货人">
              {{ order.address.receiverName }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ order.address.phone }}
            </el-descriptions-item>
            <el-descriptions-item label="收货地址" :span="2">
              {{ order.address.province }} {{ order.address.city }} 
              {{ order.address.district }} {{ order.address.detail }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 商品明细 -->
        <div class="goods-section">
          <h3 class="section-title">商品明细</h3>
          <el-table :data="order.items" border style="width: 100%">
            <el-table-column label="商品图片" width="100">
              <template #default="{ row }">
                <el-image
                  :src="row.image"
                  fit="cover"
                  style="width: 60px; height: 60px; border-radius: 4px;"
                />
              </template>
            </el-table-column>
            <el-table-column prop="productName" label="商品名称" min-width="200" />
            <el-table-column label="规格" width="150">
              <template #default="{ row }">
                {{ row.specs || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120">
              <template #default="{ row }">
                ¥{{ row.price.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column label="小计" width="120">
              <template #default="{ row }">
                <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 订单信息 -->
        <div class="info-section">
          <h3 class="section-title">订单信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">
              {{ order.orderNo }}
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">
              {{ formatDate(order.createdAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="支付方式">
              {{ order.paymentMethodText }}
            </el-descriptions-item>
            <el-descriptions-item label="支付时间">
              {{ order.paidAt ? formatDate(order.paidAt) : '-' }}
            </el-descriptions-item>
            <el-descriptions-item v-if="order.trackingNo" label="物流单号">
              {{ order.trackingNo }}
            </el-descriptions-item>
            <el-descriptions-item v-if="order.shippedAt" label="发货时间">
              {{ formatDate(order.shippedAt) }}
            </el-descriptions-item>
          </el-descriptions>
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

        <!-- 订单备注 -->
        <div class="note-section">
          <h3 class="section-title">订单备注</h3>
          <div v-if="order.notes && order.notes.length > 0" class="notes-list">
            <div v-for="(note, index) in order.notes" :key="index" class="note-item">
              <div class="note-time">{{ formatDate(note.createdAt) }}</div>
              <div class="note-content">{{ note.content }}</div>
            </div>
          </div>
          <div v-if="order.status === 'completed'" class="add-note">
            <el-input
              v-model="newNote"
              type="textarea"
              :rows="3"
              placeholder="添加订单备注..."
              maxlength="200"
              show-word-limit
            />
            <el-button
              type="primary"
              style="margin-top: 10px;"
              :loading="addingNote"
              @click="handleAddNote"
            >
              添加备注
            </el-button>
          </div>
          <el-empty v-if="!order.notes || order.notes.length === 0" description="暂无备注" />
        </div>

        <!-- 操作按钮 -->
        <div class="action-section">
          <el-button
            v-if="order.status === 'pending'"
            type="danger"
            size="large"
            :loading="cancelling"
            @click="handleCancel"
          >
            取消订单
          </el-button>
          
          <el-button
            v-if="order.status === 'paid'"
            type="success"
            size="large"
            @click="shipDialogVisible = true"
          >
            订单发货
          </el-button>
        </div>
      </div>

      <!-- 加载中 -->
      <el-skeleton v-else :rows="10" animated />
    </el-card>

    <!-- 发货对话框 -->
    <el-dialog
      v-model="shipDialogVisible"
      title="订单发货"
      width="500px"
    >
      <el-form
        ref="shipFormRef"
        :model="shipForm"
        :rules="shipRules"
        label-width="100px"
      >
        <el-form-item label="订单号">
          <el-input v-model="order.orderNo" disabled />
        </el-form-item>

        <el-form-item label="物流单号" prop="trackingNo">
          <el-input
            v-model="shipForm.trackingNo"
            placeholder="请输入物流单号"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipping" @click="confirmShip">
          确定发货
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getAdminOrderDetail, shipOrder, adminCancelOrder, addOrderNote } from '@/api/order'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const newNote = ref('')
const shipDialogVisible = ref(false)
const shipping = ref(false)
const cancelling = ref(false)
const addingNote = ref(false)
const shipFormRef = ref(null)

const shipForm = reactive({
  trackingNo: ''
})

const shipRules = {
  trackingNo: [
    { required: true, message: '请输入物流单号', trigger: 'blur' }
  ]
}

// 当前步骤
const currentStep = computed(() => {
  if (!order.value) return 0
  
  const statusSteps = {
    'pending': 1,
    'paid': 2,
    'shipped': 3,
    'completed': 4,
    'cancelled': 0
  }
  
  return statusSteps[order.value.status] || 0
})

/**
 * 获取订单详情
 */
const fetchOrderDetail = async () => {
  try {
    const orderId = route.params.id
    const orderData = await getAdminOrderDetail(orderId)
    order.value = orderData
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
    router.push('/admin/orders')
  }
}

/**
 * 确认发货
 */
const confirmShip = async () => {
  try {
    await shipFormRef.value.validate()
    
    shipping.value = true
    
    await shipOrder(order.value.id, {
      trackingNo: shipForm.trackingNo
    })
    
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发货失败:', error)
      ElMessage.error(error.message || '发货失败')
    }
  } finally {
    shipping.value = false
  }
}

/**
 * 取消订单
 */
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    cancelling.value = true
    
    await adminCancelOrder(order.value.id, {
      reason: '商家取消'
    })
    
    ElMessage.success('订单已取消')
    fetchOrderDetail()
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
 * 添加备注
 */
const handleAddNote = async () => {
  if (!newNote.value.trim()) {
    ElMessage.warning('请输入备注内容')
    return
  }

  try {
    addingNote.value = true
    
    await addOrderNote(order.value.id, {
      note: newNote.value
    })
    
    ElMessage.success('备注添加成功')
    newNote.value = ''
    fetchOrderDetail()
  } catch (error) {
    console.error('添加备注失败:', error)
    ElMessage.error('添加备注失败')
  } finally {
    addingNote.value = false
  }
}

/**
 * 返回列表
 */
const goBack = () => {
  router.push('/admin/orders')
}

/**
 * 获取状态类型
 */
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    paid: 'info',
    shipped: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return typeMap[status] || 'info'
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
.admin-order-detail {
  padding: 0;
}

.page-card {
  min-height: calc(100vh - 120px);
}

.back-bar {
  margin-bottom: 20px;
}

.order-content > div {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

/* 订单状态 */
.status-section {
  background: white !important;
  padding: 30px 20px !important;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.status-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

/* 信息展示 */
.info-section,
.goods-section {
  background: white !important;
}

.subtotal {
  font-weight: 600;
  color: #f56c6c;
}

/* 费用明细 */
.price-section {
  background: white !important;
  text-align: right;
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
  color: #f56c6c;
  font-size: 24px;
}

/* 备注区域 */
.note-section {
  background: white !important;
}

.notes-list {
  margin-bottom: 20px;
}

.note-item {
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
  margin-bottom: 10px;
}

.note-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.note-content {
  font-size: 14px;
  color: #333;
}

/* 操作按钮 */
.action-section {
  background: white !important;
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 30px 20px !important;
}
</style>


