package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.OrderItem;

import java.util.List;

/**
 * 订单项 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface OrderItemService extends IService<OrderItem> {

    /**
     * 根据订单ID查询订单项
     * 
     * @param orderId 订单ID
     * @return 订单项列表
     */
    List<OrderItem> getByOrderId(Long orderId);

    /**
     * 查询用户待评价的订单项
     * 
     * @param userId 用户ID
     * @return 待评价订单项列表
     */
    List<OrderItem> getUnreviewedItems(Long userId);

    /**
     * 查询用户已评价的订单项
     * 
     * @param userId 用户ID
     * @return 已评价订单项列表
     */
    List<OrderItem> getReviewedItems(Long userId);

    /**
     * 获取商品销售排行
     * 
     * @param limit 数量限制
     * @return 商品销售排行列表
     */
    List<OrderItem> getProductSalesRanking(Integer limit);
}

