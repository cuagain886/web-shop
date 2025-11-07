/**
 * 购物车相关 API
 */

import request from '@/utils/request'
import { useUserStore } from '@/stores/userStore'

// ========== Mock 数据（开发阶段使用）==========
const MOCK_ENABLED = false // 是否启用Mock数据
const CART_STORAGE_KEY = 'mock_cart_data' // localStorage存储key

// 从localStorage获取购物车数据
const getCartFromStorage = () => {
  try {
    const data = localStorage.getItem(CART_STORAGE_KEY)
    return data ? JSON.parse(data) : []
  } catch (error) {
    console.error('读取购物车数据失败:', error)
    return []
  }
}

// 保存购物车数据到localStorage
const saveCartToStorage = (cart) => {
  try {
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart))
  } catch (error) {
    console.error('保存购物车数据失败:', error)
  }
}

// 生成唯一ID
let nextId = Date.now()

/**
 * 获取购物车列表
 */
export function getCartList() {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const cart = getCartFromStorage()
        console.log('📦 获取购物车列表:', cart)
        resolve(cart)
      }, 200)
    })
  }
  
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/cart/${userId}`,
    method: 'get'
  })
}

/**
 * 添加商品到购物车
 * data: { productId, name, price, image, specs, quantity, stock }
 */
export function addToCart(data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        let cart = getCartFromStorage()
        
        // 查找是否已存在相同商品和规格
        const specsKey = JSON.stringify(data.specs || {})
        const existingIndex = cart.findIndex(item =>
          item.productId === data.productId &&
          JSON.stringify(item.specs) === specsKey
        )
        
        if (existingIndex > -1) {
          // 已存在，更新数量
          cart[existingIndex].quantity += data.quantity
          console.log('📦 更新购物车商品数量:', cart[existingIndex])
        } else {
          // 新增商品
          const newItem = {
            id: nextId++,
            productId: data.productId,
            name: data.name,
            price: data.price,
            image: data.image,
            specs: data.specs || {},
            quantity: data.quantity,
            stock: data.stock,
            checked: true, // 默认选中
            addTime: Date.now()
          }
          cart.unshift(newItem) // 添加到最前面
          console.log('📦 添加新商品到购物车:', newItem)
        }
        
        saveCartToStorage(cart)
        resolve({ success: true })
      }, 200)
    })
  }
  
  // 获取用户ID
  const userStore = useUserStore()
  console.log('🔍 userStore.userInfo:', userStore.userInfo)
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  console.log('🔍 最终使用的userId:', userId)
  
  // 转换为后端期望的格式
  const cartData = {
    userId: userId,
    productId: data.productId,
    quantity: data.quantity,
    specInfo: JSON.stringify(data.specs || {})
  }
  console.log('🔍 发送到后端的数据:', cartData)
  
  return request({
    url: '/cart',
    method: 'post',
    data: cartData
  })
}

/**
 * 更新购物车商品数量
 */
export function updateCartItem(id, data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        let cart = getCartFromStorage()
        const item = cart.find(item => item.id === id)
        
        if (item) {
          item.quantity = data.quantity
          saveCartToStorage(cart)
          console.log('📦 更新商品数量:', item)
          resolve({ success: true })
        } else {
          reject(new Error('商品不存在'))
        }
      }, 200)
    })
  }
  
  return request({
    url: `/cart/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除购物车商品
 */
export function deleteCartItem(id) {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        let cart = getCartFromStorage()
        cart = cart.filter(item => item.id !== id)
        saveCartToStorage(cart)
        console.log('📦 删除购物车商品:', id)
        resolve({ success: true })
      }, 200)
    })
  }
  
  return request({
    url: `/cart/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除购物车商品
 */
export function batchDeleteCartItems(ids) {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        let cart = getCartFromStorage()
        cart = cart.filter(item => !ids.includes(item.id))
        saveCartToStorage(cart)
        console.log('📦 批量删除购物车商品:', ids)
        resolve({ success: true })
      }, 200)
    })
  }
  
  return request({
    url: '/cart/batch-delete',
    method: 'post',
    data: { ids }
  })
}

/**
 * 选中/取消选中购物车商品
 */
export function toggleCartItem(id, checked) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        let cart = getCartFromStorage()
        const item = cart.find(item => item.id === id)
        
        if (item) {
          item.checked = checked
          saveCartToStorage(cart)
          console.log('📦 切换商品选中状态:', item)
          resolve({ success: true })
        } else {
          reject(new Error('商品不存在'))
        }
      }, 100)
    })
  }
  
  return request({
    url: `/cart/${id}/toggle`,
    method: 'put',
    data: { checked }
  })
}

/**
 * 全选/取消全选购物车商品
 */
export function toggleAllCartItems(checked) {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        let cart = getCartFromStorage()
        cart.forEach(item => {
          item.checked = checked
        })
        saveCartToStorage(cart)
        console.log('📦 全选/取消全选:', checked)
        resolve({ success: true })
      }, 100)
    })
  }
  
  return request({
    url: '/cart/toggle-all',
    method: 'put',
    data: { checked }
  })
}

/**
 * 清空购物车
 */
export function clearCart() {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        saveCartToStorage([])
        console.log('📦 清空购物车')
        resolve({ success: true })
      }, 200)
    })
  }
  
  return request({
    url: '/cart/clear',
    method: 'post'
  })
}

/**
 * 获取购物车商品数量
 */
export function getCartCount() {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      const cart = getCartFromStorage()
      const count = cart.reduce((sum, item) => sum + item.quantity, 0)
      console.log('📦 购物车商品数量:', count)
      resolve(count)
    })
  }
  
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/cart/${userId}/count`,
    method: 'get'
  })
}


