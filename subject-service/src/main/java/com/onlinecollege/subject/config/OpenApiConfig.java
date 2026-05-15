package com.onlinecollege.subject.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger 文档元数据
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI subjectServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Subject Service API")
                        .description("在线学院 - 课程分类服务接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("online-college-team"))
                        .license(new License().name("MIT")));
    }
}
