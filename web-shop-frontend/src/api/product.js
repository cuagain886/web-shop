/**
 * 商品相关 API
 */

import request from '@/utils/request'

// ========== Mock 数据（开发阶段使用）==========
const MOCK_ENABLED = true // 是否启用Mock数据

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
 * 获取商品列表
 */
export function getProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
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

/**
 * 获取商品列表（管理端）
 */
export function getAdminProductList(params) {
  return request({
    url: '/admin/products',
    method: 'get',
    params
  })
}

/**
 * 添加商品（管理端）
 */
export function addProduct(data) {
  return request({
    url: '/admin/products',
    method: 'post',
    data
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
  return request({
    url: `/admin/products/${id}/status`,
    method: 'put',
    data: { status }
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
export function uploadProductImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/admin/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}


