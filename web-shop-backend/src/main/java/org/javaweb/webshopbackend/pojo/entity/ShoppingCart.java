package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@TableName("shopping_cart")
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购物车项ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 关联商品ID
     */
    private Long productId;

    /**
     * 关联SKU ID
     */
    private Long skuId;

    /**
     * 规格信息（如："颜色:红色,尺寸:XL"）
     */
    private String specInfo;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 是否选中（0-否，1-是）
     */
    private Integer checked;

    /**
     * 添加时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 关联的商品信息（非数据库字段）
     */
    @TableField(exist = false)
    private Product product;
}

