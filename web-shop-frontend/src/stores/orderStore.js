/**
 * 订单状态管理
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  createOrder as createOrderApi,
  getOrderList as getOrderListApi,
  getOrderDetail as getOrderDetailApi,
  cancelOrder as cancelOrderApi,
  payOrder as payOrderApi,
  confirmReceipt as confirmReceiptApi
} from '@/api/order'
import { useUserStore } from './userStore'

// 订单状态映射
const STATUS_MAP = {
  0: { text: '待支付', value: 'pending' },
  1: { text: '待发货', value: 'paid' },
  2: { text: '待收货', value: 'processing' },
  3: { text: '已完成', value: 'completed' },
  4: { text: '已取消', value: 'cancelled' },
  5: { text: '退款中', value: 'refunding' },
  6: { text: '已退款', value: 'refunded' }
}

/**
 * 转换订单数据格式
 */
const transformOrder = (order) => {
  if (!order) return null
  
  const statusInfo = STATUS_MAP[order.status] || { text: '未知', value: 'unknown' }
  
  return {
    id: order.id,
    orderNo: order.orderNo,
    userId: order.userId,
    status: order.status,
    statusText: statusInfo.text,
    totalAmount: order.totalAmount,
    payAmount: order.payAmount,
    freight: order.freight,
    paymentMethod: order.paymentMethod,
    paymentMethodText: order.paymentMethod === 1 ? '微信支付' : order.paymentMethod === 2 ? '支付宝' : '未知',
    receiverName: order.receiverName,
    receiverPhone: order.receiverPhone,
    receiverAddress: order.receiverAddress,
    expressCompany: order.expressCompany,
    trackingNo: order.trackingNo,
    note: order.note,
    createdAt: order.createdTime,
    paidAt: order.payTime,
    shippedAt: order.shipTime,
    completedAt: order.receiveTime,
    cancelTime: order.cancelTime,
    cancelReason: order.cancelReason,
    // 构建地址对象
    address: {
      receiverName: order.receiverName,
      phone: order.receiverPhone,
      province: '',
      city: '',
      district: '',
      detail: order.receiverAddress
    },
    // 转换订单项
    items: (order.items || []).map(item => ({
      id: item.id,
      orderId: item.orderId,
      productId: item.productId,
      productName: item.productName,
      image: item.productImage || item.image,
      specs: item.specInfo || item.specs,
      price: item.unitPrice || item.price,
      quantity: item.quantity
    }))
  }
}

export const useOrderStore = defineStore('order', () => {
  // 订单列表
  const orderList = ref([])
  
  // 当前订单详情
  const currentOrder = ref(null)
  
  // 加载状态
  const loading = ref(false)

  /**
   * 获取订单列表
   */
  const fetchOrderList = async (params = {}) => {
    try {
      loading.value = true
      const result = await getOrderListApi(params)
      // 后端返回IPage对象，数据在records字段中
      const records = result.records || []
      // 转换订单数据格式
      orderList.value = records.map(transformOrder)
      return result
    } catch (error) {
      console.error('获取订单列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取订单详情
   */
  const fetchOrderDetail = async (orderId) => {
    try {
      loading.value = true
      const order = await getOrderDetailApi(orderId)
      currentOrder.value = transformOrder(order)
      return currentOrder.value
    } catch (error) {
      console.error('获取订单详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 创建订单
   */
  const createOrder = async (orderData) => {
    try {
      loading.value = true
      const order = await createOrderApi(orderData)
      const transformedOrder = transformOrder(order)
      
      // 将新订单添加到列表开头
      if (orderList.value) {
        orderList.value.unshift(transformedOrder)
      }
      
      return transformedOrder
    } catch (error) {
      console.error('创建订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 支付订单
   */
  const payOrder = async (orderId, paymentMethod = 1) => {
    try {
      loading.value = true
      
      // 先找到订单获取orderNo
      const order = orderList.value.find(o => o.id === orderId) || currentOrder.value
      if (!order) {
        throw new Error('订单不存在')
      }
      
      const userStore = useUserStore()
      const userId = userStore.userInfo?.id || userStore.userInfo?.userId
      
      const result = await payOrderApi(order.orderNo, userId, paymentMethod)
      const updatedOrder = result.data || result
      const transformedOrder = transformOrder(updatedOrder)
      
      // 更新列表中的订单状态
      const index = orderList.value.findIndex(o => o.id === orderId)
      if (index !== -1) {
        orderList.value[index] = transformedOrder
      }
      
      // 更新当前订单
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = transformedOrder
      }
      
      return transformedOrder
    } catch (error) {
      console.error('支付订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 取消订单
   */
  const cancelOrder = async (orderId) => {
    try {
      loading.value = true
      
      // 先找到订单获取orderNo
      const order = orderList.value.find(o => o.id === orderId) || currentOrder.value
      if (!order) {
        throw new Error('订单不存在')
      }
      
      const userStore = useUserStore()
      const userId = userStore.userInfo?.id || userStore.userInfo?.userId
      
      const updatedOrder = await cancelOrderApi(order.orderNo, userId, '用户取消')
      const transformedOrder = transformOrder(updatedOrder)
      
      // 更新列表中的订单状态
      const index = orderList.value.findIndex(o => o.id === orderId)
      if (index !== -1) {
        orderList.value[index] = transformedOrder
      }
      
      // 更新当前订单
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = transformedOrder
      }
      
      return transformedOrder
    } catch (error) {
      console.error('取消订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 确认收货
   */
  const confirmReceipt = async (orderId) => {
    try {
      loading.value = true
      
      // 先找到订单获取orderNo
      const order = orderList.value.find(o => o.id === orderId) || currentOrder.value
      if (!order) {
        throw new Error('订单不存在')
      }
      
      const userStore = useUserStore()
      const userId = userStore.userInfo?.id || userStore.userInfo?.userId
      
      const updatedOrder = await confirmReceiptApi(order.orderNo, userId)
      const transformedOrder = transformOrder(updatedOrder)
      
      // 更新列表中的订单状态
      const index = orderList.value.findIndex(o => o.id === orderId)
      if (index !== -1) {
        orderList.value[index] = transformedOrder
      }
      
      // 更新当前订单
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = transformedOrder
      }
      
      return transformedOrder
    } catch (error) {
      console.error('确认收货失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 清空当前订单
   */
  const clearCurrentOrder = () => {
    currentOrder.value = null
  }

  return {
    orderList,
    currentOrder,
    loading,
    fetchOrderList,
    fetchOrderDetail,
    createOrder,
    payOrder,
    cancelOrder,
    confirmReceipt,
    clearCurrentOrder
  }
})

