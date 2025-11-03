# Postman快速测试清单

**目的：** 快速验证所有核心API功能是否正常  
**预计时间：** 15-20分钟

---

## 🚀 开始前准备

### 1. 启动后端服务
```bash
cd web-shop-backend
mvn spring-boot:run
```

### 2. 验证服务状态
访问：http://localhost:8080/doc.html  
看到Knife4j文档页面 ✅

### 3. Postman环境变量设置
创建环境 "WebShop-Local"，添加变量：
```
baseUrl = http://localhost:8080
```

---

## ✅ 核心功能快速测试（按顺序执行）

### 📝 测试1: 用户注册

```
POST {{baseUrl}}/api/user/register

Body (JSON):
{
  "username": "testuser001",
  "password": "123456",
  "phone": "13800138001",
  "nickname": "测试用户"
}

✅ 预期: code=200, message="注册成功"
```

**保存返回的userId到环境变量！**

---

### 📝 测试2: 用户登录

```
POST {{baseUrl}}/api/user/login

Body (JSON):
{
  "username": "testuser001",
  "password": "123456"
}

✅ 预期: code=200, data包含token和用户信息
```

**保存返回的token和userId！**

---

### 📝 测试3: 新增商品（需要管理员权限）

```
POST {{baseUrl}}/api/product

Body (JSON):
{
  "name": "测试商品-iPhone",
  "categoryId": 1,
  "price": 9999.00,
  "stock": 100,
  "description": "测试用商品",
  "status": 1,
  "isHot": 1,
  "isRecommend": 1
}

✅ 预期: code=200, message="商品添加成功"
```

**保存返回的productId！**

---

### 📝 测试4: 查询商品列表

```
GET {{baseUrl}}/api/product/list?pageNum=1&pageSize=10

✅ 预期: code=200, data包含records数组
```

---

### 📝 测试5: 获取商品详情

```
GET {{baseUrl}}/api/product/{{productId}}

✅ 预期: code=200, data包含商品完整信息
```

---

### 📝 测试6: 添加收货地址

```
POST {{baseUrl}}/api/address

Body (JSON):
{
  "userId": 1,
  "receiverName": "张三",
  "receiverPhone": "13800138001",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detailAddress": "科技园XX路XX号",
  "isDefault": 1
}

✅ 预期: code=200, message="地址添加成功"
```

**保存返回的addressId！**

---

### 📝 测试7: 添加商品到购物车

```
POST {{baseUrl}}/api/cart

Body (JSON):
{
  "userId": 1,
  "productId": 1,
  "quantity": 2,
  "specInfo": "颜色:黑色"
}

✅ 预期: code=200, message="添加成功"
```

---

### 📝 测试8: 查看购物车

```
GET {{baseUrl}}/api/cart/{{userId}}

✅ 预期: code=200, data包含购物车列表
```

**保存返回的第一个cartId！**

---

### 📝 测试9: 创建订单

```
POST {{baseUrl}}/api/orders

Body (JSON):
{
  "userId": 1,
  "addressId": 1,
  "cartItemIds": [1],
  "note": "测试订单，请尽快发货"
}

✅ 预期: code=200, message="订单创建成功"
```

**保存返回的orderNo和orderId！**

---

### 📝 测试10: 支付订单

```
PUT {{baseUrl}}/api/orders/{{orderNo}}/pay?userId=1&paymentMethod=1

✅ 预期: code=200, message="支付成功"
```

---

### 📝 测试11: 商家发货

```
PUT {{baseUrl}}/api/orders/{{orderNo}}/ship?expressCompany=顺丰速运&trackingNo=SF123456789

✅ 预期: code=200, message="发货成功"
```

---

### 📝 测试12: 确认收货

```
PUT {{baseUrl}}/api/orders/{{orderNo}}/receive?userId=1

✅ 预期: code=200, message="已确认收货"
```

---

### 📝 测试13: 发布商品评价

```
POST {{baseUrl}}/api/review

Body (JSON):
{
  "userId": 1,
  "productId": 1,
  "orderId": 1,
  "rating": 5,
  "content": "商品很好，物流快，好评！"
}

✅ 预期: code=200, message="评价成功"
```

---

### 📝 测试14: 获取订单统计

```
GET {{baseUrl}}/api/orders/statistics

✅ 预期: code=200, data包含统计数据
```

---

### 📝 测试15: 获取订单状态分布

```
GET {{baseUrl}}/api/orders/status-distribution

✅ 预期: code=200, data包含状态分布
```

---

## 📊 测试结果记录

| # | 测试项 | 状态 | 备注 |
|---|--------|------|------|
| 1 | 用户注册 | ⬜ | |
| 2 | 用户登录 | ⬜ | |
| 3 | 新增商品 | ⬜ | |
| 4 | 查询商品列表 | ⬜ | |
| 5 | 获取商品详情 | ⬜ | |
| 6 | 添加收货地址 | ⬜ | |
| 7 | 添加到购物车 | ⬜ | |
| 8 | 查看购物车 | ⬜ | |
| 9 | 创建订单 | ⬜ | |
| 10 | 支付订单 | ⬜ | |
| 11 | 商家发货 | ⬜ | |
| 12 | 确认收货 | ⬜ | |
| 13 | 发布评价 | ⬜ | |
| 14 | 订单统计 | ⬜ | |
| 15 | 状态分布 | ⬜ | |

**测试完成率：** ___/15 (___%)

---

## 🔍 补充测试（可选）

### 分类管理
```
GET {{baseUrl}}/api/category/tree
✅ 预期: 获取分类树
```

### 用户收藏
```
POST {{baseUrl}}/api/favorite
Body: {"userId": 1, "productId": 1}
✅ 预期: 收藏成功
```

### 浏览历史
```
POST {{baseUrl}}/api/history
Body: {"userId": 1, "productId": 1}
✅ 预期: 记录成功
```

### 获取热门商品
```
GET {{baseUrl}}/api/product/hot?limit=10
✅ 预期: 返回热门商品列表
```

### 获取推荐商品
```
GET {{baseUrl}}/api/product/recommend?limit=10
✅ 预期: 返回推荐商品列表
```

---

## ❗ 常见问题快速排查

### 问题1: 所有接口返回404
```
原因: 后端服务未启动
解决: mvn spring-boot:run
验证: curl http://localhost:8080/doc.html
```

### 问题2: 创建订单失败
```
原因: 购物车为空或地址不存在
解决: 先执行测试6-8，确保有地址和购物车数据
```

### 问题3: 新增商品失败
```
原因: 用户不是管理员
解决: 在数据库中手动设置user表的role=1
SQL: UPDATE user SET role=1 WHERE id=1;
```

### 问题4: 支付订单失败
```
原因: 订单状态不是"待付款"
解决: 确保订单刚创建，状态为0
```

---

## 💡 快速技巧

### 1. 批量替换userId
在Postman中使用环境变量 `{{userId}}`，统一管理

### 2. 快速复制请求
右键请求 → Duplicate，快速创建相似测试

### 3. 使用Collection Runner
批量运行所有测试，自动生成测试报告

### 4. 保存常用数据
将测试数据保存为环境变量：
```javascript
// 在Tests标签添加
pm.environment.set("userId", jsonData.data.userId);
pm.environment.set("productId", jsonData.data.id);
pm.environment.set("orderNo", jsonData.data.orderNo);
```

---

## 🎯 测试完成标准

✅ 所有15个核心测试通过  
✅ 完整的购物流程可以走通  
✅ 订单状态可以正常流转  
✅ 统计数据可以正常获取  

**如果以上都通过，说明后端核心功能运行正常！** 🎉

---

**提示：** 更详细的测试文档请参考 [Postman接口测试文档.md](./Postman接口测试文档.md)

**文档版本：** v1.0  
**更新日期：** 2025-11-03

