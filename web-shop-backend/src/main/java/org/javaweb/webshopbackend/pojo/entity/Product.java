package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 关联分类ID
     */
    private Long categoryId;

    /**
     * 商品单价（元）
     */
    private BigDecimal price;

    /**
     * 原价（元）
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 销售数量
     */
    private Integer sales;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 商品图片（JSON数组）
     */
    private String images;

    /**
     * 商品描述（富文本）
     */
    private String description;

    /**
     * 商品规格（JSON格式）
     */
    private String specs;

    /**
     * 状态（0-下架，1-上架）
     */
    private Integer status;

    /**
     * 是否热销（0-否，1-是）
     */
    private Integer isHot;

    /**
     * 是否推荐（0-否，1-是）
     */
    private Integer isRecommend;

    /**
     * 是否限时秒杀（0-否，1-是）
     */
    private Integer isFlashSale;

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

