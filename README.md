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

V1 的最小企业组件集包括：

- 用户管理
- 基于角色的权限管理
- 软件项目管理
- 工单管理
- 看板管理

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
- 当前主规格索引见 `.spec-driven/specs/INDEX.md`
- AI 接手仓库的统一入口见 `docs/ai/quickstart.md`
- 机器可读上下文见 `docs/ai/context.yaml`

## AI Ready 入口

如果目标是让 AI 代理在当前仓库里安全实施改动，优先使用这些仓库内置入口：

- `docs/ai/quickstart.md`
- `docs/ai/context.yaml`
- `docs/ai/playbooks/`
- `docs/ai/troubleshooting.md`
- `scripts/verify-backend.sh`
- `scripts/verify-frontend.sh`
- `scripts/verify-fullstack.sh`

## 当前状态

- backend 核心与 frontend 管理后台壳都已实现
- 当前基线已经覆盖最小企业组件与最小写工作流
- 当前没有活动中的 spec-driven change
- 下一步更适合继续补齐企业应用基础组件的完整度和可复用性
