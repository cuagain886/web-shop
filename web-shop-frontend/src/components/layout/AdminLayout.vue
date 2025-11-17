<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="admin-aside">
        <div class="logo-area" :class="{ collapse: isCollapse }">
          <span v-if="!isCollapse" class="logo-text">商家后台</span>
          <span v-else class="logo-icon">🏪</span>
        </div>

        <el-menu
          :default-active="currentRoute"
          :collapse="isCollapse"
          :unique-opened="true"
          router
          class="admin-menu"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>数据概览</template>
          </el-menu-item>

          <el-sub-menu index="product">
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="/admin/products">商品列表</el-menu-item>
            <el-menu-item index="/admin/products/add">新增商品</el-menu-item>
            <el-menu-item index="/admin/categories">分类管理</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/orders">
            <el-icon><ShoppingCart /></el-icon>
            <template #title>订单管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/operation-log">
            <el-icon><Document /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>

          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/settings">
            <el-icon><Setting /></el-icon>
            <template #title>系统设置</template>
          </el-menu-item>

          <el-menu-item index="/admin/announcements">
            <el-icon><Document /></el-icon>
            <template #title>公告管理</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航栏 -->
        <el-header class="admin-header">
          <div class="header-left">
            <el-button
              text
              @click="toggleCollapse"
              class="collapse-btn"
            >
              <el-icon :size="20">
                <Fold v-if="!isCollapse" />
                <Expand v-else />
              </el-icon>
            </el-button>

            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="breadcrumbs.length > 0">
                {{ breadcrumbs[breadcrumbs.length - 1] }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <el-button text @click="goToUserSite">
              <el-icon><House /></el-icon>
              <span>用户端</span>
            </el-button>

            <el-dropdown @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
                <span class="username">{{ userStore.userInfo?.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主内容 -->
        <el-main class="admin-main">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Odometer,
  Goods,
  ShoppingCart,
  User,
  Setting,
  Fold,
  Expand,
  House,
  ArrowDown,
  Document
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

// 当前路由
const currentRoute = computed(() => route.path)

// 面包屑
const breadcrumbs = computed(() => {
  const routeMap = {
    '/admin/dashboard': '数据概览',
    '/admin/products': '商品列表',
    '/admin/products/add': '新增商品',
    '/admin/products/edit': '编辑商品',
    '/admin/categories': '分类管理',
    '/admin/orders': '订单管理',
    '/admin/operation-log': '操作日志',
    '/admin/profile': '个人信息',
    '/admin/security': '安全设置',
    '/admin/users': '用户管理',
    '/admin/settings': '系统设置',
    '/admin/announcements': '公告管理'
  }
  
  const crumbs = []
  const path = route.path
  
  if (routeMap[path]) {
    crumbs.push(routeMap[path])
  } else if (path.startsWith('/admin/products/edit/')) {
    crumbs.push('商品列表', '编辑商品')
  } else if (path.startsWith('/admin/orders/') && path !== '/admin/orders') {
    crumbs.push('订单管理', '订单详情')
  }
  
  return crumbs
})

/**
 * 切换侧边栏折叠
 */
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

/**
 * 跳转到用户端
 */
const goToUserSite = () => {
  router.push('/')
}

/**
 * 处理下拉菜单命令
 */
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/admin/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/admin/login')
      } catch (error) {
        if (error !== 'cancel') {
          console.error('退出登录失败:', error)
        }
      }
      break
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.el-container {
  height: 100%;
}

/* 侧边栏 */
.admin-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2a3f54;
  color: white;
  font-size: 18px;
  font-weight: 600;
  transition: all 0.3s;
}

.logo-area.collapse {
  font-size: 24px;
}

.logo-icon {
  font-size: 24px;
}

.admin-menu {
  border-right: none;
  background-color: #304156;
}

.admin-menu :deep(.el-menu-item),
.admin-menu :deep(.el-sub-menu__title) {
  color: #bfcbd9;
}

.admin-menu :deep(.el-menu-item:hover),
.admin-menu :deep(.el-sub-menu__title:hover) {
  background-color: #263445 !important;
  color: #409eff;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
  color: white;
}

/* 顶部导航栏 */
.admin-header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
}

/* 主内容区 */
.admin-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>


