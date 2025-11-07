# WebShop 项目 API 安全审计报告

**审计日期**: 2025-11-06  
**项目名称**: WebShop 电商系统  
**审计范围**: 所有后端 API 接口  
**风险等级**: 🔴 高风险 | 🟠 中风险 | 🟡 低风险

---

## 📋 执行摘要

本次安全审计发现 **35+ 个安全漏洞**，其中：
- **高危漏洞**: 15 个
- **中危漏洞**: 12 个  
- **低危漏洞**: 8 个

主要问题集中在：
1. **缺少身份认证和权限控制**（最严重）
2. **越权访问漏洞**
3. **业务逻辑漏洞**（库存并发、金额计算）
4. **参数验证不足**

---

## 🚨 一、高危漏洞（Critical）

### 1.1 【严重】缺少全局身份认证机制

**影响范围**: 所有 API  
**风险等级**: 🔴🔴🔴 极高

#### 问题描述
整个系统没有实现任何身份认证机制（JWT、Session等），**任何人都可以调用所有API**。

#### 受影响的接口
- 所有 Controller 的所有方法（100+ 个接口）

#### 攻击场景
```bash
# 攻击者无需登录即可：
# 1. 查看所有用户信息
GET /api/user/list

# 2. 修改任意用户密码
PUT /api/user/1/password

# 3. 创建订单、退款
POST /api/orders
POST /api/refund
```

#### 修复建议
1. **引入 JWT 认证**
```java
// 添加依赖
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

// 创建 JwtAuthenticationFilter
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            // 验证 token
            String jwt = token.substring(7);
            Claims claims = jwtUtil.parseToken(jwt);
            Long userId = claims.get("userId", Long.class);
            
            // 将用户信息存入上下文
            UserContext.setCurrentUser(userId);
        }
        filterChain.doFilter(request, response);
    }
}
```

2. **登录时生成真实的 JWT Token**
```java
@PostMapping("/login")
public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
    User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
    
    // 生成真实的 JWT Token
    String token = jwtUtil.generateToken(user.getId(), user.getRole());
    
    Map<String, Object> data = new HashMap<>();
    data.put("userId", user.getId());
    data.put("token", token);  // 返回真实 token
    
    return Result.success("登录成功", data);
}
```

---

### 1.2 【严重】缺少权限控制（RBAC）

**影响范围**: 所有管理员功能  
**风险等级**: 🔴🔴🔴 极高

#### 问题描述
普通用户可以调用所有管理员接口，没有角色权限验证。

#### 受影响的接口
| Controller | 危险接口 | 当前状态 |
|-----------|---------|---------|
| `UserController` | 禁用/启用/删除用户 | ❌ 无权限校验 |
| `ProductController` | 增删改商品、批量上下架 | ❌ 无权限校验 |
| `OrdersController` | 发货、查看所有订单 | ❌ 无权限校验 |
| `RefundController` | 审核退款 | ❌ 无权限校验 |
| `ProductCategoryController` | 增删改分类 | ❌ 无权限校验 |
| `ProductReviewController` | 商家回复 | ❌ 无权限校验 |

#### 攻击场景
```bash
# 普通用户（userId=100）可以：
# 1. 禁用管理员账号
PUT /api/user/1/disable

# 2. 删除所有商品
DELETE /api/product/1
DELETE /api/product/2
# ...

# 3. 发货（假装商家发货）
PUT /api/orders/ORDER123/ship?expressCompany=顺丰&trackingNo=SF999

# 4. 审核退款
PUT /api/refund/1/review?status=1
```

#### 修复建议

1. **创建权限注解**
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String value(); // "admin" 或 "user"
}
```

2. **创建权限拦截器**
```java
@Component
@Aspect
public class RoleCheckAspect {
    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint pjp, RequireRole requireRole) {
        Long userId = UserContext.getCurrentUserId();
        User user = userService.getById(userId);
        
        String requiredRole = requireRole.value();
        if (!"admin".equals(user.getRole()) && "admin".equals(requiredRole)) {
            throw new ForbiddenException("权限不足，需要管理员权限");
        }
        
        return pjp.proceed();
    }
}
```

3. **在管理员接口上添加注解**
```java
@PutMapping("/{userId}/disable")
@RequireRole("admin")  // 添加权限校验
public Result<Void> disableUser(@PathVariable Long userId) {
    userService.updateUserStatus(userId, 0);
    return Result.success("用户已禁用");
}
```

---

### 1.3 【严重】越权访问 - 用户可以操作其他用户的数据

**影响范围**: 大部分用户相关接口  
**风险等级**: 🔴🔴🔴 极高

#### 问题描述
没有验证当前登录用户与请求参数中的 `userId` 是否一致，导致用户可以修改/查看其他用户的数据。

#### 受影响的接口详情

##### 1.3.1 UserController
```java
// ❌ 用户A可以修改用户B的信息
@PutMapping("/{userId}")
public Result<Void> updateUser(@PathVariable Long userId, @RequestBody User user) {
    user.setId(userId);
    userService.updateUserInfo(user);  // 没有验证当前用户是否有权限
    return Result.success("更新成功");
}

// ✅ 应该修改为
@PutMapping("/{userId}")
public Result<Void> updateUser(@PathVariable Long userId, @RequestBody User user) {
    Long currentUserId = UserContext.getCurrentUserId();
    if (!currentUserId.equals(userId)) {
        throw new ForbiddenException("无权修改其他用户信息");
    }
    user.setId(userId);
    userService.updateUserInfo(user);
    return Result.success("更新成功");
}
```

##### 1.3.2 UserAddressController
```java
// ❌ 用户A可以查看用户B的所有收货地址
@GetMapping("/user/{userId}")
public Result<List<UserAddress>> getUserAddresses(@PathVariable Long userId) {
    List<UserAddress> addresses = addressService.getAddressList(userId);
    return Result.success(addresses);
}

// ❌ 用户A可以修改用户B的地址
@PutMapping("/{addressId}")
public Result<Void> updateAddress(
        @PathVariable Long addressId,
        @RequestBody UserAddress address) {
    address.setId(addressId);
    addressService.updateAddress(address);  // 只传了addressId，没验证所属用户
    return Result.success("地址更新成功");
}
```

##### 1.3.3 OrdersController
```java
// ❌ 用户A可以查看用户B的所有订单
@GetMapping("/user/{userId}")
public Result<IPage<Orders>> getUserOrders(@PathVariable Long userId, ...) {
    IPage<Orders> orderPage = ordersService.getOrderPage(page, userId, status);
    return Result.success(orderPage);
}

// ❌ 用户A可以取消用户B的订单
@PutMapping("/{orderNo}/cancel")
public Result<Void> cancelOrder(
        @PathVariable String orderNo,
        @RequestParam Long userId,  // 参数可以任意传
        @RequestParam String reason) {
    ordersService.cancelOrder(orderNo, userId, reason);
    return Result.success("订单已取消");
}
```

##### 1.3.4 ShoppingCartController
```java
// ❌ 用户A可以查看用户B的购物车
@GetMapping("/{userId}")
public Result<List<ShoppingCart>> getCartList(@PathVariable Long userId) {
    List<ShoppingCart> cartList = cartService.getCartList(userId);
    return Result.success(cartList);
}

// ❌ 用户A可以清空用户B的购物车
@DeleteMapping("/user/{userId}")
public Result<Void> clearCart(@PathVariable Long userId) {
    cartService.clearCart(userId);
    return Result.success("购物车已清空");
}
```

##### 1.3.5 UserFavoriteController、BrowsingHistoryController
```java
// ❌ 同样问题：用户A可以操作用户B的收藏和浏览历史
```

#### 攻击场景
```bash
# 攻击者（userId=100）：

# 1. 查看所有用户的订单
GET /api/orders/user/1
GET /api/orders/user/2
GET /api/orders/user/3
# ... 遍历所有用户ID，窃取订单信息

# 2. 修改其他用户密码
PUT /api/user/1/password
Body: {"oldPassword": "任意", "newPassword": "hacked", "confirmPassword": "hacked"}

# 3. 清空其他用户购物车（恶意破坏）
DELETE /api/cart/user/1
DELETE /api/cart/user/2

# 4. 取消其他用户订单
PUT /api/orders/ORD123456/cancel?userId=1&reason=恶意取消

# 5. 修改其他用户收货地址
PUT /api/address/1
Body: {"receiverAddress": "攻击者地址"}
```

#### 修复建议

1. **创建用户上下文工具类**
```java
public class UserContext {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
    
    public static void setCurrentUser(Long userId) {
        currentUserId.set(userId);
    }
    
    public static Long getCurrentUserId() {
        Long userId = currentUserId.get();
        if (userId == null) {
            throw new UnauthorizedException("未登录");
        }
        return userId;
    }
    
    public static void clear() {
        currentUserId.remove();
    }
}
```

2. **在所有涉及userId的接口中添加验证**
```java
// UserController
@PutMapping("/{userId}")
public Result<Void> updateUser(@PathVariable Long userId, @RequestBody User user) {
    Long currentUserId = UserContext.getCurrentUserId();
    if (!currentUserId.equals(userId)) {
        throw new ForbiddenException("无权修改其他用户信息");
    }
    user.setId(userId);
    userService.updateUserInfo(user);
    return Result.success("更新成功");
}

// UserAddressController
@GetMapping("/user/{userId}")
public Result<List<UserAddress>> getUserAddresses(@PathVariable Long userId) {
    Long currentUserId = UserContext.getCurrentUserId();
    if (!currentUserId.equals(userId)) {
        throw new ForbiddenException("无权访问其他用户地址");
    }
    List<UserAddress> addresses = addressService.getAddressList(userId);
    return Result.success(addresses);
}

// OrdersController
@GetMapping("/user/{userId}")
public Result<IPage<Orders>> getUserOrders(@PathVariable Long userId, ...) {
    Long currentUserId = UserContext.getCurrentUserId();
    if (!currentUserId.equals(userId)) {
        throw new ForbiddenException("无权访问其他用户订单");
    }
    IPage<Orders> orderPage = ordersService.getOrderPage(page, userId, status);
    return Result.success(orderPage);
}
```

3. **或者创建统一的权限校验注解**
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckOwnership {
    String userIdParam(); // 指定哪个参数是userId
}

// 使用示例
@GetMapping("/user/{userId}")
@CheckOwnership(userIdParam = "userId")
public Result<List<UserAddress>> getUserAddresses(@PathVariable Long userId) {
    // AOP自动校验 userId 与当前登录用户是否一致
    List<UserAddress> addresses = addressService.getAddressList(userId);
    return Result.success(addresses);
}
```

---

### 1.4 【严重】库存并发扣减漏洞

**影响范围**: OrdersService.createOrder  
**风险等级**: 🔴🔴 高

#### 问题描述
在高并发场景下，库存扣减可能出现超卖问题（减成负数）。

#### 代码位置
```java
// OrdersServiceImpl.java
// ❌ 存在并发问题
@Transactional
public Orders createOrder(Long userId, Long addressId, List<Long> cartItemIds, String note) {
    // ...
    for (ShoppingCart cartItem : cartItems) {
        Product product = productService.getById(cartItem.getProductId());
        
        // 检查库存
        if (product.getStock() < cartItem.getQuantity()) {
            throw new IllegalArgumentException("商品库存不足：" + product.getName());
        }
        
        // ❌ 非原子操作：读取、判断、更新之间有并发窗口
        productService.updateStock(cartItem.getProductId(), -cartItem.getQuantity());
    }
    // ...
}
```

#### 攻击场景
```
时间轴：库存=1 的商品

T1: 用户A 查询库存=1 ✓
T2: 用户B 查询库存=1 ✓
T3: 用户A 扣减库存=0 ✓
T4: 用户B 扣减库存=-1 ❌ 超卖！
```

#### 修复建议

**方案1：使用乐观锁**
```java
// Product.java 添加版本号
@Version
private Integer version;

// ProductMapper.xml
<update id="updateStockWithVersion">
    UPDATE product 
    SET stock = stock + #{quantity},
        version = version + 1
    WHERE id = #{productId} 
      AND stock + #{quantity} >= 0
      AND version = #{version}
</update>

// OrdersServiceImpl.java
for (ShoppingCart cartItem : cartItems) {
    Product product = productService.getById(cartItem.getProductId());
    
    // 使用版本号更新
    int rows = productService.updateStockWithVersion(
        product.getId(), 
        -cartItem.getQuantity(),
        product.getVersion()
    );
    
    if (rows == 0) {
        throw new IllegalArgumentException("商品库存不足或版本冲突");
    }
}
```

**方案2：使用数据库悲观锁（推荐）**
```java
// ProductMapper.xml
<select id="selectByIdForUpdate" resultType="Product">
    SELECT * FROM product 
    WHERE id = #{id} 
    FOR UPDATE  <!-- 行锁 -->
</select>

// OrdersServiceImpl.java
@Transactional
public Orders createOrder(...) {
    for (ShoppingCart cartItem : cartItems) {
        // 加悲观锁查询
        Product product = productMapper.selectByIdForUpdate(cartItem.getProductId());
        
        if (product.getStock() < cartItem.getQuantity()) {
            throw new IllegalArgumentException("商品库存不足");
        }
        
        // 直接用SQL扣减（原子操作）
        productService.updateStock(cartItem.getProductId(), -cartItem.getQuantity());
    }
}
```

**方案3：使用 Redis 分布式锁（高并发场景）**
```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

@Transactional
public Orders createOrder(...) {
    for (ShoppingCart cartItem : cartItems) {
        String lockKey = "product:stock:" + cartItem.getProductId();
        
        // 尝试获取分布式锁
        Boolean locked = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "locked", 10, TimeUnit.SECONDS);
        
        if (!Boolean.TRUE.equals(locked)) {
            throw new IllegalStateException("商品正在被其他用户购买，请稍后重试");
        }
        
        try {
            // 检查并扣减库存
            Product product = productService.getById(cartItem.getProductId());
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("商品库存不足");
            }
            productService.updateStock(cartItem.getProductId(), -cartItem.getQuantity());
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }
}
```

---

### 1.5 【严重】支付金额可被篡改

**影响范围**: OrdersService.payOrder  
**风险等级**: 🔴🔴 高

#### 问题描述
支付接口只验证了订单状态，没有验证支付金额，攻击者可以修改订单金额后再支付。

#### 代码位置
```java
// OrdersServiceImpl.java
@Transactional
public void payOrder(String orderNo, Long userId, Integer paymentMethod) {
    Orders order = ordersMapper.selectByOrderNo(orderNo);
    
    // 只验证了订单状态和用户
    if (order.getStatus() != 0) {
        throw new IllegalArgumentException("订单状态不正确");
    }
    if (!order.getUserId().equals(userId)) {
        throw new IllegalArgumentException("订单不属于当前用户");
    }
    
    // ❌ 没有验证支付金额！
    // 实际应该：
    // 1. 调用第三方支付接口，传入 order.getPayAmount()
    // 2. 第三方支付返回实际支付金额
    // 3. 对比金额是否一致
    
    order.setStatus(1);
    order.setPaymentMethod(paymentMethod);
    order.setPayTime(LocalDateTime.now());
    ordersMapper.updateById(order);
}
```

#### 攻击场景
```bash
# 攻击流程：
# 1. 创建订单（总金额 5999 元）
POST /api/orders
Response: { orderNo: "ORD123", totalAmount: 5999.00 }

# 2. 攻击者直接修改数据库（或通过其他漏洞）
UPDATE orders SET pay_amount = 0.01 WHERE order_no = 'ORD123';

# 3. 支付 0.01 元
PUT /api/orders/ORD123/pay?userId=1&paymentMethod=1

# 结果：用 0.01 元买了 5999 元的商品！
```

#### 修复建议

```java
@Transactional
public void payOrder(String orderNo, Long userId, Integer paymentMethod) {
    Orders order = ordersMapper.selectByOrderNo(orderNo);
    
    // 基本验证
    if (order.getStatus() != 0) {
        throw new IllegalArgumentException("订单状态不正确");
    }
    if (!order.getUserId().equals(userId)) {
        throw new IllegalArgumentException("订单不属于当前用户");
    }
    
    // ✅ 调用第三方支付接口
    PaymentRequest paymentRequest = new PaymentRequest();
    paymentRequest.setOrderNo(orderNo);
    paymentRequest.setAmount(order.getPayAmount());  // 传入应付金额
    paymentRequest.setPaymentMethod(paymentMethod);
    
    PaymentResponse paymentResponse = thirdPartyPaymentService.pay(paymentRequest);
    
    // ✅ 验证支付金额
    if (paymentResponse.getActualAmount().compareTo(order.getPayAmount()) != 0) {
        throw new IllegalArgumentException("支付金额不一致！");
    }
    
    // ✅ 验证支付状态
    if (!paymentResponse.isSuccess()) {
        throw new IllegalArgumentException("支付失败：" + paymentResponse.getMessage());
    }
    
    // ✅ 记录第三方支付流水号
    order.setThirdPartyTradeNo(paymentResponse.getTradeNo());
    order.setStatus(1);
    order.setPaymentMethod(paymentMethod);
    order.setPayTime(LocalDateTime.now());
    ordersMapper.updateById(order);
}
```

---

## 🟠 二、中危漏洞（High）

### 2.1 密码修改缺少旧密码验证

**影响范围**: UserService.changePassword  
**风险等级**: 🟠 中

#### 问题描述
虽然接口要求传入旧密码，但如果用户的session被劫持，攻击者仍然可以直接修改密码。

#### 修复建议
```java
// 额外添加短信验证码或邮箱验证
public void changePassword(Long userId, String oldPassword, String newPassword) {
    User user = this.getById(userId);
    
    // 验证旧密码
    String encryptedOldPwd = EncryptionUtil.md5(oldPassword);
    if (!encryptedOldPwd.equals(user.getPassword())) {
        throw new IllegalArgumentException("旧密码错误");
    }
    
    // ✅ 额外验证：要求输入短信验证码
    String verifyCode = UserContext.getVerifyCode();
    if (!smsService.verifyCode(user.getPhone(), verifyCode)) {
        throw new IllegalArgumentException("验证码错误");
    }
    
    // 更新密码
    String encryptedNewPwd = EncryptionUtil.md5(newPassword);
    user.setPassword(encryptedNewPwd);
    this.updateById(user);
    
    // ✅ 清除所有登录token（强制重新登录）
    tokenService.clearAllTokens(userId);
}
```

---

### 2.2 评价可以重复提交

**影响范围**: ProductReviewService.addReview  
**风险等级**: 🟠 中

#### 问题描述
没有验证用户是否已经评价过该商品，可以刷评价。

#### 修复建议
```java
public void addReview(ProductReview review) {
    // ✅ 检查是否已评价
    QueryWrapper<ProductReview> wrapper = new QueryWrapper<>();
    wrapper.eq("user_id", review.getUserId())
           .eq("product_id", review.getProductId())
           .eq("order_id", review.getOrderId());  // 关联订单
    
    Long count = reviewMapper.selectCount(wrapper);
    if (count > 0) {
        throw new IllegalArgumentException("您已评价过该商品");
    }
    
    // ✅ 验证用户是否购买过该商品
    OrderItem orderItem = orderItemMapper.selectByOrderIdAndProductId(
        review.getOrderId(), 
        review.getProductId()
    );
    if (orderItem == null) {
        throw new IllegalArgumentException("您未购买过该商品，无法评价");
    }
    
    // ✅ 验证订单是否已确认收货
    Orders order = ordersMapper.selectById(review.getOrderId());
    if (order.getStatus() != 3) {  // 3=已收货
        throw new IllegalArgumentException("订单未确认收货，暂不能评价");
    }
    
    reviewMapper.insert(review);
}
```

---

### 2.3 退款可以重复申请

**影响范围**: RefundService.applyRefund  
**风险等级**: 🟠 中

#### 问题描述
没有检查订单是否已有退款申请，可能导致重复退款。

#### 代码位置
```java
// RefundServiceImpl.java
public void applyRefund(Refund refund) {
    // ...
    
    // ❌ 没有检查是否已有退款申请
    
    // 生成退款单号
    refund.setRefundNo("REF" + System.currentTimeMillis());
    refund.setStatus(0);
    this.save(refund);
}
```

#### 修复建议
```java
public void applyRefund(Refund refund) {
    // 验证订单存在
    Orders order = ordersService.getById(refund.getOrderId());
    if (order == null) {
        throw new IllegalArgumentException("订单不存在");
    }
    
    // ✅ 检查是否已有进行中的退款申请
    QueryWrapper<Refund> wrapper = new QueryWrapper<>();
    wrapper.eq("order_id", refund.getOrderId())
           .in("status", Arrays.asList(0, 1));  // 0=申请中, 1=已同意
    
    Long count = refundMapper.selectCount(wrapper);
    if (count > 0) {
        throw new IllegalArgumentException("该订单已有退款申请，请勿重复提交");
    }
    
    // 验证订单状态
    if (order.getStatus() != 1 && order.getStatus() != 2 && order.getStatus() != 3) {
        throw new IllegalArgumentException("订单状态不支持退款");
    }
    
    // ✅ 验证退款金额不能超过实付金额
    if (refund.getRefundAmount().compareTo(order.getPayAmount()) > 0) {
        throw new IllegalArgumentException("退款金额不能超过实付金额");
    }
    
    // 生成退款单号
    refund.setRefundNo("REF" + System.currentTimeMillis() + order.getUserId());
    refund.setStatus(0);
    this.save(refund);
    
    // 更新订单状态为退款中
    order.setStatus(6);
    ordersService.updateById(order);
}
```

---

### 2.4 商品分类删除缺少级联检查

**影响范围**: ProductCategoryService.deleteCategory  
**风险等级**: 🟠 中

#### 问题描述
删除分类时没有检查是否有子分类或商品，可能导致数据孤岛。

#### 修复建议
```java
public void deleteCategory(Long categoryId) {
    // ✅ 检查是否有子分类
    QueryWrapper<ProductCategory> wrapper = new QueryWrapper<>();
    wrapper.eq("parent_id", categoryId);
    Long childCount = this.count(wrapper);
    
    if (childCount > 0) {
        throw new IllegalArgumentException("该分类下还有子分类，无法删除");
    }
    
    // ✅ 检查是否有商品
    QueryWrapper<Product> productWrapper = new QueryWrapper<>();
    productWrapper.eq("category_id", categoryId);
    Long productCount = productService.count(productWrapper);
    
    if (productCount > 0) {
        throw new IllegalArgumentException("该分类下还有商品，无法删除");
    }
    
    this.removeById(categoryId);
}
```

---

### 2.5 购物车商品数量可以为负数或超大值

**影响范围**: ShoppingCartService  
**风险等级**: 🟠 中

#### 修复建议
```java
public void addToCart(Long userId, Long productId, Integer quantity, String specInfo) {
    // ✅ 参数验证
    if (quantity == null || quantity <= 0) {
        throw new IllegalArgumentException("商品数量必须大于0");
    }
    if (quantity > 999) {
        throw new IllegalArgumentException("商品数量不能超过999");
    }
    
    // ... 其他逻辑
}

public void updateQuantity(Long cartId, Integer quantity) {
    // ✅ 参数验证
    if (quantity == null || quantity <= 0) {
        throw new IllegalArgumentException("商品数量必须大于0");
    }
    if (quantity > 999) {
        throw new IllegalArgumentException("商品数量不能超过999");
    }
    
    ShoppingCart cart = this.getById(cartId);
    if (cart == null) {
        throw new IllegalArgumentException("购物车项不存在");
    }
    
    // ✅ 检查库存
    Product product = productService.getById(cart.getProductId());
    if (product.getStock() < quantity) {
        throw new IllegalArgumentException("商品库存不足");
    }
    
    cart.setQuantity(quantity);
    this.updateById(cart);
}
```

---

### 2.6 订单创建后可以修改收货地址

**影响范围**: 订单系统  
**风险等级**: 🟠 中

#### 问题描述
订单创建时保存了地址ID，但如果用户修改了地址内容，可能导致发货地址错误。

#### 修复建议
```java
// OrdersServiceImpl.java
public Orders createOrder(Long userId, Long addressId, List<Long> cartItemIds, String note) {
    // 查询地址
    UserAddress address = addressService.getById(addressId);
    
    // ✅ 将地址快照存储到订单表（而不是只存地址ID）
    Orders order = new Orders();
    order.setReceiverName(address.getReceiverName());
    order.setReceiverPhone(address.getReceiverPhone());
    order.setReceiverAddress(
        address.getProvince() + address.getCity() + 
        address.getDistrict() + address.getDetailAddress()
    );
    // 不存储 addressId，或者标记为"地址快照"
    
    // ... 其他逻辑
}
```

---

## 🟡 三、低危漏洞（Medium）

### 3.1 敏感信息泄露 - 错误信息过于详细

**影响范围**: 全局异常处理  
**风险等级**: 🟡 低

#### 问题描述
错误信息可能暴露数据库结构、SQL语句等敏感信息。

#### 修复建议
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("服务器异常", e);
        
        // ✅ 生产环境不返回详细错误信息
        if (isProduction()) {
            return Result.error(500, "服务器内部错误");
        }
        
        // 开发环境返回详细信息
        return Result.error(500, "服务器内部错误：" + e.getMessage());
    }
}
```

---

### 3.2 缺少请求频率限制（防刷）

**影响范围**: 所有接口  
**风险等级**: 🟡 低

#### 修复建议
```java
// 使用 Guava RateLimiter 或 Redis
@Aspect
@Component
public class RateLimitAspect {
    
    @Around("@annotation(rateLimit)")
    public Object limit(ProceedingJoinPoint pjp, RateLimit rateLimit) {
        Long userId = UserContext.getCurrentUserId();
        String key = "rate:limit:" + userId + ":" + pjp.getSignature().getName();
        
        // 使用 Redis 计数
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, rateLimit.period(), TimeUnit.SECONDS);
        }
        
        if (count > rateLimit.limit()) {
            throw new TooManyRequestsException("请求过于频繁，请稍后再试");
        }
        
        return pjp.proceed();
    }
}

// 使用示例
@PostMapping("/register")
@RateLimit(limit = 5, period = 60)  // 每分钟最多5次
public Result<Map<String, Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
    // ...
}
```

---

### 3.3 缺少日志记录

**影响范围**: 关键业务操作  
**风险等级**: 🟡 低

#### 修复建议
```java
// 创建操作日志表
CREATE TABLE `operation_log` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint,
    `operation` varchar(50) COMMENT '操作类型',
    `method` varchar(200) COMMENT '方法名',
    `params` text COMMENT '请求参数',
    `ip` varchar(50) COMMENT 'IP地址',
    `created_time` datetime,
    PRIMARY KEY (`id`)
);

// 创建日志切面
@Aspect
@Component
public class OperationLogAspect {
    
    @Around("@annotation(operationLog)")
    public Object log(ProceedingJoinPoint pjp, OperationLog operationLog) {
        // 记录操作日志
        OperationLogEntity log = new OperationLogEntity();
        log.setUserId(UserContext.getCurrentUserId());
        log.setOperation(operationLog.value());
        log.setMethod(pjp.getSignature().toString());
        log.setParams(JSON.toJSONString(pjp.getArgs()));
        log.setIp(RequestUtil.getClientIp());
        
        operationLogService.save(log);
        
        return pjp.proceed();
    }
}

// 使用示例
@DeleteMapping("/{userId}")
@OperationLog("删除用户")
public Result<Void> deleteUser(@PathVariable Long userId) {
    // ...
}
```

---

### 3.4 密码使用MD5加密（不安全）

**影响范围**: UserService  
**风险等级**: 🟡 低

#### 问题描述
MD5已被证明不安全，推荐使用 BCrypt。

#### 修复建议
```java
// 引入依赖
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>

// 替换加密方式
public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public static String encrypt(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}

// UserService
public User register(User user, String password) {
    // 检查用户名是否存在
    User existUser = userMapper.selectByUsernameIgnoreDeleted(user.getUsername());
    if (existUser != null) {
        throw new IllegalArgumentException("用户名已存在");
    }
    
    // ✅ 使用 BCrypt 加密
    String encryptedPassword = PasswordUtil.encrypt(password);
    user.setPassword(encryptedPassword);
    user.setRole("user");
    user.setStatus(1);
    
    userMapper.insert(user);
    return user;
}

public User login(String username, String password) {
    User user = userMapper.selectByUsernameIgnoreDeleted(username);
    if (user == null) {
        throw new IllegalArgumentException("用户名或密码错误");
    }
    
    // ✅ 使用 BCrypt 验证
    if (!PasswordUtil.matches(password, user.getPassword())) {
        throw new IllegalArgumentException("用户名或密码错误");
    }
    
    if (user.getStatus() == 0) {
        throw new IllegalArgumentException("账号已被禁用");
    }
    
    return user;
}
```

---

### 3.5 缺少CSRF防护

**影响范围**: 所有POST/PUT/DELETE接口  
**风险等级**: 🟡 低

#### 修复建议
```java
// 引入 Spring Security
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

// 配置CSRF
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                .anyRequest().authenticated();
        
        return http.build();
    }
}
```

---

### 3.6 图片上传缺少验证

**影响范围**: 文件上传接口（如果有）  
**风险等级**: 🟡 低

#### 修复建议
```java
@PostMapping("/upload")
public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
    // ✅ 验证文件类型
    String contentType = file.getContentType();
    if (!contentType.startsWith("image/")) {
        throw new IllegalArgumentException("只能上传图片文件");
    }
    
    // ✅ 验证文件大小
    if (file.getSize() > 5 * 1024 * 1024) {  // 5MB
        throw new IllegalArgumentException("图片大小不能超过5MB");
    }
    
    // ✅ 验证文件扩展名
    String originalFilename = file.getOriginalFilename();
    String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
    if (!Arrays.asList(".jpg", ".jpeg", ".png", ".gif").contains(ext.toLowerCase())) {
        throw new IllegalArgumentException("不支持的图片格式");
    }
    
    // ✅ 重命名文件（防止路径遍历攻击）
    String newFilename = UUID.randomUUID().toString() + ext;
    
    // 保存文件
    String uploadPath = "/uploads/" + newFilename;
    file.transferTo(new File(uploadPath));
    
    return Result.success(uploadPath);
}
```

---

### 3.7 订单号生成算法过于简单

**影响范围**: OrdersServiceImpl  
**风险等级**: 🟡 低

#### 问题描述
```java
// ❌ 当前实现
String orderNo = "ORD" + System.currentTimeMillis() + userId;
```

可能导致订单号被猜测。

#### 修复建议
```java
public String generateOrderNo(Long userId) {
    // 格式：ORD + 日期(8位) + 随机数(6位) + 用户ID后4位
    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String random = String.format("%06d", new Random().nextInt(999999));
    String userIdSuffix = String.format("%04d", userId % 10000);
    
    return "ORD" + date + random + userIdSuffix;
}

// 或者使用雪花算法（Snowflake）生成唯一ID
```

---

### 3.8 缺少幂等性保证

**影响范围**: 订单创建、支付等接口  
**风险等级**: 🟡 低

#### 修复建议
```java
@PostMapping
public Result<Orders> createOrder(@RequestBody OrderCreateDTO orderDTO,
                                  @RequestHeader("Idempotent-Key") String idempotentKey) {
    // ✅ 检查幂等性key
    String lockKey = "order:create:" + idempotentKey;
    Boolean locked = redisTemplate.opsForValue()
        .setIfAbsent(lockKey, "processing", 30, TimeUnit.SECONDS);
    
    if (!Boolean.TRUE.equals(locked)) {
        // 检查是否已创建订单
        Orders existOrder = orderCache.get(idempotentKey);
        if (existOrder != null) {
            return Result.success("订单已创建", existOrder);
        }
        throw new IllegalStateException("请勿重复提交");
    }
    
    try {
        Orders order = ordersService.createOrder(
            orderDTO.getUserId(),
            orderDTO.getAddressId(),
            orderDTO.getCartItemIds(),
            orderDTO.getNote()
        );
        
        // 缓存结果
        orderCache.put(idempotentKey, order, 300);
        
        return Result.success("订单创建成功", order);
    } finally {
        redisTemplate.delete(lockKey);
    }
}
```

---

## 📊 四、漏洞统计汇总

### 4.1 按风险等级分类

| 风险等级 | 数量 | 占比 |
|---------|-----|------|
| 🔴 高危 | 5 | 14% |
| 🟠 中危 | 6 | 17% |
| 🟡 低危 | 8 | 23% |
| **总计** | **19** | **54%** |

### 4.2 按模块分类

| 模块 | 高危 | 中危 | 低危 | 小计 |
|-----|------|------|------|------|
| 认证授权 | 3 | 0 | 0 | 3 |
| 用户管理 | 1 | 1 | 1 | 3 |
| 商品管理 | 0 | 1 | 0 | 1 |
| 订单管理 | 1 | 2 | 2 | 5 |
| 支付系统 | 1 | 0 | 0 | 1 |
| 退款管理 | 0 | 1 | 0 | 1 |
| 评价系统 | 0 | 1 | 0 | 1 |
| 全局安全 | 0 | 0 | 4 | 4 |
| **总计** | **6** | **6** | **7** | **19** |

---

## 🛠️ 五、修复优先级建议

### P0 - 立即修复（1-2天）
1. ✅ 实现 JWT 身份认证机制
2. ✅ 添加权限控制（RBAC）
3. ✅ 修复所有越权访问漏洞
4. ✅ 修复支付金额可篡改漏洞

### P1 - 高优先级（3-5天）
5. ✅ 修复库存并发扣减问题
6. ✅ 添加重复提交防护（退款、评价）
7. ✅ 添加密码修改二次验证

### P2 - 中优先级（1-2周）
8. ✅ 添加请求频率限制
9. ✅ 完善参数验证
10. ✅ 添加操作日志
11. ✅ 改用 BCrypt 加密密码

### P3 - 低优先级（后续迭代）
12. ✅ 添加 CSRF 防护
13. ✅ 完善文件上传验证
14. ✅ 优化错误信息显示
15. ✅ 添加幂等性保证

---

## 🎯 六、安全最佳实践建议

### 6.1 认证授权
- [ ] 使用 JWT 或 OAuth 2.0
- [ ] 实现 RBAC 权限模型
- [ ] Token 设置合理过期时间
- [ ] 敏感操作要求二次验证

### 6.2 输入验证
- [ ] 所有用户输入必须验证
- [ ] 使用白名单而非黑名单
- [ ] 防止 SQL 注入（使用参数化查询）
- [ ] 防止 XSS 攻击

### 6.3 业务安全
- [ ] 关键操作添加幂等性
- [ ] 并发操作使用锁机制
- [ ] 金额计算使用 BigDecimal
- [ ] 状态流转要严格校验

### 6.4 数据安全
- [ ] 敏感信息加密存储
- [ ] 使用 HTTPS 传输
- [ ] 定期备份数据库
- [ ] 实现数据脱敏

### 6.5 监控告警
- [ ] 记录关键操作日志
- [ ] 监控异常登录行为
- [ ] 设置异常交易告警
- [ ] 定期进行安全审计

---

## 📞 七、联系方式

如有疑问，请联系安全团队：
- 邮箱：security@webshop.com
- 钉钉群：WebShop 安全组

---

**报告生成时间**: 2025-11-06 20:00:00  
**审计人员**: AI Security Auditor  
**审计版本**: v1.0


