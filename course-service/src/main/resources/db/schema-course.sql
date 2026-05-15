-- =========================================================
-- 课程服务相关表
-- 主键一律用 MyBatis-Plus ASSIGN_ID（雪花算法，应用侧生成）
-- =========================================================
USE `online_college`;

DROP TABLE IF EXISTS `edu_video`;
DROP TABLE IF EXISTS `edu_chapter`;
DROP TABLE IF EXISTS `edu_course`;

-- 课程主表 -----------------------------------------------------
CREATE TABLE `edu_course` (
  `id`                 BIGINT       NOT NULL                     COMMENT '主键（雪花算法）',
  `title`              VARCHAR(128) NOT NULL                     COMMENT '课程标题',
  `cover`              VARCHAR(512) DEFAULT NULL                 COMMENT '课程封面 URL（OSS）',
  `subject_id`         BIGINT       DEFAULT NULL                 COMMENT '二级分类 ID',
  `subject_parent_id`  BIGINT       DEFAULT NULL                 COMMENT '一级分类 ID（冗余便于聚合）',
  `teacher_id`         BIGINT       DEFAULT NULL                 COMMENT '讲师 ID',
  `price`              DECIMAL(10,2) DEFAULT 0.00                COMMENT '课程价格',
  `lesson_num`         INT          NOT NULL DEFAULT 0           COMMENT '小节总数（冗余）',
  `description`        VARCHAR(1024) DEFAULT NULL                COMMENT '课程简介',
  `status`             VARCHAR(32)  NOT NULL DEFAULT 'DRAFT'     COMMENT '状态：DRAFT/CHAPTER_PENDING/VIDEO_PENDING/REVIEWING/PUBLISHED/UNPUBLISHED',
  `view_count`         BIGINT       NOT NULL DEFAULT 0           COMMENT '浏览量',
  `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                           COMMENT '创建时间',
  `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`            TINYINT      NOT NULL DEFAULT 0           COMMENT '逻辑删除：0=未删，1=已删',
  PRIMARY KEY (`id`),
  KEY `idx_status_view` (`status`, `view_count`),
  KEY `idx_subject`     (`subject_id`),
  KEY `idx_teacher`     (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程主表';

-- 章节表（一级菜单）--------------------------------------------
CREATE TABLE `edu_chapter` (
  `id`          BIGINT       NOT NULL                 COMMENT '主键（雪花算法）',
  `course_id`   BIGINT       NOT NULL                 COMMENT '所属课程 ID',
  `title`       VARCHAR(128) NOT NULL                 COMMENT '章节标题',
  `sort`        INT          NOT NULL DEFAULT 0       COMMENT '章节排序',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                          COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_course_sort` (`course_id`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程章节表';

-- 小节表（二级菜单，挂 VOD 视频）------------------------------
CREATE TABLE `edu_video` (
  `id`                BIGINT       NOT NULL                 COMMENT '主键（雪花算法）',
  `course_id`         BIGINT       NOT NULL                 COMMENT '所属课程 ID（冗余便于查询）',
  `chapter_id`        BIGINT       NOT NULL                 COMMENT '所属章节 ID',
  `title`             VARCHAR(128) NOT NULL                 COMMENT '小节标题',
  `sort`              INT          NOT NULL DEFAULT 0       COMMENT '小节排序',
  `video_id`          VARCHAR(128) DEFAULT NULL             COMMENT '阿里云 VOD 视频 ID',
  `video_source_url`  VARCHAR(1024) DEFAULT NULL            COMMENT '视频源地址（可空）',
  `video_duration`    INT          DEFAULT NULL             COMMENT '视频时长（秒）',
  `is_free`           TINYINT      NOT NULL DEFAULT 0       COMMENT '是否免费试看：0=否 1=是',
  `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                          COMMENT '创建时间',
  `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`           TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_chapter_sort` (`chapter_id`, `sort`),
  KEY `idx_course`       (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程小节表（视频）';
