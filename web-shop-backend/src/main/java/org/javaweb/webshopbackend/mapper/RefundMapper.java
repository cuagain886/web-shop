package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.Refund;

/**
 * 退款 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface RefundMapper extends BaseMapper<Refund> {

    /**
     * 分页查询退款申请（带订单信息）
     * 
     * @param page 分页对象
     * @param status 退款状态（可为null）
     * @param keyword 关键词（退款单号/订单号）（可为null）
     * @return 退款分页数据
     */
    IPage<Refund> selectPageWithOrder(Page<Refund> page,
                                      @Param("status") Integer status,
                                      @Param("keyword") String keyword);

    /**
     * 根据订单ID查询退款记录
     * 
     * @param orderId 订单ID
     * @return 退款记录
     */
    Refund selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据退款单号查询退款记录
     * 
     * @param refundNo 退款单号
     * @return 退款记录
     */
    Refund selectByRefundNo(@Param("refundNo") String refundNo);

    /**
     * 统计各状态退款数量
     * 
     * @param status 退款状态
     * @return 退款数量
     */
    Long countByStatus(@Param("status") Integer status);

    /**
     * 分页查询用户的退款申请（带订单信息）
     * 
     * @param page 分页对象
     * @param userId 用户ID
     * @return 退款分页数据
     */
    IPage<Refund> selectPageByUserId(Page<Refund> page, @Param("userId") Long userId);

    /**
     * 查询退款详情（带完整订单和商品信息）
     * 
     * @param refundId 退款ID
     * @return 退款详情
     */
    Refund selectDetailById(@Param("refundId") Long refundId);
}

