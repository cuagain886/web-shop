package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 * 注意：表名为 orders（避免与 SQL 关键字 order 冲突）
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@TableName("orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号（唯一）
     */
    private String orderNo;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 订单总金额（元）
     */
    private BigDecimal totalAmount;

    /**
     * 实付金额（元）
     */
    private BigDecimal payAmount;

    /**
     * 运费（元）
     */
    private BigDecimal freight;

    /**
     * 状态（0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-退款中，6-已退款）
     */
    private Integer status;

    /**
     * 支付方式（1-微信，2-支付宝）
     */
    private Integer paymentMethod;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 确认收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 收件人电话
     */
    private String receiverPhone;

    /**
     * 收件人完整地址
     */
    private String receiverAddress;

    /**
     * 物流公司
     */
    private String expressCompany;

    /**
     * 物流单号
     */
    private String trackingNo;

    /**
     * 订单备注
     */
    private String note;

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
     * 订单项列表（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private List<OrderItem> items;
}

