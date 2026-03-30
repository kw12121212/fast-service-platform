# Project Merge Support

### Requirement: Bound Projects Expose Merge Support Context For Managed Linked Worktrees
The system MUST expose project-scoped merge support context for a managed linked worktree of a software project that is bound to a local Git repository.

#### Scenario: A contributor inspects merge support for a linked worktree
- GIVEN a software project is bound to a local Git repository
- AND the repository includes a managed linked worktree checked out to local branch `feature/api-preview`
- WHEN the contributor reads the project's merge support context for that linked worktree
- THEN they can identify the source worktree path
- AND they can identify the source branch currently checked out in that worktree
- AND they can inspect the available target branches from the same repository context, excluding the source branch
- AND they can identify whether the source worktree is currently merge-ready or restricted

### Requirement: Merge Source Must Be A Managed Clean Linked Worktree
The system MUST allow project-scoped merge execution only when the selected merge source is a managed linked worktree whose working tree is clean.

#### Scenario: A contributor merges from a clean linked worktree into another local branch
- GIVEN a software project is bound to a local Git repository
- AND the project includes a managed linked worktree checked out to local branch `feature/api-preview`
- AND the selected source worktree is clean
- AND the requested target local branch `main` exists in the same repository context
- WHEN the contributor requests a merge from that linked worktree into `main`
- THEN the project-scoped merge is executed
- AND the updated project Git and worktree context reflects the resulting repository state

#### Scenario: A contributor tries to merge from the main repository worktree
- GIVEN a software project is bound to a local Git repository
- AND the main repository worktree is visible through the project's worktree context
- WHEN the contributor requests a merge using the main repository worktree as the source
- THEN the merge request is rejected
- AND the repository state remains unchanged

#### Scenario: A contributor tries to merge from a dirty linked worktree
- GIVEN a software project is bound to a local Git repository
- AND the project includes a managed linked worktree checked out to local branch `feature/api-preview`
- AND the selected source worktree has uncommitted changes
- WHEN the contributor requests a merge from that linked worktree
- THEN the merge request is rejected
- AND the repository state remains unchanged

### Requirement: Merge Target Must Be Another Existing Local Branch
The system MUST restrict project-scoped merge targets to another existing local branch in the same repository context.

#### Scenario: A contributor tries to merge into a missing local branch
- GIVEN a software project is bound to a local Git repository
- AND the project includes a managed linked worktree checked out to local branch `feature/api-preview`
- WHEN the contributor requests a merge into local branch `release/9.9` that does not exist
- THEN the merge request is rejected
- AND the repository state remains unchanged

#### Scenario: A contributor tries to merge into the same branch as the source
- GIVEN a software project is bound to a local Git repository
- AND the project includes a managed linked worktree checked out to local branch `feature/api-preview`
- WHEN the contributor requests a merge from that worktree into local branch `feature/api-preview`
- THEN the merge request is rejected
- AND the repository state remains unchanged

### Requirement: Merge Conflicts Fail Safely
The system MUST fail a project-scoped merge when the requested source and target branches cannot be merged cleanly, and it MUST not leave the repository in an in-progress merge state afterward.

#### Scenario: A contributor requests a conflicting merge
- GIVEN a software project is bound to a local Git repository
- AND the project includes a managed linked worktree checked out to local branch `feature/api-preview`
- AND the selected source worktree is clean
- AND the requested target local branch `main` exists
- AND merging `feature/api-preview` into `main` would produce conflicts
- WHEN the contributor requests the merge
- THEN the merge request fails
- AND the repository does not remain in an in-progress merge state afterward
- AND the contributor can still inspect readable project Git and worktree context after the failure

### Requirement: Unbound Projects Do Not Pretend To Have Merge Support State
The system MUST keep software projects usable when no repository binding exists, without exposing fake merge data or normal merge actions.

#### Scenario: A contributor inspects merge support for an unbound project
- GIVEN a software project has no repository binding
- WHEN the contributor reads the project's merge support context
- THEN they receive a clear unbound state instead of merge source, target, or action data
- AND the project remains usable for the rest of the current platform workflows
