# index-service 首页服务

负责 banner 管理和首页聚合数据，所有读取都带 Redis 缓存。

## 端口

`9091`，context-path = `/index-service`

## 核心接口

| Method | 路径 | 说明 |
|---|---|---|
| GET  | `/home`                    | **C 端**：首页聚合（banner + 热门课程 + 最新课程 + 推荐讲师） |
| POST | `/home/cache/evict`        | 后台主动失效首页缓存 |
| GET  | `/banners/active`          | C 端：当前生效的 banner（带缓存） |
| GET  | `/banners`                 | 后台：所有 banner |
| POST | `/banners`                 | 新增 banner（imageUrl 需先经 file-service 上传到 OSS） |
| PUT  | `/banners/{id}`            | 修改 banner |
| DELETE | `/banners/{id}`          | 删除 banner |

## Redis 缓存设计

- Key 前缀：`index:`
- 首页聚合：`index:home:v1`，TTL 默认 300 秒
- 生效 banner 列表：`index:banners:active:v1`，TTL 默认 600 秒
- 任意 banner 写操作（新增/修改/删除）会同时删除这两个 key，保证一致性
- 所有 Redis 调用都有异常兜底：Redis 不可达时直接穿透 DB，不让服务整体挂掉
- 配置开关 `index.cache.enabled=false` 时全部直连 DB，便于本地调试

## 依赖

- `file-service` 提供图片上传（返回 OSS URL，存到 `edu_banner.image_url`）
- 读 `edu_course` 和 `teacher` 表做聚合查询（教学简化，跨服务直连 DB；生产应该走 Feign）

## 数据库

[`src/main/resources/db/schema-banner.sql`](src/main/resources/db/schema-banner.sql)

## Redis 配置（可用环境变量覆盖）

```bash
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=
```
