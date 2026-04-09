# Playbook: Prepare Descriptor-Driven Management Module

适用场景：

- 已经有 `solution-to-manifest plan`，并且需要把一个窄范围业务模块继续收敛成 machine-readable descriptor
- 需要在 `planning / recommendation` 与 `app-manifest` 之间增加一层受边界约束的 management-module 资产
- 需要确保首条 descriptor-driven 路径复用 `dynamic form` 与 `dynamic report`，而不是发明平行交互系统

## 先读这些

1. `docs/ai/context.yaml`
2. `docs/ai/solution-to-manifest-planning-contract.json`
3. `docs/ai/schemas/solution-to-manifest-planning.schema.json`
4. `docs/ai/solution-to-manifest-recommendation-contract.json`
5. `docs/ai/schemas/solution-to-manifest-recommendation.schema.json`
6. `docs/ai/descriptor-driven-management-module-contract.json`
7. `docs/ai/schemas/descriptor-driven-management-module.schema.json`
8. `docs/ai/management-modules/department-directory.management-module.json`
9. `docs/ai/app-assembly-contract.json`
10. `docs/ai/structured-app-template-contract.json`
11. `docs/ai/template-classifications/default-derived-app-template-map.json`
12. `frontend/src/components/admin/dynamic-form.tsx`
13. `frontend/src/components/admin/dynamic-report.tsx`

## 默认原则

- descriptor 是 `bounded upstream asset`，不是 assembly runtime contract。
- descriptor 必须依赖合法的 planning output；如果有 recommendation guidance，可以引用，但不能跳过 planning。
- 当前首条 path 只支持窄范围 management-module shape：一个主记录类型、一个 report/list 视图、一个 create-edit form 视图。
- 首条 path 必须复用 `dynamic form` 与 `dynamic report`。
- descriptor 不能扩展关闭的 `module-registry`，也不能把 workflow-specific page generation 一起吞进来。
- 真正传给 assembly tooling 的仍然是 standalone `app-manifest`。

## 标准顺序

1. 先确认 `solution-to-manifest plan` 已经收敛了 application identity 与 base module selection
2. 如有需要，再读 `solution-to-manifest recommendation`，只把可选 guidance 作为 descriptor 输入之一
3. 按 `descriptor-driven-management-module-contract.json` 准备 bounded descriptor：
   - 模块标识
   - route path
   - primary entity
   - report columns
   - form fields
   - manifest preparation boundary
   - template bindings
4. 把 descriptor 里的 platform module 选择继续保持为 standalone `app-manifest`
5. 把 descriptor 的 form/report/backend facts 收敛成 controlled module-generation inputs
6. 确认输出仍然落在已声明的 slot host 或 customization zone 内
7. 再执行：

```bash
./scripts/platform-tool.sh assembly scaffold <manifest-path> <absolute-output-dir>
```

## descriptor 至少要表达什么

- 上游 planning asset，以及可选 recommendation asset
- 当前应用 identity 与 manifest identity
- 当前 management module 的显示名称、主实体、路由入口
- `dynamic-report` 需要的列定义
- `dynamic-form` 需要的字段定义
- 输出落在哪些 slot host / customization zone
- 明确声明：
  - `standaloneManifestRequired: true`
  - `usesWorkflowGeneration: false`
  - `extendsClosedModuleRegistry: false`
  - `descriptorIsAssemblyRuntimeInput: false`

## 常见坑

- 把 descriptor 当成可以直接传给 assembly tooling 的 runtime input
- 跳过 planning，直接从 prose 或 recommendation 猜 descriptor
- 把 descriptor 扩成任意 CRUD / low-code DSL
- 在首条 path 中加入 workflow-specific 页面生成
- 绕开 `structured-app-template-contract.json` 与 template classification map，直接定义未分类输出路径
