# Design: add-java-generated-app-verifier-implementation

## Approach

Add a standalone Maven workspace for a Java verifier, expected under a tool-oriented path such as `tools/java-generated-app-verifier/`.

The Java verifier should:

1. Read the generated application's own normative verification assets
   - `docs/ai/generated-app-verification-contract.json`
   - `app-manifest.json`
   - `docs/ai/module-registry.json`
   - `docs/ai/app-assembly-contract.json`
   - generated frontend/backend contract files named by the verification contract

2. Apply the same observable generated-app checks already defined by the contract
   - required generated files exist
   - verification input files exist
   - generated context matches selected modules
   - frontend route and navigation wiring matches selected modules
   - backend service and table contracts match selected modules

3. Emit the same high-level result semantics
   - success/failure outcome
   - issue list
   - selected modules
   - verifier id
   - contract version

4. Integrate into repository-owned guidance and verification paths
   - contributors should be able to discover the Java verifier path from the repository docs and machine-readable assets
   - the Node verifier remains the reference verifier, while Java becomes a compatible verifier implementation

## Key Decisions

- Put the Java verifier in a separate Maven workspace.
  Rationale: generated-app verification is repository tooling, not backend runtime behavior.

- Make the Java verifier read the generated application's own assets rather than repository-only source files.
  Rationale: the generated-app verification contract explicitly requires validation from observable generated output.

- Keep Node as the reference verifier while adding Java as a compatible verifier.
  Rationale: this preserves current workflows while proving the verifier contract is multi-implementation.

- Do not require byte-for-byte identical CLI output to Node.
  Rationale: conformance should be based on the verifier contract's observable result semantics, not formatting identity.

## Alternatives Considered

- Extend the existing Java assembly CLI workspace to also host verifier logic.
  Rejected because assembly and verification are now separate standards and should remain separately evolvable.

- Replace the Node verifier immediately with Java.
  Rejected because it would turn a compatibility expansion into a migration with unnecessary risk.

- Keep verification Java-free and rely only on Node.
  Rejected because it would leave the new verifier contract unproven as a language-neutral standard.
