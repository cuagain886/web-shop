package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.entity.UserAddress;
import org.javaweb.webshopbackend.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户地址Controller
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping("/api/address")
@Tag(name = "用户地址管理", description = "用户地址相关接口")
public class UserAddressController {

    @Autowired
    private UserAddressService addressService;

    /**
     * 获取用户所有地址
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户所有地址", description = "获取指定用户的所有收货地址")
    public Result<List<UserAddress>> getUserAddresses(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("获取用户所有地址：userId={}", userId);

        List<UserAddress> addresses = addressService.getAddressList(userId);

        return Result.success(addresses);
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/user/{userId}/default")
    @Operation(summary = "获取默认地址", description = "获取用户的默认收货地址")
    public Result<UserAddress> getDefaultAddress(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("获取默认地址：userId={}", userId);

        UserAddress address = addressService.getDefaultAddress(userId);

        return Result.success(address);
    }

    /**
     * 添加地址
     */
    @PostMapping
    @Operation(summary = "添加地址", description = "用户添加新的收货地址")
    public Result<Void> addAddress(@RequestBody UserAddress address) {
        log.info("添加地址：userId={}", address.getUserId());

        addressService.addAddress(address);

        return Result.success("地址添加成功");
    }

    /**
     * 更新地址
     */
    @PutMapping("/{addressId}")
    @Operation(summary = "更新地址", description = "用户更新收货地址信息")
    public Result<Void> updateAddress(
            @Parameter(description = "地址ID", required = true, example = "1")
            @PathVariable Long addressId,
            @RequestBody UserAddress address) {
        log.info("更新地址：addressId={}", addressId);

        address.setId(addressId);
        addressService.updateAddress(address);

        return Result.success("地址更新成功");
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{addressId}")
    @Operation(summary = "删除地址", description = "用户删除收货地址")
    public Result<Void> deleteAddress(
            @Parameter(description = "地址ID", required = true, example = "1")
            @PathVariable Long addressId,
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId) {
        log.info("删除地址：addressId={}, userId={}", addressId, userId);

        addressService.deleteAddress(addressId, userId);

        return Result.success("地址删除成功");
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/{addressId}/default")
    @Operation(summary = "设置默认地址", description = "用户设置默认收货地址")
    public Result<Void> setDefaultAddress(
            @Parameter(description = "地址ID", required = true, example = "1")
            @PathVariable Long addressId,
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId) {
        log.info("设置默认地址：addressId={}, userId={}", addressId, userId);

        addressService.setDefaultAddress(addressId, userId);

        return Result.success("默认地址设置成功");
    }
}

