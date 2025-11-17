package org.javaweb.webshopbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * 解决前后端分离时的跨域问题
 * 
 * @author WebShop Team
 * @date 2025-11-02
 */
@Configuration
public class CorsConfig {

    /**
     * 配置跨域过滤器
     * 允许前端（如 http://localhost:5174）访问后端API
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有域名进行跨域调用（生产环境建议配置具体域名）
        config.addAllowedOriginPattern("*");
        
        // 允许跨域发送cookie
        config.setAllowCredentials(true);
        
        // 允许所有请求方法跨域调用（GET、POST、PUT、DELETE等）
        config.addAllowedMethod("*");
        
        // 允许所有请求头
        config.addAllowedHeader("*");
        
        // 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");
        
        // 预检请求的缓存时间（秒），即在这个时间段内，对于相同的跨域请求不会再预检
        config.setMaxAge(3600L);
        
        // 添加映射路径，拦截所有请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}


