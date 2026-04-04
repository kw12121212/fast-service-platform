# lealone-vendor-sync

## What

将 `vendor/lealone`（本地 git clone）同步到上游 Lealone master 分支的 Apr 3, 2026 最新状态，同时将 `backend/pom.xml` 中直接声明的 Tomcat 版本对齐至上游，并在 `AGENTS.md` 中补充 Tomcat 版本说明。

## Why

上游 Lealone 从 Mar 27 至 Apr 3 持续迭代，包含依赖升级（Jackson-core 2.13.1→2.18.6、Tomcat 11.0.15→11.0.18）、`SourceCompiler` 动态编译改进、代码生成修复（跳过系统 schema 表），以及新 SQL 能力（`CREATE WORKFLOW`）。`vendor/lealone` 停留在旧版会导致本地构建使用过时的依赖，与上游产生隐性漂移，并增加后续更新的合并成本。

## Scope

### 在范围内
- 在 `vendor/lealone` 执行 `git pull origin master`，拉取 Mar 28 – Apr 3 的所有上游变更
- 更新 `backend/pom.xml` 中 Tomcat 版本：`11.0.15` → `11.0.18`，与上游 lealone-http/pom.xml 保持一致
- 在 `AGENTS.md` 中补充 Tomcat 当前版本说明
- 重新运行 `scripts/install-lealone-source-deps.sh` 完成本地重建
- 执行 `scripts/verify-backend.sh` 验证后端

### 不在范围内
- 采纳上游新增的 `CREATE WORKFLOW` SQL 功能
- 集成 AI 自动生成 ORM 代码的流程
- 修改任何平台业务逻辑、服务接口或前端

## Unchanged Behavior

- 所有现有 `ServiceExecutor` 实现签名保持不变
- `BackendBootstrap` 启动流程不变
- `Lealone.runScript()`、`LealoneApplication`、`SysProperties.setBaseDir()` 调用方式不变
- 现有测试的可观测行为不变
