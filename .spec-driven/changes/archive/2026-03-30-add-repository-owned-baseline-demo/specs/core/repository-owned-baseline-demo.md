## ADDED Requirements

### Requirement: Repository Provides A Dedicated Baseline Demo Area
The repository MUST provide a dedicated `demo/` area that contains a repository-owned baseline demo derived application and its accompanying guide.

#### Scenario: A contributor inspects the repository for a showcase artifact
- GIVEN a contributor wants to evaluate the platform through a concrete sample
- WHEN they inspect the repository root
- THEN they can find a dedicated `demo/` area instead of reconstructing the showcase artifact from scattered files
- AND they can identify both the committed baseline demo and the guide that explains it

### Requirement: Baseline Demo Is Regenerable From Repository-Owned Inputs
The repository MUST make the committed baseline demo reproducible from explicit repository-owned inputs and repository-owned assembly tooling.

#### Scenario: A contributor recreates the committed baseline demo
- GIVEN a contributor wants to rebuild the committed baseline demo from scratch
- WHEN they follow the repository-owned guide
- THEN they can identify the assembly input and the repository-owned command sequence used to regenerate the demo
- AND the guide does not require reverse-engineering the committed demo contents by hand

### Requirement: Baseline Demo Supports Human Demonstration Of Baseline Workflows
The repository MUST make the committed baseline demo suitable for human demonstration of the current baseline enterprise-management workflows.

#### Scenario: A contributor presents the platform baseline to a human reviewer
- GIVEN the contributor has started the committed baseline demo using the documented prerequisites
- WHEN they navigate through the demo
- THEN they can demonstrate the baseline dashboard, user management, role-permission management, project management, ticket management, and kanban management experience
- AND they can exercise the minimum write workflows expected by the current baseline

### Requirement: Baseline Demo Includes Validation Guidance
The repository MUST define how contributors validate the committed baseline demo before presenting it.

#### Scenario: A contributor validates the baseline demo before a presentation
- GIVEN a contributor wants confidence that the committed baseline demo still works
- WHEN they consult the repository-owned guide
- THEN they can identify the required validation commands for the committed demo
- AND they can identify the observable checks that confirm the demo is runnable, demonstrable, and aligned with the current platform baseline
