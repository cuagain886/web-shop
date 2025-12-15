<template>
  <div class="cart-page">
    <div class="container">
      <h1 class="page-title">购物车</h1>

      <!-- 空购物车提示 -->
      <div v-if="cartStore.cartList.length === 0" class="empty-cart">
        <el-empty description="购物车空空如也">
          <el-button type="primary" @click="goToHome">去逛逛</el-button>
        </el-empty>
      </div>

      <!-- 购物车列表 -->
      <div v-else class="cart-content">
        <!-- 表头 -->
        <div class="cart-header">
          <el-checkbox 
            v-model="selectAll" 
            @change="handleSelectAll"
            class="header-checkbox"
          >
            全选
          </el-checkbox>
          <span class="header-product">商品信息</span>
          <span class="header-price">单价</span>
          <span class="header-quantity">数量</span>
          <span class="header-total">小计</span>
          <span class="header-action">操作</span>
        </div>

        <!-- 商品列表 -->
        <div class="cart-list">
          <div 
            v-for="item in cartStore.cartList" 
            :key="item.id" 
            class="cart-item"
          >
            <el-checkbox 
              :model-value="item.checked" 
              @change="handleCheckItem(item.id, $event)"
              class="item-checkbox"
            />
            
            <!-- 商品信息 -->
            <div class="item-product">
              <img :src="item.image" :alt="item.name" class="product-image">
              <div class="product-info">
                <div class="product-name">{{ item.name }}</div>
                <div class="product-specs" v-if="item.specs && Object.keys(item.specs).length">
                  <span v-for="(value, key) in item.specs" :key="key" class="spec-tag">
                    {{ key }}: {{ value }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 单价 -->
            <div class="item-price">
              <span class="price">¥{{ item.price }}</span>
            </div>

            <!-- 数量 -->
            <div class="item-quantity">
              <el-input-number
                :model-value="item.quantity"
                @change="handleQuantityChange(item.id, $event)"
                :min="1"
                :max="item.stock"
                size="small"
              />
              <div class="stock-tip" v-if="item.stock < 10">
                仅剩{{ item.stock }}件
              </div>
            </div>

            <!-- 小计 -->
            <div class="item-total">
              <span class="total-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>

            <!-- 操作 -->
            <div class="item-action">
              <el-button 
                type="danger" 
                text 
                @click="handleDelete(item.id)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 底部结算栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox 
              v-model="selectAll" 
              @change="handleSelectAll"
            >
              全选
            </el-checkbox>
            <el-button 
              text 
              type="danger" 
              @click="handleBatchDelete"
              :disabled="cartStore.checkedItems.length === 0"
            >
              删除选中商品
            </el-button>
          </div>

          <div class="footer-right">
            <div class="selected-info">
              已选择 <span class="highlight">{{ cartStore.checkedCount }}</span> 件商品
            </div>
            <div class="total-info">
              <span class="label">合计：</span>
              <span class="total-price">¥{{ cartStore.checkedTotalPrice.toFixed(2) }}</span>
            </div>
            <el-button 
              type="danger" 
              size="large" 
              class="checkout-btn"
              @click="handleCheckout"
              :disabled="cartStore.checkedItems.length === 0"
            >
              去结算
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cartStore'
import { useUserStore } from '@/stores/userStore'

console.log('购物车页面加载')

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

// 全选状态
const selectAll = computed({
  get: () => cartStore.isAllChecked,
  set: () => {}
})

// 处理全选
const handleSelectAll = (checked) => {
  cartStore.toggleAll(checked)
}

// 处理单个商品选中
const handleCheckItem = (id, checked) => {
  cartStore.toggleItem(id, checked)
}

// 处理数量变化
const handleQuantityChange = async (id, quantity) => {
  try {
    await cartStore.updateQuantity(id, quantity)
  } catch (error) {
    ElMessage.error('更新数量失败')
  }
}

// 删除单个商品
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该商品吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await cartStore.deleteItem(id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (cartStore.checkedItems.length === 0) {
    ElMessage.warning('请先选择要删除的商品')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${cartStore.checkedItems.length} 件商品吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const ids = cartStore.checkedItems.map(item => item.id)
    await cartStore.batchDelete(ids)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 去结算
const handleCheckout = () => {
  if (cartStore.checkedItems.length === 0) {
    ElMessage.warning('请先选择要结算的商品')
    return
  }

  // 检查是否登录
  if (!userStore.isLoggedIn) {
    ElMessageBox.confirm(
      '请先登录后再进行结算',
      '提示',
      {
        confirmButtonText: '去登录',
        cancelButtonText: '取消',
        type: 'info'
      }
    ).then(() => {
      // 跳转到登录页，并记录来源页面（登录后返回结算页）
      router.push({
        path: '/login',
        query: { redirect: '/checkout' }
      })
    }).catch(() => {})
    return
  }

  // 跳转到订单确认页
  console.log('结算商品:', cartStore.checkedItems)
  router.push('/checkout')
}

// 返回首页
const goToHome = () => {
  router.push('/')
}

// 加载购物车数据
onMounted(async () => {
  try {
    await cartStore.fetchCartList()
    console.log('购物车数据加载完成')
  } catch (error) {
    console.error('加载购物车数据失败:', error)
  }
})
</script>

<style scoped>
.cart-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding: 20px 0 50px;
}

.container {
  width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

/* 空购物车 */
.empty-cart {
  background: #fff;
  padding: 80px 20px;
  text-align: center;
  border-radius: 4px;
}

/* 购物车内容 */
.cart-content {
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
}

/* 表头 */
.cart-header {
  display: grid;
  grid-template-columns: 50px 1fr 120px 150px 120px 80px;
  align-items: center;
  padding: 15px 20px;
  background: #f5f5f5;
  border-bottom: 2px solid #e5e5e5;
  font-weight: 500;
  color: #666;
}

.header-checkbox {
  justify-self: center;
}

.header-product,
.header-price,
.header-quantity,
.header-total,
.header-action {
  text-align: center;
}

.header-product {
  text-align: left;
  padding-left: 20px;
}

/* 商品列表 */
.cart-list {
  padding: 0;
}

.cart-item {
  display: grid;
  grid-template-columns: 50px 1fr 120px 150px 120px 80px;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.3s;
}

.cart-item:hover {
  background: #fafafa;
}

.item-checkbox {
  justify-self: center;
}

/* 商品信息 */
.item-product {
  display: flex;
  gap: 15px;
  align-items: center;
  padding-left: 20px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #e5e5e5;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.product-specs {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.spec-tag {
  font-size: 12px;
  color: #666;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 2px;
}

/* 单价 */
.item-price {
  text-align: center;
}

.price {
  font-size: 16px;
  color: #333;
}

/* 数量 */
.item-quantity {
  text-align: center;
}

.stock-tip {
  font-size: 12px;
  color: #e6a23c;
  margin-top: 5px;
}

/* 小计 */
.item-total {
  text-align: center;
}

.total-price {
  font-size: 18px;
  color: #e4393c;
  font-weight: bold;
}

/* 操作 */
.item-action {
  text-align: center;
}

/* 底部结算栏 */
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-top: 2px solid #e5e5e5;
  background: #fff;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.selected-info {
  font-size: 14px;
  color: #666;
}

.selected-info .highlight {
  color: #e4393c;
  font-weight: bold;
  font-size: 18px;
  margin: 0 3px;
}

.total-info {
  font-size: 14px;
  color: #666;
}

.total-info .label {
  margin-right: 5px;
}

.total-info .total-price {
  font-size: 24px;
  color: #e4393c;
  font-weight: bold;
}

.checkout-btn {
  width: 120px;
  height: 50px;
  font-size: 16px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .container {
    width: 100%;
  }

  .cart-header,
  .cart-item {
    grid-template-columns: 40px 1fr 100px 120px 100px 60px;
    padding: 15px;
  }

  .product-image {
    width: 60px;
    height: 60px;
  }

  .cart-footer {
    flex-direction: column;
    gap: 15px;
  }

  .footer-left,
  .footer-right {
    width: 100%;
    justify-content: space-between;
  }

  .checkout-btn {
    width: 100%;
  }
}
</style>

