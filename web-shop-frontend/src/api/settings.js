/**
 * 系统设置相关 API
 */

import request from '@/utils/request'

/**
 * 获取系统设置
 */
export function getSettings() {
  return request({
    url: '/api/settings',
    method: 'get'
  })
}

/**
 * 更新系统设置
 */
export function updateSettings(data) {
  return request({
    url: '/api/settings',
    method: 'put',
    data
  })
}
