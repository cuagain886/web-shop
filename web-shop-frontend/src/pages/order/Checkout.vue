<template>
  <div class="checkout-page">
    <div class="page-container">
      <h2 class="page-title">确认订单</h2>

      <!-- 收货地址 -->
      <div class="section address-section">
        <div class="section-header">
          <h3 class="section-title">收货地址</h3>
          <el-button type="primary" size="small" @click="showAddressDialog = true">
            选择其他地址
          </el-button>
        </div>

        <div v-if="selectedAddress" class="address-card">
          <div class="address-info">
            <span class="receiver-name">{{ selectedAddress.receiverName }}</span>
            <span class="receiver-phone">{{ selectedAddress.receiverPhone }}</span>
            <el-tag v-if="selectedAddress.isDefault" type="success" size="small">默认</el-tag>
          </div>
          <div class="address-detail">
            {{ selectedAddress.province }} {{ selectedAddress.city }} {{ selectedAddress.district }} {{ selectedAddress.detailAddress }}
          </div>
        </div>

        <el-empty v-else description="请添加收货地址">
          <el-button type="primary" @click="showAddressDialog = true">添加地址</el-button>
        </el-empty>
      </div>

      <!-- 商品列表 -->
      <div class="section goods-section">
        <h3 class="section-title">商品信息</h3>
        
        <div class="goods-list">
          <div v-for="item in orderItems" :key="item.id" class="goods-item">
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

      <!-- 支付方式 -->
      <div class="section payment-section">
        <h3 class="section-title">支付方式</h3>
        
        <el-radio-group v-model="paymentMethod" class="payment-methods">
          <el-radio value="wechat" class="payment-option">
            <div class="payment-label">
              <span class="payment-icon">💚</span>
              <span>微信支付</span>
            </div>
          </el-radio>
          <el-radio value="alipay" class="payment-option">
            <div class="payment-label">
              <span class="payment-icon">💙</span>
              <span>支付宝</span>
            </div>
          </el-radio>
        </el-radio-group>
      </div>

      <!-- 订单汇总 -->
      <div class="section summary-section">
        <div class="summary-row">
          <span class="summary-label">商品总价：</span>
          <span class="summary-value">¥{{ totalAmount.toFixed(2) }}</span>
        </div>
        <div class="summary-row">
          <span class="summary-label">运费：</span>
          <span class="summary-value">¥{{ freight.toFixed(2) }}</span>
        </div>
        <div class="summary-row total-row">
          <span class="summary-label">应付总额：</span>
          <span class="summary-value total-amount">¥{{ payAmount.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 提交订单 -->
      <div class="submit-section">
        <div class="submit-info">
          <span>应付总额：</span>
          <span class="total-price">¥{{ payAmount.toFixed(2) }}</span>
        </div>
        <el-button
          type="danger"
          size="large"
          :loading="submitting"
          @click="handleSubmitOrder"
        >
          提交订单
        </el-button>
      </div>
    </div>

    <!-- 地址选择对话框 -->
    <el-dialog
      v-model="showAddressDialog"
      title="选择收货地址"
      width="600px"
    >
      <div class="address-dialog-header">
        <el-button type="primary" size="small" @click="showAddAddressForm">
          <el-icon><Plus /></el-icon>
          新增地址
        </el-button>
      </div>

      <div class="address-list">
        <div
          v-for="addr in addressList"
          :key="addr.id"
          class="address-item"
          :class="{ selected: tempSelectedAddress?.id === addr.id }"
          @click="selectAddress(addr)"
        >
          <div class="address-item-header">
            <span class="receiver-name">{{ addr.receiverName }}</span>
            <span class="receiver-phone">{{ addr.receiverPhone }}</span>
            <el-tag v-if="addr.isDefault" type="success" size="small">默认</el-tag>
          </div>
          <div class="address-item-detail">
            {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAddress">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增地址对话框 -->
    <el-dialog
      v-model="showAddDialog"
      title="新增收货地址"
      width="500px"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="100px"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>

        <el-form-item label="手机号码" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号码" />
        </el-form-item>

        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>

        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>

        <el-form-item label="区/县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区/县" />
        </el-form-item>

        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="addressForm.detailAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>

        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleAddAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cartStore'
import { useOrderStore } from '@/stores/orderStore'
import { useUserStore } from '@/stores/userStore'
import { getAddressList, addAddress } from '@/api/address'
import { getSettings } from '@/api/settings'

const router = useRouter()
const cartStore = useCartStore()
const orderStore = useOrderStore()
const userStore = useUserStore()

// 数据状态
const addressList = ref([])
const selectedAddress = ref(null)
const tempSelectedAddress = ref(null)
const paymentMethod = ref('wechat')
const showAddressDialog = ref(false)
const showAddDialog = ref(false)
const submitting = ref(false)
const saving = ref(false)
const addressFormRef = ref(null)

// 订单商品列表（从购物车或立即购买获取）
const orderItems = ref([])

// 运费
const freight = ref(0)

// 商品总金额
const totalAmount = computed(() => {
  const amount = orderItems.value.reduce((sum, item) => {
    const itemPrice = parseFloat(item.price) || 0
    const itemQuantity = parseInt(item.quantity) || 1
    return sum + (itemPrice * itemQuantity)
  }, 0)
  console.log('💰 计算商品总金额:', { items: orderItems.value, totalAmount: amount })
  return amount
})

// 应付总额（商品总价 + 运费）
const payAmount = computed(() => {
  const total = totalAmount.value + (parseFloat(freight.value) || 0)
  console.log('💰 计算应付总额:', { totalAmount: totalAmount.value, freight: freight.value, payAmount: total })
  return total
})

// 新增地址表单
const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

// 表单验证规则
const addressRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区/县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

/**
 * 获取地址列表
 */
const fetchAddressList = async () => {
  try {
    const addresses = await getAddressList()
    addressList.value = addresses || []
    
    // 如果没有选中地址，自动选择默认地址
    if (!selectedAddress.value && addresses && addresses.length > 0) {
      const defaultAddr = addresses.find(addr => addr.isDefault)
      selectedAddress.value = defaultAddr || addresses[0]
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
    ElMessage.error('获取地址列表失败')
  }
}

/**
 * 选择地址
 */
const selectAddress = (address) => {
  tempSelectedAddress.value = address
}

/**
 * 确认地址选择
 */
const confirmAddress = () => {
  if (tempSelectedAddress.value) {
    selectedAddress.value = tempSelectedAddress.value
    showAddressDialog.value = false
    ElMessage.success('地址已更新')
  } else {
    ElMessage.warning('请选择一个地址')
  }
}

/**
 * 显示新增地址表单
 */
const showAddAddressForm = () => {
  // 重置表单
  Object.assign(addressForm, {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  })
  showAddDialog.value = true
}

/**
 * 新增地址
 */
const handleAddAddress = async () => {
  try {
    await addressFormRef.value.validate()
    
    saving.value = true
    
    const userId = userStore.userInfo?.id || userStore.userInfo?.userId
    const newAddress = await addAddress({
      ...addressForm,
      userId,
      isDefault: addressForm.isDefault ? 1 : 0
    })
    
    ElMessage.success('地址添加成功')
    showAddDialog.value = false
    
    // 刷新地址列表
    await fetchAddressList()
    
    // 自动选择新添加的地址
    if (newAddress) {
      selectedAddress.value = newAddress
      tempSelectedAddress.value = newAddress
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('添加地址失败:', error)
      ElMessage.error('添加地址失败')
    }
  } finally {
    saving.value = false
  }
}

/**
 * 提交订单
 */
const handleSubmitOrder = async () => {
  // 校验
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  if (orderItems.value.length === 0) {
    ElMessage.warning('购物车中没有选中的商品')
    router.push('/cart')
    return
  }

  // 确认提交
  try {
    await ElMessageBox.confirm('确认提交订单？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    submitting.value = true

    const userId = userStore.userInfo?.id || userStore.userInfo?.userId
    
    // 准备订单数据
    const orderData = {
      userId,
      addressId: selectedAddress.value.id,
      items: orderItems.value.map(item => ({
        productId: item.productId,
        skuId: item.skuId,
        quantity: item.quantity
      })),
      note: ''
    }

    // 创建订单
    const order = await orderStore.createOrder(orderData)
    
    ElMessage.success('订单创建成功')
    
    // 清空购物车中已结算的商品
    const itemIds = orderItems.value.map(item => item.id)
    await cartStore.batchDelete(itemIds)
    
    // 跳转到订单详情页（用户可在详情页手动支付）
    router.push(`/order/${order.id}`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交订单失败:', error)
      ElMessage.error(error.message || '提交订单失败')
    }
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({
      path: '/login',
      query: { redirect: '/checkout' }
    })
    return
  }

  // 检查是否是立即购买
  const buyNowItem = sessionStorage.getItem('buyNowItem')
  if (buyNowItem) {
    // 立即购买场景
    try {
      const item = JSON.parse(buyNowItem)
      orderItems.value = [{
        id: item.productId,
        productId: item.productId,
        skuId: item.skuId,
        productName: item.name,
        image: item.image,
        specs: JSON.stringify(item.specs),
        price: parseFloat(item.price) || 0,
        quantity: parseInt(item.quantity) || 1
      }]
      console.log('📦 立即购买商品信息:', orderItems.value)
      // 清除sessionStorage
      sessionStorage.removeItem('buyNowItem')
    } catch (error) {
      console.error('解析立即购买商品失败:', error)
      ElMessage.error('商品信息错误')
      router.push('/')
      return
    }
  } else {
    // 购物车结算场景
    if (cartStore.checkedItems.length === 0) {
      ElMessage.warning('请先选择要结算的商品')
      router.push('/cart')
      return
    }
    // 确保价格和数量是正确的数值类型
    orderItems.value = cartStore.checkedItems.map(item => ({
      ...item,
      price: parseFloat(item.price) || 0,
      quantity: parseInt(item.quantity) || 1
    }))
    console.log('📦 购物车结算商品信息:', orderItems.value)
  }

  // 获取地址列表
  await fetchAddressList()
  
  // 获取系统设置（运费）
  try {
    const settings = await getSettings()
    freight.value = parseFloat(settings.defaultShipping) || 0
    console.log('📦 系统运费:', freight.value)
  } catch (error) {
    console.error('获取系统设置失败:', error)
  }
})
</script>

<style scoped>
.checkout-page {
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

.section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 15px 0;
}

/* 地址部分 */
.address-card {
  border: 2px solid #409eff;
  border-radius: 8px;
  padding: 15px;
  background-color: #f0f9ff;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.receiver-phone {
  color: #666;
}

.address-detail {
  color: #666;
  line-height: 1.6;
}

/* 商品列表 */
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

/* 支付方式 */
.payment-methods {
  display: flex;
  gap: 20px;
}

.payment-option {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-option:hover {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.payment-label {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
}

.payment-icon {
  font-size: 24px;
}

/* 订单汇总 */
.summary-section {
  text-align: right;
}

.summary-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
}

.summary-label {
  color: #666;
  margin-right: 20px;
}

.summary-value {
  color: #333;
  min-width: 100px;
}

.total-row {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
  font-size: 18px;
  font-weight: 600;
}

.total-amount {
  color: #ff4d4f;
  font-size: 24px;
}

/* 提交订单 */
.submit-section {
  position: sticky;
  bottom: 0;
  background: white;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 30px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.submit-info {
  font-size: 16px;
  color: #666;
}

.total-price {
  color: #ff4d4f;
  font-size: 28px;
  font-weight: 700;
  margin-left: 10px;
}

/* 地址选择对话框 */
.address-list {
  max-height: 400px;
  overflow-y: auto;
}

.address-item {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.address-item.selected {
  border-color: #409eff;
  background-color: #f0f9ff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.address-item-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 8px;
}

.address-item-detail {
  color: #666;
  font-size: 14px;
}

.address-dialog-header {
  margin-bottom: 15px;
  display: flex;
  justify-content: flex-end;
}
</style>

