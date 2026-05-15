package com.onlinecollege.file.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fileServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("File Service API")
                        .description("在线学院 - 文件上传服务（OSS + EasyExcel）")
                        .version("1.0.0")
                        .contact(new Contact().name("online-college-team"))
                        .license(new License().name("MIT")));
    }
}
