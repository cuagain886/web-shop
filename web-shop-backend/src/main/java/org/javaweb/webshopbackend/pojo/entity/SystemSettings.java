package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统设置实体类
 * 
 * @author WebShop Team
 * @date 2025-11-11
 */
@Data
@TableName("system_settings")
public class SystemSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设置ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 网站名称
     */
    private String siteName;

    /**
     * 网站描述
     */
    private String siteDescription;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 自动取消订单时间（小时）
     */
    private Integer orderCancelTime;

    /**
     * 自动确认收货时间（天）
     */
    private Integer orderConfirmTime;

    /**
     * 库存预警数量
     */
    private Integer stockWarning;

    /**
     * 默认运费
     */
    private BigDecimal defaultShipping;

    /**
     * 是否允许用户注册
     */
    private Boolean allowRegister;

    /**
     * 是否允许商品评价
     */
    private Boolean allowComment;

    /**
     * 是否开启维护模式
     */
    private Boolean maintenanceMode;

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
