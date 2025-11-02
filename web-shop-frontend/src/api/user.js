/**
 * 用户相关 API
 */

import request from '@/utils/request'

// ========== Mock 数据（开发阶段使用）==========
const MOCK_ENABLED = true // 是否启用Mock数据

// Mock用户数据
const mockUsers = [
  { username: 'test', password: '123456', phone: '13800138000', nickname: '测试用户' },
  { username: 'admin', password: 'admin123', phone: '13800138001', nickname: '管理员' }
]

// 生成Mock Token
const generateMockToken = (username) => {
  return `mock_token_${username}_${Date.now()}`
}

/**
 * 用户登录
 */
export function login(data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const { username, password } = data
        
        // 查找用户
        const user = mockUsers.find(u => u.username === username)
        
        if (!user) {
          console.warn('⚠️ 用户不存在:', username)
          reject(new Error('用户名不存在'))
          return
        }
        
        if (user.password !== password) {
          console.warn('⚠️ 密码错误')
          reject(new Error('密码错误'))
          return
        }
        
        // 登录成功
        const token = generateMockToken(username)
        const userInfo = {
          id: Date.now(),
          username: user.username,
          nickname: user.nickname,
          phone: user.phone,
          avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${username}`
        }
        
        console.log('✅ Mock登录成功:', userInfo)
        resolve({
          token,
          userInfo
        })
      }, 500)
    })
  }
  
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 */
export function register(data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const { username, password, phone } = data
        
        // 检查用户名是否已存在
        const existingUser = mockUsers.find(u => u.username === username)
        if (existingUser) {
          console.warn('⚠️ 用户名已存在:', username)
          reject(new Error('用户名已被注册'))
          return
        }
        
        // 检查手机号是否已存在
        const existingPhone = mockUsers.find(u => u.phone === phone)
        if (existingPhone) {
          console.warn('⚠️ 手机号已存在:', phone)
          reject(new Error('手机号已被注册'))
          return
        }
        
        // 注册成功（实际不会真正保存到mockUsers）
        console.log('✅ Mock注册成功:', username)
        resolve({
          success: true,
          message: '注册成功'
        })
      }, 500)
    })
  }
  
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        // 从localStorage获取用户信息
        const userInfo = localStorage.getItem('user_info')
        if (userInfo) {
          resolve(JSON.parse(userInfo))
        } else {
          reject(new Error('未登录'))
        }
      }, 300)
    })
  }
  
  return request({
    url: '/user/info',
    method: 'get'
  })
}

/**
 * 更新用户信息
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/info',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 */
export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

/**
 * 获取用户地址列表
 */
export function getAddressList() {
  return request({
    url: '/user/address',
    method: 'get'
  })
}

/**
 * 添加用户地址
 */
export function addAddress(data) {
  return request({
    url: '/user/address',
    method: 'post',
    data
  })
}

/**
 * 更新用户地址
 */
export function updateAddress(id, data) {
  return request({
    url: `/user/address/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户地址
 */
export function deleteAddress(id) {
  return request({
    url: `/user/address/${id}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 */
export function setDefaultAddress(id) {
  return request({
    url: `/user/address/${id}/default`,
    method: 'put'
  })
}

/**
 * 用户登出
 */
export function logout() {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        console.log('✅ Mock登出成功')
        resolve({ success: true })
      }, 300)
    })
  }
  
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

/**
 * 获取用户列表（管理端）
 */
export function getUserList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/**
 * 更新用户状态（管理端）
 */
export function updateUserStatus(id, data) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data
  })
}


