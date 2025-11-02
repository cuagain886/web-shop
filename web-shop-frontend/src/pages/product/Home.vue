<template>
  <div class="jd-home">
    <!-- 主要内容区 -->
    <div class="jd-container">
      <div class="jd-main">
        <!-- 左侧分类导航 -->
        <aside class="jd-category">
          <div class="category-list">
            <div 
              v-for="cat in categories" 
              :key="cat.id" 
              class="category-item"
              @click="goToCategory(cat.id)"
            >
              <span class="cat-icon">{{ cat.icon }}</span>
              <span class="cat-name">{{ cat.name }}</span>
              <span class="cat-arrow">›</span>
            </div>
          </div>
        </aside>

        <!-- 中间轮播图 -->
        <div class="jd-banner">
          <el-carousel height="470px" indicator-position="outside">
            <el-carousel-item v-for="item in banners" :key="item.id">
              <div class="banner-item" :style="{ background: item.color }">
                <div class="banner-text">
                  <h2>{{ item.title }}</h2>
                  <p>{{ item.desc }}</p>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <!-- 右侧信息栏 -->
        <aside class="jd-sidebar">
          <div class="sidebar-card">
            <div class="news-title">📢 公告</div>
            <div class="news-item">双11狂欢即将开始</div>
            <div class="news-item">新用户专享优惠</div>
            <div class="news-item">全场包邮活动</div>
          </div>
        </aside>
      </div>
    </div>

    <!-- 秒杀区域 -->
    <div class="jd-seckill">
      <div class="jd-container">
        <div class="section-header">
          <h3>⚡ 限时秒杀</h3>
          <span class="more">查看更多 ›</span>
        </div>
        <div class="seckill-list">
          <div 
            v-for="item in seckillProducts" 
            :key="item.id" 
            class="seckill-item"
            @click="goToProductDetail(item.id)"
          >
            <div class="item-img" :style="{ background: item.color }">
              {{ item.name }}
            </div>
            <div class="item-price">
              <span class="price">¥{{ item.price }}</span>
              <span class="origin-price">¥{{ item.originPrice }}</span>
            </div>
            <el-button type="danger" size="small" class="buy-btn" @click.stop="goToProductDetail(item.id)">立即抢购</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐商品 -->
    <div class="jd-recommend">
      <div class="jd-container">
        <div class="section-header">
          <h3>🔥 为你推荐</h3>
        </div>
        <div class="product-grid">
          <div 
            v-for="item in recommendProducts" 
            :key="item.id" 
            class="product-item"
            @click="goToProductDetail(item.id)"
          >
            <div class="product-img" :style="{ background: item.color }">
              {{ item.name }}
            </div>
            <div class="product-info">
              <div class="product-name">{{ item.name }}</div>
              <div class="product-desc">{{ item.desc }}</div>
              <div class="product-price">
                <span class="price">¥{{ item.price }}</span>
                <span class="sales">{{ item.sales }}人付款</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

console.log('🎉 Home页面开始加载')

const router = useRouter()

// 跳转到商品详情页
const goToProductDetail = (productId) => {
  console.log('🔍 点击商品，准备跳转到商品详情:', productId)
  try {
    router.push(`/product/${productId}`)
    console.log('✅ 商品详情跳转成功')
  } catch (error) {
    console.error('❌ 商品详情跳转失败:', error)
  }
}

// 跳转到分类商品列表
const goToCategory = (categoryId) => {
  console.log('📂 点击分类，准备跳转到分类列表:', categoryId)
  try {
    router.push({
      path: '/products',
      query: { categoryId }
    })
    console.log('✅ 分类列表跳转成功')
  } catch (error) {
    console.error('❌ 分类列表跳转失败:', error)
  }
}

// 分类数据
const categories = ref([
  { id: 1, name: '家用电器', icon: '📺' },
  { id: 2, name: '手机数码', icon: '📱' },
  { id: 3, name: '电脑办公', icon: '💻' },
  { id: 4, name: '家居家具', icon: '🛋️' },
  { id: 5, name: '服装鞋靴', icon: '👕' },
  { id: 6, name: '美妆个护', icon: '💄' },
  { id: 7, name: '运动户外', icon: '⚽' },
  { id: 8, name: '食品生鲜', icon: '🍎' },
  { id: 9, name: '母婴玩具', icon: '🍼' },
  { id: 10, name: '图书文娱', icon: '📚' }
])

// 轮播图
const banners = ref([
  { id: 1, title: '双11全球狂欢节', desc: '满300减50 跨店满减', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { id: 2, title: '手机超级品类日', desc: 'iPhone新品首发', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { id: 3, title: '家电焕新季', desc: '以旧换新 最高补贴500', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { id: 4, title: '运动装备大促', desc: '专业运动装备5折起', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

// 秒杀商品
const seckillProducts = ref([
  { id: 1, name: 'iPhone 15', price: 5999, originPrice: 7999, color: '#409eff' },
  { id: 2, name: 'AirPods Pro', price: 1299, originPrice: 1899, color: '#67c23a' },
  { id: 3, name: 'iPad Air', price: 3999, originPrice: 4999, color: '#e6a23c' },
  { id: 4, name: 'Apple Watch', price: 2399, originPrice: 3199, color: '#f56c6c' },
  { id: 5, name: 'MacBook', price: 8999, originPrice: 12999, color: '#909399' }
])

// 推荐商品
const recommendProducts = ref([
  { id: 1, name: 'iPhone 15 Pro Max', desc: 'A17 Pro芯片 钛金属设计', price: 9999, sales: 1000, color: '#409eff' },
  { id: 2, name: 'MacBook Pro 16', desc: 'M3 Max芯片 专业性能', price: 19999, sales: 500, color: '#67c23a' },
  { id: 3, name: 'iPad Pro', desc: 'M2芯片 12.9英寸', price: 6999, sales: 800, color: '#e6a23c' },
  { id: 4, name: 'AirPods Max', desc: '头戴式降噪耳机', price: 4399, sales: 600, color: '#f56c6c' },
  { id: 5, name: 'Apple Watch Ultra', desc: '专业运动手表', price: 6299, sales: 400, color: '#909399' },
  { id: 6, name: 'Mac Studio', desc: 'M2 Ultra芯片', price: 14999, sales: 200, color: '#409eff' },
  { id: 7, name: 'Studio Display', desc: '27英寸5K显示器', price: 11499, sales: 150, color: '#67c23a' },
  { id: 8, name: 'HomePod', desc: '空间音频 智能音箱', price: 2299, sales: 300, color: '#e6a23c' }
])

console.log('✅ 数据加载完成')
console.log('📊 分类:', categories.value.length)
console.log('📊 轮播:', banners.value.length)
console.log('📊 秒杀:', seckillProducts.value.length)
console.log('📊 推荐:', recommendProducts.value.length)
</script>

<style scoped>
/* 全局容器 */
.jd-home {
  background: #f0f0f0;
  min-height: 100vh;
}

.jd-container {
  width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
}

/* 主要内容区 */
.jd-main {
  display: grid;
  grid-template-columns: 200px 1fr 270px;
  gap: 10px;
  margin-bottom: 20px;
}

/* 左侧分类 */
.jd-category {
  background: #fff;
  border-radius: 4px;
  padding: 10px 0;
}

.category-list {
  padding: 0;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.category-item:hover {
  background: #f5f5f5;
}

.cat-icon {
  font-size: 18px;
  margin-right: 10px;
}

.cat-name {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.cat-arrow {
  color: #999;
  font-size: 20px;
}

/* 轮播图 */
.jd-banner {
  border-radius: 4px;
  overflow: hidden;
}

.banner-item {
  height: 470px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.banner-text h2 {
  font-size: 42px;
  margin: 0 0 10px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.banner-text p {
  font-size: 20px;
  margin: 0;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
}

/* 右侧边栏 */
.jd-sidebar {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sidebar-card {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
}

.news-title {
  font-weight: bold;
  margin-bottom: 12px;
  color: #333;
}

.news-item {
  padding: 8px 0;
  font-size: 13px;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.news-item:hover {
  color: #e4393c;
}

.news-item:last-child {
  border-bottom: none;
}

/* 秒杀区 */
.jd-seckill {
  background: #fff;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 2px solid #e4393c;
}

.section-header h3 {
  margin: 0;
  font-size: 22px;
  color: #333;
}

.more {
  color: #e4393c;
  font-size: 14px;
  cursor: pointer;
}

.seckill-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  padding: 20px 0;
}

.seckill-item {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.seckill-item:hover {
  transform: translateY(-3px);
}

.item-img {
  height: 200px;
  border-radius: 4px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 14px;
  padding: 10px;
}

.item-price {
  margin-bottom: 10px;
}

.price {
  font-size: 20px;
  color: #e4393c;
  font-weight: bold;
  margin-right: 8px;
}

.origin-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.buy-btn {
  width: 100%;
}

/* 推荐商品 */
.jd-recommend {
  background: #fff;
  padding-bottom: 40px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  padding-top: 20px;
}

.product-item {
  cursor: pointer;
  transition: all 0.3s;
}

.product-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.product-img {
  height: 220px;
  border-radius: 4px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 14px;
  padding: 15px;
  text-align: center;
}

.product-info {
  padding: 10px;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price .price {
  font-size: 18px;
  color: #e4393c;
  font-weight: bold;
}

.sales {
  font-size: 12px;
  color: #999;
}

/* 响应式 */
@media (max-width: 1200px) {
  .jd-container {
    width: 100%;
    padding: 20px;
  }
  
  .jd-main {
    grid-template-columns: 1fr;
  }
  
  .jd-category,
  .jd-sidebar {
    display: none;
  }
}
</style>
