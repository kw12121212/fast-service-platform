# migrate-admin-forms-to-dynamic-form

## What

将 Users、Projects、Kanban 三个页面的手写创建表单替换为平台级 `DynamicForm` 组件，让这些页面通过声明式 `FormDescriptor` 驱动表单渲染，而不是每个实体各自维护独立的 `<form>`、`useState`、校验和 mutation feedback 逻辑。

## Why

`DynamicForm` 组件已经实现并通过测试，但没有任何实际页面使用它。这意味着：

1. 组件契约未经过真实 backend-backed 场景验证
2. AI 生成新实体时没有可参考的 DynamicForm 使用范例
3. 三个页面各自重复了相同的表单骨架（useState + handleSubmit + field rendering + MutationStatus）

通过迁移这三个页面，平台能够证明 DynamicForm V1 契约在真实管理场景下可用，同时建立 AI 可复制的表单集成模式。

## Scope

**In scope:**
- Users 页面创建表单 → DynamicForm（username: text, displayName: text, email: text）
- Projects 页面创建表单 → DynamicForm（projectKey: text, projectName: text, description: textarea）
- Kanban 页面创建表单 → DynamicForm（boardName: text）
- 迁移后页面行为（表单提交、成功反馈、列表刷新、错误展示）与迁移前完全一致
- 迁移后删除各页面中因迁移而不再需要的表单相关代码（useState、handleSubmit、Input/Label/textarea 直接引用等）

**Out of scope:**
- Tickets 页面表单迁移（需要 dynamic select options，V1 不支持）
- DynamicReport 使用范围扩展
- Dashboard 页面变更
- Roles/permissions 页面变更
- DynamicForm 组件本身的 API 变更或功能扩展
- 后端 API 变更

## Unchanged Behavior

- 三个页面的表单提交行为与迁移前完全一致（相同的 payload、相同的 reload 行为）
- 表单校验行为保持不变（required 字段仍然必填）
- MutationStatus 反馈保持不变（submitting / success / error 消息内容不变）
- 其他页面（Dashboard、Tickets、Roles）不受影响
- DynamicForm 组件本身不做修改
- 前端 data-access 约定不变
