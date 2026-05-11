USE `online_college`;

DROP TABLE IF EXISTS `edu_banner`;

CREATE TABLE `edu_banner` (
  `id`          BIGINT AUTO_INCREMENT PRIMARY KEY                COMMENT 'banner ID',
  `title`       VARCHAR(128) NOT NULL                            COMMENT '标题',
  `image_url`   VARCHAR(512) NOT NULL                            COMMENT '图片 URL（OSS 地址）',
  `link_url`    VARCHAR(512) DEFAULT NULL                        COMMENT '点击跳转 URL',
  `sort`        INT          NOT NULL DEFAULT 0                  COMMENT '排序，越小越靠前',
  `enabled`     TINYINT      NOT NULL DEFAULT 1                  COMMENT '启用：0=否 1=是',
  `start_time`  DATETIME     DEFAULT NULL                        COMMENT '生效开始时间',
  `end_time`    DATETIME     DEFAULT NULL                        COMMENT '生效结束时间',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                          COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0                  COMMENT '逻辑删除',
  KEY `idx_enabled_sort` (`enabled`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页 Banner';
