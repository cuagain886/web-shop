/**
 * 订单相关 API
 */

import request from '@/utils/request'

/**
 * 创建订单
 */
export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

/**
 * 获取订单列表
 */
export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

/**
 * 取消订单
 */
export function cancelOrder(id) {
  return request({
    url: `/order/${id}/cancel`,
    method: 'put'
  })
}

/**
 * 删除订单
 */
export function deleteOrder(id) {
  return request({
    url: `/order/${id}`,
    method: 'delete'
  })
}

/**
 * 确认收货
 */
export function confirmReceipt(id) {
  return request({
    url: `/order/${id}/confirm`,
    method: 'put'
  })
}

/**
 * 申请退款
 */
export function applyRefund(id, data) {
  return request({
    url: `/order/${id}/refund`,
    method: 'post',
    data
  })
}

/**
 * 支付订单
 */
export function payOrder(id, data) {
  return request({
    url: `/order/${id}/pay`,
    method: 'post',
    data
  })
}

/**
 * 获取支付状态
 */
export function getPaymentStatus(id) {
  return request({
    url: `/order/${id}/payment-status`,
    method: 'get'
  })
}

// ========== 管理端接口 ==========

/**
 * 获取订单列表（管理端）
 */
export function getAdminOrderList(params) {
  return request({
    url: '/admin/orders',
    method: 'get',
    params
  })
}

/**
 * 更新订单状态（管理端）
 */
export function updateOrderStatus(id, data) {
  return request({
    url: `/admin/orders/${id}/status`,
    method: 'put',
    data
  })
}

/**
 * 订单发货（管理端）
 */
export function shipOrder(id, data) {
  return request({
    url: `/admin/orders/${id}/ship`,
    method: 'put',
    data
  })
}

/**
 * 处理退款（管理端）
 */
export function handleRefund(id, data) {
  return request({
    url: `/admin/orders/${id}/refund`,
    method: 'put',
    data
  })
}

/**
 * 获取订单统计数据（管理端）
 */
export function getOrderStatistics(params) {
  return request({
    url: '/admin/orders/statistics',
    method: 'get',
    params
  })
}

/**
 * 获取销售数据（管理端）
 */
export function getSalesData(params) {
  return request({
    url: '/admin/sales/data',
    method: 'get',
    params
  })
}


