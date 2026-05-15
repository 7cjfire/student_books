package com.onlinecollege.file.controller;

import com.onlinecollege.file.config.OssProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地存储模式下的只读文件访问端点。
 *
 * <p>仅当 {@code oss.enabled=false} 时有意义；真正上传到阿里云 OSS 时，文件 URL
 * 指向 OSS 域名，不经过本服务。
 */
@Slf4j
@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
@Tag(name = "本地文件访问", description = "本地存储模式下的只读文件下载端点")
public class LocalFileController {

    private final OssProperties properties;

    @GetMapping("/**")
    @Operation(summary = "读取本地文件")
    public ResponseEntity<?> getFile(HttpServletRequest request) {
        // 获取 /local/ 之后的相对路径
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (fullPath == null) {
            fullPath = request.getRequestURI();
        }
        int idx = fullPath.indexOf("/local/");
        String relative = idx >= 0 ? fullPath.substring(idx + "/local/".length()) : "";

        Path root = Paths.get(properties.getLocal().getRoot()).toAbsolutePath().normalize();
        Path file = root.resolve(relative).normalize();
        if (!file.startsWith(root) || !Files.exists(file) || Files.isDirectory(file)) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = MediaTypeFactory.getMediaType(file.getFileName().toString())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .body(new FileSystemResource(file.toFile()));
    }
}
