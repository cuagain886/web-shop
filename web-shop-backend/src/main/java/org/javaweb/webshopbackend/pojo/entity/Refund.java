package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款实体类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@TableName("refund")
public class Refund implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 退款ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 退款单号（唯一）
     */
    private String refundNo;

    /**
     * 退款金额（元）
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 状态（0-申请中，1-已同意，2-已拒绝）
     */
    private Integer status;

    /**
     * 逻辑删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;

    /**
     * 申请时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 关联订单信息（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private Orders order;
}

