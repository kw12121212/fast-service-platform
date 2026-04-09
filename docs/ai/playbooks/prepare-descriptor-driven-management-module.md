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
- 首条 path 必须复用 `dynamic form` 与 `dynamic report`，当需要 workflow 时复用 `workflow panel`。
- descriptor 不能扩展关闭的 `module-registry`。
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
- `dynamic-report` 需要的列定义（简单表格视图）或 sections 定义（多区块仪表盘）
- `dynamic-form` 需要的字段定义
- 输出落在哪些 slot host / customization zone
- 明确声明：
  - `standaloneManifestRequired: true`
  - `usesWorkflowGeneration: true`（当包含 workflow 时）或 `false`（当不含 workflow 时）
  - `extendsClosedModuleRegistry: false`
  - `descriptorIsAssemblyRuntimeInput: false`

## report descriptor 的两种路径

### 1. columns 路径（简单表格视图）

适用于单一表格展示的场景，如部门列表、用户目录等。

```json
{
  "componentId": "dynamic-report",
  "title": "Department Directory",
  "columns": [
    {"id": "departmentCode", "label": "Department Code"},
    {"id": "departmentName", "label": "Department Name"},
    {"id": "managerName", "label": "Manager"},
    {"id": "active", "label": "Active"}
  ]
}
```

参考示例：`docs/ai/management-modules/department-directory.management-module.json`

### 2. sections 路径（多区块仪表盘）

适用于需要混合可视化类型的仪表盘场景，如统计概览、数据分布等。

```json
{
  "componentId": "dynamic-report",
  "title": "Department Overview",
  "sections": [
    {
      "sectionKey": "summary",
      "type": "summary-cards",
      "cardKeys": ["totalDepartments", "activeDepartments", "totalEmployees"]
    },
    {
      "sectionKey": "details",
      "type": "table",
      "title": "Department Details",
      "columns": [
        {"id": "departmentCode", "label": "Department Code"},
        {"id": "departmentName", "label": "Department Name"}
      ]
    },
    {
      "sectionKey": "headcountChart",
      "type": "bar-chart",
      "title": "Headcount by Department"
    }
  ]
}
```

支持的 section 类型：
- `summary-cards`: 汇总卡片，需要 `cardKeys` 数组声明指标键名
- `table`: 表格，需要 `columns` 数组和 `title`
- `bar-chart`: 柱状图，需要 `title`
- `line-chart`: 折线图，需要 `title`
- `pie-chart`: 饼图，需要 `title`

sectionKey 映射：
- 每个 section 的 `sectionKey` 对应 dynamic-report 组件的 `ReportResults` 数据键
- 例如 `sectionKey: "summary"` 对应 `results.summary` 提供卡片数据

参考示例：`docs/ai/management-modules/department-overview.management-module.json`

### 何时使用哪种路径

- 使用 **columns** 当：只需要一个简单表格展示记录列表
- 使用 **sections** 当：需要混合可视化（卡片 + 表格 + 图表）或仪表盘布局
- 两者互斥：一个 report descriptor 只能包含 `columns` 或 `sections`，不能同时包含两者

## workflow descriptor（可选工作流）

### 何时包含 workflow

当业务模块需要单记录工作流交互（提交、审批、拒绝、转办）时，在 `managementModule` 中添加可选的 `workflow` section。不需要工作流的模块保持不填即可。

### workflow section 如何映射到 WorkflowPanel

workflow descriptor 的属性与 `WorkflowPanel` 组件的 `WorkflowDescriptor` 类型 1:1 对应：

| descriptor 属性 | WorkflowPanel 映射 |
|---|---|
| `entityName` | 工作流实体名称 |
| `stateLabel` | 状态字段标签 |
| `assigneeLabel` | 指派人字段标签 |
| `commentLabel` | 评论字段标签 |
| `commentPlaceholder` | 评论输入占位文本 |
| `historyTitle` | 工作流历史标题 |
| `actions` | 可执行操作数组（submit/approve/reject/reassign） |
| `backendWorkflowService` | 后端工作流服务声明（可选） |

### workflow descriptor 示例

```json
{
  "componentId": "workflow-panel",
  "entityName": "LeaveRequest",
  "stateLabel": "Status",
  "assigneeLabel": "Assignee",
  "commentLabel": "Comment",
  "commentPlaceholder": "Add a comment about this action...",
  "historyTitle": "Workflow History",
  "actions": [
    {"action": "submit", "label": "Submit"},
    {"action": "approve", "label": "Approve"},
    {"action": "reject", "label": "Reject"},
    {"action": "reassign", "label": "Reassign", "requiresAssignee": true}
  ],
  "backendWorkflowService": {
    "workflowServiceName": "leave_request_workflow_service",
    "ddlReference": "backend/src/main/resources/sql/workflows/leave-request.sql"
  }
}
```

### 边界约束

- 包含 `workflow` 时，`boundaries.usesWorkflowGeneration` 必须为 `true`
- 不包含 `workflow` 时，`boundaries.usesWorkflowGeneration` 必须为 `false`
- 包含 `workflow` 时，`generatedModuleInputs.frontend` 必须包含 `workflowComponentId: "workflow-panel"`
- action 类型限定为 submit、approve、reject、reassign（与 `WorkflowActionType` 一致）

参考示例：`docs/ai/management-modules/leave-request.management-module.json`

## 常见坑

- 把 descriptor 当成可以直接传给 assembly tooling 的 runtime input
- 跳过 planning，直接从 prose 或 recommendation 猜 descriptor
- 把 descriptor 扩成任意 CRUD / low-code DSL
- 在首条 path 中加入 workflow-specific 页面生成但不设置 `usesWorkflowGeneration: true`
- 绕开 `structured-app-template-contract.json` 与 template classification map，直接定义未分类输出路径
