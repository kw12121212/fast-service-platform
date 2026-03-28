# Design: improve-ai-consumption-readiness

## Approach

Treat AI-readiness as a repository capability, not as an application feature.
The implementation should create one clear human-and-agent starting path, then back that path with machine-readable metadata, reproducible change playbooks, and repository-owned verification commands.
The documentation and automation should point to the existing backend and frontend structure instead of inventing a new abstraction layer, so AI agents learn the real repository layout rather than a parallel guide.

## Key Decisions

- Include a machine-readable context manifest in the same change as the human-readable quickstart.
  Rationale: the user explicitly wants AI-readiness improvements to work for both prose-driven and tool-driven agents, and a manifest reduces repeated inference about constraints and commands.
- Require automated smoke and verification entrypoints rather than documenting only manual steps.
  Rationale: AI agents fail more often on reconstructed command sequences than on stable repository-owned scripts.
- Focus the first playbook set on high-frequency scenarios rather than trying to document every possible change path.
  Rationale: the highest value comes from reducing ambiguity on the edits AI will make most often in the current repository baseline.
- Keep the readiness assets inside the current dependency boundary.
  Rationale: the repository explicitly constrains AI reuse to built-in capabilities and existing dependencies instead of external tooling.
- Model the readiness contract partly as a new dedicated spec area instead of overloading the existing foundation spec.
  Rationale: the repository foundation spec should remain concise, while the AI-readiness behaviors need their own observable contract around manifests, playbooks, and automation entrypoints.

## Alternatives Considered

- Improve only `README.md` and `AGENTS.md`.
  Rejected because that would still leave AI without stable executable verification entrypoints or structured context data.
- Add verification scripts without stronger guidance documents.
  Rejected because automation alone does not tell AI where to work or which repository conventions control common changes.
- Treat AI-readiness as an external workflow concern outside the repository.
  Rejected because this repository already defines AI as its primary consumer, so the repository itself should expose the assets needed for AI to contribute safely.
- Defer the machine-readable manifest to a later change.
  Rejected because the user explicitly wants it included now, and it is central to lowering ambiguity for tool-using agents.
