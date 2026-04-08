## MODIFIED Requirements

### Requirement: Planning Layer Distinguishes Deterministic Planning From Recommendation Logic
Previously: The repository MUST define the planning layer so deterministic planning remains usable without repository-owned recommendation heuristics, and it MUST distinguish mandatory planning facts from any future optional recommendation guidance.

The repository MUST define the planning layer so deterministic planning remains usable without repository-owned recommendation heuristics, and it MUST distinguish mandatory planning facts from any repository-owned optional recommendation guidance through separate machine-readable assets rather than by collapsing both responsibilities into one artifact.

#### Scenario: A contributor uses deterministic planning without recommendation
- GIVEN a contributor needs to move from valid solution input to a valid manifest
- WHEN the repository-owned recommendation asset is unavailable, omitted, or intentionally skipped
- THEN the contributor can still complete the required planning path from explicit constraints and declared dependency rules
- AND the planning output remains sufficient to prepare a valid standalone `app-manifest`

## ADDED Requirements

### Requirement: Repository Defines A Machine-Readable Solution-To-Manifest Recommendation Asset
The repository MUST define a machine-readable recommendation asset that may follow deterministic planning and guide contributors toward a recommended manifest shape without becoming the direct assembly runtime input.

#### Scenario: A contributor inspects the recommendation layer after planning
- GIVEN a contributor has prepared a valid repository-owned planning artifact from a valid structured solution input
- WHEN they inspect the repository-owned recommendation assets
- THEN they can identify a machine-readable recommendation artifact that consumes the planning output
- AND they can identify that the recommendation layer is optional guidance rather than a required replacement for planning

### Requirement: Recommendation Asset Exposes Observable Basis And Confidence
The recommendation asset MUST expose the recommended modules or manifest-shaping suggestions, the observable basis for each recommendation, and a confidence or strength indicator that helps contributors evaluate whether to accept or adjust that guidance.

#### Scenario: A contributor reviews recommendation output before producing a manifest
- GIVEN a contributor has prepared a recommendation artifact from a valid planning output
- WHEN they inspect its output
- THEN they can identify which modules or manifest choices are recommended
- AND they can identify which planning facts, explicit constraints, module-registry rules, or repository-owned heuristics contributed to those recommendations
- AND they can identify how strongly the repository recommends each suggested choice

### Requirement: Recommendation Layer Preserves Product Boundary And Optionality
The repository MUST define the recommendation layer so it stays within the current product boundary of enterprise-internal-management monolith applications and does not turn optional guidance into hidden mandatory behavior.

#### Scenario: A contributor evaluates whether recommendation expands product scope
- GIVEN a contributor reviews the repository-defined recommendation assets
- WHEN they inspect allowed recommendation behavior
- THEN they can confirm the guidance stays within enterprise-internal-management monolith applications
- AND they do not interpret repository-owned recommendations as approval to expand into unsupported product shapes, runtime AI features, or hidden mandatory assembly rules
