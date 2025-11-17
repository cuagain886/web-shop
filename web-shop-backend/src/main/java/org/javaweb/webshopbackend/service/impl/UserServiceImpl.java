package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.UserMapper;
import org.javaweb.webshopbackend.pojo.entity.User;
import org.javaweb.webshopbackend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 用户 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 密码加密（使用MD5，实际项目建议使用BCrypt）
     */
    private String encryptPassword(String password) {
        // 简单示例，实际项目应使用 BCryptPasswordEncoder
        return DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(User user, String password) {
        log.info("用户注册：username={}, phone={}", user.getUsername(), user.getPhone());

        // 检查用户名是否已存在
        if (checkUsernameExists(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (checkPhoneExists(user.getPhone())) {
            throw new IllegalArgumentException("手机号已被使用");
        }

        // 加密密码
        user.setPassword(encryptPassword(password));

        // 设置默认值（仅当未提供时）
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername());  // 默认昵称为用户名
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");  // 默认角色为普通用户
        }
        if (user.getStatus() == null) {
            user.setStatus(1);  // 默认状态为启用
        }

        // 保存用户
        this.save(user);

        log.info("用户注册成功：userId={}", user.getId());

        // 返回用户信息（不含密码）
        user.setPassword(null);
        return user;
    }

    @Override
    public User login(String username, String password) {
        log.info("用户登录：username={}", username);

        // 查询用户（包括已删除的用户）
        User user = baseMapper.selectByUsernameIgnoreDeleted(username);

        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 检查账号是否已被删除
        if (user.getDeleted() == 1) {
            throw new IllegalArgumentException("账号已被删除");
        }

        // 检查账号是否被禁用
        if (user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已被禁用");
        }

        // 验证密码
        if (!user.getPassword().equals(encryptPassword(password))) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 更新最后登录时间
        baseMapper.updateLastLoginTime(user.getId());

        log.info("用户登录成功：userId={}", user.getId());

        // 返回用户信息（不含密码）
        user.setPassword(null);
        return user;
    }

    @Override
    public User merchantLogin(String username, String password) {
        log.info("商家登录：username={}", username);

        // 先进行普通登录验证
        User user = login(username, password);

        // 验证是否为商家角色
        if (!"merchant".equals(user.getRole())) {
            throw new IllegalArgumentException("该账号不是商家账号");
        }

        log.info("商家登录成功：userId={}", user.getId());
        return user;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        User user = baseMapper.selectByUsernameIgnoreDeleted(username);
        return user != null && user.getDeleted() == 0;
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        User user = baseMapper.selectByPhoneIgnoreDeleted(phone);
        return user != null && user.getDeleted() == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码：userId={}", userId);

        // 查询用户
        User user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证旧密码
        if (!user.getPassword().equals(encryptPassword(oldPassword))) {
            throw new IllegalArgumentException("原密码错误");
        }

        // 更新密码
        user.setPassword(encryptPassword(newPassword));
        this.updateById(user);

        log.info("密码修改成功：userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(User user) {
        log.info("更新用户信息：userId={}", user.getId());

        // 清空敏感字段，防止修改
        user.setPassword(null);
        user.setRole(null);
        user.setDeleted(null);

        this.updateById(user);

        log.info("用户信息更新成功：userId={}", user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, Integer status) {
        log.info("更新用户状态：userId={}, status={}", userId, status);

        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        this.updateById(user);

        log.info("用户状态更新成功：userId={}", userId);
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);

        if (user != null) {
            // 不返回密码
            user.setPassword(null);
        }

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(Long userId) {
        log.info("注销账户：userId={}", userId);

        User user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 物理删除用户
        this.removeById(userId);

        log.info("账户注销成功：userId={}", userId);
    }
}

