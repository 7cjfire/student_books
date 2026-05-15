package com.onlinecollege.course.client;

import com.onlinecollege.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 调 file-service 拿 VOD 播放凭证的 Feign Client。
 *
 * <p>只用到播放凭证这一个接口；上传凭证前端直接调 file-service。
 * 配置熔断降级在 {@link FileServiceClientFallback}。
 */
@FeignClient(name = "file-service", path = "/file-service",
        fallback = FileServiceClientFallback.class)
public interface FileServiceClient {

    /**
     * 获取视频播放凭证。
     *
     * <p>使用 {@code Map<String, Object>} 作为 data 类型避免把 file-service 的 DTO
     * 再抽到 common 模块，保持服务解耦。
     */
    @GetMapping("/vod/play-auth/{videoId}")
    Result<Map<String, Object>> getPlayAuth(@PathVariable("videoId") String videoId);
}
