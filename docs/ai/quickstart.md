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

## 如果目标是派生一个新应用

先读这些机器可读资产：

- `docs/ai/schemas/app-manifest.schema.json`
- `docs/ai/schemas/module-registry.schema.json`
- `docs/ai/generated-app-verification-contract.json`
- `docs/ai/schemas/generated-app-verification-contract.schema.json`
- `docs/ai/compatibility/app-assembly-suite.json`
- `docs/ai/module-registry.json`
- `docs/ai/app-assembly-contract.json`
- `docs/ai/manifests/default-baseline-app.json`
- `docs/ai/manifests/core-admin-app.json`

注意：

- `docs/ai/app-assembly-contract.json`、schema 和 compatibility suite 才是规范事实来源
- `docs/ai/generated-app-verification-contract.json` 定义生成后验证的标准输入、检查项和结果语义
- `scripts/scaffold-derived-app.mjs` 是当前的 `Node` 参考实现，不应被当成唯一标准
- `tools/java-assembly-cli/` 提供第二个兼容实现；它要通过同一套 compatibility suite，而不是复用 Node 内部逻辑
- `scripts/verify-derived-app.mjs` 是当前的 `Node` reference verifier，不是 generated-app verification contract 本身

再执行：

```bash
node scripts/scaffold-derived-app.mjs \
  --manifest docs/ai/manifests/core-admin-app.json \
  --output ../core-admin-console
```

或者走 Java CLI 路径：

```bash
./scripts/scaffold-derived-app-java.sh \
  docs/ai/manifests/core-admin-app.json \
  ../core-admin-console
```

验证装配系统本身：

```bash
./scripts/verify-app-assembly.sh
```

它现在验证的是“compatibility suite + Node/Java 两个兼容实现”，不是仅仅检查某一个脚本还能否跑通。

验证某个已生成应用骨架：

```bash
./scripts/verify-derived-app.sh ../core-admin-console
```

如果你要看当前 reference verifier 的直接入口：

```bash
node scripts/verify-derived-app.mjs ../core-admin-console
```
