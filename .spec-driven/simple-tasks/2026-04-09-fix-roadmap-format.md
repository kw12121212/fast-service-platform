# Simple Task Log

## Task

修复 `.spec-driven/roadmap/` 里 milestone 文档的格式，使其满足 `spec-driven.js verify-roadmap` 和 `roadmap-status` 的结构与状态校验要求。

## What was done

- 将所有 milestone 文档统一为脚本要求的固定 `##` 章节结构：`Goal`、`In Scope`、`Out of Scope`、`Done Criteria`、`Planned Changes`、`Dependencies`、`Risks`、`Status`、`Notes`
- 将多行 planned change 描述改写为 canonical 单行格式：`- \`<change-name>\` - Declared: <planned|complete> - <summary>`
- 为已归档的 roadmap items 回填 `Declared: complete`，为未开始项回填 `Declared: planned`
- 将 `solution-input-to-assembly-planning` 中已过时的 `solution-input-gap-analysis` 项更新为与 archive 真实状态一致的 `standardize-ai-solution-input-model`
- 运行 roadmap 校验脚本，确认 `verify-roadmap` 与 `roadmap-status` 都返回 `valid: true`

## Spec impact

none

## Follow-up

None
