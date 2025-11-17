/**
 * Axios 请求封装
 * 统一处理请求和响应，自动添加 JWT Token
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearAuth } from './auth'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加 JWT Token
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是 200，则认为是错误
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    // 返回data字段中的实际数据
    return res.data
  },
  error => {
    console.error('响应错误:', error)
    
    // 处理 401 未授权错误
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      clearAuth()
      router.push('/login')
      return Promise.reject(error)
    }
    
    // 处理其他错误
    const message = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
