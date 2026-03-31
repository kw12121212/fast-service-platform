# Tasks: migrate-admin-forms-to-dynamic-form

## Implementation

- [x] 迁移 Users 页面创建表单到 DynamicForm（替换 useState + handleSubmit + form markup）
- [x] 迁移 Projects 页面创建表单到 DynamicForm（替换 useState + handleSubmit + form markup）
- [x] 迁移 Kanban 页面创建表单到 DynamicForm（替换 useState + handleSubmit + form markup）
- [x] 清理各页面迁移后不再需要的未使用 import（Input、Label、FormEvent 等如已无其他引用）

## Testing

- [x] Lint 通过
- [x] 现有 DynamicForm 单元测试通过
- [x] 现有 DynamicReport 单元测试通过

## Verification

- [x] 验证三个页面的表单提交行为与迁移前一致（UI 层面：字段显示、提交、成功反馈、列表刷新）
- [x] 验证 DynamicForm descriptor 正确映射了每个页面的字段类型和校验规则
- [x] 验证无未使用的 import 残留
