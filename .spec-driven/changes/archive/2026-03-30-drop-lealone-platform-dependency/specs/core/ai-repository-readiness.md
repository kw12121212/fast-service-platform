# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Provides AI Contribution Quickstart
The repository MUST provide an AI-oriented quickstart that identifies required references, common task locations, the standard verification entrypoints for repository changes, the required local `Lealone` dependency bootstrap, the repository-owned path for deriving an independent application from the platform, and the discoverable entrypoint for any repository-owned baseline demo and its regeneration guide.

#### Scenario: An AI contributor starts a common repository task
- GIVEN an AI contributor needs to extend or modify the repository
- WHEN it opens the AI contribution quickstart
- THEN it can identify what to read first, where common backend and frontend work lives, which repository commands validate the change, and how the local `Lealone` dependency should be prepared

### Requirement: Repository Provides Machine-Readable AI Context
The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the required local `Lealone` source dependency checkout and install path, the machine-readable assets used for structured solution definition and later application derivation, the machine-readable assets that classify generated template boundaries and customization zones, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, the execution assets needed to plan or apply derived-app upgrades, the AI tool-orchestration assets needed to teach AI contributors how to choose and sequence repository-owned tooling, and the unified repository-owned tooling facade that serves as the default invocation surface for those workflows, while distinguishing frontend `Node/bun` requirements from Java-owned platform tooling requirements.

#### Scenario: A tool-driven agent loads repository context
- GIVEN an AI agent can consume structured repository metadata
- WHEN it reads the machine-readable AI context asset
- THEN it can determine the repository's required references, runtime assumptions, directory responsibilities, validation entrypoints, non-negotiable constraints, and required local `Lealone` dependency bootstrap without inferring them from prose alone

### Requirement: Repository Provides AI-Oriented Troubleshooting Guidance
The repository MUST provide troubleshooting guidance for the current local setup so contributors can resolve common environment, dependency, and integration failures without guessing.

#### Scenario: A contributor hits a missing local dependency failure
- GIVEN a contributor encounters a missing local dependency failure in the current repository environment
- WHEN they consult the repository troubleshooting guidance
- THEN they can identify `vendor/lealone` as the required source checkout and the repository-approved recovery path
- AND they are not told that `vendor/lealone-platform` is required for the current runnable baseline
