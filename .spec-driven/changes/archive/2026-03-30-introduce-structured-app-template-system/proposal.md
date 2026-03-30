# introduce-structured-app-template-system

## What

定义一个 repository-owned 的 structured app template system，用来明确生成结果里的稳定模板层、slot 边界、模块装配片段和允许覆盖点。

这次 change 不改变“solution input -> app-manifest -> assembly tooling”的主路径，而是在 assembly 输出侧增加一层更清晰的模板语义：

- 哪些生成文件属于平台管理的稳定模板
- 哪些位置是可替换 slot
- 哪些文件或片段来自模块装配
- 哪些覆盖属于派生应用定制，后续 upgrade 不应无条件覆盖

## Why

当前平台已经把输入层、module registry、assembly contract、verification、lifecycle 和 upgrade 路径标准化了，但输出层仍然主要表现为“按当前实现复制和改写文件”。

这带来几个问题：

- 生成结果的稳定模板边界还不够明确
- 定制点和平台管理点没有独立 contract
- upgrade execution 虽然已经存在，但缺少更清晰的模板/slot 语义支撑
- AI 虽然会用工具了，但还不容易判断哪些输出区域可以安全覆盖，哪些应该保留派生应用改动

如果不补这层，平台更像“受控文件拼装器”，还不像有长期演进边界的模板基础库。

## Scope

In scope:

- 定义 structured app template system 的 machine-readable contract
- 定义模板层、slot、模块装配片段和覆盖点的边界语义
- 定义 generated output 中哪些路径或片段由平台管理，哪些属于派生应用可定制区
- 接入 AI context、assembly contract、lifecycle / upgrade contract 和 playbooks
- 为后续 upgrade planning / execution 提供更清晰的模板边界事实来源

Out of scope:

- 不重写现有前后端生成逻辑为全新模板引擎
- 不引入新的模板语言或外部模板依赖
- 不直接做“可视化模板编辑器”或 runtime theme system
- 不在这次 change 中扩大产品边界或引入新的应用形态

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- `solution input -> app-manifest -> assembly tooling` 的输入路径保持不变
- `app-manifest` 仍然是 assembly 的直接输入
- 现有 Java-owned platform tooling、generated-app verification、lifecycle 和 upgrade workflows 仍然有效
- 当前模块选择、compatibility suite 和 generated output invariants 不能被弱化
