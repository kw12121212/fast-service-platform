# Tasks: add-derived-app-runtime-smoke

## Implementation

- [x] Define delta specs for a repository-owned derived-app runtime smoke validation path, including the minimum observable success criteria and failure boundaries.
- [x] Add a repository-owned validation entrypoint that runs runtime smoke against a derived app directory by starting the generated backend and generated frontend, then checking proxied `/service/*` responses.
- [x] Reuse or adapt the current smoke helpers so runtime smoke reports whether failure happened during backend startup, frontend startup, proxy reachability, or response-shape validation.
- [x] Wire the committed baseline demo to the derived-app runtime smoke path so demo validation includes a real runtime proxy proof instead of contract/build checks only.
- [x] Update AI-facing guidance and demo guidance so contributors can discover when to run derived-app runtime smoke and how it differs from generated-app verification.

## Testing

- [x] `./scripts/platform-tool.sh generated-app verify <derived-app-dir>` still passes for the committed baseline demo.
- [x] The derived-app runtime smoke entrypoint passes for `demo/baseline-demo/` and proves proxied `/service/*` JSON responses through the generated frontend.
- [x] Existing repository validation remains green, including backend tests, frontend test/build/lint, and the current main-workspace full-stack smoke path.

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-brainstorm/scripts/spec-driven.js verify add-derived-app-runtime-smoke` passes.
- [x] Confirm runtime smoke remains a separate validation layer from generated-app contract verification instead of silently redefining that contract.
