package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.Refund;

/**
 * 退款 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface RefundService extends IService<Refund> {

    /**
     * 提交退款申请
     * 
     * @param refund 退款信息
     */
    void applyRefund(Refund refund);

    /**
     * 商家审核退款申请
     * 
     * @param refundId 退款ID
     * @param status 审核状态（1-同意，2-拒绝）
     */
    void reviewRefund(Long refundId, Integer status);

    /**
     * 取消退款申请
     * 
     * @param refundId 退款ID
     * @param userId 用户ID（验证权限）
     */
    void cancelRefund(Long refundId, Long userId);

    /**
     * 分页查询退款申请
     * 
     * @param page 分页对象
     * @param status 退款状态（可为null）
     * @return 退款分页数据
     */
    IPage<Refund> getRefundPage(Page<Refund> page, Integer status);

    /**
     * 获取用户的退款申请列表
     * 
     * @param userId 用户ID
     * @return 退款列表
     */
    IPage<Refund> getUserRefunds(Long userId, Page<Refund> page);

    /**
     * 获取退款详情
     * 
     * @param refundId 退款ID
     * @return 退款详情
     */
    Refund getRefundDetail(Long refundId);

    /**
     * 完成退款
     *
     * @param refundId 退款ID
     */
    void completeRefund(Long refundId);

    /**
     * 根据订单ID获取退款信息
     *
     * @param orderId 订单ID
     * @return 退款信息
     */
    Refund getRefundByOrderId(Long orderId);
}

