# Design

## Approach

Restructure backend validation around two distinct verification layers.

The first layer is the default backend baseline used during routine repository validation. It should cover unit behavior, service behavior, bootstrap behavior, and lightweight repository-oriented behavior without running the heaviest sandbox runtime operations.

The second layer is a dedicated heavyweight backend runtime validation path that keeps real sandbox image and container behavior under repository-owned verification, including real `podman` execution.

The implementation will likely combine:
- splitting mixed backend tests into smaller classes aligned to runtime cost and responsibility
- reducing repeated embedded runtime bootstrap for lightweight tests where class-level isolation is sufficient
- introducing a dedicated repository script for heavyweight sandbox runtime verification
- updating documentation and AI-readable validation entrypoints to distinguish the fast baseline from the heavier runtime check

## Key Decisions

### Keep Real Sandbox Runtime Validation

The repository should not replace all real sandbox runtime checks with mocks. The platform contract includes observable sandbox behavior, so at least one repository-owned validation path must still exercise real image and container execution.

### Remove Real `podman` Runtime From The Default Backend Baseline

The default backend baseline should optimize for fast, repeatable feedback during normal repository changes. Real `podman build/run` belongs in a heavier runtime-oriented path, not the default `mvn test` path.

### Preserve Observable Coverage Instead Of Internal Coupling

The change should preserve coverage of observable behavior and keep tests aligned with spec language. It should not optimize by asserting internal implementation details or by weakening externally visible guarantees.

### Improve Test Layering Before Adding More Verification Surface

The current problem is mostly structural rather than missing tooling. The first fix is to reorganize test responsibility and verification entrypoints, not to add new frameworks or large custom harnesses.

## Alternatives Considered

### Keep The Current Layout And Accept Slow Backend Verification

Rejected because the current default backend loop is already dominated by a small number of heavyweight runtime tests, which hurts iteration speed without improving routine feedback quality.

### Mock `podman` Everywhere

Rejected because it would remove the repository-owned proof that sandbox runtime behavior still works in a real host environment.

### Keep Real Sandbox Runtime Tests Inside `mvn test` But Parallelize More

Rejected as the primary fix because it does not address the main issue: default backend validation is paying for heavyweight runtime coverage that does not need to run on every routine backend change.
