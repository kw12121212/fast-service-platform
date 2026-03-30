# Proposal

## What

Refactor the backend verification and test layout so the default backend validation path remains behaviorally trustworthy but no longer pays the full cost of the heaviest engineering-runtime tests on every run.

This change will:
- separate lightweight backend behavior coverage from heavyweight repository-runtime coverage
- keep real sandbox runtime validation available, but move it behind a dedicated heavier verification entrypoint instead of the default backend baseline
- reduce repeated embedded-database bootstrap cost for tests that do not need isolated runtime state per test case
- preserve observable backend behavior coverage for unit, service, and integration validation

## Why

The current backend baseline is slow primarily because one mixed test class combines:
- basic service behavior
- repository and worktree behavior
- merge behavior
- real sandbox image and container behavior through `podman`

In the current layout, the default backend verification path pays for repeated embedded database initialization and multiple real `podman build/run` cycles. That increases iteration time without improving the signal of every routine backend change.

The repository still needs real sandbox runtime validation, but it does not need to run in the default fast-feedback backend baseline.

## Scope

In scope:
- split backend tests into clearer layers based on runtime cost and verification purpose
- keep `mvn test` focused on unit tests, lightweight service tests, and lightweight integration checks
- move real `podman` sandbox runtime validation into a separate backend-owned verification path
- reduce unnecessary repeated backend bootstrap work where test isolation does not require a fresh database per method
- update repository verification docs and scripts to reflect the new validation layering

Out of scope:
- changing sandbox product behavior
- removing real sandbox runtime coverage entirely
- introducing a new test framework
- redefining frontend or full-stack verification scope
- specifying exact performance guarantees in the contract

## Unchanged Behavior

- The repository still provides backend unit, service, and integration test coverage.
- The repository still provides real sandbox runtime validation for linked-worktree sandbox behavior.
- The documented repository-owned verification entrypoints remain the supported way to validate repository changes.
- Full-stack smoke validation continues to validate the active `/service/*` integration path.
