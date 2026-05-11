package com.onlinecollege.file.storage;

import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.file.config.OssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地磁盘存储实现。当 {@code oss.enabled=false} 时启用，方便没有阿里云 AccessKey
 * 的本地开发环境联调整个上传流程。
 *
 * <p>文件保存到 {@link OssProperties.Local#getRoot()} 指定目录，并在 HTTP 层通过
 * {@code LocalFileController} 提供只读访问。
 */
@Slf4j
@RequiredArgsConstructor
public class LocalFileStorage implements FileStorage {

    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyy/MM/dd/");

    private final OssProperties properties;

    @Override
    public String upload(MultipartFile file, String dir) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("上传文件不能为空");
        }

        String normalizedDir = normalizeDir(dir);
        String relative = normalizedDir + DATE_DIR.format(LocalDate.now()) + randomFilename(file);
        Path root = Paths.get(properties.getLocal().getRoot()).toAbsolutePath().normalize();
        Path target = root.resolve(relative).normalize();

        // 防止路径穿越
        if (!target.startsWith(root)) {
            throw BusinessException.badRequest("非法的存储路径");
        }

        try {
            Files.createDirectories(target.getParent());
            try (var in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            log.error("本地存储写入失败 path={}", target, e);
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }

        String urlPrefix = properties.getLocal().getUrlPrefix();
        if (!urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix + "/";
        }
        String url = urlPrefix + relative;
        log.info("本地存储成功 path={}, url={}", target, url);
        return url;
    }

    @Override
    public boolean delete(String objectKey) {
        try {
            Path root = Paths.get(properties.getLocal().getRoot()).toAbsolutePath().normalize();
            Path target = root.resolve(objectKey).normalize();
            if (!target.startsWith(root)) {
                return false;
            }
            return Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("本地存储删除失败 key={}", objectKey, e);
            return false;
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
