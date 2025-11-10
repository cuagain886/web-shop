package org.javaweb.webshopbackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现类
 * 
 * @author WebShop Team
 * @date 2025-11-10
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendShipmentNotification(String toEmail, String orderNo, String expressCompany, String trackingNo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("订单发货通知 - " + orderNo);
            message.setText(String.format(
                "尊敬的用户：\n\n" +
                "您的订单 %s 已发货！\n\n" +
                "物流公司：%s\n" +
                "物流单号：%s\n\n" +
                "请注意查收，感谢您的购买！\n\n" +
                "此邮件为系统自动发送，请勿回复。",
                orderNo, expressCompany, trackingNo
            ));
            
            mailSender.send(message);
            log.info("发货通知邮件发送成功：toEmail={}, orderNo={}", toEmail, orderNo);
        } catch (Exception e) {
            log.error("发货通知邮件发送失败：toEmail={}, orderNo={}, error={}", toEmail, orderNo, e.getMessage());
        }
    }
}