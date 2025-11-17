package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.UserAddressMapper;
import org.javaweb.webshopbackend.pojo.entity.UserAddress;
import org.javaweb.webshopbackend.service.UserAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户地址 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    public List<UserAddress> getAddressList(Long userId) {
        log.info("获取用户地址列表：userId={}", userId);

        return baseMapper.selectByUserId(userId);
    }

    @Override
    public UserAddress getDefaultAddress(Long userId) {
        log.info("获取用户默认地址：userId={}", userId);

        return baseMapper.selectDefaultByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(UserAddress address) {
        log.info("添加地址：userId={}", address.getUserId());

        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            baseMapper.cancelDefaultByUserId(address.getUserId());
        }

        this.save(address);

        log.info("地址添加成功：addressId={}", address.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(UserAddress address) {
        log.info("更新地址：addressId={}", address.getId());

        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            UserAddress oldAddress = this.getById(address.getId());
            if (oldAddress != null) {
                baseMapper.cancelDefaultByUserId(oldAddress.getUserId());
            }
        }

        this.updateById(address);

        log.info("地址更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long addressId, Long userId) {
        log.info("删除地址：addressId={}, userId={}", addressId, userId);

        // 验证地址是否属于当前用户
        UserAddress address = this.getById(addressId);
        if (address == null) {
            throw new IllegalArgumentException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权删除此地址");
        }

        this.removeById(addressId);

        log.info("地址删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long addressId, Long userId) {
        log.info("设置默认地址：addressId={}, userId={}", addressId, userId);

        // 验证地址是否属于当前用户
        UserAddress address = this.getById(addressId);
        if (address == null) {
            throw new IllegalArgumentException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此地址");
        }

        // 取消其他默认地址
        baseMapper.cancelDefaultByUserId(userId);

        // 设置为默认地址
        address.setIsDefault(1);
        this.updateById(address);

        log.info("默认地址设置成功");
    }
}

