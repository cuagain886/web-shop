<template>
  <div class="admin-order-list">
    <el-card class="page-card">
      <!-- 页面标题 -->
      <template #header>
        <h3>订单管理</h3>
      </template>

      <!-- 搜索和筛选 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="订单号">
            <el-input
              v-model="filterForm.keyword"
              placeholder="请输入订单号"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            />
          </el-form-item>

          <el-form-item label="订单状态">
            <el-select
              v-model="filterForm.status"
              placeholder="全部状态"
              clearable
              @change="handleSearch"
            >
              <el-option label="全部" value="" />
              <el-option label="待支付" :value="0" />
              <el-option label="待发货" :value="1" />
              <el-option label="待收货" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已取消" :value="4" />
              <el-option label="退款中" :value="5" />
              <el-option label="已退款" :value="7" />
            </el-select>
          </el-form-item>

          <el-form-item label="时间范围">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @change="handleSearch"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><RefreshRight /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 快捷筛选 -->
        <div class="quick-filters">
          <el-button
            :type="filterForm.status === '' ? 'primary' : ''"
            size="small"
            @click="quickFilter('')"
          >
            全部订单
          </el-button>
          <el-button
            :type="filterForm.status === 0 ? 'primary' : ''"
            size="small"
            @click="quickFilter(0)"
          >
            待支付
          </el-button>
          <el-button
            :type="filterForm.status === 1 ? 'primary' : ''"
            size="small"
            @click="quickFilter(1)"
          >
            待发货
          </el-button>
          <el-button
            :type="filterForm.status === 2 ? 'primary' : ''"
            size="small"
            @click="quickFilter(2)"
          >
            已发货
          </el-button>
        </div>
      </div>

      <!-- 批量操作栏 -->
      <div v-if="selectedOrders.length > 0" class="batch-actions">
        <span class="selected-count">已选择 {{ selectedOrders.length }} 项</span>
        <el-button type="primary" @click="handleExportSelected">
          <el-icon><Download /></el-icon>
          导出选中订单
        </el-button>
      </div>

      <!-- 导出按钮 -->
      <div class="export-bar">
        <el-button type="success" plain @click="handleExportAll">
          <el-icon><Download /></el-icon>
          导出全部订单
        </el-button>
      </div>

      <!-- 订单表格 -->
      <el-table
        v-loading="loading"
        :data="orderList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="orderNo" label="订单号" width="180" />
        
        <el-table-column label="商品信息" min-width="250">
          <template #default="{ row }">
            <div class="goods-info">
              <div v-for="(item, index) in (row.items || []).slice(0, 2)" :key="item.id" class="goods-item">
                <el-image
                  :src="item.productImage || item.image"
                  fit="cover"
                  style="width: 50px; height: 50px; border-radius: 4px; margin-right: 10px;"
                />
                <span class="goods-name">{{ item.productName }}</span>
              </div>
              <div v-if="(row.items || []).length > 2" class="more-goods">
                还有{{ row.items.length - 2 }}件商品...
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="收货人" width="120">
          <template #default="{ row }">
            <div>{{ row.receiverName }}</div>
            <div class="sub-text">{{ row.receiverPhone }}</div>
          </template>
        </el-table-column>

        <el-table-column label="金额" width="120">
          <template #default="{ row }">
            <div class="price-cell">¥{{ Number(row.totalAmount).toFixed(2) }}</div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="下单时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="goToOrderDetail(row.id)">
              查看详情
            </el-button>
            
            <el-button
              v-if="row.status === 1"
              text
              type="success"
              @click="handleShipOrder(row)"
            >
              发货
            </el-button>
            
            <el-popconfirm
              v-if="row.status === 0"
              title="确定要取消该订单吗？"
              @confirm="handleCancelOrder(row.id)"
            >
              <template #reference>
                <el-button text type="danger">取消订单</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
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
          <el-input v-model="currentOrder.orderNo" disabled />
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, RefreshRight, Download } from '@element-plus/icons-vue'
import { getAdminOrderList, shipOrder, adminCancelOrder } from '@/api/order'
import { exportOrdersToCSV } from '@/utils/export'
import { useUserStore } from '@/stores/userStore'
import { recordLog, OperationType } from '@/utils/operationLog'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const orderList = ref([])
const selectedOrders = ref([])
const dateRange = ref([])
const shipDialogVisible = ref(false)
const shipping = ref(false)
const currentOrder = ref({})
const shipFormRef = ref(null)

const filterForm = reactive({
  keyword: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const shipForm = reactive({
  trackingNo: ''
})

const shipRules = {
  trackingNo: [
    { required: true, message: '请输入物流单号', trigger: 'blur' }
  ]
}

/**
 * 获取订单列表
 */
const fetchOrders = async () => {
  try {
    loading.value = true
    
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filterForm.keyword || undefined,
      status: filterForm.status === '' ? undefined : filterForm.status,
      startDate: dateRange.value && dateRange.value[0] ? dateRange.value[0] : undefined,
      endDate: dateRange.value && dateRange.value[1] ? dateRange.value[1] : undefined
    }
    
    const result = await getAdminOrderList(params)
    orderList.value = result.list
    pagination.total = result.total
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.page = 1
  fetchOrders()
}

/**
 * 重置
 */
const handleReset = () => {
  filterForm.keyword = ''
  filterForm.status = ''
  dateRange.value = []
  pagination.page = 1
  fetchOrders()
}

/**
 * 快捷筛选
 */
const quickFilter = (status) => {
  filterForm.status = status
  handleSearch()
}

/**
 * 分页改变
 */
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  fetchOrders()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchOrders()
}

/**
 * 跳转到订单详情
 */
const goToOrderDetail = (id) => {
  router.push(`/admin/orders/${id}`)
}

/**
 * 发货
 */
const handleShipOrder = (order) => {
  currentOrder.value = order
  shipForm.trackingNo = ''
  shipDialogVisible.value = true
}

/**
 * 确认发货
 */
const confirmShip = async () => {
  try {
    await shipFormRef.value.validate()
    
    shipping.value = true
    
    await shipOrder(currentOrder.value.orderNo, {
      trackingNo: shipForm.trackingNo
    })
    
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    fetchOrders()
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
const handleCancelOrder = async (id) => {
  try {
    const order = orderList.value.find(o => o.id === id)
    await adminCancelOrder(id, {
      reason: '商家取消'
    })
    
    // 记录操作日志
    recordLog(
      OperationType.ORDER_CANCEL,
      userStore.userInfo?.nickname || userStore.userInfo?.username,
      `取消订单"${order?.orderNo}"`,
      `订单ID: ${id}`
    )
    
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    console.error('取消订单失败:', error)
    ElMessage.error(error.message || '取消订单失败')
  }
}

/**
 * 选择改变
 */
const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

/**
 * 导出选中订单
 */
const handleExportSelected = () => {
  if (selectedOrders.value.length === 0) {
    ElMessage.warning('请先选择要导出的订单')
    return
  }

  const success = exportOrdersToCSV(selectedOrders.value, '订单导出_选中')
  
  if (success) {
    ElMessage.success(`成功导出 ${selectedOrders.value.length} 个订单`)
    
    // 记录操作日志
    recordLog(
      OperationType.ORDER_CANCEL,
      userStore.userInfo?.nickname || userStore.userInfo?.username,
      `导出了 ${selectedOrders.value.length} 个订单`,
      `订单号: ${selectedOrders.value.map(o => o.orderNo).join(', ')}`
    )
  } else {
    ElMessage.error('导出失败')
  }
}

/**
 * 导出全部订单
 */
const handleExportAll = () => {
  if (orderList.value.length === 0) {
    ElMessage.warning('当前没有可导出的订单')
    return
  }

  const success = exportOrdersToCSV(orderList.value, '订单导出_全部')
  
  if (success) {
    ElMessage.success(`成功导出 ${orderList.value.length} 个订单`)
    
    // 记录操作日志
    recordLog(
      OperationType.ORDER_CANCEL,
      userStore.userInfo?.nickname || userStore.userInfo?.username,
      `导出了当前页面的 ${orderList.value.length} 个订单`,
      '批量导出'
    )
  } else {
    ElMessage.error('导出失败')
  }
}

/**
 * 获取状态类型
 */
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',  // 待付款
    1: 'info',     // 待发货
    2: 'primary',  // 待收货
    3: 'success',  // 已完成
    4: 'danger',   // 已取消
    5: 'warning',  // 退款中
    6: 'info'      // 已退款
  }
  return typeMap[status] || 'info'
}

/**
 * 获取状态文本
 */
const getStatusText = (status) => {
  const textMap = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消',
    5: '退款中',
    6: '已退款',
    7: '已退款'
  }
  return textMap[status] || '未知'
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

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.admin-order-list {
  padding: 0;
}

.page-card {
  min-height: calc(100vh - 120px);
}

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.quick-filters {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

.goods-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.goods-item {
  display: flex;
  align-items: center;
}

.goods-name {
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.more-goods {
  font-size: 12px;
  color: #999;
  margin-left: 60px;
}

.sub-text {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.price-cell {
  font-weight: 600;
  color: #f56c6c;
  font-size: 16px;
}

.batch-actions {
  margin-bottom: 15px;
  padding: 10px 15px;
  background-color: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.selected-count {
  font-size: 14px;
  color: #409eff;
  margin-right: 10px;
}

.export-bar {
  margin-bottom: 15px;
  display: flex;
  justify-content: flex-end;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>


