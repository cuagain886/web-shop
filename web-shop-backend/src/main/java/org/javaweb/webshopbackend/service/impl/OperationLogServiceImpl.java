package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.OperationLogMapper;
import org.javaweb.webshopbackend.pojo.entity.OperationLog;
import org.javaweb.webshopbackend.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 操作日志 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordLog(Long operatorId, String operatorName, String operationType, 
                         String operationContent, String targetType, Long targetId) {
        log.info("记录操作日志：operator={}, type={}, content={}", 
                operatorName, operationType, operationContent);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperatorId(operatorId);
        operationLog.setOperatorName(operatorName);
        operationLog.setOperationType(operationType);
        operationLog.setOperationContent(operationContent);
        operationLog.setTargetType(targetType);
        operationLog.setTargetId(targetId);

        this.save(operationLog);
    }

    @Override
    public IPage<OperationLog> getLogPage(Page<OperationLog> page, String operationType, 
                                         LocalDateTime startTime, LocalDateTime endTime) {
        log.info("分页查询操作日志：type={}, startTime={}, endTime={}", 
                operationType, startTime, endTime);

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (operationType != null && !operationType.isEmpty()) {
            wrapper.eq(OperationLog::getOperationType, operationType);
        }
        if (startTime != null) {
            wrapper.ge(OperationLog::getCreatedTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(OperationLog::getCreatedTime, endTime);
        }
        
        wrapper.orderByDesc(OperationLog::getCreatedTime);

        return this.page(page, wrapper);
    }

    @Override
    public IPage<OperationLog> getLogsByOperator(Long operatorId, Integer limit) {
        log.info("获取操作人的操作日志：operatorId={}, limit={}", operatorId, limit);

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getOperatorId, operatorId)
               .orderByDesc(OperationLog::getCreatedTime)
               .last("LIMIT " + limit);

        Page<OperationLog> page = new Page<>(1, limit);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanOldLogs(Integer days) {
        log.info("清理{}天前的操作日志", days);

        LocalDateTime beforeDate = LocalDateTime.now().minusDays(days);
        
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(OperationLog::getCreatedTime, beforeDate);
        
        long count = this.count(wrapper);
        this.remove(wrapper);
        
        log.info("清理了{}条操作日志", count);
    }
}

