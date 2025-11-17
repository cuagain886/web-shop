package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.entity.ShoppingCart;
import org.javaweb.webshopbackend.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车Controller
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping("/api/cart")
@Tag(name = "购物车管理", description = "购物车相关接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService cartService;

    /**
     * 获取用户购物车列表
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取购物车列表", description = "获取用户的购物车商品列表")
    public Result<List<ShoppingCart>> getCartList(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("获取购物车列表：userId={}", userId);

        List<ShoppingCart> cartList = cartService.getCartList(userId);

        return Result.success(cartList);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping
    @Operation(summary = "添加商品到购物车", description = "将商品添加到用户购物车")
    public Result<Void> addToCart(@RequestBody ShoppingCart cart) {
        log.info("添加商品到购物车：userId={}, productId={}, skuId={}", cart.getUserId(), cart.getProductId(), cart.getSkuId());

        cartService.addToCart(cart.getUserId(), cart.getProductId(), cart.getQuantity(), cart.getSpecInfo(), cart.getSkuId());

        return Result.success("添加成功");
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/{cartId}")
    @Operation(summary = "更新购物车商品数量", description = "修改购物车中商品的数量")
    public Result<Void> updateCartQuantity(
            @Parameter(description = "购物车ID", required = true, example = "1")
            @PathVariable Long cartId,
            @Parameter(description = "新数量", required = true, example = "3")
            @RequestParam Integer quantity) {
        log.info("更新购物车商品数量：cartId={}, quantity={}", cartId, quantity);

        cartService.updateQuantity(cartId, quantity);

        return Result.success("更新成功");
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{cartId}")
    @Operation(summary = "删除购物车商品", description = "从购物车中删除指定商品")
    public Result<Void> removeFromCart(
            @Parameter(description = "购物车ID", required = true, example = "1")
            @PathVariable Long cartId) {
        log.info("删除购物车商品：cartId={}", cartId);

        cartService.removeCart(cartId);

        return Result.success("删除成功");
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "清空购物车", description = "清空用户的所有购物车商品")
    public Result<Void> clearCart(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("清空购物车：userId={}", userId);

        cartService.clearCart(userId);

        return Result.success("购物车已清空");
    }

    /**
     * 批量删除购物车商品
     */
    @PostMapping("/batch-delete")
    @Operation(summary = "批量删除购物车商品", description = "批量删除购物车中的商品")
    public Result<Void> batchRemoveFromCart(@RequestBody Map<String, List<Long>> request) {
        List<Long> cartIds = request.get("ids");
        log.info("批量删除购物车商品：cartIds={}", cartIds);

        for (Long cartId : cartIds) {
            cartService.removeCart(cartId);
        }

        return Result.success("批量删除成功");
    }

    /**
     * 切换购物车商品选中状态
     */
    @PutMapping("/{cartId}/toggle")
    @Operation(summary = "切换商品选中状态", description = "切换购物车商品的选中状态")
    public Result<Void> toggleCartItem(
            @Parameter(description = "购物车ID", required = true) @PathVariable Long cartId,
            @RequestBody Map<String, Boolean> request) {
        Boolean checked = request.get("checked");
        log.info("切换购物车商品选中状态：cartId={}, checked={}", cartId, checked);
        
        cartService.updateChecked(cartId, checked ? 1 : 0);
        
        return Result.success("状态更新成功");
    }

    /**
     * 全选/取消全选购物车商品
     */
    @PutMapping("/toggle-all")
    @Operation(summary = "全选/取消全选", description = "全选或取消全选用户购物车中的所有商品")
    public Result<Void> toggleAllCartItems(
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId,
            @RequestBody Map<String, Boolean> request) {
        Boolean checked = request.get("checked");
        log.info("全选/取消全选购物车：userId={}, checked={}", userId, checked);
        
        cartService.updateAllChecked(userId, checked ? 1 : 0);
        
        return Result.success("状态更新成功");
    }

    /**
     * 获取购物车商品数量
     */
    @GetMapping("/{userId}/count")
    @Operation(summary = "获取购物车商品数量", description = "获取用户购物车中的商品总数")
    public Result<Integer> getCartCount(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("获取购物车商品数量：userId={}", userId);

        Integer count = cartService.countCartItems(userId);

        return Result.success(count);
    }
}

