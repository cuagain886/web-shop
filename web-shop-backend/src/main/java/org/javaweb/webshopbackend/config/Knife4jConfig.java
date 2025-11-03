package org.javaweb.webshopbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API 文档配置类
 * 访问地址：http://localhost:8080/doc.html
 * 
 * @author WebShop Team
 * @date 2025-11-02
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置 OpenAPI 文档信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WebShop 电商平台 API 文档")
                        .version("1.0.0")
                        .description("基于 Spring Boot + MyBatis-Plus 的电商平台后端 API 文档")
                        .contact(new Contact()
                                .name("WebShop Team")
                                .email("webshop@example.com")
                                .url("https://github.com/webshop"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}


