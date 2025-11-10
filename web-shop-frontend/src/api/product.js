/**
 * 商品相关 API
 */

import request from '@/utils/request'

/**
 * 获取商品分类列表
 */
export function getCategoryList() {
  return request({
    url: '/product/categories',
    method: 'get'
  })
}

/**
 * 获取商品详情
 */
export function getProductDetail(id) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

/**
 * 搜索商品
 */
export function searchProduct(params) {
  return request({
    url: '/product/search',
    method: 'get',
    params
  })
}

/**
 * 获取热门商品
 */
export function getHotProducts(params) {
  return request({
    url: '/product/hot',
    method: 'get',
    params
  })
}

/**
 * 获取推荐商品
 */
export function getRecommendProducts(params) {
  return request({
    url: '/product/recommend',
    method: 'get',
    params
  })
}

/**
 * 获取秒杀商品
 */
export function getFlashSaleProducts(params) {
  return request({
    url: '/product/flash-sale',
    method: 'get',
    params
  })
}

/**
 * 获取商品评价列表
 */
export function getProductReviews(id, params) {
  return request({
    url: `/product/${id}/reviews`,
    method: 'get',
    params
  })
}

/**
 * 添加商品评价
 */
export function addProductReview(id, data) {
  return request({
    url: `/product/${id}/review`,
    method: 'post',
    data
  })
}

// ========== 管理端接口 ==========

/**
 * 获取商品列表（管理端）
 */
export function getAdminProductList(params = {}) {
  return request({
    url: '/product/admin/list',
    method: 'get',
    params
  })
}

/**
 * 添加商品（管理端）
 */
export function addProduct(data) {
  return request({
    url: '/product',
    method: 'post',
    data
  })
}

/**
 * 获取商品详情（管理端）
 */
export function getAdminProductDetail(id) {
  return request({
    url: `/admin/products/${id}`,
    method: 'get'
  })
}

/**
 * 更新商品（管理端）
 */
export function updateProduct(id, data) {
  return request({
    url: `/admin/products/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除商品（管理端）
 */
export function deleteProduct(id) {
  return request({
    url: `/admin/products/${id}`,
    method: 'delete'
  })
}

/**
 * 商品上架/下架（管理端）
 */
export function updateProductStatus(id, status) {
  const endpoint = status === 'active' ? 'on-shelf' : 'off-shelf'
  return request({
    url: `/product/${id}/${endpoint}`,
    method: 'put'
  })
}

/**
 * 调整商品库存（管理端）
 */
export function updateProductStock(id, data) {
  return request({
    url: `/admin/products/${id}/stock`,
    method: 'put',
    data
  })
}

/**
 * 上传商品图片（管理端）
 */
export function uploadProductImage(file, prefix) {
  const formData = new FormData()
  formData.append('file', file)
  if (prefix) {
    formData.append('prefix', prefix)
  }
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量删除商品（管理端）
 */
export function batchDeleteProducts(ids) {
  return request({
    url: '/admin/products/batch-delete',
    method: 'post',
    data: { ids }
  })
}

/**
 * 更新商品推荐状态（管理端）
 */
export function updateProductRecommend(id, isRecommend) {
  return request({
    url: `/admin/products/${id}/recommend`,
    method: 'put',
    params: { isRecommend }
  })
}

/**
 * 更新商品秒杀状态（管理端）
 */
export function updateProductFlashSale(id, isFlashSale) {
  return request({
    url: `/admin/products/${id}/flash-sale`,
    method: 'put',
    params: { isFlashSale }
  })
}

/**
 * 获取商品列表（用户端）
 */
export const getProductList = (params = {}) => {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}
