/**
 * 购物车相关 API
 */

import request from '@/utils/request'
import { useUserStore } from '@/stores/userStore'

/**
 * 获取购物车列表
 */
export function getCartList() {
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
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  const cartData = {
    userId: userId,
    productId: data.productId,
    quantity: data.quantity,
    specInfo: JSON.stringify(data.specs || {})
  }
  
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
  return request({
    url: `/cart/${id}`,
    method: 'put',
    params: { quantity: data.quantity }
  })
}

/**
 * 删除购物车商品
 */
export function deleteCartItem(id) {
  return request({
    url: `/cart/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除购物车商品
 */
export function batchDeleteCartItems(ids) {
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
  return request({
    url: '/cart/clear',
    method: 'post'
  })
}

/**
 * 获取购物车商品数量
 */
export function getCartCount() {
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/cart/${userId}/count`,
    method: 'get'
  })
}
