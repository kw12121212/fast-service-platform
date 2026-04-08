## ADDED Requirements

### Requirement: Repository Defines A Machine-Readable Solution-To-Manifest Planning Asset
The repository MUST define a machine-readable planning asset that bridges a valid structured solution input to manifest preparation without becoming the direct assembly runtime input.

#### Scenario: A contributor inspects the planning layer after preparing solution input
- GIVEN a contributor has a valid repository-defined structured solution input
- WHEN they inspect the repository-owned planning assets
- THEN they can identify a machine-readable planning artifact that connects solution input facts to manifest preparation
- AND they can identify which repository-owned assets that planning layer depends on
- AND they do not treat the planning artifact itself as the direct assembly runtime contract

### Requirement: Planning Asset Exposes Observable Module And Manifest-Preparation Facts
The planning asset MUST expose the selected modules, excluded modules, the observable basis for each inclusion or exclusion, and any unresolved manifest fields or conflicts that still require explicit contributor resolution before assembly.

#### Scenario: A contributor reviews planning output before producing a manifest
- GIVEN a contributor has prepared a planning artifact from a valid solution input
- WHEN they inspect its output
- THEN they can identify which modules were included or excluded
- AND they can identify which solution-input facts, explicit constraints, or module dependency rules contributed to those decisions
- AND they can identify what remains unresolved before a valid `app-manifest` can be produced

### Requirement: Planning Layer Distinguishes Deterministic Planning From Recommendation Logic
The repository MUST define the planning layer so deterministic planning remains usable without repository-owned recommendation heuristics, and it MUST distinguish mandatory planning facts from any future optional recommendation guidance.

#### Scenario: A contributor uses the planning layer before module recommendation exists
- GIVEN a contributor needs to move from valid solution input to a valid manifest
- WHEN they use the repository-owned planning layer
- THEN they can complete the required planning path from explicit constraints and declared dependency rules
- AND they do not need repository-owned recommendation heuristics to understand the mandatory planning outcome
