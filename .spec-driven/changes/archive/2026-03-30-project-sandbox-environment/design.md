# Design: project-sandbox-environment

## Approach

这次 change 延续当前已经形成的工程支持主路径：

`project -> bound repository -> managed linked worktree -> engineering action`

第一版 sandbox 不引入新的全局 Engineering 控制台，而是继续挂在当前 `Projects` 体验中，
把 sandbox 收敛成三个彼此清晰的层次：

1. `linked worktree sandbox context`
   为每个受管 linked worktree 暴露 sandbox 可用性、受限状态、image 状态、container 状态和脚本路径来源。
2. `persistent image lifecycle`
   平台为单个 linked worktree 通过 `podman` 创建持久 image，并在该流程中执行 `init-image.sh`。
3. `temporary container lifecycle`
   平台只允许基于该 linked worktree 的现有 `podman` image 创建临时 container，并在容器创建后执行 `init-project.sh`。

这样可以把 sandbox 精确定义为现有 project-scoped workflow 的下一层工程支持能力，
而不是扩成一个脱离项目上下文的通用容器资源系统。

## Key Decisions

- 只支持 `linked worktree`
  Rationale: 当前仓库的工程支持主路径已经围绕 managed linked worktree 展开，sandbox 沿用同一对象模型最稳妥。

- image 按 linked worktree 粒度持久化
  Rationale: 用户已经确认 image 不是按仓库共享，而是按具体 worktree 保留，避免跨 worktree 状态耦合。

- container 是临时资源
  Rationale: 第一版只补齐受控环境准备与短生命周期执行，不引入长期运行环境管理。

- 每个 linked worktree 同时最多一个活动 container
  Rationale: 这样 sandbox 状态模型最简单，前后端都能清楚表达“无容器 / 有一个活动容器 / 受限”。

- `init-image.sh` 与 `init-project.sh` 均采用“默认路径 + worktree 属性覆盖”
  Rationale: 需要给项目约定留出默认路径，同时允许特定 worktree 明确覆盖，不把配置系统无边界扩大。

- 第一版 sandbox 运行时使用 `podman`
  Rationale: 用户已经明确指定使用 `podman`；第一版应把 host runtime 假设写清楚，而不是模糊成抽象占位。

- image 与 container 生命周期都由平台显式触发
  Rationale: 第一版不做隐式懒创建，避免把失败来源隐藏在读取状态或其他工程动作里。

- 初始化脚本非零退出码即失败
  Rationale: sandbox 第一版必须偏保守，不能把部分初始化成功误报成可用环境。

- 宿主机缺少 `podman` 时暴露受限状态，而不是让项目视图崩溃
  Rationale: 既然第一版运行时是外部主机能力，就必须把前提缺失当成受限状态清晰表达出来。

## Alternatives Considered

- 同时支持 main repository worktree 与 linked worktree
  Rejected: 这会立刻扩大对象模型和受限状态矩阵，偏离当前以 linked worktree 为中心的工程支持路径。

- 让同一仓库共享一个 image
  Rejected: 用户已确认 image 应按 linked worktree 粒度持久化，不应在 proposal 阶段改成共享资源。

- 允许一个 linked worktree 拥有多个活动 container
  Rejected: 第一版无需引入并行容器调度，状态和 UI 复杂度会明显上升。

- 自动推断依赖安装或项目初始化逻辑
  Rejected: 当前边界已经明确要求通过 `init-image.sh` 和 `init-project.sh` 驱动初始化，而不是平台自作主张推断。

- 第一版直接提供交互式终端
  Rejected: 这会把 change 从“sandbox lifecycle”扩展到“完整容器交互体验”，明显超出当前范围。
