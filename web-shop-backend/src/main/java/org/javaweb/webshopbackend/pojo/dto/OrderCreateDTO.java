package org.javaweb.webshopbackend.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建订单DTO
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@Schema(description = "创建订单请求")
public class OrderCreateDTO {

    @Schema(description = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "收货地址ID", example = "1")
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    @Schema(description = "购物车项ID列表（从购物车创建订单时使用）", example = "[1, 2, 3]")
    private List<Long> cartItemIds;

    @Schema(description = "订单商品列表（直接创建订单时使用）")
    private List<OrderItemDTO> items;

    @Schema(description = "订单备注", example = "请尽快发货")
    private String note;

    /**
     * 订单商品项DTO
     */
    @Data
    @Schema(description = "订单商品项")
    public static class OrderItemDTO {

        @Schema(description = "商品ID", example = "1")
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @Schema(description = "SKU ID", example = "1")
        private Long skuId;

        @Schema(description = "购买数量", example = "2")
        @NotNull(message = "购买数量不能为空")
        private Integer quantity;
    }
}

