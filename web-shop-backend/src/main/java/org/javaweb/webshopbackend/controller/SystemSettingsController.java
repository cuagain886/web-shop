package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.entity.SystemSettings;
import org.javaweb.webshopbackend.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统设置Controller
 * 
 * @author WebShop Team
 * @date 2025-11-11
 */
@Slf4j
@RestController
@RequestMapping("/api/settings")
@Tag(name = "系统设置", description = "系统设置相关接口")
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService systemSettingsService;

    /**
     * 获取系统设置
     */
    @GetMapping
    @Operation(summary = "获取系统设置", description = "获取当前系统设置")
    public Result<SystemSettings> getSettings() {
        log.info("获取系统设置");

        SystemSettings settings = systemSettingsService.getSettings();

        return Result.success(settings);
    }

    /**
     * 更新系统设置
     */
    @PutMapping
    @Operation(summary = "更新系统设置", description = "更新系统设置")
    public Result<Void> updateSettings(@RequestBody SystemSettings settings) {
        log.info("更新系统设置");

        systemSettingsService.updateSettings(settings);

        return Result.success("设置已保存");
    }
}
