# AI Quickstart

这个入口面向第一次接手本仓库的 AI 代理，也适合人类贡献者快速定位上下文。

## 目标

- 用最少的读取成本建立当前仓库上下文
- 快速判断需求是否在当前产品边界内
- 让常见改动能直接映射到目录、文件和验证命令
- 避免在分散文档之间反复推断约定

## 第一次接手时按这个顺序读

1. `README.md`
2. `RTK.md`
3. `AGENTS.md`
4. `.spec-driven/config.yaml`
5. `.spec-driven/specs/INDEX.md`
6. 与本次改动直接相关的主 spec 文件
7. `docs/ai/context.yaml`
8. 对应任务的 playbook

## 当前硬边界

- 这是一个 `AI 生成业务系统的平台`，不是仓库内置的 AI chat 产品。
- V1 只面向 `企业内部管理应用`。
- V1 只生成 `单体应用`。
- 输入边界仍然是 `自然语言 + 可选原型图片 + 仅用于 UI 的参考网站`。
- 平台依赖边界仍然是 `Lealone-Platform + 本项目已有依赖`，不要自行引入新的外部软件库。
- 非平凡改动先进入 `.spec-driven/`，不要跳过 proposal 扩范围。

## 常见任务去哪里改

### 新增或扩展 backend 领域能力

- 主入口：`backend/src/main/resources/sql/services.sql`
- 表结构：`backend/src/main/resources/sql/tables.sql`
- Java 实现：`backend/src/main/java/com/fastservice/platform/backend/<domain>/`
- demo 数据：`backend/src/main/resources/sql/demo.sql` 和 `backend/src/main/java/com/fastservice/platform/backend/demo/DemoDataSupport.java`
- backend 测试：`backend/src/test/java/com/fastservice/platform/backend/`

参考 playbook：
- `docs/ai/playbooks/backend-domain-change.md`
- `docs/ai/playbooks/integration-demo-and-smoke.md`

### 新增或扩展 frontend 管理页

- 页面：`frontend/src/features/`
- 管理后台通用 UI：`frontend/src/components/admin/`
- 基础 UI：`frontend/src/components/ui/`
- 路由和壳：`frontend/src/app/`
- 数据访问：`frontend/src/lib/api/`
- 前端测试：`frontend/src/app/*.test.tsx`

参考 playbook：
- `docs/ai/playbooks/frontend-admin-change.md`

### 做前后端联调、demo 数据或 smoke 验证

- 先确认 backend 启动路径和 `/service/*` 契约
- 通过 Vite dev server 的 `/service/*` 代理验证，而不是只打 backend 直连接口
- 优先复用：
  - `scripts/verify-backend.sh`
  - `scripts/verify-frontend.sh`
  - `scripts/verify-fullstack.sh`

参考 playbook：
- `docs/ai/playbooks/integration-demo-and-smoke.md`

## 标准验证入口

从仓库根目录执行：

```bash
./scripts/verify-backend.sh
./scripts/verify-frontend.sh
./scripts/verify-fullstack.sh
```

说明：

- `verify-backend.sh` 跑 backend 基线测试
- `verify-frontend.sh` 跑 frontend 的 `test`、`build`、`lint`
- `verify-fullstack.sh` 会准备 backend 运行时、启动 backend 和 Vite dev server，并通过 frontend 的 `/service/*` 代理做 smoke 验证

## 高价值改动顺序

1. 先确认 proposal / spec 范围
2. 再读对应 playbook
3. 最后读将要修改的代码文件

如果跳过第 2 步，AI 很容易在不该动的目录里扩散改动。

## 常见失败先看哪里

优先读：

- `docs/ai/troubleshooting.md`

重点覆盖：

- `vendor/lealone` 或 `vendor/lealone-platform` 缺失
- Java / Maven / Node / bun 版本和路径不一致
- backend 运行时 classpath 没准备好
- `8080` 或 `4173` 端口冲突
- `/service/*` 代理路径没有真正打到 backend
