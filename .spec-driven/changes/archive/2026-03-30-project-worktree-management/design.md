# Design: project-worktree-management

## Approach

这个 change 把 `worktree management` 视为当前“项目绑定本地仓库”能力的自然延伸，而不是单独做一套通用 Git 控制台。

整体设计分三层：

1. `project-scoped backend behavior`
   后端继续以“项目 -> 已绑定仓库 -> 工程支持上下文”的方式暴露 worktree 列表、创建、删除、`repair`、`prune`。

2. `safety-first state model`
   worktree 管理不是无条件开放的仓库操作，而是显式受限的工程支持能力。平台需要把这些限制作为可观察行为暴露出来：
   - detached HEAD 不是正常 worktree 管理状态
   - 一个分支只能对应一个 worktree
   - 删除前必须 clean
   - 只要存在未 push 内容，就必须人工处理后才能删除
   - 没有 upstream 也属于不可安全删除状态

3. `projects experience integration`
   前端不新增独立工作台，而是在当前 Projects 体验里显示 worktree 信息、受限原因和操作结果，使它与现有 repository binding / branch context / branch switch 处于同一项目工程区域。

## Key Decisions

- 先做 `worktree-management`，不和 `merge-support`、`sandbox-environment` 一起推进。
  理由：这一步与当前项目仓库管理主路径最连续，范围也更容易闭合。

- worktree 根目录使用仓库外平行目录，而不是仓库内子目录。
  理由：这样更符合 worktree 作为并行工作目录的预期，也能避免把额外工作目录混入仓库本身。

- worktree 子目录默认映射分支名，并做最小必要的目录名清洗。
  理由：这样让目录命名可预测、便于 AI 和人类理解，同时避免 `/` 等字符带来的路径问题。

- 一个分支只允许对应一个 worktree。
  理由：这让分支到 worktree 的关系保持简单、可解释，也减少“同分支多工作目录”带来的状态混淆。

- 删除限制采用保守模型，不允许平台自动代为清理风险状态。
  理由：本仓库定位是 AI 友好的企业工程平台，不应把“自动帮用户处理未提交或未推送内容”作为默认行为。

- `repair` 和 `prune` 直接纳入 V1。
  理由：如果只支持创建和删除，不支持仓库维护，worktree 状态很容易随着目录漂移或陈旧记录而失真，第一版可用性不足。

- 前后端一起交付。
  理由：worktree 既是 backend 能力，也是当前项目管理体验里的工程支持能力；只有后端没有前端，不符合当前仓库“直接可见、可操作”的基线风格。

## Alternatives Considered

- 直接先做 `merge-support`。
  否决原因：会更快碰到冲突表达、失败恢复、合并策略和人工干预边界，范围更大。

- 直接先做 `sandbox-environment`。
  否决原因：这会把问题扩大到环境生命周期、隔离边界和运行约束，明显超出当前最小下一步。

- 把 worktree 做成独立页面或全局 Git 控制台。
  否决原因：当前仓库已经以“项目绑定仓库”为核心工程上下文，脱离项目会削弱现有模型的一致性。

- 允许删除时自动 stash、自动 push 或自动放弃本地内容。
  否决原因：这会把平台变成替用户做高风险 Git 决策的工具，不符合当前保守边界。

- 允许一个分支对应多个 worktree。
  否决原因：虽然理论上可以讨论更灵活的模型，但它会明显增加命名、状态和删除策略的复杂度，不适合作为 V1。
