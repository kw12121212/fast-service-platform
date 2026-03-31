# Design: migrate-admin-forms-to-dynamic-form

## Approach

对每个目标页面执行相同的迁移模式：

1. 将手写的 `<form>` 替换为 `<DynamicForm>` 组件调用
2. 将每个手写 `useState` 字段转为 `FormDescriptor` 的 `fields` 数组条目
3. 将手写 `handleSubmit` 的 payload 组装逻辑移入 `onSubmit` 回调
4. 将 `MutationStatus` 的 `status`/`error` 传入 `mutationStatus`/`mutationError` props
5. 表单提交成功后在 `onSubmit` 回调内执行 `reload()` 和字段重置（通过 `key` prop 重建组件实例）

每个页面的 `FormDescriptor` 定义为页面级常量，不引入额外的抽象层。

迁移完成后，页面中不再直接引用 `Input`、`Label`（表单相关部分），也不再维护独立的表单状态变量。页面文件仅保留数据获取、列表渲染、和 `DynamicForm` 的 descriptor 定义。

## Key Decisions

1. **通过 `key` prop 重置 DynamicForm** — 表单提交成功后需要清空字段，但 DynamicForm 不暴露 `reset()` 方法。使用 `key={resetKey}` + `setResetKey(v => v + 1)` 的方式强制重建组件实例来达到重置效果。这避免了为 DynamicForm 增加 reset API。

2. **descriptor 定义为页面级常量** — 不为每个实体创建独立的 descriptor 文件，descriptor 直接写在页面文件中。理由：descriptor 与页面耦合紧密（字段名、placeholder、校验规则都是页面级的），过早抽离会增加间接层而不会带来复用收益。

3. **Kanban 虽然只有 1 个字段但仍迁移** — 为了平台一致性。AI 和贡献者看到所有简单表单都用 DynamicForm 时，不会困惑为什么 Kanban 是特例。

4. **保留页面布局结构不变** — DynamicForm 替换的是 `<form>` 元素及其内部内容，不改变页面的 Card 布局、PageHeader、ResourceState 等外部结构。

## Alternatives Considered

- **增加 DynamicForm reset 方法** — 可以给 DynamicForm 加一个 `useImperativeHandle` 暴露 `reset()`。但当前只有这一个使用场景，且 `key` 方案已经足够，遵循 YAGNI 原则暂不引入。
- **抽离 descriptor 到独立文件** — 如果未来有多个页面需要同一个 descriptor（如 create 和 edit 复用），可以再抽离。当前每个页面只有一个 create 表单，抽离不增加价值。
