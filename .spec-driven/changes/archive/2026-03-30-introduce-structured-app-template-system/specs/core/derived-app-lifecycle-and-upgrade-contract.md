## MODIFIED Requirements

### Requirement: Repository Defines Upgrade Compatibility Inputs
Previously: The repository MUST define the machine-readable inputs used to evaluate whether a derived application is compatible with a later platform release, including the standardized release-history / lineage assets, the release-delta and advisory assets for candidate targets, and the supported upgrade-path declarations that explain which source-to-target combinations the repository recognizes.

The repository MUST define the machine-readable inputs used to evaluate whether a derived application is compatible with a later platform release, including the standardized release-history / lineage assets, the release-delta and advisory assets for candidate targets, the supported upgrade-path declarations that explain which source-to-target combinations the repository recognizes, and the structured template-boundary assets that clarify which generated areas are platform-managed versus customization-owned.

#### Scenario: A contributor prepares an upgrade decision using template-boundary facts
- GIVEN a contributor wants to know whether a platform-managed output change may safely apply to a derived application
- WHEN they inspect the repository-owned lifecycle and upgrade assets
- THEN they can identify template-boundary facts that distinguish platform-owned template regions from derived-app customization regions
