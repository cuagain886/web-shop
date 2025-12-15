package org.javaweb.webshopbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置类
 * 
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 配置BCrypt密码加密器
     * BCrypt是一种安全的密码哈希算法，自动加盐
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}