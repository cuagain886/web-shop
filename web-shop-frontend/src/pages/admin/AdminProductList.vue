<template>
  <div class="admin-product-list">
    <el-card class="page-card">
      <!-- 页面标题 -->
      <template #header>
        <div class="card-header">
          <h3>商品列表</h3>
          <el-button type="primary" @click="goToAddProduct">
            <el-icon><Plus /></el-icon>
            新增商品
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="关键词">
            <el-input
              v-model="filterForm.keyword"
              placeholder="商品名称或ID"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            />
          </el-form-item>

          <el-form-item label="分类">
            <el-select
              v-model="filterForm.categoryId"
              placeholder="全部分类"
              clearable
              @change="handleSearch"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="状态">
            <el-select
              v-model="filterForm.status"
              placeholder="全部状态"
              clearable
              @change="handleSearch"
            >
              <el-option label="上架中" value="active" />
              <el-option label="已下架" value="inactive" />
              <el-option label="待上架" value="pending" />
            </el-select>
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
      </div>

      <!-- 批量操作栏 -->
      <div v-if="selectedIds.length > 0" class="batch-actions">
        <span class="selected-count">已选择 {{ selectedIds.length }} 项</span>
        <el-button type="success" @click="handleBatchStatus('active')">
          批量上架
        </el-button>
        <el-button type="warning" @click="handleBatchStatus('inactive')">
          批量下架
        </el-button>
        <el-popconfirm
          title="确定要删除选中的商品吗？"
          @confirm="handleBatchDelete"
        >
          <template #reference>
            <el-button type="danger">批量删除</el-button>
          </template>
        </el-popconfirm>
      </div>

      <!-- 商品表格 -->
      <el-table
        v-loading="loading"
        :data="productList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
              :src="row.images[0]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
              :preview-src-list="row.images"
            />
          </template>
        </el-table-column>

        <el-table-column prop="name" label="商品名称" min-width="200" show-overflow-tooltip />

        <el-table-column prop="categoryName" label="分类" width="120" />

        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <div class="price-cell">
              <span class="current-price">¥{{ row.price }}</span>
              <span v-if="row.originalPrice > row.price" class="original-price">
                ¥{{ row.originalPrice }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="stock" label="库存" width="100">
          <template #default="{ row }">
            <el-tag :type="row.stock > 10 ? 'success' : row.stock > 0 ? 'warning' : 'danger'">
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="sales" label="销量" width="100" />

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="推荐" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.isRecommend === 1"
              @change="handleToggleRecommend(row.id, $event)"
            />
          </template>
        </el-table-column>

        <el-table-column label="秒杀" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.isFlashSale === 1"
              @change="handleToggleFlashSale(row.id, $event)"
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="goToEditProduct(row.id)">
              编辑
            </el-button>
            
            <el-button
              v-if="row.status === 'pending' || row.status === 'inactive'"
              text
              type="success"
              @click="handleUpdateStatus(row.id, 'active')"
            >
              上架
            </el-button>
            
            <el-button
              v-if="row.status === 'active'"
              text
              type="warning"
              @click="handleUpdateStatus(row.id, 'inactive')"
            >
              下架
            </el-button>
            
            <el-popconfirm
              title="确定要删除该商品吗？"
              @confirm="handleDelete(row.id)"
            >
              <template #reference>
                <el-button text type="danger">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import { getAdminProductList, deleteProduct, updateProductStatus, batchDeleteProducts, updateProductRecommend, updateProductFlashSale } from '@/api/product'
import { getCategoryList } from '@/api/category'
import { useUserStore } from '@/stores/userStore'
import { recordLog, OperationType } from '@/utils/operationLog'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const productList = ref([])
const categories = ref([])
const selectedIds = ref([])

const filterForm = reactive({
  keyword: '',
  categoryId: null,
  status: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

/**
 * 获取商品列表
 */
const fetchProducts = async () => {
  try {
    loading.value = true
    
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filterForm.keyword || undefined,
      categoryId: filterForm.categoryId || undefined,
      status: filterForm.status || undefined
    }
    
    const result = await getAdminProductList(params)
    // 处理后端返回的IPage格式数据
    const pageData = result.data || result
    productList.value = pageData.records || pageData.list || []
    pagination.total = pageData.total || 0
    
    // 处理商品数据：将images JSON字符串转为数组，status数字转为字符串
    productList.value = productList.value.map(product => ({
      ...product,
      images: typeof product.images === 'string' ? JSON.parse(product.images || '[]') : (product.images || []),
      status: product.status === 1 ? 'active' : product.status === 0 ? 'inactive' : 'pending'
    }))
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取分类列表
 */
const fetchCategories = async () => {
  try {
    const list = await getCategoryList({ level: 2 }) // 获取二级分类
    categories.value = list
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.page = 1
  fetchProducts()
}

/**
 * 重置
 */
const handleReset = () => {
  filterForm.keyword = ''
  filterForm.categoryId = null
  filterForm.status = null
  pagination.page = 1
  fetchProducts()
}

/**
 * 分页大小改变
 */
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  fetchProducts()
}

/**
 * 页码改变
 */
const handlePageChange = (page) => {
  pagination.page = page
  fetchProducts()
}

/**
 * 跳转到新增商品页
 */
const goToAddProduct = () => {
  router.push('/admin/products/add')
}

/**
 * 跳转到编辑商品页
 */
const goToEditProduct = (id) => {
  router.push(`/admin/products/edit/${id}`)
}

/**
 * 更新商品状态
 */
const handleUpdateStatus = async (id, status) => {
  try {
    const product = productList.value.find(p => p.id === id)
    await updateProductStatus(id, status)
    
    // 记录操作日志
    recordLog(
      OperationType.PRODUCT_STATUS,
      userStore.userInfo?.nickname || userStore.userInfo?.username,
      `将商品"${product?.name}"${status === 'active' ? '上架' : '下架'}`,
      `商品ID: ${id}`
    )
    
    ElMessage.success(`${status === 'active' ? '上架' : '下架'}成功`)
    fetchProducts()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('操作失败')
  }
}

/**
 * 删除商品
 */
const handleDelete = async (id) => {
  try {
    const product = productList.value.find(p => p.id === id)
    await deleteProduct(id)
    
    // 记录操作日志
    recordLog(
      OperationType.PRODUCT_DELETE,
      userStore.userInfo?.nickname || userStore.userInfo?.username,
      `删除商品"${product?.name}"`,
      `商品ID: ${id}`
    )
    
    ElMessage.success('删除成功')
    
    // 如果当前页只有一条数据且不是第一页，返回上一页
    if (productList.value.length === 1 && pagination.page > 1) {
      pagination.page--
    }
    
    fetchProducts()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

/**
 * 选择改变
 */
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

/**
 * 批量更新状态
 */
const handleBatchStatus = async (status) => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }

  try {
    for (const id of selectedIds.value) {
      await updateProductStatus(id, status)
    }
    
    // 记录操作日志
    recordLog(
      OperationType.PRODUCT_STATUS,
      userStore.userInfo?.nickname || userStore.userInfo?.username,
      `批量${status === 'active' ? '上架' : '下架'}了 ${selectedIds.value.length} 个商品`,
      `商品ID: ${selectedIds.value.join(', ')}`
    )
    
    ElMessage.success(`批量${status === 'active' ? '上架' : '下架'}成功`)
    selectedIds.value = []
    fetchProducts()
  } catch (error) {
    console.error('批量操作失败:', error)
    ElMessage.error('批量操作失败')
  }
}

/**
 * 批量删除
 */
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }

  try {
    await batchDeleteProducts(selectedIds.value)
    
    // 记录操作日志
    recordLog(
      OperationType.PRODUCT_DELETE,
      userStore.userInfo?.nickname || userStore.userInfo?.username,
      `批量删除了 ${selectedIds.value.length} 个商品`,
      `商品ID: ${selectedIds.value.join(', ')}`
    )
    
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    
    // 如果当前页数据被删完且不是第一页，返回上一页
    if (selectedIds.value.length === productList.value.length && pagination.page > 1) {
      pagination.page--
    }
    
    fetchProducts()
  } catch (error) {
    console.error('批量删除失败:', error)
    ElMessage.error('批量删除失败')
  }
}

/**
 * 获取状态类型
 */
const getStatusType = (status) => {
  const typeMap = {
    active: 'success',
    inactive: 'info',
    pending: 'warning'
  }
  return typeMap[status] || 'info'
}

/**
 * 获取状态文本
 */
const getStatusText = (status) => {
  const textMap = {
    active: '上架中',
    inactive: '已下架',
    pending: '待上架'
  }
  return textMap[status] || '未知'
}

/**
 * 切换推荐状态
 */
const handleToggleRecommend = async (id, value) => {
  try {
    await updateProductRecommend(id, value ? 1 : 0)
    ElMessage.success(`${value ? '已设为' : '已取消'}推荐`)
    fetchProducts()
  } catch (error) {
    console.error('更新推荐状态失败:', error)
    ElMessage.error('操作失败')
  }
}

/**
 * 切换秒杀状态
 */
const handleToggleFlashSale = async (id, value) => {
  try {
    await updateProductFlashSale(id, value ? 1 : 0)
    ElMessage.success(`${value ? '已设为' : '已取消'}秒杀`)
    fetchProducts()
  } catch (error) {
    console.error('更新秒杀状态失败:', error)
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchProducts()
  fetchCategories()
})
</script>

<style scoped>
.admin-product-list {
  padding: 0;
}

.page-card {
  min-height: calc(100vh - 120px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.price-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.current-price {
  font-weight: 600;
  color: #f56c6c;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
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

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>


