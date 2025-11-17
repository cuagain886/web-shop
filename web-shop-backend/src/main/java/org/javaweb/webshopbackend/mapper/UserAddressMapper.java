package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.UserAddress;

import java.util.List;

/**
 * 用户地址 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {

    /**
     * 查询用户的所有地址
     * 
     * @param userId 用户ID
     * @return 地址列表
     */
    List<UserAddress> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的默认地址
     * 
     * @param userId 用户ID
     * @return 默认地址
     */
    UserAddress selectDefaultByUserId(@Param("userId") Long userId);

    /**
     * 取消用户的所有默认地址（用于设置新的默认地址前）
     * 
     * @param userId 用户ID
     */
    void cancelDefaultByUserId(@Param("userId") Long userId);
}

