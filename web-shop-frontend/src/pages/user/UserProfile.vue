<template>
  <div class="user-profile">
    <div class="profile-container">
      <!-- 左侧导航 -->
      <div class="profile-sidebar">
        <div class="user-info-card">
          <div class="avatar-section">
            <el-avatar :size="80" :src="userInfo?.avatar">
              {{ userInfo?.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <div class="user-basic">
              <div class="username">{{ userInfo?.nickname || userInfo?.username }}</div>
              <div class="user-level">
                <el-tag :type="levelInfo.type" size="small">{{ levelInfo.name }}</el-tag>
              </div>
            </div>
          </div>
          
          <div class="user-badges">
            <el-tag v-if="userInfo?.phone" type="success" size="small" effect="plain">
              <el-icon><Check /></el-icon>
              已绑定手机
            </el-tag>
            <el-tag v-else type="info" size="small" effect="plain">
              未绑定手机
            </el-tag>
          </div>
        </div>

        <el-menu
          :default-active="activeMenu"
          class="profile-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="overview">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="orders">
            <el-icon><ShoppingCart /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="addresses">
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
          </el-menu-item>
          <el-menu-item index="favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="history">
            <el-icon><Clock /></el-icon>
            <span>浏览历史</span>
          </el-menu-item>
          <el-menu-item index="reviews">
            <el-icon><Comment /></el-icon>
            <span>我的评价</span>
          </el-menu-item>
          <el-menu-item index="security">
            <el-icon><Lock /></el-icon>
            <span>安全设置</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容区 -->
      <div class="profile-content">
        <!-- 个人中心概览 -->
        <div v-if="activeMenu === 'overview'" class="overview-section">
          <!-- 待办事项 -->
          <el-card class="todo-card">
            <template #header>
              <h3>我的待办</h3>
            </template>
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="todo-item" @click="goToOrders('pending')">
                  <div class="todo-icon" style="background: #f56c6c;">
                    <el-icon :size="24"><Wallet /></el-icon>
                  </div>
                  <div class="todo-info">
                    <div class="todo-count">{{ todoStats.pendingPay }}</div>
                    <div class="todo-label">待支付</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="todo-item" @click="goToOrders('shipped')">
                  <div class="todo-icon" style="background: #409eff;">
                    <el-icon :size="24"><Box /></el-icon>
                  </div>
                  <div class="todo-info">
                    <div class="todo-count">{{ todoStats.pendingReceive }}</div>
                    <div class="todo-label">待收货</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="todo-item" @click="goToReviews">
                  <div class="todo-icon" style="background: #67c23a;">
                    <el-icon :size="24"><Edit /></el-icon>
                  </div>
                  <div class="todo-info">
                    <div class="todo-count">{{ todoStats.pendingReview }}</div>
                    <div class="todo-label">待评价</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="todo-item">
                  <div class="todo-icon" style="background: #e6a23c;">
                    <el-icon :size="24"><Service /></el-icon>
                  </div>
                  <div class="todo-info">
                    <div class="todo-count">0</div>
                    <div class="todo-label">售后服务</div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-card>

          <!-- 资产信息 -->
          <el-card class="assets-card">
            <template #header>
              <h3>我的资产</h3>
            </template>
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="asset-item">
                  <div class="asset-label">优惠券</div>
                  <div class="asset-value">{{ assets.coupons }} <span class="unit">张</span></div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="asset-item">
                  <div class="asset-label">积分</div>
                  <div class="asset-value">{{ assets.points }} <span class="unit">分</span></div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="asset-item">
                  <div class="asset-label">余额</div>
                  <div class="asset-value">¥{{ assets.balance.toFixed(2) }}</div>
                </div>
              </el-col>
            </el-row>
          </el-card>

          <!-- 最近订单 -->
          <el-card class="recent-orders-card">
            <template #header>
              <div class="card-header">
                <h3>最近订单</h3>
                <el-button text type="primary" @click="goToOrders('all')">
                  查看全部 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </template>
            <div v-if="recentOrders.length > 0">
              <div v-for="order in recentOrders" :key="order.id" class="order-item">
                <div class="order-header">
                  <span class="order-no">订单号：{{ order.orderNo }}</span>
                  <el-tag :type="getOrderStatusType(order.status)" size="small">
                    {{ order.statusText }}
                  </el-tag>
                </div>
                <div class="order-content">
                  <div class="order-products">
                    <div v-for="item in order.items.slice(0, 3)" :key="item.id" class="product-item">
                      <el-image
                        :src="item.image"
                        fit="cover"
                        style="width: 60px; height: 60px; border-radius: 4px;"
                      />
                    </div>
                    <span v-if="order.items.length > 3" class="more-products">
                      等{{ order.items.length }}件商品
                    </span>
                  </div>
                  <div class="order-amount">¥{{ order.totalAmount.toFixed(2) }}</div>
                  <div class="order-actions">
                    <el-button v-if="order.status === 'pending'" type="primary" size="small">
                      立即支付
                    </el-button>
                    <el-button v-if="order.status === 'shipped'" type="success" size="small">
                      确认收货
                    </el-button>
                    <el-button size="small" @click="goToOrderDetail(order.id)">
                      查看详情
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无订单" />
          </el-card>
        </div>

        <!-- 我的订单 -->
        <div v-if="activeMenu === 'orders'" class="orders-section">
          <el-card>
            <template #header>
              <div class="card-header">
                <h3>我的订单</h3>
                <el-button text type="primary" @click="$router.push('/orders')">
                  查看完整列表 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </template>
            <div v-if="allOrders.length > 0">
              <div v-for="order in allOrders" :key="order.id" class="order-item">
                <div class="order-header">
                  <span class="order-no">订单号：{{ order.orderNo }}</span>
                  <el-tag :type="getOrderStatusType(order.status)" size="small">
                    {{ order.statusText }}
                  </el-tag>
                </div>
                <div class="order-content">
                  <div class="order-products">
                    <div v-for="item in order.items.slice(0, 3)" :key="item.id" class="product-item">
                      <el-image
                        :src="item.image"
                        fit="cover"
                        style="width: 60px; height: 60px; border-radius: 4px;"
                      >
                        <template #error>
                          <div class="image-slot">
                            <el-icon><Picture /></el-icon>
                          </div>
                        </template>
                      </el-image>
                    </div>
                    <span v-if="order.items.length > 3" class="more-products">
                      等{{ order.items.length }}件商品
                    </span>
                  </div>
                  <div class="order-amount">¥{{ (order.totalAmount || 0).toFixed(2) }}</div>
                  <div class="order-actions">
                    <el-button v-if="order.status === 0" type="primary" size="small">
                      立即支付
                    </el-button>
                    <el-button v-if="order.status === 2" type="success" size="small">
                      确认收货
                    </el-button>
                    <el-button size="small" @click="goToOrderDetail(order.id)">
                      查看详情
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无订单" />
          </el-card>
        </div>

        <!-- 收货地址 -->
        <div v-if="activeMenu === 'addresses'" class="addresses-section">
          <AddressManage />
        </div>

        <!-- 我的收藏 -->
        <div v-if="activeMenu === 'favorites'" class="favorites-section">
          <FavoriteList />
        </div>

        <!-- 浏览历史 -->
        <div v-if="activeMenu === 'history'" class="history-section">
          <BrowsingHistory />
        </div>

        <!-- 我的评价 -->
        <div v-if="activeMenu === 'reviews'" class="reviews-section">
          <ReviewList />
        </div>

        <!-- 安全设置 -->
        <div v-if="activeMenu === 'security'" class="security-section">
          <SecuritySettings />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  ShoppingCart,
  Location,
  Star,
  Clock,
  Comment,
  Lock,
  Check,
  Wallet,
  Box,
  Edit,
  Service,
  ArrowRight,
  Picture
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import { useOrderStore } from '@/stores/orderStore'
import AddressManage from './AddressManage.vue'
import FavoriteList from './FavoriteList.vue'
import BrowsingHistory from './BrowsingHistory.vue'
import ReviewList from './ReviewList.vue'
import SecuritySettings from './SecuritySettings.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const orderStore = useOrderStore()

const activeMenu = ref('overview')
const recentOrders = ref([])
const allOrders = ref([])

const userInfo = computed(() => userStore.userInfo)

// 待办事项统计
const todoStats = reactive({
  pendingPay: 0,
  pendingReceive: 0,
  pendingReview: 0
})

// 资产信息
const assets = reactive({
  coupons: 0,
  points: 0,
  balance: 0
})

// 会员等级信息
const levelInfo = computed(() => {
  const points = assets.points
  if (points >= 5000) {
    return { name: '钻石会员', type: '' }
  } else if (points >= 2000) {
    return { name: '黄金会员', type: 'warning' }
  } else if (points >= 500) {
    return { name: '白银会员', type: 'info' }
  } else {
    return { name: '普通会员', type: 'info' }
  }
})

/**
 * 处理菜单选择
 */
const handleMenuSelect = (index) => {
  activeMenu.value = index
}

/**
 * 获取订单状态类型
 */
const getOrderStatusType = (status) => {
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
 * 跳转到订单列表
 */
const goToOrders = (status) => {
  if (status === 'all') {
    router.push('/orders')
  } else {
    router.push({ path: '/orders', query: { status } })
  }
}

/**
 * 跳转到订单详情
 */
const goToOrderDetail = (id) => {
  router.push(`/order/${id}`)
}

/**
 * 跳转到评价页面
 */
const goToReviews = () => {
  activeMenu.value = 'reviews'
}

/**
 * 加载最近订单
 */
const loadRecentOrders = async () => {
  try {
    const userId = userStore.userInfo?.id
    if (!userId) {
      console.warn('用户未登录，无法加载订单')
      return
    }

    // 使用orderStore加载订单，这样会经过数据转换
    await orderStore.fetchOrderList({ userId, page: 1, pageSize: 100 })
    
    // 从orderStore获取转换后的订单数据
    allOrders.value = orderStore.orderList
    recentOrders.value = orderStore.orderList.slice(0, 3)
    
    // 统计待办事项
    todoStats.pendingPay = allOrders.value.filter(o => o.status === 0).length
    todoStats.pendingReceive = allOrders.value.filter(o => o.status === 2).length
    todoStats.pendingReview = allOrders.value.filter(o => o.status === 3).length
  } catch (error) {
    console.error('加载订单失败:', error)
  }
}

onMounted(() => {
  // 从URL参数获取初始菜单
  const tab = route.query.tab
  if (tab) {
    activeMenu.value = tab
  }
  
  loadRecentOrders()
})
</script>

<style scoped>
.user-profile {
  min-height: calc(100vh - 60px);
  background: #f5f5f5;
  padding: 20px 0;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
}

/* 左侧导航 */
.profile-sidebar {
  width: 240px;
  flex-shrink: 0;
}

.user-info-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.user-basic {
  flex: 1;
}

.username {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.user-level {
  font-size: 12px;
}

.user-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.profile-menu {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: none;
}

.profile-menu .el-menu-item {
  height: 48px;
  line-height: 48px;
}

/* 右侧内容 */
.profile-content {
  flex: 1;
  min-width: 0;
}

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

/* 概览页面 */
.overview-section > * {
  margin-bottom: 20px;
}

.overview-section > *:last-child {
  margin-bottom: 0;
}

/* 待办事项 */
.todo-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.todo-item:hover {
  background: #f0f0f0;
  transform: translateY(-2px);
}

.todo-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.todo-info {
  flex: 1;
}

.todo-count {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  line-height: 1;
  margin-bottom: 5px;
}

.todo-label {
  font-size: 14px;
  color: #666;
}

/* 资产信息 */
.asset-item {
  text-align: center;
  padding: 20px 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.asset-label {
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.asset-value {
  font-size: 28px;
  font-weight: 600;
}

.asset-value .unit {
  font-size: 14px;
  font-weight: normal;
  margin-left: 4px;
}

/* 最近订单 */
.order-item {
  border-bottom: 1px solid #eee;
  padding: 15px 0;
}

.order-item:last-child {
  border-bottom: none;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-size: 14px;
  color: #666;
}

.order-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.order-products {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-item {
  flex-shrink: 0;
}

.more-products {
  font-size: 14px;
  color: #666;
}

.order-amount {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
  min-width: 100px;
  text-align: right;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 24px;
}
</style>

