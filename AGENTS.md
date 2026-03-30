# AGENTS

@RTK.md

## Start Here

- 先读 `README.md`、`RTK.md`、`.spec-driven/config.yaml` 和 `.spec-driven/specs/INDEX.md`。
- 如果要实际实施改动，再读 `docs/ai/quickstart.md` 和 `docs/ai/context.yaml`。
- 把 `.spec-driven/` 视为范围和任务的事实来源；不要跳过 proposal 就扩展需求。
- 如果改动影响前后端协作方式、目录结构、平台边界或 AI 使用方式，先补 spec 再动手。

## Repository Defaults

- 后端基线是 `Java 25 LTS + Maven 3.9.x + Lealone`。
- 前端基线是 `Node 24 + bun + Vite 8 + React 19 + shadcn/ui + Tailwind CSS 4`。
- 文档默认优先使用中文；技术标识、命令、路径和协议名保留英文原文。

## Local Environment Assumptions

- Java 默认路径：`$HOME/.sdkman/candidates/java/25.0.2-tem`
- Maven 默认路径：`$HOME/.sdkman/candidates/maven/current/bin/mvn`
- Node.js 由 `nvm` 管理，当前使用 `Node 24`
- `bun` 作为前端包管理器和脚本执行器

如果环境不同，优先兼容仓库已有脚本和说明，不要默默改掉本仓库的默认约定。

## Dependency Rules

- `Lealone` 需要以源码形式存在于 `vendor/`：
  - `vendor/lealone`
- 本地安装使用 `scripts/install-lealone-source-deps.sh`。
- 不要随意把产品原则改成依赖额外外部软件库。

## Current Product Boundary

- 这是一个 `AI 生成业务系统的平台`，不是普通业务项目。
- V1 只面向 `企业内部管理应用`。
- V1 只生成 `单体应用`。
- 输入边界是 `自然语言 + 可选原型图片 + 仅用于 UI 的参考网站`。
- 当前最小企业域是：
  - 用户管理
  - 基于角色的权限管理
  - 软件项目管理
  - 工单管理
  - 看板管理

## Current Implementation Reality

- `backend/` 已经是可运行的单模块 Maven 后端，不再是占位目录。
- `frontend/` 已经是可运行的 PC 管理后台，不再是空工作区。
- frontend 直接联调当前 backend；不要把它退回到纯静态 mock。
- backend 支持 `可选 demo 数据`，前端允许展示空数据态和 demo 数据态。

## Engineering Expectations

- 优先保持 AI 可读性：目录命名清晰、说明直接、少隐式约定。
- 优先复用现有结构和模式，不要无故引入新的框架层、状态层或代码生成魔法。
- 没有 spec 支撑时，不要默默扩大范围。
- 新增文档时，优先解释“做什么、为什么、边界是什么”。
- 修改 README、AGENTS、RTK 或 specs 时，确保它们和仓库真实状态一致。

## Frontend Expectations

- 前端页面按领域放在 `frontend/src/features/`。
- 通用后台 UI 放在 `frontend/src/components/admin/`。
- 基础 UI 组件放在 `frontend/src/components/ui/`。
- 数据访问约定放在 `frontend/src/lib/api/`，不要把请求逻辑重新打散到页面里。
- 保持对 AI 友好的页面和资源模式，优先沿用现有 router、shell、hook 和资源状态处理方式。

## Backend Expectations

- 后端按领域组织包结构，不要回退成大杂烩式分层。
- 服务主路径继续遵循当前 Lealone 的推荐模型：SQL 定义表和服务，Java 实现服务，`LealoneApplication` 启动。
- 如果为了测试或稳定性做工程化补充，保留主路径一致性，不要把 backend 演化成完全脱离 Lealone 的实现。

## Verification Baseline

- backend 至少跑：`mvn -q test`
- frontend 至少跑：`bun run test`、`bun run build`、`bun run lint`
- 涉及前后端联调的改动，尽量验证 `/service/*` 路径而不是只看本地 mock
- 优先复用仓库验证入口：`scripts/verify-backend.sh`、`scripts/verify-frontend.sh`、`scripts/verify-fullstack.sh`
