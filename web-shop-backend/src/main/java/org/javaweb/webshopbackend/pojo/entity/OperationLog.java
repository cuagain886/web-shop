package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@TableName("operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作类型（如：商品管理、订单管理）
     */
    private String operationType;

    /**
     * 操作内容
     */
    private String operationContent;

    /**
     * 操作对象（如：商品ID、订单号）
     */
    private String operationObject;

    /**
     * 操作对象类型（如：商品、订单、用户）
     */
    private String targetType;

    /**
     * 操作对象ID
     */
    private Long targetId;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 操作时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}

