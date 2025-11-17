package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.SystemSettingsMapper;
import org.javaweb.webshopbackend.pojo.entity.SystemSettings;
import org.javaweb.webshopbackend.service.SystemSettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统设置 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-11
 */
@Slf4j
@Service
public class SystemSettingsServiceImpl extends ServiceImpl<SystemSettingsMapper, SystemSettings> implements SystemSettingsService {

    @Override
    public SystemSettings getSettings() {
        log.info("获取系统设置");

        LambdaQueryWrapper<SystemSettings> wrapper = new LambdaQueryWrapper<>();
        SystemSettings settings = this.getOne(wrapper);

        if (settings == null) {
            // 如果没有设置，返回默认设置
            settings = new SystemSettings();
            settings.setSiteName("购物平台");
            settings.setSiteDescription("一个基于Vue3的现代化购物平台");
            settings.setContactPhone("400-888-8888");
            settings.setContactEmail("contact@example.com");
            settings.setOrderCancelTime(24);
            settings.setOrderConfirmTime(7);
            settings.setStockWarning(10);
            settings.setDefaultShipping(new BigDecimal("10.00"));
            settings.setAllowRegister(true);
            settings.setAllowComment(true);
            settings.setMaintenanceMode(false);
        }

        return settings;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSettings(SystemSettings settings) {
        log.info("更新系统设置");

        SystemSettings existing = this.getSettings();

        if (existing != null && existing.getId() != null) {
            // 更新现有设置
            settings.setId(existing.getId());
            settings.setUpdatedTime(LocalDateTime.now());
            this.updateById(settings);
        } else {
            // 创建新设置
            settings.setCreatedTime(LocalDateTime.now());
            settings.setUpdatedTime(LocalDateTime.now());
            this.save(settings);
        }

        log.info("系统设置更新成功");
    }
}
