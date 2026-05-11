package com.onlinecollege.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 限流器配置
 */
@Configuration
public class RateLimiterConfig {
    
    /**
     * 根据用户IP进行限流
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // 获取客户端IP地址
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }
    
    /**
     * 根据请求路径进行限流
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getPath().value();
            return Mono.just(path);
        };
    }
    
    /**
     * 根据用户ID进行限流（需要从token中解析）
     */
    @Bean
    public KeyResolver userIdKeyResolver() {
        return exchange -> {
            // 从请求头中获取token
            String token = exchange.getRequest().getHeaders().getFirst("token");
            String userId = "anonymous";
            
            if (token != null && !token.isEmpty()) {
                // 这里可以解析token获取用户ID
                // 简化处理：使用token作为用户标识
                userId = token;
            }
            
            return Mono.just(userId);
        };
    }
}