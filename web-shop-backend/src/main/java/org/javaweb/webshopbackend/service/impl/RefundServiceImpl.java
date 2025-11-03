package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.RefundMapper;
import org.javaweb.webshopbackend.pojo.entity.Orders;
import org.javaweb.webshopbackend.pojo.entity.Refund;
import org.javaweb.webshopbackend.service.OrdersService;
import org.javaweb.webshopbackend.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 退款 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class RefundServiceImpl extends ServiceImpl<RefundMapper, Refund> implements RefundService {

    @Autowired
    private OrdersService ordersService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyRefund(Refund refund) {
        log.info("提交退款申请：orderId={}, amount={}", refund.getOrderId(), refund.getRefundAmount());

        // 1. 验证订单
        Orders order = ordersService.getById(refund.getOrderId());
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 2. 验证订单状态（只有已支付或已发货的订单才能退款）
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            throw new IllegalArgumentException("订单状态不支持退款");
        }

        // 3. 验证退款金额
        if (refund.getRefundAmount().compareTo(order.getTotalAmount()) > 0) {
            throw new IllegalArgumentException("退款金额不能超过订单金额");
        }
        if (refund.getRefundAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("退款金额必须大于0");
        }

        // 4. 检查是否已有退款申请
        LambdaQueryWrapper<Refund> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Refund::getOrderId, refund.getOrderId())
               .in(Refund::getStatus, 0, 1); // 待审核或已同意
        long count = this.count(wrapper);
        if (count > 0) {
            throw new IllegalArgumentException("该订单已存在退款申请");
        }

        // 5. 设置默认值
        refund.setStatus(0); // 待审核
        this.save(refund);

        // 6. 更新订单状态为退款中
        order.setStatus(6); // 退款中
        ordersService.updateById(order);

        log.info("退款申请提交成功：refundId={}", refund.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewRefund(Long refundId, Integer status, String remark) {
        log.info("商家审核退款：refundId={}, status={}", refundId, status);

        // 1. 获取退款申请
        Refund refund = this.getById(refundId);
        if (refund == null) {
            throw new IllegalArgumentException("退款申请不存在");
        }

        // 2. 验证状态
        if (refund.getStatus() != 0) {
            throw new IllegalArgumentException("退款申请已处理");
        }

        // 3. 更新退款状态
        refund.setStatus(status);
        refund.setRemark(remark);
        this.updateById(refund);

        // 4. 更新订单状态
        Orders order = ordersService.getById(refund.getOrderId());
        if (status == 1) {
            // 同意退款
            order.setStatus(7); // 已退款
            log.info("退款申请已同意");
        } else {
            // 拒绝退款，恢复原状态
            order.setStatus(2); // 恢复为已支付
            log.info("退款申请已拒绝");
        }
        ordersService.updateById(order);

        log.info("退款审核完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelRefund(Long refundId, Long userId) {
        log.info("取消退款申请：refundId={}, userId={}", refundId, userId);

        // 1. 获取退款申请
        Refund refund = this.getById(refundId);
        if (refund == null) {
            throw new IllegalArgumentException("退款申请不存在");
        }

        // 2. 验证权限
        Orders order = ordersService.getById(refund.getOrderId());
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此退款申请");
        }

        // 3. 验证状态
        if (refund.getStatus() != 0) {
            throw new IllegalArgumentException("退款申请已处理，无法取消");
        }

        // 4. 取消退款
        refund.setStatus(2); // 已取消
        this.updateById(refund);

        // 5. 恢复订单状态
        order.setStatus(2); // 恢复为已支付
        ordersService.updateById(order);

        log.info("退款申请已取消");
    }

    @Override
    public IPage<Refund> getRefundPage(Page<Refund> page, Integer status) {
        log.info("分页查询退款申请：status={}", status);

        LambdaQueryWrapper<Refund> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Refund::getStatus, status);
        }
        wrapper.orderByDesc(Refund::getCreatedTime);

        return this.page(page, wrapper);
    }

    @Override
    public IPage<Refund> getUserRefunds(Long userId, Page<Refund> page) {
        log.info("获取用户退款列表：userId={}", userId);

        return baseMapper.selectPageByUserId(page, userId);
    }

    @Override
    public Refund getRefundDetail(Long refundId) {
        log.info("获取退款详情：refundId={}", refundId);

        return baseMapper.selectDetailById(refundId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeRefund(Long refundId) {
        log.info("完成退款：refundId={}", refundId);

        Refund refund = this.getById(refundId);
        if (refund == null) {
            throw new IllegalArgumentException("退款申请不存在");
        }

        if (refund.getStatus() != 1) {
            throw new IllegalArgumentException("退款申请未审核通过");
        }

        // 这里可以添加实际的退款处理逻辑，比如调用支付接口
        
        log.info("退款完成");
    }
}

