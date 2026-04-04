# Design: lealone-vendor-sync

## Approach

1. 在 `vendor/lealone` 目录中执行 `git pull origin master`，拉取上游全部变更
2. 在 `backend/pom.xml` 中将 Tomcat embed 版本从 `11.0.15` 更新为 `11.0.18`
3. 在 `AGENTS.md` 的环境说明区块补充 Tomcat 当前版本
4. 运行 `scripts/install-lealone-source-deps.sh` 重新本地安装 Lealone
5. 运行 `scripts/verify-backend.sh` 验证后端编译和测试通过

vendor/lealone 本身的 pom.xml 版本（Jackson、Tomcat）由 git pull 自动更新，无需手动修改 vendor 内文件。

## Key Decisions

- **全量 git pull 而非选择性 cherry-pick**：上游变更均为非破坏性，且互相有依赖关系（如 SourceCompiler 的 JavaFileManager 改动跨多个提交），全量拉取避免漏合依赖。
- **backend/pom.xml Tomcat 版本同步**：backend 直接声明 Tomcat 依赖，与 lealone-http/pom.xml 分开管理。git pull 只更新 vendor，不会自动更新 backend，因此需要手动对齐。
- **暂不采纳 CREATE WORKFLOW 等新 AI 功能**：这些是独立的产品能力，需要单独的 spec 驱动变更，不宜混入版本同步任务。

## Alternatives Considered

- **手动逐文件同步**：成本高、易遗漏，vendor 本身是 git clone，直接 pull 更可靠。
- **等待 Lealone 发布正式版本**：上游当前为 8.0.0-SNAPSHOT，无明确发版时间，等待会持续积累漂移。
