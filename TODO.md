# TODO

临时待办清单，基于 `2026-03-28` 对当前仓库、specs 和基线校验结果的扫描整理。

## P0

- 收束当前未提交的 `project-git-management` 变更，避免 archive、主 specs 和源码实现继续漂移。

## P1

- 增加 backend `/service/*` 契约级验证，补足当前以 service impl 和 bootstrap 为主的测试覆盖。
- 去掉 dashboard 对 demo 数据的硬编码依赖，避免默认依赖固定 `roleId=200` 和“第一个 project”。
- 补齐 Git 管理受限态的前端测试，至少覆盖 `unbound`、`dirty working tree`、`detached HEAD`。

## P2

- 细化 Git 相关错误语义，区分无效仓库路径、受限状态和命令执行失败，减少误导性报错。
- 为下一阶段 engineering-support 能力补 spec，优先明确 `worktree`、`merge`、`sandbox` 的边界和最小行为。

## P3

- 刷新根文档中的“当前已实现能力”说明，在相关改动稳定后补齐 repository binding 和 Git management 基线。

## Notes

- 当前基线校验已通过：`backend mvn -q test`、`frontend bun run test`、`bun run build`、`bun run lint`。
- 这是临时文档，不替代 `.spec-driven/` 中的正式 proposal、tasks 和 specs。
