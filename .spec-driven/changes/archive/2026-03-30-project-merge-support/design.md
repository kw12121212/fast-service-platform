# Design: project-merge-support

## Approach

这个 change 继续沿用当前仓库已经确立的主路径：`software project -> bound local repository -> project-scoped engineering support`。

第一版 merge support 不会被设计成通用 Git 控制台，而是作为当前 Projects 体验中的一个受限工程操作：

1. 以后端项目上下文为锚点
   后端继续围绕项目已绑定仓库暴露 merge 上下文和 merge 执行入口，而不是引入新的全局仓库身份。

2. 以 managed linked worktree 作为唯一 merge source
   平台只允许从项目已经管理的 non-main worktree 发起 merge，用它当前检出的 branch 作为 source。

3. 以 safety-first 作为默认边界
   来源 worktree 必须 clean；目标必须是另一个 existing local branch；发生冲突直接失败；失败后仓库不能停留在进行中的 merge 状态。

4. 以前后端一起交付为基线
   前端需要在当前 Projects 页面里暴露 merge 候选、受限原因、操作提交和结果反馈，使 merge support 与 repository binding / Git context / worktree management 保持同一条项目主路径。

## Key Decisions

- merge 来源只允许是项目已管理的 linked worktree。
  理由：用户已经明确要求 merge 以 worktree 为源头；这样也能让 merge support 明确建立在现有 worktree-management 能力之上，而不是回退成通用分支 merge。

- main repository worktree 不是合法 merge source。
  理由：这能把“日常主分支上下文”与“特性工作目录来源”区分开，降低第一版 merge support 的状态混淆。

- 来源 worktree 的当前已检出 branch 就是 merge source，不额外支持从 worktree 中选择别的 source ref。
  理由：这样最贴合 worktree 本身的语义，也让前端与后端的状态模型更直接。

- 第一版做实际 merge execution，而不是只做 readiness 预览。
  理由：只有真正执行 merge，平台的工程支持能力才从“只读检查”推进到可完成的工作流闭环。

- merge 冲突默认失败，并要求失败后不保留进行中的 merge 状态。
  理由：用户已经确认“有冲突就提示失败”；再进一步要求清理进行中 merge 状态，可以避免把仓库遗留在半完成状态。

- 第一版不进入 remote、rebase、cherry-pick 或自动冲突解决。
  理由：这些能力会明显扩大范围，也会把平台带向更危险、更不稳定的 Git 自动化。

## Alternatives Considered

- 允许从任意 existing local branch 发起 merge。
  否决原因：这会削弱 worktree management 与 merge support 的关联，也会把第一版边界扩成通用分支操作。

- 只做 merge readiness，不做实际 merge。
  否决原因：这不足以完成用户要的“从 worktree 合并到目标分支”的最小闭环。

- 冲突时保留原地，要求用户手动处理未完成 merge。
  否决原因：这会让平台把仓库留在不稳定状态，不符合当前安全优先边界。

- 直接把 merge support 做成新的全局 Engineering 页面。
  否决原因：当前仓库已经把仓库绑定、Git 管理和 worktree 管理都放在 Projects 体验里，merge support 应该延续同一信息架构。
