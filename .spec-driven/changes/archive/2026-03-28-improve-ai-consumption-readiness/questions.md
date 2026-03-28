# Questions: improve-ai-consumption-readiness

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the change include a machine-readable repository context manifest, or stay limited to narrative documentation and scripts?
  Context: This determines whether the AI-readiness contract should support tool-driven agents directly instead of only human-readable guidance.
  A: Include the machine-readable context manifest in the same change.
- [x] Q: Should the smoke path be documented as a manual checklist or required as automated repository-owned commands?
  Context: This determines whether AI agents must reconstruct integration validation steps themselves.
  A: Require automated smoke and verification entrypoints.
- [x] Q: Should the change playbooks stay narrow or cover the repository's high-frequency extension scenarios?
  Context: This determines how much common implementation ambiguity the first AI-readiness change should remove.
  A: Cover the high-frequency change scenarios.
