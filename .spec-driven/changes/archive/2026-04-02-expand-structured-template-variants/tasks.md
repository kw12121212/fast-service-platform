# Tasks: expand-structured-template-variants

## Implementation

- [x] 在 `default-derived-app-template-map.json` 中为 `admin-shell` 新增 2 条 module-fragment 条目（`admin-shell.tsx`、`dashboard-page.tsx`）
- [x] 在 `default-derived-app-template-map.json` 中为 `user-management` 新增 2 条 module-fragment 条目（`users-page.tsx`、`UserServiceImpl.java`）
- [x] 在 `default-derived-app-template-map.json` 中为 `role-permission-management` 新增 2 条 module-fragment 条目（`role-permissions-page.tsx`、`AccessControlServiceImpl.java`）
- [x] 在 `default-derived-app-template-map.json` 中为 `kanban-management` 新增 2 条 module-fragment 条目（`kanban-page.tsx`、`KanbanServiceImpl.java`）
- [x] 在 `default-derived-app-template-map.json` 中为 `ticket-management` 新增 2 条 module-fragment 条目（`tickets-page.tsx`、`TicketServiceImpl.java`）
- [x] 在 `default-derived-app-template-map.json` 中为 `module-selection.ts` 新增 1 条 customization-zone 条目（`derived-managed`，`preserve-by-default`）
- [x] 更新 delta spec `core/structured-app-template-system.md`，新增"模板映射需覆盖所有模块注册表中每个模块至少一条分类条目"的要求

## Testing

- [x] 对 `default-derived-app-template-map.json` 运行 JSON schema 校验（`derived-app-template-map.schema.json`），确认所有新条目格式合规
- [x] 确认 JSON 文件本身语法无误（可用 `node -e "require('./...')"` 验证）

## Verification

- [x] 确认 7 个模块注册表模块在模板映射中各有覆盖条目：admin-shell（2 条 module-fragment）、user-management（2 条）、role-permission-management（2 条）、project-management（2 条）、kanban-management（2 条）、ticket-management（2 条）已通过 moduleId 链接；project-repository-management 无独立前端页面或服务实现，其唯一贡献（project_repository_binding 表）由 backend-table-contract slot-host 条目覆盖，符合 proposal.md 范围限定（"前端页面和后端服务实现"）
- [x] 确认现有 15 条条目的 `unitType`、`ownership`、`upgradeBehavior` 字段未改动
- [x] 确认 `structured-app-template-contract.json` 中的 slot 定义未改动
- [x] 确认 delta spec 与 proposal.md 范围一致
