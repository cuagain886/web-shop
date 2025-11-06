package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 浏览历史实体类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@TableName("browsing_history")
public class BrowsingHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 历史记录ID
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
     * 浏览时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime viewTime;

    /**
     * 关联商品信息（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private Product product;
}

