/**
 * 用户端路由配置
 */

export default [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/pages/product/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/pages/product/ProductList.vue'),
    meta: { title: '商品列表' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/pages/product/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/pages/cart/CartPage.vue'),
    meta: { title: '购物车' }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('@/pages/order/Checkout.vue'),
    meta: { title: '确认订单', requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('@/pages/order/OrderList.vue'),
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('@/pages/order/OrderDetail.vue'),
    meta: { title: '订单详情', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: () => import('@/pages/user/UserProfile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/user/Login.vue'),
    meta: { title: '用户登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/user/Register.vue'),
    meta: { title: '用户注册' }
  },
  {
    path: '/announcement/:id',
    name: 'AnnouncementDetail',
    component: () => import('@/pages/announcement/AnnouncementDetail.vue'),
    meta: { title: '公告详情' }
  }
]
