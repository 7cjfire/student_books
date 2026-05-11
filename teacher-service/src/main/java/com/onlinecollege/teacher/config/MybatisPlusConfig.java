package com.onlinecollege.teacher.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

/**
 * MyBatis-Plus 配置
 *
 * <p>重要：之前缺少分页插件，导致 {@code page(...)} 不会拼 LIMIT，分页实际上是全表扫描。
 * 本类同时注册 {@link MetaObjectHandler}，支撑 {@code Teacher} 上的 {@code @TableField(fill=INSERT)}。
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDate today = LocalDate.now();
                this.strictInsertFill(metaObject, "createTime", LocalDate.class, today);
                this.strictInsertFill(metaObject, "updateTime", LocalDate.class, today);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDate.class, LocalDate.now());
            }
        };
    }
}
