# Fast Service Platform

Fast Service Platform is an AI-oriented repository of reusable enterprise application components built on top of `Lealone`.

This repository is not a generic business application and not an end-user AI chat product. Its purpose is to provide a reliable, AI-friendly backend and frontend baseline that AI agents can reuse under explicit constraints to assemble internal enterprise management systems.

## Positioning

- The primary reuser is `AI`.
- The repository does not directly provide `AI chat`, `prompt intake`, prototype upload, or reference-site ingestion.
- V1 targets `internal enterprise management applications`.
- V1 targets a `monolithic application` shape.
- The platform boundary is `Lealone + built-in repository components + existing repository dependencies`.
- Avoiding new external software libraries is a product principle.

## What Exists Today

This repository is already a runnable baseline, not an empty scaffold.

- `backend/` is a working `Java 25 + Maven` backend core.
- `frontend/` is a working `Vite 8 + React 19` PC admin frontend.
- The backend includes these minimum enterprise domains:
  - User management
  - Role and permission management
  - Software project management
  - Ticket management
  - Kanban management
- The frontend includes these visible admin pages:
  - Dashboard
  - Users
  - Roles and permissions
  - Software projects
  - Tickets
  - Kanban
- The backend supports optional demo data.
- The frontend talks to the real backend through `/service/*`, not static mocks.

From the baseline application's point of view, all of these domains are available today. From the derived-application point of view, `project-management`, `ticket-management`, and `kanban-management` are already being treated as optional built-in modules rather than mandatory business domains.

## Current Engineering Capabilities

Project and repository workflows are already available from the current software-project experience.

- Create software projects.
- Bind a project to an absolute local Git repository path.
- Inspect bound repository state:
  - repository root path
  - current branch or detached HEAD
  - working tree state
  - recent commits
  - local branch list
- Switch to an existing local branch when the repository is clean and not detached.
- Manage project-scoped Git worktrees:
  - inspect worktree inventory
  - create a worktree for an existing local branch
  - delete a linked worktree only when it is clean, fully pushed, and has an upstream
  - run `repair` and `prune` from the project context
- Merge a managed linked worktree branch into another existing local branch:
  - source must be a clean linked worktree
  - conflicts fail and are aborted instead of being auto-resolved
- Manage project-scoped sandbox environments for managed linked worktrees:
  - inspect sandbox image and container state from the Projects page
  - create or rebuild a persistent worktree-scoped sandbox image through `podman`
  - create and destroy a temporary sandbox container through `podman`
  - run `init-image.sh` during image creation and `init-project.sh` during container creation
  - surface restricted states and initialization failures clearly

## AI-Ready Platform Workflows

The repository already includes platform-owned contracts, manifests, schemas, compatibility fixtures, and scripts so AI agents can reuse repository tooling instead of inventing ad hoc workflows.

AI agents and platform contributors can:

- converge from structured solution input into an application manifest
- prepare a bounded descriptor-driven management-module asset between planning guidance and manifest shaping
- scaffold a derived admin application from repository-owned contracts and module metadata
- verify generated applications against repository-owned validation rules
- inspect release history and upgrade targets
- evaluate, advise, and execute derived-app upgrades through repository-owned tooling

Primary entrypoints:

- `docs/ai/quickstart.md`
- `docs/ai/context.yaml`
- `docs/ai/descriptor-driven-management-module-contract.json`
- `docs/ai/ai-tool-orchestration-contract.json`
- `docs/ai/app-assembly-contract.json`
- `docs/ai/generated-app-verification-contract.json`
- `docs/ai/structured-app-template-contract.json`
- `docs/ai/template-classifications/default-derived-app-template-map.json`
- `./scripts/platform-tool.sh`

## Demo

The repository includes a committed baseline demo that shows what this platform can already generate and run.

- Demo manifest: `demo/baseline-demo.manifest.json`
- Committed demo derived app: `demo/baseline-demo/`
- Demo guide: `demo/GUIDE.md`
- Five-minute talk track: `demo/5-minute-demo.md`

Useful commands:

```bash
./scripts/regenerate-baseline-demo.sh
./scripts/verify-baseline-demo.sh
```

## Technical Baseline

| Area | Baseline | Notes |
| --- | --- | --- |
| Backend runtime | Java 25 LTS | Unified Java baseline |
| Backend build | Maven 3.9.x | Managed locally through SDKMAN in this repository |
| Backend foundation | [Lealone](https://github.com/lealone/Lealone) | Service-oriented backend foundation |
| Frontend runtime | Node 24 | Managed locally through `nvm` |
| Frontend package manager | bun | Frontend dependency and script runner |
| Frontend build tool | Vite 8 | Dev and build entrypoint |
| Frontend framework | React 19 | UI runtime |
| Component system | shadcn/ui | Source-controlled UI components |
| Styling system | Tailwind CSS 4 | Design tokens and utility styling |
| Change workflow | spec-driven workflow | Define scope before implementation |

## Prerequisites

The backend expects a source checkout of `Lealone` under `vendor/`.

Expected paths:

- `vendor/lealone`

After cloning those repositories, install the local source dependencies:

```bash
./scripts/install-lealone-source-deps.sh
```

Default local tool paths used by repository scripts:

- Java: `$HOME/.sdkman/candidates/java/25.0.2-tem`
- Maven: `$HOME/.sdkman/candidates/maven/current/bin/mvn`

Additional host prerequisite for sandbox support:

- `podman`

If your environment differs, override with `JAVA_HOME` and `MVN_BIN`.

## Local Run

### Start the backend

```bash
cd backend
$HOME/.sdkman/candidates/maven/current/bin/mvn test
$HOME/.sdkman/candidates/java/25.0.2-tem/bin/java \
  -Dfsp.demo-data=true \
  -cp "target/classes:$(cat target/runtime-classpath.txt)" \
  com.fastservice.platform.backend.BackendApplication
```

Notes:

- HTTP service defaults to `http://127.0.0.1:8080`
- Lealone TCP defaults to `127.0.0.1:9210`
- `fsp.demo-data=true` loads optional demo data for frontend exploration

### Start the frontend

```bash
cd frontend
bun install
bun run dev
```

Notes:

- Vite starts on its local dev port
- `/service/*` proxies to `http://127.0.0.1:8080`
- the frontend depends on real backend responses

## Verification

Baseline validation commands:

```bash
cd backend
$HOME/.sdkman/candidates/maven/current/bin/mvn -q test

cd ../frontend
bun run test
bun run build
bun run lint
```

Repository-owned verification entrypoints:

```bash
./scripts/verify-backend.sh
./scripts/verify-backend-sandbox-runtime.sh
./scripts/verify-frontend.sh
./scripts/verify-fullstack.sh
```

- `mvn test` and `verify-backend.sh` run the fast backend baseline and exclude heavyweight real sandbox runtime execution.
- `verify-backend-sandbox-runtime.sh` runs the dedicated real `podman` sandbox runtime validation path.
- `verify-fullstack.sh` starts the backend with demo data, runs the frontend, and validates real `/service/*` responses through the frontend proxy.

## V1 Minimum Baseline

The current baseline application includes:

- user management
- role and permission management
- software project management
- ticket management
- kanban management

From the platform point of view, the more stable mandatory core is:

- admin shell
- user management
- role and permission management

Optional built-in modules can include:

- software project management
- ticket management
- kanban management

The V1 platform baseline must still provide:

- tests
- optional demo data
- admin dashboard shell
- user management
- role and permission management
- software project management
- ticket management
- kanban management

## Repository Layout

```text
.
├── .spec-driven/     # specs, change proposals, tasks, and archived changes
├── backend/          # Java 25 + Lealone backend core
├── demo/             # baseline demo manifest and committed demo app
├── docs/ai/          # AI-facing contracts, schemas, context, and playbooks
├── frontend/         # Vite + React + shadcn/ui + Tailwind CSS admin frontend
├── scripts/          # install, verification, and platform tooling scripts
├── vendor/           # Lealone source dependency
├── AGENTS.md         # repository agent instructions
├── RTK.md            # repository technical knowledge
└── README.md         # project overview
```

## Collaboration Rules

- Route non-trivial changes through `.spec-driven/` first.
- Treat `.spec-driven/` as the source of truth for scope and requirements.
- Read `AGENTS.md` first for repository rules.
- Read `RTK.md` for technical background.
- Read `.spec-driven/specs/INDEX.md` for the current spec map.
- Read `docs/ai/quickstart.md` and `docs/ai/context.yaml` before hands-on implementation.

## Spec-Driven Workflow

This repository expects non-trivial work to follow the spec-driven workflow:

1. define or refine scope
2. create a proposal
3. implement against the approved change
4. verify behavior
5. archive the completed change

The current main spec index is:

- `.spec-driven/specs/INDEX.md`

Recent completed engineering-support work includes archived project-scoped worktree management under:

- `.spec-driven/changes/archive/2026-03-30-project-worktree-management/`
