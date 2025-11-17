# 京东风格购物平台

一个基于 Vue 3 的现代化电商前端项目，采用京东风格的界面设计。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **UI 组件**: Element Plus
- **HTTP 请求**: Axios
- **构建工具**: Vite

## 项目特点

- 🎨 京东风格的界面设计
- 📱 响应式布局，支持移动端
- 🚀 使用 Vite 构建，开发体验流畅
- 💡 纯前端展示项目，使用模拟数据
- 🎯 简洁清晰的代码结构

## 项目结构

```
src/
├── api/                 # API 请求封装
├── assets/              # 静态资源
├── components/          # 公共组件
│   └── layout/          # 布局组件（Header、Footer）
├── hooks/               # 自定义组合式函数
├── pages/               # 页面组件
│   └── product/         # 商品相关页面
│       └── Home.vue     # 首页
├── router/              # 路由配置
│   ├── index.js         # 路由主文件
│   └── userRoutes.js    # 用户端路由
├── stores/              # Pinia 状态管理
├── utils/               # 工具函数
├── App.vue              # 根组件
└── main.js              # 入口文件
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

项目将在 `http://localhost:5173` 启动

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 功能说明

### 已实现功能

- ✅ 首页展示
  - 左侧分类导航（10个主要分类）
  - 中间轮播图（4个促销广告）
  - 右侧信息栏（用户登录、公告）
  - 限时秒杀区域（5个秒杀商品）
  - 推荐商品区域（8个推荐商品）

### 数据说明

目前所有数据均为前端模拟数据，用于展示界面效果。后续可以根据需要接入真实的后端 API。

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 开发说明

- 使用 Vue 3 Composition API 编写
- 组件采用 `<script setup>` 语法糖
- 样式采用 scoped CSS
- 所有组件都有控制台日志输出，方便调试

## 注意事项

- 这是一个前端展示项目，暂未接入后端 API
- 所有数据为模拟数据
- 仅供学习和演示使用

## 许可证

MIT License

---

**开发时间**: 2025年11月
