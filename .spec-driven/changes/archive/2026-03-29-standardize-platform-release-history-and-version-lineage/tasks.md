# Tasks: standardize-platform-release-history-and-version-lineage

## Implementation

- [x] 新增 release history / version lineage 主 spec delta，定义 release history index、lineage、support status 和 supported upgrade path
- [x] 修改 derived-app lifecycle / upgrade 主 spec delta，要求 evaluation / advisory / execution 依赖标准化 release history 输入
- [x] 修改 AI repository readiness delta，要求 AI 入口暴露 release history / lineage 资产、playbook 和 lookup 入口
- [x] 增加机器可读的 platform release history / lineage 资产与 schema
- [x] 增加 repository-owned release lookup / upgrade target selection 入口
- [x] 更新 AI quickstart、context 和 lifecycle / upgrade playbook，纳入 history / lineage 事实来源和使用方式
- [x] 增加对应的测试或结构校验，证明 history / lineage 资产和入口可用

## Testing

- [x] `bun run lint` passes
- [x] `node --test scripts/app-assembly.test.mjs` passes
- [x] release history / lineage contract、schema、entrypoint 的结构校验或对应单元测试通过

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify standardize-platform-release-history-and-version-lineage` passes
- [x] 相关 history / lineage 资产、AI 文档和仓库入口与 proposal scope 一致
