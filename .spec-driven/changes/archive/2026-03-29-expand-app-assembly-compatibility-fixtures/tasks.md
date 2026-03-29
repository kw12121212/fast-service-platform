# Tasks: expand-app-assembly-compatibility-fixtures

## Implementation

- [x] 修改 app assembly compatibility-suite 主 spec delta，要求更完整的 fixture coverage
- [x] 修改 AI repository readiness delta，要求 AI 入口暴露扩展后的 compatibility fixture 集
- [x] 增加新的 valid / invalid compatibility fixtures 和 suite 元数据
- [x] 更新 Node / Java compatibility 验证逻辑与测试断言，覆盖新增 fixtures
- [x] 更新 AI quickstart 或相关文档，对扩展后的 compatibility coverage 做说明

## Testing

- [x] `bun run lint` passes
- [x] `node --test scripts/app-assembly.test.mjs` passes
- [x] `./scripts/verify-app-assembly.sh` passes

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify expand-app-assembly-compatibility-fixtures` passes
- [x] 扩展后的 fixture 集、测试和文档与 proposal scope 一致
