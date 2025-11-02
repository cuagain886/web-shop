/**
 * 操作日志工具
 */

const LOG_STORAGE_KEY = 'operation_logs'

/**
 * 操作类型枚举
 */
export const OperationType = {
  PRODUCT_ADD: 'product_add',
  PRODUCT_EDIT: 'product_edit',
  PRODUCT_DELETE: 'product_delete',
  PRODUCT_STATUS: 'product_status',
  PRODUCT_STOCK: 'product_stock',
  ORDER_SHIP: 'order_ship',
  ORDER_CANCEL: 'order_cancel',
  ORDER_NOTE: 'order_note',
  CATEGORY_ADD: 'category_add',
  CATEGORY_EDIT: 'category_edit',
  CATEGORY_DELETE: 'category_delete',
  PASSWORD_CHANGE: 'password_change',
  PROFILE_UPDATE: 'profile_update'
}

/**
 * 操作类型中文映射
 */
const operationTypeMap = {
  [OperationType.PRODUCT_ADD]: '新增商品',
  [OperationType.PRODUCT_EDIT]: '编辑商品',
  [OperationType.PRODUCT_DELETE]: '删除商品',
  [OperationType.PRODUCT_STATUS]: '修改商品状态',
  [OperationType.PRODUCT_STOCK]: '修改商品库存',
  [OperationType.ORDER_SHIP]: '订单发货',
  [OperationType.ORDER_CANCEL]: '取消订单',
  [OperationType.ORDER_NOTE]: '添加订单备注',
  [OperationType.CATEGORY_ADD]: '新增分类',
  [OperationType.CATEGORY_EDIT]: '编辑分类',
  [OperationType.CATEGORY_DELETE]: '删除分类',
  [OperationType.PASSWORD_CHANGE]: '修改密码',
  [OperationType.PROFILE_UPDATE]: '更新个人信息'
}

/**
 * 操作类别映射
 */
const operationCategoryMap = {
  [OperationType.PRODUCT_ADD]: '商品管理',
  [OperationType.PRODUCT_EDIT]: '商品管理',
  [OperationType.PRODUCT_DELETE]: '商品管理',
  [OperationType.PRODUCT_STATUS]: '商品管理',
  [OperationType.PRODUCT_STOCK]: '商品管理',
  [OperationType.ORDER_SHIP]: '订单管理',
  [OperationType.ORDER_CANCEL]: '订单管理',
  [OperationType.ORDER_NOTE]: '订单管理',
  [OperationType.CATEGORY_ADD]: '商品管理',
  [OperationType.CATEGORY_EDIT]: '商品管理',
  [OperationType.CATEGORY_DELETE]: '商品管理',
  [OperationType.PASSWORD_CHANGE]: '账户设置',
  [OperationType.PROFILE_UPDATE]: '账户设置'
}

/**
 * 记录操作日志
 * @param {string} type - 操作类型
 * @param {string} operator - 操作人
 * @param {string} content - 操作内容
 * @param {string} target - 操作对象（可选）
 */
export const recordLog = (type, operator, content, target = '') => {
  try {
    const logs = JSON.parse(localStorage.getItem(LOG_STORAGE_KEY) || '[]')
    
    const log = {
      id: Date.now() + Math.random().toString(36).substr(2, 9),
      type,
      typeName: operationTypeMap[type] || '未知操作',
      category: operationCategoryMap[type] || '其他',
      operator,
      content,
      target,
      createdAt: new Date().toISOString()
    }

    logs.unshift(log) // 添加到开头，最新的在前面

    // 只保留最近1000条记录
    if (logs.length > 1000) {
      logs.splice(1000)
    }

    localStorage.setItem(LOG_STORAGE_KEY, JSON.stringify(logs))
    
    console.log('📝 操作日志已记录:', log)
  } catch (error) {
    console.error('❌ 记录操作日志失败:', error)
  }
}

/**
 * 获取操作日志列表
 * @param {Object} params - 查询参数
 * @returns {Object} 分页数据
 */
export const getOperationLogs = (params = {}) => {
  try {
    const {
      page = 1,
      pageSize = 20,
      category = '',
      keyword = '',
      startDate = '',
      endDate = ''
    } = params

    let logs = JSON.parse(localStorage.getItem(LOG_STORAGE_KEY) || '[]')

    // 筛选
    if (category) {
      logs = logs.filter(log => log.category === category)
    }

    if (keyword) {
      logs = logs.filter(log => 
        log.content.includes(keyword) || 
        log.operator.includes(keyword) ||
        log.target.includes(keyword)
      )
    }

    if (startDate) {
      logs = logs.filter(log => new Date(log.createdAt) >= new Date(startDate))
    }

    if (endDate) {
      const endDateTime = new Date(endDate)
      endDateTime.setHours(23, 59, 59, 999)
      logs = logs.filter(log => new Date(log.createdAt) <= endDateTime)
    }

    // 分页
    const total = logs.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = logs.slice(start, end)

    return {
      list,
      total,
      page,
      pageSize
    }
  } catch (error) {
    console.error('❌ 获取操作日志失败:', error)
    return {
      list: [],
      total: 0,
      page: 1,
      pageSize: 20
    }
  }
}

/**
 * 清空操作日志
 */
export const clearOperationLogs = () => {
  try {
    localStorage.removeItem(LOG_STORAGE_KEY)
    console.log('🗑️ 操作日志已清空')
  } catch (error) {
    console.error('❌ 清空操作日志失败:', error)
  }
}

