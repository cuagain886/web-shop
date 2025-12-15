<template>
  <header class="jd-header">
    <div class="header-container">
      <!-- Logo -->
      <div class="logo">
        <span class="logo-text">Web-Shop</span>
      </div>

      <!-- 搜索框 -->
      <div class="search-box">
        <el-input
          v-model="searchText"
          placeholder="搜索商品..."
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 右侧导航 -->
      <nav class="header-nav">
        <router-link to="/" class="nav-link">首页</router-link>
        
        <router-link to="/cart" class="nav-link">
          <el-badge :value="cartCount" :hidden="cartCount === 0">
            购物车
          </el-badge>
        </router-link>
        
        <template v-if="userStore.isLoggedIn">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="nav-link user-menu">
              {{ userStore.userInfo?.nickname || userStore.userInfo?.username }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link">登录</router-link>
          <router-link to="/register" class="nav-link">注册</router-link>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import { useCartStore } from '@/stores/cartStore'
import { ElMessage, ElMessageBox } from 'element-plus'

console.log('Header组件加载')

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const searchText = ref('')

// 购物车数量
const cartCount = computed(() => cartStore.cartCount)

// 处理搜索
const handleSearch = () => {
  const keyword = searchText.value.trim()
  if (keyword) {
    console.log('搜索:', keyword)
    router.push({
      path: '/products',
      query: { keyword }
    })
    searchText.value = ''
  }
}

// 处理用户菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'orders':
      router.push('/orders')
      break
    case 'profile':
      // 根据用户角色跳转到对应的个人中心
      if (userStore.userInfo?.role === 'merchant') {
        router.push('/admin/profile')
      } else {
        router.push('/profile')
      }
      break
    case 'logout':
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        await userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/')
      } catch (error) {
        // 用户取消
      }
      break
  }
}

// 初始化时更新购物车数量
onMounted(async () => {
  if (userStore.isLoggedIn) {
    await cartStore.updateCartCount()
  }
})
</script>

<style scoped>
.jd-header {
  background: #e3393c;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  width: 1200px;
  margin: 0 auto;
  height: 80px;
  display: flex;
  align-items: center;
  gap: 40px;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: white;
  font-weight: bold;
  white-space: nowrap;
}

.logo-text {
  font-size: 20px;
}

.search-box {
  flex: 1;
  max-width: 600px;
}

.search-input {
  width: 100%;
}

.header-nav {
  display: flex;
  gap: 25px;
  align-items: center;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-size: 14px;
  white-space: nowrap;
  transition: opacity 0.2s;
}

.nav-link:hover {
  opacity: 0.8;
}

.user-menu {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

@media (max-width: 1200px) {
  .header-container {
    width: 100%;
  }
}
</style>

