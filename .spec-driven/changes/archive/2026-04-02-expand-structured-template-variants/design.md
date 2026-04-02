# Design: expand-structured-template-variants

## Approach

所有改动都限于 `docs/ai/template-classifications/default-derived-app-template-map.json` 和 delta spec 文件。不修改应用代码、schema 文件或合约文件。

### 新增条目

每条新条目遵循 `project-management` 已有的 module-fragment 模式：
- `matchKind: "exact"`，使用文件相对路径
- `unitType: "module-fragment"`
- `ownership: "module-managed"`
- `upgradeBehavior: "review-before-overwrite"`
- `moduleId` 指向声明该模块的 ID

**admin-shell**（2 条）：
- `frontend/src/app/admin-shell.tsx` — admin-shell 贡献的平台 shell 布局
- `frontend/src/features/dashboard/dashboard-page.tsx` — admin-shell 贡献的仪表盘页

**user-management**（2 条）：
- `frontend/src/features/users/users-page.tsx`
- `backend/src/main/java/com/fastservice/platform/backend/user/UserServiceImpl.java`

**role-permission-management**（2 条）：
- `frontend/src/features/roles/role-permissions-page.tsx`
- `backend/src/main/java/com/fastservice/platform/backend/access/AccessControlServiceImpl.java`

**kanban-management**（2 条）：
- `frontend/src/features/kanban/kanban-page.tsx`
- `backend/src/main/java/com/fastservice/platform/backend/kanban/KanbanServiceImpl.java`

**ticket-management**（2 条）：
- `frontend/src/features/tickets/tickets-page.tsx`
- `backend/src/main/java/com/fastservice/platform/backend/ticket/TicketServiceImpl.java`

**module-selection**（1 条）：
- `frontend/src/app/module-selection.ts` — 分类为 `customization-zone` / `derived-managed`，因为衍生应用在此配置激活的模块 profile
- `upgradeBehavior: "preserve-by-default"`，与其他 derived-managed 区一致

### 不需要新增 slot 定义

现有 slot 定义（`frontend-admin-routes`、`frontend-admin-navigation`、`backend-table-contract`、`backend-service-contract`、`backend-demo-data`）已覆盖 slot-host 层。新增条目均为 module-fragment 或 customization-zone，不需要新的 slot 宿主。

### 验证

新增条目后，对照 `derived-app-template-map.schema.json` 做 JSON schema 校验，确认所有新条目格式合规。

## Key Decisions

- **module-selection.ts 归类为 derived-managed customization-zone，而非 stable-template** — 此文件是衍生应用配置激活模块 profile 的主要入口，自动刷新会覆盖衍生应用的选择。
- **admin-shell 条目归类为 module-fragment，而非 stable-template** — 即使 admin-shell 是 required-core，其贡献的文件（shell 布局、仪表盘页）在不同衍生应用中可能有品牌差异，`module-managed` + `review-before-overwrite` 保证升级安全而不强制刷新。
- **排除工程支撑基础设施文件** — `GitRepositoryInspector.java`、`ProjectSandboxManager.java`、bootstrap 工具等属于平台内部基础设施，不是可选模块贡献的生成输出，不应进入衍生应用模板映射。

## Alternatives Considered

- **为每个 assembly profile 分别生成变体映射文件** — 例如 `core-admin-template-map.json` 只列 required-core 的三个模块。拒绝，理由是过早：AI agent 可按 `moduleId` 与当前激活 profile 交叉过滤，无需独立的每 profile 映射文件。
- **将工程支撑 port 接口归类** — 例如 `GitRepositoryManagementPort.java`。拒绝，这些是平台内部合约，不是供衍生应用自定义的模块输出。
