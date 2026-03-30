# Project Sandbox Environment

## ADDED Requirements

### Requirement: Bound Projects Expose Sandbox Context For Managed Linked Worktrees
The system MUST expose project-scoped sandbox context for a managed linked worktree of a software project that is bound to a local repository.

#### Scenario: A contributor inspects sandbox context for a linked worktree
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- WHEN the contributor reads the project's sandbox context for that linked worktree
- THEN they can identify the linked worktree path
- AND they can identify whether a persistent sandbox image exists for that linked worktree
- AND they can identify whether that linked worktree currently has an active temporary sandbox container
- AND they can identify whether the linked worktree is sandbox-ready or restricted

### Requirement: Sandbox Images Are Linked-Worktree-Scoped Persistent Resources
The system MUST persist sandbox images at linked-worktree scope instead of treating them as main-worktree or repository-shared resources.

#### Scenario: A contributor creates an image for a linked worktree
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- AND no sandbox image exists yet for that linked worktree
- WHEN the contributor requests sandbox image creation for that linked worktree
- THEN the platform creates a persistent sandbox image for that linked worktree
- AND the updated project sandbox context shows that the image now exists

### Requirement: Sandbox Initialization Uses Default Or Worktree-Configured Script Paths
The system MUST initialize sandbox resources using default script paths unless worktree properties provide explicit overrides.

#### Scenario: A contributor creates an image using the default image initialization script
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- AND the linked worktree does not override the image initialization path
- WHEN the contributor requests sandbox image creation
- THEN the platform runs the default `init-image.sh` path for that linked worktree

#### Scenario: A contributor creates an image using a worktree-configured image initialization script
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- AND the linked worktree properties explicitly override the image initialization path
- WHEN the contributor requests sandbox image creation
- THEN the platform runs the configured image initialization path instead of the default path

#### Scenario: A contributor creates a container using a worktree-configured project initialization script
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- AND a sandbox image already exists for that linked worktree
- AND the linked worktree properties explicitly override the project initialization path
- WHEN the contributor requests sandbox container creation
- THEN the platform runs the configured `init-project.sh` path instead of the default path

### Requirement: Temporary Sandbox Containers Require An Existing Image
The system MUST only allow temporary sandbox container creation after a persistent image already exists for the same linked worktree.

#### Scenario: A contributor creates a temporary container from an existing image
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- AND a sandbox image exists for that linked worktree
- WHEN the contributor requests sandbox container creation
- THEN the platform creates a temporary sandbox container from that image
- AND the updated sandbox context shows an active container for that linked worktree

#### Scenario: A contributor tries to create a temporary container without an image
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- AND no sandbox image exists for that linked worktree
- WHEN the contributor requests sandbox container creation
- THEN the request is rejected
- AND the sandbox context remains readable afterward

### Requirement: Each Linked Worktree Has At Most One Active Sandbox Container
The system MUST enforce that each managed linked worktree has at most one active temporary sandbox container at a time.

#### Scenario: A contributor tries to create a second active container for the same linked worktree
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- AND a sandbox image exists for that linked worktree
- AND that linked worktree already has an active temporary sandbox container
- WHEN the contributor requests another sandbox container for the same linked worktree
- THEN the request is rejected
- AND the existing active container remains the only active container for that linked worktree

#### Scenario: A contributor destroys the active container for a linked worktree
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- AND that linked worktree currently has an active temporary sandbox container
- WHEN the contributor requests container destruction
- THEN the active container is destroyed
- AND the updated sandbox context shows that the linked worktree no longer has an active container

### Requirement: Sandbox Initialization Failures Are Visible
The system MUST surface failed sandbox initialization outcomes instead of pretending the sandbox became ready.

#### Scenario: Image initialization fails
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- AND the selected image initialization script exits with a non-zero status
- WHEN the contributor requests sandbox image creation
- THEN the image creation request fails
- AND the sandbox context exposes a clear image initialization failure state

#### Scenario: Container initialization fails
- GIVEN a software project is bound to a local repository
- AND the repository includes a managed linked worktree checked out to branch `feature/api-preview`
- AND a sandbox image exists for that linked worktree
- AND the selected project initialization script exits with a non-zero status
- WHEN the contributor requests sandbox container creation
- THEN the container creation request fails
- AND the sandbox context exposes a clear container initialization failure state

### Requirement: Unbound Projects And Main Worktrees Do Not Pretend To Have Normal Sandbox State
The system MUST keep projects usable when sandbox preconditions do not hold, without exposing fake normal sandbox state.

#### Scenario: A contributor inspects sandbox support for an unbound project
- GIVEN a software project has no repository binding
- WHEN the contributor reads the project's sandbox context
- THEN they receive a clear unbound state instead of normal sandbox image or container data
- AND the project remains usable for the rest of the current platform workflows

#### Scenario: A contributor inspects sandbox support for the main repository worktree
- GIVEN a software project is bound to a local repository
- AND the main repository worktree is visible through the project's worktree context
- WHEN the contributor reads sandbox context for that main repository worktree
- THEN they receive a clear restricted state instead of normal sandbox source behavior
