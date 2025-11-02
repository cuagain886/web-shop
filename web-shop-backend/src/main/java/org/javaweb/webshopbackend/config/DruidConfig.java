package org.javaweb.webshopbackend.config;

import com.alibaba.druid.support.jakarta.StatViewServlet;
import com.alibaba.druid.support.jakarta.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Druid 数据源监控配置类
 * 配置 Druid 监控页面和过滤规则
 * 注意：Spring Boot 3 使用 Jakarta EE，需要使用 druid.support.jakarta 包
 * 
 * @author WebShop Team
 * @date 2025-11-02
 */
@Configuration
public class DruidConfig {

    /**
     * 配置 Druid 监控页面
     * 访问地址：http://localhost:8080/druid/
     * 默认账号密码：无需登录（可在下方配置）
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = 
                new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        
        // 登录账号和密码（可选，不配置则无需登录）
        // bean.addInitParameter("loginUsername", "admin");
        // bean.addInitParameter("loginPassword", "123456");
        
        // IP白名单（为空则允许所有IP访问）
        bean.addInitParameter("allow", "");
        
        // IP黑名单（deny 优先于 allow）
        // bean.addInitParameter("deny", "192.168.1.100");
        
        // 禁用HTML页面上的"Reset All"功能
        bean.addInitParameter("resetEnable", "false");
        
        return bean;
    }

    /**
     * 配置 Druid Web 监控过滤器
     * 监控 Web 应用的请求
     */
    @Bean
    public FilterRegistrationBean<WebStatFilter> druidWebStatFilter() {
        FilterRegistrationBean<WebStatFilter> bean = 
                new FilterRegistrationBean<>(new WebStatFilter());
        
        // 添加过滤规则
        bean.addUrlPatterns("/*");
        
        // 忽略的资源（不统计）
        bean.addInitParameter("exclusions", 
                "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,/doc.html,/webjars/*,/swagger-resources,/v3/api-docs");
        
        // 是否启用session统计
        bean.addInitParameter("sessionStatEnable", "true");
        
        // session最大数量
        bean.addInitParameter("sessionStatMaxCount", "1000");
        
        // 配置principalSessionName，使得druid能够知道当前的用户是谁
        bean.addInitParameter("principalSessionName", "user");
        
        return bean;
    }
}

