package org.javaweb.webshopbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebShopBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebShopBackendApplication.class, args);
    }

}
