package com.onlinecollege.index.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI indexServiceOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Index Service API")
                .description("在线学院 - 首页服务（banner + 聚合数据 + Redis 缓存）")
                .version("1.0.0")
                .contact(new Contact().name("online-college-team"))
                .license(new License().name("MIT")));
    }
}
