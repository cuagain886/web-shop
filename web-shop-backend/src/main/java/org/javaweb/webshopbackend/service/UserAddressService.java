package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.UserAddress;

import java.util.List;

/**
 * 用户地址 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 获取用户地址列表
     * 
     * @param userId 用户ID
     * @return 地址列表
     */
    List<UserAddress> getAddressList(Long userId);

    /**
     * 获取用户默认地址
     * 
     * @param userId 用户ID
     * @return 默认地址
     */
    UserAddress getDefaultAddress(Long userId);

    /**
     * 添加地址
     * 
     * @param address 地址信息
     */
    void addAddress(UserAddress address);

    /**
     * 更新地址
     * 
     * @param address 地址信息
     */
    void updateAddress(UserAddress address);

    /**
     * 删除地址
     * 
     * @param addressId 地址ID
     * @param userId 用户ID（验证权限）
     */
    void deleteAddress(Long addressId, Long userId);

    /**
     * 设置默认地址
     * 
     * @param addressId 地址ID
     * @param userId 用户ID（验证权限）
     */
    void setDefaultAddress(Long addressId, Long userId);
}

