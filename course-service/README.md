# course-service 课程服务

负责课程多步骤发布、章节/小节二级结构、与 file-service 的 VOD 集成。

## 端口

`9090`，context-path = `/course-service`

## 多步骤发布

课程状态机：

```
DRAFT --(step2 完成)--> CHAPTER_PENDING? (预留) 
DRAFT --(advance-to-video)--> VIDEO_PENDING --(advance-to-review)--> REVIEWING
REVIEWING --(review publish=true)--> PUBLISHED
REVIEWING --(review publish=false)--> UNPUBLISHED
UNPUBLISHED --(可再次编辑)--> DRAFT
```

## REST 接口一览

| Method | 路径 | 说明 |
|---|---|---|
| POST | `/courses/step-info`                          | 步骤1 创建课程草稿 |
| PUT  | `/courses/{id}/step-info`                     | 步骤1 更新基本信息 |
| POST | `/courses/{id}/chapters`                      | 步骤2 新增章节 |
| POST | `/courses/{id}/videos`                        | 步骤2 新增小节 |
| POST | `/courses/{id}/step-advance/video`            | 步骤2→3 |
| POST | `/courses/{id}/videos/{videoPk}/bind-vod`     | 步骤3 绑定 VOD 视频 |
| POST | `/courses/{id}/step-advance/review`           | 步骤3→4 提交审核 |
| POST | `/courses/{id}/review?publish=true|false`     | 步骤4 审核结果 |
| GET  | `/courses/{id}/detail`                        | 课程完整结构 |
| GET  | `/courses/videos/{videoId}/play-auth`         | 获取播放凭证（经 Feign 调 file-service） |

## 数据库

执行 [`src/main/resources/db/schema-course.sql`](src/main/resources/db/schema-course.sql) 建表。

## 依赖

- `file-service` 提供 VOD 播放凭证（Feign 调用，有熔断降级）
- `teacher-service` / `subject-service` 仅存外键 ID，不做跨服务一致性检查（演示项目简化）
