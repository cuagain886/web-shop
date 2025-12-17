# WebShop 网上商城系统

作者：饶小峰     202330451531

一个基于 Spring Boot + Vue 3 的全栈电商平台，支持用户购物、订单管理、后台管理等完整功能。

## 项目简介

WebShop 是一个功能完善的网上商城系统，采用前后端分离架构，提供用户端购物体验和管理员后台管理功能。

### 技术栈

**后端技术：**
- Spring Boot 3.5.7
- MyBatis-Plus 3.5.7
- MySQL 8.0
- Druid 数据源
- JWT 认证
- Knife4j API 文档
- Java 21

**前端技术：**
- Vue 3.5
- Vite 7
- Element Plus 2.11
- Pinia 状态管理
- Vue Router 4
- Axios
- ECharts 图表

**部署技术：**
- Docker & Docker Compose
- Nginx

## 功能特性

### 用户端功能
-  **商品浏览** - 商品列表、分类筛选、商品详情
-  **商品搜索** - 关键词搜索、热门商品、推荐商品
-  **购物车** - 添加商品、修改数量、批量操作
-  **订单管理** - 下单、支付、取消、确认收货
-  **收藏夹** - 商品收藏、收藏列表
-  **地址管理** - 收货地址增删改、设置默认地址
-  **用户中心** - 个人信息、密码修改、浏览历史
-  **商品评价** - 订单评价、查看评价
-  **公告系统** - 查看商城公告

### 管理端功能
-  **数据统计** - 订单统计、销售分析
-  **商品管理** - 商品增删改查、上下架、库存管理
-  **订单管理** - 订单列表、发货、取消
-  **用户管理** - 用户列表、禁用/启用
-  **退款管理** - 退款申请审核
-  **公告管理** - 公告发布、编辑
-  **系统设置** - 商城基础配置
-  **操作日志** - 管理员操作记录

## 项目结构

```
WebShop/
├── docker-compose.yml          # Docker 编排配置
├── web-shop-backend/           # 后端项目
│   ├── src/main/java/org/javaweb/webshopbackend/
│   │   ├── config/             # 配置类
│   │   ├── controller/         # 控制器层
│   │   ├── service/            # 服务层
│   │   ├── mapper/             # 数据访问层
│   │   ├── pojo/               # 实体类
│   │   │   ├── entity/         # 数据库实体
│   │   │   ├── dto/            # 数据传输对象
│   │   │   └── common/         # 通用响应类
│   │   ├── interceptor/        # 拦截器
│   │   ├── util/               # 工具类
│   │   └── task/               # 定时任务
│   ├── src/main/resources/
│   │   ├── mapper/             # MyBatis XML 映射文件
│   │   ├── application.properties  # 应用配置
│   │   └── database_init.sql   # 数据库初始化脚本
│   ├── Dockerfile              # 后端 Docker 镜像
│   └── pom.xml                 # Maven 配置
│
└── web-shop-frontend/          # 前端项目
    ├── src/
    │   ├── api/                # API 接口封装
    │   ├── assets/             # 静态资源
    │   ├── components/         # 公共组件
    │   ├── pages/              # 页面组件
    │   │   ├── admin/          # 管理端页面
    │   │   ├── product/        # 商品相关页面
    │   │   ├── cart/           # 购物车页面
    │   │   ├── order/          # 订单页面
    │   │   ├── user/           # 用户中心页面
    │   │   └── announcement/   # 公告页面
    │   ├── router/             # 路由配置
    │   ├── stores/             # Pinia 状态管理
    │   ├── App.vue             # 根组件
    │   └── main.js             # 入口文件
    ├── Dockerfile              # 前端 Docker 镜像
    ├── nginx.conf              # Nginx 配置
    ├── package.json            # 依赖配置
    └── vite.config.js          # Vite 配置
```

## 核心代码文件说明

### 后端核心文件

| 文件路径 | 说明 |
|---------|------|
| `controller/UserController.java` | 用户注册、登录、信息管理 |
| `controller/ProductController.java` | 商品查询、管理接口 |
| `controller/OrdersController.java` | 订单创建、支付、发货等 |
| `controller/ShoppingCartController.java` | 购物车操作 |
| `controller/ProductCategoryController.java` | 商品分类管理 |
| `controller/UserAddressController.java` | 收货地址管理 |
| `controller/UserFavoriteController.java` | 商品收藏功能 |
| `controller/ProductReviewController.java` | 商品评价功能 |
| `controller/RefundController.java` | 退款申请处理 |
| `controller/AnnouncementController.java` | 公告管理 |
| `controller/FileController.java` | 文件上传 |
| `interceptor/JwtInterceptor.java` | JWT 认证拦截器 |
| `util/JwtUtil.java` | JWT 工具类 |
| `config/CorsConfig.java` | 跨域配置 |
| `config/MyBatisPlusConfig.java` | MyBatis-Plus 配置 |

### 前端核心文件

| 文件路径 | 说明 |
|---------|------|
| `pages/product/Home.vue` | 首页 |
| `pages/product/ProductList.vue` | 商品列表页 |
| `pages/product/ProductDetail.vue` | 商品详情页 |
| `pages/cart/CartPage.vue` | 购物车页面 |
| `pages/order/Checkout.vue` | 结算页面 |
| `pages/order/OrderList.vue` | 订单列表 |
| `pages/user/Login.vue` | 用户登录 |
| `pages/user/Register.vue` | 用户注册 |
| `pages/user/UserProfile.vue` | 个人中心 |
| `pages/admin/AdminDashboard.vue` | 管理后台首页 |
| `pages/admin/AdminProductList.vue` | 商品管理 |
| `pages/admin/AdminOrderList.vue` | 订单管理 |
| `api/*.js` | 各模块 API 接口封装 |
| `stores/cartStore.js` | 购物车状态管理 |
| `router/userRoutes.js` | 用户端路由配置 |

## 快速开始

### 环境要求

- JDK 21+
- Node.js 20.19+ 或 22.12+
- MySQL 8.0+
- Maven 3.6+

### 本地开发

**1. 克隆项目**
```bash
git clone https://github.com/cuagain886/web-shop.git
cd WebShop
```

**2. 配置数据库**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE web_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入初始化脚本
mysql -u root -p web_shop < web-shop-backend/src/main/resources/database_init.sql
```

**3. 修改后端配置**

编辑 `web-shop-backend/src/main/resources/application.properties`：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/web_shop?useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**4. 启动后端**
```bash
cd web-shop-backend
./mvnw spring-boot:run
```

**5. 启动前端**
```bash
cd web-shop-frontend
npm install
npm run dev
```

**6. 访问应用**
- 前端地址：http://localhost:5173
- 后端 API：http://localhost:8080
- API 文档：http://localhost:8080/doc.html

### Docker 部署

```bash
# 一键启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 停止服务
docker-compose down
```

Docker 部署后访问：
- 前端：http://localhost
- 后端 API：http://localhost:8080
- MySQL：localhost:3307

## 数据库设计

主要数据表：
- `user` - 用户表
- `product` - 商品表
- `product_category` - 商品分类表
- `product_sku` - 商品 SKU 表
- `shopping_cart` - 购物车表
- `orders` - 订单表
- `order_item` - 订单项表
- `user_address` - 用户地址表
- `user_favorite` - 用户收藏表
- `product_review` - 商品评价表
- `refund` - 退款表
- `announcement` - 公告表
- `browsing_history` - 浏览历史表
- `operation_log` - 操作日志表
- `system_settings` - 系统设置表

