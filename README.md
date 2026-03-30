# Fast Service Platform

一个面向 AI 复用的企业应用基础组件平台仓库。

这个项目不是普通业务系统，也不是直接提供 AI 对话入口的产品。它的目标是在 `Lealone-Platform` 的基础上沉淀一套可靠、对 AI 友好的企业应用前后端组件，让 AI 在明确约束下复用这些组件来组装企业内部管理系统。

## 项目定位

- 主要复用者是 `AI`
- 本仓库不直接提供 `AI 对话`、`prompt intake` 或原型上传入口
- V1 只面向 `企业内部管理应用`
- V1 目标应用形态是 `单体应用`
- 平台边界是 `Lealone-Platform + 本项目内置组件 + 本项目已有依赖`
- `不额外引入外部软件库` 是产品原则

## 当前已实现内容

当前仓库已经不是初始化空壳，已经落下了第一批可运行基线：

- `backend/` 已实现 Java 25 单模块后端核心
- `frontend/` 已实现 Vite 8 + React 19 的 PC 管理后台壳
- 后端已包含这些最小企业域：
  - 用户管理
  - 基于角色的权限管理
  - 软件项目管理
  - 工单管理
  - 看板管理
- 前端已包含这些最小可见页面：
  - 管理首页
  - 用户管理
  - 角色权限
  - 软件项目管理
  - 工单管理
  - 看板管理
- 后端支持 `可选 demo 数据`
- 前端已直接联调当前 backend，而不是只靠静态 mock

当前默认运行形态仍然是完整 baseline 应用；但从“AI 派生新应用”的角度看，`项目管理 / 工单管理 / 看板管理` 已经开始被视为可选装配模块，而不是所有派生应用都必须包含的硬性业务域。

## 当前已经达成的目标

当前仓库已经不只是“能启动一套演示后台”，而是已经完成了三类关键目标：

- `可运行 baseline`：
  - backend 和 frontend 都已落地，并通过 `/service/*` 完成真实联调
  - 最小企业域已经覆盖用户、角色权限、软件项目、工单、看板
  - backend 支持 `可选 demo 数据`
- `可组合平台模块`：
  - 当前 runnable baseline 仍可完整复现
  - 从派生应用角度，`admin shell`、`user-management`、`role-permission-management` 已经形成更稳定的必选核心
  - `project-management`、`project-repository-management`、`kanban-management`、`ticket-management` 已经形成更细粒度的可选模块边界
- `AI-ready 平台工作流`：
  - 已定义 machine-readable 的 `contract + schemas + compatibility suite`
  - 已提供 repository-owned 的 app assembly、generated-app verification、upgrade targets / evaluation / advisory / execution 入口
  - 已提供 AI quickstart、context、playbooks 和统一 tooling façade，约束 AI 优先复用仓库工具而不是重写 workflow

## 当前使用者可以做到什么

### 平台使用者

- 直接运行当前 baseline 企业管理应用
- 在管理后台里查看和操作用户、角色权限、软件项目、工单、看板等最小企业域
- 使用 demo 数据快速演示当前平台能力
- 通过前端代理路径 `/service/*` 做真实前后端联调和 smoke 验证

### 项目 / 工程使用者

- 在项目页创建软件项目
- 给项目绑定一个本地 Git 仓库绝对路径
- 查看绑定仓库的根路径、当前分支或 detached HEAD、working tree 状态、最近提交和本地分支列表
- 在仓库处于 clean working tree 且非 detached HEAD 时切换到现有本地分支

### AI 代理 / 平台贡献者

- 从结构化 `solution input` 收敛到 `app-manifest`
- 基于仓库拥有的 assembly contract 和 module registry 派生新的管理类单体应用骨架
- 使用统一入口 `./scripts/platform-tool.sh` 执行：
  - `assembly scaffold`
  - `assembly verify`
  - `assembly compatibility`
  - `generated-app verify`
  - `upgrade targets`
  - `upgrade evaluate`
  - `upgrade advisory`
  - `upgrade execute`
- 依据 structured app template contract 和 template classification map 判断哪些生成区域属于平台管理区、slot host 或 customization zone

## 当前仍未完成的内容

这些能力已经被纳入平台边界或预留扩展位，但还没有达到当前 baseline 业务模块同等成熟度：

- `worktree-management`
- `merge-support`
- `sandbox-environment`

这些方向更适合作为下一阶段的 spec-driven change，而不是当成已经可直接使用的现成功能。

## Demo

仓库现在提供一个 repository-owned 的 baseline demo，用于向人类直接展示“本项目作为基础库可以生成并运行什么样的企业管理基线”。

- demo manifest: `demo/baseline-demo.manifest.json`
- committed demo derived app: `demo/baseline-demo/`
- guide: `demo/GUIDE.md`
- 5 minute talk track: `demo/5-minute-demo.md`

常用入口：

```bash
./scripts/regenerate-baseline-demo.sh
./scripts/verify-baseline-demo.sh
```

## 技术基线

| 领域 | 基线 | 说明 |
| --- | --- | --- |
| 后端运行时 | Java 25 LTS | 统一 Java 基线 |
| 后端构建 | Maven 3.9.x | 当前本机通过 SDKMAN 管理 |
| 后端基础 | [Lealone-Platform](https://github.com/lealone/Lealone-Platform) | 快速服务与 API 创建基础 |
| 前端运行时 | Node 24 | 当前本机通过 `nvm` 管理 |
| 前端包管理 | bun | 当前前端包管理与脚本执行器 |
| 前端构建 | Vite 8 | 前端开发与构建入口 |
| 前端框架 | React 19 | UI 和交互核心框架 |
| 组件体系 | shadcn/ui | 源码可控组件体系 |
| 样式体系 | Tailwind CSS 4 | 设计 token 与实用类样式基础 |
| 变更方式 | spec-driven workflow | 先定义范围，再实施与验证 |

## 运行前准备

后端依赖 `Lealone` 和 `Lealone-Platform` 的源码安装。

当前仓库约定：

- `vendor/lealone`
- `vendor/lealone-platform`

先将这两个仓库 clone 到 `vendor/` 下，再执行：

```bash
./scripts/install-lealone-source-deps.sh
```

默认脚本会使用当前仓库约定的本机路径：

- Java: `$HOME/.sdkman/candidates/java/25.0.2-tem`
- Maven: `$HOME/.sdkman/candidates/maven/current/bin/mvn`

如有不同，可通过 `JAVA_HOME` 和 `MVN_BIN` 覆盖。

## 本地运行

### 启动 backend

```bash
cd backend
$HOME/.sdkman/candidates/maven/current/bin/mvn test
$HOME/.sdkman/candidates/java/25.0.2-tem/bin/java \
  -Dfsp.demo-data=true \
  -cp "target/classes:$(cat target/runtime-classpath.txt)" \
  com.fastservice.platform.backend.BackendApplication
```

说明：

- HTTP 服务默认在 `http://127.0.0.1:8080`
- Lealone TCP 默认在 `127.0.0.1:9210`
- `fsp.demo-data=true` 会加载可选 demo 数据，方便前端演示

### 启动 frontend

```bash
cd frontend
bun install
bun run dev
```

说明：

- Vite dev server 默认启动在本地开发端口
- `/service/*` 会代理到 `http://127.0.0.1:8080`
- frontend 直接依赖当前 backend 的服务返回

## 质量校验

当前仓库已经有基础自动化校验：

```bash
cd backend
$HOME/.sdkman/candidates/maven/current/bin/mvn -q test

cd frontend
bun run test
bun run build
bun run lint
```

## V1 最小能力

当前默认 baseline 应用包含：

- 用户管理
- 基于角色的权限管理
- 软件项目管理
- 工单管理
- 看板管理

从派生应用角度，V1 的必选核心更偏向：

- 管理后台首页 / admin shell
- 用户管理
- 基于角色的权限管理

而 `软件项目管理 / 工单管理 / 看板管理` 可以作为可选内置模块按需装配。

V1 平台基线至少必须提供：

- 测试
- 可选 demo 数据
- 管理后台首页
- 用户管理
- 角色权限管理
- 软件项目管理
- 工单管理
- 看板管理

## 仓库结构

```text
.
├── .spec-driven/     # 需求、变更提案、任务和规格
├── backend/          # Java 25 + Lealone-Platform 后端核心
├── frontend/         # Vite + React + shadcn/ui + Tailwind CSS 管理后台
├── ROADMAP.md        # 平台后续方向与建议优先级
├── scripts/          # 本地安装与辅助脚本
├── vendor/           # Lealone 与 Lealone-Platform 源码依赖
├── AGENTS.md         # AI/代理协作入口
├── RTK.md            # Repository Technical Knowledge
└── README.md         # 项目总览
```

## 协作方式

- 非平凡改动先进入 `.spec-driven/`
- 主规则先看 `AGENTS.md`
- 技术背景先看 `RTK.md`
- 后续方向优先看 `ROADMAP.md`
- 当前主规格索引见 `.spec-driven/specs/INDEX.md`
- AI 接手仓库的统一入口见 `docs/ai/quickstart.md`
- 机器可读上下文见 `docs/ai/context.yaml`

## AI Ready 入口

如果目标是让 AI 代理在当前仓库里安全实施改动，优先使用这些仓库内置入口：

- `docs/ai/quickstart.md`
- `docs/ai/context.yaml`
- `docs/ai/ai-tool-orchestration-contract.json`
- `docs/ai/structured-app-template-contract.json`
- `docs/ai/template-classifications/default-derived-app-template-map.json`
- `docs/ai/schemas/`
- `docs/ai/module-registry.json`
- `docs/ai/app-assembly-contract.json`
- `docs/ai/compatibility/app-assembly-suite.json`
- `docs/ai/playbooks/`
- `docs/ai/troubleshooting.md`
- `scripts/platform-tool.sh`
- `scripts/scaffold-derived-app-java.sh`
- `scripts/VerifyDerivedApp.java`
- `scripts/verify-app-assembly.sh`
- `scripts/verify-backend.sh`
- `scripts/verify-frontend.sh`
- `scripts/verify-fullstack.sh`

说明：

- `contract + schemas + compatibility suite` 是语言无关的装配标准
- `docs/ai/ai-tool-orchestration-contract.json` 规定 AI 应优先编排仓库工具，而不是直接重做已支持 workflow
- `docs/ai/structured-app-template-contract.json` 和默认 classification map 规定生成输出里的平台管理区、slot host、模块片段和派生应用 customization zone
- `scripts/platform-tool.sh` 是统一的 repository-owned tooling façade
- repository-owned platform tooling 当前统一收敛到 `Java`；前端仍然保留 `Node/bun`

## 当前状态

- backend 核心与 frontend 管理后台壳都已实现
- 当前基线已经覆盖最小企业组件与最小写工作流
- 当前没有活动中的 spec-driven change
- 下一步更适合继续补齐企业应用基础组件的完整度和可复用性
