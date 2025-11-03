# Service 层创建指南

## 📋 Service 层结构

### 完整列表（12个模块，24个文件）

| 模块 | Service 接口 | ServiceImpl 实现类 | 状态 |
|------|-------------|-------------------|------|
| 用户 | `UserService.java` | `UserServiceImpl.java` | ✅ |
| 地址 | `UserAddressService.java` | `UserAddressServiceImpl.java` | ⏳ |
| 分类 | `ProductCategoryService.java` | `ProductCategoryServiceImpl.java` | ⏳ |
| 商品 | `ProductService.java` | `ProductServiceImpl.java` | ⏳ |
| 购物车 | `ShoppingCartService.java` | `ShoppingCartServiceImpl.java` | ⏳ |
| 订单 | `OrdersService.java` | `OrdersServiceImpl.java` | ⏳ |
| 订单项 | `OrderItemService.java` | `OrderItemServiceImpl.java` | ⏳ |
| 评价 | `ProductReviewService.java` | `ProductReviewServiceImpl.java` | ⏳ |
| 收藏 | `UserFavoriteService.java` | `UserFavoriteServiceImpl.java` | ⏳ |
| 历史 | `BrowsingHistoryService.java` | `BrowsingHistoryServiceImpl.java` | ⏳ |
| 日志 | `OperationLogService.java` | `OperationLogServiceImpl.java` | ⏳ |
| 退款 | `RefundService.java` | `RefundServiceImpl.java` | ⏳ |

---

## 📝 Service 接口模板

```java
package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.EntityName;

/**
 * XXX Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface XxxService extends IService<EntityName> {

    // 自定义业务方法
    
}
```

---

## 📝 ServiceImpl 实现类模板

```java
package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.XxxMapper;
import org.javaweb.webshopbackend.pojo.entity.EntityName;
import org.javaweb.webshopbackend.service.XxxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * XXX Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class XxxServiceImpl extends ServiceImpl<XxxMapper, EntityName> implements XxxService {

    // 实现业务方法
    
}
```

---

## 🎯 核心业务逻辑说明

### 1. UserService（已完成 ✅）

**核心方法**:
- `register()` - 用户注册（验证用户名/手机号唯一性，密码加密）
- `login()` - 用户登录（验证账号状态，密码验证，更新登录时间）
- `merchantLogin()` - 商家登录（额外验证角色）
- `changePassword()` - 修改密码（验证旧密码，更新新密码）

**关键点**:
- 密码使用 MD5 加密（生产环境建议 BCrypt）
- 登录验证账号是否删除/禁用
- 返回用户信息时不包含密码

---

### 2. ProductService（接口已创建）

**核心方法**:
- `getProductPage()` - 分页查询商品（支持分类、关键词、价格区间、排序）
- `getHotProducts()` - 获取热销商品
- `getRecommendProducts()` - 获取推荐商品
- `addProduct()` - 新增商品
- `updateProductStock()` - 更新库存（支持增减）
- `updateProductSales()` - 更新销量
- `batchUpdateProductStatus()` - 批量上下架

**实现要点**:
```java
@Override
public IPage<Product> getProductPage(Page<Product> page, Long categoryId, 
                                     String keyword, BigDecimal minPrice, 
                                     BigDecimal maxPrice, String sortBy) {
    LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Product::getStatus, 1)  // 只查上架商品
           .eq(categoryId != null, Product::getCategoryId, categoryId)
           .like(keyword != null, Product::getName, keyword)
           .ge(minPrice != null, Product::getPrice, minPrice)
           .le(maxPrice != null, Product::getPrice, maxPrice);
    
    // 排序
    if ("sales".equals(sortBy)) {
        wrapper.orderByDesc(Product::getSales);
    } else if ("price".equals(sortBy)) {
        wrapper.orderByAsc(Product::getPrice);
    }
    
    return this.page(page, wrapper);
}
```

---

### 3. ShoppingCartService

**核心方法**:
- `addToCart()` - 添加到购物车（检查商品是否存在，合并相同商品）
- `getCartList()` - 获取购物车列表（带商品信息）
- `updateQuantity()` - 更新数量
- `updateChecked()` - 更新选中状态
- `clearCart()` - 清空购物车
- `getCheckedItems()` - 获取选中项（用于结算）

**实现要点**:
```java
@Override
@Transactional(rollbackFor = Exception.class)
public void addToCart(Long userId, Long productId, Integer quantity) {
    // 1. 检查商品是否存在且上架
    Product product = productService.getById(productId);
    if (product == null || product.getStatus() == 0) {
        throw new IllegalArgumentException("商品不存在或已下架");
    }
    
    // 2. 检查库存
    if (!productService.checkStock(productId, quantity)) {
        throw new IllegalArgumentException("商品库存不足");
    }
    
    // 3. 查询购物车中是否已有该商品
    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ShoppingCart::getUserId, userId)
           .eq(ShoppingCart::getProductId, productId);
    ShoppingCart cartItem = this.getOne(wrapper);
    
    if (cartItem != null) {
        // 已存在，更新数量
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        this.updateById(cartItem);
    } else {
        // 新增
        cartItem = new ShoppingCart();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        cartItem.setChecked(1);
        this.save(cartItem);
    }
}
```

---

### 4. OrdersService

**核心方法**:
- `createOrder()` - 创建订单（检查库存，减库存，清购物车）
- `getOrderPage()` - 分页查询订单（用户端）
- `getAdminOrderPage()` - 分页查询订单（管理端）
- `getOrderDetail()` - 获取订单详情（含订单项）
- `cancelOrder()` - 取消订单（恢复库存）
- `payOrder()` - 支付订单（更新状态，增加销量）
- `shipOrder()` - 发货（填写物流信息）
- `confirmReceive()` - 确认收货

**实现要点**:
```java
@Override
@Transactional(rollbackFor = Exception.class)
public Orders createOrder(Long userId, Long addressId, List<Long> cartItemIds, String note) {
    // 1. 查询地址
    UserAddress address = userAddressService.getById(addressId);
    if (address == null) {
        throw new IllegalArgumentException("收货地址不存在");
    }
    
    // 2. 查询购物车项
    List<ShoppingCart> cartItems = shoppingCartService.listByIds(cartItemIds);
    if (cartItems.isEmpty()) {
        throw new IllegalArgumentException("购物车为空");
    }
    
    // 3. 检查库存并计算总金额
    BigDecimal totalAmount = BigDecimal.ZERO;
    for (ShoppingCart cartItem : cartItems) {
        Product product = productService.getById(cartItem.getProductId());
        if (!productService.checkStock(product.getId(), cartItem.getQuantity())) {
            throw new IllegalArgumentException("商品 " + product.getName() + " 库存不足");
        }
        totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
    }
    
    // 4. 创建订单
    Orders order = new Orders();
    order.setOrderNo(generateOrderNo());  // 生成订单号
    order.setUserId(userId);
    order.setTotalAmount(totalAmount);
    order.setPayAmount(totalAmount);  // 暂不考虑优惠
    order.setFreight(BigDecimal.ZERO);  // 暂不收运费
    order.setStatus(0);  // 待付款
    order.setReceiverName(address.getReceiverName());
    order.setReceiverPhone(address.getReceiverPhone());
    order.setReceiverAddress(address.getProvince() + address.getCity() + 
                            address.getDistrict() + address.getDetailAddress());
    order.setNote(note);
    this.save(order);
    
    // 5. 创建订单项并减库存
    for (ShoppingCart cartItem : cartItems) {
        Product product = productService.getById(cartItem.getProductId());
        
        // 创建订单项
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getCoverImage());
        orderItem.setUnitPrice(product.getPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setTotalPrice(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        orderItem.setIsReviewed(0);
        orderItemService.save(orderItem);
        
        // 减库存
        productService.updateProductStock(product.getId(), -cartItem.getQuantity());
    }
    
    // 6. 清除购物车
    shoppingCartService.removeByIds(cartItemIds);
    
    return order;
}

// 生成订单号
private String generateOrderNo() {
    return "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
}
```

---

### 5. ProductReviewService

**核心方法**:
- `addReview()` - 添加评价（验证订单项，更新评价状态）
- `getProductReviews()` - 获取商品评价
- `getAvgRating()` - 获取平均评分
- `replyReview()` - 商家回复评价

**实现要点**:
```java
@Override
@Transactional(rollbackFor = Exception.class)
public void addReview(ProductReview review) {
    // 1. 验证订单项是否存在且属于当前用户
    OrderItem orderItem = orderItemService.getById(review.getOrderItemId());
    if (orderItem == null) {
        throw new IllegalArgumentException("订单项不存在");
    }
    
    // 2. 检查是否已评价
    if (orderItem.getIsReviewed() == 1) {
        throw new IllegalArgumentException("该商品已评价");
    }
    
    // 3. 保存评价
    this.save(review);
    
    // 4. 更新订单项评价状态
    orderItem.setIsReviewed(1);
    orderItemService.updateById(orderItem);
}
```

---

## 🔧 通用 Service 功能

### IService 提供的基础方法

```java
// 保存
boolean save(T entity);
boolean saveBatch(Collection<T> entityList);

// 更新
boolean updateById(T entity);
boolean update(T entity, Wrapper<T> updateWrapper);
boolean updateBatchById(Collection<T> entityList);

// 删除
boolean removeById(Serializable id);
boolean remove(Wrapper<T> queryWrapper);
boolean removeByIds(Collection<? extends Serializable> idList);

// 查询
T getById(Serializable id);
T getOne(Wrapper<T> queryWrapper);
List<T> list();
List<T> list(Wrapper<T> queryWrapper);
List<T> listByIds(Collection<? extends Serializable> idList);
long count();
long count(Wrapper<T> queryWrapper);
IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper);
```

---

## ⚠️ 开发注意事项

### 1. 事务管理
```java
@Transactional(rollbackFor = Exception.class)
public void methodName() {
    // 涉及多表操作的方法必须加事务
}
```

### 2. 异常处理
```java
// 使用 IllegalArgumentException 抛出业务异常
if (condition) {
    throw new IllegalArgumentException("错误信息");
}
```

### 3. 日志记录
```java
log.info("操作描述：参数={}", param);
log.error("错误信息", exception);
```

### 4. 参数验证
```java
// 方法开始时验证参数
if (param == null) {
    throw new IllegalArgumentException("参数不能为空");
}
```

### 5. 敏感信息处理
```java
// 返回用户信息时清空密码
user.setPassword(null);
return user;
```

---

## 📊 Service 层开发进度

| 阶段 | 内容 | 文件数 | 进度 | 状态 |
|------|------|--------|------|------|
| **Service 接口** | 业务接口定义 | 12个 | 17% | ⏳ 进行中 |
| **ServiceImpl** | 业务逻辑实现 | 12个 | 8% | ⏳ 进行中 |
| **单元测试** | Service 测试 | 12个 | 0% | ⏳ 待开发 |

---

## 🎯 下一步计划

1. ✅ **UserService** - 已完成
2. ⏳ **ProductService** - 创建实现类
3. ⏳ **ShoppingCartService** - 创建接口和实现类
4. ⏳ **OrdersService** - 创建接口和实现类（最复杂）
5. ⏳ **其他 Service** - 依次创建

**预计工作量**: 20-30小时

---

**Service 层开发中！** 🚀

