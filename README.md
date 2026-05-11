# 在线学院微服务项目（Online College Microservices）

一个基于 Spring Cloud 2021 + Spring Cloud Alibaba 的微服务练手/教学项目，覆盖：服务注册发现、网关鉴权与熔断限流、Nacos 配置中心、统一响应 / 全局异常、阿里云 OSS 文件上传、EasyExcel 批量导入等。

---

## 技术栈

| 分类 | 选型 |
|---|---|
| JDK | 17 |
| 构建 | Maven 3.6+ |
| 框架 | Spring Boot 2.7.18、Spring Cloud 2021.0.8、Spring Cloud Alibaba 2021.0.5.0 |
| 数据 | MySQL 8.0+、MyBatis-Plus 3.5.7、HikariCP |
| 注册中心 | Eureka Server（`book-service / file-service / api-gateway` 用）+ Nacos Discovery（`teacher-service / subject-service` 用） |
| 配置中心 | Nacos Config |
| 网关 | Spring Cloud Gateway（响应式） |
| 韧性 | Resilience4j（熔断 + 限流） |
| 文档 | Springdoc OpenAPI 3（Swagger UI） |
| 文件 | 阿里云 OSS SDK 3.17.4（可降级为本地磁盘） |
| Excel | EasyExcel 3.3.4 |
| 监控 | Spring Boot Actuator |
| 辅助 | Lombok |

---

## 模块总览

```
online-college-parent/
├── eureka-server/        8761   独立注册中心（可选，仅 Eureka 模式使用）
├── api-gateway/          8080   API 网关：鉴权 + 熔断 + 限流 + 降级
├── book-service/         8081   图书管理（Eureka）
├── teacher-service/      8084   教师管理（Nacos Config + Discovery）
├── subject-service/      8085   课程分类（Nacos Config + Discovery；支持树形结构/路径）
├── file-service/         8086   文件上传（阿里云 OSS / 本地磁盘）+ EasyExcel 导入 + 阿里云 VOD 凭证
├── course-service/       9090   课程（多步骤发布 + 章节/小节 + VOD 集成）
├── index-service/        9091   首页服务（banner + 聚合查询 + Redis 缓存）
├── service-provider/     8082   服务提供者示例（骨架）
├── service-consumer/     8083   服务消费者示例（骨架）
└── nacos-config/                Nacos 里应导入的 YAML 配置文件
```

### 统一响应格式

所有 HTTP 接口返回 `Result<T>`：

```json
{ "code": 200, "message": "操作成功", "data": {} }
```

- 业务异常 → 4xx + `Result`
- 参数校验失败 → 400 + `Result`，`message` 聚合所有字段错误
- 未捕获异常 → 500 + `Result("系统内部错误")`，真实堆栈只记录到日志

> `book-service / teacher-service / subject-service / file-service` 都已实现统一响应 + 全局异常处理。

---

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Eureka / Nacos（视模块而定，见下文）
- 可选：阿里云 OSS Bucket（没有可以用本地磁盘兜底）

---

## 数据库准备

```sql
CREATE DATABASE IF NOT EXISTS online_college
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE online_college;
```

### 图书表 `book`（book-service）

```sql
CREATE TABLE `book` (
  `id`           BIGINT AUTO_INCREMENT PRIMARY KEY,
  `book_name`    VARCHAR(100) NOT NULL,
  `author`       VARCHAR(50)  NOT NULL,
  `publisher`    VARCHAR(100),
  `publish_date` DATE,
  `price`        DECIMAL(10,2),
  `stock`        INT DEFAULT 0,
  `create_time`  DATE,
  `update_time`  DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 教师表 `teacher`（teacher-service）

```sql
CREATE TABLE `teacher` (
  `id`           BIGINT AUTO_INCREMENT PRIMARY KEY,
  `teacher_name` VARCHAR(50) NOT NULL,
  `teacher_no`   VARCHAR(30) NOT NULL UNIQUE,
  `gender`       VARCHAR(10),
  `age`          INT,
  `title`        VARCHAR(30),
  `department`   VARCHAR(50),
  `phone`        VARCHAR(20),
  `email`        VARCHAR(100),
  `hire_date`    DATE,
  `salary`       DECIMAL(10,2),
  `status`       INT DEFAULT 1,
  `create_time`  DATE,
  `update_time`  DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 课程分类表 `edu_subject`（subject-service / file-service 共用）

完整 SQL 见 [`subject-service/src/main/resources/db/schema-edu_subject.sql`](./subject-service/src/main/resources/db/schema-edu_subject.sql)。核心：

```sql
CREATE TABLE `edu_subject` (
  `id`          BIGINT       NOT NULL,                -- 主键（应用侧雪花算法生成）
  `title`       VARCHAR(50)  NOT NULL,
  `parent_id`   BIGINT       NOT NULL DEFAULT 0,      -- 0 表示一级分类
  `sort`        INT          NOT NULL DEFAULT 0,
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id`   (`parent_id`),
  KEY `idx_parent_sort` (`parent_id`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## Nacos 配置

`teacher-service` 和 `subject-service` 启动时会从 Nacos 拉配置。请在 Nacos 控制台创建 namespace `online-college`（group 用 `DEFAULT_GROUP`），并导入 [`nacos-config/`](./nacos-config) 下的所有 YAML 文件：

| Data ID | 作用域 |
|---|---|
| `common-config.yaml` | 所有服务共享：Jackson、日志、CORS、multipart |
| `datasource-config.yaml` | 所有服务共享：MySQL + HikariCP + MyBatis-Plus |
| `teacher-service-config.yaml` | `teacher-service` 独享：端口、discovery、业务规则、Resilience4j |
| `subject-service-config.yaml` | `subject-service` 独享：端口、discovery、业务规则、Resilience4j |

`book-service / file-service / api-gateway` 暂不依赖 Nacos，直接使用本地 `application.yml`。

---

## 启动顺序

```
1. MySQL                                       （所有数据服务前置）
2. Eureka Server         8761                  （book-service / file-service 注册到这里）
3. Nacos                 8848                  （teacher-service / subject-service 配置 + 注册）
4. book-service          8081
5. teacher-service       8084
6. subject-service       8085
7. file-service          8086
8. api-gateway           8080                  （最后启动，路由到以上所有服务）
9. service-provider / service-consumer         （可选示例模块）
```

用 IDE 逐个启动，或：

```bash
mvn -pl eureka-server      spring-boot:run
mvn -pl book-service       spring-boot:run
mvn -pl teacher-service    spring-boot:run
mvn -pl subject-service    spring-boot:run
mvn -pl file-service       spring-boot:run
mvn -pl api-gateway        spring-boot:run
```

---

## 网关路由表

经网关访问都走 `http://localhost:8080/**`，StripPrefix=1 会去掉 `/api`（文件服务去掉 `/api` 后到 `/upload/...`）。

| 外部路径 | 转发到 | 对应直连 |
|---|---|---|
| `/api/books/**`    | `lb://book-service`    | `http://localhost:8081/book-service/api/books/...` |
| `/api/teacher/**`  | `lb://teacher-service` | `http://localhost:8084/teacher-service/teacher/...` |
| `/api/subjects/**` | `lb://subject-service` | `http://localhost:8085/subject-service/api/subjects/...` |
| `/api/upload/**`   | `lb://file-service`    | `http://localhost:8086/file-service/upload/...` |
| `/api/vod/**`      | `lb://file-service`    | `http://localhost:8086/file-service/vod/...` (上传/播放凭证) |
| `/api/course/**`   | `lb://course-service`  | `http://localhost:9090/course-service/...` (RewritePath 保留 context-path) |
| `/api/index/**`    | `lb://index-service`   | `http://localhost:9091/index-service/...` (RewritePath 保留 context-path) |
| `/provider/**`     | `lb://service-provider` | `http://localhost:8082/provider/...` |
| `/consumer/**`     | `lb://service-consumer` | `http://localhost:8083/consumer/...` |

### 鉴权规则

- 非白名单请求必须携带请求头 `token: admin123`
- 白名单（前缀匹配）：
  - `book-service` 公开查询：`/api/books/page`、`/api/books/list`
  - `subject-service` 公开查询：`/api/subjects/tree`、`/api/subjects/first-level`、`/api/subjects/children/`、`/api/subjects/path/`
  - `course-service` 公开查询：`/api/course/courses/page`、`/api/course/courses/videos/`（播放凭证）
  - `index-service` 公开查询：`/api/index/home`、`/api/index/banners/active`
  - `/fallback/`、`/actuator/`
- 所有 POST/PUT/DELETE 一律要求 token
- `token` 目前是硬编码的教学用值；生产环境应换成 JWT / OAuth2

---

## API 使用示例

### book-service

```bash
# 分页（公开，不需要 token）
curl "http://localhost:8080/api/books/page?pageNum=1&pageSize=10"

# 新增（需要 token）
curl -X POST "http://localhost:8080/api/books" \
  -H "Content-Type: application/json" -H "token: admin123" \
  -d '{"bookName":"Java编程思想","author":"Bruce Eckel","publisher":"机械工业出版社","price":89.50,"stock":100}'

# 按 ID 查
curl "http://localhost:8080/api/books/1" -H "token: admin123"

# 按 ID 更新
curl -X PUT "http://localhost:8080/api/books/1" \
  -H "Content-Type: application/json" -H "token: admin123" \
  -d '{"bookName":"Java编程思想(第4版)","author":"Bruce Eckel","price":99.00,"stock":50}'

# 按 ID 删除
curl -X DELETE "http://localhost:8080/api/books/1" -H "token: admin123"

# 条件查询
curl "http://localhost:8080/api/books/list?bookName=Java&author=Bruce" -H "token: admin123"
```

### teacher-service

```bash
curl -X POST "http://localhost:8080/api/teacher/add" \
  -H "Content-Type: application/json" -H "token: admin123" \
  -d '{"teacherName":"张三","teacherNo":"T2023001","gender":"男","age":35,"title":"教授","department":"计算机学院","phone":"13800138000","email":"zhangsan@example.com","salary":15000.00}'

curl "http://localhost:8080/api/teacher/list?pageNum=1&pageSize=10"   -H "token: admin123"
curl "http://localhost:8080/api/teacher/search?teacherName=张"          -H "token: admin123"
curl -X DELETE "http://localhost:8080/api/teacher/delete/1"             -H "token: admin123"
```

### subject-service

```bash
# 树形结构（公开）
curl "http://localhost:8080/api/subjects/tree"

# 新增（需要 token）
curl -X POST "http://localhost:8080/api/subjects" \
  -H "Content-Type: application/json" -H "token: admin123" \
  -d '{"title":"前端开发","parentId":0,"sort":1}'

# 按 parentId 查子分类（公开）
curl "http://localhost:8080/api/subjects/children/0"

# 路径查询：从根到当前节点（公开）
curl "http://localhost:8080/api/subjects/path/1234567890"
```

### file-service

```bash
# 头像上传（走网关）
curl -F "file=@avatar.png" -H "token: admin123" \
  "http://localhost:8080/api/upload/avatar"

# 课程分类 Excel 上传并导入 edu_subject 表
curl -F "file=@subjects.xlsx" -H "token: admin123" \
  "http://localhost:8080/api/upload/course-catalog"

# VOD 上传凭证
curl -X POST "http://localhost:8080/api/vod/upload-auth" \
  -H "Content-Type: application/json" -H "token: admin123" \
  -d '{"title":"第一节视频","fileName":"lesson-1.mp4"}'

# VOD 播放凭证
curl "http://localhost:8080/api/vod/play-auth/<videoId>" -H "token: admin123"
```

### course-service（多步骤发布）

```bash
# 步骤1 创建
curl -X POST "http://localhost:8080/api/course/courses/step-info" \
  -H "Content-Type: application/json" -H "token: admin123" \
  -d '{"title":"Spring Cloud 实战","subjectId":2001,"subjectParentId":2,"teacherId":1,"price":99.00,"description":"..."}'

# 步骤2 新增章节
curl -X POST "http://localhost:8080/api/course/courses/<courseId>/chapters" \
  -H "Content-Type: application/json" -H "token: admin123" \
  -d '{"title":"第一章 入门","sort":1}'

# 步骤2 新增小节
curl -X POST "http://localhost:8080/api/course/courses/<courseId>/videos" \
  -H "Content-Type: application/json" -H "token: admin123" \
  -d '{"chapterId":123,"title":"1.1 环境搭建","sort":1}'

# 步骤2→3
curl -X POST "http://localhost:8080/api/course/courses/<courseId>/step-advance/video" \
  -H "token: admin123"

# 步骤3 绑定 VOD 视频（前端直传完成后回传 videoId）
curl -X POST "http://localhost:8080/api/course/courses/<courseId>/videos/<videoPk>/bind-vod" \
  -H "Content-Type: application/json" -H "token: admin123" \
  -d '{"videoId":"xxxxxx","videoDuration":620}'

# 步骤3→4 提交审核
curl -X POST "http://localhost:8080/api/course/courses/<courseId>/step-advance/review" \
  -H "token: admin123"

# 步骤4 审核通过
curl -X POST "http://localhost:8080/api/course/courses/<courseId>/review?publish=true" \
  -H "token: admin123"

# 课程详情（公开：章节+小节树）
curl "http://localhost:8080/api/course/courses/<courseId>/detail"
```

### index-service（首页 + banner）

```bash
# 首页聚合数据（公开，带 Redis 缓存）
curl "http://localhost:8080/api/index/home"

# 生效 banner 列表（公开，带缓存）
curl "http://localhost:8080/api/index/banners/active"

# 后台新增 banner（imageUrl 需先通过 /api/upload/avatar 等接口上传获得）
curl -X POST "http://localhost:8080/api/index/banners" \
  -H "Content-Type: application/json" -H "token: admin123" \
  -d '{"title":"新学期活动","imageUrl":"https://cdn.example.com/a.jpg","linkUrl":"https://...","sort":1,"enabled":1}'
```

Excel 模板列顺序：`[一级分类, 二级分类, 一级排序, 二级排序]`，表头会被跳过。详见 [`file-service/README.md`](./file-service/README.md)。

---

## 阿里云 OSS / VOD 配置

**OSS**：默认 `oss.enabled=false`，即走本地磁盘（`./uploads/`），方便本地联调。切到 OSS 时推荐用环境变量注入，**不要把 AccessKey 写到 yml 里提交到仓库**：

```bash
export OSS_ENABLED=true
export OSS_ENDPOINT=oss-cn-shanghai.aliyuncs.com
export OSS_ACCESS_KEY_ID=LTAI...
export OSS_ACCESS_KEY_SECRET=xxxx
export OSS_BUCKET=online-college-avatar
# 可选：自定义访问域名
# export OSS_URL_PREFIX=https://cdn.example.com
```

**VOD**：默认 `vod.enabled=false`，相关接口会返回 503 + 提示。启用：

```bash
export VOD_ENABLED=true
export VOD_REGION_ID=cn-shanghai
export VOD_ACCESS_KEY_ID=LTAI...
export VOD_ACCESS_KEY_SECRET=xxxx
# 可选
# export VOD_PLAY_AUTH_TIMEOUT=3000
# export VOD_DEFAULT_CATE_ID=0
```

> 视频数据由前端拿到 `uploadAddress + uploadAuth` 后**直传 VOD**，后端只负责发凭证，不承载视频流。

## Redis（首页缓存）

`index-service` 依赖 Redis。配置：

```bash
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=
```

Redis 不可达时 index-service 不会挂，所有查询会穿透到 DB（会在日志里打 warn）。

控制台侧（一次性动作）：
1. 创建 Bucket（演示场景可开"公共读"）
2. 记录 Endpoint / Bucket 名称
3. 创建子账号并授 OSS 读写权限，生成 AccessKeyId / Secret

---

## Swagger / 监控

| 服务 | Swagger UI |
|---|---|
| book-service    | `http://localhost:8081/book-service/swagger-ui.html` |
| teacher-service | `http://localhost:8084/teacher-service/swagger-ui.html` |
| subject-service | `http://localhost:8085/subject-service/swagger-ui.html` |
| file-service    | `http://localhost:8086/file-service/swagger-ui.html` |
| course-service  | `http://localhost:9090/course-service/swagger-ui.html` |
| index-service   | `http://localhost:9091/index-service/swagger-ui.html` |

Actuator：
- 注册中心：`http://localhost:8761`
- 网关：`http://localhost:8080/actuator/health`、`/circuitbreakers`、`/ratelimiters`

---

## 降级响应

| 情况 | 状态码 | 返回 |
|---|---|---|
| 下游熔断 | 503 | `{"code":503,"message":"xxx服务暂时不可用..."}` |
| 限流触发 | 429 | `{"code":429,"message":"请求过于频繁..."}` |

> 注：`FallbackController` 所有端点都支持 `GET/POST/PUT/DELETE`，保证非 GET 请求触发熔断时也能正确返回 503。

---

## 常见问题

**Q：`mvn install` 报 `book-service/file-service` 子模块不存在？**  
A：确认已经 pull 到最新代码（这两个模块目前都已实现）。

**Q：启动 teacher-service / subject-service 报 Nacos 连接失败？**  
A：这两个服务依赖 Nacos 加载配置。确认 Nacos 已在 `localhost:8848` 启动，且已导入 `nacos-config/` 下的 YAML。本地 `application.yml` 只含最小兜底，不完整。

**Q：token 正确但仍然 401？**  
A：检查路径是否和网关路由匹配；直连端口不会经过网关的鉴权。

**Q：分页查询返回了全部数据，没按 pageSize 截断？**  
A：老版本缺失 `PaginationInnerInterceptor`，现在 `book-service / teacher-service / subject-service` 都已注册分页插件。

---

## 项目进度

- [x] 父工程 + Eureka Server
- [x] API Gateway（鉴权 + 熔断 + 限流 + 降级 + 多路由）
- [x] book-service（完整 CRUD + 校验 + 全局异常 + 分页插件）
- [x] teacher-service（Nacos 配置 + CRUD + 校验 + 全局异常）
- [x] subject-service（树形结构 + 路径查询 + Nacos 配置 + 全局异常）
- [x] file-service（阿里云 OSS + 本地兜底 + EasyExcel 导入 edu_subject + 阿里云 VOD 凭证）
- [x] course-service（课程多步骤发布 + 章节/小节二级结构 + VOD 集成 + Feign 取播放凭证）
- [x] index-service（首页聚合 + banner 管理 + Redis 缓存 + 多级失效策略）
- [x] 全局异常 + Bean Validation + `Result<T>` 全模块统一
- [ ] 用户管理 / 订单管理

---

## 许可证

MIT
