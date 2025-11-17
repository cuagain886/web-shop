/**
 * 浏览历史相关 API
 */

import request from '@/utils/request'

/**
 * 记录浏览历史
 */
export function recordBrowsing(userId, productId) {
  return request({
    url: '/api/history',
    method: 'post',
    params: { userId, productId }
  })
}

/**
 * 获取用户浏览历史
 */
export function getUserHistory(userId, limit = 20) {
  return request({
    url: `/api/history/user/${userId}`,
    method: 'get',
    params: { limit }
  })
}

/**
 * 删除浏览记录
 */
export function deleteHistory(historyId, userId) {
  return request({
    url: `/api/history/${historyId}`,
    method: 'delete',
    params: { userId }
  })
}

/**
 * 清空浏览历史
 */
export function clearHistory(userId) {
  return request({
    url: `/api/history/user/${userId}`,
    method: 'delete'
  })
}