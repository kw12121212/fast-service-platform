# Design: standardize-platform-release-history-and-version-lineage

## Approach

通过“release history index + lineage contract + repository-owned lookup entrypoint”的方式，把平台版本关系提升为仓库拥有的标准能力。

设计上分三层：

1. 新增一个独立的 release history / lineage 主 spec，定义什么是 release identity、lineage parent、advisory archive、support window 和 supported upgrade path。
2. 修改 derived-app lifecycle / upgrade 主 spec，要求 upgrade evaluation、advisory 和 execution 读取标准化的 history / lineage 输入，而不是只依赖当前 release 的局部元数据。
3. 修改 AI repository readiness，使 AI 可以发现 release history 资产、lookup playbook 和仓库入口。

第一版实现只要求“版本历史可读、upgrade path 可查、target 可选”，不负责自动执行跨多版本合并。

## Key Decisions

- 把 release history / lineage 单独做成主 spec。
  理由：这部分已经超出 advisory 的范围，属于平台版本管理本身，不应继续塞进 lifecycle 或 advisory 的局部要求里。
- history 资产必须显式声明 supported upgrade paths，而不是只推断 semver。
  理由：平台当前还没有稳定的 semver-compatible 自动迁移能力，显式路径比隐式版本推断更可靠。
- repository-owned 入口先做 lookup / selection，不直接承诺 multi-hop auto-upgrade。
  理由：先把“支持哪些路径”标准化，再谈自动执行，风险更低。

## Alternatives Considered

- 继续只在 `platform-release.json` 里保留 `currentRelease` 和 `previousRelease`。
  放弃原因：这只能覆盖单步差异，无法支撑长期 lineage 和 upgrade target 选择。
- 只写 prose 文档说明 release 历史。
  放弃原因：AI 和多语言工具无法稳定消费，也无法被 repository-owned 入口直接使用。
- 直接进入 multi-hop automatic upgrade execution。
  放弃原因：当前还没有正式的 history / path contract，直接执行会把路径选择和执行逻辑耦合在一起。
