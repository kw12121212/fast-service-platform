# project-worktree-management

## What

为已绑定本地 Git 仓库的软件项目补齐第一版 `worktree management` 能力，并把它纳入当前 Projects 体验。

这次 change 会把当前已经存在的“项目绑定仓库 + 查看 Git 上下文 + 安全切换分支”继续向前推进，增加围绕项目的 worktree 行为边界：

- 项目可查看当前仓库的 worktree 列表和状态
- 项目可基于现有分支创建 worktree
- 项目可在满足安全限制时删除 worktree
- 项目可执行 `repair` 和 `prune` 维护操作
- 前端在当前 Projects 体验中展示 worktree 状态、受限原因和操作结果

这不是做一个脱离项目上下文的通用 Git 工具，而是继续围绕“软件项目同时承担交付范围和工程范围”这个现有模型补齐工程支持能力。

## Why

当前仓库已经把项目仓库绑定、仓库摘要、分支可见性和安全分支切换做成了可运行能力，但 `worktree / merge / sandbox` 仍停留在平台边界声明或占位接口层面。

对这个平台来说，`worktree management` 是最接近现有仓库管理能力、也最容易形成第一版可用工程支持组件的下一步：

- 它直接复用现有“项目绑定本地仓库”的主路径
- 它比 `merge-support` 更容易先定义清晰的安全边界
- 它比 `sandbox-environment` 更不容易膨胀成环境编排问题
- 它能让“一个项目对应多个并行工程工作目录”的平台价值开始具备可见成果

如果这一步继续停留在“边界里有名词、代码里有 port、但没有行为规格”的状态，平台的工程支持组件仍然不够可信，也不利于 AI 或贡献者在既有项目上下文内继续扩展。

## Scope

In scope:

- 为已绑定本地 Git 仓库的软件项目定义 backend worktree 管理行为
- 为当前 Projects 体验定义 frontend worktree 可见性、受限状态和操作反馈
- 要求 worktree 根目录位于仓库外的平行目录，例如 `proj/` 对应同级 `proj-worktrees/`
- 要求 worktree 子目录默认以分支名命名，并把 `/` 等不适合作为目录名的字符转换为 `-`
- 要求一个分支在同一仓库上下文中只允许对应一个 worktree
- 要求 detached HEAD 视为受限状态，不允许正常的 worktree 创建或修复类管理操作
- 要求 worktree 删除前必须是 clean，且不存在任何未 push 内容
- 要求没有 upstream 的 worktree 也视为不可删除
- 要求平台提供 `repair` 和 `prune` 作为仓库维护操作
- 要求平台清楚暴露“必须人工提交或人工放弃后才能删除”的限制，而不是自动替用户处理

Out of scope:

- `merge-support`
- `sandbox-environment`
- 自动 stash、自动 commit、自动 push、自动放弃未提交内容
- 支持 detached HEAD 作为正常 worktree 工作流
- 一个分支对应多个 worktree
- 脱离项目绑定关系的全局 Git 管理台
- 在这次 change 中顺手扩展新的业务域或新的 AI workflow
- 改写当前 repository binding、Git context 或 branch switch 的既有能力边界

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 软件项目的仓库绑定、仓库摘要展示和现有安全分支切换行为保持不变
- 未绑定仓库的项目仍然必须保持可用，不应伪造 worktree 数据
- 平台对 `merge-support` 和 `sandbox-environment` 的边界保持不变，不因这次 change 被默认视为已实现
- 平台不替贡献者做危险 Git 决策，尤其是不自动处理未提交或未推送内容
