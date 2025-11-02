/**
 * 路由配置
 */

import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import userRoutes from './userRoutes'
import adminRoutes from './adminRoutes'
import { useUserStore } from '@/stores/userStore'

console.log('🚀 路由模块初始化')

const routes = [
  ...userRoutes,
  ...adminRoutes,
  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  console.log('📍 路由跳转:', to.path)
  
  // 设置页面标题
  const isAdmin = to.path.startsWith('/admin')
  const titleSuffix = isAdmin ? ' - 商家后台' : ' - 购物平台'
  document.title = to.meta.title ? `${to.meta.title}${titleSuffix}` : (isAdmin ? '商家后台' : '购物平台')
  
  // 权限检查
  const userStore = useUserStore()
  
  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      
      // 如果是管理端，跳转到管理端登录页
      if (to.meta.requiresMerchant) {
        next({
          path: '/admin/login',
          query: { redirect: to.fullPath }
        })
      } else {
        // 普通用户，跳转到用户登录页
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
      }
      return
    }
    
    // 检查是否需要商家权限
    if (to.meta.requiresMerchant) {
      if (userStore.userInfo?.role !== 'merchant') {
        ElMessage.error('无权访问，仅限商家账号')
        next('/admin/login')
        return
      }
    }
  }
  
  // 如果已经是商家登录，访问管理端登录页时，直接跳转到管理端首页
  if (to.path === '/admin/login' && userStore.isLoggedIn && userStore.userInfo?.role === 'merchant') {
    next('/admin/dashboard')
    return
  }
  
  // 如果已经是普通用户登录，访问用户端登录页时，直接跳转到首页
  if (to.path === '/login' && userStore.isLoggedIn && userStore.userInfo?.role === 'user') {
    next('/')
    return
  }
  
  next()
})

console.log('✅ 路由配置完成')

export default router
