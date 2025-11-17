/**
 * 登录认证相关的自定义钩子
 */

import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'

export function useAuth() {
  const router = useRouter()
  const userStore = useUserStore()

  // 是否已登录
  const isLoggedIn = computed(() => userStore.isLoggedIn)

  // 用户信息
  const userInfo = computed(() => userStore.userInfo)

  // 是否为管理员
  const isAdmin = computed(() => userStore.isAdmin())

  // 登录
  const handleLogin = async (loginForm) => {
    try {
      await userStore.login(loginForm)
      ElMessage.success('登录成功')
      return true
    } catch (error) {
      ElMessage.error('登录失败，请检查用户名和密码')
      return false
    }
  }

  // 登出
  const handleLogout = async () => {
    try {
      await userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch (error) {
      ElMessage.error('退出登录失败')
    }
  }

  // 检查登录状态，未登录则跳转到登录页
  const requireAuth = () => {
    if (!isLoggedIn.value) {
      ElMessage.warning('请先登录')
      router.push({
        path: '/login',
        query: { redirect: router.currentRoute.value.fullPath }
      })
      return false
    }
    return true
  }

  // 检查管理员权限
  const requireAdmin = () => {
    if (!requireAuth()) {
      return false
    }
    if (!isAdmin.value) {
      ElMessage.error('没有管理员权限')
      router.push('/')
      return false
    }
    return true
  }

  return {
    isLoggedIn,
    userInfo,
    isAdmin,
    handleLogin,
    handleLogout,
    requireAuth,
    requireAdmin
  }
}


