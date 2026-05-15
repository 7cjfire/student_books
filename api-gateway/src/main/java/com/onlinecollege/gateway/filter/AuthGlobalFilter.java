package com.onlinecollege.gateway.filter;

import com.onlinecollege.gateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局鉴权过滤器（JWT 版）。
 *
 * <p>工作流程：
 * <ol>
 *   <li>OPTIONS 预检请求直接放行（由 {@code GatewayCorsConfig} 统一处理 CORS）</li>
 *   <li>白名单路径（登录、公开查询、降级、健康检查）放行</li>
 *   <li>其它请求必须在 {@code Authorization: Bearer <jwt>} 里带有效 token</li>
 *   <li>解析出用户 ID / 用户名后，通过 {@code X-User-Id / X-User-Name / X-User-Roles}
 *       三个请求头透传给下游服务</li>
 * </ol>
 *
 * <p>老的硬编码 {@code token: admin123} 方案已移除。
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /** 白名单路径前缀（任一前缀匹配即放行） */
    private static final List<String> WHITE_LIST_PREFIXES = List.of(
            // 登录 / 登出 / 健康检查
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/ping",
            // book-service 公开查询
            "/api/books/page",
            "/api/books/list",
            // subject-service 公开查询
            "/api/subjects/tree",
            "/api/subjects/first-level",
            "/api/subjects/children/",
            "/api/subjects/path/",
            // course-service 公开（C 端）
            "/api/course/courses/page",
            "/api/course/courses/videos/",
            // index-service 公开
            "/api/index/home",
            "/api/index/banners/active",
            // 熔断降级 & 健康检查
            "/fallback/",
            "/actuator/"
    );

    @Autowired
    private JwtProperties jwtProperties;

    private SecretKey signingKey;

    @PostConstruct
    void init() {
        if (!StringUtils.hasText(jwtProperties.getSecret())) {
            throw new IllegalStateException("jwt.secret 未配置");
        }
        byte[] bytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret 必须至少 32 字节 (HS256)");
        }
        this.signingKey = Keys.hmacShaKeyFor(bytes);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1) CORS 预检直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethodValue())) {
            return chain.filter(exchange);
        }

        String path = request.getPath().value();

        // 2) 白名单
        if (isWhiteListPath(path)) {
            log.debug("白名单放行: {}", path);
            return chain.filter(exchange);
        }

        // 3) 解析 Authorization
        String auth = request.getHeaders().getFirst("Authorization");
        if (!StringUtils.hasText(auth) || !auth.startsWith("Bearer ")) {
            log.warn("缺少 Bearer token: path={}", path);
            return unauthorized(exchange, "未登录或 token 已过期");
        }
        String token = auth.substring("Bearer ".length()).trim();

        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .requireIssuer(jwtProperties.getIssuer())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.warn("token 校验失败: path={}, err={}", path, e.getMessage());
            return unauthorized(exchange, "token 无效或已过期");
        }

        String userId   = claims.getSubject();
        String username = asString(claims.get("name"));
        String roles    = asString(claims.get("roles"));

        // 4) 透传给下游
        ServerHttpRequest mutated = request.mutate()
                .header("X-User-Id",    userId == null ? "" : userId)
                .header("X-User-Name",  username == null ? "" : username)
                .header("X-User-Roles", roles == null ? "" : roles)
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isWhiteListPath(String path) {
        if (!StringUtils.hasText(path)) return false;
        for (String p : WHITE_LIST_PREFIXES) {
            if (path.startsWith(p)) return true;
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format(
                "{\"code\":401,\"message\":\"%s\",\"data\":null}",
                message.replace("\"", "'"));
        DataBuffer buf = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buf));
    }

    private static String asString(Object o) {
        return o == null ? null : o.toString();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
