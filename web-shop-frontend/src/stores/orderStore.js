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
      const orders = await getOrderListApi(params)
      orderList.value = orders
      return orders
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
      currentOrder.value = order
      return order
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
      
      // 将新订单添加到列表开头
      if (orderList.value) {
        orderList.value.unshift(order)
      }
      
      return order
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
  const payOrder = async (orderId, paymentData) => {
    try {
      loading.value = true
      const order = await payOrderApi(orderId, paymentData)
      
      // 更新列表中的订单状态
      const index = orderList.value.findIndex(o => o.id === orderId)
      if (index !== -1) {
        orderList.value[index] = order
      }
      
      // 更新当前订单
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = order
      }
      
      return order
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
      const order = await cancelOrderApi(orderId)
      
      // 更新列表中的订单状态
      const index = orderList.value.findIndex(o => o.id === orderId)
      if (index !== -1) {
        orderList.value[index] = order
      }
      
      // 更新当前订单
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = order
      }
      
      return order
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
      const order = await confirmReceiptApi(orderId)
      
      // 更新列表中的订单状态
      const index = orderList.value.findIndex(o => o.id === orderId)
      if (index !== -1) {
        orderList.value[index] = order
      }
      
      // 更新当前订单
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = order
      }
      
      return order
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

