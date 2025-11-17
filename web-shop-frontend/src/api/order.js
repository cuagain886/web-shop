/**
 * 订单相关 API
 */

import request from '@/utils/request'

/**
 * 创建订单
 */
export function createOrder(data) {
  return request({
    url: '/api/orders',
    method: 'post',
    data
  })
}

/**
 * 获取订单列表
 */
export function getOrderList(params = {}) {
  const { userId, page = 1, pageSize = 10, status } = params
  return request({
    url: `/api/orders/user/${userId}`,
    method: 'get',
    params: {
      pageNum: page,
      pageSize,
      status
    }
  })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id) {
  return request({
    url: `/api/orders/${id}`,
    method: 'get'
  })
}

/**
 * 取消订单
 */
export function cancelOrder(orderNo, userId = 1, reason = '不想要了') {
  return request({
    url: `/api/orders/${orderNo}/cancel`,
    method: 'put',
    params: { userId, reason }
  })
}

/**
 * 删除订单
 */
export function deleteOrder(id) {
  return request({
    url: `/api/orders/${id}`,
    method: 'delete'
  })
}

/**
 * 确认收货
 */
export function confirmReceipt(orderNo, userId) {
  return request({
    url: `/api/orders/${orderNo}/receive`,
    method: 'put',
    params: { userId }
  })
}

/**
 * 申请退款
 */
export function applyRefund(data) {
  return request({
    url: '/api/refund',
    method: 'post',
    data
  })
}

/**
 * 获取用户退款列表
 */
export function getUserRefunds(userId, params = {}) {
  return request({
    url: `/api/refund/user/${userId}`,
    method: 'get',
    params: {
      pageNum: params.page || 1,
      pageSize: params.pageSize || 10
    }
  })
}

/**
 * 取消退款申请
 */
export function cancelRefund(refundId, userId) {
  return request({
    url: `/api/refund/${refundId}/cancel`,
    method: 'put',
    params: { userId }
  })
}

/**
 * 审核退款申请（管理端）
 */
export function reviewRefund(refundId, status) {
  return request({
    url: `/api/refund/${refundId}/review`,
    method: 'put',
    params: { status }
  })
}

/**
 * 获取订单的退款信息
 */
export function getOrderRefund(orderId) {
  return request({
    url: `/api/refund/order/${orderId}`,
    method: 'get'
  })
}

/**
 * 支付订单
 */
export function payOrder(orderNo, userId, paymentMethod = 1) {
  return request({
    url: `/api/orders/${orderNo}/pay`,
    method: 'put',
    params: { userId, paymentMethod }
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
export function getAdminOrderList(params = {}) {
  return request({
    url: '/api/orders/list',
    method: 'get',
    params: {
      pageNum: params.page,
      pageSize: params.pageSize,
      status: params.status,
      keyword: params.keyword,
      startTime: params.startDate,
      endTime: params.endDate
    }
  }).then(res => {
    return {
      list: res.records || [],
      total: res.total || 0,
      page: res.current || 1,
      pageSize: res.size || 10
    }
  })
}

/**
 * 获取订单详情（管理端）
 */
export function getAdminOrderDetail(id) {
  return request({
    url: `/api/orders/${id}`,
    method: 'get'
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
export function shipOrder(orderNo, dataOrExpressCompany, trackingNo) {
  let expressCompany, tracking
  if (typeof dataOrExpressCompany === 'object') {
    expressCompany = dataOrExpressCompany.expressCompany || '顺丰速运'
    tracking = dataOrExpressCompany.trackingNo || ''
  } else {
    expressCompany = dataOrExpressCompany || '顺丰速运'
    tracking = trackingNo || ''
  }
  
  return request({
    url: `/api/orders/${orderNo}/ship`,
    method: 'put',
    params: {
      expressCompany,
      trackingNo: tracking
    }
  })
}

/**
 * 商家取消订单（管理端）
 */
export function adminCancelOrder(id, data) {
  return request({
    url: `/admin/orders/${id}/cancel`,
    method: 'put',
    data
  })
}

/**
 * 添加订单备注（管理端）
 */
export function addOrderNote(id, data) {
  return request({
    url: `/admin/orders/${id}/note`,
    method: 'post',
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


