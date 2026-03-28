# improve-ai-consumption-readiness

## What

Improve the repository's readiness for AI-driven contribution by adding a clear AI-first repository entrypoint, a machine-readable context manifest, high-frequency change playbooks, automated verification and smoke entrypoints, and troubleshooting guidance for the current local stack.
The change is limited to repository-consumption and execution readiness. It does not add in-product AI interaction surfaces or expand the business-domain baseline.

## Why

The repository already positions AI as its primary consumer, but the practical path for an AI agent to start work remains fragmented across multiple documents and manual commands.
That increases ambiguity in three places that matter most for agent success: how to establish context, where to make common changes, and how to verify the result end to end.
This change closes that gap by turning existing conventions into explicit, repository-native assets that AI can read and execute with lower failure risk.

## Scope

In scope:
- Add an AI-first quickstart entrypoint that tells an agent what to read, where common work lives, and how to verify changes.
- Add a machine-readable repository context manifest covering required references, directory responsibilities, toolchain assumptions, verification commands, and hard boundaries.
- Add high-frequency change playbooks for the most common repository extension paths across backend, frontend, and demo-data or integration updates.
- Add automated verification entrypoints so AI can run backend, frontend, and full-stack smoke validation through stable repository commands instead of reconstructing command sequences ad hoc.
- Add troubleshooting guidance for common environment and integration failures in the current Java, Maven, Node, bun, and Lealone setup.
- Keep the readiness work aligned with the current repository structure, current stack, and current dependency boundary.

Out of scope:
- Adding chat, prompt intake, prototype upload, or reference-site collection features to the product runtime.
- Expanding the V1 business-domain baseline beyond users, roles, projects, tickets, and kanban.
- Changing the monolithic application target or restructuring the repository into a different platform shape.
- Introducing new external software libraries, new infrastructure dependencies, or a different backend or frontend stack.
- Reworking existing frontend or backend implementation patterns beyond what is needed to expose stable documentation and verification entrypoints.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):
- The repository remains an AI-oriented enterprise component platform rather than a built-in AI interaction product.
- V1 remains limited to enterprise internal management applications and a monolithic application target.
- The backend and frontend technical baselines remain Java 25 + Maven + Lealone-Platform and Node 24 + bun + Vite 8 + React 19 + shadcn/ui + Tailwind CSS 4.
- The platform dependency contract remains limited to Lealone-Platform and dependencies already present in this repository.
