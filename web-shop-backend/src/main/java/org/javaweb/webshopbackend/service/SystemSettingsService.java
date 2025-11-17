package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.SystemSettings;

/**
 * 系统设置 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-11
 */
public interface SystemSettingsService extends IService<SystemSettings> {

    /**
     * 获取系统设置
     * 
     * @return 系统设置
     */
    SystemSettings getSettings();

    /**
     * 更新系统设置
     * 
     * @param settings 系统设置
     */
    void updateSettings(SystemSettings settings);
}
