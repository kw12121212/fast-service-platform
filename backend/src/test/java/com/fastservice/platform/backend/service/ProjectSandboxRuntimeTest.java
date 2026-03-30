package com.fastservice.platform.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.fastservice.platform.backend.project.ProjectServiceImpl;
import com.fastservice.platform.backend.support.BackendTestSupport;

@Tag("heavy-runtime")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectSandboxRuntimeTest extends ProjectServiceTestSupport {

    @BeforeAll
    void bootstrapBackend() {
        BackendTestSupport.bootstrap(BackendTestSupport.uniqueDatabaseName("service_test"), false);
    }

    @Test
    void createsSandboxImageUsingDefaultScriptAndCleansItUpWithWorktreeDeletion() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("SBIMG", "Sandbox Image", "Sandbox image validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");

        String imageReference = projects.createProjectSandboxImage(projectId, worktreePath);
        String payload = projects.listProjects();

        assertTrue(imageReference.contains("localhost/fsp-sandbox-"));
        assertTrue(podmanResourceExists("image", imageReference));
        assertTrue(payload.contains("\"imageStatus\":\"READY\""));
        assertTrue(payload.contains("\"imageReference\":\"" + escapeJson(imageReference) + "\""));

        projects.deleteProjectWorktree(projectId, worktreePath);
        assertTrue(!podmanResourceExists("image", imageReference));
    }

    @Test
    void createsSandboxImageAndContainerUsingWorktreeConfiguredOverrideScripts() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("SBOVR", "Sandbox Override", "Sandbox override validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");
        Path worktreeDir = Path.of(worktreePath);
        Files.createDirectories(worktreeDir.resolve("scripts"));
        Files.writeString(
                worktreeDir.resolve("scripts/custom-image.sh"),
                "#!/bin/sh\nprintf 'override image\\n' > custom-image.txt\n",
                StandardCharsets.UTF_8);
        Files.writeString(
                worktreeDir.resolve("scripts/custom-project.sh"),
                "#!/bin/sh\nprintf 'override project\\n' > custom-project.txt\n",
                StandardCharsets.UTF_8);
        upsertSandboxScriptOverrides(projectId, worktreePath, "scripts/custom-image.sh", "scripts/custom-project.sh");

        String imageReference = projects.createProjectSandboxImage(projectId, worktreePath);
        String containerName = projects.createProjectSandboxContainer(projectId, worktreePath);
        String payload = projects.listProjects();

        assertTrue(podmanResourceExists("image", imageReference));
        assertTrue(podmanResourceExists("container", containerName));
        assertEquals("ok", runPodmanAndReadOutput("exec", containerName, "/bin/sh", "-c",
                "test -f /workspace/custom-image.txt && test -f /workspace/custom-project.txt && printf ok"));
        assertTrue(payload.contains("\"imageInitScriptPath\":\"scripts/custom-image.sh\""));
        assertTrue(payload.contains("\"projectInitScriptPath\":\"scripts/custom-project.sh\""));
        assertTrue(payload.contains("\"imageInitScriptSource\":\"WORKTREE_PROPERTY\""));
        assertTrue(payload.contains("\"projectInitScriptSource\":\"WORKTREE_PROPERTY\""));
        assertTrue(payload.contains("\"containerStatus\":\"ACTIVE\""));

        projects.deleteProjectSandboxContainer(projectId, worktreePath);
        deleteRecursively(worktreeDir.resolve("scripts"));
        projects.deleteProjectWorktree(projectId, worktreePath);
    }

    @Test
    void rejectsCreatingSandboxContainerWithoutImageAndRejectsSecondActiveContainer() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("SBCTR", "Sandbox Container", "Sandbox container validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");

        IllegalStateException missingImageError = assertThrows(
                IllegalStateException.class,
                () -> projects.createProjectSandboxContainer(projectId, worktreePath));
        assertEquals("Sandbox image does not exist for this worktree", missingImageError.getMessage());

        projects.createProjectSandboxImage(projectId, worktreePath);
        String containerName = projects.createProjectSandboxContainer(projectId, worktreePath);

        IllegalStateException duplicateContainerError = assertThrows(
                IllegalStateException.class,
                () -> projects.createProjectSandboxContainer(projectId, worktreePath));
        assertEquals("A sandbox container is already active for this worktree", duplicateContainerError.getMessage());
        assertTrue(podmanResourceExists("container", containerName));

        projects.deleteProjectSandboxContainer(projectId, worktreePath);
        projects.deleteProjectWorktree(projectId, worktreePath);
    }

    @Test
    void exposesSandboxInitializationFailuresForImageAndContainer() throws Exception {
        ProjectServiceImpl projects = new ProjectServiceImpl();
        long projectId = projects.createProject("SBFAIL", "Sandbox Failure", "Sandbox failure validation");
        Path repositoryDir = createGitRepository();

        projects.bindProjectRepository(projectId, repositoryDir.toString());
        String worktreePath = projects.createProjectWorktree(projectId, "feature/api-preview");
        Path worktreeDir = Path.of(worktreePath);
        Files.createDirectories(worktreeDir.resolve("scripts"));
        Files.writeString(
                worktreeDir.resolve("scripts/fail-image.sh"),
                "#!/bin/sh\necho image failed\nexit 7\n",
                StandardCharsets.UTF_8);
        upsertSandboxScriptOverrides(projectId, worktreePath, "scripts/fail-image.sh", null);

        IllegalStateException imageError = assertThrows(
                IllegalStateException.class,
                () -> projects.createProjectSandboxImage(projectId, worktreePath));
        assertTrue(imageError.getMessage().contains("Unable to build sandbox image"));
        assertTrue(projects.listProjects().contains("\"imageStatus\":\"FAILED\""));
        assertTrue(projects.listProjects().contains("image failed"));

        Files.writeString(
                worktreeDir.resolve("scripts/ok-image.sh"),
                "#!/bin/sh\nprintf 'image repaired\\n' > repaired-image.txt\n",
                StandardCharsets.UTF_8);
        Files.writeString(
                worktreeDir.resolve("scripts/fail-project.sh"),
                "#!/bin/sh\necho project failed\nexit 9\n",
                StandardCharsets.UTF_8);
        upsertSandboxScriptOverrides(projectId, worktreePath, "scripts/ok-image.sh", "scripts/fail-project.sh");
        projects.createProjectSandboxImage(projectId, worktreePath);

        IllegalStateException containerError = assertThrows(
                IllegalStateException.class,
                () -> projects.createProjectSandboxContainer(projectId, worktreePath));
        assertTrue(containerError.getMessage().contains("Sandbox container initialization failed"));
        assertTrue(projects.listProjects().contains("\"containerStatus\":\"FAILED\""));
        assertTrue(projects.listProjects().contains("project failed"));

        deleteRecursively(worktreeDir.resolve("scripts"));
        projects.deleteProjectWorktree(projectId, worktreePath);
    }
}
