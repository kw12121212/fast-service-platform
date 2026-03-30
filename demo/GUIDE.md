# Baseline Demo Guide

这个目录提供一个 repository-owned 的 baseline demo，用来向人类直接展示 Fast Service Platform 当前能生成和运行的企业管理基线。

## 目标

这个 demo 要证明两件事：

1. 本项目当前不仅有 contract 和 tooling，也能产出一个可直接展示的 derived app
2. 这个 derived app 不是手工拼出来的特殊样本，而是基于本仓库的 assembly path 生成的

## 目录内容

- `baseline-demo.manifest.json`
  - baseline demo 的 assembly input
- `baseline-demo/`
  - 已提交到仓库的 baseline demo derived app
- `5-minute-demo.md`
  - 面向销售、客户或管理层演示的 5 分钟讲稿版路径

## 生成来源

这个 demo 来自本仓库拥有的 assembly tooling：

```bash
./scripts/platform-tool.sh assembly scaffold \
  demo/baseline-demo.manifest.json \
  /absolute/path/to/demo-output
```

仓库内置 demo 的标准再生成入口是：

```bash
./scripts/regenerate-baseline-demo.sh
```

它会使用：

- manifest: `demo/baseline-demo.manifest.json`
- repository-owned entrypoint: `./scripts/platform-tool.sh assembly scaffold`

## 重新生成仓库内置 demo

从仓库根目录执行：

```bash
./scripts/regenerate-baseline-demo.sh
```

执行后，仓库内的 `demo/baseline-demo/` 会被重新生成。

## 演示前验证

从仓库根目录执行：

```bash
./scripts/verify-baseline-demo.sh
```

这个验证会做三件事：

1. 对 `demo/baseline-demo/` 运行 repository-owned generated-app verify
2. 对 `demo/baseline-demo/backend` 运行 `mvn -q test`
3. 对 `demo/baseline-demo/frontend` 运行 `bun install --frozen-lockfile`、`bun run build`、`bun run lint`

## 启动 demo backend

```bash
cd demo/baseline-demo/backend
$HOME/.sdkman/candidates/maven/current/bin/mvn -q -DskipTests package dependency:build-classpath \
  -Dmdep.outputFile=target/runtime-classpath.txt \
  -DincludeScope=runtime

$HOME/.sdkman/candidates/java/25.0.2-tem/bin/java \
  -Dfsp.demo-data=true \
  -cp "target/classes:$(cat target/runtime-classpath.txt)" \
  com.fastservice.platform.backend.BackendApplication
```

默认情况下：

- backend HTTP 服务在 `http://127.0.0.1:8080`
- demo data 会初始化管理员、基础 RBAC、一个软件项目、一个看板和两条示例工单
- 你可以先展示现成 baseline，再继续现场创建新项目、看板和工单来演示写工作流

## 启动 demo frontend

```bash
cd demo/baseline-demo/frontend
bun install
bun run dev
```

frontend 会代理 `/service/*` 到本地 backend。

## 推荐演示路径

建议按这个顺序演示：

1. `Dashboard`
   - 展示当前 baseline 是一个完整管理后台，不是静态落地页
   - 展示统计来自真实 backend，而不是静态 mock
   - 说明首页已经能看到 demo data 回流出的 baseline 指标
2. `Users`
   - 展示最小用户管理能力
   - 现场创建一个用户
3. `Roles`
   - 展示最小 RBAC 管理能力
   - 现场创建角色、权限，或给用户分配角色
4. `Projects`
   - 展示项目管理
   - 先展示默认 demo project
   - 再现场创建一个新项目
   - 如本机有可用 Git 仓库，再绑定本地仓库路径并演示 branch context
5. `Kanban`
   - 先展示默认 `Delivery Board`
   - 再基于新项目创建一个 board
6. `Tickets`
   - 先展示默认 demo tickets 和状态分布
   - 再基于新项目和 board 创建 ticket
   - 现场推进 ticket 状态
7. `Dashboard`
   - 回到首页，展示默认 demo data 和你刚刚新增的数据都已回流到总览

## 演示时的注意事项

- 如果只想稳定展示 baseline，先用默认 demo data 完成首页和主流程展示，再补少量现场写操作
- 如果要演示 Git repository binding，请提前准备一个本地绝对路径 Git 仓库
- 如果 `vendor/lealone` 依赖未准备好，先回到仓库根目录执行：

```bash
./scripts/install-lealone-source-deps.sh
```

## 这个 demo 不是什么

- 它不是仓库的第二主开发工作区
- 它不是脱离平台主路径手工维护的样板
- 它不是新的业务方向

它是当前平台 baseline derived app 的 repository-owned 展示样本。
