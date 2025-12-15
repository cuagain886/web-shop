package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单项实体类
 *
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@TableName("order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单项ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 订单号（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private String orderNo;

    /**
     * 关联商品ID
     */
    private Long productId;

    /**
     * 关联SKU ID（可选）
     */
    private Long skuId;

    /**
     * 商品名称（冗余）
     */
    private String productName;

    /**
     * 商品图片（冗余）
     */
    private String productImage;

    /**
     * 规格信息（如："颜色:红色,尺寸:XL"）
     */
    private String specInfo;

    /**
     * 购买时单价（元）
     */
    private BigDecimal unitPrice;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 小计金额（元）
     */
    private BigDecimal totalPrice;

    /**
     * 是否已评价（0-未评价，1-已评价）
     */
    private Integer isReviewed;
}

