package com.onlinecollege.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** HS256 密钥，必须 &gt;= 32 字节 */
    private String secret;

    private String issuer = "online-college";

    /** token 有效期（分钟） */
    private long expireMinutes = 720L;
}
