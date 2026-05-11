package com.onlinecollege.file.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.onlinecollege.file.storage.FileStorage;
import com.onlinecollege.file.storage.LocalFileStorage;
import com.onlinecollege.file.storage.OssFileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

/**
 * 存储策略装配：
 * <ul>
 *   <li>{@code oss.enabled=true}  &rarr; {@link OssFileStorage}（阿里云 OSS）</li>
 *   <li>{@code oss.enabled=false} &rarr; {@link LocalFileStorage}（本地磁盘兜底）</li>
 * </ul>
 */
@Slf4j
@Configuration
public class StorageConfig {

    private OSS ossClient;

    @Bean
    public FileStorage fileStorage(OssProperties properties) {
        if (properties.isEnabled()) {
            if (isBlank(properties.getAccessKeyId()) || isBlank(properties.getAccessKeySecret())
                    || isBlank(properties.getEndpoint()) || isBlank(properties.getBucketName())) {
                throw new IllegalStateException(
                        "oss.enabled=true 时必须配置 oss.endpoint/access-key-id/access-key-secret/bucket-name");
            }
            this.ossClient = new OSSClientBuilder().build(
                    properties.getEndpoint(),
                    properties.getAccessKeyId(),
                    properties.getAccessKeySecret());
            log.info("使用阿里云 OSS 存储: endpoint={}, bucket={}",
                    properties.getEndpoint(), properties.getBucketName());
            return new OssFileStorage(ossClient, properties);
        }
        log.warn("oss.enabled=false，使用本地磁盘存储: root={}", properties.getLocal().getRoot());
        return new LocalFileStorage(properties);
    }

    @PreDestroy
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
