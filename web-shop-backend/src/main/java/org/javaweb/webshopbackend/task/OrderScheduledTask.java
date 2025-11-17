package org.javaweb.webshopbackend.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.entity.Orders;
import org.javaweb.webshopbackend.pojo.entity.SystemSettings;
import org.javaweb.webshopbackend.service.OrdersService;
import org.javaweb.webshopbackend.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时任务
 * 
 * @author WebShop Team
 * @date 2025-11-11
 */
@Slf4j
@Component
public class OrderScheduledTask {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    /**
     * 定时自动取消未支付订单
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void autoCancelUnpaidOrders() {
        try {
            log.info("开始执行自动取消未支付订单任务");

            // 获取系统设置
            SystemSettings settings = systemSettingsService.getSettings();
            if (settings == null || settings.getOrderCancelTime() == null) {
                log.warn("系统设置不存在或自动取消时间未配置");
                return;
            }

            // 计算截止时间
            LocalDateTime cancelTime = LocalDateTime.now().minusHours(settings.getOrderCancelTime());

            // 查询需要取消的订单（状态为0-待付款，且创建时间超过设置的时间）
            LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Orders::getStatus, 0)
                   .lt(Orders::getCreatedTime, cancelTime);

            List<Orders> orders = ordersService.list(wrapper);

            if (orders.isEmpty()) {
                log.info("没有需要取消的订单");
                return;
            }

            log.info("找到 {} 个需要取消的订单", orders.size());

            // 批量取消订单
            for (Orders order : orders) {
                try {
                    ordersService.adminCancelOrder(order.getOrderNo(), "系统自动取消：未在规定时间内支付");
                    log.info("订单已自动取消：orderId={}, orderNo={}", order.getId(), order.getOrderNo());
                } catch (Exception e) {
                    log.error("自动取消订单失败：orderId={}, orderNo={}", order.getId(), order.getOrderNo(), e);
                }
            }

            log.info("自动取消未支付订单任务完成");
        } catch (Exception e) {
            log.error("自动取消未支付订单任务异常", e);
        }
    }

    /**
     * 定时自动确认收货
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void autoConfirmReceivedOrders() {
        try {
            log.info("开始执行自动确认收货任务");

            // 获取系统设置
            SystemSettings settings = systemSettingsService.getSettings();
            if (settings == null || settings.getOrderConfirmTime() == null) {
                log.warn("系统设置不存在或自动确认收货时间未配置");
                return;
            }

            // 计算截止时间
            LocalDateTime confirmTime = LocalDateTime.now().minusDays(settings.getOrderConfirmTime());

            // 查询需要确认收货的订单（状态为2-待收货，且发货时间超过设置的时间）
            LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Orders::getStatus, 2)
                   .lt(Orders::getShipTime, confirmTime);

            List<Orders> orders = ordersService.list(wrapper);

            if (orders.isEmpty()) {
                log.info("没有需要自动确认收货的订单");
                return;
            }

            log.info("找到 {} 个需要自动确认收货的订单", orders.size());

            // 批量确认收货
            for (Orders order : orders) {
                try {
                    // 更新订单状态为已完成
                    order.setStatus(3);
                    order.setReceiveTime(LocalDateTime.now());
                    ordersService.updateById(order);
                    log.info("订单已自动确认收货：orderId={}, orderNo={}", order.getId(), order.getOrderNo());
                } catch (Exception e) {
                    log.error("自动确认收货失败：orderId={}, orderNo={}", order.getId(), order.getOrderNo(), e);
                }
            }

            log.info("自动确认收货任务完成");
        } catch (Exception e) {
            log.error("自动确认收货任务异常", e);
        }
    }
}
