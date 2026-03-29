# define-ai-tool-orchestration-contract

## What

This change defines a repository-owned AI tool-orchestration contract for platform workflows.

It makes the repository describe, in machine-readable and playbook form, how AI contributors are expected to invoke platform tooling for application assembly, generated-app verification, release advisory, lifecycle evaluation, upgrade target lookup, and upgrade execution.

The change treats AI as a tool orchestrator rather than a replacement implementation of those workflows. The repository will expose the default invocation order, allowed entrypoints, expected inputs, result interpretation, and failure-handling guidance for tool-driven AI usage.

## Why

The repository already has repository-owned tooling and contracts for assembly, verification, advisory, lifecycle, and upgrade workflows. What is still missing is an explicit contract that teaches AI contributors how to use those tools correctly.

Without that contract, AI agents can drift toward re-implementing platform behavior directly instead of invoking the repository-owned tooling facade and playbooks. That makes behavior less stable, increases variance between agents, and weakens the value of the repository's standard workflows.

This change keeps the platform tool-first: AI should learn how to use the project's tools and contracts, not silently bypass them.

## Scope

In scope:

- Define an AI tool-orchestration contract that states AI contributors SHOULD use repository-owned tooling entrypoints before attempting workflow-specific reimplementation.
- Define the expected orchestration sequence for common tool-driven workflows, including assembly, generated-app verification, advisory lookup, upgrade target selection, upgrade evaluation, and upgrade execution.
- Add machine-readable assets and AI-facing guidance that explain default entrypoints, fallback behavior, and failure-handling expectations.
- Update AI readiness documentation and playbooks so the repository clearly teaches AI how to use existing tools.

Out of scope:

- Adding AI-direct assembly or AI-direct verification as a new first-class implementation path.
- Replacing the current Node or Java compatible implementations.
- Introducing new platform business modules or changing module boundaries.
- Redesigning the unified tooling facade itself beyond what is necessary to document and expose orchestration expectations.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- The repository continues to provide Node and Java compatible implementations for app assembly and generated-app verification.
- The unified repository-owned tooling facade remains the default invocation surface for supported workflows.
- Existing assembly, verification, advisory, lifecycle, lineage, and upgrade execution contracts remain valid and must not be weakened.
