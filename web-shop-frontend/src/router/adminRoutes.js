/**
 * 管理端路由配置
 */

import AdminLayout from '@/components/layout/AdminLayout.vue'

export default [
  // 管理端登录页（不使用Layout）
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/pages/admin/AdminLogin.vue'),
    meta: { title: '商家登录' }
  },
  
  // 管理端主体（使用Layout）
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, requiresMerchant: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/pages/admin/AdminDashboard.vue'),
        meta: { title: '数据概览', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'products',
        name: 'AdminProductList',
        component: () => import('@/pages/admin/AdminProductList.vue'),
        meta: { title: '商品列表', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'products/add',
        name: 'AdminProductAdd',
        component: () => import('@/pages/admin/AdminProductEdit.vue'),
        meta: { title: '新增商品', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'products/edit/:id',
        name: 'AdminProductEdit',
        component: () => import('@/pages/admin/AdminProductEdit.vue'),
        meta: { title: '编辑商品', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'categories',
        name: 'AdminCategoryManage',
        component: () => import('@/pages/admin/AdminCategoryManage.vue'),
        meta: { title: '分类管理', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'orders',
        name: 'AdminOrderList',
        component: () => import('@/pages/admin/AdminOrderList.vue'),
        meta: { title: '订单管理', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'orders/:id',
        name: 'AdminOrderDetail',
        component: () => import('@/pages/admin/AdminOrderDetail.vue'),
        meta: { title: '订单详情', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/pages/admin/AdminProfile.vue'),
        meta: { title: '个人信息', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'security',
        name: 'AdminSecurity',
        component: () => import('@/pages/admin/AdminSecurity.vue'),
        meta: { title: '安全设置', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'operation-log',
        name: 'AdminOperationLog',
        component: () => import('@/pages/admin/AdminOperationLog.vue'),
        meta: { title: '操作日志', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'users',
        name: 'AdminUserList',
        component: () => import('@/pages/admin/AdminUserList.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('@/pages/admin/AdminSettings.vue'),
        meta: { title: '系统设置', requiresAuth: true, requiresMerchant: true }
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncementManage',
        component: () => import('@/pages/admin/AdminAnnouncementManage.vue'),
        meta: { title: '公告管理', requiresAuth: true, requiresMerchant: true }
      }
    ]
  }
]


