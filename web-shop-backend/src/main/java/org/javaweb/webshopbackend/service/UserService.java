package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.User;

/**
 * 用户 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * 
     * @param user 用户信息（包含用户名、手机号、昵称等）
     * @param password 密码（明文，需要加密）
     * @return 注册成功的用户（不含密码）
     */
    User register(User user, String password);

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码（明文）
     * @return 登录成功的用户（不含密码）
     */
    User login(String username, String password);

    /**
     * 商家登录
     * 
     * @param username 用户名
     * @param password 密码（明文）
     * @return 登录成功的商家（不含密码）
     */
    User merchantLogin(String username, String password);

    /**
     * 检查用户名是否已存在
     * 
     * @param username 用户名
     * @return true-已存在，false-不存在
     */
    boolean checkUsernameExists(String username);

    /**
     * 检查手机号是否已存在
     * 
     * @param phone 手机号
     * @return true-已存在，false-不存在
     */
    boolean checkPhoneExists(String phone);

    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码（明文）
     * @param newPassword 新密码（明文）
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     */
    void updateUserInfo(User user);

    /**
     * 禁用/启用用户
     * 
     * @param userId 用户ID
     * @param status 状态（0-禁用，1-启用）
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 根据用户名获取用户（不含密码）
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 注销账户（物理删除）
     *
     * @param userId 用户ID
     */
    void deleteAccount(Long userId);
}

