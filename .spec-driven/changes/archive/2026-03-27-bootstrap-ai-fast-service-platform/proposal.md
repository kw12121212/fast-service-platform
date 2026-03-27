# bootstrap-ai-fast-service-platform

## What

Initialize the repository as a documentation-first, AI-friendly fast-service platform.
Add root-level collaboration documents, ignore rules, and reserved backend/frontend workspaces so future implementation changes have a clear baseline.

## Why

The repository currently contains only a license file, which is not enough for coordinated human and AI development.
This project needs an explicit stack baseline, repository layout, and agent-readable operating rules before backend and frontend scaffolding begins.

## Scope

In scope:
- Create and populate `.gitignore`, `README.md`, `AGENTS.md`, and `RTK.md`.
- Reserve `backend/` and `frontend/` directories with stack-specific guidance.
- Record a bootstrap proposal and delta specs for the repository foundation.

Out of scope:
- Generating the actual Java backend project.
- Generating the actual Vite/React frontend project.
- Installing dependencies or adding runnable application code.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):
- Preserve the existing `LICENSE` file unchanged.
- Keep the repository safe for future spec-driven changes instead of baking in premature framework or package-manager decisions.
