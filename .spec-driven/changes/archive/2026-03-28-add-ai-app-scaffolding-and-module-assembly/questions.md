# Questions: add-ai-app-scaffolding-and-module-assembly

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should AI extend the current repository app directly, or derive an independent new application skeleton from the repository?
  Context: This determines whether the change is mainly repository contribution guidance or a reusable app-assembly contract.
  A: AI should derive an independent new application skeleton from this repository.

- [x] Q: Should `software project`, `ticket`, and `kanban` remain part of the mandatory minimum baseline?
  Context: This determines the platform-core versus optional-module boundary.
  A: They should become optional modules that AI may freely choose to include or exclude.

- [x] Q: Is the scope limited to specs and docs, or should scaffolding and assembly implementation be included?
  Context: This determines whether the change stops at planning artifacts or must include executable repository behavior.
  A: Scaffolding and assembly implementation are in scope.

- [x] Q: Does the repository need index files specifically designed for AI consumption?
  Context: AI needs stable facts about modules and assembly rules, not only prose entry docs.
  A: Yes. The change must include layered indexes for repository entry, modules, and application assembly.
