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
        log.info("添加商品到购物车：userId={}, productId={}", cart.getUserId(), cart.getProductId());

        cartService.addToCart(cart.getUserId(), cart.getProductId(), cart.getQuantity(), cart.getSpecInfo());

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

