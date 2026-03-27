package com.fastservice.platform.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
        assertTrue(payload.contains("\"rootPath\":\"" + escapeJson(repositoryDir.toString()) + "\""));
        assertTrue(payload.contains("\"branch\":\"repo-test\""));
        assertTrue(payload.contains("\"workingTreeState\":\"CLEAN\""));
        assertTrue(payload.contains("Initial platform repo"));
    }

    @Test
    void rejectsRelativeRepositoryPathAndKeepsProjectUnbound() {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("PATH", "Path Validation", "Repository path validation");

        assertThrows(IllegalArgumentException.class, () -> projects.bindProjectRepository(projectId, "relative/repository"));
        assertTrue(projects.listProjects().contains("\"repository\":null"));
    }

    private Path createGitRepository() throws Exception {
        Path repositoryDir = Files.createTempDirectory("fsp-project-repository-");
        runGit(repositoryDir, "init");
        runGit(repositoryDir, "config", "user.name", "Fast Service Tests");
        runGit(repositoryDir, "config", "user.email", "tests@fastservice.local");
        runGit(repositoryDir, "checkout", "-b", "repo-test");
        Files.writeString(repositoryDir.resolve("README.md"), "repository binding test\n", StandardCharsets.UTF_8);
        runGit(repositoryDir, "add", "README.md");
        runGit(repositoryDir, "commit", "-m", "Initial platform repo");
        return repositoryDir;
    }

    private void runGit(Path repositoryDir, String... args) throws Exception {
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
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\");
    }
}
