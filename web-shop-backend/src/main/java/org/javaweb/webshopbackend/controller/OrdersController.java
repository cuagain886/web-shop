package org.javaweb.webshopbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.dto.OrderCreateDTO;
import org.javaweb.webshopbackend.pojo.entity.Orders;
import org.javaweb.webshopbackend.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 订单Controller
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单相关接口")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 创建订单
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "用户创建新订单")
    public Result<Orders> createOrder(@RequestBody OrderCreateDTO orderDTO) {
        log.info("创建订单：userId={}, addressId={}", orderDTO.getUserId(), orderDTO.getAddressId());

        Orders order;
        if (orderDTO.getCartItemIds() != null && !orderDTO.getCartItemIds().isEmpty()) {
            // 从购物车创建订单
            order = ordersService.createOrder(
                    orderDTO.getUserId(),
                    orderDTO.getAddressId(),
                    orderDTO.getCartItemIds(),
                    orderDTO.getNote()
            );
        } else if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            // 直接购买创建订单
            order = ordersService.createOrderFromItems(
                    orderDTO.getUserId(),
                    orderDTO.getAddressId(),
                    orderDTO.getItems(),
                    orderDTO.getNote()
            );
        } else {
            throw new IllegalArgumentException("订单商品不能为空");
        }

        return Result.success("订单创建成功", order);
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderNo}/cancel")
    @Operation(summary = "取消订单", description = "用户/管理员取消订单")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单号", required = true, example = "ORDER123456")
            @PathVariable String orderNo,
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId,
            @Parameter(description = "取消原因", example = "不想要了")
            @RequestParam String reason) {
        log.info("取消订单：orderNo={}, userId={}", orderNo, userId);

        ordersService.cancelOrder(orderNo, userId, reason);

        return Result.success("订单已取消");
    }

    /**
     * 支付订单
     */
    @PutMapping("/{orderNo}/pay")
    @Operation(summary = "支付订单", description = "用户支付订单")
    public Result<Orders> payOrder(
            @Parameter(description = "订单号", required = true, example = "ORDER123456")
            @PathVariable String orderNo,
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId,
            @Parameter(description = "支付方式（1-微信，2-支付宝）", required = true, example = "1")
            @RequestParam Integer paymentMethod) {
        log.info("支付订单：orderNo={}, userId={}, paymentMethod={}", orderNo, userId, paymentMethod);

        Orders order = ordersService.payOrder(orderNo, userId, paymentMethod);

        return Result.success("支付成功", order);
    }

    /**
     * 发货
     */
    @PutMapping("/{orderNo}/ship")
    @Operation(summary = "发货", description = "商家发货")
    public Result<Void> shipOrder(
            @Parameter(description = "订单号", required = true, example = "ORDER123456")
            @PathVariable String orderNo,
            @Parameter(description = "物流公司", example = "顺丰速运")
            @RequestParam String expressCompany,
            @Parameter(description = "物流单号", example = "SF1234567890")
            @RequestParam String trackingNo) {
        log.info("发货：orderNo={}, expressCompany={}, trackingNo={}", orderNo, expressCompany, trackingNo);

        ordersService.shipOrder(orderNo, expressCompany, trackingNo);

        return Result.success("发货成功");
    }

    /**
     * 确认收货
     */
    @PutMapping("/{orderNo}/receive")
    @Operation(summary = "确认收货", description = "用户确认收货")
    public Result<Void> receiveOrder(
            @Parameter(description = "订单号", required = true, example = "ORDER123456")
            @PathVariable String orderNo,
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId) {
        log.info("确认收货：orderNo={}, userId={}", orderNo, userId);

        ordersService.confirmReceive(orderNo, userId);

        return Result.success("已确认收货");
    }

    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询订单列表（管理端）", description = "支持按状态、时间范围、关键词查询")
    public Result<IPage<Orders>> getOrderList(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "订单状态", example = "2")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "关键词", example = "张三")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "开始时间", example = "2025-01-01 00:00:00")
            @RequestParam(required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间", example = "2025-12-31 23:59:59")
            @RequestParam(required = false) LocalDateTime endTime) {
        log.info("分页查询订单列表（管理端）：pageNum={}, pageSize={}, status={}", pageNum, pageSize, status);

        Page<Orders> page = new Page<>(pageNum, pageSize);
        IPage<Orders> orderPage = ordersService.getAdminOrderPage(page, status, keyword, startTime, endTime);

        return Result.success(orderPage);
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户订单列表", description = "获取指定用户的所有订单")
    public Result<IPage<Orders>> getUserOrders(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "订单状态", example = "2")
            @RequestParam(required = false) Integer status) {
        log.info("获取用户订单列表：userId={}, status={}", userId, status);

        Page<Orders> page = new Page<>(pageNum, pageSize);
        IPage<Orders> orderPage = ordersService.getOrderPage(page, userId, status);

        return Result.success(orderPage);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情", description = "获取订单完整信息（含商品明细）")
    public Result<Orders> getOrderDetail(
            @Parameter(description = "订单ID", required = true, example = "1")
            @PathVariable Long orderId) {
        log.info("获取订单详情：orderId={}", orderId);

        Orders order = ordersService.getOrderDetailById(orderId);

        return Result.success(order);
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取订单统计数据", description = "获取销售统计数据")
    public Result<Map<String, Object>> getOrderStatistics(
            @Parameter(description = "开始时间", example = "2025-01-01 00:00:00")
            @RequestParam(required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间", example = "2025-12-31 23:59:59")
            @RequestParam(required = false) LocalDateTime endTime) {
        log.info("获取订单统计数据：startTime={}, endTime={}", startTime, endTime);

        Map<String, Object> statistics = ordersService.getSalesStatistics(startTime, endTime);

        return Result.success(statistics);
    }

    /**
     * 获取订单状态分布
     */
    @GetMapping("/status-distribution")
    @Operation(summary = "获取订单状态分布", description = "获取各状态订单的数量分布")
    public Result<Map<Integer, Long>> getStatusDistribution() {
        log.info("获取订单状态分布");

        Map<Integer, Long> distribution = ordersService.countByStatus();

        return Result.success(distribution);
    }

    /**
     * 获取用户订单数量统计
     */
    @GetMapping("/user/{userId}/count")
    @Operation(summary = "获取用户订单数量统计", description = "获取用户各状态订单数量")
    public Result<Map<String, Long>> getUserOrderCount(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("获取用户订单数量统计：userId={}", userId);

        Map<String, Long> count = ordersService.countUserOrders(userId);

        return Result.success(count);
    }

    /**
     * 商家取消订单（管理端）
     */
    @PutMapping("/admin/{orderId}/cancel")
    @Operation(summary = "商家取消订单", description = "管理员取消订单")
    public Result<Void> adminCancelOrder(
            @Parameter(description = "订单ID", required = true, example = "1")
            @PathVariable Long orderId,
            @RequestBody Map<String, String> data) {
        log.info("商家取消订单：orderId={}", orderId);

        Orders order = ordersService.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        String reason = data.getOrDefault("reason", "商家取消");
        ordersService.adminCancelOrder(order.getOrderNo(), reason);

        return Result.success("订单已取消");
    }

    /**
     * 添加订单备注（管理端）
     */
    @PostMapping("/admin/{orderId}/note")
    @Operation(summary = "添加订单备注", description = "管理员添加订单备注")
    public Result<Void> addOrderNote(
            @Parameter(description = "订单ID", required = true, example = "1")
            @PathVariable Long orderId,
            @RequestBody Map<String, String> data) {
        log.info("添加订单备注：orderId={}", orderId);

        Orders order = ordersService.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        String note = data.get("note");
        if (note == null || note.trim().isEmpty()) {
            throw new IllegalArgumentException("备注内容不能为空");
        }

        // 这里可以添加备注保存逻辑，如果有备注表的话
        // 目前只是记录日志
        log.info("订单备注已添加：orderId={}, note={}", orderId, note);

        return Result.success("备注添加成功");
    }
}

