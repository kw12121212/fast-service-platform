# Tasks: define-derived-app-lifecycle-and-upgrade-contract

## Implementation

- [x] 定义 derived-app lifecycle / upgrade 的主 spec delta，覆盖平台来源、兼容状态和升级输入语义
- [x] 修改 app assembly contract delta，要求生成应用暴露生命周期元数据和升级指引
- [x] 修改 AI repository readiness delta，要求 AI 入口暴露 lifecycle / upgrade 资产和仓库验证路径
- [x] 增加机器可读 lifecycle contract 与 schema
- [x] 增加仓库拥有的 lifecycle / upgrade playbook 与 AI context 入口
- [x] 增加仓库拥有的升级评估或升级准备脚本入口

## Testing

- [x] `bun run lint` passes
- [x] `node --test scripts/app-assembly.test.mjs` passes
- [x] 生命周期 / 升级 contract 的结构校验或对应单元测试通过

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify define-derived-app-lifecycle-and-upgrade-contract` passes
- [x] 相关 AI 文档、contract、schema、playbook 和脚本入口与 proposal scope 一致
