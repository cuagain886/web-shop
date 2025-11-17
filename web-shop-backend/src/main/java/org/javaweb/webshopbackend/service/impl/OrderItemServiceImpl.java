package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.OrderItemMapper;
import org.javaweb.webshopbackend.pojo.entity.OrderItem;
import org.javaweb.webshopbackend.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单项 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    public List<OrderItem> getByOrderId(Long orderId) {
        log.info("查询订单项：orderId={}", orderId);

        return baseMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderItem> getUnreviewedItems(Long userId) {
        log.info("查询待评价订单项：userId={}", userId);

        return baseMapper.selectUnreviewedByUserId(userId);
    }

    @Override
    public List<OrderItem> getReviewedItems(Long userId) {
        log.info("查询已评价订单项：userId={}", userId);

        return baseMapper.selectReviewedByUserId(userId);
    }

    @Override
    public List<OrderItem> getProductSalesRanking(Integer limit) {
        log.info("获取商品销售排行：limit={}", limit);

        return baseMapper.selectProductSalesRanking(limit);
    }
}

