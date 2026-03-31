# Design: add-derived-app-runtime-smoke

## Approach

新增一个独立的 derived-app runtime smoke 验证层，目标不是替代现有 generated-app verify，而是在它之后补上一层最小运行证明。

V1 路径保持很窄：

1. 以一个 derived app 根目录作为输入
2. 准备 generated backend 运行时并启动 backend，或在允许前提下复用已可用实例
3. 启动 generated frontend dev server
4. 通过 generated frontend 的 `/service/*` 代理请求 generated backend
5. 校验至少一个到两个最小只读接口返回有效 JSON 数组
6. 将 backend 启动失败、frontend 启动失败、代理不可达、响应非 JSON 或响应结构不符，区分为可定位失败

baseline demo 不再只停留在 contract verify + backend test + frontend build/lint，而要复用这条 derived-app runtime smoke path，证明演示样本本身也能走真实代理链路。

在 spec 结构上，把 runtime smoke 作为独立核心能力建模，再补充 AI readiness 和 baseline demo 对这条能力的可发现性与使用要求。

## Key Decisions

- **Runtime smoke 与 generated-app verification 分层** — 结构合同验证和运行时证明解决的是不同问题。把两者混成一个合同会让验证语义膨胀，也会模糊失败原因。
- **V1 只验证最小 `/service/*` 读链路** — 先证明 derived app 能启动、代理、返回合法 JSON，而不是一步扩成完整业务回归。
- **通过 frontend 代理而不是 backend 直连作为成功标准** — 当前仓库的真实联调边界就是 generated frontend 到 generated backend 的 `/service/*` 代理，这比 backend 直连更贴近实际使用路径。
- **baseline demo 必须复用同一路径** — demo 是仓库对外展示的标准样本，不能只有“能 build”和“结构没坏”的弱证明。
- **优先复用现有 smoke 经验而不是创造全新测试体系** — 主 workspace 已有 `verify-fullstack.sh` 模式，derived app runtime smoke 应该沿用相同原则，减少认知和维护成本。

## Alternatives Considered

- **把 runtime smoke 直接并入 generated-app verification contract**：看起来入口更少，但会让当前合同从“结构与资产验证”膨胀成“运行时测试合同”，破坏已有边界。
- **只强化 baseline demo 验证，不做通用 derived-app 路径**：能解决 demo 展示问题，但不能解决 roadmap 指出的 derived app 普遍信任缺口。
- **直接引入浏览器级 E2E 自动化**：覆盖更广，但对当前阶段过重，也不符合 V1 先补最小运行证明的目标。
