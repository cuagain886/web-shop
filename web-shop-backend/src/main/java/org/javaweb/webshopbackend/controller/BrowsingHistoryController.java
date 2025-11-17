package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.entity.BrowsingHistory;
import org.javaweb.webshopbackend.service.BrowsingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 浏览历史Controller
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping("/api/history")
@Tag(name = "浏览历史管理", description = "浏览历史相关接口")
public class BrowsingHistoryController {

    @Autowired
    private BrowsingHistoryService historyService;

    /**
     * 记录浏览历史
     */
    @PostMapping
    @Operation(summary = "记录浏览历史", description = "记录用户浏览商品的历史")
    public Result<Void> recordBrowsing(
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId,
            @Parameter(description = "商品ID", required = true, example = "1")
            @RequestParam Long productId) {
        log.info("记录浏览历史：userId={}, productId={}", userId, productId);

        historyService.recordBrowsing(userId, productId);

        return Result.success("记录成功");
    }

    /**
     * 获取用户浏览历史
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户浏览历史", description = "获取用户的浏览历史（含商品信息）")
    public Result<List<BrowsingHistory>> getUserHistory(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "数量限制", example = "20")
            @RequestParam(required = false) Integer limit) {
        log.info("获取用户浏览历史：userId={}, limit={}", userId, limit);

        List<BrowsingHistory> history = historyService.getUserHistory(userId, limit);

        return Result.success(history);
    }

    /**
     * 删除指定浏览记录
     */
    @DeleteMapping("/{historyId}")
    @Operation(summary = "删除指定浏览记录", description = "用户删除单条浏览记录")
    public Result<Void> deleteHistory(
            @Parameter(description = "浏览记录ID", required = true, example = "1")
            @PathVariable Long historyId,
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId) {
        log.info("删除浏览记录：historyId={}, userId={}", historyId, userId);

        historyService.deleteHistory(userId, historyId);

        return Result.success("删除成功");
    }

    /**
     * 清空用户浏览历史
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "清空用户浏览历史", description = "用户清空所有浏览历史")
    public Result<Void> clearHistory(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("清空用户浏览历史：userId={}", userId);

        historyService.clearHistory(userId);

        return Result.success("浏览历史已清空");
    }

    /**
     * 获取热门商品
     */
    @GetMapping("/hot-products")
    @Operation(summary = "获取热门商品", description = "基于浏览次数统计热门商品")
    public Result<List<Long>> getHotProducts(
            @Parameter(description = "数量限制", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门商品：limit={}", limit);

        List<Long> hotProductIds = historyService.getHotProducts(limit);

        return Result.success(hotProductIds);
    }
}

