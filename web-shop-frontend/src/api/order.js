/**
 * 订单相关 API
 */

import request from '@/utils/request'

// ========== Mock 数据（开发阶段使用）==========
const MOCK_ENABLED = false // 是否启用Mock数据
export const ORDER_STORAGE_KEY = 'mock_order_data' // localStorage存储key（导出供其他模块使用）

// 从localStorage获取订单数据
const getOrdersFromStorage = () => {
  try {
    const data = localStorage.getItem(ORDER_STORAGE_KEY)
    if (data) {
      return JSON.parse(data)
    }
  } catch (error) {
    console.error('读取订单数据失败:', error)
  }
  
  // 返回默认订单数据
  const defaultOrders = [
    {
      id: 1001,
      orderNo: 'ORDER20231101001',
      status: 'pending', // pending-待支付, paid-已支付, shipped-已发货, completed-已完成, cancelled-已取消
      statusText: '待支付',
      totalAmount: 9999,
      items: [
        {
          id: 1,
          productId: 1,
          productName: 'iPhone 15 Pro Max',
          image: 'https://placehold.co/100x100/409eff/fff?text=iPhone',
          specs: '原色钛金属 256GB',
          price: 9999,
          quantity: 1
        }
      ],
      address: {
        receiverName: '张三',
        phone: '13800138000',
        province: '北京市',
        city: '北京市',
        district: '朝阳区',
        detail: '某某街道某某小区1号楼1单元101室'
      },
      paymentMethod: 'wechat',
      paymentMethodText: '微信支付',
      createdAt: new Date(Date.now() - 3600000).toISOString(), // 1小时前
      paidAt: null,
      shippedAt: null,
      completedAt: null
    },
    {
      id: 1002,
      orderNo: 'ORDER20231031002',
      status: 'shipped',
      statusText: '已发货',
      totalAmount: 5999,
      items: [
        {
          id: 2,
          productId: 2,
          productName: 'MacBook Pro 14',
          image: 'https://placehold.co/100x100/67c23a/fff?text=MacBook',
          specs: '深空灰 512GB',
          price: 5999,
          quantity: 1
        }
      ],
      address: {
        receiverName: '张三',
        phone: '13800138000',
        province: '北京市',
        city: '北京市',
        district: '朝阳区',
        detail: '某某街道某某小区1号楼1单元101室'
      },
      paymentMethod: 'alipay',
      paymentMethodText: '支付宝',
      trackingNo: 'SF1234567890',
      createdAt: new Date(Date.now() - 86400000 * 2).toISOString(), // 2天前
      paidAt: new Date(Date.now() - 86400000 * 2 + 300000).toISOString(),
      shippedAt: new Date(Date.now() - 86400000).toISOString(),
      completedAt: null
    },
    {
      id: 1003,
      orderNo: 'ORDER20231030003',
      status: 'completed',
      statusText: '已完成',
      totalAmount: 1999,
      items: [
        {
          id: 3,
          productId: 3,
          productName: 'AirPods Pro 2',
          image: 'https://placehold.co/100x100/e6a23c/fff?text=AirPods',
          specs: '白色',
          price: 1999,
          quantity: 1
        }
      ],
      address: {
        receiverName: '李四',
        phone: '13800138001',
        province: '上海市',
        city: '上海市',
        district: '浦东新区',
        detail: '某某路某某大厦10楼1001室'
      },
      paymentMethod: 'wechat',
      paymentMethodText: '微信支付',
      createdAt: new Date(Date.now() - 86400000 * 7).toISOString(), // 7天前
      paidAt: new Date(Date.now() - 86400000 * 7 + 300000).toISOString(),
      shippedAt: new Date(Date.now() - 86400000 * 5).toISOString(),
      completedAt: new Date(Date.now() - 86400000 * 3).toISOString()
    }
  ]
  
  // 保存默认数据
  saveOrdersToStorage(defaultOrders)
  return defaultOrders
}

// 保存订单数据到localStorage
const saveOrdersToStorage = (orders) => {
  try {
    localStorage.setItem(ORDER_STORAGE_KEY, JSON.stringify(orders))
  } catch (error) {
    console.error('保存订单数据失败:', error)
  }
}

let nextOrderId = 1004

/**
 * 创建订单
 */
export function createOrder(data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        
        const newOrder = {
          id: nextOrderId++,
          orderNo: `ORDER${new Date().getFullYear()}${String(new Date().getMonth() + 1).padStart(2, '0')}${String(new Date().getDate()).padStart(2, '0')}${String(nextOrderId).padStart(3, '0')}`,
          status: 'pending',
          statusText: '待支付',
          totalAmount: data.totalAmount,
          items: data.items,
          address: data.address,
          paymentMethod: data.paymentMethod || 'wechat',
          paymentMethodText: data.paymentMethod === 'alipay' ? '支付宝' : '微信支付',
          createdAt: new Date().toISOString(),
          paidAt: null,
          shippedAt: null,
          completedAt: null
        }
        
        orders.unshift(newOrder) // 新订单放在最前面
        saveOrdersToStorage(orders)
        
        console.log('📦 创建订单成功:', newOrder)
        resolve(newOrder)
      }, 500)
    })
  }
  
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

/**
 * 获取订单列表
 */
export function getOrderList(params = {}) {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        let orders = getOrdersFromStorage()
        
        // 按状态筛选
        if (params.status && params.status !== 'all') {
          orders = orders.filter(order => order.status === params.status)
        }
        
        // 按关键词搜索（订单号或商品名）
        if (params.keyword) {
          orders = orders.filter(order =>
            order.orderNo.includes(params.keyword) ||
            order.items.some(item => item.productName.includes(params.keyword))
          )
        }
        
        console.log('📦 获取订单列表:', orders)
        resolve(orders)
      }, 300)
    })
  }
  
  // 使用用户订单接口
  const { userId, page = 1, pageSize = 10, status } = params
  return request({
    url: `/orders/user/${userId}`,
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
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        const order = orders.find(o => o.id === Number(id))
        
        if (order) {
          console.log('📦 获取订单详情:', order)
          resolve(order)
        } else {
          reject(new Error('订单不存在'))
        }
      }, 300)
    })
  }
  
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

/**
 * 取消订单
 */
export function cancelOrder(id) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        const order = orders.find(o => o.id === Number(id))
        
        if (!order) {
          reject(new Error('订单不存在'))
          return
        }
        
        if (order.status !== 'pending') {
          reject(new Error('当前订单状态不允许取消'))
          return
        }
        
        order.status = 'cancelled'
        order.statusText = '已取消'
        order.cancelledAt = new Date().toISOString()
        
        saveOrdersToStorage(orders)
        console.log('📦 取消订单成功:', order)
        resolve(order)
      }, 300)
    })
  }
  
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
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        const order = orders.find(o => o.id === Number(id))
        
        if (!order) {
          reject(new Error('订单不存在'))
          return
        }
        
        if (order.status !== 'shipped') {
          reject(new Error('当前订单状态不允许确认收货'))
          return
        }
        
        order.status = 'completed'
        order.statusText = '已完成'
        order.completedAt = new Date().toISOString()
        
        saveOrdersToStorage(orders)
        console.log('📦 确认收货成功:', order)
        resolve(order)
      }, 300)
    })
  }
  
  return request({
    url: `/order/${id}/confirm`,
    method: 'put'
  })
}

/**
 * 申请退款
 */
export function applyRefund(id, data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        const order = orders.find(o => o.id === Number(id))
        
        if (!order) {
          reject(new Error('订单不存在'))
          return
        }
        
        order.refundReason = data.reason
        order.refundStatus = 'pending' // pending-待审核, approved-已通过, rejected-已拒绝
        order.refundAppliedAt = new Date().toISOString()
        
        saveOrdersToStorage(orders)
        console.log('📦 申请退款成功:', order)
        resolve(order)
      }, 300)
    })
  }
  
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
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        const order = orders.find(o => o.id === Number(id))
        
        if (!order) {
          reject(new Error('订单不存在'))
          return
        }
        
        if (order.status !== 'pending') {
          reject(new Error('当前订单状态不允许支付'))
          return
        }
        
        order.status = 'paid'
        order.statusText = '已支付'
        order.paidAt = new Date().toISOString()
        if (data.paymentMethod) {
          order.paymentMethod = data.paymentMethod
          order.paymentMethodText = data.paymentMethod === 'alipay' ? '支付宝' : '微信支付'
        }
        
        saveOrdersToStorage(orders)
        console.log('📦 支付订单成功:', order)
        resolve(order)
      }, 1000) // 模拟支付延迟
    })
  }
  
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
export function getAdminOrderList(params = {}) {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        let orders = getOrdersFromStorage()
        
        // 按状态筛选
        if (params.status && params.status !== 'all') {
          orders = orders.filter(order => order.status === params.status)
        }
        
        // 按时间范围筛选
        if (params.startDate) {
          const startTime = new Date(params.startDate).getTime()
          orders = orders.filter(order => new Date(order.createdAt).getTime() >= startTime)
        }
        if (params.endDate) {
          const endTime = new Date(params.endDate).getTime() + 86400000 // 加一天
          orders = orders.filter(order => new Date(order.createdAt).getTime() < endTime)
        }
        
        // 按关键词搜索（订单号）
        if (params.keyword) {
          orders = orders.filter(order => order.orderNo.includes(params.keyword))
        }
        
        // 分页
        const page = params.page || 1
        const pageSize = params.pageSize || 10
        const total = orders.length
        const start = (page - 1) * pageSize
        const end = start + pageSize
        const list = orders.slice(start, end)
        
        console.log('📦 获取管理端订单列表:', { total, page, pageSize, list: list.length })
        resolve({
          list,
          total,
          page,
          pageSize,
          totalPages: Math.ceil(total / pageSize)
        })
      }, 300)
    })
  }
  
  return request({
    url: '/admin/orders',
    method: 'get',
    params
  })
}

/**
 * 获取订单详情（管理端）
 */
export function getAdminOrderDetail(id) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        const order = orders.find(o => o.id === Number(id))
        
        if (order) {
          console.log('📦 获取管理端订单详情:', order)
          resolve(order)
        } else {
          reject(new Error('订单不存在'))
        }
      }, 300)
    })
  }
  
  return request({
    url: `/admin/orders/${id}`,
    method: 'get'
  })
}

/**
 * 更新订单状态（管理端）
 */
export function updateOrderStatus(id, data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        const order = orders.find(o => o.id === Number(id))
        
        if (!order) {
          reject(new Error('订单不存在'))
          return
        }
        
        order.status = data.status
        order.statusText = getStatusText(data.status)
        order.updatedAt = new Date().toISOString()
        
        saveOrdersToStorage(orders)
        console.log('📦 更新订单状态成功:', { id, status: data.status })
        resolve(order)
      }, 300)
    })
  }
  
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
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        const order = orders.find(o => o.id === Number(id))
        
        if (!order) {
          reject(new Error('订单不存在'))
          return
        }
        
        if (order.status !== 'paid') {
          reject(new Error('只有已支付的订单才能发货'))
          return
        }
        
        order.status = 'shipped'
        order.statusText = '已发货'
        order.trackingNo = data.trackingNo || ''
        order.shippedAt = new Date().toISOString()
        order.updatedAt = new Date().toISOString()
        
        saveOrdersToStorage(orders)
        console.log('📦 订单发货成功:', order)
        resolve(order)
      }, 500)
    })
  }
  
  return request({
    url: `/admin/orders/${id}/ship`,
    method: 'put',
    data
  })
}

/**
 * 商家取消订单（管理端）
 */
export function adminCancelOrder(id, data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        const order = orders.find(o => o.id === Number(id))
        
        if (!order) {
          reject(new Error('订单不存在'))
          return
        }
        
        if (order.status !== 'pending') {
          reject(new Error('只有待支付的订单才能取消'))
          return
        }
        
        order.status = 'cancelled'
        order.statusText = '已取消'
        order.cancelReason = data.reason || '商家取消'
        order.cancelledAt = new Date().toISOString()
        order.updatedAt = new Date().toISOString()
        
        saveOrdersToStorage(orders)
        console.log('📦 商家取消订单成功:', order)
        resolve(order)
      }, 300)
    })
  }
  
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
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const orders = getOrdersFromStorage()
        const order = orders.find(o => o.id === Number(id))
        
        if (!order) {
          reject(new Error('订单不存在'))
          return
        }
        
        if (!order.notes) {
          order.notes = []
        }
        
        order.notes.push({
          content: data.note,
          createdAt: new Date().toISOString()
        })
        order.updatedAt = new Date().toISOString()
        
        saveOrdersToStorage(orders)
        console.log('📦 添加订单备注成功:', order)
        resolve(order)
      }, 300)
    })
  }
  
  return request({
    url: `/admin/orders/${id}/note`,
    method: 'post',
    data
  })
}

// 辅助函数：根据状态获取状态文本
function getStatusText(status) {
  const statusMap = {
    pending: '待支付',
    paid: '已支付',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || '未知状态'
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


