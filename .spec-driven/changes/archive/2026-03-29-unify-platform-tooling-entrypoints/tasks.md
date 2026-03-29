# Tasks: unify-platform-tooling-entrypoints

## Implementation

- [x] 新增统一 tooling-entrypoints 主 spec delta，定义 façade 和兼容 wrapper 语义
- [x] 修改 AI repository readiness delta，要求 AI 入口优先暴露统一 tooling façade
- [x] 增加统一 repository-owned tooling façade，并接入现有 assembly / verify / lifecycle / upgrade 脚本能力
- [x] 更新 machine-readable contracts、quickstart 和 playbooks，统一指向新的默认入口
- [x] 增加测试或结构校验，证明统一入口和兼容 wrapper 都可用

## Testing

- [x] `bun run lint` passes
- [x] `node --test scripts/app-assembly.test.mjs` passes
- [x] 与 tooling façade 相关的 repository-owned wrapper 验证通过

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify unify-platform-tooling-entrypoints` passes
- [x] 统一入口、兼容 wrapper 和 AI 文档与 proposal scope 一致
