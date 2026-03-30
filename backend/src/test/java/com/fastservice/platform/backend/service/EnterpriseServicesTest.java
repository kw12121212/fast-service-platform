package com.fastservice.platform.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastservice.platform.backend.access.AccessControlServiceImpl;
import com.fastservice.platform.backend.kanban.KanbanServiceImpl;
import com.fastservice.platform.backend.project.ProjectServiceImpl;
import com.fastservice.platform.backend.support.BackendTestSupport;
import com.fastservice.platform.backend.ticket.TicketServiceImpl;
import com.fastservice.platform.backend.user.UserServiceImpl;

class EnterpriseServicesTest {

    @BeforeEach
    void bootstrapBackend() {
        BackendTestSupport.bootstrap(BackendTestSupport.uniqueDatabaseName("service_test"), false);
    }

    @Test
    void createsUsersRolesProjectsAndTickets() {
        UserServiceImpl users = new UserServiceImpl();
        AccessControlServiceImpl access = new AccessControlServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();

        long userId = users.createUser("service-user", "Service User", "service@example.com");
        long roleId = access.createRole("DEV", "Developer");
        long permissionId = access.createPermission("project:view", "View Projects", "FUNCTION");
        access.assignPermissionToRole(roleId, permissionId);
        access.assignRoleToUser(userId, roleId);

        long projectId = projects.createProject("SVT", "Service Test", "Service layer test project");
        long kanbanId = kanbans.createKanban(projectId, "Sprint Board");
        long ticketId = tickets.createTicket(projectId, kanbanId, "SVT-1", "Create service test ticket", "Validate service APIs", userId);
        String movedState = tickets.moveTicket(ticketId, "IN_PROGRESS");

        assertTrue(users.listUsers().contains("service-user"));
        assertTrue(access.listRoles().contains("\"code\":\"DEV\""));
        assertTrue(access.listPermissions().contains("\"code\":\"project:view\""));
        assertTrue(access.listRolesForUser(userId).contains("\"code\":\"DEV\""));
        assertTrue(access.listPermissionsForRole(roleId).contains("\"code\":\"project:view\""));
        assertTrue(projects.listProjects().contains("Service Test"));
        assertTrue(kanbans.listKanbansByProject(projectId).contains("Sprint Board"));
        assertTrue(tickets.listTicketsByProject(projectId).contains("SVT-1"));
        assertTrue("IN_PROGRESS".equals(movedState));
    }

    @Test
    void escapesJsonOutputValues() {
        UserServiceImpl users = new UserServiceImpl();

        users.createUser("json-user", "Display \"Name\"\nLine", "json\\user@example.com");

        String payload = users.listUsers();
        assertTrue(payload.contains("\"displayName\":\"Display \\\"Name\\\"\\nLine\""));
        assertTrue(payload.contains("\"email\":\"json\\\\user@example.com\""));
    }

    @Test
    void rejectsInvalidProjectAndKanbanRelationships() {
        UserServiceImpl users = new UserServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();

        long userId = users.createUser("relationship-user", "Relationship User", "relationship@example.com");
        long projectId = projects.createProject("REL", "Relationship Test", "Relationship validation");
        long boardId = kanbans.createKanban(projectId, "Relationship Board");
        long otherProjectId = projects.createProject("OTH", "Other Project", "Relationship mismatch");

        assertThrows(IllegalArgumentException.class, () -> kanbans.createKanban(99999L, "Broken Board"));
        assertThrows(IllegalArgumentException.class,
                () -> tickets.createTicket(projectId, 99999L, "REL-404", "Missing board", "Should fail", userId));
        assertThrows(IllegalArgumentException.class,
                () -> tickets.createTicket(otherProjectId, boardId, "OTH-1", "Wrong board", "Should fail", userId));
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

    private Path createGitRepository() throws Exception {
        Path remoteRepositoryDir = Files.createTempDirectory("fsp-project-remote-");
        runGit(remoteRepositoryDir, "init", "--bare");
        Path repositoryDir = Files.createTempDirectory("fsp-project-repository-");
        runGit(repositoryDir, "init");
        runGit(repositoryDir, "config", "user.name", "Fast Service Tests");
        runGit(repositoryDir, "config", "user.email", "tests@fastservice.local");
        runGit(repositoryDir, "checkout", "-b", "repo-test");
        Files.writeString(repositoryDir.resolve("README.md"), "repository binding test\n", StandardCharsets.UTF_8);
        runGit(repositoryDir, "add", "README.md");
        runGit(repositoryDir, "commit", "-m", "Initial platform repo");
        runGit(repositoryDir, "remote", "add", "origin", remoteRepositoryDir.toString());
        runGit(repositoryDir, "push", "-u", "origin", "repo-test");
        Files.writeString(repositoryDir.resolve("README.md"), "repository binding test\nsecond commit\n", StandardCharsets.UTF_8);
        runGit(repositoryDir, "add", "README.md");
        runGit(repositoryDir, "commit", "-m", "Second platform repo commit");
        runGit(repositoryDir, "push", "origin", "repo-test");
        runGit(repositoryDir, "checkout", "-b", "feature/api-preview");
        Files.writeString(repositoryDir.resolve("api-preview.txt"), "preview worktree branch\n", StandardCharsets.UTF_8);
        runGit(repositoryDir, "add", "api-preview.txt");
        runGit(repositoryDir, "commit", "-m", "Add API preview branch");
        runGit(repositoryDir, "push", "-u", "origin", "feature/api-preview");
        runGit(repositoryDir, "checkout", "repo-test");
        runGit(repositoryDir, "branch", "feature-preview");
        return repositoryDir;
    }

    private Path createDetachedHeadRepository() throws Exception {
        Path repositoryDir = createGitRepository();
        String headCommit = runGitAndReadOutput(repositoryDir, "rev-parse", "HEAD");
        runGit(repositoryDir, "checkout", headCommit);
        return repositoryDir;
    }

    private void runGit(Path repositoryDir, String... args) throws Exception {
        String output = runGitAndReadOutput(repositoryDir, args);
        if (output.startsWith("fatal:")) {
            throw new IOException("Git command failed: " + output);
        }
    }

    private String runGitAndReadOutput(Path repositoryDir, String... args) throws Exception {
        String[] command = new String[args.length + 3];
        command[0] = "git";
        command[1] = "-C";
        command[2] = repositoryDir.toString();
        System.arraycopy(args, 0, command, 3, args.length);
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Git command failed: " + output);
        }
        return output;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\");
    }

    private void deleteRecursively(Path path) throws IOException {
        if (Files.notExists(path)) {
            return;
        }

        try (var stream = Files.walk(path)) {
            stream.sorted(Comparator.reverseOrder()).forEach(entry -> {
                try {
                    Files.deleteIfExists(entry);
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to delete path: " + entry, e);
                }
            });
        }
    }
}
