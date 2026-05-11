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
 *
 * <p>非白名单路径必须在请求头携带 {@code token: admin123} 才放行。
 * 白名单用"前缀匹配"实现，支持 {@code /api/books/{id}} 风格的占位符（内部按前缀比较，
 * 占位符后面的任意字符都放行）。
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 白名单路径前缀。所有以下面任意一项为前缀的请求不需要携带 token。
     *
     * <p>GET 类的公开查询接口放在这里，写操作（POST/PUT/DELETE）一律要求鉴权。
     * 注意：这是简单的前缀匹配，实际项目里应该结合 HttpMethod 一起判断。
     */
    private static final List<String> WHITE_LIST_PREFIXES = List.of(
            // book-service 公开查询
            "/api/books/page",
            "/api/books/list",
            // subject-service 公开查询（树形、一级、子分类、路径）
            "/api/subjects/tree",
            "/api/subjects/first-level",
            "/api/subjects/children/",
            "/api/subjects/path/",
            // 熔断降级 & 健康检查
            "/fallback/",
            "/actuator/"
    );

    /** 认证 token（教学项目硬编码，生产环境应替换为 JWT/OAuth2） */
    private static final String AUTH_TOKEN = "admin123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (isWhiteListPath(path)) {
            log.debug("白名单路径放行: {}", path);
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst("token");
        if (!StringUtils.hasText(token) || !AUTH_TOKEN.equals(token)) {
            log.warn("鉴权失败: path={}, token={}", path, token);
            return unauthorizedResponse(exchange, "未授权访问，请提供有效的 token");
        }

        log.debug("鉴权通过: path={}", path);
        return chain.filter(exchange);
    }

    /**
     * 判断是否为白名单路径：任意前缀匹配即放行。
     */
    private boolean isWhiteListPath(String path) {
        if (!StringUtils.hasText(path)) {
            return false;
        }
        for (String prefix : WHITE_LIST_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 简易 JSON（避免引入 Jackson 依赖），message 里不应包含 " 或换行
        String body = String.format(
                "{\"code\":401,\"message\":\"%s\",\"data\":null}",
                message.replace("\"", "'"));
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
