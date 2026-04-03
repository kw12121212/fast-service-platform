# Design: project-derived-app-assembly

## Approach

这个 change 延续仓库当前已经建立的主路径：`software project -> bound local repository -> project-scoped lifecycle orchestration -> repository-owned tooling`。

第一版 `project-scoped derived-app assembly` 采用以下方式收敛范围：

1. 以后端项目上下文为锚点
   后端围绕软件项目的已绑定仓库暴露 assembly context 和 assembly request 入口，而不是引入新的全局 derived-app 身份模型。

2. 复用 repository-owned assembly workflow
   项目级 assembly 只负责收集项目范围内的输入、校验状态并委派给现有 repository-owned assembly tooling，不在项目层重新实现 assembly contract 或生成逻辑。

3. 第一版只使用主仓库上下文
   即使项目存在 managed linked worktrees，首版 assembly 也只针对项目绑定的主仓库上下文开放，避免把第一版流程扩成“从任意工作目录生成”。

4. 输入与结果保持可观察
   请求输入限定为有效的 `app-manifest` 与显式绝对输出目录；首版允许输出目录预先存在，但必须为空；结果需要区分请求校验失败、受限状态和 assembly 执行失败，并暴露持久化的最近一次可见 outcome。

5. 以前后端一起交付为基线
   前端继续在当前 `Projects` 体验里展示 assembly 的可用性、受限原因、原始 `app-manifest` 提交入口和结果反馈，而不是开辟新的独立控制台。

## Key Decisions

- 第一版 assembly 只基于项目绑定的主仓库上下文。
  理由：主 milestone 明确要求把现有 derived-app lifecycle 工具收敛到 project scope；首版先固定主仓库上下文，能减少状态模型复杂度，也为 verification 的首版依赖建立稳定前提。

- assembly 请求必须同时提供有效 `app-manifest` 和显式绝对输出目录。
  理由：这与现有 backend/frontend assembly spec 中的外部可观察输入保持一致，也避免引入隐式默认输出位置。

- 输出目录可以预先存在，但必须为空。
  理由：这样可以支持明确的目录复用，同时避免把生成结果写进已有内容混杂的目录。

- 最新可见 outcome 足够，首版不要求持久运行历史浏览，但该 outcome 需要持久化。
  理由：后续 verification 首版依赖最近一次成功 assembly 输出；如果服务重启后结果丢失，项目级 lifecycle 状态会失真。

- 项目级 assembly 必须区分输入无效与执行失败。
  理由：只有这样，用户和后续自动化流程才能明确判断是请求本身有问题，还是 repository-owned assembly workflow 在执行中失败。

- 不在前端或项目层重写 assembly 逻辑。
  理由：milestone 的边界已经明确要求保留 repository-owned tooling ownership，避免 UI 重新实现 assembly。

- 首版前端输入形态只要求直接编辑或粘贴原始 `app-manifest`。
  理由：这满足首版最小闭环，同时避免把 proposal 范围扩展到文件导入或更复杂的 manifest 编辑器能力。

## Alternatives Considered

- 允许第一版从 linked worktree 选择 assembly source。
  否决原因：这会让 assembly 与现有 worktree 管理状态深度耦合，扩大首版路径复杂度，也削弱“主仓库上下文是默认生命周期起点”的约束。

- 继续只保留手工脚本入口，不提供 project-scoped assembly。
  否决原因：这样无法形成 lifecycle 体验闭环，也无法为后续 project-scoped verification 提供稳定前置输出。

- 第一版同时做 assembly 和 verification。
  否决原因：verification 已有独立 roadmap item，而且首版 verification 明确依赖成功的 assembly 输出；两个 change 一起推进会模糊边界。

- 第一版直接引入持久运行历史和任务浏览。
  否决原因：这会把范围推向作业编排或 CI 风格系统，不符合当前 milestone 的最小闭环目标。
