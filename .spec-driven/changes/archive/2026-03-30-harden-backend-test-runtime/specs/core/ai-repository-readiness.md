## MODIFIED Requirements

### Requirement: Repository Provides Automated Validation Entrypoints
The repository MUST provide stable automated entrypoints for backend validation, frontend validation, full-stack validation, and derived-application assembly validation.

#### Scenario: A contributor validates a repository change
- GIVEN a contributor has modified the repository
- WHEN they run the documented validation entrypoints
- THEN they can execute backend checks, frontend checks, the expected full-stack validation path, and the derived-application assembly checks through repository-owned commands

## ADDED Requirements

### Requirement: Repository Distinguishes Fast Backend Validation From Heavy Runtime Validation
The repository MUST document which backend validation entrypoint is the default fast-feedback baseline and which separate entrypoint exercises heavier engineering-runtime behavior such as real sandbox execution.

#### Scenario: A contributor chooses a backend validation path
- GIVEN a contributor needs to validate a repository change
- WHEN they inspect the documented repository-owned validation commands
- THEN they can distinguish the default backend baseline from the heavier sandbox-runtime validation path
- AND they can determine which path includes real sandbox runtime execution
