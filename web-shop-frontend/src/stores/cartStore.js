/**
 * 购物车状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getCartList,
  addToCart as addToCartApi,
  updateCartItem,
  deleteCartItem,
  batchDeleteCartItems,
  toggleCartItem,
  toggleAllCartItems,
  clearCart as clearCartApi,
  getCartCount
} from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  // 状态
  const cartList = ref([])
  const cartCount = ref(0)

  // 计算属性
  // 已选中的商品列表
  const checkedItems = computed(() => {
    return cartList.value.filter(item => item.checked)
  })

  // 已选中商品总数
  const checkedCount = computed(() => {
    return checkedItems.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  // 已选中商品总价
  const checkedTotalPrice = computed(() => {
    return checkedItems.value.reduce((sum, item) => {
      return sum + item.price * item.quantity
    }, 0)
  })

  // 是否全选
  const isAllChecked = computed(() => {
    return cartList.value.length > 0 && cartList.value.every(item => item.checked)
  })

  // 获取购物车列表
  const fetchCartList = async () => {
    try {
      const data = await getCartList()
      console.log('📦 后端返回的购物车数据:', data)
      // 转换后端数据格式为前端期望的格式
      cartList.value = data.map(item => ({
        id: item.id,
        productId: item.productId,
        skuId: item.skuId,
        name: item.product?.name || '未知商品',
        price: item.product?.price || 0,
        image: item.product?.coverImage || item.product?.image || '',
        specs: item.specInfo ? JSON.parse(item.specInfo) : {},
        quantity: item.quantity,
        stock: item.product?.stock || 0,
        checked: item.checked === 1
      }))
      console.log('📦 转换后的购物车数据:', cartList.value)
      cartCount.value = cartList.value.reduce((sum, item) => sum + item.quantity, 0)
      return cartList.value
    } catch (error) {
      console.error('获取购物车列表失败：', error)
      throw error
    }
  }

  // 添加到购物车
  const addToCart = async (productData) => {
    try {
      await addToCartApi(productData)
      await fetchCartList() // 重新获取购物车列表
    } catch (error) {
      console.error('添加到购物车失败：', error)
      throw error
    }
  }

  // 更新购物车商品数量
  const updateQuantity = async (id, quantity) => {
    try {
      await updateCartItem(id, { quantity })
      await fetchCartList()
    } catch (error) {
      console.error('更新购物车商品数量失败：', error)
      throw error
    }
  }

  // 删除购物车商品
  const deleteItem = async (id) => {
    try {
      await deleteCartItem(id)
      await fetchCartList()
    } catch (error) {
      console.error('删除购物车商品失败：', error)
      throw error
    }
  }

  // 批量删除
  const batchDelete = async (ids) => {
    try {
      await batchDeleteCartItems(ids)
      await fetchCartList()
    } catch (error) {
      console.error('批量删除购物车商品失败：', error)
      throw error
    }
  }

  // 切换商品选中状态
  const toggleItem = async (id, checked) => {
    try {
      await toggleCartItem(id, checked)
      // 本地更新，提升响应速度
      const item = cartList.value.find(item => item.id === id)
      if (item) {
        item.checked = checked
      }
    } catch (error) {
      console.error('切换商品选中状态失败：', error)
      throw error
    }
  }

  // 全选/取消全选
  const toggleAll = async (checked) => {
    try {
      await toggleAllCartItems(checked)
      // 本地更新
      cartList.value.forEach(item => {
        item.checked = checked
      })
    } catch (error) {
      console.error('全选/取消全选失败：', error)
      throw error
    }
  }

  // 清空购物车
  const clearCart = async () => {
    try {
      await clearCartApi()
      cartList.value = []
      cartCount.value = 0
    } catch (error) {
      console.error('清空购物车失败：', error)
      throw error
    }
  }

  // 更新购物车数量（仅用于显示）
  const updateCartCount = async () => {
    try {
      const count = await getCartCount()
      cartCount.value = count
    } catch (error) {
      console.error('更新购物车数量失败：', error)
    }
  }

  return {
    cartList,
    cartCount,
    checkedItems,
    checkedCount,
    checkedTotalPrice,
    isAllChecked,
    fetchCartList,
    addToCart,
    updateQuantity,
    deleteItem,
    batchDelete,
    toggleItem,
    toggleAll,
    clearCart,
    updateCartCount
  }
})


