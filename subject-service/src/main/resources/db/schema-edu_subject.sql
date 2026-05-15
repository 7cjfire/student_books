-- =========================================================
-- edu_subject 课程分类表
-- 支持两级（或更多）树形分类：parent_id = 0 表示一级分类
-- 主键使用 MyBatis-Plus 雪花算法分配（应用侧生成）
-- =========================================================

CREATE DATABASE IF NOT EXISTS `online_college`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `online_college`;

DROP TABLE IF EXISTS `edu_subject`;

CREATE TABLE `edu_subject` (
  `id`          BIGINT       NOT NULL                    COMMENT '主键 ID（雪花算法，应用侧分配）',
  `title`       VARCHAR(50)  NOT NULL                    COMMENT '类别名称',
  `parent_id`   BIGINT       NOT NULL DEFAULT 0          COMMENT '父级 ID，0 代表一级分类',
  `sort`        INT          NOT NULL DEFAULT 0          COMMENT '排序字段，越小越靠前',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                          COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_parent_sort` (`parent_id`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程分类表';

-- 示例数据（可选，方便联调）
-- INSERT INTO `edu_subject` (`id`, `title`, `parent_id`, `sort`) VALUES
--   (1, '前端开发',    0, 1),
--   (2, '后端开发',    0, 2),
--   (11, 'Vue',       1, 1),
--   (12, 'React',     1, 2),
--   (21, 'Java',      2, 1),
--   (22, 'Spring',    2, 2);
