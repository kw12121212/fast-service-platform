## MODIFIED Requirements

### Requirement: Current Runnable Baseline Remains Reproducible As A Default Assembly
Previously: The system MUST preserve a default assembly profile that reproduces the repository's current runnable baseline application behavior even when the optional delivery-management area is decomposed into smaller module units.

The system MUST preserve a default assembly profile that reproduces the repository's current runnable baseline application behavior even when the optional delivery-management area is decomposed into smaller module units.
The system MUST provide a repository-owned baseline demo path that allows contributors to demonstrate that default assembly behavior to human reviewers without reconstructing the baseline from scattered setup notes.

#### Scenario: A contributor prepares a baseline product demonstration
- GIVEN a contributor wants to show the current platform baseline to a human reviewer
- WHEN they use the repository-owned baseline demo path
- THEN they can launch and demonstrate the default baseline assembly behavior as one coherent enterprise-management application
- AND they can show the required core and current baseline delivery-management capabilities together without inventing an ad hoc sample
