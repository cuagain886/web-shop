/**
 * 商品分类相关 API
 */

import request from '@/utils/request'

/**
 * 获取分类列表
 */
export function getCategoryList(params = {}) {
  return request({
    url: '/api/category/list',
    method: 'get',
    params
  })
}

/**
 * 获取分类树（层级结构）
 */
export function getCategoryTree() {
  return request({
    url: '/api/category/tree',
    method: 'get'
  })
}

/**
 * 获取分类详情
 */
export function getCategoryDetail(id) {
  return request({
    url: `/api/category/${id}`,
    method: 'get'
  })
}

/**
 * 新增分类
 */
export function addCategory(data) {
  return request({
    url: '/api/category',
    method: 'post',
    data
  })
}

/**
 * 更新分类
 */
export function updateCategory(id, data) {
  return request({
    url: `/api/category/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除分类
 */
export function deleteCategory(id) {
  return request({
    url: `/api/category/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除分类
 */
export function batchDeleteCategories(ids) {
  return request({
    url: '/api/category/batch',
    method: 'post',
    data: { ids }
  })
}
