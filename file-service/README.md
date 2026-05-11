# file-service 文件服务

负责两类上传：

| 接口 | 用途 | 限制 |
|---|---|---|
| `POST /upload/avatar` | 头像上传 | 5MB；jpg/jpeg/png/gif/webp |
| `POST /upload/course-catalog` | 课程分类 Excel 上传并写入 `edu_subject` 表 | 10MB；xls/xlsx |

## 运行端口

`8086`，context-path = `/file-service`

直连：`http://localhost:8086/file-service/upload/avatar`  
经网关（需在 api-gateway 补路由）：`http://localhost:8080/api/upload/avatar`

## 存储模式

- `oss.enabled=true`（生产 / 演示）→ 上传到阿里云 OSS，返回 OSS 域名 URL
- `oss.enabled=false`（默认 / 本地）→ 存到 `oss.local.root`（默认 `./uploads`），通过 `GET /file-service/local/**` 读取，方便没有 AccessKey 的本地联调

## 配置方式（推荐环境变量，不要写死到 yml）

```bash
export OSS_ENABLED=true
export OSS_ENDPOINT=oss-cn-shanghai.aliyuncs.com
export OSS_ACCESS_KEY_ID=LTAI...
export OSS_ACCESS_KEY_SECRET=xxxx
export OSS_BUCKET=online-college-avatar
# 可选：自定义域名
# export OSS_URL_PREFIX=https://cdn.example.com
```

## Excel 导入格式

见 `SubjectExcelRow` 注释；列顺序：

```
| 一级分类 | 二级分类 | 一级排序 | 二级排序 |
| 前端开发 | Vue     | 1       | 1       |
| 前端开发 | React   | 1       | 2       |
| 后端开发 | Java    | 2       | 1       |
```

- 表头第一行会被跳过
- 同一一级分类名称会自动去重，只插入一次
- 二级分类列为空时，本行只新建一级分类

## curl 快速验证

```bash
# 头像
curl -F "file=@avatar.png" http://localhost:8086/file-service/upload/avatar

# 课程分类 Excel
curl -F "file=@subjects.xlsx" http://localhost:8086/file-service/upload/course-catalog
```

## 与 subject-service 的关系

为保持演示简单，本服务直接连 `online_college.edu_subject` 表写数据，Entity 与 subject-service 共享一张表。
分类的 CRUD 在 `subject-service` 里，本服务只负责"把 Excel 里的数据倒进这张表"。
