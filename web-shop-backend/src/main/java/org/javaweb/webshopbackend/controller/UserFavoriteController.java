package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.entity.UserFavorite;
import org.javaweb.webshopbackend.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户收藏Controller
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping("/api/favorite")
@Tag(name = "用户收藏管理", description = "用户收藏相关接口")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping
    @Operation(summary = "添加收藏", description = "用户收藏商品")
    public Result<Void> addFavorite(
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId,
            @Parameter(description = "商品ID", required = true, example = "1")
            @RequestParam Long productId) {
        log.info("添加收藏：userId={}, productId={}", userId, productId);

        favoriteService.addFavorite(userId, productId);

        return Result.success("收藏成功");
    }

    /**
     * 取消收藏
     */
    @DeleteMapping
    @Operation(summary = "取消收藏", description = "用户取消收藏商品")
    public Result<Void> removeFavorite(
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId,
            @Parameter(description = "商品ID", required = true, example = "1")
            @RequestParam Long productId) {
        log.info("取消收藏：userId={}, productId={}", userId, productId);

        favoriteService.removeFavorite(userId, productId);

        return Result.success("已取消收藏");
    }

    /**
     * 获取用户收藏列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户收藏列表", description = "获取用户的所有收藏商品（含商品信息）")
    public Result<List<UserFavorite>> getFavoriteList(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("获取用户收藏列表：userId={}", userId);

        List<UserFavorite> favorites = favoriteService.getFavoriteList(userId);

        return Result.success(favorites);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    @Operation(summary = "检查是否已收藏", description = "检查用户是否已收藏某商品")
    public Result<Boolean> isFavorite(
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId,
            @Parameter(description = "商品ID", required = true, example = "1")
            @RequestParam Long productId) {
        log.info("检查是否已收藏：userId={}, productId={}", userId, productId);

        boolean isFavorite = favoriteService.isFavorite(userId, productId);

        return Result.success(isFavorite);
    }

    /**
     * 清空用户收藏
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "清空用户收藏", description = "清空用户的所有收藏")
    public Result<Void> clearFavorites(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("清空用户收藏：userId={}", userId);

        favoriteService.clearFavorites(userId);

        return Result.success("收藏已清空");
    }

    /**
     * 统计商品被收藏次数
     */
    @GetMapping("/product/{productId}/count")
    @Operation(summary = "统计商品被收藏次数", description = "获取商品被收藏的总次数")
    public Result<Long> countByProduct(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("统计商品被收藏次数：productId={}", productId);

        Long count = favoriteService.countByProduct(productId);

        return Result.success(count);
    }
}

