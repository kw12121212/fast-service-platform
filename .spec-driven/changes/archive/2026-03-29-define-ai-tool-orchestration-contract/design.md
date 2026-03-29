# Design: define-ai-tool-orchestration-contract

## Approach

The change will define a new normative contract for AI tool orchestration and connect it to the repository's existing AI-readiness assets.

The implementation will add a machine-readable orchestration asset plus AI-facing playbook updates that describe:

- which repository-owned tooling entrypoint is the default for each workflow
- what prerequisite assets or paths an AI agent should inspect before invoking the tool
- what output or result semantics the AI should expect back
- when the AI may retry, fall back to a compatible wrapper, or stop and surface a blocker

The current repository-owned tooling facade and workflow-specific wrappers will remain the execution layer. This change only standardizes how AI contributors are expected to choose and sequence those tools.

## Key Decisions

- AI is modeled as a tool orchestrator, not a direct replacement implementation.
  Rationale: this matches the repository's current product direction and keeps behavior anchored to repository-owned contracts and tooling.

- The repository should publish orchestration behavior as both prose playbooks and machine-readable facts.
  Rationale: AI contributors need lightweight structured inputs for repeatable tool choice, while humans still need readable operational guidance.

- The orchestration contract should sit alongside existing assembly, verification, lifecycle, and tooling specs rather than being hidden inside one existing contract.
  Rationale: the behavior being standardized is cross-cutting and applies to multiple platform workflows.

## Alternatives Considered

- Add AI-direct assembly and verification as the next step.
  Rejected because the current goal is to teach AI contributors to use repository-owned tooling, not to bypass it.

- Rely on scattered quickstart and playbook prose without a dedicated orchestration contract.
  Rejected because it leaves too much room for AI agents to infer their own workflow ordering and fallback behavior.

- Encode orchestration behavior only in implementation scripts.
  Rejected because that would not create a language-neutral, AI-readable contract describing when and why tools should be used.
