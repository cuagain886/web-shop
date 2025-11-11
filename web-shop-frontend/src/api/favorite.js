/**
 * 用户收藏相关 API
 */

import request from '@/utils/request'

/**
 * 获取用户收藏列表
 */
export function getFavoriteList(userId) {
  return request({
    url: `/api/favorite/user/${userId}`,
    method: 'get'
  })
}

/**
 * 添加收藏
 */
export function addFavorite(productId) {
  return request({
    url: '/api/favorite',
    method: 'post',
    data: { productId }
  })
}

/**
 * 取消收藏
 */
export function removeFavorite(productId) {
  return request({
    url: `/api/favorite/${productId}`,
    method: 'delete'
  })
}

/**
 * 检查是否已收藏
 */
export function checkFavorite(productId) {
  return request({
    url: `/api/favorite/check/${productId}`,
    method: 'get'
  })
}

/**
 * 清空用户收藏
 */
export function clearFavorites(userId) {
  return request({
    url: `/api/favorite/user/${userId}`,
    method: 'delete'
  })
}

/**
 * 统计商品被收藏次数
 */
export function countFavorites(productId) {
  return request({
    url: `/api/favorite/product/${productId}/count`,
    method: 'get'
  })
}