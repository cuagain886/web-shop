/**
 * 购物车操作相关的自定义钩子
 */

import { computed } from 'vue'
import { useCartStore } from '@/stores/cartStore'
import { useAuth } from './useAuth'
import { ElMessage, ElMessageBox } from 'element-plus'

export function useCart() {
  const cartStore = useCartStore()
  const { requireAuth } = useAuth()

  // 购物车列表
  const cartList = computed(() => cartStore.cartList)

  // 购物车商品数量
  const cartCount = computed(() => cartStore.cartCount)

  // 已选中商品列表
  const checkedItems = computed(() => cartStore.checkedItems)

  // 已选中商品总数
  const checkedCount = computed(() => cartStore.checkedCount)

  // 已选中商品总价
  const checkedTotalPrice = computed(() => cartStore.checkedTotalPrice)

  // 是否全选
  const isAllChecked = computed(() => cartStore.isAllChecked)

  // 添加到购物车
  const handleAddToCart = async (product) => {
    if (!requireAuth()) {
      return false
    }

    try {
      await cartStore.addToCart(product)
      ElMessage.success('已添加到购物车')
      return true
    } catch (error) {
      ElMessage.error('添加到购物车失败')
      return false
    }
  }

  // 更新商品数量
  const handleUpdateQuantity = async (id, quantity) => {
    if (quantity < 1) {
      ElMessage.warning('商品数量不能小于1')
      return
    }

    try {
      await cartStore.updateQuantity(id, quantity)
    } catch (error) {
      ElMessage.error('更新商品数量失败')
    }
  }

  // 删除商品
  const handleDeleteItem = async (id) => {
    try {
      await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      await cartStore.deleteItem(id)
      ElMessage.success('删除成功')
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
      }
    }
  }

  // 批量删除已选中商品
  const handleDeleteChecked = async () => {
    if (checkedItems.value.length === 0) {
      ElMessage.warning('请先选择要删除的商品')
      return
    }

    try {
      await ElMessageBox.confirm(`确定要删除已选中的 ${checkedCount.value} 件商品吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      const ids = checkedItems.value.map(item => item.id)
      await cartStore.batchDelete(ids)
      ElMessage.success('删除成功')
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
      }
    }
  }

  // 切换商品选中状态
  const handleToggleItem = async (id, checked) => {
    try {
      await cartStore.toggleItem(id, checked)
    } catch (error) {
      ElMessage.error('操作失败')
    }
  }

  // 全选/取消全选
  const handleToggleAll = async (checked) => {
    try {
      await cartStore.toggleAll(checked)
    } catch (error) {
      ElMessage.error('操作失败')
    }
  }

  // 清空购物车
  const handleClearCart = async () => {
    try {
      await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      await cartStore.clearCart()
      ElMessage.success('购物车已清空')
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('操作失败')
      }
    }
  }

  // 结算
  const handleCheckout = () => {
    if (!requireAuth()) {
      return false
    }

    if (checkedItems.value.length === 0) {
      ElMessage.warning('请先选择要结算的商品')
      return false
    }

    return true
  }

  return {
    cartList,
    cartCount,
    checkedItems,
    checkedCount,
    checkedTotalPrice,
    isAllChecked,
    handleAddToCart,
    handleUpdateQuantity,
    handleDeleteItem,
    handleDeleteChecked,
    handleToggleItem,
    handleToggleAll,
    handleClearCart,
    handleCheckout
  }
}


