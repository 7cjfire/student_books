# auth-service 认证服务

用户登录 + JWT 签发。

## 端口

`9000`，context-path = `/auth-service`

## 接口

| Method | 路径 | 说明 |
|---|---|---|
| POST | `/auth/login`  | 登录，返回 JWT |
| GET  | `/auth/me`     | 当前登录用户（经网关 `X-User-Id` 解析后查 DB） |
| POST | `/auth/logout` | 登出（无状态：前端删除 token 即可） |

经网关：`http://localhost:8080/api/auth/login`。

## 种子账号

启动时如果 `edu_user` 表为空，会自动创建：
- 用户名 `admin`
- 密码 `admin123`
- 角色 `ADMIN`

首次登录后请在运管里修改密码。

## JWT 密钥

`jwt.secret` 必须与 `api-gateway` 一致，推荐用环境变量：

```bash
export JWT_SECRET=<64位以上的随机字符串>
export JWT_EXPIRE_MINUTES=720   # 可选，默认 12h
```

## 登录示例

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 响应
# {
#   "code": 200, "message": "操作成功",
#   "data": {
#     "token": "eyJhbGciOi...",
#     "user": {"id":1,"username":"admin","nickName":"超级管理员","roles":"ADMIN"}
#   }
# }

# 拿到 token 后调用受保护接口
curl "http://localhost:8080/api/teacher/list" \
  -H "Authorization: Bearer eyJhbGciOi..."
```
