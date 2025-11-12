package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.Orders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 创建订单
     * 
     * @param userId 用户ID
     * @param addressId 地址ID
     * @param cartItemIds 购物车项ID列表
     * @param note 订单备注
     * @return 创建的订单
     */
    Orders createOrder(Long userId, Long addressId, List<Long> cartItemIds, String note);

    /**
     * 从商品列表创建订单（直接购买）
     *
     * @param userId 用户ID
     * @param addressId 地址ID
     * @param items 商品列表
     * @param note 订单备注
     * @return 创建的订单
     */
    Orders createOrderFromItems(Long userId, Long addressId, List<org.javaweb.webshopbackend.pojo.dto.OrderCreateDTO.OrderItemDTO> items, String note);

    /**
     * 分页查询用户订单
     * 
     * @param page 分页对象
     * @param userId 用户ID
     * @param status 订单状态（可为null）
     * @return 订单分页数据
     */
    IPage<Orders> getOrderPage(Page<Orders> page, Long userId, Integer status);

    /**
     * 分页查询所有订单（管理端）
     * 
     * @param page 分页对象
     * @param status 订单状态（可为null）
     * @param keyword 关键词（订单号/收件人）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单分页数据
     */
    IPage<Orders> getAdminOrderPage(Page<Orders> page, Integer status, String keyword,
                                    LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据订单号获取订单详情（含订单项）
     * 
     * @param orderNo 订单号
     * @return 订单详情
     */
    Orders getOrderDetail(String orderNo);

    /**
     * 根据订单ID获取订单详情
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    Orders getOrderDetailById(Long orderId);

    /**
     * 取消订单
     * 
     * @param orderNo 订单号
     * @param userId 用户ID（验证权限）
     * @param reason 取消原因
     */
    void cancelOrder(String orderNo, Long userId, String reason);

    /**
     * 管理员取消订单
     * 
     * @param orderNo 订单号
     * @param reason 取消原因
     */
    void adminCancelOrder(String orderNo, String reason);

    /**
     * 支付订单
     *
     * @param orderNo 订单号
     * @param userId 用户ID（验证权限）
     * @param paymentMethod 支付方式（1-微信，2-支付宝）
     * @return 更新后的订单
     */
    Orders payOrder(String orderNo, Long userId, Integer paymentMethod);

    /**
     * 发货
     * 
     * @param orderNo 订单号
     * @param expressCompany 物流公司
     * @param trackingNo 物流单号
     */
    void shipOrder(String orderNo, String expressCompany, String trackingNo);

    /**
     * 确认收货
     * 
     * @param orderNo 订单号
     * @param userId 用户ID（验证权限）
     */
    void confirmReceive(String orderNo, Long userId);

    /**
     * 删除订单（逻辑删除）
     * 
     * @param orderNo 订单号
     * @param userId 用户ID（验证权限）
     */
    void deleteOrder(String orderNo, Long userId);

    /**
     * 统计用户各状态订单数量
     * 
     * @param userId 用户ID
     * @return 各状态订单数量
     */
    Map<String, Long> countUserOrders(Long userId);

    /**
     * 统计销售数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 销售统计数据
     */
    Map<String, Object> getSalesStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计各状态订单数量
     * 
     * @return 各状态订单数量
     */
    Map<Integer, Long> countByStatus();
}

