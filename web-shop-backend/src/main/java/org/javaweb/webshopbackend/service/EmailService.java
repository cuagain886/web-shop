package org.javaweb.webshopbackend.service;

/**
 * 邮件服务接口
 * 
 * @author WebShop Team
 * @date 2025-11-10
 */
public interface EmailService {

    /**
     * 发送发货通知邮件
     * 
     * @param toEmail 收件人邮箱
     * @param orderNo 订单号
     * @param expressCompany 物流公司
     * @param trackingNo 物流单号
     */
    void sendShipmentNotification(String toEmail, String orderNo, String expressCompany, String trackingNo);
}