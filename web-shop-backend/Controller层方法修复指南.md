# Controller层方法修复指南

## 📋 概述

Controller层已创建完成，但部分方法调用需要与Service层实际方法名匹配。本文档提供快速修复指南。

---

## ✅ 已修复的Controller

### 1. UserController ✅
- ✅ `register()` - 修改为调用 `userService.register(username, password, phone)`
- ✅ `getUserInfo()` - 修改为调用 `userService.getById(userId)`  
- ✅ `updateUser()` - 修改为调用 `userService.updateUserInfo(user)`
- ✅ `updatePassword()` - 修改为调用 `userService.changePassword(...)`
- ✅ `getUserList()` - 修改为调用 `userService.listUsers(...)`
- ✅ `disableUser()` - 修改为调用 `userService.updateUserStatus(userId, 0)`
- ✅ `enableUser()` - 修改为调用 `userService.updateUserStatus(userId, 1)`
- ✅ `deleteUser()` - 修改为调用 `userService.removeById(userId)`
- ✅ Result参数顺序修改为 `Result.success("message", data)`

### 2. ProductController ✅
- ✅ `getProductDetail()` - 修改为调用 `productService.getProductById(productId)`
- ✅ `onShelfProduct()` - 修改为调用 `productService.onShelfProduct(productId)`
- ✅ `offShelfProduct()` - 修改为调用 `productService.offShelfProduct(productId)`
- ✅ `batchOnShelf()` - 修改为调用 `productService.batchOnShelfProducts(productIds)`
- ✅ `batchOffShelf()` - 修改为调用 `productService.batchOffShelfProducts(productIds)`
- ✅ `updateStock()` - 修改为调用 `productService.updateProductStock(productId, stock)`

---

## ⚠️ 待修复的Controller

### 3. ShoppingCartController
需要修复的方法：
- `addToCart()` - 需要检查Service方法签名，可能需要4个参数
- `removeFromCart()` - 需要检查Service方法名
- `getCartCount()` - 需要检查Service方法名

### 4. OrdersController  
需要修复的方法：
- `cancelOrder()` - Service方法需要更多参数
- `payOrder()` - Service方法需要更多参数
- `shipOrder()` - Service方法签名不同
- `receiveOrder()` - 需要检查方法名
- `addRemark()` - 需要检查方法名
- 多个统计方法需要检查方法名

### 5. UserAddressController
需要修复的方法：
- `getUserAddresses()` - 需要检查方法名
- `deleteAddress()` - 可能需要额外参数
- `setDefaultAddress()` - 可能需要额外参数

---

## 🔧 通用修复规则

### Result类使用规范
```java
// ❌ 错误用法
return Result.success(data, "消息");

// ✅ 正确用法
return Result.success("消息", data);
return Result.success(data);  // 仅返回数据
return Result.success("消息");  // 仅返回消息
```

### Service方法调用规范
1. **优先使用MyBatis-Plus提供的方法**
   - `getById(id)` - 查询单个
   - `list()` - 查询列表
   - `page(page, wrapper)` - 分页查询
   - `save(entity)` - 保存
   - `updateById(entity)` - 更新
   - `removeById(id)` - 删除

2. **自定义方法命名规范**
   - 查询：`getXxx()`, `listXxx()`, `findXxx()`
   - 更新：`updateXxx()`, `modifyXxx()`
   - 删除：`deleteXxx()`, `removeXxx()`
   - 统计：`countXxx()`, `statisticsXxx()`

---

## 📝 快速修复步骤

### 步骤1：查看Service接口
```bash
# 查看Service接口的实际方法定义
grep "public.*方法名" web-shop-backend/src/main/java/org/javaweb/webshopbackend/service/xxxService.java
```

### 步骤2：修改Controller调用
根据实际方法签名修改Controller中的调用

### 步骤3：验证修复
```bash
# 检查编译错误
mvn compile
```

---

## 🎯 建议的修复顺序

1. ✅ **UserController** - 已完成
2. ✅ **ProductController** - 已完成  
3. ⬜ **ShoppingCartController** - 简单，优先修复
4. ⬜ **UserAddressController** - 简单，优先修复
5. ⬜ **ProductCategoryController** - 可能无需修改
6. ⬜ **ProductReviewController** - 可能无需修改
7. ⬜ **UserFavoriteController** - 可能无需修改
8. ⬜ **RefundController** - 可能无需修改
9. ⬜ **BrowsingHistoryController** - 可能无需修改
10. ⬜ **OrdersController** - 较复杂，最后修复

---

## 💡 提示

1. **大部分Controller可能无需修改** - ProductCategoryController、ProductReviewController等可能已经匹配
2. **重点关注统计方法** - OrdersController的统计方法最需要检查
3. **参数校验保持不变** - 只需修改Service方法调用
4. **Result参数顺序** - 确保所有地方都是 `Result.success("message", data)`

---

## 📞 参考文档

- [Service层完整总结.md](./Service层完整总结.md) - 查看所有Service方法
- [Controller层创建完成报告.md](./Controller层创建完成报告.md) - Controller完整列表

---

**创建日期：** 2025-11-03  
**状态：** 部分Controller已修复，其余待处理

