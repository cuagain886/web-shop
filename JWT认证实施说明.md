# JWT 认证实施说明

## 实施概述

本项目已成功引入 JWT（JSON Web Token）认证机制，替换了原有的 Mock Token 方式，大幅提升了系统的安全性。

## 实施内容

### 后端改动

#### 1. 添加 JWT 依赖

在 [`pom.xml`](web-shop-backend/pom.xml) 中添加了 jjwt 依赖：
```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

#### 2. JWT 配置

在 [`application.properties`](web-shop-backend/src/main/resources/application.properties) 中添加：
```properties
# JWT 配置
jwt.secret=webshop-jwt-secret-key-change-this-in-production-environment-2024
jwt.expiration=86400000
jwt.refresh-expiration=604800000
```

**注意**：生产环境必须更换为强随机密钥！

#### 3. JWT 工具类

创建了 [`JwtUtil.java`](web-shop-backend/src/main/java/org/javaweb/webshopbackend/util/JwtUtil.java)，提供以下功能：
- `generateToken()` - 生成 JWT Token
- `validateToken()` - 验证 Token 有效性
- `getClaimsFromToken()` - 解析 Token 获取用户信息
- `getUserIdFromToken()` - 获取用户 ID
- `getUsernameFromToken()` - 获取用户名
- `getRoleFromToken()` - 获取用户角色

#### 4. JWT 拦截器

创建了 [`JwtInterceptor.java`](web-shop-backend/src/main/java/org/javaweb/webshopbackend/interceptor/JwtInterceptor.java)：
- 自动验证请求头中的 JWT Token
- 将用户信息注入到请求属性中
- 对未授权请求返回 401 状态码

#### 5. 拦截器配置

在 [`WebMvcConfig.java`](web-shop-backend/src/main/java/org/javaweb/webshopbackend/config/WebMvcConfig.java) 中配置拦截器：
- 拦截所有 `/api/**` 路径
- 放行登录、注册、商品浏览等公开接口
- 放行 API 文档相关路径

#### 6. 登录接口改造

修改了 [`UserController.java`](web-shop-backend/src/main/java/org/javaweb/webshopbackend/controller/UserController.java) 的登录方法：
- 使用 `JwtUtil` 生成真实的 JWT Token
- 返回格式调整为 `{ token, userInfo }`

### 前端改动

#### 1. 请求工具类

创建了 [`request.js`](web-shop-frontend/src/utils/request.js)：
- 自动在请求头添加 `Authorization: Bearer {token}`
- 统一处理 401 未授权错误
- 自动跳转到登录页

#### 2. API 接口更新

更新了所有 API 文件，添加 `/api` 前缀：
- [`user.js`](web-shop-frontend/src/api/user.js) - 用户相关接口
- [`product.js`](web-shop-frontend/src/api/product.js) - 商品相关接口
- [`cart.js`](web-shop-frontend/src/api/cart.js) - 购物车相关接口
- [`order.js`](web-shop-frontend/src/api/order.js) - 订单相关接口
- [`category.js`](web-shop-frontend/src/api/category.js) - 分类相关接口

#### 3. 登录响应适配

修改了登录 API 的响应处理，适配新的后端返回格式。

## 使用说明

### 后端使用

#### 在 Controller 中获取当前用户信息

```java
@GetMapping("/profile")
public Result<User> getProfile(HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    String username = (String) request.getAttribute("username");
    String role = (String) request.getAttribute("role");
    
    // 使用用户信息处理业务逻辑
    User user = userService.getById(userId);
    return Result.success(user);
}
```

#### 放行特定接口

如需放行某个接口，在 [`WebMvcConfig.java`](web-shop-backend/src/main/java/org/javaweb/webshopbackend/config/WebMvcConfig.java) 的 `excludePathPatterns` 中添加：

```java
.excludePathPatterns(
    "/api/user/login",
    "/api/user/register",
    "/api/your-new-public-endpoint"  // 添加新的公开接口
);
```

### 前端使用

#### 登录流程

```javascript
import { login } from '@/api/user'
import { setToken, setUserInfo } from '@/utils/auth'

// 登录
const data = await login({ username, password })

// 保存 token 和用户信息
setToken(data.token)
setUserInfo(data.userInfo)
```

#### 发起请求

所有通过 `request.js` 发起的请求都会自动携带 Token：

```javascript
import request from '@/utils/request'

// 自动添加 Authorization 头
const data = await request({
  url: '/api/user/profile',
  method: 'get'
})
```

## 安全增强建议

### 已实现
✅ JWT Token 认证机制  
✅ 请求拦截器验证  
✅ 自动 Token 过期处理  
✅ 统一错误处理  

### 待实现（可选）
⏳ Token 刷新机制（双 Token）  
⏳ 密码加密升级为 BCrypt  
⏳ 接口限流  
⏳ 请求签名验证  
⏳ IP 黑名单  
⏳ 登录失败锁定  

详见 [`项目问题分析与改进建议.md`](项目问题分析与改进建议.md)

## 测试建议

### 1. 登录测试
- 测试正常登录流程
- 验证 Token 是否正确生成
- 检查用户信息是否正确返回

### 2. 权限测试
- 未登录访问受保护接口应返回 401
- 登录后访问受保护接口应正常
- Token 过期后应自动跳转登录

### 3. 接口测试
- 测试所有 API 接口是否正常工作
- 验证请求头是否正确携带 Token
- 检查后端是否正确解析 Token

## 注意事项

1. **生产环境配置**
   - 必须更换 `jwt.secret` 为强随机密钥（至少 256 位）
   - 建议使用环境变量或配置中心管理密钥
   - 调整 Token 过期时间（当前为 24 小时）

2. **CORS 配置**
   - 生产环境需要限制允许的域名
   - 修改 [`CorsConfig.java`](web-shop-backend/src/main/java/org/javaweb/webshopbackend/config/CorsConfig.java)

3. **密码安全**
   - 当前使用 MD5 加密，建议升级为 BCrypt
   - 参考问题分析文档中的实施方案

4. **Token 存储**
   - 前端使用 localStorage 存储
   - 注意 XSS 攻击防护

## 故障排查

### Token 验证失败
- 检查 Token 格式是否为 `Bearer {token}`
- 验证 Token 是否过期
- 确认密钥配置是否正确

### 401 错误
- 检查请求是否携带 Authorization 头
- 验证 Token 是否有效
- 确认接口是否在拦截器白名单中

### 跨域问题
- 检查 CORS 配置
- 确认前端请求地址是否正确
- 验证 OPTIONS 请求是否被正确处理

## 总结

JWT 认证机制已成功集成到项目中，显著提升了系统安全性。后续可根据实际需求，参考问题分析文档继续完善其他安全特性。