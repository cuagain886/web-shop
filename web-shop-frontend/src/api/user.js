/**
 * 用户相关 API
 */

import request from '@/utils/request'

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 */
export function register(data) {
  return request({
    url: '/api/user/register',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo(userId) {
  return request({
    url: `/api/user/info/${userId}`,
    method: 'get'
  })
}

/**
 * 更新用户信息
 */
export function updateUserInfo(userId, data) {
  return request({
    url: `/api/user/${userId}`,
    method: 'put',
    data
  })
}

/**
 * 修改密码
 */
export function changePassword(userId, data) {
  return request({
    url: `/api/user/${userId}/password`,
    method: 'put',
    data
  })
}

/**
 * 获取用户地址列表
 */
export function getAddressList(userId) {
  return request({
    url: `/api/address/${userId}`,
    method: 'get'
  })
}

/**
 * 添加用户地址
 */
export function addAddress(data) {
  return request({
    url: '/api/address',
    method: 'post',
    data
  })
}

/**
 * 更新用户地址
 */
export function updateAddress(id, data) {
  return request({
    url: `/api/address/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户地址
 */
export function deleteAddress(id) {
  return request({
    url: `/api/address/${id}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 */
export function setDefaultAddress(id) {
  return request({
    url: `/api/address/${id}/default`,
    method: 'put'
  })
}

/**
 * 商家登录（仅限商家账号）
 */
export function merchantLogin(data) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

/**
 * 获取用户列表（管理端）
 */
export function getUserList(params) {
  return request({
    url: '/api/user/list',
    method: 'get',
    params
  })
}

/**
 * 禁用用户（管理端）
 */
export function disableUser(userId) {
  return request({
    url: `/api/user/${userId}/disable`,
    method: 'put'
  })
}

/**
 * 启用用户（管理端）
 */
export function enableUser(userId) {
  return request({
    url: `/api/user/${userId}/enable`,
    method: 'put'
  })
}

/**
 * 注销账户
 */
export function deleteAccount(userId) {
  return request({
    url: `/api/user/${userId}/account`,
    method: 'delete'
  })
}

