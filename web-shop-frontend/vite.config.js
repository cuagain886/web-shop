import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // ✅ 修改为后端端口
        changeOrigin: true,
        secure: false,  // 如果是https，需要设置为false
        // ✅ 不重写路径，因为后端接口路径就是 /api/xxx
        // rewrite: (path) => path.replace(/^\/api/, '')  // 已删除
      }
    }
  }
})
