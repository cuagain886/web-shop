<template>
  <div class="product-detail">
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <div class="container">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>商品详情</el-breadcrumb-item>
          <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!-- 商品主体信息 -->
    <div class="product-main" v-if="product.id">
      <div class="container">
        <div class="product-info-wrap">
          <!-- 左侧：商品图片 -->
          <div class="product-gallery">
            <div class="main-image">
              <img :src="currentImage" :alt="product.name">
            </div>
            <div class="image-list">
              <div 
                v-for="(img, index) in product.images" 
                :key="index"
                class="image-item"
                :class="{ active: currentImageIndex === index }"
                @click="selectImage(index)"
              >
                <img :src="img" :alt="product.name">
              </div>
            </div>
          </div>

          <!-- 右侧：商品信息 -->
          <div class="product-info">
            <h1 class="product-title">{{ product.name }}</h1>
            <div class="product-subtitle">{{ product.subtitle }}</div>

            <!-- 价格区域 -->
            <div class="price-box">
              <div class="price-row">
                <span class="label">价格</span>
                <div class="price-info">
                  <span class="current-price">¥{{ selectedSpec.price || product.price }}</span>
                  <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                  <span class="discount-tag" v-if="product.discount">{{ product.discount }}</span>
                </div>
              </div>
              <div class="promo-info" v-if="product.promos && product.promos.length">
                <span class="label">促销</span>
                <div class="promo-list">
                  <el-tag 
                    v-for="(promo, index) in product.promos" 
                    :key="index"
                    type="danger" 
                    size="small"
                    class="promo-tag"
                  >
                    {{ promo }}
                  </el-tag>
                </div>
              </div>
            </div>

            <!-- 规格选择 -->
            <div class="spec-box" v-if="product.specs && product.specs.length">
              <div 
                v-for="specGroup in product.specs" 
                :key="specGroup.name"
                class="spec-group"
              >
                <div class="spec-label">{{ specGroup.name }}</div>
                <div class="spec-options">
                  <div
                    v-for="option in specGroup.options"
                    :key="option.value"
                    class="spec-option"
                    :class="{ 
                      active: isSpecSelected(specGroup.name, option.value),
                      disabled: !option.stock || option.stock === 0
                    }"
                    @click="selectSpec(specGroup.name, option.value, option)"
                  >
                    {{ option.label }}
                    <span v-if="!option.stock || option.stock === 0" class="no-stock">无货</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 库存和数量 -->
            <div class="stock-box">
              <div class="stock-row">
                <span class="label">库存</span>
                <span class="stock-num" :class="{ low: currentStock < 10 }">
                  {{ stockDisplayText }}
                  <span v-if="currentStock < 10 && currentStock > 0" class="low-tip">（库存紧张）</span>
                </span>
              </div>
              <div class="quantity-row">
                <span class="label">数量</span>
                <el-input-number 
                  v-model="quantity" 
                  :min="1" 
                  :max="currentStock"
                  :disabled="currentStock === 0"
                />
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="action-box">
              <el-button
                type="danger"
                size="large"
                class="buy-btn"
                :disabled="currentStock === 0 || !allSpecsSelected"
                @click="handleBuyNow"
              >
                立即购买
              </el-button>
              <el-button
                type="warning"
                size="large"
                class="cart-btn"
                :disabled="currentStock === 0 || !allSpecsSelected"
                @click="handleAddToCart"
              >
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button
                :type="isFavorited ? 'danger' : 'default'"
                size="large"
                class="favorite-btn"
                @click="handleToggleFavorite"
              >
                <el-icon><Star :style="{ color: isFavorited ? '#e4393c' : '#999' }" /></el-icon>
                {{ isFavorited ? '已收藏' : '收藏' }}
              </el-button>
            </div>

            <!-- 提示信息 -->
            <div class="tips" v-if="!allSpecsSelected">
              <el-alert
                title="请选择完整的商品规格"
                type="warning"
                :closable="false"
              />
            </div>

            <!-- 服务承诺 -->
            <div class="service-box">
              <div class="service-item">
                <el-icon><Select /></el-icon>
                <span>正品保证</span>
              </div>
              <div class="service-item">
                <el-icon><Van /></el-icon>
                <span>极速物流</span>
              </div>
              <div class="service-item">
                <el-icon><RefreshRight /></el-icon>
                <span>7天无理由退换</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品详情 -->
    <div class="product-detail-content">
      <div class="container">
        <el-tabs v-model="activeTab" class="detail-tabs">
          <el-tab-pane label="商品评价" name="reviews">
            <div class="reviews-section">
              <!-- 评价表单 - 仅购买过商品的用户可见 -->
              <div v-if="canReview" class="review-form-section">
                <h4>发表评价</h4>
                <el-form :model="reviewForm" label-width="80px">
                  <el-form-item label="评分">
                    <el-rate v-model="reviewForm.rating" show-text :texts="['很差', '较差', '一般', '满意', '非常满意']" />
                  </el-form-item>
                  <el-form-item label="评价内容">
                    <el-input
                      v-model="reviewForm.content"
                      type="textarea"
                      :rows="4"
                      placeholder="请分享您的使用体验..."
                      maxlength="500"
                      show-word-limit
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handleSubmitReview" :loading="submittingReview">
                      提交评价
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>
              <div v-else-if="userStore.userInfo?.id && !canReview" class="review-tip">
                <el-alert
                  title="购买并确认收货后才能评价该商品"
                  type="info"
                  :closable="false"
                  show-icon
                />
              </div>
              <div v-else class="review-tip">
                <el-alert
                  title="请登录后查看是否可以评价"
                  type="info"
                  :closable="false"
                  show-icon
                />
              </div>

              <!-- 评价列表 -->
              <div class="reviews-list">
                <h4>全部评价 ({{ reviews.length }})</h4>
                <div v-if="reviews.length === 0" class="empty-reviews">
                  <el-empty description="暂无评价" />
                </div>
                <div v-else>
                  <div v-for="review in reviews" :key="review.id" class="review-item">
                    <div class="review-header">
                      <span class="review-user">{{ review.user?.nickname || review.user?.username || '匿名用户' }}</span>
                      <el-rate v-model="review.rating" disabled size="small" />
                      <span class="review-time">{{ formatTime(review.createdTime) }}</span>
                    </div>
                    <div class="review-content">{{ review.content }}</div>
                    <div v-if="review.reply" class="review-reply">
                      <span class="reply-label">商家回复：</span>
                      <span>{{ review.reply }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="商品详情" name="detail">
            <div class="detail-section">
              <h3>商品介绍</h3>
              <div class="detail-text" v-html="product.description"></div>
              
              <!-- 商品详情图片 -->
              <div class="detail-images" v-if="product.detailImages">
                <img 
                  v-for="(img, index) in product.detailImages" 
                  :key="index"
                  :src="img" 
                  :alt="`详情图${index + 1}`"
                >
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="规格参数" name="params">
            <div class="params-section">
              <table class="params-table">
                <tr v-for="param in product.parameters" :key="param.name">
                  <td class="param-name">{{ param.name }}</td>
                  <td class="param-value">{{ param.value }}</td>
                </tr>
              </table>
            </div>
          </el-tab-pane>

          <el-tab-pane label="售后保障" name="service">
            <div class="service-section">
              <h3>售后服务</h3>
              <ul class="service-list">
                <li>本产品全国联保，享受三包服务</li>
                <li>如因质量问题或故障，凭厂商维修中心或特约维修点的质量检测证明</li>
                <li>自收到商品之日起7日内可退货，15日内可换货</li>
                <li>正品保证，假一赔十</li>
              </ul>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Select, Van, RefreshRight, Star } from '@element-plus/icons-vue'
import { getProductDetail, addFavorite, removeFavorite, checkFavorite, getProductReviews, checkPurchased, addProductReview } from '@/api/product'
import { recordBrowsing } from '@/api/history'
import { useCartStore } from '@/stores/cartStore'
import { useUserStore } from '@/stores/userStore'

console.log('商品详情页加载')

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

// 商品数据
const product = ref({
  id: null,
  name: '',
  subtitle: '',
  price: 0,
  originalPrice: 0,
  discount: '',
  images: [],
  specs: [],
  stock: 0,
  description: '',
  detailImages: [],
  parameters: [],
  promos: []
})

// 当前选中的图片
const currentImageIndex = ref(0)
const currentImage = computed(() => {
  if (product.value.images && product.value.images.length > 0) {
    return product.value.images[currentImageIndex.value] || product.value.images[0]
  }
  return ''
})

// 选中的规格
const selectedSpecs = ref({})

// 当前选中规格的信息
const selectedSpec = ref({})

// 存储SKU数据供查询使用
const skuMap = ref(new Map())

// 购买数量
const quantity = ref(1)

// 当前标签页
const activeTab = ref('detail')

// 收藏状态
const isFavorited = ref(false)

// 评价列表
const reviews = ref([])

// 是否可以评价
const canReview = ref(false)
const orderItemId = ref(null)
const submittingReview = ref(false)

// 评价表单
const reviewForm = reactive({
  rating: 5,
  content: ''
})

// 当前库存
const currentStock = computed(() => {
  // 如果已选择完整的规格，显示该SKU的库存
  if (allSpecsSelected.value && selectedSpec.value.stock !== undefined) {
    return selectedSpec.value.stock || 0
  }
  // 否则显示商品总库存
  return product.value.stock || 0
})

// 库存显示文本
const stockDisplayText = computed(() => {
  const stock = currentStock.value
  if (allSpecsSelected.value) {
    return `${stock} 件`
  }
  return `${stock} 件`
})

// 检查是否选择了所有规格
const allSpecsSelected = computed(() => {
  if (!product.value.specs || product.value.specs.length === 0) {
    return true // 无规格商品默认已选择
  }
  return product.value.specs.every(spec => selectedSpecs.value[spec.name])
})

// 选择图片
const selectImage = (index) => {
  currentImageIndex.value = index
  console.log('切换图片:', index)
}

// 判断规格是否选中
const isSpecSelected = (specName, value) => {
  return selectedSpecs.value[specName] === value
}

// 选择规格
const selectSpec = (specName, value, option) => {
   // 如果没有SKU数据，允许选择但使用商品总库存
   const hasSkuData = product.value.skus && product.value.skus.length > 0
   
   if (hasSkuData && (!option.stock || option.stock === 0)) {
     ElMessage.warning('该规格暂无库存')
     return
   }

   selectedSpecs.value[specName] = value
   
   // 如果所有规格都已选择
   if (allSpecsSelected.value) {
     if (hasSkuData) {
       const sku = findSkuBySpecs(selectedSpecs.value)
       if (sku) {
         selectedSpec.value = {
           stock: sku.stock,
           price: sku.price,
           skuId: sku.id,
           skuCode: sku.skuCode
         }
       } else {
         selectedSpec.value = {
           stock: option.stock || product.value.stock,
           price: option.price || product.value.price,
           skuId: option.skus && option.skus.length > 0 ? option.skus[0].id : null
         }
       }
     } else {
       // 没有SKU数据，使用商品总库存
       selectedSpec.value = {
         stock: product.value.stock,
         price: product.value.price,
         skuId: null
       }
     }
   } else {
     selectedSpec.value = {
       stock: hasSkuData ? (option.stock || 0) : product.value.stock,
       price: option.price || product.value.price,
       skuId: option.skus && option.skus.length > 0 ? option.skus[0].id : null
     }
   }
}

// 根据规格查找对应的SKU
const findSkuBySpecs = (specs) => {
  console.log('根据规格查找SKU:', specs)
  
  // 遍历所有SKU，找到attributes匹配的SKU
  const skus = product.value.skus || []
  for (const sku of skus) {
    const skuAttrs = typeof sku.attributes === 'string' ? JSON.parse(sku.attributes) : sku.attributes
    console.log(`检查SKU ${sku.id}的attributes:`, skuAttrs)
    
    // 检查所有规格是否都匹配
    let allMatch = true
    for (const [specName, specValue] of Object.entries(specs)) {
      if (skuAttrs[specName] !== specValue) {
        allMatch = false
        break
      }
    }
    
    if (allMatch) {
      console.log(`SKU ${sku.id}的所有规格都匹配`)
      return sku
    }
  }
  
  console.log('未找到匹配的SKU')
  return null
}

// 加入购物车
const handleAddToCart = async () => {
   if (!allSpecsSelected.value) {
     ElMessage.warning('请选择完整的商品规格')
     return
   }

   if (currentStock.value === 0) {
     ElMessage.error('商品暂无库存')
     return
   }

   try {
     // 调用购物车API
     await cartStore.addToCart({
       productId: product.value.id,
       skuId: selectedSpec.value.skuId,
       name: product.value.name,
       price: selectedSpec.value.price || product.value.price,
       image: product.value.images && product.value.images.length > 0 ? product.value.images[0] : '',
       specs: selectedSpecs.value,
       quantity: quantity.value,
       stock: currentStock.value
     })

     ElMessage.success('已加入购物车')
     console.log('加入购物车成功')

     // 更新购物车数量显示
     await cartStore.updateCartCount()
   } catch (error) {
     console.error('加入购物车失败:', error)
     ElMessage.error('加入购物车失败')
   }
}

// 立即购买
const handleBuyNow = () => {
   if (!allSpecsSelected.value) {
     ElMessage.warning('请选择完整的商品规格')
     return
   }

   if (currentStock.value === 0) {
     ElMessage.error('商品暂无库存')
     return
   }

   // 构建订单商品数据
   const orderItem = {
     productId: product.value.id,
     skuId: selectedSpec.value.skuId,
     name: product.value.name,
     price: selectedSpec.value.price || product.value.price,
     image: product.value.images && product.value.images.length > 0 ? product.value.images[0] : '',
     specs: selectedSpecs.value,
     quantity: quantity.value,
     stock: currentStock.value
   }

   // 将商品信息存储到sessionStorage，供订单确认页使用
   sessionStorage.setItem('buyNowItem', JSON.stringify(orderItem))

   // 跳转到订单确认页
   router.push('/checkout')
}

// 加载商品详情
const loadProductDetail = async () => {
  const productId = route.params.id
  console.log('加载商品详情, ID:', productId)

  try {
    const data = await getProductDetail(productId)
    console.log('后端返回的完整数据:', data)
    console.log('SKU数据:', data.skus)
    
    // 解析images字段
    let images = []
    if (data.images) {
      images = typeof data.images === 'string' ? JSON.parse(data.images) : data.images
    }
    
    // 解析specs字段并转换为UI需要的格式
    let specs = []
    if (data.specs) {
      const specsData = typeof data.specs === 'string' ? JSON.parse(data.specs) : data.specs
      // 转换格式：[{name: '颜色', values: ['黑色', '白色']}]
      // => [{name: '颜色', options: [{label: '黑色', value: '黑色', skus: [{id, stock, price, ...}]}, ...]}]
      specs = specsData.map(spec => ({
        name: spec.name,
        options: spec.values.map(value => ({
          label: value,
          value: value,
          skus: [] // 存储所有匹配该规格值的SKU
        }))
      }))
    }
    
    // 如果有SKU数据，将SKU信息关联到对应的规格选项
    if (data.skus && data.skus.length > 0) {
      console.log('开始处理SKU数据，共', data.skus.length, '个SKU')
      data.skus.forEach((sku, skuIndex) => {
        console.log(`SKU ${skuIndex}:`, sku)
        // 解析SKU的attributes
        const skuAttrs = typeof sku.attributes === 'string' ? JSON.parse(sku.attributes) : sku.attributes
        console.log(`SKU ${skuIndex} attributes:`, skuAttrs)
        
        // 为每个规格组找到对应的SKU选项，并将SKU信息添加到skus数组
        specs.forEach(specGroup => {
          const attrValue = skuAttrs[specGroup.name]
          console.log(`查找规格 ${specGroup.name} = ${attrValue}`)
          if (attrValue) {
            const option = specGroup.options.find(opt => opt.value === attrValue)
            if (option) {
              console.log(`找到匹配的选项，添加SKU信息`)
              // 将SKU信息添加到该规格选项的skus数组
              option.skus.push({
                id: sku.id,
                stock: sku.stock || 0,
                price: sku.price || data.price,
                skuCode: sku.skuCode,
                attributes: skuAttrs
              })
            } else {
              console.log(`未找到匹配的选项，规格值: ${attrValue}`)
            }
          }
        })
      })
      
      // 计算每个规格选项的总库存（所有匹配SKU的库存之和）
      specs.forEach(specGroup => {
        specGroup.options.forEach(option => {
          if (option.skus && option.skus.length > 0) {
            option.stock = option.skus.reduce((sum, sku) => sum + sku.stock, 0)
            option.price = option.skus[0].price // 使用第一个SKU的价格
            console.log(`规格选项 ${option.value} 的总库存: ${option.stock}`)
          } else {
            option.stock = data.stock || 0
            option.price = data.price
          }
        })
      })
    }
    
    product.value = {
      ...data,
      images: images,
      specs: specs
    }
    console.log('商品数据加载成功:', product.value)
    
    // 记录浏览历史
    const userId = userStore.userInfo?.id
    if (userId) {
      try {
        await recordBrowsing(userId, productId)
        console.log('浏览历史已记录')
      } catch (error) {
        console.error('记录浏览历史失败:', error)
      }
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败')
  }
}

// 检查收藏状态
const checkFavoriteStatus = async () => {
  const productId = route.params.id
  const userId = userStore.userInfo?.id
  
  if (!userId) {
    console.log('用户未登录，跳过收藏状态检查')
    return
  }

  try {
    const result = await checkFavorite(productId)
    isFavorited.value = result.isFavorited || false
    console.log('收藏状态检查完成:', isFavorited.value)
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 添加收藏
const handleAddFavorite = async () => {
  const userId = userStore.userInfo?.id
  
  if (!userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await addFavorite(product.value.id)
    isFavorited.value = true
    ElMessage.success('已添加到收藏')
    console.log('添加收藏成功')
  } catch (error) {
    console.error('添加收藏失败:', error)
    ElMessage.error('添加收藏失败')
  }
}

// 取消收藏
const handleRemoveFavorite = async () => {
  const userId = userStore.userInfo?.id
  
  if (!userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await removeFavorite(product.value.id)
    isFavorited.value = false
    ElMessage.success('已取消收藏')
    console.log('取消收藏成功')
  } catch (error) {
    console.error('取消收藏失败:', error)
    ElMessage.error('取消收藏失败')
  }
}

// 切换收藏状态
const handleToggleFavorite = async () => {
  if (isFavorited.value) {
    await handleRemoveFavorite()
  } else {
    await handleAddFavorite()
  }
}

// 加载商品评价
const loadReviews = async () => {
  const productId = route.params.id
  try {
    const data = await getProductReviews(productId, { pageNum: 1, pageSize: 10 })
    reviews.value = data.records || data || []
  } catch (error) {
    console.error('加载评价失败:', error)
  }
}

// 检查用户是否可以评价
const checkCanReview = async () => {
  const productId = route.params.id
  const userId = userStore.userInfo?.id
  
  if (!userId) {
    canReview.value = false
    return
  }

  try {
    const result = await checkPurchased(productId, userId)
    canReview.value = result.hasPurchased || false
    orderItemId.value = result.orderItemId || null
    console.log('检查评价权限:', result)
  } catch (error) {
    console.error('检查评价权限失败:', error)
    canReview.value = false
  }
}

// 提交评价
const handleSubmitReview = async () => {
  if (!reviewForm.content || reviewForm.content.trim() === '') {
    ElMessage.warning('请输入评价内容')
    return
  }

  if (!orderItemId.value) {
    ElMessage.error('无法获取订单信息，请刷新页面重试')
    return
  }

  submittingReview.value = true
  try {
    await addProductReview({
      userId: userStore.userInfo.id,
      productId: product.value.id,
      orderItemId: orderItemId.value,
      rating: reviewForm.rating,
      content: reviewForm.content.trim()
    })
    
    ElMessage.success('评价提交成功')
    
    // 重置表单
    reviewForm.rating = 5
    reviewForm.content = ''
    
    // 刷新评价列表和评价权限
    await loadReviews()
    await checkCanReview()
  } catch (error) {
    console.error('提交评价失败:', error)
    ElMessage.error(error.message || '提交评价失败')
  } finally {
    submittingReview.value = false
  }
}

// 格式化时间
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
  loadProductDetail()
  checkFavoriteStatus()
  loadReviews()
  checkCanReview()
})
</script>

<style scoped>
.product-detail {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 50px;
}

.container {
  width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 面包屑 */
.breadcrumb {
  background: #fff;
  padding: 15px 0;
  margin-bottom: 20px;
}

/* 商品主体 */
.product-main {
  background: #fff;
  padding: 30px 0;
  margin-bottom: 20px;
}

.product-info-wrap {
  display: grid;
  grid-template-columns: 420px 1fr;
  gap: 30px;
}

/* 图片区域 */
.product-gallery {
  position: sticky;
  top: 20px;
}

.main-image {
  width: 420px;
  height: 420px;
  border: 1px solid #e5e5e5;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.main-image img {
  max-width: 100%;
  max-height: 100%;
}

.image-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.image-item {
  width: 60px;
  height: 60px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s;
}

.image-item:hover,
.image-item.active {
  border-color: #e4393c;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息 */
.product-info {
  padding: 0 20px;
}

.product-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0 0 10px;
  line-height: 1.4;
}

.product-subtitle {
  font-size: 14px;
  color: #999;
  margin-bottom: 20px;
}

/* 价格区域 */
.price-box {
  background: #f7f7f7;
  padding: 20px;
  margin-bottom: 20px;
}

.price-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.price-row .label {
  width: 80px;
  color: #666;
  font-size: 14px;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.current-price {
  font-size: 28px;
  color: #e4393c;
  font-weight: bold;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.discount-tag {
  background: #e4393c;
  color: white;
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 2px;
}

.promo-info {
  display: flex;
  align-items: flex-start;
}

.promo-info .label {
  width: 80px;
  color: #666;
  font-size: 14px;
}

.promo-list {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.promo-tag {
  cursor: pointer;
}

/* 规格选择 */
.spec-box {
  margin-bottom: 20px;
}

.spec-group {
  margin-bottom: 20px;
}

.spec-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.spec-option {
  min-width: 80px;
  height: 40px;
  padding: 0 15px;
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  font-size: 14px;
}

.spec-option:hover {
  border-color: #e4393c;
  color: #e4393c;
}

.spec-option.active {
  border-color: #e4393c;
  background: #fff4f4;
  color: #e4393c;
}

.spec-option.disabled {
  background: #f5f5f5;
  color: #ccc;
  cursor: not-allowed;
}

.spec-option .no-stock {
  position: absolute;
  top: 0;
  right: 0;
  background: #999;
  color: white;
  font-size: 10px;
  padding: 0 4px;
  transform: scale(0.8);
}

/* 库存和数量 */
.stock-box {
  margin-bottom: 20px;
}

.stock-row,
.quantity-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.stock-row .label,
.quantity-row .label {
  width: 80px;
  color: #666;
  font-size: 14px;
}

.stock-num {
  color: #67c23a;
  font-weight: bold;
}

.stock-num.low {
  color: #e6a23c;
}

.low-tip {
  font-size: 12px;
  margin-left: 5px;
}

/* 操作按钮 */
.action-box {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.buy-btn,
.cart-btn,
.favorite-btn {
  height: 50px;
  font-size: 16px;
}

.buy-btn,
.cart-btn {
  flex: 1;
}

.favorite-btn {
  min-width: 120px;
}

.tips {
  margin-bottom: 20px;
}

/* 服务承诺 */
.service-box {
  display: flex;
  gap: 30px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 4px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

/* 商品详情 */
.product-detail-content {
  background: #fff;
  padding: 30px 0;
}

.detail-tabs {
  margin-top: 20px;
}

.detail-section h3,
.params-section h3,
.service-section h3 {
  font-size: 18px;
  margin-bottom: 20px;
  color: #333;
}

.detail-text {
  line-height: 1.8;
  color: #666;
  margin-bottom: 30px;
}

.detail-images {
  text-align: center;
}

.detail-images img {
  max-width: 100%;
  margin-bottom: 20px;
}

/* 规格参数表格 */
.params-table {
  width: 100%;
  border-collapse: collapse;
}

.params-table tr {
  border-bottom: 1px solid #f0f0f0;
}

.params-table td {
  padding: 15px;
}

.param-name {
  width: 200px;
  color: #999;
  background: #fafafa;
}

.param-value {
  color: #666;
}

/* 售后服务 */
.service-list {
  list-style: none;
  padding: 0;
}

.service-list li {
  padding: 10px 0;
  color: #666;
  line-height: 1.8;
}

.service-list li:before {
  content: "✓ ";
  color: #67c23a;
  margin-right: 8px;
  font-weight: bold;
}

/* 评价区域 */
.reviews-section {
  padding: 20px 0;
}

/* 评价表单 */
.review-form-section {
  max-width: 800px;
  margin-bottom: 30px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
}

.review-form-section h4 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.review-tip {
  max-width: 800px;
  margin-bottom: 30px;
}

.reviews-list {
  max-width: 800px;
}

.reviews-list h4 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.empty-reviews {
  padding: 40px 0;
}

.review-item {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.review-user {
  font-weight: 500;
  color: #333;
}

.review-time {
  font-size: 12px;
  color: #999;
  margin-left: auto;
}

.review-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
}

.review-reply {
  background: #f7f7f7;
  padding: 10px;
  border-radius: 4px;
  font-size: 14px;
  color: #666;
}

.reply-label {
  color: #e4393c;
  font-weight: 500;
  margin-right: 5px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .container {
    width: 100%;
  }
  
  .product-info-wrap {
    grid-template-columns: 1fr;
  }
  
  .product-gallery {
    position: static;
  }
}
</style>

