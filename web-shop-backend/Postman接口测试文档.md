# WebShop后端接口Postman测试文档

**版本：** v1.0  
**更新日期：** 2025-11-03  
**测试工具：** Postman  
**后端地址：** http://localhost:8080

---

## 📋 目录

1. [环境准备](#环境准备)
2. [Postman配置](#postman配置)
3. [用户模块测试](#用户模块测试)
4. [商品模块测试](#商品模块测试)
5. [购物车模块测试](#购物车模块测试)
6. [订单模块测试](#订单模块测试)
7. [地址模块测试](#地址模块测试)
8. [其他模块测试](#其他模块测试)
9. [常见问题](#常见问题)

---

## 🚀 环境准备

### 1. 启动后端服务

```bash
# 进入后端目录
cd web-shop-backend

# 确保数据库已初始化
mysql -u root -p < src/main/resources/database_init.sql

# 启动应用
mvn spring-boot:run
```

### 2. 验证服务启动

访问API文档：http://localhost:8080/doc.html  
如果能正常打开Knife4j文档页面，说明服务启动成功。

### 3. 安装Postman

下载地址：https://www.postman.com/downloads/

---

## ⚙️ Postman配置

### 1. 创建环境变量

在Postman中创建环境（Environment）：

| 变量名 | 初始值 | 当前值 |
|--------|--------|--------|
| baseUrl | http://localhost:8080 | http://localhost:8080 |
| userId | 1 | (测试时会更新) |
| token | | (登录后会更新) |
| productId | 1 | (测试时会更新) |
| orderId | 1 | (测试时会更新) |
| cartId | 1 | (测试时会更新) |
| addressId | 1 | (测试时会更新) |

### 2. 创建Collection

创建一个名为 "WebShop API Tests" 的Collection，用于组织所有测试请求。

### 3. 设置公共请求头

在Collection级别设置：
```
Content-Type: application/json
Accept: application/json
```

---

## 👤 用户模块测试

### 1. 用户注册

**接口：** POST `/api/user/register`

**请求示例：**
```json
{
  "username": "testuser001",
  "password": "123456",
  "phone": "13800138001",
  "nickname": "测试用户001"
}
```

**预期响应：**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 1,
    "username": "testuser001",
    "nickname": "测试用户001"
  }
}
```

**测试脚本（Tests标签）：**
```javascript
// 验证状态码
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// 验证响应结构
pm.test("Response has correct structure", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
    pm.expect(jsonData.message).to.eql("注册成功");
    pm.expect(jsonData.data).to.have.property("userId");
});

// 保存userId到环境变量
var jsonData = pm.response.json();
if (jsonData.data && jsonData.data.userId) {
    pm.environment.set("userId", jsonData.data.userId);
}
```

---

### 2. 用户登录

**接口：** POST `/api/user/login`

**请求示例：**
```json
{
  "username": "testuser001",
  "password": "123456"
}
```

**预期响应：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "username": "testuser001",
    "nickname": "测试用户001",
    "phone": "13800138001",
    "role": 0,
    "token": "mock-token-1"
  }
}
```

**测试脚本：**
```javascript
pm.test("Login successful", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
    pm.expect(jsonData.data).to.have.property("token");
    
    // 保存token和userId
    pm.environment.set("token", jsonData.data.token);
    pm.environment.set("userId", jsonData.data.userId);
});
```

---

### 3. 获取用户信息

**接口：** GET `/api/user/info/{{userId}}`

**URL：** `{{baseUrl}}/api/user/info/{{userId}}`

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser001",
    "nickname": "测试用户001",
    "phone": "13800138001",
    "role": 0,
    "status": 1,
    "password": null
  }
}
```

---

### 4. 更新用户信息

**接口：** PUT `/api/user/{{userId}}`

**请求示例：**
```json
{
  "nickname": "新昵称",
  "avatar": "http://example.com/avatar.jpg"
}
```

---

### 5. 修改密码

**接口：** PUT `/api/user/{{userId}}/password`

**请求参数（Query Params）：**
- oldPassword: 123456
- newPassword: 654321

**URL：** `{{baseUrl}}/api/user/{{userId}}/password?oldPassword=123456&newPassword=654321`

---

### 6. 分页查询用户列表（管理员）

**接口：** GET `/api/user/list`

**请求参数（Query Params）：**
- pageNum: 1
- pageSize: 10
- keyword: test
- role: 0
- status: 1

**URL：** `{{baseUrl}}/api/user/list?pageNum=1&pageSize=10`

---

## 📦 商品模块测试

### 1. 新增商品（管理员）

**接口：** POST `/api/product`

**请求示例：**
```json
{
  "name": "iPhone 15 Pro Max",
  "categoryId": 1,
  "price": 9999.00,
  "stock": 100,
  "description": "苹果最新旗舰手机",
  "images": "[\"http://example.com/img1.jpg\", \"http://example.com/img2.jpg\"]",
  "status": 1,
  "isHot": 1,
  "isRecommend": 1
}
```

**测试脚本：**
```javascript
pm.test("Product created successfully", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
    
    // 保存商品ID
    if (jsonData.data && jsonData.data.id) {
        pm.environment.set("productId", jsonData.data.id);
    }
});
```

---

### 2. 分页查询商品列表

**接口：** GET `/api/product/list`

**请求参数（Query Params）：**
- pageNum: 1
- pageSize: 10
- categoryId: 1
- name: iPhone
- minPrice: 5000
- maxPrice: 15000
- status: 1

**URL：** `{{baseUrl}}/api/product/list?pageNum=1&pageSize=10&categoryId=1`

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "iPhone 15 Pro Max",
        "price": 9999.00,
        "stock": 100,
        "sales": 0,
        "status": 1
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 3. 获取商品详情

**接口：** GET `/api/product/{{productId}}`

**URL：** `{{baseUrl}}/api/product/{{productId}}`

---

### 4. 更新商品信息

**接口：** PUT `/api/product/{{productId}}`

**请求示例：**
```json
{
  "name": "iPhone 15 Pro Max（更新）",
  "price": 9899.00,
  "stock": 150
}
```

---

### 5. 商品上架

**接口：** PUT `/api/product/{{productId}}/on-shelf`

**URL：** `{{baseUrl}}/api/product/{{productId}}/on-shelf`

---

### 6. 商品下架

**接口：** PUT `/api/product/{{productId}}/off-shelf`

**URL：** `{{baseUrl}}/api/product/{{productId}}/off-shelf`

---

### 7. 获取热门商品

**接口：** GET `/api/product/hot`

**请求参数：**
- limit: 10

**URL：** `{{baseUrl}}/api/product/hot?limit=10`

---

### 8. 获取推荐商品

**接口：** GET `/api/product/recommend`

**请求参数：**
- limit: 10

**URL：** `{{baseUrl}}/api/product/recommend?limit=10`

---

## 🛒 购物车模块测试

### 1. 添加商品到购物车

**接口：** POST `/api/cart`

**请求示例：**
```json
{
  "userId": 1,
  "productId": 1,
  "quantity": 2,
  "specInfo": "颜色:黑色;内存:256GB"
}
```

**测试脚本：**
```javascript
pm.test("Add to cart successful", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
    pm.expect(jsonData.message).to.eql("添加成功");
});
```

---

### 2. 获取购物车列表

**接口：** GET `/api/cart/{{userId}}`

**URL：** `{{baseUrl}}/api/cart/{{userId}}`

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "productId": 1,
      "quantity": 2,
      "specInfo": "颜色:黑色;内存:256GB",
      "product": {
        "id": 1,
        "name": "iPhone 15 Pro Max",
        "price": 9999.00
      }
    }
  ]
}
```

**测试脚本：**
```javascript
pm.test("Cart list retrieved", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
    
    // 保存第一个购物车项的ID
    if (jsonData.data && jsonData.data.length > 0) {
        pm.environment.set("cartId", jsonData.data[0].id);
    }
});
```

---

### 3. 更新购物车商品数量

**接口：** PUT `/api/cart/{{cartId}}`

**请求参数：**
- quantity: 3

**URL：** `{{baseUrl}}/api/cart/{{cartId}}?quantity=3`

---

### 4. 删除购物车商品

**接口：** DELETE `/api/cart/{{cartId}}`

**URL：** `{{baseUrl}}/api/cart/{{cartId}}`

---

### 5. 清空购物车

**接口：** DELETE `/api/cart/user/{{userId}}`

**URL：** `{{baseUrl}}/api/cart/user/{{userId}}`

---

### 6. 获取购物车商品数量

**接口：** GET `/api/cart/{{userId}}/count`

**URL：** `{{baseUrl}}/api/cart/{{userId}}/count`

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5
}
```

---

## 📍 地址模块测试

### 1. 添加收货地址

**接口：** POST `/api/address`

**请求示例：**
```json
{
  "userId": 1,
  "receiverName": "张三",
  "receiverPhone": "13800138001",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detailAddress": "科技园南区XX路XX号",
  "isDefault": 1
}
```

**测试脚本：**
```javascript
pm.test("Address added successfully", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
});
```

---

### 2. 获取用户所有地址

**接口：** GET `/api/address/user/{{userId}}`

**URL：** `{{baseUrl}}/api/address/user/{{userId}}`

**测试脚本：**
```javascript
pm.test("Address list retrieved", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
    
    // 保存第一个地址的ID
    if (jsonData.data && jsonData.data.length > 0) {
        pm.environment.set("addressId", jsonData.data[0].id);
    }
});
```

---

### 3. 获取默认地址

**接口：** GET `/api/address/user/{{userId}}/default`

**URL：** `{{baseUrl}}/api/address/user/{{userId}}/default`

---

### 4. 更新地址

**接口：** PUT `/api/address/{{addressId}}`

**请求示例：**
```json
{
  "receiverName": "李四",
  "receiverPhone": "13900139001"
}
```

---

### 5. 设置默认地址

**接口：** PUT `/api/address/{{addressId}}/default`

**请求参数：**
- userId: 1

**URL：** `{{baseUrl}}/api/address/{{addressId}}/default?userId=1`

---

### 6. 删除地址

**接口：** DELETE `/api/address/{{addressId}}`

**请求参数：**
- userId: 1

**URL：** `{{baseUrl}}/api/address/{{addressId}}?userId=1`

---

## 📦 订单模块测试

### 1. 创建订单

**接口：** POST `/api/orders`

**请求示例：**
```json
{
  "userId": 1,
  "addressId": 1,
  "cartItemIds": [1, 2, 3],
  "note": "请尽快发货，谢谢！"
}
```

**测试脚本：**
```javascript
pm.test("Order created successfully", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
    
    // 保存订单号
    if (jsonData.data && jsonData.data.orderNo) {
        pm.environment.set("orderNo", jsonData.data.orderNo);
        pm.environment.set("orderId", jsonData.data.id);
    }
});
```

---

### 2. 获取订单详情

**接口：** GET `/api/orders/{{orderId}}`

**URL：** `{{baseUrl}}/api/orders/{{orderId}}`

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "ORDER20251103001",
    "userId": 1,
    "totalAmount": 19998.00,
    "payAmount": 19998.00,
    "status": 0,
    "receiverName": "张三",
    "receiverPhone": "13800138001",
    "receiverAddress": "广东省深圳市南山区科技园南区XX路XX号"
  }
}
```

---

### 3. 获取用户订单列表

**接口：** GET `/api/orders/user/{{userId}}`

**请求参数：**
- pageNum: 1
- pageSize: 10
- status: 0

**URL：** `{{baseUrl}}/api/orders/user/{{userId}}?pageNum=1&pageSize=10`

---

### 4. 支付订单

**接口：** PUT `/api/orders/{{orderNo}}/pay`

**请求参数：**
- userId: 1
- paymentMethod: 1

**URL：** `{{baseUrl}}/api/orders/{{orderNo}}/pay?userId=1&paymentMethod=1`

**预期响应：**
```json
{
  "code": 200,
  "message": "支付成功",
  "data": null
}
```

---

### 5. 发货

**接口：** PUT `/api/orders/{{orderNo}}/ship`

**请求参数：**
- expressCompany: 顺丰速运
- trackingNo: SF1234567890

**URL：** `{{baseUrl}}/api/orders/{{orderNo}}/ship?expressCompany=顺丰速运&trackingNo=SF1234567890`

---

### 6. 确认收货

**接口：** PUT `/api/orders/{{orderNo}}/receive`

**请求参数：**
- userId: 1

**URL：** `{{baseUrl}}/api/orders/{{orderNo}}/receive?userId=1`

---

### 7. 取消订单

**接口：** PUT `/api/orders/{{orderNo}}/cancel`

**请求参数：**
- userId: 1
- reason: 不想要了

**URL：** `{{baseUrl}}/api/orders/{{orderNo}}/cancel?userId=1&reason=不想要了`

---

### 8. 获取订单统计数据

**接口：** GET `/api/orders/statistics`

**URL：** `{{baseUrl}}/api/orders/statistics`

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalOrders": 100,
    "totalSales": 999999.00,
    "avgOrderAmount": 9999.99
  }
}
```

---

### 9. 获取订单状态分布

**接口：** GET `/api/orders/status-distribution`

**URL：** `{{baseUrl}}/api/orders/status-distribution`

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "0": 10,
    "1": 20,
    "2": 15,
    "3": 50,
    "4": 5
  }
}
```

---

## 🏷️ 商品分类测试

### 1. 获取所有分类（树形）

**接口：** GET `/api/category/tree`

**URL：** `{{baseUrl}}/api/category/tree`

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "数码产品",
      "parentId": 0,
      "sort": 1,
      "children": [
        {
          "id": 2,
          "name": "手机",
          "parentId": 1,
          "sort": 1
        }
      ]
    }
  ]
}
```

---

### 2. 新增分类

**接口：** POST `/api/category`

**请求示例：**
```json
{
  "name": "家用电器",
  "parentId": 0,
  "sort": 1
}
```

---

### 3. 更新分类

**接口：** PUT `/api/category/{{categoryId}}`

**请求示例：**
```json
{
  "name": "数码产品（更新）",
  "sort": 2
}
```

---

### 4. 删除分类

**接口：** DELETE `/api/category/{{categoryId}}`

---

## ⭐ 其他模块测试

### 商品评价

**1. 发布评价**

**接口：** POST `/api/review`

**请求示例：**
```json
{
  "userId": 1,
  "productId": 1,
  "orderId": 1,
  "rating": 5,
  "content": "商品非常好，物流快，好评！",
  "images": "[\"http://example.com/review1.jpg\"]"
}
```

**2. 获取商品评价列表**

**接口：** GET `/api/review/product/{{productId}}`

**请求参数：**
- pageNum: 1
- pageSize: 10

---

### 用户收藏

**1. 添加收藏**

**接口：** POST `/api/favorite`

**请求示例：**
```json
{
  "userId": 1,
  "productId": 1
}
```

**2. 取消收藏**

**接口：** DELETE `/api/favorite/{{favoriteId}}`

**3. 获取收藏列表**

**接口：** GET `/api/favorite/user/{{userId}}`

---

### 浏览历史

**1. 记录浏览历史**

**接口：** POST `/api/history`

**请求示例：**
```json
{
  "userId": 1,
  "productId": 1
}
```

**2. 获取浏览历史**

**接口：** GET `/api/history/user/{{userId}}`

**请求参数：**
- limit: 20

---

## 🧪 完整测试流程

### 测试场景：完整购物流程

**执行顺序：**

1. **用户注册** → POST `/api/user/register`
2. **用户登录** → POST `/api/user/login`
3. **浏览商品** → GET `/api/product/list`
4. **查看商品详情** → GET `/api/product/{id}`
5. **添加到购物车** → POST `/api/cart`
6. **查看购物车** → GET `/api/cart/{userId}`
7. **添加收货地址** → POST `/api/address`
8. **创建订单** → POST `/api/orders`
9. **支付订单** → PUT `/api/orders/{orderNo}/pay`
10. **商家发货** → PUT `/api/orders/{orderNo}/ship`
11. **确认收货** → PUT `/api/orders/{orderNo}/receive`
12. **发布评价** → POST `/api/review`

---

## 📝 测试数据准备

### 创建测试用户

```json
// 普通用户
{
  "username": "buyer001",
  "password": "123456",
  "phone": "13800138001",
  "nickname": "买家001"
}

// 商家用户（需要在数据库手动设置role=1）
{
  "username": "seller001",
  "password": "123456",
  "phone": "13900139001",
  "nickname": "商家001"
}
```

### 创建测试商品

```json
{
  "name": "iPhone 15 Pro Max",
  "categoryId": 1,
  "price": 9999.00,
  "stock": 100,
  "description": "苹果最新旗舰手机，性能强劲",
  "images": "[\"http://example.com/iphone1.jpg\"]",
  "status": 1,
  "isHot": 1,
  "isRecommend": 1
}
```

---

## ❓ 常见问题

### Q1: 接口返回404错误

**原因：** 服务未启动或URL路径错误

**解决：**
1. 检查服务是否启动：`curl http://localhost:8080/doc.html`
2. 检查请求URL是否正确，注意前缀 `/api`

### Q2: 接口返回500错误

**原因：** 服务器内部错误

**解决：**
1. 查看后端控制台日志
2. 检查请求参数是否正确
3. 检查数据库连接是否正常

### Q3: 创建订单失败

**原因：** 购物车为空或地址不存在

**解决：**
1. 先添加商品到购物车
2. 先创建收货地址
3. 确保cartItemIds正确

### Q4: 用户登录失败

**原因：** 用户不存在或密码错误

**解决：**
1. 先注册用户
2. 检查用户名密码是否正确
3. 检查数据库user表

---

## 📊 测试检查清单

### 基础功能测试
- [ ] 用户注册
- [ ] 用户登录
- [ ] 获取用户信息
- [ ] 更新用户信息

### 商品功能测试
- [ ] 商品列表查询
- [ ] 商品详情查看
- [ ] 商品增删改（管理员）
- [ ] 商品上下架（管理员）

### 购物车功能测试
- [ ] 添加商品到购物车
- [ ] 更新购物车数量
- [ ] 删除购物车商品
- [ ] 查看购物车列表

### 订单功能测试
- [ ] 创建订单
- [ ] 订单支付
- [ ] 订单发货
- [ ] 确认收货
- [ ] 取消订单

### 地址功能测试
- [ ] 添加收货地址
- [ ] 更新收货地址
- [ ] 设置默认地址
- [ ] 删除收货地址

---

## 💡 高级技巧

### 1. 使用Collection Runner批量测试

在Postman中：
1. 点击Collection右侧的 "..."
2. 选择 "Run collection"
3. 设置迭代次数和延迟
4. 点击 "Run" 批量执行

### 2. 使用Pre-request Script准备数据

```javascript
// 生成随机用户名
pm.environment.set("randomUsername", "user" + Date.now());

// 生成随机手机号
pm.environment.set("randomPhone", "138" + Math.floor(Math.random() * 100000000));
```

### 3. 链式测试

在Tests标签中保存响应数据到环境变量，供后续请求使用：

```javascript
var jsonData = pm.response.json();
pm.environment.set("productId", jsonData.data.id);
pm.environment.set("orderNo", jsonData.data.orderNo);
```

---

## 🎯 测试报告模板

### 测试概要

| 项目 | 内容 |
|------|------|
| 测试日期 | 2025-11-03 |
| 测试人员 | XXX |
| 测试环境 | 本地开发环境 |
| 测试结果 | ✅ 通过 / ❌ 失败 |

### 测试详情

| 模块 | 测试用例数 | 通过 | 失败 | 通过率 |
|------|-----------|------|------|--------|
| 用户模块 | 6 | 6 | 0 | 100% |
| 商品模块 | 8 | 8 | 0 | 100% |
| 购物车模块 | 6 | 6 | 0 | 100% |
| 订单模块 | 9 | 9 | 0 | 100% |
| 地址模块 | 6 | 6 | 0 | 100% |
| **总计** | **35** | **35** | **0** | **100%** |

### 问题记录

| 编号 | 问题描述 | 严重程度 | 状态 |
|------|---------|---------|------|
| 1 | XXX接口返回500错误 | 高 | 已修复 |
| 2 | XXX字段缺失 | 中 | 待修复 |

---

## 📎 附录

### Postman导出配置

完成测试后，可以导出Collection和Environment：

1. **导出Collection：**
   - 右键Collection → Export
   - 选择Collection v2.1格式
   - 保存为 `WebShop-API-Tests.postman_collection.json`

2. **导出Environment：**
   - 点击右上角齿轮图标
   - 选择Environment → Export
   - 保存为 `WebShop-Local.postman_environment.json`

### 分享给团队

将导出的两个JSON文件分享给团队成员，他们可以直接导入使用。

---

**文档维护：** WebShop Team  
**最后更新：** 2025-11-03  
**版本：** v1.0

**祝测试顺利！** 🎉

