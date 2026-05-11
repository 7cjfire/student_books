package com.onlinecollege.index.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "index.cache")
public class IndexCacheProperties {

    /** Redis key 前缀 */
    private String keyPrefix = "index:";

    /** 首页聚合数据缓存 TTL（秒） */
    private long homeTtlSeconds = 300L;

    /** banner 列表缓存 TTL（秒） */
    private long bannerTtlSeconds = 600L;

    /** 缓存总开关，false 时全部穿透 DB */
    private boolean enabled = true;
}
