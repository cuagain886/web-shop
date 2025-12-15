<template>
  <div class="product-list-page">
    <div class="page-container">
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentCategory">{{ currentCategory }}</el-breadcrumb-item>
        <el-breadcrumb-item v-else>全部商品</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 筛选区域 -->
      <el-card class="filter-card" shadow="never">
        <div class="filter-section">
          <!-- 分类筛选 -->
          <div class="filter-item">
            <label>商品分类：</label>
            <el-radio-group v-model="filters.categoryId" @change="handleFilterChange">
              <el-radio-button value="">全部分类</el-radio-button>
              <el-radio-button
                v-for="cat in categories"
                :key="cat.id"
                :value="cat.id"
              >
                {{ cat.name }}
              </el-radio-button>
            </el-radio-group>
          </div>

          <!-- 价格区间筛选 -->
          <div class="filter-item">
            <label>价格区间：</label>
            <el-radio-group v-model="filters.priceRange" @change="handleFilterChange">
              <el-radio-button value="">不限</el-radio-button>
              <el-radio-button value="0-100">0-100元</el-radio-button>
              <el-radio-button value="100-500">100-500元</el-radio-button>
              <el-radio-button value="500-1000">500-1000元</el-radio-button>
              <el-radio-button value="1000-5000">1000-5000元</el-radio-button>
              <el-radio-button value="5000+">5000元以上</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 自定义价格 -->
          <div class="filter-item custom-price">
            <label>自定义价格：</label>
            <el-input
              v-model.number="filters.minPrice"
              placeholder="最低价"
              type="number"
              style="width: 120px"
              @change="handleCustomPriceChange"
            />
            <span class="price-separator">-</span>
            <el-input
              v-model.number="filters.maxPrice"
              placeholder="最高价"
              type="number"
              style="width: 120px"
              @change="handleCustomPriceChange"
            />
            <el-button @click="applyCustomPrice">确定</el-button>
          </div>

          <!-- 关键词搜索 -->
          <div class="filter-item search-box">
            <label>关键词搜索：</label>
            <el-input
              v-model="filters.keyword"
              placeholder="请输入商品名称或描述"
              style="width: 300px"
              clearable
              @keyup.enter="handleFilterChange"
            >
              <template #append>
                <el-button :icon="Search" @click="handleFilterChange">搜索</el-button>
              </template>
            </el-input>
          </div>
        </div>
      </el-card>

      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <span class="result-count">共找到 {{ total }} 件商品</span>
        </div>
        <div class="toolbar-right">
          <!-- 排序 -->
          <el-select v-model="sortType" style="width: 150px; margin-right: 10px" @change="handleSortChange">
            <el-option label="综合排序" value="default" />
            <el-option label="价格从低到高" value="price-asc" />
            <el-option label="价格从高到低" value="price-desc" />
            <el-option label="销量从高到低" value="sales-desc" />
          </el-select>

          <!-- 视图切换 -->
          <el-button-group>
            <el-button
              :type="viewMode === 'grid' ? 'primary' : ''"
              :icon="Grid"
              @click="viewMode = 'grid'"
            >
              网格
            </el-button>
            <el-button
              :type="viewMode === 'list' ? 'primary' : ''"
              :icon="List"
              @click="viewMode = 'list'"
            >
              列表
            </el-button>
          </el-button-group>
        </div>
      </div>

      <!-- 商品展示区域 -->
      <div v-loading="loading" class="product-display">
        <!-- 网格视图 -->
        <div v-if="viewMode === 'grid'" class="grid-view">
          <div
            v-for="product in displayProducts"
            :key="product.id"
            class="product-card"
            @click="goToDetail(product.id)"
          >
            <div class="product-image" :style="{ background: product.color }">
              <el-image v-if="product.image" :src="product.image" fit="cover" style="width: 100%; height: 100%; object-fit: cover;" />
              <div v-else class="placeholder-text">{{ product.name }}</div>
            </div>
            <div class="product-name" :title="product.name">{{ product.name.length > 20 ? product.name.substring(0, 20) + '...' : product.name }}</div>
            <div class="product-footer">
              <span class="product-price">¥{{ product.price }}</span>
              <span class="product-sales">{{ product.sales || 0 }}人付款</span>
            </div>
          </div>
        </div>

        <!-- 列表视图 -->
        <div v-if="viewMode === 'list'" class="list-view">
          <div
            v-for="product in displayProducts"
            :key="product.id"
            class="product-item"
            @click="goToDetail(product.id)"
          >
            <div class="item-image" :style="{ background: product.color }">
              <el-image v-if="product.image" :src="product.image" fit="cover" />
              <div v-else class="placeholder-text">{{ product.name }}</div>
            </div>
            <div class="item-content">
              <h3 class="item-name">{{ product.name }}</h3>
              <p class="item-desc">{{ product.desc || product.description || '暂无描述' }}</p>
              <div class="item-meta">
                <el-tag v-if="product.categoryName" size="small">{{ product.categoryName }}</el-tag>
                <span class="meta-item">库存：{{ product.stock || 0 }}</span>
                <span class="meta-item">已售：{{ product.sales || 0 }}</span>
              </div>
            </div>
            <div class="item-action">
              <div class="item-price">¥{{ product.price }}</div>
              <el-button type="primary" size="small">查看详情</el-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading && displayProducts.length === 0" description="暂无商品" />
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Grid, List } from '@element-plus/icons-vue'
import { getProductList } from '@/api/product'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 分类数据
const categories = ref([
  { id: 1, name: '家用电器' },
  { id: 2, name: '手机数码' },
  { id: 3, name: '电脑办公' },
  { id: 4, name: '家居家具' },
  { id: 5, name: '服装鞋靴' },
  { id: 6, name: '美妆个护' },
  { id: 7, name: '运动户外' },
  { id: 8, name: '食品生鲜' },
  { id: 9, name: '母婴玩具' },
  { id: 10, name: '图书文娱' }
])

// 筛选条件
const filters = reactive({
  categoryId: '',
  priceRange: '',
  minPrice: null,
  maxPrice: null,
  keyword: ''
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 10
})

// 其他状态
const loading = ref(false)
const viewMode = ref('grid') // grid 或 list
const sortType = ref('default')
const allProducts = ref([])
const total = ref(0)

// 当前分类名称
const currentCategory = computed(() => {
  if (!filters.categoryId) return ''
  const cat = categories.value.find(c => c.id === filters.categoryId)
  return cat ? cat.name : ''
})

// 过滤和排序后的商品
const filteredProducts = computed(() => {
  let result = [...allProducts.value]

  // 分类筛选
  if (filters.categoryId) {
    result = result.filter(p => p.categoryId === filters.categoryId)
  }

  // 价格区间筛选
  if (filters.priceRange) {
    result = result.filter(p => {
      const price = p.price
      if (filters.priceRange === '0-100') return price >= 0 && price <= 100
      if (filters.priceRange === '100-500') return price > 100 && price <= 500
      if (filters.priceRange === '500-1000') return price > 500 && price <= 1000
      if (filters.priceRange === '1000-5000') return price > 1000 && price <= 5000
      if (filters.priceRange === '5000+') return price > 5000
      return true
    })
  }

  // 自定义价格筛选
  if (filters.minPrice !== null && filters.minPrice !== '') {
    result = result.filter(p => p.price >= filters.minPrice)
  }
  if (filters.maxPrice !== null && filters.maxPrice !== '') {
    result = result.filter(p => p.price <= filters.maxPrice)
  }

  // 关键词搜索
  if (filters.keyword) {
    const keyword = filters.keyword.toLowerCase()
    result = result.filter(p => {
      const name = (p.name || '').toLowerCase()
      const desc = (p.desc || p.description || '').toLowerCase()
      return name.includes(keyword) || desc.includes(keyword)
    })
  }

  // 排序
  if (sortType.value === 'price-asc') {
    result.sort((a, b) => a.price - b.price)
  } else if (sortType.value === 'price-desc') {
    result.sort((a, b) => b.price - a.price)
  } else if (sortType.value === 'sales-desc') {
    result.sort((a, b) => (b.sales || 0) - (a.sales || 0))
  }

  return result
})

// 当前页显示的商品
const displayProducts = computed(() => {
  total.value = filteredProducts.value.length
  const start = (pagination.page - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  return filteredProducts.value.slice(start, end)
})

/**
 * 加载商品列表
 */
const loadProducts = async () => {
  loading.value = true
  try {
    const data = await getProductList()
    // 后端返回的是分页对象 {records: [], total: 0, ...}
    const products = data.records || data || []
    allProducts.value = products.map(p => {
      // 解析images字段
      let images = []
      if (p.images) {
        images = typeof p.images === 'string' ? JSON.parse(p.images) : p.images
      }
      return {
        ...p,
        images: images,
        image: images[0] || '', // 取第一张图片作为主图
        categoryName: categories.value.find(c => c.id === p.categoryId)?.name || ''
      }
    })
    console.log('商品列表加载成功:', allProducts.value.length)
  } catch (error) {
    console.error('加载商品列表失败:', error)
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理筛选条件变化
 */
const handleFilterChange = () => {
  pagination.page = 1 // 重置到第一页
}

/**
 * 应用自定义价格
 */
const applyCustomPrice = () => {
  if (filters.minPrice !== null && filters.maxPrice !== null) {
    if (filters.minPrice > filters.maxPrice) {
      ElMessage.warning('最低价不能大于最高价')
      return
    }
  }
  filters.priceRange = '' // 清空预设价格区间
  handleFilterChange()
}

/**
 * 自定义价格输入变化
 */
const handleCustomPriceChange = () => {
  // 输入自定义价格时，清空预设价格区间
  if (filters.minPrice !== null || filters.maxPrice !== null) {
    filters.priceRange = ''
  }
}

/**
 * 排序变化
 */
const handleSortChange = () => {
  // 排序会自动通过 computed 生效
}

/**
 * 分页大小变化
 */
const handleSizeChange = () => {
  pagination.page = 1
}

/**
 * 页码变化
 */
const handlePageChange = () => {
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

/**
 * 跳转到商品详情
 */
const goToDetail = (productId) => {
  router.push(`/product/${productId}`)
}

/**
 * 从路由参数初始化筛选条件
 */
const initFromRoute = () => {
  // 从路由获取分类ID
  const categoryId = route.query.categoryId
  if (categoryId) {
    filters.categoryId = parseInt(categoryId)
  }

  // 从路由获取关键词
  const keyword = route.query.keyword
  if (keyword) {
    filters.keyword = keyword
  }
}

/**
 * 监听路由变化
 */
watch(() => route.query, (newQuery) => {
  if (route.path === '/products') {
    initFromRoute()
  }
}, { deep: true })

onMounted(() => {
  initFromRoute()
  loadProducts()
})
</script>

<style scoped>
.product-list-page {
  background: #f5f5f5;
  min-height: calc(100vh - 60px);
  padding: 20px 0;
}

.page-container {
  width: 1200px;
  margin: 0 auto;
}

/* 面包屑 */
.breadcrumb {
  margin-bottom: 20px;
  padding: 10px;
  background: white;
  border-radius: 4px;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 20px;
}

.filter-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.filter-item {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-item label {
  font-weight: 500;
  color: #606266;
  min-width: 100px;
}

.custom-price {
  align-items: center;
}

.price-separator {
  margin: 0 5px;
  color: #909399;
}

.search-box {
  align-items: center;
}

/* 工具栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 4px;
  margin-bottom: 20px;
}

.result-count {
  color: #909399;
  font-size: 14px;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

/* 商品展示区域 */
.product-display {
  min-height: 400px;
  background: white;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

/* 网格视图 */
.grid-view {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.product-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.product-card:hover {
  transform: translateY(-3px);
}

.product-image {
  width: 100%;
  padding-bottom: 100%; /* 1:1 ratio for square */
  position: relative;
  border-radius: 4px;
  margin-bottom: 10px;
  overflow: hidden;
  background: #f5f5f5;
}

.product-image .el-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.placeholder-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 14px;
  font-weight: bold;
  padding: 20px;
  text-align: center;
  width: 100%;
}

.product-name {
  font-size: 14px;
  color: #333;
  height: 40px;
  line-height: 20px;
  margin-bottom: 8px;
  padding: 0 5px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.product-price {
  font-size: 20px;
  color: #e4393c;
  font-weight: bold;
}

.product-sales {
  font-size: 12px;
  color: #909399;
}

/* 列表视图 */
.list-view {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.product-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.item-image {
  width: 200px;
  height: 200px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f5f5;
}

.item-image .el-image {
  width: 100%;
  height: 100%;
}

.item-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.item-name {
  font-size: 18px;
  color: #303133;
  margin: 0;
  font-weight: 500;
}

.item-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-meta {
  display: flex;
  gap: 15px;
  align-items: center;
}

.meta-item {
  font-size: 13px;
  color: #909399;
}

.item-action {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-end;
  min-width: 120px;
}

.item-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: white;
  border-radius: 4px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .page-container {
    width: 100%;
    padding: 0 20px;
  }

  .grid-view {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .grid-view {
    grid-template-columns: repeat(2, 1fr);
  }

  .product-item {
    flex-direction: column;
  }

  .item-image {
    width: 100%;
  }

  .item-action {
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
  }
}
</style>

