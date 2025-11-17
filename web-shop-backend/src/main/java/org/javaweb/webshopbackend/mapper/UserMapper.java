package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.User;

/**
 * 用户 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户（包括已删除的用户）
     * 用于登录时验证用户名是否存在
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsernameIgnoreDeleted(@Param("username") String username);

    /**
     * 根据手机号查询用户（包括已删除的用户）
     * 用于注册时验证手机号是否已被使用
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhoneIgnoreDeleted(@Param("phone") String phone);

    /**
     * 更新最后登录时间
     * 
     * @param userId 用户ID
     */
    void updateLastLoginTime(@Param("userId") Long userId);
}

