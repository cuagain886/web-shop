/**
 * 商品相关 API
 */

import request from '@/utils/request'

// ========== Mock 数据（开发阶段使用）==========
const MOCK_ENABLED = false // 是否启用Mock数据

// Mock商品列表数据
const mockProductList = [
  { id: 1, name: 'iPhone 15 Pro Max', desc: 'A17 Pro芯片 钛金属设计', price: 9999, sales: 1000, stock: 50, categoryId: 2, color: '#409eff', image: '' },
  { id: 2, name: 'MacBook Pro 16', desc: 'M3 Max芯片 专业性能', price: 19999, sales: 500, stock: 30, categoryId: 3, color: '#67c23a', image: '' },
  { id: 3, name: 'iPad Pro 12.9', desc: 'M2芯片 12.9英寸', price: 6999, sales: 800, stock: 40, categoryId: 3, color: '#e6a23c', image: '' },
  { id: 4, name: 'AirPods Max', desc: '头戴式降噪耳机', price: 4399, sales: 600, stock: 80, categoryId: 2, color: '#f56c6c', image: '' },
  { id: 5, name: 'Apple Watch Ultra', desc: '专业运动手表', price: 6299, sales: 400, stock: 60, categoryId: 2, color: '#909399', image: '' },
  { id: 6, name: 'Mac Studio', desc: 'M2 Ultra芯片', price: 14999, sales: 200, stock: 20, categoryId: 3, color: '#409eff', image: '' },
  { id: 7, name: 'Studio Display', desc: '27英寸5K显示器', price: 11499, sales: 150, stock: 25, categoryId: 3, color: '#67c23a', image: '' },
  { id: 8, name: 'HomePod', desc: '空间音频 智能音箱', price: 2299, sales: 300, stock: 100, categoryId: 1, color: '#e6a23c', image: '' },
  { id: 9, name: 'AirPods Pro', desc: '主动降噪 无线耳机', price: 1899, sales: 1500, stock: 200, categoryId: 2, color: '#67c23a', image: '' },
  { id: 10, name: 'Magic Keyboard', desc: '妙控键盘 触控板', price: 899, sales: 800, stock: 150, categoryId: 3, color: '#909399', image: '' },
  { id: 11, name: 'Apple TV 4K', desc: '高清流媒体播放器', price: 1499, sales: 250, stock: 80, categoryId: 1, color: '#409eff', image: '' },
  { id: 12, name: 'iPhone 15', desc: 'A16仿生芯片 超视网膜XDR', price: 5999, sales: 2000, stock: 100, categoryId: 2, color: '#f56c6c', image: '' },
  { id: 13, name: 'iPad Air', desc: 'M1芯片 10.9英寸', price: 4399, sales: 900, stock: 70, categoryId: 3, color: '#409eff', image: '' },
  { id: 14, name: 'Mac mini', desc: 'M2芯片 小巧强大', price: 3999, sales: 350, stock: 50, categoryId: 3, color: '#67c23a', image: '' },
  { id: 15, name: 'Apple Pencil', desc: '第二代 触控笔', price: 999, sales: 600, stock: 200, categoryId: 3, color: '#e6a23c', image: '' },
  { id: 16, name: 'AirTag', desc: '物品追踪器 4件装', price: 799, sales: 1200, stock: 300, categoryId: 2, color: '#909399', image: '' },
  { id: 17, name: 'iPhone 14 Pro', desc: 'A16仿生芯片 灵动岛', price: 7999, sales: 1800, stock: 90, categoryId: 2, color: '#409eff', image: '' },
  { id: 18, name: 'MacBook Air M2', desc: 'M2芯片 13.6英寸', price: 9499, sales: 1100, stock: 60, categoryId: 3, color: '#67c23a', image: '' },
  { id: 19, name: 'iMac 24', desc: 'M3芯片 4.5K显示屏', price: 10499, sales: 280, stock: 35, categoryId: 3, color: '#e6a23c', image: '' },
  { id: 20, name: 'Magic Mouse', desc: '妙控鼠标 多点触控', price: 599, sales: 950, stock: 180, categoryId: 3, color: '#f56c6c', image: '' },
  { id: 21, name: 'AirPods 3', desc: '空间音频 无线耳机', price: 1399, sales: 1600, stock: 220, categoryId: 2, color: '#409eff', image: '' },
  { id: 22, name: 'Apple Watch SE', desc: '健康管理 运动追踪', price: 1999, sales: 700, stock: 120, categoryId: 2, color: '#67c23a', image: '' },
  { id: 23, name: 'iPad mini', desc: 'A15仿生芯片 8.3英寸', price: 3999, sales: 550, stock: 75, categoryId: 3, color: '#e6a23c', image: '' },
  { id: 24, name: 'MagSafe充电器', desc: '无线快充 支持iPhone', price: 329, sales: 2200, stock: 400, categoryId: 2, color: '#909399', image: '' },
  { id: 25, name: 'iPhone SE', desc: 'A15芯片 经典设计', price: 3499, sales: 1300, stock: 110, categoryId: 2, color: '#f56c6c', image: '' },
  { id: 26, name: 'Smart Keyboard', desc: 'iPad键盘保护套', price: 1299, sales: 420, stock: 90, categoryId: 3, color: '#409eff', image: '' },
  { id: 27, name: 'Beats Studio Pro', desc: '头戴式无线耳机', price: 2799, sales: 380, stock: 65, categoryId: 2, color: '#67c23a', image: '' },
  { id: 28, name: 'Pro Display XDR', desc: '32英寸6K显示器', price: 39999, sales: 80, stock: 15, categoryId: 3, color: '#e6a23c', image: '' },
  { id: 29, name: 'Thunderbolt数据线', desc: '高速传输 1米', price: 449, sales: 1500, stock: 500, categoryId: 3, color: '#909399', image: '' },
  { id: 30, name: 'Apple Care+', desc: '延保服务 2年', price: 1399, sales: 850, stock: 999, categoryId: 2, color: '#f56c6c', image: '' }
]

// Mock商品详情数据
const mockProductDetails = {
  1: {
    id: 1,
    name: 'iPhone 15 Pro Max',
    subtitle: 'A17 Pro芯片 钛金属设计 全新48MP主摄',
    price: 9999,
    originalPrice: 12999,
    discount: '限时优惠',
    stock: 50,
    images: [
      'https://placehold.co/400x400/409eff/fff?text=iPhone+15+Pro+Max',
      'https://placehold.co/400x400/67c23a/fff?text=侧面',
      'https://placehold.co/400x400/e6a23c/fff?text=背面',
      'https://placehold.co/400x400/f56c6c/fff?text=细节'
    ],
    specs: [
      {
        name: '颜色',
        options: [
          { label: '原色钛金属', value: 'titanium', stock: 20, price: 9999 },
          { label: '黑色钛金属', value: 'black', stock: 15, price: 9999 },
          { label: '白色钛金属', value: 'white', stock: 10, price: 9999 },
          { label: '蓝色钛金属', value: 'blue', stock: 5, price: 9999 }
        ]
      },
      {
        name: '存储容量',
        options: [
          { label: '256GB', value: '256gb', stock: 30, price: 9999 },
          { label: '512GB', value: '512gb', stock: 15, price: 10999 },
          { label: '1TB', value: '1tb', stock: 5, price: 12999 }
        ]
      }
    ],
    promos: [
      '新用户立减500元',
      '分期免息12期',
      '以旧换新最高抵2000'
    ],
    description: `
      <p>iPhone 15 Pro Max 采用航天级钛金属设计，是迄今为止最轻的 Pro 机型。</p>
      <p>配备全新 A17 Pro 芯片，性能再创新高，支持更强大的游戏和专业应用。</p>
      <p>摄像头系统全面升级，主摄升级至 48MP，支持多种焦段切换。</p>
    `,
    detailImages: [
      'https://placehold.co/800x600/409eff/fff?text=产品特写1',
      'https://placehold.co/800x600/67c23a/fff?text=产品特写2',
      'https://placehold.co/800x600/e6a23c/fff?text=产品特写3'
    ],
    parameters: [
      { name: '品牌', value: 'Apple' },
      { name: '型号', value: 'iPhone 15 Pro Max' },
      { name: '屏幕尺寸', value: '6.7英寸' },
      { name: '分辨率', value: '2796 x 1290' },
      { name: '处理器', value: 'A17 Pro' },
      { name: '后置摄像头', value: '48MP主摄 + 12MP超广角 + 12MP长焦' },
      { name: '前置摄像头', value: '12MP' },
      { name: '电池容量', value: '4422mAh' },
      { name: '操作系统', value: 'iOS 17' },
      { name: '机身材质', value: '钛金属边框 + 玻璃后盖' }
    ]
  },
  2: {
    id: 2,
    name: 'MacBook Pro 16',
    subtitle: 'M3 Max芯片 专业性能 16英寸Liquid视网膜显示屏',
    price: 19999,
    originalPrice: 24999,
    discount: '专业版限时优惠',
    stock: 30,
    images: [
      'https://placehold.co/400x400/67c23a/fff?text=MacBook+Pro+16',
      'https://placehold.co/400x400/409eff/fff?text=键盘',
      'https://placehold.co/400x400/e6a23c/fff?text=接口',
      'https://placehold.co/400x400/f56c6c/fff?text=细节'
    ],
    specs: [
      {
        name: '颜色',
        options: [
          { label: '深空灰色', value: 'gray', stock: 20, price: 19999 },
          { label: '银色', value: 'silver', stock: 10, price: 19999 }
        ]
      },
      {
        name: '芯片配置',
        options: [
          { label: 'M3 Max 14核CPU 30核GPU', value: 'm3max-14-30', stock: 15, price: 19999 },
          { label: 'M3 Max 16核CPU 40核GPU', value: 'm3max-16-40', stock: 10, price: 24999 }
        ]
      },
      {
        name: '内存',
        options: [
          { label: '36GB', value: '36gb', stock: 15, price: 19999 },
          { label: '48GB', value: '48gb', stock: 10, price: 22999 },
          { label: '64GB', value: '64gb', stock: 5, price: 25999 }
        ]
      },
      {
        name: '存储',
        options: [
          { label: '512GB', value: '512gb', stock: 20, price: 19999 },
          { label: '1TB', value: '1tb', stock: 10, price: 21999 }
        ]
      }
    ],
    promos: [
      '教育优惠立减1500元',
      '分期免息24期',
      '免费刻字服务'
    ],
    description: `
      <p>MacBook Pro 16 配备 M3 Max 芯片，为专业用户带来惊人的性能提升。</p>
      <p>16.2 英寸 Liquid 视网膜 XDR 显示屏，支持 ProMotion 自适应刷新率技术。</p>
      <p>续航时间长达 22 小时，支持快速充电和 MagSafe 3 充电接口。</p>
    `,
    detailImages: [
      'https://placehold.co/800x600/67c23a/fff?text=专业性能',
      'https://placehold.co/800x600/409eff/fff?text=显示效果',
      'https://placehold.co/800x600/e6a23c/fff?text=接口展示'
    ],
    parameters: [
      { name: '品牌', value: 'Apple' },
      { name: '型号', value: 'MacBook Pro 16 2024' },
      { name: '屏幕尺寸', value: '16.2英寸' },
      { name: '分辨率', value: '3456 x 2234' },
      { name: '处理器', value: 'M3 Max' },
      { name: '显卡', value: '集成显卡' },
      { name: '操作系统', value: 'macOS Sonoma' },
      { name: '接口', value: 'HDMI、雷雳4、SDXC卡槽、MagSafe 3' },
      { name: '重量', value: '2.16kg' },
      { name: '电池', value: '最长22小时' }
    ]
  },
  3: {
    id: 3,
    name: 'iPad Pro 12.9',
    subtitle: 'M2芯片 12.9英寸 Liquid视网膜XDR显示屏',
    price: 6999,
    originalPrice: 8999,
    discount: '学生专享',
    stock: 40,
    images: [
      'https://placehold.co/400x400/e6a23c/fff?text=iPad+Pro',
      'https://placehold.co/400x400/409eff/fff?text=显示屏',
      'https://placehold.co/400x400/67c23a/fff?text=配件',
      'https://placehold.co/400x400/f56c6c/fff?text=细节'
    ],
    specs: [
      {
        name: '颜色',
        options: [
          { label: '深空灰色', value: 'gray', stock: 25, price: 6999 },
          { label: '银色', value: 'silver', stock: 15, price: 6999 }
        ]
      },
      {
        name: '存储容量',
        options: [
          { label: '128GB', value: '128gb', stock: 15, price: 6999 },
          { label: '256GB', value: '256gb', stock: 15, price: 7999 },
          { label: '512GB', value: '512gb', stock: 10, price: 9999 }
        ]
      },
      {
        name: '网络',
        options: [
          { label: 'WLAN', value: 'wifi', stock: 30, price: 6999 },
          { label: 'WLAN + 蜂窝网络', value: 'cellular', stock: 10, price: 8499 }
        ]
      }
    ],
    promos: [
      '教育优惠减800元',
      '购机送Apple Pencil',
      '分期免息12期'
    ],
    description: `
      <p>iPad Pro 配备 M2 芯片，性能提升高达 15%。</p>
      <p>12.9 英寸 Liquid 视网膜 XDR 显示屏，支持 ProMotion 和原彩显示。</p>
      <p>支持 Apple Pencil（第二代）和妙控键盘，是创作和办公的理想设备。</p>
    `,
    detailImages: [
      'https://placehold.co/800x600/e6a23c/fff?text=创作工具',
      'https://placehold.co/800x600/409eff/fff?text=显示效果',
      'https://placehold.co/800x600/67c23a/fff?text=配件生态'
    ],
    parameters: [
      { name: '品牌', value: 'Apple' },
      { name: '型号', value: 'iPad Pro 12.9' },
      { name: '屏幕尺寸', value: '12.9英寸' },
      { name: '分辨率', value: '2732 x 2048' },
      { name: '处理器', value: 'M2芯片' },
      { name: '摄像头', value: '12MP广角 + 10MP超广角' },
      { name: '前置摄像头', value: '12MP超广角' },
      { name: '扬声器', value: '四扬声器系统' },
      { name: '操作系统', value: 'iPadOS 17' },
      { name: '厚度', value: '6.4mm' }
    ]
  }
}

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
  // 如果启用Mock数据，直接返回Mock数据
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const product = mockProductDetails[id]
        if (product) {
          console.log('📦 返回Mock商品详情:', id, product)
          resolve(product)
        } else {
          console.warn('⚠️ 未找到商品:', id)
          reject(new Error('商品不存在'))
        }
      }, 300) // 模拟网络延迟
    })
  }
  
  // 生产环境：调用真实API
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

// 管理端商品数据存储key
const ADMIN_PRODUCT_STORAGE_KEY = 'mock_admin_product_data'

// 获取管理端商品数据
const getAdminProductsFromStorage = () => {
  try {
    const data = localStorage.getItem(ADMIN_PRODUCT_STORAGE_KEY)
    if (data) {
      return JSON.parse(data)
    }
  } catch (error) {
    console.error('读取管理端商品数据失败:', error)
  }
  
  // 返回默认商品数据
  const defaultProducts = [
    {
      id: 1,
      name: 'iPhone 15 Pro Max',
      subtitle: 'A17 Pro芯片 钛金属设计',
      price: 9999,
      originalPrice: 12999,
      stock: 50,
      sales: 1234,
      categoryId: 11, // 手机通讯
      categoryName: '手机通讯',
      status: 'active', // active-上架, inactive-下架, pending-待上架
      images: [
        'https://placehold.co/400x400/409eff/fff?text=iPhone+15',
        'https://placehold.co/400x400/67c23a/fff?text=侧面'
      ],
      description: 'iPhone 15 Pro Max 采用航天级钛金属设计...',
      createdAt: new Date(Date.now() - 86400000 * 30).toISOString(),
      updatedAt: new Date(Date.now() - 86400000 * 5).toISOString()
    },
    {
      id: 2,
      name: 'MacBook Pro 14',
      subtitle: 'M3 Pro芯片 专业性能',
      price: 15999,
      originalPrice: 17999,
      stock: 30,
      sales: 567,
      categoryId: 12, // 电脑办公
      categoryName: '电脑办公',
      status: 'active',
      images: [
        'https://placehold.co/400x400/67c23a/fff?text=MacBook',
        'https://placehold.co/400x400/409eff/fff?text=键盘'
      ],
      description: 'MacBook Pro 14英寸，搭载M3 Pro芯片...',
      createdAt: new Date(Date.now() - 86400000 * 25).toISOString(),
      updatedAt: new Date(Date.now() - 86400000 * 3).toISOString()
    },
    {
      id: 3,
      name: 'AirPods Pro 2',
      subtitle: '主动降噪 无线充电',
      price: 1999,
      originalPrice: 2299,
      stock: 100,
      sales: 2345,
      categoryId: 13, // 数码配件
      categoryName: '数码配件',
      status: 'active',
      images: ['https://placehold.co/400x400/e6a23c/fff?text=AirPods'],
      description: 'AirPods Pro 2代，支持主动降噪...',
      createdAt: new Date(Date.now() - 86400000 * 20).toISOString(),
      updatedAt: new Date(Date.now() - 86400000 * 2).toISOString()
    },
    {
      id: 4,
      name: 'iPad Air',
      subtitle: 'M1芯片 10.9英寸',
      price: 4999,
      originalPrice: 5499,
      stock: 0,
      sales: 890,
      categoryId: 11,
      categoryName: '手机通讯',
      status: 'inactive', // 缺货下架
      images: ['https://placehold.co/400x400/f56c6c/fff?text=iPad'],
      description: 'iPad Air，搭载M1芯片...',
      createdAt: new Date(Date.now() - 86400000 * 15).toISOString(),
      updatedAt: new Date(Date.now() - 86400000).toISOString()
    },
    {
      id: 5,
      name: 'Apple Watch Series 9',
      subtitle: '健康监测 运动追踪',
      price: 3199,
      originalPrice: 3499,
      stock: 80,
      sales: 456,
      categoryId: 13,
      categoryName: '数码配件',
      status: 'pending', // 待上架
      images: ['https://placehold.co/400x400/909399/fff?text=Watch'],
      description: 'Apple Watch Series 9，全天健康监测...',
      createdAt: new Date(Date.now() - 86400000 * 5).toISOString(),
      updatedAt: new Date(Date.now() - 86400000).toISOString()
    }
  ]
  
  saveAdminProductsToStorage(defaultProducts)
  return defaultProducts
}

// 保存管理端商品数据
const saveAdminProductsToStorage = (products) => {
  try {
    localStorage.setItem(ADMIN_PRODUCT_STORAGE_KEY, JSON.stringify(products))
  } catch (error) {
    console.error('保存管理端商品数据失败:', error)
  }
}

let nextAdminProductId = 100

/**
 * 获取商品列表（管理端）
 */
export function getAdminProductList(params = {}) {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        let products = getAdminProductsFromStorage()
        
        // 搜索（名称或ID）
        if (params.keyword) {
          products = products.filter(p => 
            p.name.includes(params.keyword) || 
            String(p.id).includes(params.keyword)
          )
        }
        
        // 按分类筛选
        if (params.categoryId) {
          products = products.filter(p => p.categoryId === Number(params.categoryId))
        }
        
        // 按状态筛选
        if (params.status) {
          products = products.filter(p => p.status === params.status)
        }
        
        // 排序
        if (params.sortBy) {
          products.sort((a, b) => {
            if (params.sortBy === 'price') {
              return params.sortOrder === 'desc' ? b.price - a.price : a.price - b.price
            }
            if (params.sortBy === 'sales') {
              return params.sortOrder === 'desc' ? b.sales - a.sales : a.sales - b.sales
            }
            if (params.sortBy === 'stock') {
              return params.sortOrder === 'desc' ? b.stock - a.stock : a.stock - b.stock
            }
            return 0
          })
        }
        
        // 分页
        const page = params.page || 1
        const pageSize = params.pageSize || 10
        const total = products.length
        const start = (page - 1) * pageSize
        const end = start + pageSize
        const list = products.slice(start, end)
        
        console.log('🛍️ 获取管理端商品列表:', { total, page, pageSize, list: list.length })
        resolve({
          list,
          total,
          page,
          pageSize,
          totalPages: Math.ceil(total / pageSize)
        })
      }, 300)
    })
  }
  
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
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const products = getAdminProductsFromStorage()
        
        // 验证必填字段
        if (!data.name || !data.price || data.stock === undefined || !data.categoryId) {
          reject(new Error('请填写完整的商品信息'))
          return
        }
        
        const newProduct = {
          id: nextAdminProductId++,
          name: data.name,
          subtitle: data.subtitle || '',
          price: Number(data.price),
          originalPrice: data.originalPrice ? Number(data.originalPrice) : Number(data.price),
          stock: Number(data.stock),
          sales: 0,
          categoryId: Number(data.categoryId),
          categoryName: data.categoryName || '',
          status: data.status || 'pending', // 默认待上架
          images: data.images || [],
          description: data.description || '',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        }
        
        products.push(newProduct)
        saveAdminProductsToStorage(products)
        
        console.log('🛍️ 添加商品成功:', newProduct)
        resolve(newProduct)
      }, 500)
    })
  }
  
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
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const products = getAdminProductsFromStorage()
        const product = products.find(p => p.id === Number(id))
        
        if (product) {
          console.log('🛍️ 获取商品详情:', product)
          resolve(product)
        } else {
          reject(new Error('商品不存在'))
        }
      }, 200)
    })
  }
  
  return request({
    url: `/admin/products/${id}`,
    method: 'get'
  })
}

/**
 * 更新商品（管理端）
 */
export function updateProduct(id, data) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const products = getAdminProductsFromStorage()
        const index = products.findIndex(p => p.id === Number(id))
        
        if (index === -1) {
          reject(new Error('商品不存在'))
          return
        }
        
        const updatedProduct = {
          ...products[index],
          ...data,
          id: Number(id), // 保持ID不变
          price: data.price !== undefined ? Number(data.price) : products[index].price,
          originalPrice: data.originalPrice !== undefined ? Number(data.originalPrice) : products[index].originalPrice,
          stock: data.stock !== undefined ? Number(data.stock) : products[index].stock,
          categoryId: data.categoryId !== undefined ? Number(data.categoryId) : products[index].categoryId,
          updatedAt: new Date().toISOString()
        }
        
        products[index] = updatedProduct
        saveAdminProductsToStorage(products)
        
        console.log('🛍️ 更新商品成功:', updatedProduct)
        resolve(updatedProduct)
      }, 500)
    })
  }
  
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
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const products = getAdminProductsFromStorage()
        const filteredProducts = products.filter(p => p.id !== Number(id))
        saveAdminProductsToStorage(filteredProducts)
        
        console.log('🛍️ 删除商品成功:', id)
        resolve({ success: true })
      }, 300)
    })
  }
  
  return request({
    url: `/admin/products/${id}`,
    method: 'delete'
  })
}

/**
 * 商品上架/下架（管理端）
 */
export function updateProductStatus(id, status) {
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const products = getAdminProductsFromStorage()
        const product = products.find(p => p.id === Number(id))
        
        if (!product) {
          reject(new Error('商品不存在'))
          return
        }
        
        product.status = status
        product.updatedAt = new Date().toISOString()
        saveAdminProductsToStorage(products)
        
        console.log('🛍️ 更新商品状态成功:', { id, status })
        resolve(product)
      }, 300)
    })
  }
  
  // 根据status调用不同的后端接口
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
  if (MOCK_ENABLED) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const products = getAdminProductsFromStorage()
        const product = products.find(p => p.id === Number(id))
        
        if (!product) {
          reject(new Error('商品不存在'))
          return
        }
        
        product.stock = Number(data.stock)
        product.updatedAt = new Date().toISOString()
        saveAdminProductsToStorage(products)
        
        console.log('🛍️ 更新商品库存成功:', { id, stock: product.stock })
        resolve(product)
      }, 300)
    })
  }
  
  return request({
    url: `/admin/products/${id}/stock`,
    method: 'put',
    data
  })
}

/**
 * 上传商品图片（管理端）
 */
export function uploadProductImage(file) {
  const formData = new FormData()
  formData.append('file', file)
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
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const products = getAdminProductsFromStorage()
        const filteredProducts = products.filter(p => !ids.includes(p.id))
        saveAdminProductsToStorage(filteredProducts)
        
        console.log('🛍️ 批量删除商品成功:', ids)
        resolve({ success: true, count: ids.length })
      }, 500)
    })
  }
  
  return request({
    url: '/admin/products/batch-delete',
    method: 'post',
    data: { ids }
  })
}

/**
 * 获取商品列表（用户端）
 */
export const getProductList = (params = {}) => {
  if (MOCK_ENABLED) {
    return new Promise((resolve) => {
      setTimeout(() => {
        console.log('📋 获取商品列表（Mock）')
        resolve([...mockProductList])
      }, 300)
    })
  }
  
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}


