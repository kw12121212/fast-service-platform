# Questions: harden-release-upgrade-smoke

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should `upgrade-execute --apply` be in scope?
  Context: Apply writes managed assets into the fixture directory, which
  complicates cleanup and blurs the line between smoke validation and actual
  upgrade execution.
  A: Out of scope for this change. Dry-run is sufficient to prove the planning
  stage works.

- [x] Q: Should multi-hop upgrade paths be tested?
  Context: Only two releases exist (0.0.0-bootstrap and 0.1.0-dev), so there
  is no multi-hop path to test yet.
  A: Out of scope. Only the direct 0.0.0-bootstrap → 0.1.0-dev path is
  covered.
