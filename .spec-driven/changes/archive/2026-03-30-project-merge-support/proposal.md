# project-merge-support

## What

为当前已经具备 `repository binding + project Git management + project worktree management`
的软件项目，定义第一版 `project-scoped merge support`。

这次 change 计划把 `merge-support` 从“平台边界里的保留能力”推进到可观察、可验证的项目级行为：

- 项目可以针对已管理的 `linked worktree` 查看 merge 支持上下文
- merge 来源必须是一个已管理的 non-main worktree
- merge 目标必须是同一仓库里的另一个 existing local branch
- 平台实际执行 merge，而不只是展示只读预检查
- 如果 merge 会产生冲突，平台返回失败结果，并且不把仓库留在进行中的 merge 状态
- 前端在当前 `Projects` 体验中展示 merge 候选、受限原因和成功或失败反馈

## Why

当前仓库已经实现了项目仓库绑定、Git 上下文查看、安全分支切换，以及项目级 worktree 管理，但 `merge-support`
仍然只有平台边界声明和一个后端占位接口。

如果继续停在这里，平台的工程支持组件链路会出现明显断点：

- 项目已经可以创建并管理并行 worktree，但还不能从这些 worktree 完成受控的合并动作
- `merge-support` 作为平台内建工程支持组件仍然缺少明确的行为边界
- AI 或贡献者无法在当前 project-scoped workflow 中完成“从特性 worktree 合并回目标分支”的最小闭环

把 merge 明确定义为“从受管 worktree 发起，到另一个本地目标分支，并以冲突失败为保守默认”之后，平台才算把工程支持能力从查看、切换、并行工作目录进一步推进到受限写操作。

## Scope

In scope:

- 为已绑定本地 Git 仓库的软件项目定义 project-scoped merge support
- 要求 merge 来源必须是项目已管理的 non-main linked worktree
- 要求 merge 来源 worktree 必须是 clean 状态
- 要求 merge 来源 worktree 的当前分支作为 merge source
- 要求 merge 目标必须是同一仓库中的另一个 existing local branch
- 要求 merge 执行成功后，项目可见的 Git 和 worktree 上下文刷新为最新状态
- 要求 merge 冲突以失败处理，且失败后不保留进行中的 merge 状态
- 要求前端在当前 `Projects` 体验中展示 merge 候选、受限原因和操作反馈

Out of scope:

- 从 main repository worktree 直接发起 merge
- 从未受项目管理的工作目录发起 merge
- 自动冲突解决
- `rebase`、`cherry-pick`、`squash merge`、merge strategy 选择
- remote `fetch`、`pull`、`push`、credentials 或 hosting 集成
- sandbox-environment
- 脱离当前 Projects 体验的新全局 Git / Engineering 控制台
- 顺手改写既有 repository binding、branch switch 或 worktree management 的行为边界

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 未绑定仓库的软件项目仍然必须保持可用，不应伪造 merge 数据或 merge 操作
- 现有项目仓库绑定、分支切换和 worktree 管理行为保持不变
- 平台仍然不自动替贡献者做高风险 Git 决策，尤其是不自动解决冲突或自动处理未提交内容
- 平台不会因为这次 change 默认扩展成完整的通用 Git 客户端或远程代码托管集成
