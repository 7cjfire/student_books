USE `online_college`;

DROP TABLE IF EXISTS `edu_comment`;

CREATE TABLE `edu_comment` (
  `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
  `course_id`   BIGINT       NOT NULL                 COMMENT '课程 ID',
  `user_id`     BIGINT       DEFAULT NULL             COMMENT '评论者用户 ID',
  `nickname`    VARCHAR(64)  DEFAULT '匿名用户'       COMMENT '评论者昵称',
  `avatar`      VARCHAR(512) DEFAULT NULL             COMMENT '评论者头像',
  `content`     VARCHAR(1024) NOT NULL                COMMENT '评论内容',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  KEY `idx_course_time` (`course_id`, `create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程评论';
