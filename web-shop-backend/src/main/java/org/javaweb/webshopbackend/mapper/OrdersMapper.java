package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.Orders;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单 Mapper 接口
 * 注意：实体类名为 Orders（复数）
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 分页查询用户订单（带订单项）
     * 
     * @param page 分页对象
     * @param userId 用户ID
     * @param status 订单状态（可为null）
     * @return 订单分页数据
     */
    IPage<Orders> selectPageWithItems(Page<Orders> page,
                                      @Param("userId") Long userId,
                                      @Param("status") Integer status);

    /**
     * 分页查询所有订单（管理端）
     * 
     * @param page 分页对象
     * @param status 订单状态（可为null）
     * @param keyword 关键词（订单号/收件人）（可为null）
     * @param startTime 开始时间（可为null）
     * @param endTime 结束时间（可为null）
     * @return 订单分页数据
     */
    IPage<Orders> selectAdminPage(Page<Orders> page,
                                  @Param("status") Integer status,
                                  @Param("keyword") String keyword,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 根据订单号查询订单（带订单项）
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    Orders selectByOrderNoWithItems(@Param("orderNo") String orderNo);

    /**
     * 统计用户各状态订单数量
     * 
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单数量
     */
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 统计时间范围内的销售额
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 销售额
     */
    Double sumAmountByDateRange(@Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各状态订单数量
     * 
     * @return 状态和数量的映射
     */
    List<Orders> countByStatus();
}

