package org.javaweb.webshopbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.dto.ChangePasswordDTO;
import org.javaweb.webshopbackend.pojo.dto.LoginDTO;
import org.javaweb.webshopbackend.pojo.dto.RegisterDTO;
import org.javaweb.webshopbackend.pojo.entity.User;
import org.javaweb.webshopbackend.service.UserService;
import org.javaweb.webshopbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户Controller
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("用户注册：username={}", registerDTO.getUsername());

        // 将DTO转换为User实体
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPhone(registerDTO.getPhone());
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setAvatar(registerDTO.getAvatar());
        user.setGender(registerDTO.getGender());
        user.setBirthday(registerDTO.getBirthday());

        // 调用Service注册（传入User对象和密码）
        User registeredUser = userService.register(user, registerDTO.getPassword());

        // 返回用户信息（不含密码）
        Map<String, Object> data = new HashMap<>();
        data.put("userId", registeredUser.getId());
        data.put("username", registeredUser.getUsername());
        data.put("nickname", registeredUser.getNickname());
        data.put("email", registeredUser.getEmail());
        data.put("avatar", registeredUser.getAvatar());
        data.put("role", registeredUser.getRole());

        return Result.success("注册成功", data);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，支持用户名/邮箱/手机号登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录：username={}", loginDTO.getUsername());

        // 调用Service登录
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

        // 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 返回用户信息和token
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", user);

        return Result.success("登录成功", data);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info/{userId}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    public Result<User> getUserInfo(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("获取用户信息：userId={}", userId);

        User user = userService.getById(userId);
        // 清空密码
        user.setPassword(null);

        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    @Operation(summary = "更新用户信息", description = "更新用户基本信息")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @RequestBody User user) {
        log.info("更新用户信息：userId={}", userId);

        user.setId(userId);
        userService.updateUserInfo(user);

        return Result.success("更新成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/{userId}/password")
    @Operation(summary = "修改密码", description = "用户修改密码")
    public Result<Void> updatePassword(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        log.info("修改密码：userId={}", userId);

        // 验证新密码和确认密码是否一致
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的新密码不一致");
        }

        userService.changePassword(userId, changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());

        return Result.success("密码修改成功");
    }

    /**
     * 分页查询用户列表（管理员功能）
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询用户列表", description = "管理员查询用户列表，支持搜索和筛选")
    public Result<IPage<User>> getUserList(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "搜索关键词（用户名/邮箱/手机号）", example = "user")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "用户角色（0-普通用户，1-管理员）", example = "0")
            @RequestParam(required = false) Integer role,
            @Parameter(description = "用户状态（0-禁用，1-正常）", example = "1")
            @RequestParam(required = false) Integer status) {
        log.info("分页查询用户列表：pageNum={}, pageSize={}, keyword={}", pageNum, pageSize, keyword);

        Page<User> page = new Page<>(pageNum, pageSize);
        
        // 使用MyBatis-Plus的QueryWrapper进行条件查询
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("username", keyword)
                   .or().like("phone", keyword);
        }
        if (role != null) {
            wrapper.eq("role", role);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        
        IPage<User> userPage = userService.page(page, wrapper);

        // 清空所有密码
        userPage.getRecords().forEach(user -> user.setPassword(null));

        return Result.success(userPage);
    }

    /**
     * 禁用用户
     */
    @PutMapping("/{userId}/disable")
    @Operation(summary = "禁用用户", description = "管理员禁用用户账号")
    public Result<Void> disableUser(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("禁用用户：userId={}", userId);

        userService.updateUserStatus(userId, 0);

        return Result.success("用户已禁用");
    }

    /**
     * 启用用户
     */
    @PutMapping("/{userId}/enable")
    @Operation(summary = "启用用户", description = "管理员启用用户账号")
    public Result<Void> enableUser(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("启用用户：userId={}", userId);

        userService.updateUserStatus(userId, 1);

        return Result.success("用户已启用");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "管理员删除用户（逻辑删除）")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("删除用户：userId={}", userId);

        userService.removeById(userId);

        return Result.success("用户已删除");
    }

    /**
     * 注销账户
     */
    @DeleteMapping("/{userId}/account")
    @Operation(summary = "注销账户", description = "用户注销自己的账户（物理删除）")
    public Result<Void> deleteAccount(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("注销账户：userId={}", userId);

        userService.deleteAccount(userId);

        return Result.success("账户已注销");
    }
}

