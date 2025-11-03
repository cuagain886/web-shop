package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.OperationLog;

import java.time.LocalDateTime;

/**
 * 操作日志 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 分页查询操作日志
     * 
     * @param page 分页对象
     * @param operatorId 操作人ID（可为null）
     * @param operationType 操作类型（可为null）
     * @param keyword 关键词（可为null）
     * @param startTime 开始时间（可为null）
     * @param endTime 结束时间（可为null）
     * @return 日志分页数据
     */
    IPage<OperationLog> selectPage(Page<OperationLog> page,
                                   @Param("operatorId") Long operatorId,
                                   @Param("operationType") String operationType,
                                   @Param("keyword") String keyword,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 删除指定时间之前的日志
     * 
     * @param beforeTime 时间点
     */
    void deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 清空所有日志
     */
    void deleteAll();
}

