package com.onlinecollege;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 文件服务启动类
 *
 * <p>模块职责：
 * <ul>
 *   <li>头像上传 / Excel 上传到阿里云 OSS（Endpoint 不可达时自动降级为本地磁盘存储，方便本地联调）</li>
 *   <li>解析课程分类 Excel 并批量写入 {@code edu_subject} 表（EasyExcel 流式监听器）</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.onlinecollege.file.mapper")
public class FileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);
    }
}
