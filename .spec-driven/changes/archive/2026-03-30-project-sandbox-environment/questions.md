# Questions: project-sandbox-environment

## Open

<!-- No open questions -->

## Resolved

- [x] Q: 第一版 sandbox 支持 main repository worktree，还是只支持 linked worktree？
  Context: 这决定 sandbox 是否继续沿用当前 worktree/merge 的 project-scoped 工程主路径。
  A: 只支持 linked worktree。

- [x] Q: sandbox 生命周期是怎样的？
  Context: 这决定第一版是短暂执行环境，还是包含持久资源和临时资源的双层模型。
  A: 先创建永久保存的 image，再用该 image 创建临时 container。

- [x] Q: 初始化动作由谁负责，运行什么脚本？
  Context: 这决定第一版 sandbox 是自动推断环境，还是显式脚本驱动。
  A: 创建 image 时运行 `init-image.sh`，创建 container 后运行 `init-project.sh`。

- [x] Q: 初始化脚本路径是否固定？
  Context: 这决定第一版是否需要支持 worktree 级配置覆盖。
  A: 两个脚本都有默认路径，但允许通过 worktree 属性配置覆盖。

- [x] Q: image 和 container 的粒度与并发限制是什么？
  Context: 这决定 sandbox 的状态模型和 UI 复杂度。
  A: image 按 linked worktree 粒度持久化；每个 linked worktree 同时最多一个活动 container。

- [x] Q: 第一版是否允许平台隐式懒创建 image 或 container？
  Context: 这决定读状态与执行动作是否会混在一起，也决定失败边界是否可预测。
  A: 不允许，image 和 container 生命周期都由平台显式创建或销毁。

- [x] Q: 初始化脚本返回非零退出码时如何处理？
  Context: 这决定失败语义是否清晰，也影响前端如何展示 sandbox 可用性。
  A: 视为失败，并保留清晰失败状态。

- [x] Q: 第一版 sandbox 使用什么 image/container 运行时？
  Context: 这决定 backend 的宿主机前提、失败模式和工程实现路径。
  A: 第一版暂时使用 `podman`。
