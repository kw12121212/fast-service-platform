# Design: bootstrap-ai-fast-service-platform

## Approach

Use a docs-first bootstrap so the repository becomes immediately navigable for both humans and coding agents.
Capture project intent in root documents, keep the workspace layout minimal, and encode the expected stack and workflow without creating implementation code too early.

## Key Decisions

- Keep the first change limited to documentation and reserved directories.
  Rationale: the user asked for project initialization files and AI-friendly conventions, not a full application scaffold.
- Write repository guidance in Chinese with technical identifiers kept in English where useful.
  Rationale: it matches the user's request while staying readable for tooling and future contributors.
- Treat Java 25 LTS, Lealone-Platform, Vite 8, React 19, Tailwind CSS 4, and shadcn/ui as the current baseline.
  Rationale: these are the requested platform constraints for future implementation work.

## Alternatives Considered

- Scaffold the backend and frontend immediately.
  Rejected because it would force unresolved choices such as build tooling, package management, and template variants before the repository rules exist.
- Leave the repository with only a README.
  Rejected because AI-driven work also needs explicit agent instructions and technical reference notes.
