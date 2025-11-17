package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.OperationLog;

import java.time.LocalDateTime;

/**
 * 操作日志 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 记录操作日志
     * 
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param operationType 操作类型（商品管理/订单管理/用户管理等）
     * @param operationContent 操作内容描述
     * @param targetType 操作对象类型（商品/订单/用户等）
     * @param targetId 操作对象ID
     */
    void recordLog(Long operatorId, String operatorName, String operationType, 
                   String operationContent, String targetType, Long targetId);

    /**
     * 分页查询操作日志
     * 
     * @param page 分页对象
     * @param operationType 操作类型（可为null）
     * @param startTime 开始时间（可为null）
     * @param endTime 结束时间（可为null）
     * @return 操作日志分页数据
     */
    IPage<OperationLog> getLogPage(Page<OperationLog> page, String operationType, 
                                   LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取操作人的操作日志
     * 
     * @param operatorId 操作人ID
     * @param limit 限制数量
     * @return 操作日志列表
     */
    IPage<OperationLog> getLogsByOperator(Long operatorId, Integer limit);

    /**
     * 清理指定天数前的日志
     * 
     * @param days 保留天数
     */
    void cleanOldLogs(Integer days);
}

