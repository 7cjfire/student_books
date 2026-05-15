package com.onlinecollege.course.client;

import com.onlinecollege.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * file-service 熔断/降级实现：play-auth 拿不到时返回 503，不让详情接口整体挂掉。
 */
@Slf4j
@Component
public class FileServiceClientFallback implements FileServiceClient {

    @Override
    public Result<Map<String, Object>> getPlayAuth(String videoId) {
        log.warn("file-service 不可用，播放凭证降级返回: videoId={}", videoId);
        return Result.error(503, "视频服务暂不可用，请稍后重试");
    }
}
