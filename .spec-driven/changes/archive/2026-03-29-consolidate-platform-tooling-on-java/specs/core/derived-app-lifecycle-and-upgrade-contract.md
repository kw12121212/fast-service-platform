## MODIFIED Requirements

### Requirement: Repository Provides A Repository-Owned Upgrade Evaluation Path
Previously: The repository MUST provide repository-owned entrypoints that evaluate derived-application upgrade compatibility, surface release advisory details for repository-declared target releases, provide release lookup guidance for selecting a supported target, and provide a controlled upgrade execution path.

The repository MUST provide repository-owned entrypoints that evaluate derived-application upgrade compatibility, surface release advisory details for repository-declared target releases, provide release lookup guidance for selecting a supported target, and provide a controlled upgrade execution path, with Java as the repository-owned implementation runtime for those platform workflows.

#### Scenario: A contributor invokes lifecycle tooling through the repository-owned path
- GIVEN a contributor runs repository-owned advisory, target-selection, evaluation, or execution workflows
- WHEN those platform workflows execute
- THEN they run through the repository's Java-owned tooling implementation path instead of a Node-owned tooling path
