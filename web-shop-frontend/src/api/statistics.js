/**
 * 统计数据API
 */

import request from '@/utils/request'
import { ORDER_STORAGE_KEY } from './order'

const MOCK_ENABLED = false

/**
 * 获取销售概览数据
 */
export const getSalesOverview = async () => {
  if (!MOCK_ENABLED) {
    try {
      // 前端基于订单列表计算概览数据
      const ordersRes = await request({
        url: '/orders/list',
        method: 'get',
        params: {
          pageNum: 1,
          pageSize: 1000
        }
      })
      
      const orders = ordersRes?.records || []
      const now = new Date()
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
      const thisMonth = new Date(now.getFullYear(), now.getMonth(), 1)
      
      // 今日数据
      const todayOrders = orders.filter(order => {
        const orderDate = new Date(order.createdTime)
        return orderDate >= today
      })
      
      const todayPaidOrders = todayOrders.filter(order =>
        [1, 2, 3].includes(order.status) // 已支付、已发货、已完成
      )
      
      const todaySales = todayPaidOrders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
      
      // 本月数据
      const thisMonthOrders = orders.filter(order => {
        const orderDate = new Date(order.createdTime)
        return orderDate >= thisMonth
      })
      
      const thisMonthPaidOrders = thisMonthOrders.filter(order =>
        [1, 2, 3].includes(order.status) // 已支付、已发货、已完成
      )
      
      const thisMonthSales = thisMonthPaidOrders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
      
      // 客单价
      const avgOrderAmount = thisMonthPaidOrders.length > 0
        ? thisMonthSales / thisMonthPaidOrders.length
        : 0
      
      return {
        today: {
          orderCount: todayOrders.length,
          sales: todaySales,
          paidOrderCount: todayPaidOrders.length
        },
        thisMonth: {
          orderCount: thisMonthOrders.length,
          sales: thisMonthSales,
          paidOrderCount: thisMonthPaidOrders.length,
          avgOrderAmount
        }
      }
    } catch (error) {
      console.error('获取销售概览失败:', error)
      return {
        today: {
          orderCount: 0,
          sales: 0,
          paidOrderCount: 0
        },
        thisMonth: {
          orderCount: 0,
          sales: 0,
          paidOrderCount: 0,
          avgOrderAmount: 0
        }
      }
    }
  }

  // Mock实现
  const orders = JSON.parse(localStorage.getItem(ORDER_STORAGE_KEY) || '[]')
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const thisMonth = new Date(now.getFullYear(), now.getMonth(), 1)

  // 今日数据
  const todayOrders = orders.filter(order => {
    const orderDate = new Date(order.createdAt)
    return orderDate >= today
  })

  const todayPaidOrders = todayOrders.filter(order => 
    ['paid', 'shipped', 'completed'].includes(order.status)
  )

  const todaySales = todayPaidOrders.reduce((sum, order) => sum + order.totalAmount, 0)

  // 本月数据
  const thisMonthOrders = orders.filter(order => {
    const orderDate = new Date(order.createdAt)
    return orderDate >= thisMonth
  })

  const thisMonthPaidOrders = thisMonthOrders.filter(order => 
    ['paid', 'shipped', 'completed'].includes(order.status)
  )

  const thisMonthSales = thisMonthPaidOrders.reduce((sum, order) => sum + order.totalAmount, 0)

  // 客单价
  const avgOrderAmount = thisMonthPaidOrders.length > 0 
    ? thisMonthSales / thisMonthPaidOrders.length 
    : 0

  return {
    today: {
      orderCount: todayOrders.length,
      sales: todaySales,
      paidOrderCount: todayPaidOrders.length
    },
    thisMonth: {
      orderCount: thisMonthOrders.length,
      sales: thisMonthSales,
      paidOrderCount: thisMonthPaidOrders.length,
      avgOrderAmount
    }
  }
}

/**
 * 获取近7天销售趋势
 */
export const getSalesTrend = async (days = 7) => {
  if (!MOCK_ENABLED) {
    try {
      // 前端基于订单列表计算趋势数据
      const now = new Date()
      const result = []
      
      // 获取近7天的所有订单（不传时间参数，获取所有订单后前端筛选）
      const ordersRes = await request({
        url: '/orders/list',
        method: 'get',
        params: {
          pageNum: 1,
          pageSize: 1000
        }
      })
      
      const orders = ordersRes?.records || []
      
      // 按日期统计
      for (let i = days - 1; i >= 0; i--) {
        const date = new Date(now.getFullYear(), now.getMonth(), now.getDate() - i)
        const nextDate = new Date(date.getTime() + 24 * 60 * 60 * 1000)
        
        const dayOrders = orders.filter(order => {
          const orderDate = new Date(order.createdTime)
          return orderDate >= date && orderDate < nextDate &&
                 [1, 2, 3].includes(order.status) // 已支付、已发货、已完成
        })
        
        const sales = dayOrders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
        
        result.push({
          date: `${date.getMonth() + 1}/${date.getDate()}`,
          sales: sales,
          orderCount: dayOrders.length
        })
      }
      
      return result
    } catch (error) {
      console.error('获取销售趋势失败:', error)
      return []
    }
  }

  // Mock实现
  const orders = JSON.parse(localStorage.getItem(ORDER_STORAGE_KEY) || '[]')
  const now = new Date()
  const result = []

  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(now.getFullYear(), now.getMonth(), now.getDate() - i)
    const nextDate = new Date(date.getTime() + 24 * 60 * 60 * 1000)
    
    const dayOrders = orders.filter(order => {
      const orderDate = new Date(order.createdAt)
      return orderDate >= date && orderDate < nextDate && 
             ['paid', 'shipped', 'completed'].includes(order.status)
    })

    const sales = dayOrders.reduce((sum, order) => sum + order.totalAmount, 0)

    result.push({
      date: `${date.getMonth() + 1}/${date.getDate()}`,
      sales: sales,
      orderCount: dayOrders.length
    })
  }

  return result
}

/**
 * 获取商品销售排行
 */
export const getProductRanking = async (type = 'sales', limit = 10) => {
  if (!MOCK_ENABLED) {
    try {
      // 前端基于订单数据计算商品排行
      const ordersRes = await request({
        url: '/orders/list',
        method: 'get',
        params: {
          pageNum: 1,
          pageSize: 1000
        }
      })
      
      const orders = ordersRes?.records || []
      const completedOrders = orders.filter(order =>
        [1, 2, 3].includes(order.status) // 已支付、已发货、已完成
      )
      
      // 统计每个商品的销量和销售额
      const productStats = {}
      
      completedOrders.forEach(order => {
        if (order.items && Array.isArray(order.items)) {
          order.items.forEach(item => {
            const productId = item.productId
            if (!productStats[productId]) {
              productStats[productId] = {
                productId: productId,
                productName: item.productName,
                image: item.productImage,
                quantity: 0,
                sales: 0
              }
            }
            productStats[productId].quantity += item.quantity
            productStats[productId].sales += Number(item.unitPrice) * item.quantity
          })
        }
      })
      
      // 转换为数组并排序
      let ranking = Object.values(productStats)
      
      if (type === 'quantity') {
        ranking.sort((a, b) => b.quantity - a.quantity)
      } else {
        ranking.sort((a, b) => b.sales - a.sales)
      }
      
      // 计算总量用于占比
      const total = ranking.reduce((sum, item) =>
        sum + (type === 'quantity' ? item.quantity : item.sales), 0
      )
      
      // 取前N名并计算占比
      ranking = ranking.slice(0, limit).map(item => ({
        ...item,
        percentage: total > 0
          ? ((type === 'quantity' ? item.quantity : item.sales) / total * 100).toFixed(2)
          : 0
      }))
      
      return ranking
    } catch (error) {
      console.error('获取商品排行失败:', error)
      return []
    }
  }

  // Mock实现
  const orders = JSON.parse(localStorage.getItem(ORDER_STORAGE_KEY) || '[]')
  const completedOrders = orders.filter(order => 
    ['paid', 'shipped', 'completed'].includes(order.status)
  )

  // 统计每个商品的销量和销售额
  const productStats = {}

  completedOrders.forEach(order => {
    order.items.forEach(item => {
      if (!productStats[item.productId]) {
        productStats[item.productId] = {
          productId: item.productId,
          productName: item.productName,
          image: item.image,
          quantity: 0,
          sales: 0
        }
      }
      productStats[item.productId].quantity += item.quantity
      productStats[item.productId].sales += item.price * item.quantity
    })
  })

  // 转换为数组并排序
  let ranking = Object.values(productStats)

  if (type === 'quantity') {
    ranking.sort((a, b) => b.quantity - a.quantity)
  } else {
    ranking.sort((a, b) => b.sales - a.sales)
  }

  // 计算总量用于占比
  const total = ranking.reduce((sum, item) => 
    sum + (type === 'quantity' ? item.quantity : item.sales), 0
  )

  // 取前N名并计算占比
  ranking = ranking.slice(0, limit).map(item => ({
    ...item,
    percentage: total > 0 
      ? ((type === 'quantity' ? item.quantity : item.sales) / total * 100).toFixed(2)
      : 0
  }))

  return ranking
}

/**
 * 获取订单状态分布
 */
export const getOrderStatusDistribution = async () => {
  if (!MOCK_ENABLED) {
    try {
      const res = await request({
        url: '/orders/status-distribution',
        method: 'get'
      })
      
      // 转换后端返回的数据格式
      const statusMap = {
        0: '待付款',
        1: '待发货',
        2: '待收货',
        3: '已完成',
        4: '已取消',
        5: '退款中',
        6: '已退款',
        7: '已退款'
      }
      
      const total = Object.values(res || {}).reduce((sum, count) => sum + count, 0)
      
      return Object.entries(res || {}).map(([status, count]) => ({
        name: statusMap[status] || '未知',
        value: count,
        percentage: total > 0 ? (count / total * 100).toFixed(2) : 0
      }))
    } catch (error) {
      console.error('获取订单状态分布失败:', error)
      return []
    }
  }

  // Mock实现
  const orders = JSON.parse(localStorage.getItem(ORDER_STORAGE_KEY) || '[]')
  
  const statusCount = {
    pending: 0,
    paid: 0,
    shipped: 0,
    completed: 0,
    cancelled: 0
  }

  orders.forEach(order => {
    if (statusCount.hasOwnProperty(order.status)) {
      statusCount[order.status]++
    }
  })

  const statusMap = {
    pending: '待支付',
    paid: '已支付',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  }

  const total = orders.length

  return Object.entries(statusCount).map(([status, count]) => ({
    name: statusMap[status],
    value: count,
    percentage: total > 0 ? (count / total * 100).toFixed(2) : 0
  }))
}

