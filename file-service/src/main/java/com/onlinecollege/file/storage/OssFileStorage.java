package com.onlinecollege.file.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.file.config.OssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 阿里云 OSS 实现。
 *
 * <p>只在 {@code oss.enabled=true} 时由 {@link com.onlinecollege.file.config.StorageConfig} 装配。
 */
@Slf4j
@RequiredArgsConstructor
public class OssFileStorage implements FileStorage {

    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyy/MM/dd/");

    private final OSS ossClient;
    private final OssProperties properties;

    @Override
    public String upload(MultipartFile file, String dir) {
        validate(file);

        String normalizedDir = normalizeDir(dir);
        String objectKey = normalizedDir + DATE_DIR.format(LocalDate.now()) + randomFilename(file);

        try (InputStream in = file.getInputStream()) {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(file.getSize());
            if (StringUtils.hasText(file.getContentType())) {
                meta.setContentType(file.getContentType());
            }
            ossClient.putObject(properties.getBucketName(), objectKey, in, meta);
        } catch (Exception e) {
            log.error("OSS 上传失败 bucket={}, key={}", properties.getBucketName(), objectKey, e);
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }

        String url = buildUrl(objectKey);
        log.info("OSS 上传成功 bucket={}, key={}, url={}", properties.getBucketName(), objectKey, url);
        return url;
    }

    @Override
    public boolean delete(String objectKey) {
        try {
            ossClient.deleteObject(properties.getBucketName(), objectKey);
            return true;
        } catch (Exception e) {
            log.warn("OSS 删除失败 bucket={}, key={}", properties.getBucketName(), objectKey, e);
            return false;
        }
    }

    private String buildUrl(String objectKey) {
        String prefix = properties.getUrlPrefix();
        if (!StringUtils.hasText(prefix)) {
            prefix = "https://" + properties.getBucketName() + "." + properties.getEndpoint();
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix + objectKey;
    }

    private static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("上传文件不能为空");
        }
    }

    private static String normalizeDir(String dir) {
        if (!StringUtils.hasText(dir)) {
            return "";
        }
        String d = dir.startsWith("/") ? dir.substring(1) : dir;
        return d.endsWith("/") ? d : d + "/";
    }

    private static String randomFilename(MultipartFile file) {
        String original = file.getOriginalFilename();
        String ext = "";
        if (StringUtils.hasText(original) && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        return UUID.randomUUID().toString().replace("-", "") + ext;
    }
}
