# Questions: project-merge-support

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Merge 来源必须是任意 local branch，还是必须来自项目已管理的 worktree？
  Context: 这决定第一版 merge-support 是扩成通用 Git merge，还是建立在现有 worktree-management 主路径上。
  A: merge 来源必须是项目已管理的 worktree，并从该 worktree 发起到其他目标分支。

- [x] Q: 来源 worktree 有未提交内容时，平台是否可以继续 merge？
  Context: 这决定第一版 merge 的安全边界，以及平台是否会替用户处理脏工作区。
  A: 不可以。有未提交内容时不允许 merge。

- [x] Q: 第一版 merge-support 是只做预检查，还是实际执行 merge？如果有冲突如何处理？
  Context: 这决定 proposal 的最小闭环是否包含真正的 merge 写操作。
  A: 第一版需要实际执行 merge；如果发生冲突，则提示失败，并且不保留进行中的 merge 状态。
