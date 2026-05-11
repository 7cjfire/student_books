# 在线学院微服务项目 (Online College Microservices)

一个完整的Java微服务项目，采用Spring Cloud微服务架构，包含服务注册发现、API网关、限流熔断、统一认证等功能。

## 项目概述

本项目是一个"在线学院"系统的微服务实现，采用模块化设计，每个服务独立部署，通过服务注册中心进行服务发现和调用。

## 技术栈

### 后端技术
- **Java**: 17
- **Spring Boot**: 2.7.18
- **Spring Cloud**: 2021.0.8
- **Spring Cloud Alibaba**: 2021.0.5.0

### 数据访问
- **MySQL**: 8.0.33
- **MyBatis-Plus**: 3.5.7

### 微服务组件
- **服务注册与发现**: Eureka Server / Nacos
- **配置中心**: Nacos Config
- **API网关**: Spring Cloud Gateway
- **限流熔断**: Resilience4j
- **服务调用**: OpenFeign + LoadBalancer

### 接口文档与监控
- **API文档**: Springdoc / OpenAPI 3.0
- **监控**: Spring Boot Actuator

### 统一规范
- 所有API返回结果封装在统一的`Result<T>`类中
- 包含`code`、`message`、`data`三个字段

## 项目结构

```
online-college-parent/                    # 父工程 (Maven聚合项目)
├── api-gateway/                         # API网关服务 (端口: 8080)
│   ├── 全局鉴权过滤器 (AuthGlobalFilter)
│   ├── 路由转发配置
│   ├── Resilience4j断路器限流配置
│   └── 降级处理控制器
├── book-service/                        # 图书管理服务 (端口: 8081)
│   ├── 完整的CRUD操作
│   ├── MyBatis-Plus数据访问
│   ├── Swagger API文档
│   └── 统一异常处理
├── teacher-service/                     # 教师管理服务 (端口: 8084)
│   ├── 教师CRUD操作
│   ├── Nacos配置中心集成
│   ├── MyBatis-Plus数据访问
│   └── 统一异常处理
├── eureka-server/                      # 服务注册中心 (端口: 8761)
├── service-provider/                    # 服务提供者示例 (端口: 8082)
├── service-consumer/                    # 服务消费者示例 (端口: 8083)
└── nacos-config/                       # Nacos配置文件
    ├── common-config.yaml              # 通用配置
    ├── datasource-config.yaml          # 数据源配置
    └── teacher-service-config.yaml     # 教师服务专用配置
```

## 环境要求

### 系统要求
- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Git**: 2.0+

### 数据库配置
1. 创建数据库：
```sql
CREATE DATABASE online_college CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 创建图书表：
```sql
CREATE TABLE book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_name VARCHAR(100) NOT NULL,
    author VARCHAR(50) NOT NULL,
    publisher VARCHAR(100),
    publish_date DATE,
    price DECIMAL(10,2),
    stock INT DEFAULT 0,
    create_time DATE,
    update_time DATE
);
```

3. 创建教师表：
```sql
CREATE TABLE teacher (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_name VARCHAR(50) NOT NULL,
    teacher_no VARCHAR(20) NOT NULL UNIQUE,
    gender VARCHAR(10),
    age INT,
    title VARCHAR(50),
    department VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    hire_date DATE,
    salary DECIMAL(10,2),
    status INT DEFAULT 1,
    create_time DATE,
    update_time DATE
);
```

## 启动步骤

### 1. 克隆项目
```bash
git clone https://github.com/7cjfire/student_books.git
cd student_books
```

### 2. 配置数据库
修改 `book-service/src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_college?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
```

### 3. 启动Nacos（可选）
```bash
# 下载Nacos: https://github.com/alibaba/nacos/releases
# 解压后进入bin目录
cd nacos/bin

# Windows启动单机模式
startup.cmd -m standalone

# Linux/Mac启动单机模式
sh startup.sh -m standalone

# 访问Nacos控制台: http://localhost:8848
# 用户名/密码: nacos/nacos
```

### 4. 启动服务（按顺序）

#### 方式一：使用IDE启动
1. **启动Eureka Server** (`eureka-server`)
   - 端口: 8761
   - 访问: http://localhost:8761

2. **启动Book Service** (`book-service`)
   - 端口: 8081
   - Swagger文档: http://localhost:8081/book-service/swagger-ui.html

3. **启动Teacher Service** (`teacher-service`)
   - 端口: 8084
   - Nacos配置: 从Nacos获取配置
   - Swagger文档: http://localhost:8084/teacher-service/swagger-ui.html

4. **启动API Gateway** (`api-gateway`)
   - 端口: 8080
   - 网关入口: http://localhost:8080

5. **启动Service Provider** (`service-provider`) - 可选
   - 端口: 8082

6. **启动Service Consumer** (`service-consumer`) - 可选
   - 端口: 8083

#### 方式二：使用Maven命令启动
```bash
# 进入各模块目录，分别启动
cd eureka-server
mvn spring-boot:run

cd ../book-service
mvn spring-boot:run

cd ../teacher-service
mvn spring-boot:run

cd ../api-gateway
mvn spring-boot:run
```

## API使用示例

### 1. 通过网关访问图书服务

#### 查询所有图书（分页）
```bash
curl -X GET "http://localhost:8080/api/books/page?pageNum=1&pageSize=10" \
  -H "token: admin123"
```

#### 新增图书
```bash
curl -X POST "http://localhost:8080/api/books" \
  -H "Content-Type: application/json" \
  -H "token: admin123" \
  -d '{
    "bookName": "Java编程思想",
    "author": "Bruce Eckel",
    "publisher": "机械工业出版社",
    "price": 89.50,
    "stock": 100
  }'
```

#### 根据ID查询图书
```bash
curl -X GET "http://localhost:8080/api/books/1" \
  -H "token: admin123"
```

#### 条件查询图书列表
```bash
curl -X GET "http://localhost:8080/api/books/list?bookName=Java&author=Bruce" \
  -H "token: admin123"
```

### 2. 教师管理服务API

#### 新增教师
```bash
curl -X POST "http://localhost:8080/api/teacher/add" \
  -H "Content-Type: application/json" \
  -H "token: admin123" \
  -d '{
    "teacherName": "张三",
    "teacherNo": "T2023001",
    "gender": "男",
    "age": 35,
    "title": "教授",
    "department": "计算机学院",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "salary": 15000.00
  }'
```

#### 分页查询教师列表
```bash
curl -X GET "http://localhost:8080/api/teacher/list?pageNum=1&pageSize=10" \
  -H "token: admin123"
```

#### 根据ID删除教师
```bash
curl -X DELETE "http://localhost:8080/api/teacher/delete/1" \
  -H "token: admin123"
```

#### 条件查询教师
```bash
curl -X GET "http://localhost:8080/api/teacher/search?teacherName=张&department=计算机学院" \
  -H "token: admin123"
```

### 2. 认证说明
- **需要认证的接口**: 必须包含请求头 `token: admin123`
- **白名单接口**（无需认证）:
  - `/api/books/page` - 分页查询
  - `/api/books/list` - 列表查询
  - `/api/books/{id}` - 单本查询
  - `/fallback/` - 降级接口
  - `/actuator/` - 监控端点

### 3. 直接访问服务（不通过网关）
```bash
# 直接访问图书服务
curl -X GET "http://localhost:8081/book-service/api/books/page?pageNum=1&pageSize=10"
```

## 监控与测试

### 1. 服务监控
- **Eureka控制台**: http://localhost:8761
- **Gateway监控**: http://localhost:8080/actuator
- **Book Service监控**: http://localhost:8081/book-service/actuator

### 2. API文档
- **Book Service Swagger**: http://localhost:8081/book-service/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/book-service/v3/api-docs

### 3. Resilience4j监控
- **断路器状态**: http://localhost:8080/actuator/circuitbreakers
- **限流器状态**: http://localhost:8080/actuator/ratelimiters
- **健康检查**: http://localhost:8080/actuator/health

### 4. 降级处理
当服务不可用或触发限流时，会返回降级响应：
- **服务不可用**: HTTP 503, 消息"服务暂时不可用，请稍后重试"
- **请求限流**: HTTP 429, 消息"请求过于频繁，请稍后重试"

## 功能特性

### 1. API网关功能
- **路由转发**: 根据路径转发到不同的微服务
- **全局鉴权**: 统一的token认证机制
- **限流保护**: 基于Resilience4j的请求限流
- **熔断降级**: 服务不可用时的自动降级处理
- **负载均衡**: 基于Ribbon的客户端负载均衡

### 2. 图书管理服务
- **完整的CRUD操作**: 增删改查全部实现
- **分页查询**: 支持分页参数pageNum和pageSize
- **条件查询**: 支持按书名模糊查询、按作者精确查询
- **统一异常处理**: 参数校验、业务异常统一处理
- **Swagger文档**: 自动生成的API文档

### 3. 教师管理服务
- **教师CRUD操作**: 新增、删除、修改、查询
- **Nacos配置中心**: 从Nacos获取数据库连接等配置
- **分页查询**: 支持分页参数pageNum和pageSize
- **条件查询**: 支持按姓名模糊查询、按院系精确查询
- **统一异常处理**: 参数校验、业务异常统一处理
- **Swagger文档**: 自动生成的API文档

### 4. 微服务架构
- **服务注册发现**: 通过Eureka/Nacos实现服务注册与发现
- **配置管理**: 通过Nacos Config实现统一配置管理
- **服务调用**: 服务间通过OpenFeign调用
- **监控告警**: 通过Actuator暴露监控指标

## 配置说明

### 1. 限流配置
```yaml
resilience4j:
  ratelimiter:
    instances:
      bookServiceRateLimiter:
        limitForPeriod: 10      # 每秒10个请求
        limitRefreshPeriod: 1s  # 刷新周期1秒
        burstCapacity: 20       # 突发容量20
```

### 2. 熔断配置
```yaml
resilience4j:
  circuitbreaker:
    instances:
      bookServiceCircuitBreaker:
        slidingWindowSize: 10           # 滑动窗口大小
        minimumNumberOfCalls: 5         # 最小调用数
        failureRateThreshold: 50        # 失败率阈值50%
        waitDurationInOpenState: 5s     # 等待时间5秒
```

### 3. 路由配置
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: book-service-route
          uri: lb://book-service        # 负载均衡到book-service
          predicates:
            - Path=/api/books/**       # 匹配路径
          filters:
            - StripPrefix=1            # 去除前缀
            - name: CircuitBreaker     # 断路器
            - name: RequestRateLimiter # 限流器
```

## 开发指南

### 1. 添加新的微服务
1. 在父工程`pom.xml`的`<modules>`中添加新模块
2. 创建模块目录和`pom.xml`
3. 继承父工程的依赖管理
4. 配置服务注册和发现
5. 实现业务逻辑

### 2. 扩展网关路由
在`api-gateway/src/main/resources/application.yml`中添加新的路由配置：
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: new-service-route
          uri: lb://new-service
          predicates:
            - Path=/new-service/**
          filters:
            - StripPrefix=1
```

### 3. 自定义鉴权逻辑
修改`AuthGlobalFilter.java`中的`filter`方法，实现自定义的认证逻辑。

## 常见问题

### 1. 服务启动失败
- **检查端口冲突**: 确保8761、8080、8081等端口未被占用
- **检查数据库连接**: 确认MySQL服务已启动，连接配置正确
- **检查Java版本**: 确保使用Java 17或更高版本

### 2. 网关访问返回401
- **检查token**: 确保请求头中包含`token: admin123`
- **检查白名单**: 确认访问的路径是否在白名单中
- **检查网关日志**: 查看网关服务的日志输出

### 3. 服务注册失败
- **检查Eureka Server**: 确保Eureka Server已启动并运行在8761端口
- **检查服务配置**: 确认服务配置中的Eureka地址正确
- **检查网络连接**: 确保服务可以访问Eureka Server

## 项目进度

- [x] 创建父工程和基础模块
- [x] 实现图书管理微服务
- [x] 实现Eureka服务注册中心
- [x] 实现API网关（含鉴权、限流、熔断）
- [x] 实现服务提供者和消费者示例
- [x] 实现教师管理微服务
- [x] 集成Nacos配置中心
- [x] 编写完整的使用文档
- [ ] 集成阿里云OSS和VOD
- [ ] 添加用户管理服务
- [ ] 添加课程管理服务
- [ ] 添加订单管理服务

## 贡献指南

1. Fork本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 许可证

本项目采用MIT许可证。详见 [LICENSE](LICENSE) 文件。

## 联系方式

- 项目地址: https://github.com/7cjfire/student_books
- 问题反馈: 请在GitHub Issues中提交问题

---

**最后更新**: 2026-05-11