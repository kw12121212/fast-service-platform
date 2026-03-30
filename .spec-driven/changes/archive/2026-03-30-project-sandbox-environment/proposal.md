# project-sandbox-environment

## What

为当前已经具备 `repository binding + project Git management + project worktree management + project merge support`
的软件项目，定义第一版 `project-scoped sandbox environment`。

这次 change 计划把 `sandbox-environment` 从“平台边界里的保留能力”推进到可观察、可验证的项目级行为：

- 项目可以针对已管理的 `linked worktree` 查看 sandbox 上下文
- sandbox 只支持 `linked worktree`，不支持 main repository worktree
- 每个 linked worktree 拥有一个按 worktree 粒度持久化保存的 image
- 平台基于该 image 创建临时 container，并限制每个 linked worktree 同时最多一个活动 container
- 第一版 sandbox 暂时使用 `podman` 作为 image 和 container 运行时
- image 创建时运行 `init-image.sh`
- container 创建后运行 `init-project.sh`
- 两个脚本都有默认路径，但允许通过 worktree 属性覆盖
- 前端在当前 `Projects` 体验中展示 sandbox 状态、受限原因和操作结果

## Why

当前仓库已经实现了项目仓库绑定、Git 上下文查看、安全分支切换、项目级 worktree 管理和受限 merge，
但 `sandbox-environment` 仍然只有平台边界声明和一个后端占位接口。

如果继续停在这里，平台的工程支持组件链路仍然缺一段关键能力：

- 项目已经可以围绕 linked worktree 组织分支开发与合并，但还缺少受平台控制的环境准备与临时执行空间
- `sandbox-environment` 作为平台内建工程支持组件仍然缺少清晰的项目级行为边界
- AI 或贡献者无法在当前 project-scoped workflow 中完成“为某个 worktree 准备依赖环境并创建临时运行容器”的最小闭环

把 sandbox 明确定义为“linked worktree 级持久 image + 临时 container，并由显式脚本初始化”之后，
平台才算把工程支持能力从仓库、worktree、merge 进一步推进到受控环境准备，而不把自己扩展成通用容器平台。

## Scope

In scope:

- 为已绑定本地仓库的软件项目定义 project-scoped sandbox context
- 只为项目已管理的 `linked worktree` 提供 sandbox 能力
- 要求 image 按 linked worktree 粒度持久化保存
- 要求 container 基于已存在 image 临时创建
- 要求每个 linked worktree 同时最多一个活动 container
- 要求第一版 sandbox 使用 `podman`，并在宿主机缺少 `podman` 时暴露清晰受限状态
- 要求 image 创建时执行 `init-image.sh`
- 要求 container 创建后执行 `init-project.sh`
- 要求两个脚本都有默认路径，并允许通过 worktree 属性覆盖
- 要求前端在当前 `Projects` 体验中展示 sandbox 状态、受限原因、创建结果和销毁结果
- 要求初始化脚本非零退出码作为失败处理，并保留清晰失败状态

Out of scope:

- main repository worktree sandbox
- 一个 linked worktree 对应多个活动 container
- 远程主机、集群或容器编排
- UI 内交互式终端
- 自动推断依赖安装逻辑而不经过 `init-image.sh`
- 隐式懒创建 image 或 container
- 把 worktree 属性扩展成通用 sandbox 配置中心
- 顺手改写既有 repository binding、branch switch、worktree management 或 merge support 的边界

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 未绑定仓库的软件项目仍然必须保持可用，不应伪造正常 sandbox 数据或正常 sandbox 操作
- main repository worktree 仍然不是当前工程支持链路里的 sandbox source
- 现有项目仓库绑定、分支切换、worktree 管理和 merge 行为保持不变
- 平台不会因为这次 change 默认扩展成完整的通用容器平台、远程执行平台或编排系统
- 平台不会把 `podman` 依赖伪装成仓库内部纯实现；如果宿主机缺少它，必须清楚暴露为受限状态
- 平台不会跳过 `init-image.sh` 或 `init-project.sh` 自动推断环境初始化逻辑
