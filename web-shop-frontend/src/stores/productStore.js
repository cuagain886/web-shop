/**
 * 商品状态管理
 * 主要用于存储商品筛选条件、分类等临时状态
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCategoryList } from '@/api/product'

export const useProductStore = defineStore('product', () => {
  // 状态
  const categories = ref([]) // 商品分类列表
  const searchKeyword = ref('') // 搜索关键词
  const filterConditions = ref({
    categoryId: null,
    minPrice: null,
    maxPrice: null,
    sortBy: 'default', // default, price_asc, price_desc, sales, new
    page: 1,
    pageSize: 20
  })

  // 获取分类列表
  const fetchCategories = async () => {
    try {
      const data = await getCategoryList()
      categories.value = data
      return data
    } catch (error) {
      console.error('获取分类列表失败：', error)
      throw error
    }
  }

  // 设置搜索关键词
  const setSearchKeyword = (keyword) => {
    searchKeyword.value = keyword
  }

  // 设置筛选条件
  const setFilterConditions = (conditions) => {
    filterConditions.value = { ...filterConditions.value, ...conditions }
  }

  // 重置筛选条件
  const resetFilterConditions = () => {
    filterConditions.value = {
      categoryId: null,
      minPrice: null,
      maxPrice: null,
      sortBy: 'default',
      page: 1,
      pageSize: 20
    }
  }

  // 设置分类
  const setCategory = (categoryId) => {
    filterConditions.value.categoryId = categoryId
    filterConditions.value.page = 1 // 重置页码
  }

  // 设置价格区间
  const setPriceRange = (minPrice, maxPrice) => {
    filterConditions.value.minPrice = minPrice
    filterConditions.value.maxPrice = maxPrice
    filterConditions.value.page = 1
  }

  // 设置排序方式
  const setSortBy = (sortBy) => {
    filterConditions.value.sortBy = sortBy
    filterConditions.value.page = 1
  }

  // 设置页码
  const setPage = (page) => {
    filterConditions.value.page = page
  }

  return {
    categories,
    searchKeyword,
    filterConditions,
    fetchCategories,
    setSearchKeyword,
    setFilterConditions,
    resetFilterConditions,
    setCategory,
    setPriceRange,
    setSortBy,
    setPage
  }
})


