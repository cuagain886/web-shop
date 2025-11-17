/**
 * 统计数据API
 */

import request from '@/utils/request'

/**
 * 获取销售概览数据
 */
export const getSalesOverview = async () => {
  try {
    const ordersRes = await request({
      url: '/api/orders/list',
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
    
    const todayOrders = orders.filter(order => {
      const orderDate = new Date(order.createdTime)
      return orderDate >= today
    })
    
    const todayPaidOrders = todayOrders.filter(order =>
      [1, 2, 3].includes(order.status)
    )
    
    const todaySales = todayPaidOrders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
    
    const thisMonthOrders = orders.filter(order => {
      const orderDate = new Date(order.createdTime)
      return orderDate >= thisMonth
    })
    
    const thisMonthPaidOrders = thisMonthOrders.filter(order =>
      [1, 2, 3].includes(order.status)
    )
    
    const thisMonthSales = thisMonthPaidOrders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
    
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

/**
 * 获取近7天销售趋势
 */
export const getSalesTrend = async (days = 7) => {
  try {
    const now = new Date()
    const result = []
    
    const ordersRes = await request({
      url: '/api/orders/list',
      method: 'get',
      params: {
        pageNum: 1,
        pageSize: 1000
      }
    })
    
    const orders = ordersRes?.records || []
    
    for (let i = days - 1; i >= 0; i--) {
      const date = new Date(now.getFullYear(), now.getMonth(), now.getDate() - i)
      const nextDate = new Date(date.getTime() + 24 * 60 * 60 * 1000)
      
      const dayOrders = orders.filter(order => {
        const orderDate = new Date(order.createdTime)
        return orderDate >= date && orderDate < nextDate &&
               [1, 2, 3].includes(order.status)
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

/**
 * 获取商品销售排行
 */
export const getProductRanking = async (type = 'sales', limit = 10) => {
  try {
    const ordersRes = await request({
      url: '/api/orders/list',
      method: 'get',
      params: {
        pageNum: 1,
        pageSize: 1000
      }
    })
    
    const orders = ordersRes?.records || []
    const completedOrders = orders.filter(order =>
      [1, 2, 3].includes(order.status)
    )
    
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
    
    let ranking = Object.values(productStats)
    
    if (type === 'quantity') {
      ranking.sort((a, b) => b.quantity - a.quantity)
    } else {
      ranking.sort((a, b) => b.sales - a.sales)
    }
    
    const total = ranking.reduce((sum, item) =>
      sum + (type === 'quantity' ? item.quantity : item.sales), 0
    )
    
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

/**
 * 获取订单状态分布
 */
export const getOrderStatusDistribution = async () => {
  try {
    const res = await request({
      url: '/api/orders/status-distribution',
      method: 'get'
    })
    
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
