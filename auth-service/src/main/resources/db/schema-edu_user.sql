-- =========================================================
-- edu_user 后台用户表（运管登录账号）
--
-- 种子 admin 账号不在本 SQL 里用写死的 hash 插入（BCrypt 每次加盐不同），
-- 由 auth-service 启动时的 DataInitializer 检测到空表后自动创建：
--   username = admin, password = admin123, roles = ADMIN
-- 第一次登录后请在运管里修改密码。
-- =========================================================
USE `online_college`;

DROP TABLE IF EXISTS `edu_user`;

CREATE TABLE `edu_user` (
  `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username`      VARCHAR(64)  NOT NULL                             COMMENT '登录名',
  `password`      VARCHAR(128) NOT NULL                             COMMENT 'BCrypt 加密后的密码',
  `nick_name`     VARCHAR(64)  DEFAULT NULL                         COMMENT '昵称',
  `roles`         VARCHAR(128) DEFAULT 'VIEWER'                     COMMENT '角色，逗号分隔',
  `avatar`        VARCHAR(512) DEFAULT NULL                         COMMENT '头像 URL',
  `enabled`       TINYINT      NOT NULL DEFAULT 1                   COMMENT '1=启用 0=禁用',
  `last_login_at` DATETIME     DEFAULT NULL                         COMMENT '上次登录时间',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT      NOT NULL DEFAULT 0                   COMMENT '逻辑删除',
  UNIQUE KEY `uk_username` (`username`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台用户';
