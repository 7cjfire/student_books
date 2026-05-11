package com.onlinecollege;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.onlinecollege.index.mapper")
public class IndexServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IndexServiceApplication.class, args);
    }
}
