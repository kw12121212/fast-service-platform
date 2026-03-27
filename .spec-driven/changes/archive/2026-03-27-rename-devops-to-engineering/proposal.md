# rename-devops-to-engineering

## What

Rename the ambiguous `devops` surface to `engineering` across the current platform terminology and backend extension-space namespace.
Clarify that this area represents engineering-support capabilities for AI-driven software work, not deployment or infrastructure operations.

## Why

`devops` is easy to misread as a deployment or operations module, which is not what this repository currently means.
The current platform boundary for this area is Git repository management, worktree management, merge support, and sandbox environments, which are closer to engineering workflow support than classic operations tooling.
Using a clearer term reduces future misunderstanding in specs, docs, and package names.

## Scope

In scope:
- Rename the current terminology for this component area from `devops` to `engineering`.
- Update current specs and RTK wording so the platform description uses the clearer term consistently.
- Rename the backend extension-space package path from `backend...devops...` to `backend...engineering...` for the current placeholder ports.

Out of scope:
- Adding new engineering-support behavior or implementations.
- Changing the actual component list of Git repository management, worktree management, merge support, and sandbox environments.
- Expanding the current V1 enterprise-management feature set.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):
- The platform still treats Git repository management, worktree management, code merge support, and sandbox environments as platform-provided components.
- The backend still only preserves extension space for this area and does not implement those capabilities yet.
