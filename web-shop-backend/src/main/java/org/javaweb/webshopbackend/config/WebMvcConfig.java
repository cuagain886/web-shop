package org.javaweb.webshopbackend.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.javaweb.webshopbackend.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * Web MVC 配置类
 * 配置消息转换器、静态资源映射等
 * 
 * @author WebShop Team
 * @date 2025-11-02
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/product/**",
                        "/api/category/**",
                        "/api/file/**",
                        "/api/announcements/published",
                        "/api/announcements/page",
                        "/doc.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/uploads/**"
                );
    }

    /**
     * 配置静态资源映射
     * 映射上传文件访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        
        // 配置静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 配置消息转换器
     * 自定义 JSON 序列化和反序列化规则
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 配置时间模块（支持 Java 8 时间类型）
        objectMapper.registerModule(new JavaTimeModule());
        
        // 配置日期格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        
        // 序列化配置
        // 美化输出（开发环境可开启，生产环境建议关闭）
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 禁用将日期序列化为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略空bean转json的错误
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        // 反序列化配置
        // 忽略json字符串中不识别的属性
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 忽略无法转换的对象
        objectMapper.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        
        // 创建消息转换器并添加到converters
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converters.add(0, converter);
    }
}


