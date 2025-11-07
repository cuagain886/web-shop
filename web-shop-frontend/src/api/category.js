/**
 * 商品分类相关 API
 */

import request from '@/utils/request'

// ========== Mock 数据（开发阶段使用）==========
const MOCK_ENABLED = false // 是否启用Mock数据
const CATEGORY_STORAGE_KEY = 'mock_category_data' // localStorage存储key

// 从localStorage获取分类数据
const getCategoriesFromStorage = () => {
  try {
    const data = localStorage.getItem(CATEGORY_STORAGE_KEY)
    if (data) {
      return JSON.parse(data)
    }
  } catch (error) {
    console.error('读取分类数据失败:', error)
  }
  
  // 返回默认分类数据
  const defaultCategories = [
    // 一级分类
    { id: 1, name: '数码产品', parentId: 0, level: 1, sort: 1, status: 'active', createdAt: new Date().toISOString() },
    { id: 2, name: '服装鞋包', parentId: 0, level: 1, sort: 2, status: 'active', createdAt: new Date().toISOString() },
    { id: 3, name: '食品饮料', parentId: 0, level: 1, sort: 3, status: 'active', createdAt: new Date().toISOString() },
    { id: 4, name: '家居生活', parentId: 0, level: 1, sort: 4, status: 'active', createdAt: new Date().toISOString() },
    
    // 二级分类（数码产品）
    { id: 11, name: '手机通讯', parentId: 1, level: 2, sort: 1, status: 'active', createdAt: new Date().toISOString() },
    { id: 12, name: '电脑办公', parentId: 1, level: 2, sort: 2, status: 'active', createdAt: new Date().toISOString() },
    { id: 13, name: '数码配件', parentId: 1, level: 2, sort: 3, status: 'active', createdAt: new Date().toISOString() },
    
    // 二级分类（服装鞋包）
    { id: 21, name: '男装', parentId: 2, level: 2, sort: 1, status: 'active', createdAt: new Date().toISOString() },
    { id: 22, name: '女装', parentId: 2, level: 2, sort: 2, status: 'active', createdAt: new Date().toISOString() },
    { id: 23, name: '运动鞋', parentId: 2, level: 2, sort: 3, status: 'active', createdAt: new Date().toISOString() }
  ]
  
  // 保存默认数据
  saveCategoriesToStorage(defaultCategories)
  return defaultCategories
}

// 保存分类数据到localStorage
const saveCategoriesToStorage = (categories) => {
  try {
    localStorage.setItem(CATEGORY_STORAGE_KEY, JSON.stringify(categories))
  } catch (error) {
    console.error('保存分类数据失败:', error)
  }
}

let nextCategoryId = 100

/**
 * 获取分类列表
 */
export function getCategoryList(params = {}) {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        let categories = getCategoriesFromStorage()
        
        // 按父级分类筛选
        if (params.parentId !== undefined) {
          categories = categories.filter(cat => cat.parentId === params.parentId)
        }
        
        // 按层级筛选
        if (params.level) {
          categories = categories.filter(cat => cat.level === params.level)
        }
        
        // 按状态筛选
        if (params.status) {
          categories = categories.filter(cat => cat.status === params.status)
        }
        
        // 按排序字段排序
        categories.sort((a, b) => a.sort - b.sort)
        
        console.log('📂 获取分类列表:', categories)
        resolve(categories)
      }, 200)
    })
  }
  
  return request({
    url: '/category/list',
    method: 'get',
    params
  })
}

/**
 * 获取分类树（层级结构）
 */
export function getCategoryTree() {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const categories = getCategoriesFromStorage()
        
        // 构建树形结构
        const tree = []
        const map = {}
        
        // 先创建映射
        categories.forEach(cat => {
          map[cat.id] = { ...cat, children: [] }
        })
        
        // 构建树
        categories.forEach(cat => {
          if (cat.parentId === 0) {
            tree.push(map[cat.id])
          } else if (map[cat.parentId]) {
            map[cat.parentId].children.push(map[cat.id])
          }
        })
        
        console.log('🌲 获取分类树:', tree)
        resolve(tree)
      }, 200)
    })
  }
  
  return request({
    url: '/category/tree',
    method: 'get'
  })
}

/**
 * 获取分类详情
 */
export function getCategoryDetail(id) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const categories = getCategoriesFromStorage()
        const category = categories.find(cat => cat.id === Number(id))
        
        if (category) {
          console.log('📂 获取分类详情:', category)
          resolve(category)
        } else {
          reject(new Error('分类不存在'))
        }
      }, 200)
    })
  }
  
  return request({
    url: `/category/${id}`,
    method: 'get'
  })
}

/**
 * 新增分类
 */
export function addCategory(data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const categories = getCategoriesFromStorage()
        
        // 验证父分类
        if (data.parentId && data.parentId !== 0) {
          const parentCategory = categories.find(cat => cat.id === data.parentId)
          if (!parentCategory) {
            reject(new Error('父分类不存在'))
            return
          }
          if (parentCategory.level >= 2) {
            reject(new Error('不支持三级及以上分类'))
            return
          }
        }
        
        // 检查同名分类
        const existCategory = categories.find(cat => 
          cat.name === data.name && cat.parentId === (data.parentId || 0)
        )
        if (existCategory) {
          reject(new Error('该分类已存在'))
          return
        }
        
        const newCategory = {
          id: nextCategoryId++,
          name: data.name,
          parentId: data.parentId || 0,
          level: data.parentId ? 2 : 1,
          sort: data.sort || 99,
          status: data.status || 'active',
          createdAt: new Date().toISOString()
        }
        
        categories.push(newCategory)
        saveCategoriesToStorage(categories)
        
        console.log('📂 新增分类成功:', newCategory)
        resolve(newCategory)
      }, 300)
    })
  }
  
  return request({
    url: '/category',
    method: 'post',
    data
  })
}

/**
 * 更新分类
 */
export function updateCategory(id, data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const categories = getCategoriesFromStorage()
        const index = categories.findIndex(cat => cat.id === Number(id))
        
        if (index === -1) {
          reject(new Error('分类不存在'))
          return
        }
        
        // 检查同名分类
        const existCategory = categories.find(cat => 
          cat.id !== Number(id) && 
          cat.name === data.name && 
          cat.parentId === categories[index].parentId
        )
        if (existCategory) {
          reject(new Error('该分类名称已存在'))
          return
        }
        
        // 不允许修改层级和父分类（防止数据混乱）
        const updatedCategory = {
          ...categories[index],
          name: data.name !== undefined ? data.name : categories[index].name,
          sort: data.sort !== undefined ? data.sort : categories[index].sort,
          status: data.status !== undefined ? data.status : categories[index].status,
          updatedAt: new Date().toISOString()
        }
        
        categories[index] = updatedCategory
        saveCategoriesToStorage(categories)
        
        console.log('📂 更新分类成功:', updatedCategory)
        resolve(updatedCategory)
      }, 300)
    })
  }
  
  return request({
    url: `/category/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除分类
 */
export function deleteCategory(id) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const categories = getCategoriesFromStorage()
        const category = categories.find(cat => cat.id === Number(id))
        
        if (!category) {
          reject(new Error('分类不存在'))
          return
        }
        
        // 检查是否有子分类
        const hasChildren = categories.some(cat => cat.parentId === Number(id))
        if (hasChildren) {
          reject(new Error('该分类下存在子分类，无法删除'))
          return
        }
        
        // 检查是否有关联商品（从product Mock数据检查）
        try {
          const productData = localStorage.getItem('mock_admin_product_data')
          if (productData) {
            const products = JSON.parse(productData)
            const hasProducts = products.some(p => p.categoryId === Number(id))
            if (hasProducts) {
              reject(new Error('该分类下存在商品，无法删除'))
              return
            }
          }
        } catch (error) {
          console.warn('检查关联商品失败:', error)
        }
        
        // 删除分类
        const filteredCategories = categories.filter(cat => cat.id !== Number(id))
        saveCategoriesToStorage(filteredCategories)
        
        console.log('📂 删除分类成功:', id)
        resolve({ success: true })
      }, 300)
    })
  }
  
  return request({
    url: `/category/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除分类
 */
export function batchDeleteCategories(ids) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(async () => {
        try {
          for (const id of ids) {
            await deleteCategory(id)
          }
          console.log('📂 批量删除分类成功:', ids)
          resolve({ success: true })
        } catch (error) {
          reject(error)
        }
      }, 300)
    })
  }
  
  return request({
    url: '/category/batch-delete',
    method: 'post',
    data: { ids }
  })
}


