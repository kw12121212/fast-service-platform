# Tasks: add-derived-app-upgrade-execution-path

## Implementation

- [x] 修改 derived-app lifecycle / upgrade 主 spec delta，加入 upgrade execution、dry-run 和 manual-intervention 语义
- [x] 修改 AI repository readiness delta，要求 AI 入口暴露 execution contract、playbook 和仓库入口
- [x] 增加 machine-readable upgrade plan / execution 资产与 schema
- [x] 增加 repository-owned upgrade execution 入口，至少支持 `dry-run`
- [x] 更新 AI quickstart、context 和 upgrade playbook，纳入 execution 事实来源和使用方式
- [x] 增加对应测试或结构校验，证明 execution path 可生成 plan 并输出人工介入项

## Testing

- [x] `bun run lint` passes
- [x] `node --test scripts/app-assembly.test.mjs` passes
- [x] upgrade execution contract / schema / entrypoint 的结构校验或对应单元测试通过

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify add-derived-app-upgrade-execution-path` passes
- [x] 相关 execution 资产、AI 文档和仓库入口与 proposal scope 一致
