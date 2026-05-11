package com.onlinecollege.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 配置。
 *
 * <p>通过 {@code application.yml} 里 {@code oss.*} 小节加载，支持环境变量注入。
 * 当 {@link #enabled} 为 {@code false} 时，应用不会初始化 OSS 客户端，上传操作会自动
 * 降级到本地磁盘（见 {@link LocalFileStorage}），方便在没有真实 AccessKey 的本地环境联调。
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /** 是否启用阿里云 OSS；false 则退化为本地磁盘存储 */
    private boolean enabled = false;

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /** 返回给前端的 URL 前缀；为空则按 https://bucket.endpoint 自动推导 */
    private String urlPrefix;

    /** 头像子目录 */
    private String avatarDir = "avatar/";

    /** 课程目录 Excel 子目录 */
    private String courseCatalogDir = "course-catalog/";

    /** 本地兜底存储配置 */
    private Local local = new Local();

    @Data
    public static class Local {
        /** 本地磁盘根目录 */
        private String root = "./uploads";
        /** 本地模式下对外暴露的 URL 前缀 */
        private String urlPrefix = "http://localhost:8086/file-service/local";
    }
}
