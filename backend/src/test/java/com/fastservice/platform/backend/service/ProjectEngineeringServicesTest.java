package com.fastservice.platform.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.fastservice.platform.backend.project.ProjectServiceImpl;
import com.fastservice.platform.backend.support.BackendTestSupport;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectEngineeringServicesTest extends ProjectServiceTestSupport {

    @BeforeAll
    void bootstrapBackend() {
        BackendTestSupport.bootstrap(BackendTestSupport.uniqueDatabaseName("service_test"), false);
    }

    @Test
    void bindsProjectToLocalRepositoryAndListsRepositorySummary() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("GIT", "Git Binding", "Repository binding test");
        Path repositoryDir = createGitRepository();

        String boundRepositoryPath = projects.bindProjectRepository(projectId, repositoryDir.toString());
        String payload = projects.listProjects();

        assertEquals(repositoryDir.toString(), boundRepositoryPath);
        assertTrue(payload.contains("\"headState\":\"BRANCH\""));
        assertTrue(payload.contains("\"rootPath\":\"" + escapeJson(repositoryDir.toString()) + "\""));
        assertTrue(payload.contains("\"branch\":\"repo-test\""));
        assertTrue(payload.contains("\"workingTreeState\":\"CLEAN\""));
        assertTrue(payload.contains("\"availableBranches\":["));
        assertTrue(payload.contains("\"feature/api-preview\""));
        assertTrue(payload.contains("\"feature-preview\""));
        assertTrue(payload.contains("\"repo-test\""));
        assertTrue(payload.contains("\"recentCommits\":["));
        assertTrue(payload.contains("Second platform repo commit"));
        assertTrue(payload.contains("\"worktrees\":["));
        assertTrue(payload.contains("\"path\":\"" + escapeJson(repositoryDir.toString()) + "\""));
        assertTrue(payload.contains("\"main\":true"));
        assertTrue(payload.contains("\"mergeAllowed\":false"));
        assertTrue(payload.contains("\"mergeRestriction\":\"Main repository worktree cannot be used as a merge source\""));
    }

    @Test
    void rejectsRelativeRepositoryPathAndKeepsProjectUnbound() {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("PATH", "Path Validation", "Repository path validation");

        assertThrows(IllegalArgumentException.class, () -> projects.bindProjectRepository(projectId, "relative/repository"));
        assertTrue(projects.listProjects().contains("\"repository\":null"));
    }

    @Test
    void switchesProjectToExistingLocalBranchWhenWorkingTreeIsClean() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("SWITCH", "Branch Switch", "Branch switch validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());

        String switchedBranch = projects.switchProjectBranch(projectId, "feature-preview");
        String payload = projects.listProjects();

        assertEquals("feature-preview", switchedBranch);
        assertTrue(payload.contains("\"branch\":\"feature-preview\""));
    }

    @Test
    void rejectsBranchSwitchWhenWorkingTreeIsDirty() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("DIRTY", "Dirty Switch", "Dirty working tree validation");
        Path repositoryDir = createGitRepository();
        Files.writeString(repositoryDir.resolve("README.md"), "dirty working tree\n", StandardCharsets.UTF_8);

        projects.bindProjectRepository(projectId, repositoryDir.toString());

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> projects.switchProjectBranch(projectId, "feature-preview"));

        assertEquals("Cannot switch branches while working tree is dirty", error.getMessage());
        assertTrue(projects.listProjects().contains("\"workingTreeState\":\"DIRTY\""));
        assertTrue(projects.listProjects().contains("\"branch\":\"repo-test\""));
    }

    @Test
    void exposesDetachedHeadAsRestrictedState() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("DETACH", "Detached Head", "Detached HEAD validation");
        Path repositoryDir = createDetachedHeadRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String payload = projects.listProjects();

        assertTrue(payload.contains("\"headState\":\"DETACHED\""));
        assertTrue(payload.contains("\"branch\":null"));
        assertThrows(IllegalStateException.class, () -> projects.switchProjectBranch(projectId, "feature-preview"));
        assertThrows(IllegalStateException.class, () -> projects.createProjectWorktree(projectId, "feature-preview"));
        assertThrows(IllegalStateException.class, () -> projects.repairProjectWorktrees(projectId));
    }

    @Test
    void createsProjectWorktreeUsingSanitizedSiblingDirectory() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("WT", "Worktree Create", "Worktree creation validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());

        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");
        String payload = projects.listProjects();

        assertEquals(repositoryDir.resolveSibling(repositoryDir.getFileName() + "-worktrees")
                .resolve("feature-api-preview")
                .toString(), worktreePath);
        assertTrue(Files.isDirectory(Path.of(worktreePath)));
        assertTrue(payload.contains("\"branch\":\"feature/api-preview\""));
        assertTrue(payload.contains("\"path\":\"" + escapeJson(worktreePath) + "\""));
        assertTrue(payload.contains("\"deletionAllowed\":true"));
        assertTrue(payload.contains("\"mergeAllowed\":true"));
        assertTrue(payload.contains("\"mergeTargetBranches\":["));
        assertTrue(payload.contains("\"feature-preview\""));
        assertTrue(payload.contains("\"repo-test\""));
    }

    @Test
    void rejectsDuplicateBranchWorktreeCreation() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("WTDUP", "Worktree Duplicate", "Duplicate worktree validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        projects.createProjectWorktree(projectId, "feature/api-preview");

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> projects.createProjectWorktree(projectId, "feature/api-preview"));

        assertEquals("Branch already has a worktree: feature/api-preview", error.getMessage());
    }

    @Test
    void rejectsWorktreeCreationForUnboundProjects() {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("UNBOUND", "Unbound Worktree", "Unbound worktree validation");

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> projects.createProjectWorktree(projectId, "feature/api-preview"));

        assertEquals("Project repository is not bound: " + projectId, error.getMessage());
    }

    @Test
    void deletesCleanPushedWorktree() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("WTDEL", "Worktree Delete", "Worktree delete validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");

        String deletedPath = projects.deleteProjectWorktree(projectId, worktreePath);

        assertEquals(worktreePath, deletedPath);
        assertTrue(Files.notExists(Path.of(worktreePath)));
        assertTrue(projects.listProjects().contains("\"worktrees\":[{\"path\":\"" + escapeJson(repositoryDir.toString())));
        assertTrue(!projects.listProjects().contains("\"path\":\"" + escapeJson(worktreePath) + "\""));
    }

    @Test
    void rejectsDeletingDirtyWorktree() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("WTDIRTY", "Dirty Worktree", "Dirty worktree validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");
        Files.writeString(Path.of(worktreePath).resolve("README.md"), "dirty worktree\n", StandardCharsets.UTF_8);

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> projects.deleteProjectWorktree(projectId, worktreePath));

        assertEquals("Cannot delete worktree while it has uncommitted changes", error.getMessage());
        assertTrue(projects.listProjects().contains("\"deletionRestriction\":\"Worktree has uncommitted changes\""));
    }

    @Test
    void rejectsDeletingWorktreeWithUnpushedCommits() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("WTAHEAD", "Ahead Worktree", "Ahead-of-upstream validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");
        Path worktreeDir = Path.of(worktreePath);
        Files.writeString(worktreeDir.resolve("ahead.txt"), "unpushed worktree commit\n", StandardCharsets.UTF_8);
        runGit(worktreeDir, "add", "ahead.txt");
        runGit(worktreeDir, "commit", "-m", "Add ahead worktree commit");

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> projects.deleteProjectWorktree(projectId, worktreePath));

        assertEquals("Cannot delete worktree while it has unpushed commits", error.getMessage());
        assertTrue(projects.listProjects().contains("\"hasUnpushedCommits\":true"));
    }

    @Test
    void rejectsDeletingWorktreeWithoutUpstream() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("WTLOCAL", "Local Worktree", "No-upstream validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature-preview");

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> projects.deleteProjectWorktree(projectId, worktreePath));

        assertEquals("Cannot delete worktree without an upstream branch", error.getMessage());
        assertTrue(projects.listProjects().contains("\"deletionRestriction\":\"Worktree has no upstream branch\""));
    }

    @Test
    void prunesStaleProjectWorktreeRecords() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("WTPRUNE", "Prune Worktree", "Prune validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");

        deleteRecursively(Path.of(worktreePath));
        String stalePayload = projects.listProjects();
        assertTrue(stalePayload.contains("\"stale\":true"));

        projects.pruneProjectWorktrees(projectId);
        String prunedPayload = projects.listProjects();

        assertTrue(!prunedPayload.contains("\"path\":\"" + escapeJson(worktreePath) + "\""));
    }

    @Test
    void repairsProjectWorktreesFromBoundRepositoryContext() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("WTREPAIR", "Repair Worktree", "Repair validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());

        assertEquals(repositoryDir.toString(), projects.repairProjectWorktrees(projectId));
    }

    @Test
    void mergesManagedLinkedWorktreeBranchIntoAnotherLocalBranch() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("MERGE", "Merge Support", "Merge execution validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");

        String mergedBranch = projects.mergeProjectWorktree(projectId, worktreePath, "repo-test");
        String payload = projects.listProjects();

        assertEquals("repo-test", mergedBranch);
        assertTrue(Files.exists(repositoryDir.resolve("api-preview.txt")));
        assertTrue(payload.contains("Merge branch 'feature/api-preview'"));
        assertTrue(payload.contains("\"branch\":\"repo-test\""));
    }

    @Test
    void rejectsMergingFromDirtyLinkedWorktree() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("MRDIRTY", "Dirty Merge", "Dirty merge validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");
        Files.writeString(Path.of(worktreePath).resolve("README.md"), "dirty merge source\n", StandardCharsets.UTF_8);

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> projects.mergeProjectWorktree(projectId, worktreePath, "repo-test"));

        assertEquals("Worktree has uncommitted changes", error.getMessage());
    }

    @Test
    void rejectsUsingMainRepositoryWorktreeAsMergeSource() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("MRMAIN", "Main Merge", "Main worktree merge validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> projects.mergeProjectWorktree(projectId, repositoryDir.toString(), "feature-preview"));

        assertEquals("Main repository worktree cannot be used as a merge source", error.getMessage());
    }

    @Test
    void rejectsMergingIntoMissingLocalBranch() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("MRTARGET", "Invalid Target", "Invalid merge target validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> projects.mergeProjectWorktree(projectId, worktreePath, "release/9.9"));

        assertEquals("Target branch does not exist locally: release/9.9", error.getMessage());
    }

    @Test
    void abortsConflictedMergeWithoutLeavingMergeInProgressState() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("MRCONFLICT", "Conflict Merge", "Conflicting merge validation");
        Path repositoryDir = createConflictingGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/conflict");

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> projects.mergeProjectWorktree(projectId, worktreePath, "repo-test"));

        assertEquals(
                "Merge conflict detected while merging feature/conflict into repo-test; the platform aborted the in-progress merge",
                error.getMessage());
        assertEquals("", runGitAndReadOutputAllowFailure(repositoryDir, "rev-parse", "-q", "--verify", "MERGE_HEAD"));
        assertEquals("", runGitAndReadOutput(repositoryDir, "status", "--porcelain"));
    }

    @Test
    void exposesSandboxContextForLinkedWorktreesAndRestrictedMainWorktree() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("SBCTX", "Sandbox Context", "Sandbox context validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");
        String payload = projects.listProjects();

        assertTrue(payload.contains("\"restriction\":\"Main repository worktree cannot be used as a sandbox source\""));
        assertTrue(payload.contains("\"imageStatus\":\"MISSING\""));
        assertTrue(payload.contains("\"containerStatus\":\"INACTIVE\""));
        assertTrue(payload.contains("\"imageInitScriptPath\":\"init-image.sh\""));
        assertTrue(payload.contains("\"projectInitScriptPath\":\"init-project.sh\""));
        assertTrue(payload.contains("\"imageInitScriptSource\":\"DEFAULT\""));
        assertTrue(payload.contains("\"projectInitScriptSource\":\"DEFAULT\""));
        assertTrue(payload.contains("\"path\":\"" + escapeJson(worktreePath) + "\""));
        assertTrue(payload.contains("\"supported\":true"));

        projects.deleteProjectWorktree(projectId, worktreePath);
    }

    @Test
    void exposesRestrictedDerivedAppAssemblyStateForUnboundProjects() {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("ASM0", "Assembly Restricted", "Assembly restriction validation");

        String payload = projects.getProjectDerivedAppAssembly(projectId);

        assertTrue(payload.contains("\"available\":false"));
        assertTrue(payload.contains("\"status\":\"RESTRICTED\""));
        assertTrue(payload.contains("Bind a repository first to run project-scoped derived-app assembly."));
    }

    @Test
    void rejectsDerivedAppAssemblyRequestWithRelativeOutputDirectoryAndKeepsOutcomeReadable() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("ASM1", "Assembly Validation", "Assembly validation test");
        Path repositoryDir = createGitRepository();
        projects.bindProjectRepository(projectId, repositoryDir.toString());

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> projects.requestProjectDerivedAppAssembly(
                        projectId,
                        """
                                {
                                  "schemaVersion": "fsp-app-manifest/v1",
                                  "application": {
                                    "id": "assembly-validation-app",
                                    "name": "Assembly Validation App",
                                    "packagePrefix": "com.fastservice.platform.derived"
                                  },
                                  "modules": [
                                    "admin-shell",
                                    "user-management",
                                    "role-permission-management"
                                  ]
                                }
                                """,
                        "relative-output"));

        assertEquals("Output directory must be absolute: relative-output", error.getMessage());
        String payload = projects.getProjectDerivedAppAssembly(projectId);
        assertTrue(payload.contains("\"category\":\"REQUEST_VALIDATION\""));
        assertTrue(payload.contains("\"status\":\"FAILED\""));
        assertTrue(payload.contains("Output directory must be absolute: relative-output"));
    }

    @Test
    void rejectsDerivedAppAssemblyRequestWithUnknownModuleAsRequestValidationFailure() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("ASM1B", "Assembly Contract Validation", "Assembly contract validation test");
        Path repositoryDir = createGitRepository();
        projects.bindProjectRepository(projectId, repositoryDir.toString());
        Path outputDir = Files.createTempDirectory("fsp-project-derived-app-invalid-");

        try {
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> projects.requestProjectDerivedAppAssembly(
                            projectId,
                            """
                                    {
                                      "schemaVersion": "fsp-app-manifest/v1",
                                      "application": {
                                        "id": "assembly-contract-validation-app",
                                        "name": "Assembly Contract Validation App",
                                        "packagePrefix": "com.fastservice.platform.derived"
                                      },
                                      "modules": [
                                        "admin-shell",
                                        "user-management",
                                        "role-permission-management",
                                        "unknown-module"
                                      ]
                                    }
                                    """,
                            outputDir.toString()));

            assertEquals("Unknown module: unknown-module", error.getMessage());
            String payload = projects.getProjectDerivedAppAssembly(projectId);
            assertTrue(payload.contains("\"category\":\"REQUEST_VALIDATION\""));
            assertTrue(payload.contains("\"status\":\"FAILED\""));
            assertTrue(payload.contains("Unknown module: unknown-module"));
        } finally {
            deleteRecursively(outputDir);
        }
    }

    @Test
    void rejectsDerivedAppAssemblyRequestWhenExistingOutputDirectoryIsNotEmpty() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("ASM1C", "Assembly Output Validation", "Assembly output directory validation test");
        Path repositoryDir = createGitRepository();
        projects.bindProjectRepository(projectId, repositoryDir.toString());
        Path outputDir = Files.createTempDirectory("fsp-project-derived-app-non-empty-");

        try {
            Files.writeString(outputDir.resolve("existing.txt"), "occupied\n", StandardCharsets.UTF_8);

            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> projects.requestProjectDerivedAppAssembly(
                            projectId,
                            """
                                    {
                                      "schemaVersion": "fsp-app-manifest/v1",
                                      "application": {
                                        "id": "assembly-existing-output-app",
                                        "name": "Assembly Existing Output App",
                                        "packagePrefix": "com.fastservice.platform.derived"
                                      },
                                      "modules": [
                                        "admin-shell",
                                        "user-management",
                                        "role-permission-management"
                                      ]
                                    }
                                    """,
                            outputDir.toString()));

            assertEquals(
                    "Output directory already exists and must be empty: " + outputDir,
                    error.getMessage());
            String payload = projects.getProjectDerivedAppAssembly(projectId);
            assertTrue(payload.contains("\"category\":\"REQUEST_VALIDATION\""));
            assertTrue(payload.contains("\"status\":\"FAILED\""));
            assertTrue(payload.contains(escapeJson(outputDir.toString())));
        } finally {
            deleteRecursively(outputDir);
        }
    }

    @Test
    void runsDerivedAppAssemblyThroughRepositoryOwnedToolingAndStoresLatestOutcome() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("ASM2", "Assembly Success", "Assembly success test");
        Path repositoryDir = createGitRepository();
        Path repoRoot = createFakePlatformToolRepoRoot();
        projects.bindProjectRepository(projectId, repositoryDir.toString());
        Path outputDir = Files.createTempDirectory("fsp-project-derived-app-");
        String previousRepoRoot = System.getProperty("fsp.repo-root");

        try {
            System.setProperty("fsp.repo-root", repoRoot.toString());
            String payload = projects.requestProjectDerivedAppAssembly(
                    projectId,
                    """
                            {
                              "schemaVersion": "fsp-app-manifest/v1",
                              "application": {
                                "id": "project-derived-success-app",
                                "name": "Project Derived Success App",
                                "packagePrefix": "com.fastservice.platform.derived"
                              },
                              "modules": [
                                "admin-shell",
                                "user-management",
                                "role-permission-management",
                                "project-management"
                              ]
                            }
                            """,
                    outputDir.toString());

            assertTrue(Files.exists(outputDir.resolve("app-manifest.json")));
            assertTrue(payload.contains("\"status\":\"AVAILABLE\""));
            assertTrue(payload.contains("\"latestOutcome\":{"));
            assertTrue(payload.contains("\"category\":\"ASSEMBLY_EXECUTION\""));
            assertTrue(payload.contains("\"status\":\"SUCCESS\""));
            assertTrue(payload.contains("project-derived-success-app"));
            assertTrue(payload.contains(escapeJson(outputDir.toString())));

            ProjectServiceImpl restartedProjects = new ProjectServiceImpl();
            String restartedPayload = restartedProjects.getProjectDerivedAppAssembly(projectId);
            assertTrue(restartedPayload.contains("\"latestOutcome\":{"));
            assertTrue(restartedPayload.contains("\"status\":\"SUCCESS\""));
            assertTrue(restartedPayload.contains("project-derived-success-app"));
            assertTrue(restartedPayload.contains(escapeJson(outputDir.toString())));
        } finally {
            if (previousRepoRoot == null) {
                System.clearProperty("fsp.repo-root");
            } else {
                System.setProperty("fsp.repo-root", previousRepoRoot);
            }
            deleteRecursively(outputDir);
            deleteRecursively(repoRoot);
        }
    }

    @Test
    void reportsAssemblyExecutionFailureSeparatelyFromRequestValidation() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("ASM2B", "Assembly Failure", "Assembly execution failure test");
        Path repositoryDir = createGitRepository();
        Path repoRoot = createFakePlatformToolRepoRoot();
        Path outputDir = Files.createTempDirectory("fsp-project-derived-app-fail-");
        String previousRepoRoot = System.getProperty("fsp.repo-root");

        try {
            projects.bindProjectRepository(projectId, repositoryDir.toString());
            System.setProperty("fsp.repo-root", repoRoot.toString());

            IllegalStateException error = assertThrows(
                    IllegalStateException.class,
                    () -> projects.requestProjectDerivedAppAssembly(
                            projectId,
                            """
                                    {
                                      "schemaVersion": "fsp-app-manifest/v1",
                                      "application": {
                                        "id": "fail-assembly",
                                        "name": "Assembly Failure App",
                                        "packagePrefix": "com.fastservice.platform.derived"
                                      },
                                      "modules": [
                                        "admin-shell",
                                        "user-management",
                                        "role-permission-management"
                                      ]
                                    }
                                    """,
                            outputDir.toString()));

            assertEquals("Repository-owned assembly tooling failed", error.getMessage());
            String payload = projects.getProjectDerivedAppAssembly(projectId);
            assertTrue(payload.contains("\"category\":\"ASSEMBLY_EXECUTION\""));
            assertTrue(payload.contains("\"status\":\"FAILED\""));
            assertTrue(payload.contains("Repository-owned assembly tooling failed"));
        } finally {
            if (previousRepoRoot == null) {
                System.clearProperty("fsp.repo-root");
            } else {
                System.setProperty("fsp.repo-root", previousRepoRoot);
            }
            deleteRecursively(outputDir);
            deleteRecursively(repoRoot);
        }
    }

    @Test
    void exposesRestrictedDerivedAppVerificationStateForUnboundProjects() {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("VER0", "Verification Restricted", "Verification restriction validation");

        String payload = projects.getProjectDerivedAppVerification(projectId);

        assertTrue(payload.contains("\"available\":false"));
        assertTrue(payload.contains("\"status\":\"RESTRICTED\""));
        assertTrue(payload.contains("Bind a repository first to run project-scoped derived-app verification."));
    }

    @Test
    void rejectsDerivedAppVerificationWhenNoSuccessfulAssemblyOutputIsAvailable() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("VER1", "Verification Missing Target", "Verification target validation");
        Path repositoryDir = createGitRepository();
        projects.bindProjectRepository(projectId, repositoryDir.toString());

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> projects.requestProjectDerivedAppVerification(projectId));

        assertEquals(
                "Run project-scoped derived-app assembly successfully before requesting verification.",
                error.getMessage());
        String payload = projects.getProjectDerivedAppVerification(projectId);
        assertTrue(payload.contains("\"category\":\"REQUEST_VALIDATION\""));
        assertTrue(payload.contains("\"status\":\"FAILED\""));
        assertTrue(payload.contains("Generated-app verification was not started."));
        assertTrue(payload.contains("Runtime smoke was not started."));
    }

    @Test
    void runsDerivedAppVerificationAgainstLatestSuccessfulAssemblyOutput() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("VER2", "Verification Success", "Verification success test");
        Path repositoryDir = createGitRepository();
        Path repoRoot = createFakePlatformToolRepoRoot();
        Path outputDir = Files.createTempDirectory("fsp-project-derived-app-verify-");
        String previousRepoRoot = System.getProperty("fsp.repo-root");

        try {
            projects.bindProjectRepository(projectId, repositoryDir.toString());
            projects.requestProjectDerivedAppAssembly(
                    projectId,
                    """
                            {
                              "schemaVersion": "fsp-app-manifest/v1",
                              "application": {
                                "id": "project-derived-verify-app",
                                "name": "Project Derived Verify App",
                                "packagePrefix": "com.fastservice.platform.derived"
                              },
                              "modules": [
                                "admin-shell",
                                "user-management",
                                "role-permission-management",
                                "project-management"
                              ]
                            }
                            """,
                    outputDir.toString());

            System.setProperty("fsp.repo-root", repoRoot.toString());

            String payload = projects.requestProjectDerivedAppVerification(projectId);

            assertTrue(payload.contains("\"status\":\"AVAILABLE\""));
            assertTrue(payload.contains("\"category\":\"COMBINED_VALIDATION\""));
            assertTrue(payload.contains("Generated-app verification and runtime smoke completed through repository-owned tooling."));
            assertTrue(payload.contains("\"targetOutputDirectory\":\"" + escapeJson(outputDir.toString()) + "\""));
            assertTrue(payload.contains("\"generatedAppVerification\":{\"status\":\"SUCCESS\""));
            assertTrue(payload.contains("\"runtimeSmoke\":{\"status\":\"SUCCESS\""));
        } finally {
            if (previousRepoRoot == null) {
                System.clearProperty("fsp.repo-root");
            } else {
                System.setProperty("fsp.repo-root", previousRepoRoot);
            }
            deleteRecursively(outputDir);
            deleteRecursively(repoRoot);
        }
    }

    @Test
    void surfacesRuntimeSmokeFailureAfterGeneratedAppVerificationSucceeds() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("VER3", "Verification Smoke Failure", "Verification smoke failure test");
        Path repositoryDir = createGitRepository();
        Path repoRoot = createFakePlatformToolRepoRoot();
        Path outputDir = Files.createTempDirectory("fsp-project-derived-app-verify-smoke-fail-");
        String previousRepoRoot = System.getProperty("fsp.repo-root");

        try {
            projects.bindProjectRepository(projectId, repositoryDir.toString());
            projects.requestProjectDerivedAppAssembly(
                    projectId,
                    """
                            {
                              "schemaVersion": "fsp-app-manifest/v1",
                              "application": {
                                "id": "project-derived-verify-smoke-fail-app",
                                "name": "Project Derived Verify Smoke Fail App",
                                "packagePrefix": "com.fastservice.platform.derived"
                              },
                              "modules": [
                                "admin-shell",
                                "user-management",
                                "role-permission-management",
                                "project-management"
                              ]
                            }
                            """,
                    outputDir.toString());
            Files.writeString(outputDir.resolve(".fail-runtime-smoke"), "fail\n", StandardCharsets.UTF_8);

            System.setProperty("fsp.repo-root", repoRoot.toString());

            IllegalStateException error = assertThrows(
                    IllegalStateException.class,
                    () -> projects.requestProjectDerivedAppVerification(projectId));

            assertEquals(
                    "Derived-app runtime smoke failed during proxy reachability for " + outputDir,
                    error.getMessage());
            String payload = projects.getProjectDerivedAppVerification(projectId);
            assertTrue(payload.contains("\"category\":\"RUNTIME_SMOKE\""));
            assertTrue(payload.contains("\"generatedAppVerification\":{\"status\":\"SUCCESS\""));
            assertTrue(payload.contains("\"runtimeSmoke\":{\"status\":\"FAILED\""));
        } finally {
            if (previousRepoRoot == null) {
                System.clearProperty("fsp.repo-root");
            } else {
                System.setProperty("fsp.repo-root", previousRepoRoot);
            }
            deleteRecursively(outputDir);
            deleteRecursively(repoRoot);
        }
    }
}
