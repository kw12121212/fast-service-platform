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

## AI 使用仓库工具的默认原则

- AI 在这个仓库里的默认角色是 `tool orchestrator`，不是现有 platform workflow 的替代实现。
- 对 assembly、generated-app verification、upgrade target selection、advisory、evaluation 和 execution，优先走 `./scripts/platform-tool.sh`。
- 对 derived app 的 runtime smoke，也优先走 `./scripts/platform-tool.sh generated-app smoke ...`，不要自己重写临时启动链路。
- 先读 `docs/ai/ai-tool-orchestration-contract.json`，再决定是否需要走特定兼容实现或 wrapper。
- 如果默认 façade 和 contract 允许的 fallback 都不可用，停止并上报 blocker，不要自己临时重写 workflow。

## 当前硬边界

- 这是一个 `AI 生成业务系统的平台`，不是仓库内置的 AI chat 产品。
- V1 只面向 `企业内部管理应用`。
- V1 只生成 `单体应用`。
- 输入边界仍然是 `自然语言 + 可选原型图片 + 仅用于 UI 的参考网站`。
- 平台依赖边界仍然是 `Lealone + 本项目已有依赖`，不要自行引入新的外部软件库。
- 非平凡改动先进入 `.spec-driven/`，不要跳过 proposal 扩范围。
- 如果目标是派生新应用，先区分四层输入：
  - `solution input` 用来描述业务意图
  - `solution-to-manifest plan` 用来显式收敛模块决策和 manifest 准备结果
  - `solution-to-manifest recommendation` 用来表达可选 guidance，帮助 contributors 调整 manifest shaping
  - `app-manifest` 用来驱动实际 assembly

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
  - `scripts/verify-derived-app-runtime-smoke.sh`

参考 playbook：
- `docs/ai/playbooks/integration-demo-and-smoke.md`

## 标准验证入口

从仓库根目录执行：

```bash
./scripts/verify-backend.sh
./scripts/verify-backend-sandbox-runtime.sh
./scripts/verify-frontend.sh
./scripts/verify-fullstack.sh
```

说明：

- `verify-backend.sh` 跑 backend 快速基线测试，不包含真实 sandbox runtime 执行
- `verify-backend-sandbox-runtime.sh` 跑 backend 的重型 sandbox runtime 验证，实际执行 `podman` image/container 路径
- `verify-frontend.sh` 跑 frontend 的 `test`、`build`、`lint`
- `verify-fullstack.sh` 会准备 backend 运行时、启动 backend 和 Vite dev server，并通过 frontend 的 `/service/*` 代理做 smoke 验证
- `verify-derived-app-runtime-smoke.sh` 面向 derived app 目录执行同类 smoke，证明 generated frontend 和 generated backend 的真实代理链路可用

## 如果你要展示仓库内置 baseline demo

优先看：

- `demo/GUIDE.md`

常用命令：

```bash
./scripts/regenerate-baseline-demo.sh
./scripts/verify-baseline-demo.sh
./scripts/platform-tool.sh generated-app smoke demo/baseline-demo
```

说明：

- `regenerate-baseline-demo.sh` 用 repository-owned assembly path 重新生成 committed baseline demo
- `verify-baseline-demo.sh` 会验证 generated-app contract、derived-app runtime smoke，以及 demo backend 和 frontend 的最小校验路径

## 高价值改动顺序

1. 先确认 proposal / spec 范围
2. 再读对应 playbook
3. 最后读将要修改的代码文件

如果跳过第 2 步，AI 很容易在不该动的目录里扩散改动。

## 常见失败先看哪里

优先读：

- `docs/ai/troubleshooting.md`

重点覆盖：

- `vendor/lealone` 缺失
- Java / Maven / Node / bun 版本和路径不一致
- backend 运行时 classpath 没准备好
- `8080` 或 `4173` 端口冲突
- `/service/*` 代理路径没有真正打到 backend

## 如果目标是派生一个新应用

先读这些机器可读资产：

- `docs/ai/ai-solution-input-contract.json`
- `docs/ai/schemas/ai-solution-input.schema.json`
- `docs/ai/solution-to-manifest-planning-contract.json`
- `docs/ai/schemas/solution-to-manifest-planning.schema.json`
- `docs/ai/solution-to-manifest-recommendation-contract.json`
- `docs/ai/schemas/solution-to-manifest-recommendation.schema.json`
- `docs/ai/ai-tool-orchestration-contract.json`
- `docs/ai/schemas/ai-tool-orchestration-contract.schema.json`
- `docs/ai/structured-app-template-contract.json`
- `docs/ai/schemas/structured-app-template-contract.schema.json`
- `docs/ai/template-classifications/default-derived-app-template-map.json`
- `docs/ai/schemas/derived-app-template-map.schema.json`
- `docs/ai/schemas/app-manifest.schema.json`
- `docs/ai/schemas/module-registry.schema.json`
- `docs/ai/derived-app-lifecycle-contract.json`
- `docs/ai/derived-app-upgrade-execution-contract.json`
- `docs/ai/platform-release.json`
- `docs/ai/platform-release-history.json`
- `docs/ai/platform-release-advisory.json`
- `docs/ai/schemas/derived-app-lifecycle-contract.schema.json`
- `docs/ai/schemas/derived-app-lifecycle-metadata.schema.json`
- `docs/ai/schemas/derived-app-upgrade-execution-contract.schema.json`
- `docs/ai/schemas/platform-release-history.schema.json`
- `docs/ai/schemas/platform-release-advisory.schema.json`
- `docs/ai/generated-app-verification-contract.json`
- `docs/ai/schemas/generated-app-verification-contract.schema.json`
- `docs/ai/compatibility/app-assembly-suite.json`
- `docs/ai/module-registry.json`
- `docs/ai/app-assembly-contract.json`
- `docs/ai/solution-inputs/core-admin-console.solution-input.json`
- `docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json`
- `docs/ai/solution-recommendations/core-admin-console.solution-to-manifest-recommendation.json`
- `docs/ai/manifests/default-baseline-app.json`
- `docs/ai/manifests/core-admin-app.json`

注意：

- `docs/ai/app-assembly-contract.json`、schema 和 compatibility suite 才是规范事实来源
- `docs/ai/ai-solution-input-contract.json` 定义更高层的结构化业务输入；它要先映射成 `solution-to-manifest plan`，再产出 `app-manifest` 进入 assembly tooling
- `docs/ai/solution-to-manifest-planning-contract.json` 定义 deterministic planning 层，显式暴露模块决策依据和 manifest 准备结果，但它本身不是 assembly runtime contract
- `docs/ai/solution-to-manifest-recommendation-contract.json` 定义 optional recommendation 层，显式暴露 recommendation basis、confidence、prerequisites 和 accepted constraints，但它本身不是 assembly runtime contract
- `docs/ai/ai-tool-orchestration-contract.json` 定义 AI 应该怎样优先使用 repository-owned tooling，而不是直接重做 assembly / verification / upgrade 逻辑
- `docs/ai/generated-app-verification-contract.json` 定义生成后验证的标准输入、检查项和结果语义
- `docs/ai/structured-app-template-contract.json` 和 `docs/ai/template-classifications/default-derived-app-template-map.json` 定义生成输出里的 stable template、slot host、module fragment 和 customization zone 边界
- `docs/ai/derived-app-lifecycle-contract.json`、`docs/ai/derived-app-upgrade-execution-contract.json`、`docs/ai/platform-release.json`、`docs/ai/platform-release-history.json` 和 `docs/ai/platform-release-advisory.json` 定义生成后生命周期、升级目标选择、升级评估、升级执行和当前发布差异说明的事实来源
- `docs/ai/compatibility/app-assembly-suite.json` 现在覆盖的不只是最小 baseline，还包括代表性的模块组合和无效边界样例
- repository-owned platform tooling 现在统一走 Java 主路径；前端仍然保留 `Node/bun`
- `./scripts/platform-tool.sh` 是 AI 调用这些 workflow 的默认 façade；只有 contract 明确允许时才回退到 wrapper
- `tools/java-assembly-cli/` 提供当前仓库拥有的 assembly 实现；它要通过同一套 compatibility suite
- `scripts/VerifyDerivedApp.java` 是 generated-app repository-owned verifier 入口，不是 contract 本身
- `tools/java-generated-app-verifier/` 保留为 compatible generated-app verifier；它读取生成应用自带资产，而不是读取 repository-owned verifier 内部状态
- 如果要改 generated output，先读 `docs/ai/playbooks/customize-derived-app-template-boundaries.md`，不要把 `slot-host` 误当成自由编辑区

如果你当前只有业务目标，还没有 manifest，先读：

```text
docs/ai/playbooks/define-ai-solution-input.md
```

把 solution input 收敛成 `solution-to-manifest plan`，按需补 `solution-to-manifest recommendation`，再产出 `app-manifest` 之后，再执行：

```bash
./scripts/platform-tool.sh assembly scaffold \
  docs/ai/manifests/core-admin-app.json \
  ../core-admin-console
```

验证装配系统本身：

```bash
./scripts/platform-tool.sh assembly verify
```

它现在验证的是“compatibility suite + Java 仓库实现”，不是仅仅检查某一个旧脚本还能否跑通。

验证某个已生成应用骨架：

```bash
./scripts/platform-tool.sh generated-app verify ../core-admin-console
```

如果你要补一层真实运行证明，而不是只做结构验证：

```bash
./scripts/platform-tool.sh generated-app smoke ../core-admin-console
```

如果你要走 Java verifier 路径：

```bash
./scripts/platform-tool.sh generated-app verify-java ../core-admin-console
```

如果你要评估一个已派生应用能否进入升级流程：

```bash
./scripts/platform-tool.sh upgrade evaluate ../core-admin-console
```

如果你要先看这个派生应用有哪些仓库支持的升级目标：

```bash
./scripts/platform-tool.sh upgrade targets ../core-admin-console
```

如果你要看当前平台发布的 advisory：

```bash
./scripts/platform-tool.sh upgrade advisory ../core-admin-console
```

如果你要先看 upgrade dry-run plan：

```bash
./scripts/platform-tool.sh upgrade execute ../core-admin-console
```

如果你要应用仓库拥有的升级动作：

```bash
./scripts/platform-tool.sh upgrade execute ../core-admin-console --apply
```
