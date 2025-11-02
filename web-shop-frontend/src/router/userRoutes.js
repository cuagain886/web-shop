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
  }
]
