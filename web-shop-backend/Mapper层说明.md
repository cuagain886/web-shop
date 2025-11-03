# Mapper 层说明文档

## 📋 Mapper 接口列表（12个）

| 序号 | Mapper 接口 | 实体类 | 自定义方法数 | 说明 |
|------|------------|--------|--------------|------|
| 1 | `UserMapper` | User | 3 | 用户数据访问 |
| 2 | `UserAddressMapper` | UserAddress | 3 | 用户地址数据访问 |
| 3 | `ProductCategoryMapper` | ProductCategory | 4 | 商品分类数据访问 |
| 4 | `ProductMapper` | Product | 6 | 商品数据访问 |
| 5 | `ShoppingCartMapper` | ShoppingCart | 5 | 购物车数据访问 |
| 6 | `OrdersMapper` | Orders | 7 | 订单数据访问 |
| 7 | `OrderItemMapper` | OrderItem | 4 | 订单项数据访问 |
| 8 | `ProductReviewMapper` | ProductReview | 5 | 商品评价数据访问 |
| 9 | `UserFavoriteMapper` | UserFavorite | 5 | 用户收藏数据访问 |
| 10 | `BrowsingHistoryMapper` | BrowsingHistory | 5 | 浏览历史数据访问 |
| 11 | `OperationLogMapper` | OperationLog | 3 | 操作日志数据访问 |
| 12 | `RefundMapper` | Refund | 4 | 退款数据访问 |

**总计**: 12 个 Mapper 接口，54 个自定义方法

---

## 🎯 MyBatis-Plus BaseMapper 提供的基础方法

每个 Mapper 接口都继承了 `BaseMapper<T>`，自动获得以下基础 CRUD 方法：

### 插入（Insert）
```java
int insert(T entity);  // 插入一条记录
```

### 删除（Delete）
```java
int deleteById(Serializable id);  // 根据 ID 删除
int deleteByMap(Map<String, Object> columnMap);  // 根据 Map 条件删除
int delete(Wrapper<T> wrapper);  // 根据 Wrapper 条件删除
int deleteBatchIds(Collection<? extends Serializable> idList);  // 批量删除
```

### 更新（Update）
```java
int updateById(T entity);  // 根据 ID 更新
int update(T entity, Wrapper<T> wrapper);  // 根据 Wrapper 条件更新
```

### 查询（Select）
```java
T selectById(Serializable id);  // 根据 ID 查询
T selectOne(Wrapper<T> wrapper);  // 查询单条记录
List<T> selectList(Wrapper<T> wrapper);  // 查询列表
List<T> selectBatchIds(Collection<? extends Serializable> idList);  // 批量查询
List<T> selectByMap(Map<String, Object> columnMap);  // 根据 Map 查询
Long selectCount(Wrapper<T> wrapper);  // 查询总数
IPage<T> selectPage(IPage<T> page, Wrapper<T> wrapper);  // 分页查询
```

---

## 📝 自定义方法说明

### 1. UserMapper

#### selectByUsernameIgnoreDeleted
```java
User selectByUsernameIgnoreDeleted(@Param("username") String username);
```
**用途**: 查询用户名（包括已删除的用户）  
**场景**: 用户登录时验证用户名是否存在  
**SQL**: 需要在 XML 中编写，不使用 `@TableLogic`

#### selectByPhoneIgnoreDeleted
```java
User selectByPhoneIgnoreDeleted(@Param("phone") String phone);
```
**用途**: 查询手机号（包括已删除的用户）  
**场景**: 用户注册时验证手机号是否已被使用

#### updateLastLoginTime
```java
void updateLastLoginTime(@Param("userId") Long userId);
```
**用途**: 更新最后登录时间  
**场景**: 用户登录成功后记录登录时间

---

### 2. UserAddressMapper

#### selectByUserId
```java
List<UserAddress> selectByUserId(@Param("userId") Long userId);
```
**用途**: 查询用户的所有地址  
**返回**: 按 `isDefault` 和 `createdTime` 排序

#### selectDefaultByUserId
```java
UserAddress selectDefaultByUserId(@Param("userId") Long userId);
```
**用途**: 查询用户的默认地址

#### cancelDefaultByUserId
```java
void cancelDefaultByUserId(@Param("userId") Long userId);
```
**用途**: 取消用户的所有默认地址  
**场景**: 设置新的默认地址前，先取消旧的默认地址

---

### 3. ProductCategoryMapper

#### selectRootCategories
```java
List<ProductCategory> selectRootCategories();
```
**用途**: 查询所有一级分类（`parentId = 0`）

#### selectByParentId
```java
List<ProductCategory> selectByParentId(@Param("parentId") Long parentId);
```
**用途**: 查询指定父分类下的子分类

#### countProductsByCategoryId
```java
Long countProductsByCategoryId(@Param("categoryId") Long categoryId);
```
**用途**: 统计分类下的商品数量  
**场景**: 删除分类前检查是否有关联商品

#### countChildrenByCategoryId
```java
Long countChildrenByCategoryId(@Param("categoryId") Long categoryId);
```
**用途**: 统计分类下的子分类数量  
**场景**: 删除分类前检查是否有子分类

---

### 4. ProductMapper

#### selectPageWithCategory
```java
IPage<Product> selectPageWithCategory(Page<Product> page,
                                      @Param("categoryId") Long categoryId,
                                      @Param("keyword") String keyword,
                                      @Param("status") Integer status);
```
**用途**: 分页查询商品（带分类名称）  
**参数**: 支持按分类、关键词、状态筛选

#### selectHotProducts
```java
List<Product> selectHotProducts(@Param("limit") Integer limit);
```
**用途**: 查询热销商品（`isHot = 1`）  
**排序**: 按销量降序

#### selectRecommendProducts
```java
List<Product> selectRecommendProducts(@Param("limit") Integer limit);
```
**用途**: 查询推荐商品（`isRecommend = 1`）  
**排序**: 按创建时间降序

#### updateSales
```java
void updateSales(@Param("productId") Long productId, @Param("quantity") Integer quantity);
```
**用途**: 更新商品销量  
**场景**: 订单完成后增加销量

#### updateStock
```java
void updateStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
```
**用途**: 更新商品库存  
**参数**: `quantity` 为正数增加库存，负数减少库存

#### batchUpdateStatus
```java
void batchUpdateStatus(@Param("productIds") List<Long> productIds, @Param("status") Integer status);
```
**用途**: 批量更新商品状态  
**场景**: 批量上架/下架商品

---

### 5. ShoppingCartMapper

#### selectCartWithProduct
```java
List<ShoppingCart> selectCartWithProduct(@Param("userId") Long userId);
```
**用途**: 查询用户购物车（带商品信息）  
**关联**: LEFT JOIN product 表

#### selectCheckedByUserId
```java
List<ShoppingCart> selectCheckedByUserId(@Param("userId") Long userId);
```
**用途**: 查询用户已选中的购物车项  
**场景**: 结算时获取已选中的商品

#### updateCheckedByUserId
```java
void updateCheckedByUserId(@Param("userId") Long userId, @Param("checked") Integer checked);
```
**用途**: 更新购物车项选中状态  
**场景**: 全选/取消全选

#### deleteByUserId
```java
void deleteByUserId(@Param("userId") Long userId);
```
**用途**: 清空用户购物车

#### deleteCheckedByUserId
```java
void deleteCheckedByUserId(@Param("userId") Long userId);
```
**用途**: 删除用户已选中的购物车项  
**场景**: 结算成功后清除已购买的商品

---

### 6. OrdersMapper

#### selectPageWithItems
```java
IPage<Orders> selectPageWithItems(Page<Orders> page,
                                  @Param("userId") Long userId,
                                  @Param("status") Integer status);
```
**用途**: 分页查询用户订单（带订单项）  
**关联**: LEFT JOIN order_item 表

#### selectAdminPage
```java
IPage<Orders> selectAdminPage(Page<Orders> page,
                              @Param("status") Integer status,
                              @Param("keyword") String keyword,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);
```
**用途**: 分页查询所有订单（管理端）  
**支持**: 状态、关键词、时间范围筛选

#### selectByOrderNoWithItems
```java
Orders selectByOrderNoWithItems(@Param("orderNo") String orderNo);
```
**用途**: 根据订单号查询订单（带订单项）

#### countByUserIdAndStatus
```java
Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
```
**用途**: 统计用户各状态订单数量  
**场景**: 个人中心显示待支付/待收货等数量

#### sumAmountByDateRange
```java
Double sumAmountByDateRange(@Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime);
```
**用途**: 统计时间范围内的销售额  
**场景**: 管理端数据统计

#### countByStatus
```java
List<Orders> countByStatus();
```
**用途**: 统计各状态订单数量  
**场景**: 管理端订单状态分布图

---

### 7. OrderItemMapper

#### selectByOrderId
```java
List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
```
**用途**: 查询订单的所有订单项

#### selectUnreviewedByUserId
```java
List<OrderItem> selectUnreviewedByUserId(@Param("userId") Long userId);
```
**用途**: 查询用户待评价的订单项  
**条件**: `isReviewed = 0` 且订单状态为已完成

#### selectReviewedByUserId
```java
List<OrderItem> selectReviewedByUserId(@Param("userId") Long userId);
```
**用途**: 查询用户已评价的订单项  
**条件**: `isReviewed = 1`

#### selectProductSalesRanking
```java
List<OrderItem> selectProductSalesRanking(@Param("limit") Integer limit);
```
**用途**: 统计商品销售数据  
**场景**: 管理端商品销售排行榜  
**分组**: 按 `productId` 分组统计销量和销售额

---

### 8. ProductReviewMapper

#### selectPageWithUser
```java
IPage<ProductReview> selectPageWithUser(Page<ProductReview> page,
                                        @Param("productId") Long productId,
                                        @Param("rating") Integer rating);
```
**用途**: 分页查询商品评价（带用户信息）  
**关联**: LEFT JOIN user 表

#### selectByUserId
```java
List<ProductReview> selectByUserId(@Param("userId") Long userId);
```
**用途**: 查询用户的评价列表

#### selectAvgRatingByProductId
```java
Double selectAvgRatingByProductId(@Param("productId") Long productId);
```
**用途**: 统计商品的平均评分

#### countByProductId
```java
Long countByProductId(@Param("productId") Long productId);
```
**用途**: 统计商品的评价数量

#### countByProductIdGroupByRating
```java
List<ProductReview> countByProductIdGroupByRating(@Param("productId") Long productId);
```
**用途**: 统计商品各星级评价数量  
**返回**: 1-5星的数量分布

---

### 9. UserFavoriteMapper

#### selectByUserIdWithProduct
```java
List<UserFavorite> selectByUserIdWithProduct(@Param("userId") Long userId);
```
**用途**: 查询用户收藏列表（带商品信息）  
**关联**: LEFT JOIN product 表

#### selectByUserIdAndProductId
```java
UserFavorite selectByUserIdAndProductId(@Param("userId") Long userId,
                                       @Param("productId") Long productId);
```
**用途**: 检查用户是否收藏了某商品

#### deleteByUserIdAndProductId
```java
void deleteByUserIdAndProductId(@Param("userId") Long userId,
                                @Param("productId") Long productId);
```
**用途**: 取消收藏

#### deleteByUserId
```java
void deleteByUserId(@Param("userId") Long userId);
```
**用途**: 清空用户收藏

#### countByProductId
```java
Long countByProductId(@Param("productId") Long productId);
```
**用途**: 统计商品被收藏次数

---

### 10. BrowsingHistoryMapper

#### selectByUserIdWithProduct
```java
List<BrowsingHistory> selectByUserIdWithProduct(@Param("userId") Long userId,
                                                @Param("limit") Integer limit);
```
**用途**: 查询用户浏览历史（带商品信息）  
**排序**: 按浏览时间降序

#### selectByUserIdAndProductId
```java
BrowsingHistory selectByUserIdAndProductId(@Param("userId") Long userId,
                                          @Param("productId") Long productId);
```
**用途**: 查询用户是否已浏览某商品

#### deleteByUserIdAndProductId
```java
void deleteByUserIdAndProductId(@Param("userId") Long userId,
                                @Param("productId") Long productId);
```
**用途**: 删除指定商品的浏览记录

#### deleteByUserId
```java
void deleteByUserId(@Param("userId") Long userId);
```
**用途**: 清空用户浏览历史

#### deleteOldRecords
```java
void deleteOldRecords(@Param("userId") Long userId, @Param("days") Integer days);
```
**用途**: 删除超过指定天数的浏览记录  
**场景**: 定时清理旧数据

---

### 11. OperationLogMapper

#### selectPage
```java
IPage<OperationLog> selectPage(Page<OperationLog> page,
                               @Param("operatorId") Long operatorId,
                               @Param("operationType") String operationType,
                               @Param("keyword") String keyword,
                               @Param("startTime") LocalDateTime startTime,
                               @Param("endTime") LocalDateTime endTime);
```
**用途**: 分页查询操作日志  
**支持**: 操作人、类型、关键词、时间范围筛选

#### deleteBeforeTime
```java
void deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);
```
**用途**: 删除指定时间之前的日志  
**场景**: 定期清理日志

#### deleteAll
```java
void deleteAll();
```
**用途**: 清空所有日志

---

### 12. RefundMapper

#### selectPageWithOrder
```java
IPage<Refund> selectPageWithOrder(Page<Refund> page,
                                  @Param("status") Integer status,
                                  @Param("keyword") String keyword);
```
**用途**: 分页查询退款申请（带订单信息）  
**关联**: LEFT JOIN orders 表

#### selectByOrderId
```java
Refund selectByOrderId(@Param("orderId") Long orderId);
```
**用途**: 根据订单ID查询退款记录

#### selectByRefundNo
```java
Refund selectByRefundNo(@Param("refundNo") String refundNo);
```
**用途**: 根据退款单号查询退款记录

#### countByStatus
```java
Long countByStatus(@Param("status") Integer status);
```
**用途**: 统计各状态退款数量

---

## 📁 XML 映射文件

自定义方法需要在 XML 文件中编写 SQL。XML 文件位置：
```
src/main/resources/mapper/
├── UserMapper.xml
├── UserAddressMapper.xml
├── ProductCategoryMapper.xml
├── ProductMapper.xml
├── ShoppingCartMapper.xml
├── OrdersMapper.xml
├── OrderItemMapper.xml
├── ProductReviewMapper.xml
├── UserFavoriteMapper.xml
├── BrowsingHistoryMapper.xml
├── OperationLogMapper.xml
└── RefundMapper.xml
```

---

## 🔧 使用示例

### 1. 基础 CRUD（BaseMapper 提供）

```java
@Service
public class UserServiceImpl {
    
    @Autowired
    private UserMapper userMapper;
    
    // 插入
    public void createUser(User user) {
        userMapper.insert(user);
    }
    
    // 查询
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    // 更新
    public void updateUser(User user) {
        userMapper.updateById(user);
    }
    
    // 删除（逻辑删除）
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
    
    // 条件查询
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
    
    // 分页查询
    public IPage<User> getUserPage(int current, int size) {
        Page<User> page = new Page<>(current, size);
        return userMapper.selectPage(page, null);
    }
}
```

### 2. 自定义方法

```java
@Service
public class UserServiceImpl {
    
    @Autowired
    private UserMapper userMapper;
    
    // 使用自定义方法
    public User login(String username, String password) {
        // 查询用户（包括已删除的）
        User user = userMapper.selectByUsernameIgnoreDeleted(username);
        
        if (user != null && user.getDeleted() == 1) {
            throw new RuntimeException("账号已被删除");
        }
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // 更新最后登录时间
            userMapper.updateLastLoginTime(user.getId());
            return user;
        }
        
        return null;
    }
}
```

### 3. 复杂查询（Lambda）

```java
@Service
public class ProductServiceImpl {
    
    @Autowired
    private ProductMapper productMapper;
    
    public List<Product> searchProducts(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)  // 上架状态
               .eq(categoryId != null, Product::getCategoryId, categoryId)
               .ge(minPrice != null, Product::getPrice, minPrice)
               .le(maxPrice != null, Product::getPrice, maxPrice)
               .orderByDesc(Product::getSales);
        return productMapper.selectList(wrapper);
    }
}
```

---

## ⚠️ 注意事项

### 1. 逻辑删除自动处理
```java
// ✅ 正确 - 自动过滤已删除数据
List<User> users = userMapper.selectList(null);
// 实际 SQL: SELECT * FROM user WHERE deleted = 0

// ✅ 正确 - 逻辑删除
userMapper.deleteById(1L);
// 实际 SQL: UPDATE user SET deleted = 1 WHERE id = 1

// ⚠️ 特殊情况 - 需要查询包括已删除的数据
User user = userMapper.selectByUsernameIgnoreDeleted(username);
// 在 XML 中不添加 deleted 条件
```

### 2. 订单 Mapper 名称
```java
// ✅ 正确
@Autowired
private OrdersMapper ordersMapper;

// ❌ 错误
@Autowired
private OrderMapper orderMapper;  // 类名是 OrdersMapper
```

### 3. 分页查询
```java
// ✅ 正确 - 使用 Page 对象
Page<Product> page = new Page<>(1, 10);  // 第1页，每页10条
IPage<Product> result = productMapper.selectPage(page, wrapper);

// 获取结果
List<Product> records = result.getRecords();  // 数据列表
long total = result.getTotal();  // 总记录数
long pages = result.getPages();  // 总页数
```

### 4. 批量操作
```java
// ✅ 正确 - 批量删除
List<Long> ids = Arrays.asList(1L, 2L, 3L);
userMapper.deleteBatchIds(ids);

// ✅ 正确 - 批量查询
List<User> users = userMapper.selectBatchIds(ids);
```

---

## 📊 Mapper 层开发进度

| 阶段 | 内容 | 进度 | 状态 |
|------|------|------|------|
| **1. Mapper 接口** | 12个接口 | 100% | ✅ 完成 |
| **2. XML 映射文件** | 54个自定义方法 | 0% | ⏳ 待开发 |
| **3. 单元测试** | Mapper 测试 | 0% | ⏳ 待开发 |

---

## 🎯 下一步

Mapper 接口已创建完成，接下来需要：

1. **创建 XML 映射文件** - 为54个自定义方法编写 SQL
2. **创建 Service 层** - 实现业务逻辑
3. **编写单元测试** - 测试 Mapper 方法

---

**Mapper 层接口创建完成！** ✨  
**自定义方法总数**: 54个  
**待编写 XML**: 54个方法  

下一步可以开始创建 Service 层或编写 XML 映射文件！

