## MODIFIED Requirements

### Requirement: Repository Provides AI Contribution Quickstart
Previously: The repository MUST provide an AI-oriented quickstart that identifies required references, common task locations, the standard verification entrypoints for repository changes, and the repository-owned path for deriving an independent application from the platform.

The repository MUST provide an AI-oriented quickstart that identifies required references, common task locations, the standard verification entrypoints for repository changes, the repository-owned path for deriving an independent application from the platform, and the discoverable entrypoint for any repository-owned baseline demo and its regeneration guide.

#### Scenario: A contributor looks for the repository-owned demo entrypoint
- GIVEN a contributor wants to run or explain the repository-owned baseline demo
- WHEN they read the repository quickstart or root guidance
- THEN they can find the committed demo location and its guide without searching through implementation directories
