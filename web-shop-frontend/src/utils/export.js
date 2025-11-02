/**
 * 导出工具
 */

/**
 * 导出订单为CSV
 * @param {Array} orders - 订单列表
 * @param {String} filename - 文件名
 */
export const exportOrdersToCSV = (orders, filename = '订单导出') => {
  try {
    // CSV表头
    const headers = [
      '订单号',
      '下单时间',
      '收货人',
      '联系电话',
      '收货地址',
      '商品信息',
      '订单金额',
      '支付方式',
      '订单状态',
      '支付时间',
      '发货时间',
      '完成时间'
    ]

    // 转换数据为CSV行
    const rows = orders.map(order => {
      // 商品信息
      const itemsInfo = order.items
        .map(item => `${item.productName} x${item.quantity}`)
        .join('; ')

      // 收货地址
      const address = order.address
        ? `${order.address.province}${order.address.city}${order.address.district}${order.address.detail}`
        : '-'

      return [
        order.orderNo,
        formatDateTime(order.createdAt),
        order.address?.receiverName || '-',
        order.address?.phone || '-',
        address,
        itemsInfo,
        order.totalAmount.toFixed(2),
        order.paymentMethodText || '-',
        order.statusText,
        order.paidAt ? formatDateTime(order.paidAt) : '-',
        order.shippedAt ? formatDateTime(order.shippedAt) : '-',
        order.completedAt ? formatDateTime(order.completedAt) : '-'
      ]
    })

    // 组合CSV内容
    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n')

    // 添加BOM以支持Excel正确识别UTF-8编码
    const BOM = '\uFEFF'
    const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })

    // 触发下载
    downloadBlob(blob, `${filename}_${formatDate(new Date())}.csv`)

    return true
  } catch (error) {
    console.error('导出CSV失败:', error)
    return false
  }
}

/**
 * 导出商品为CSV
 * @param {Array} products - 商品列表
 * @param {String} filename - 文件名
 */
export const exportProductsToCSV = (products, filename = '商品导出') => {
  try {
    // CSV表头
    const headers = [
      'ID',
      '商品名称',
      '分类',
      '价格',
      '原价',
      '库存',
      '销量',
      '状态',
      '创建时间'
    ]

    // 转换数据为CSV行
    const rows = products.map(product => [
      product.id,
      product.name,
      product.categoryName || '-',
      product.price,
      product.originalPrice || '-',
      product.stock,
      product.sales || 0,
      getStatusText(product.status),
      formatDateTime(product.createdAt || new Date())
    ])

    // 组合CSV内容
    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n')

    // 添加BOM
    const BOM = '\uFEFF'
    const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })

    // 触发下载
    downloadBlob(blob, `${filename}_${formatDate(new Date())}.csv`)

    return true
  } catch (error) {
    console.error('导出CSV失败:', error)
    return false
  }
}

/**
 * 下载Blob
 * @param {Blob} blob - 文件Blob
 * @param {String} filename - 文件名
 */
const downloadBlob = (blob, filename) => {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.style.display = 'none'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateString) => {
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

/**
 * 格式化日期
 */
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}${month}${day}`
}

/**
 * 获取状态文本
 */
const getStatusText = (status) => {
  const textMap = {
    active: '上架中',
    inactive: '已下架',
    pending: '待上架'
  }
  return textMap[status] || '未知'
}

