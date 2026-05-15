package com.onlinecollege.course.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI courseServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Course Service API")
                        .description("在线学院 - 课程服务（多步骤发布 + 章节/小节 + VOD）")
                        .version("1.0.0")
                        .contact(new Contact().name("online-college-team"))
                        .license(new License().name("MIT")));
    }
}
