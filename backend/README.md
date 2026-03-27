# Backend Workspace

首个可运行的后端核心放在这里。

当前实现目标：

- 单模块 Maven 工程
- Java 25
- 基于 Lealone-Platform 启动
- 领域按用户、权限、项目、工单、看板拆分
- 可选 demo 数据
- 为后续 Git/worktree/merge/sandbox 组件预留扩展位

本地依赖策略：

- `vendor/lealone`
- `vendor/lealone-platform`

这两个源码仓库需要先 clone 到本项目内，再通过 `scripts/install-lealone-source-deps.sh` 安装到本地 Maven 仓库。
