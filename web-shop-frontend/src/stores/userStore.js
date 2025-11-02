/**
 * 用户状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, register as registerApi, getUserInfo } from '@/api/user'

const USER_STORAGE_KEY = 'user_info'
const TOKEN_STORAGE_KEY = 'user_token'

export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref(null)
  const token = ref(localStorage.getItem(TOKEN_STORAGE_KEY) || '')
  const isLoggedIn = computed(() => !!token.value)

  // 初始化用户信息
  const initUserInfo = () => {
    try {
      const savedInfo = localStorage.getItem(USER_STORAGE_KEY)
      if (savedInfo) {
        userInfo.value = JSON.parse(savedInfo)
      }
    } catch (error) {
      console.error('初始化用户信息失败:', error)
    }
  }

  // 保存用户信息
  const saveUserInfo = (info) => {
    userInfo.value = info
    if (info) {
      localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(info))
    } else {
      localStorage.removeItem(USER_STORAGE_KEY)
    }
  }

  // 保存token
  const saveToken = (newToken) => {
    token.value = newToken
    if (newToken) {
      localStorage.setItem(TOKEN_STORAGE_KEY, newToken)
    } else {
      localStorage.removeItem(TOKEN_STORAGE_KEY)
    }
  }

  // 登录
  const login = async (loginData) => {
    try {
      console.log('🔐 开始登录:', loginData.username)
      const data = await loginApi(loginData)
      
      // 保存token和用户信息
      saveToken(data.token)
      saveUserInfo(data.userInfo)
      
      console.log('✅ 登录成功:', data.userInfo)
      return data
    } catch (error) {
      console.error('❌ 登录失败:', error)
      throw error
    }
  }

  // 注册
  const register = async (registerData) => {
    try {
      console.log('📝 开始注册:', registerData.username)
      const data = await registerApi(registerData)
      console.log('✅ 注册成功')
      return data
    } catch (error) {
      console.error('❌ 注册失败:', error)
      throw error
    }
  }

  // 登出
  const logout = async () => {
    try {
      console.log('👋 开始登出')
      await logoutApi()
      
      // 清除用户信息
      saveToken('')
      saveUserInfo(null)
      
      console.log('✅ 登出成功')
    } catch (error) {
      console.error('❌ 登出失败:', error)
      // 即使失败也清除本地信息
      saveToken('')
      saveUserInfo(null)
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const data = await getUserInfo()
      saveUserInfo(data)
      return data
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 初始化
  initUserInfo()

  return {
    userInfo,
    token,
    isLoggedIn,
    login,
    register,
    logout,
    fetchUserInfo,
    // 暴露设置方法（用于商家登录等场景）
    setToken: saveToken,
    setUserInfo: saveUserInfo
  }
})
