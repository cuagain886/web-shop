/**
 * 路由配置
 */

import { createRouter, createWebHistory } from 'vue-router'
import userRoutes from './userRoutes'

console.log('🚀 路由模块初始化')

const routes = [
  ...userRoutes,
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
  document.title = to.meta.title ? `${to.meta.title} - 购物平台` : '购物平台'
  next()
})

console.log('✅ 路由配置完成')

export default router
