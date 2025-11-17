package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.RefundMapper;
import org.javaweb.webshopbackend.pojo.entity.OrderItem;
import org.javaweb.webshopbackend.pojo.entity.Orders;
import org.javaweb.webshopbackend.pojo.entity.Refund;
import org.javaweb.webshopbackend.service.OrderItemService;
import org.javaweb.webshopbackend.service.OrdersService;
import org.javaweb.webshopbackend.service.ProductService;
import org.javaweb.webshopbackend.service.ProductSkuService;
import org.javaweb.webshopbackend.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSkuService productSkuService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyRefund(Refund refund) {
        log.info("提交退款申请：orderId={}, amount={}", refund.getOrderId(), refund.getRefundAmount());

        // 1. 验证订单
        Orders order = ordersService.getById(refund.getOrderId());
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 2. 验证订单状态（只有已支付、已发货、已收货的订单才能退款）
        // status: 1-待发货(已支付), 2-已发货, 3-已收货
        if (order.getStatus() != 1 && order.getStatus() != 2 && order.getStatus() != 3) {
            throw new IllegalArgumentException("订单状态不支持退款，只有已支付、已发货、已收货的订单可以退款");
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

        // 5. 生成退款单号并设置默认值
        refund.setRefundNo("RF" + System.currentTimeMillis());
        refund.setStatus(0); // 待审核
        this.save(refund);

        // 6. 更新订单状态为退款中
        order.setStatus(5); // 退款中
        ordersService.updateById(order);

        log.info("退款申请提交成功：refundId={}", refund.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewRefund(Long refundId, Integer status) {
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

        // 3. 获取订单信息（用于记录原始状态）
        Orders order = ordersService.getById(refund.getOrderId());
        if (order == null) {
            throw new IllegalArgumentException("关联订单不存在");
        }

        // 4. 更新退款状态
        refund.setStatus(status);
        this.updateById(refund);

        // 5. 更新订单状态
        if (status == 1) {
            // 同意退款，还原库存
            restoreStock(refund.getOrderId());
            order.setStatus(7); // 已退款
            log.info("退款申请已同意，订单状态更新为已退款，库存已还原");
        } else {
            // 拒绝退款，需要根据支付时间和发货时间判断应该恢复到什么状态
            if (order.getShipTime() != null) {
                // 已发货，恢复为已发货状态
                order.setStatus(2);
                log.info("退款申请已拒绝，订单状态恢复为已发货");
            } else {
                // 未发货，恢复为待发货状态
                order.setStatus(1);
                log.info("退款申请已拒绝，订单状态恢复为待发货");
            }
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
        if (order == null) {
            throw new IllegalArgumentException("关联订单不存在");
        }
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

        // 5. 恢复订单状态，根据发货情况判断
        if (order.getReceiveTime() != null) {
            // 已收货
            order.setStatus(3);
            log.info("退款申请已取消，订单状态恢复为已收货");
        } else if (order.getShipTime() != null) {
            // 已发货
            order.setStatus(2);
            log.info("退款申请已取消，订单状态恢复为已发货");
        } else {
            // 未发货
            order.setStatus(1);
            log.info("退款申请已取消，订单状态恢复为待发货");
        }
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

    /**
     * 还原订单库存
     */
    private void restoreStock(Long orderId) {
        List<OrderItem> items = orderItemService.getByOrderId(orderId);
        for (OrderItem item : items) {
            if (item.getSkuId() != null) {
                productSkuService.updateStock(item.getSkuId(), item.getQuantity());
                log.info("还原SKU库存：skuId={}, quantity={}", item.getSkuId(), item.getQuantity());
            }
            productService.updateProductStock(item.getProductId(), item.getQuantity());
            log.info("还原商品库存：productId={}, quantity={}", item.getProductId(), item.getQuantity());
        }
    }

    @Override
    public Refund getRefundByOrderId(Long orderId) {
        log.info("根据订单ID获取退款信息：orderId={}", orderId);
        
        LambdaQueryWrapper<Refund> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Refund::getOrderId, orderId)
               .orderByDesc(Refund::getCreatedTime)
               .last("LIMIT 1");
        
        return this.getOne(wrapper);
    }
}

