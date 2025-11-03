package org.javaweb.webshopbackend.pojo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果类
 * 用于包装分页查询的返回结果
 * 
 * @author WebShop Team
 * @date 2025-11-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 每页大小
     */
    private Long size;
    
    /**
     * 总页数
     */
    private Long pages;

    /**
     * 构造分页结果（自动计算总页数）
     */
    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
    }

    /**
     * 判断是否有下一页
     */
    public boolean hasNext() {
        return this.current < this.pages;
    }

    /**
     * 判断是否有上一页
     */
    public boolean hasPrevious() {
        return this.current > 1;
    }
}


