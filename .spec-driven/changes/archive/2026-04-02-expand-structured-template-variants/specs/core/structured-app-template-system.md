# Delta Spec: Structured App Template System

Targets: `.spec-driven/specs/core/structured-app-template-system.md`

---

## ADDED Requirements

### Requirement: Default Template Classification Map Covers All Module Registry Modules
The repository MUST ensure the default derived-app template classification map includes at least one classified entry for every module declared in the module registry.

#### Scenario: A contributor checks template coverage for a module
- GIVEN a module is declared in the module registry
- WHEN a contributor inspects the default classification map
- THEN they can find at least one classified entry with a `moduleId` matching that module
- AND the entry declares an observable `unitType`, `ownership`, and `upgradeBehavior`

### Requirement: Module-Selection Configuration Is Classified As A Derived-App Customization Zone
The repository MUST classify the module-selection configuration file as a derived-app-owned customization zone in the default classification map.

#### Scenario: A contributor evaluates whether module-selection may be safely refreshed during upgrade
- GIVEN a contributor reviews a derived-app upgrade plan
- WHEN they consult the classification map entry for the module-selection configuration
- THEN they can determine it is `derived-managed` with `preserve-by-default` upgrade behavior
- AND they do not need to inspect the file content to make that determination
