# Tasks: add-platform-release-delta-and-upgrade-advisory

## Implementation

- [x] 修改 derived-app lifecycle / upgrade 主 spec delta，加入 release delta 和 advisory 语义
- [x] 修改 AI repository readiness delta，要求 AI 入口暴露 release delta / advisory 资产和仓库入口
- [x] 增加机器可读的 platform release delta / upgrade advisory 资产与 schema
- [x] 增加 repository-owned advisory 脚本入口
- [x] 更新 AI quickstart、context 和 upgrade playbook，纳入 advisory 事实来源和使用方式
- [x] 增加对应的测试或结构校验，证明 advisory 资产和入口可用

## Testing

- [x] `bun run lint` passes
- [x] `node --test scripts/app-assembly.test.mjs` passes
- [x] advisory contract / schema / entrypoint 的结构校验或对应单元测试通过

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify add-platform-release-delta-and-upgrade-advisory` passes
- [x] 相关 advisory 资产、AI 文档和仓库入口与 proposal scope 一致
