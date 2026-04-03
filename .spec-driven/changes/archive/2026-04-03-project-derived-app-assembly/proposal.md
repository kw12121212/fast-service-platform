# project-derived-app-assembly

## What

为当前已经具备 `repository binding` 和 repository-owned app assembly tooling 的软件项目，定义第一版 `project-scoped derived-app assembly`。

这次 change 计划把 derived-app assembly 从“仓库里可单独调用的工具能力”推进到项目范围内可观察、可提交、可验证的行为：

- 已绑定仓库的软件项目可以暴露 derived-app assembly 上下文
- 第一版 assembly 路径以项目绑定的主仓库上下文为准，不要求选择 linked worktree
- 项目可以提交有效的 `app-manifest` 和显式的绝对输出目录来触发 assembly
- 第一版允许输出目录预先存在，但该目录必须为空目录
- 项目可以看到受限状态、最近一次可见且持久化的结果，以及输入校验失败和 assembly 执行失败之间的区别
- 前端在当前 `Projects` 体验中展示 assembly 入口、受限原因、原始 `app-manifest` 提交能力和结果反馈

## Why

当前仓库已经拥有 repository-owned 的 app assembly 契约、兼容性资产和工具入口，但这些能力仍然更像分散的脚本工作流，而不是软件项目上下文中的生命周期能力。

如果不先把 assembly 收敛到项目范围内，后续 roadmap 里的 `project-derived-app-verification` 就缺少它首版明确依赖的“latest visible successful project-scoped assembly output”，`project-derived-app-upgrade-support` 也会缺少完整 lifecycle 起点。

因此，这个 change 的目标不是重写 assembly 引擎，而是把现有 repository-owned assembly 能力接入当前 project-scoped workflow，形成后续 verification 和 upgrade-support 可以继续建立的第一块基础。

## Scope

In scope:

- 为已绑定本地 Git 仓库的软件项目定义 project-scoped derived-app assembly 上下文
- 要求第一版 assembly 路径只基于项目绑定的主仓库上下文
- 支持提交有效的 `app-manifest` 和显式绝对输出目录来触发 repository-owned assembly workflow
- 允许输出目录预先存在，但要求该目录为空目录
- 明确区分输入无效、项目受限和 assembly 执行失败这几类结果
- 暴露最近一次可见 assembly outcome，并要求该结果跨服务重启后仍可读取
- 在当前 `Projects` 体验中通过直接编辑或粘贴原始 `app-manifest` 内容来提交 assembly 请求
- 在当前 `Projects` 体验中展示 assembly 可用性、受限状态、请求入口和结果反馈

Out of scope:

- `project-derived-app-verification`
- `project-derived-app-upgrade-support`
- 让 linked worktree 成为第一版 assembly source
- 新建独立的全局 lifecycle / engineering 控制台
- 重写 repository-owned assembly contract、compatibility suite 或 assembly engine
- 顺手扩展成 CI/CD、部署、托管集成或长期运行任务编排产品

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 未绑定仓库的软件项目仍然必须保持可用，只是不应伪造正常 assembly 行为
- 现有 repository-owned assembly tooling、contract 和 compatibility fixtures 仍然是 assembly 的规范边界
- 第一版 project-scoped assembly 不会要求用户从 linked worktree 中选择来源
- 第一版 assembly 结果展示不要求引入持久 run history 浏览能力，但最近一次可见 outcome 需要持久化
- 这次 change 不会默认把 verification、runtime smoke 或 upgrade 行为并入 assembly 范围
