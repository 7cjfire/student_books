package com.onlinecollege.index.cache;

import com.onlinecollege.index.config.IndexCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * 首页热点数据缓存包装。
 *
 * <p>策略：
 * <ul>
 *   <li>读：先查 Redis，命中则返回；未命中调用 {@code loader} 从 DB 取数，回写 Redis 后返回。</li>
 *   <li>Redis 异常：吞掉异常、直接穿透 DB，保证首页接口不会因 Redis 故障整体挂掉。</li>
 *   <li>写（banner 变更等）：调用 {@link #evict(String)} 删除 key。</li>
 *   <li>全局开关：{@code index.cache.enabled=false} 时完全不走 Redis。</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HotDataCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private final IndexCacheProperties properties;

    /**
     * 读缓存或回源。
     *
     * @param key      Redis key（不含前缀）
     * @param ttl      TTL
     * @param loader   回源函数（一般是 DB 查询）
     * @return 最终值
     */
    public <T> T getOrLoad(String key, Duration ttl, Callable<T> loader) {
        String fullKey = properties.getKeyPrefix() + key;
        if (!properties.isEnabled()) {
            return call(loader);
        }
        // 1) 读
        Object cached;
        try {
            cached = redisTemplate.opsForValue().get(fullKey);
        } catch (Exception e) {
            log.warn("Redis 读失败, 穿透 DB: key={}, err={}", fullKey, e.getMessage());
            return call(loader);
        }
        if (cached != null) {
            log.debug("缓存命中: {}", fullKey);
            @SuppressWarnings("unchecked")
            T t = (T) cached;
            return t;
        }

        // 2) 回源
        T value = call(loader);
        if (value == null) {
            return null;
        }
        try {
            redisTemplate.opsForValue().set(fullKey, value, ttl);
            log.debug("缓存回写: {}, ttl={}s", fullKey, ttl.toSeconds());
        } catch (Exception e) {
            log.warn("Redis 回写失败: key={}, err={}", fullKey, e.getMessage());
        }
        return value;
    }

    /** 主动删除缓存（后台写操作发生时调用） */
    public void evict(String key) {
        String fullKey = properties.getKeyPrefix() + key;
        try {
            Boolean deleted = redisTemplate.delete(fullKey);
            log.debug("缓存失效: {}, deleted={}", fullKey, deleted);
        } catch (Exception e) {
            log.warn("Redis 删除失败: key={}, err={}", fullKey, e.getMessage());
        }
    }

    public void evictAll(String... keys) {
        for (String k : keys) {
            evict(k);
        }
    }

    private static <T> T call(Callable<T> loader) {
        try {
            return loader.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
