package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.entity.UserFavorite;
import org.javaweb.webshopbackend.service.UserFavoriteService;
import org.javaweb.webshopbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 添加收藏（支持两种调用方式）
     */
    @PostMapping
    @Operation(summary = "添加收藏", description = "用户收藏商品")
    public Result<Void> addFavorite(
            @Parameter(description = "商品ID", required = true, example = "1")
            @RequestBody(required = false) java.util.Map<String, Object> body,
            @Parameter(description = "用户ID", required = false, example = "1")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "商品ID", required = false, example = "1")
            @RequestParam(required = false) Long productId,
            HttpServletRequest request) {
        
        // 从请求体中获取productId
        if (body != null && body.containsKey("productId")) {
            productId = Long.parseLong(body.get("productId").toString());
        }
        
        // 从JWT token中获取userId
        if (userId == null) {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                userId = jwtUtil.getUserIdFromToken(token);
            }
        }
        
        log.info("添加收藏：userId={}, productId={}", userId, productId);

        favoriteService.addFavorite(userId, productId);

        return Result.success("收藏成功");
    }

    /**
     * 取消收藏（支持两种调用方式）
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "取消收藏", description = "用户取消收藏商品")
    public Result<Void> removeFavorite(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId,
            @Parameter(description = "用户ID", required = false, example = "1")
            @RequestParam(required = false) Long userId,
            HttpServletRequest request) {
        
        // 从JWT token中获取userId
        if (userId == null) {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                userId = jwtUtil.getUserIdFromToken(token);
            }
        }
        
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
    @GetMapping("/check/{productId}")
    @Operation(summary = "检查是否已收藏", description = "检查用户是否已收藏某商品")
    public Result<java.util.Map<String, Object>> isFavorite(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId,
            @Parameter(description = "用户ID", required = false, example = "1")
            @RequestParam(required = false) Long userId,
            HttpServletRequest request) {
        
        // 从JWT token中获取userId
        if (userId == null) {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                userId = jwtUtil.getUserIdFromToken(token);
            }
        }
        
        log.info("检查是否已收藏：userId={}, productId={}", userId, productId);

        boolean isFavorite = false;
        if (userId != null) {
            isFavorite = favoriteService.isFavorite(userId, productId);
        }
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("isFavorited", isFavorite);
        
        return Result.success(result);
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

