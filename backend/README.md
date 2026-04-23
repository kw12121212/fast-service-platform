# Backend Workspace

首个可运行的后端核心放在这里。

当前实现目标：

- 单模块 Maven 工程
- Java 25
- 基于 Lealone 启动
- 领域按用户、权限、项目、工单、看板拆分
- 可选 demo 数据
- 已支持项目级 Git / worktree / merge 工程能力，并为后续 sandbox 组件预留扩展位

当前仓库默认 baseline 仍包含项目 / 工单 / 看板，但在新的 app assembly 路径里，它们可以作为可选业务模块装配到派生应用中。

本地依赖策略：

- `vendor/lealone`（git submodule）

这个源码仓库通过 git submodule 管理，初始化后通过 `scripts/install-lealone-source-deps.sh` 安装到本地 Maven 仓库。

```bash
git submodule update --init vendor/lealone
./scripts/install-lealone-source-deps.sh
```
