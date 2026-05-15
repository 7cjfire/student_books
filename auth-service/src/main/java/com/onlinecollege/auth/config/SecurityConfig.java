package com.onlinecollege.auth.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * 只注册 BCrypt 密码编码器（不引入完整 Spring Security 过滤链）。
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override public void insertFill(MetaObject m) {
                LocalDateTime now = LocalDateTime.now();
                this.strictInsertFill(m, "createTime", LocalDateTime.class, now);
                this.strictInsertFill(m, "updateTime", LocalDateTime.class, now);
            }
            @Override public void updateFill(MetaObject m) {
                this.strictUpdateFill(m, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
