package com.onlinecollege.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局鉴权过滤器
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    
    // 白名单路径（不需要鉴权的路径）
    private static final List<String> WHITE_LIST = List.of(
            "/api/books/page",
            "/api/books/list",
            "/api/books/{id}",
            "/fallback/",
            "/actuator/"
    );
    
    // 认证token
    private static final String AUTH_TOKEN = "admin123";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        
        // 检查是否为白名单路径
        if (isWhiteListPath(path)) {
            log.debug("白名单路径放行: {}", path);
            return chain.filter(exchange);
        }
        
        // 获取token
        String token = request.getHeaders().getFirst("token");
        
        // 验证token
        if (!StringUtils.hasText(token) || !AUTH_TOKEN.equals(token)) {
            log.warn("鉴权失败: path={}, token={}", path, token);
            return unauthorizedResponse(exchange, "未授权访问，请提供有效的token");
        }
        
        log.debug("鉴权通过: path={}", path);
        return chain.filter(exchange);
    }
    
    /**
     * 检查是否为白名单路径
     */
    private boolean isWhiteListPath(String path) {
        return WHITE_LIST.stream().anyMatch(whitePath -> 
            path.startsWith(whitePath.replace("{id}", "")) || 
            path.contains(whitePath)
        );
    }
    
    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        String responseBody = String.format("{\"code\": 401, \"message\": \"%s\"}", message);
        DataBuffer buffer = response.bufferFactory()
                .wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }
    
    @Override
    public int getOrder() {
        // 设置过滤器执行顺序，数字越小优先级越高
        return -1;
    }
}