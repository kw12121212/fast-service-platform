# Questions: project-worktree-management

## Open

<!-- No open questions -->

## Resolved

- [x] Q: `repair` 和 `prune` 是后续增强，还是 V1 范围内能力？
  Context: 这决定本次 proposal 是只做 create/delete/list，还是同时要求仓库维护能力。
  A: `repair` 和 `prune` 直接纳入 V1。

- [x] Q: worktree 根目录应该放在仓库内，还是仓库外的固定位置？
  Context: 这决定 worktree 的路径规则和平台如何定义默认目录约定。
  A: 放在仓库外的平行目录；例如仓库目录是 `proj`，则 worktree 根目录是同级 `proj-worktrees`。

- [x] Q: detached HEAD 是否属于允许的正常 worktree 管理状态？
  Context: 这决定平台是否需要为 detached HEAD 单独定义创建或修复路径。
  A: 不允许，detached HEAD 视为受限状态。

- [x] Q: 删除 worktree 时，如果存在未提交或未 push 内容，平台是否可以自动处理？
  Context: 这决定平台是否允许自动 stash、自动提交、自动推送或自动放弃本地状态。
  A: 不可以。必须人工放弃或人工提交后再删除。

- [x] Q: 一个分支是否允许对应多个 worktree？
  Context: 这决定分支到 worktree 的映射关系和创建冲突策略。
  A: 不允许，一个分支只允许对应一个 worktree。

- [x] Q: worktree 目录命名应该如何从分支名生成？
  Context: 这决定 worktree 路径是否可预测，以及非法路径字符如何处理。
  A: 以分支名命名目录，并把 `/` 等不适合作为目录名的字符转换为 `-`。

- [x] Q: 没有 upstream 的 worktree 是否可以删除？
  Context: 这决定“未 push 内容”判断在没有 upstream 的场景下如何落地。
  A: 不可以，没有 upstream 也视为不可删除。

- [x] Q: worktree 管理是只做 backend 能力，还是要前后端一起交付？
  Context: 这决定 proposal 是纯后端工程组件，还是当前项目管理体验的一部分。
  A: 前后端一起交付。
