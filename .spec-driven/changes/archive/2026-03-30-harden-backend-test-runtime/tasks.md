# Tasks

## Implementation

- [x] Split the current mixed backend service test coverage into lighter service or repository test classes and heavier sandbox runtime test classes.
- [x] Reduce repeated embedded backend bootstrap cost for lightweight backend tests without weakening required observable isolation.
- [x] Add a repository-owned verification entrypoint for heavyweight sandbox runtime validation.
- [x] Update existing backend verification scripts so the default backend baseline excludes heavyweight real `podman` runtime validation.
- [x] Update repository docs and AI-readable guidance to describe the new backend validation layering.

## Testing

- [x] Run the default backend test baseline and confirm required backend unit, service, and integration coverage still passes.
- [x] Run the dedicated heavyweight sandbox runtime verification path and confirm real sandbox validation still passes.
- [x] Run the frontend and full-stack repository verification entrypoints needed to confirm no validation contract regression was introduced.

## Verification

- [x] Measure the updated backend baseline runtime and confirm it is materially lower than the pre-change default path.
- [x] Verify the new validation entrypoints and documentation are consistent with actual repository behavior.
