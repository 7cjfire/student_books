package com.onlinecollege.auth.service;

import com.onlinecollege.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 签发 / 校验。
 *
 * <p>claims:
 * <ul>
 *   <li>{@code sub}   : 用户 ID (String)</li>
 *   <li>{@code name} : 登录名</li>
 *   <li>{@code nick} : 昵称</li>
 *   <li>{@code roles}: 角色逗号串</li>
 * </ul>
 *
 * <p>密钥由 {@code jwt.secret} 提供，长度需 &ge; 32 字节；
 * 同一密钥需在 {@code api-gateway} 和本服务间共享。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;
    private SecretKey signingKey;

    @PostConstruct
    void init() {
        byte[] bytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret 必须至少 32 字节 (HS256)");
        }
        this.signingKey = Keys.hmacShaKeyFor(bytes);
    }

    public String issue(Long userId, String username, String nickName, String roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + properties.getExpireMinutes() * 60_000L);
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", username);
        claims.put("nick", nickName);
        claims.put("roles", roles == null ? "" : roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuer(properties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 解析并校验 token，失败抛 {@link JwtException} */
    public Claims parse(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .requireIssuer(properties.getIssuer())
                .build()
                .parseClaimsJws(token);
        return jws.getBody();
    }
}
