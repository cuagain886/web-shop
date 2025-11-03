package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.OrderItem;

import java.util.List;

/**
 * 订单项 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 查询订单的所有订单项
     * 
     * @param orderId 订单ID
     * @return 订单项列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 查询用户待评价的订单项
     * 
     * @param userId 用户ID
     * @return 待评价订单项列表
     */
    List<OrderItem> selectUnreviewedByUserId(@Param("userId") Long userId);

    /**
     * 查询用户已评价的订单项
     * 
     * @param userId 用户ID
     * @return 已评价订单项列表
     */
    List<OrderItem> selectReviewedByUserId(@Param("userId") Long userId);

    /**
     * 统计商品销售数据（用于销售排行）
     * 
     * @param limit 数量限制
     * @return 商品销售数据列表
     */
    List<OrderItem> selectProductSalesRanking(@Param("limit") Integer limit);
}

