package com.onlinecollege;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 教师服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.onlinecollege.teacher.mapper")
public class TeacherServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TeacherServiceApplication.class, args);
    }
}