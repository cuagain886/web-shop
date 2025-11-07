/**
 * 地址相关 API
 */

import request from '@/utils/request'
import { useUserStore } from '@/stores/userStore'

// ========== Mock 数据（开发阶段使用）==========
const MOCK_ENABLED = false // 是否启用Mock数据
const ADDRESS_STORAGE_KEY = 'mock_address_data' // localStorage存储key

// 从localStorage获取地址数据
const getAddressFromStorage = () => {
  try {
    const data = localStorage.getItem(ADDRESS_STORAGE_KEY)
    if (data) {
      return JSON.parse(data)
    }
  } catch (error) {
    console.error('读取地址数据失败:', error)
  }
  
  // 返回默认地址数据
  const defaultAddresses = [
    {
      id: 1,
      receiverName: '张三',
      phone: '13800138000',
      province: '北京市',
      city: '北京市',
      district: '朝阳区',
      detail: '某某街道某某小区1号楼1单元101室',
      isDefault: true,
      createdAt: new Date().toISOString()
    },
    {
      id: 2,
      receiverName: '李四',
      phone: '13800138001',
      province: '上海市',
      city: '上海市',
      district: '浦东新区',
      detail: '某某路某某大厦10楼1001室',
      isDefault: false,
      createdAt: new Date().toISOString()
    }
  ]
  
  // 保存默认数据
  saveAddressToStorage(defaultAddresses)
  return defaultAddresses
}

// 保存地址数据到localStorage
const saveAddressToStorage = (addresses) => {
  try {
    localStorage.setItem(ADDRESS_STORAGE_KEY, JSON.stringify(addresses))
  } catch (error) {
    console.error('保存地址数据失败:', error)
  }
}

let nextAddressId = Date.now()

/**
 * 获取地址列表
 */
export function getAddressList() {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const addresses = getAddressFromStorage()
        console.log('📍 获取地址列表:', addresses)
        resolve(addresses)
      }, 200)
    })
  }
  
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/address/user/${userId}`,
    method: 'get'
  })
}

/**
 * 获取默认地址
 */
export function getDefaultAddress() {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const addresses = getAddressFromStorage()
        const defaultAddress = addresses.find(addr => addr.isDefault)
        console.log('📍 获取默认地址:', defaultAddress)
        resolve(defaultAddress || null)
      }, 200)
    })
  }
  
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/address/user/${userId}/default`,
    method: 'get'
  })
}

/**
 * 获取地址详情
 */
export function getAddressDetail(id) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const addresses = getAddressFromStorage()
        const address = addresses.find(addr => addr.id === id)
        if (address) {
          console.log('📍 获取地址详情:', address)
          resolve(address)
        } else {
          reject(new Error('地址不存在'))
        }
      }, 200)
    })
  }
  
  return request({
    url: `/address/${id}`,
    method: 'get'
  })
}

/**
 * 添加地址
 */
export function addAddress(data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const addresses = getAddressFromStorage()
        
        // 如果设置为默认地址，将其他地址的isDefault设为false
        if (data.isDefault) {
          addresses.forEach(addr => {
            addr.isDefault = false
          })
        }
        
        const newAddress = {
          ...data,
          id: nextAddressId++,
          createdAt: new Date().toISOString()
        }
        
        addresses.push(newAddress)
        saveAddressToStorage(addresses)
        
        console.log('📍 添加地址成功:', newAddress)
        resolve(newAddress)
      }, 300)
    })
  }
  
  return request({
    url: '/address',
    method: 'post',
    data
  })
}

/**
 * 更新地址
 */
export function updateAddress(id, data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const addresses = getAddressFromStorage()
        const index = addresses.findIndex(addr => addr.id === id)
        
        if (index === -1) {
          reject(new Error('地址不存在'))
          return
        }
        
        // 如果设置为默认地址，将其他地址的isDefault设为false
        if (data.isDefault) {
          addresses.forEach(addr => {
            addr.isDefault = false
          })
        }
        
        addresses[index] = {
          ...addresses[index],
          ...data,
          updatedAt: new Date().toISOString()
        }
        
        saveAddressToStorage(addresses)
        console.log('📍 更新地址成功:', addresses[index])
        resolve(addresses[index])
      }, 300)
    })
  }
  
  return request({
    url: `/address/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除地址
 */
export function deleteAddress(id) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const addresses = getAddressFromStorage()
        const index = addresses.findIndex(addr => addr.id === id)
        
        if (index === -1) {
          reject(new Error('地址不存在'))
          return
        }
        
        addresses.splice(index, 1)
        saveAddressToStorage(addresses)
        
        console.log('📍 删除地址成功')
        resolve()
      }, 300)
    })
  }
  
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/address/${id}?userId=${userId}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 */
export function setDefaultAddress(id) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const addresses = getAddressFromStorage()
        const targetAddress = addresses.find(addr => addr.id === id)
        
        if (!targetAddress) {
          reject(new Error('地址不存在'))
          return
        }
        
        // 将所有地址的isDefault设为false
        addresses.forEach(addr => {
          addr.isDefault = false
        })
        
        // 设置目标地址为默认
        targetAddress.isDefault = true
        
        saveAddressToStorage(addresses)
        console.log('📍 设置默认地址成功:', targetAddress)
        resolve(targetAddress)
      }, 300)
    })
  }
  
  const userStore = useUserStore()
  const userId = userStore.userInfo?.id || userStore.userInfo?.userId || 1
  
  return request({
    url: `/address/${id}/default?userId=${userId}`,
    method: 'put'
  })
}

