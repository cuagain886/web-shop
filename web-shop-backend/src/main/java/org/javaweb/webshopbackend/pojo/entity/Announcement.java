package org.javaweb.webshopbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告实体类
 * 
 * @author WebShop Team
 * @date 2025-11-12
 */
@Data
@TableName("announcement")
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型（1-普通公告，2-重要公告，3-紧急公告）
     */
    private Integer type;

    /**
     * 是否置顶（0-否，1-是）
     */
    private Integer isTop;

    /**
     * 状态（0-草稿，1-发布）
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
