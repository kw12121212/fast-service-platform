# workflow-component

## What

新增一个平台级可复用的前端 `WorkflowPanel` 组件能力。调用方通过声明式 `WorkflowDescriptor`、当前流程实例数据和允许执行的动作，就可以渲染受限范围内的企业工作流界面，包括当前状态、负责人或办理人、可执行流转动作、评论输入和历史记录展示，而不需要在每个业务模块里重复手写相同的流程 UI。

这次 change 同时把工作流能力明确为平台拥有的复用交互模式，而不是某个单一业务页面的私有实现。

## Why

平台已经完成了 `dynamic-form-component` 和 `dynamic-report-component`，分别覆盖“结构化录入”和“结构化展示”。但企业内部管理应用里还有一类高频交互没有被平台吸收：围绕单条业务记录执行提交、审核、批准、驳回、重新分配、补充说明和查看处理历史的工作流操作。

如果没有平台级工作流组件，AI 生成的新业务模块仍然需要为每个流程页面重新发明状态条、动作按钮区、评论区、办理人信息和历史区，既重复，又容易在交互和边界上失控。平台拥有一个受限的工作流组件，可以让这类常见企业流程交互以统一方式被复用，同时保持“数据获取和流转执行归调用方所有”的现有架构边界。

## Scope

**In scope:**
- 前端 `WorkflowPanel` React 组件，接受声明式 `WorkflowDescriptor`
- `WorkflowDescriptor` 描述单记录或单流程实例的当前状态、阶段顺序、可见元数据区域、动作定义和历史区展示规则
- V1 支持的可见能力：
  - 当前状态或阶段展示
  - 当前 assignee / owner / reviewer 等责任人信息展示
  - 有界的动作按钮区，如 `submit`、`approve`、`reject`、`reassign`
  - 必选评论输入
  - 历史记录列表展示
- 组件只负责渲染和收集动作提交参数，不自行请求 backend，也不自己决定状态流转规则
- 动作执行通过调用方提供的回调交给现有页面或 data-access 层处理
- 动作结果与失败反馈沿用现有 mutation feedback 约定
- 将工作流组件及相关 descriptor 类型从平台组件入口导出
- 在当前 runnable admin frontend 提供一个最小真实 workflow 示例页面或示例区，证明组件如何接入真实 backend-backed 工作流数据与动作执行

**Out of scope (V1):**
- BPM 引擎、通用流程编排器或规则引擎
- 多记录批量工作流
- 拖拽式流程设计器
- 条件分支编辑器、表达式系统或 SLA 自动化规则
- 组件自己持有后端工作流状态机定义
- 新增一个独立 workflow 业务域来证明组件存在

## Unchanged Behavior

- 现有 `DynamicForm`、`DynamicReport` 和各管理页面的行为保持不变
- 现有 frontend data-access 约定保持不变；请求逻辑不会迁入工作流组件内部
- 本次 change 不要求新增 workflow backend 引擎或改变 `/service/*` 边界
- 现有最小管理写流程仍按当前页面能力工作，不因新增工作流组件而被替换
