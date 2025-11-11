/**
 * 地址相关 API
 */

import request from '@/utils/request'
import { useUserStore } from '@/stores/userStore'

/**
 * 获取地址列表
 */
export function getAddressList() {
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/api/address/user/${userId}`,
    method: 'get'
  })
}

/**
 * 获取默认地址
 */
export function getDefaultAddress() {
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/api/address/user/${userId}/default`,
    method: 'get'
  })
}

/**
 * 获取地址详情
 */
export function getAddressDetail(id) {
  return request({
    url: `/api/address/${id}`,
    method: 'get'
  })
}

/**
 * 添加地址
 */
export function addAddress(data) {
  return request({
    url: '/api/address',
    method: 'post',
    data
  })
}

/**
 * 更新地址
 */
export function updateAddress(id, data) {
  return request({
    url: `/api/address/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除地址
 */
export function deleteAddress(id) {
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/api/address/${id}?userId=${userId}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 */
export function setDefaultAddress(id) {
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/api/address/${id}/default?userId=${userId}`,
    method: 'put'
  })
}
