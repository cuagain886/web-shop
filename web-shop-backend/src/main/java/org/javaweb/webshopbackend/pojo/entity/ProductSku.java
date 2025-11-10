package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品SKU实体类
 * 
 * @author WebShop Team
 * @date 2025-11-10
 */
@Data
@TableName("product_sku")
public class ProductSku implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SKU ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * 规格名称（如：红色-128G）
     */
    private String skuName;

    /**
     * 规格属性（JSON格式，如：{"颜色":"红色","内存":"128G"}）
     */
    private String attributes;

    /**
     * SKU价格
     */
    private BigDecimal price;

    /**
     * SKU原价
     */
    private BigDecimal originalPrice;

    /**
     * SKU库存
     */
    private Integer stock;

    /**
     * SKU销量
     */
    private Integer sales;

    /**
     * SKU图片
     */
    private String image;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 逻辑删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}