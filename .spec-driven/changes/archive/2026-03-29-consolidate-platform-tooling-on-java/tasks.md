# Tasks: consolidate-platform-tooling-on-java

## Implementation

- [x] Implement Java-owned repository tooling for all platform workflow categories currently backed by Node scripts, including assembly compatibility, generated-app verification, release advisory, upgrade target selection, upgrade evaluation, and upgrade execution.
- [x] Update the unified tooling facade, compatible wrappers, and generated-app guidance so repository-owned platform workflows route through Java while frontend Node/bun workflows remain unchanged.
- [x] Update AI context, machine-readable contracts, playbooks, and compatibility assets so they no longer describe Node as a repository-owned platform tooling implementation runtime.

## Testing

- [x] `bun run lint` passes in `frontend/`
- [x] `mvn -q -f tools/java-assembly-cli/pom.xml test` passes
- [x] `mvn -q -f tools/java-generated-app-verifier/pom.xml test` passes
- [x] `./scripts/platform-tool.sh assembly verify` passes through the Java-owned tooling path
- [x] `./scripts/platform-tool.sh generated-app verify <generated-app-dir>` passes through the Java-owned tooling path on a repository-generated fixture app
- [x] `./scripts/platform-tool.sh upgrade advisory` and `./scripts/platform-tool.sh upgrade targets` pass through the Java-owned tooling path

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify consolidate-platform-tooling-on-java` passes
- [x] Confirm the repository still uses Node/bun for frontend workflows while no longer using Node as a repository-owned platform tooling implementation runtime
