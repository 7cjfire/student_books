# admin-web 运管前端

Vue 3 + TypeScript + Vite + Pinia + Element Plus 的运管后台，对接本仓库的后端微服务。

## 技术栈

- Vue 3.4 + `<script setup>` + TypeScript
- Vite 5
- Vue Router 4（`history` 模式 + 路由守卫）
- Pinia 2 + `pinia-plugin-persistedstate`（token 持久化）
- Element Plus 2.7 + 图标（自动按需导入）
- Axios（统一 `/api` 前缀 + JWT 注入 + 401 自动登出）

## 开发

```bash
cd admin-web
npm install         # 或 pnpm install / yarn
npm run dev         # Vite dev server on :5173，会把 /api 代理到 http://localhost:8080
```

默认登录：`admin` / `admin123`（由 auth-service 启动时种入）。

## 构建

```bash
npm run build       # 输出 dist/
npm run preview     # 本地预览构建产物
```

构建出的 `dist/` 可以用 Nginx 托管，配合一条 `/api/` 的反代到网关即可上线。

## 目录结构

```
src/
├── api/              后端 API 封装（每个后端服务一个文件）
│   ├── http.ts       axios 实例 + 拦截器
│   ├── auth.ts book.ts teacher.ts subject.ts course.ts index-api.ts file.ts
├── stores/auth.ts    登录态（token + 当前用户，持久化到 localStorage）
├── router/index.ts   路由 + 未登录守卫
├── layout/BasicLayout.vue  侧边栏 + 顶栏布局
├── views/
│   ├── login/
│   ├── dashboard/
│   ├── book/         图书 CRUD
│   ├── teacher/      教师 CRUD
│   ├── subject/      课程分类（树 + Excel 导入）
│   ├── course/
│   │   ├── CourseListView.vue     列表 + 状态流转
│   │   └── CourseWizardView.vue   多步骤发布向导（4 步）
│   ├── banner/       Banner 管理（含 OSS 图片上传）
│   └── home-preview/ 首页预览（聚合 + 清 Redis 缓存）
└── styles/global.scss
```

## 功能覆盖

| 后端接口                      | 前端页面 |
|-------------------------------|---------|
| auth-service `/auth/login` 等 | 登录 |
| book-service CRUD + 分页      | 图书管理 |
| teacher-service CRUD + 分页   | 教师管理 |
| subject-service 树 + Excel 导入 | 课程分类 |
| course-service 多步骤发布     | 课程列表 + 发布向导 |
| file-service OSS 上传         | 封面 / Banner 图片上传 |
| index-service 首页聚合 + 缓存 | 仪表盘 + 首页预览 |
| Banner CRUD                   | Banner 管理 |

## 路径 & CORS

- 所有 API 经网关 `/api/**`
- 开发期 Vite 代理：`/api -> http://localhost:8080`
- 生产期可以改成：前端与网关同域（Nginx 反代 `/api` 到 8080）
- 网关已开 CORS (`GatewayCorsConfig`)，Axios 带 `Authorization: Bearer <jwt>`

## 约定

- 所有写接口会弹 `ElMessage.success('已保存' | '已删除')`
- 所有后端错误统一由 `http.ts` 的响应拦截器弹 `ElMessage.error`
- 401 会自动 logout 并跳回 `/login?redirect=<原路径>`

## 沙盒限制说明

当前沙盒无法运行 `npm install` 拉取依赖，所以这份代码**没有经过真实构建验证**，但：
- 目录 / 文件结构对照官方模板
- `package.json` 里所有包都是主流稳定版
- API 层字段和后端 `Result<T>` + 实体一一对齐

你本地 `npm install` 后按 README 跑 `npm run dev` 即可。
