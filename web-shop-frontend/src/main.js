/**
 * 主入口文件
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

console.log('🚀 应用开始初始化...')

// 创建应用
const app = createApp(App)

// 全局错误处理
app.config.errorHandler = (err, instance, info) => {
  console.error('❌ 全局错误:', err)
  console.error('📍 错误位置:', info)
}

console.log('📦 注册Element Plus...')
app.use(ElementPlus, { size: 'default' })

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
console.log('✅ Element Plus注册完成')

console.log('📦 注册Pinia...')
app.use(createPinia())
console.log('✅ Pinia注册完成')

console.log('📦 注册路由...')
app.use(router)
console.log('✅ 路由注册完成')

console.log('🎬 开始挂载应用...')
app.mount('#app')
console.log('🎉 应用挂载成功！')
