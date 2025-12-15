package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评价实体类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@TableName("product_review")
public class ProductReview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联订单项ID
     */
    private Long orderItemId;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 关联商品ID
     */
    private Long productId;

    /**
     * 评分（1-5星）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片（JSON数组）
     */
    private String images;

    /**
     * 商家回复
     */
    private String reply;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

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

    /**
     * 关联用户信息（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private User user;

    /**
     * 商品名称（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 商品图片（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private String productImage;
}

