# SKU库存匹配和展示逻辑修复总结

## 问题描述
测试时发现：当白色256GB库存为1时，256GB显示无货的情况。这是因为前端SKU库存匹配逻辑存在缺陷。

## 根本原因
在 `ProductDetail.vue` 中，SKU库存处理逻辑有以下问题：
1. 当多个SKU具有相同的规格值时，后面的SKU会覆盖前面的库存值
2. 规格选项的库存显示不准确，导致某些规格组合显示为无货

## 解决方案

### 前端修改

#### 1. ProductDetail.vue - SKU数据结构优化
**文件**: `web-shop-frontend/src/pages/product/ProductDetail.vue`

**修改内容**:
- 改变规格选项的数据结构，从直接存储库存改为存储所有匹配的SKU列表
- 新结构: `option.skus = [{id, stock, price, skuCode, attributes}, ...]`
- 计算每个规格选项的总库存为所有匹配SKU的库存之和

**关键代码**:
```javascript
// 旧方式：单个SKU覆盖
option.stock = sku.stock || 0

// 新方式：收集所有匹配的SKU
option.skus.push({
  id: sku.id,
  stock: sku.stock || 0,
  price: sku.price || data.price,
  skuCode: sku.skuCode,
  attributes: skuAttrs
})

// 计算总库存
option.stock = option.skus.reduce((sum, sku) => sum + sku.stock, 0)
```

#### 2. ProductDetail.vue - 规格选择逻辑
**修改内容**:
- 更新 `selectSpec()` 函数，正确处理新的SKU数据结构
- 确保 `selectedSpec.value` 包含 `skuId` 字段

#### 3. ProductDetail.vue - 购物车和订单
**修改内容**:
- `handleAddToCart()`: 添加 `skuId` 到购物车数据
- `handleBuyNow()`: 添加 `skuId` 到订单数据

#### 4. 购物车API和Store
**文件**: 
- `web-shop-frontend/src/api/cart.js`
- `web-shop-frontend/src/stores/cartStore.js`

**修改内容**:
- `addToCart()` API: 添加 `skuId` 参数
- `cartStore.fetchCartList()`: 处理返回的 `skuId` 字段

#### 5. 订单页面
**文件**: `web-shop-frontend/src/pages/order/Checkout.vue`

**修改内容**:
- 订单创建时传递 `skuId`
- 立即购买场景也包含 `skuId`

### 后端修改

#### 1. 实体类修改

**ShoppingCart.java**:
```java
private Long skuId;  // 新增字段
```

**OrderCreateDTO.java**:
```java
public static class OrderItemDTO {
    private Long skuId;  // 新增字段
}
```

#### 2. Service层修改

**ShoppingCartService.java**:
```java
void addToCart(Long userId, Long productId, Integer quantity, String specInfo, Long skuId);
```

**ShoppingCartServiceImpl.java**:
- 更新 `addToCart()` 方法签名，添加 `skuId` 参数
- 保存购物车时设置 `skuId`

**ShoppingCartController.java**:
- 更新 `addToCart()` 方法，传递 `skuId` 到Service

#### 3. 数据库迁移脚本

**add_sku_id_to_shopping_cart.sql**:
```sql
ALTER TABLE `shopping_cart` ADD COLUMN `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID' AFTER `product_id`;
```

**add_sku_id_to_order_item.sql**:
```sql
ALTER TABLE `order_item` ADD COLUMN `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID' AFTER `product_id`;
```

## 修改文件清单

### 前端文件
- ✅ `web-shop-frontend/src/pages/product/ProductDetail.vue` - SKU匹配和展示逻辑
- ✅ `web-shop-frontend/src/api/cart.js` - 购物车API
- ✅ `web-shop-frontend/src/stores/cartStore.js` - 购物车Store
- ✅ `web-shop-frontend/src/pages/order/Checkout.vue` - 订单页面

### 后端文件
- ✅ `web-shop-backend/src/main/java/org/javaweb/webshopbackend/pojo/entity/ShoppingCart.java`
- ✅ `web-shop-backend/src/main/java/org/javaweb/webshopbackend/pojo/dto/OrderCreateDTO.java`
- ✅ `web-shop-backend/src/main/java/org/javaweb/webshopbackend/service/ShoppingCartService.java`
- ✅ `web-shop-backend/src/main/java/org/javaweb/webshopbackend/service/impl/ShoppingCartServiceImpl.java`
- ✅ `web-shop-backend/src/main/java/org/javaweb/webshopbackend/controller/ShoppingCartController.java`

### 数据库迁移脚本
- ✅ `web-shop-backend/src/main/resources/add_sku_id_to_shopping_cart.sql`
- ✅ `web-shop-backend/src/main/resources/add_sku_id_to_order_item.sql`

## 测试步骤

1. **执行数据库迁移脚本**
   ```sql
   -- 在MySQL中执行以下脚本
   ALTER TABLE `shopping_cart` ADD COLUMN `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID' AFTER `product_id`;
   ALTER TABLE `order_item` ADD COLUMN `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID' AFTER `product_id`;
   ```

2. **重启后端服务**
   - 确保所有Java代码修改被编译

3. **测试场景**
   - 添加商品到购物车时，验证SKU库存显示正确
   - 选择不同规格时，库存数字应该准确更新
   - 白色256GB库存为1时，应该显示"1件"而不是"无货"

4. **验证订单创建**
   - 从购物车创建订单时，SKU ID应该被正确保存
   - 立即购买时，SKU ID应该被正确传递

## 关键改进点

1. **准确的库存计算**: 规格选项的库存现在是所有匹配SKU的库存之和
2. **正确的SKU关联**: 购物车和订单项现在都关联了具体的SKU ID
3. **库存扣减准确**: 订单创建时可以根据SKU ID准确扣减库存
4. **库存恢复准确**: 订单取消时可以根据SKU ID准确恢复库存

## 注意事项

- 数据库迁移脚本需要手动执行
- 如果数据库中已有 `sku_id` 列，迁移脚本会报错（可以忽略）
- 建议在测试环境先验证，再在生产环境执行
