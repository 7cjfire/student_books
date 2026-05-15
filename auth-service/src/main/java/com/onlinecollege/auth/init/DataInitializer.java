package com.onlinecollege.auth.init;

import com.onlinecollege.auth.entity.EduUser;
import com.onlinecollege.auth.mapper.EduUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 启动时种子数据：如果 edu_user 里没有任何记录，自动创建超级管理员。
 *
 * <p>用户名 {@code admin} / 密码 {@code admin123}；首次登录后请立即修改。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";
    private static final String DEFAULT_NICK     = "超级管理员";
    private static final String DEFAULT_ROLES    = "ADMIN";

    private final EduUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner seedAdmin() {
        return args -> {
            Long count = userMapper.selectCount(null);
            if (count != null && count > 0) {
                return;
            }
            EduUser admin = new EduUser();
            admin.setUsername(DEFAULT_USERNAME);
            admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            admin.setNickName(DEFAULT_NICK);
            admin.setRoles(DEFAULT_ROLES);
            admin.setEnabled(1);
            userMapper.insert(admin);
            log.warn("[SEED] 已创建默认管理员：{} / {}（请尽快修改密码）",
                    DEFAULT_USERNAME, DEFAULT_PASSWORD);
        };
    }
}
