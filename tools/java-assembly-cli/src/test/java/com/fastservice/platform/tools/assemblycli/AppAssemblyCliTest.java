package com.fastservice.platform.tools.assemblycli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AppAssemblyCliTest {

    private final Path repoRoot = Path.of(System.getProperty("fsp.repo-root")).toAbsolutePath().normalize();

    @Test
    void validateManifestRejectsMissingRequiredCoreModules() throws Exception {
        AssemblyGenerator generator = new AssemblyGenerator(repoRoot);
        var registry = generator.loadRegistry();
        var contract = generator.loadContract();
        var manifest = SimpleJson.parseObject("""
                {
                  "schemaVersion": "fsp-app-manifest/v1",
                  "application": {
                    "id": "broken-app",
                    "name": "Broken App",
                    "packagePrefix": "com.fastservice.platform.derived"
                  },
                  "modules": [
                    "admin-shell",
                    "user-management"
                  ]
                }
                """);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> generator.validateManifest(manifest, registry, contract));
        assertTrue(error.getMessage().contains("required core module"));
    }

    @Test
    void scaffoldGeneratesCoreOnlyAppWithoutDeliveryWiring() throws Exception {
        AssemblyGenerator generator = new AssemblyGenerator(repoRoot);
        Path outputDir = Files.createTempDirectory("fsp-java-core-admin-");

        try {
            AssemblyGenerator.AssemblyResult result = generator.scaffold(
                    repoRoot.resolve("docs/ai/manifests/core-admin-app.json"),
                    outputDir);

            assertEquals(List.of("admin-shell", "user-management", "role-permission-management"), result.selectedModules());

            String router = Files.readString(outputDir.resolve("frontend/src/app/router.tsx"));
            assertFalse(router.contains("path: 'projects'"));
            assertFalse(router.contains("path: 'tickets'"));
            assertFalse(router.contains("path: 'kanban'"));

            String servicesSql = Files.readString(outputDir.resolve("backend/src/main/resources/sql/services.sql"));
            assertFalse(servicesSql.contains("project_service"));
            assertFalse(servicesSql.contains("ticket_service"));
            assertFalse(servicesSql.contains("kanban_service"));

            String context = Files.readString(outputDir.resolve("docs/ai/context.json"));
            String readme = Files.readString(outputDir.resolve("README.md"));
            assertTrue(context.contains("\"selectedModules\":[\"admin-shell\",\"user-management\",\"role-permission-management\"]"));
            assertTrue(context.contains("\"platformReleaseHistory\":\"docs/ai/platform-release-history.json\""));
            assertTrue(context.contains("\"aiToolOrchestrationContract\":\"docs/ai/ai-tool-orchestration-contract.json\""));
            assertTrue(context.contains("\"repositoryOwned\":\"./scripts/platform-tool.sh generated-app verify <generated-app-dir>\""));
            assertTrue(readme.contains("## AI Tooling"));
            assertTrue(readme.contains("docs/ai/ai-tool-orchestration-contract.json"));
            assertTrue(Files.exists(outputDir.resolve("scripts/VerifyDerivedApp.java")));
            assertTrue(Files.exists(outputDir.resolve("scripts/verify-derived-app.sh")));
            assertTrue(Files.exists(outputDir.resolve("docs/ai/platform-release-history.json")));
        } finally {
            deleteRecursively(outputDir);
        }
    }

    @Test
    void scaffoldProjectAdminOmitsRepositoryBindingWiring() throws Exception {
        AssemblyGenerator generator = new AssemblyGenerator(repoRoot);
        Path outputDir = Files.createTempDirectory("fsp-java-project-admin-");

        try {
            AssemblyGenerator.AssemblyResult result = generator.scaffold(
                    repoRoot.resolve("docs/ai/compatibility/fixtures/project-admin.manifest.json"),
                    outputDir);

            assertEquals(List.of(
                    "admin-shell",
                    "user-management",
                    "role-permission-management",
                    "project-management"), result.selectedModules());

            String servicesSql = Files.readString(outputDir.resolve("backend/src/main/resources/sql/services.sql"));
            String tablesSql = Files.readString(outputDir.resolve("backend/src/main/resources/sql/tables.sql"));
            String projectsPage = Files.readString(outputDir.resolve("frontend/src/features/projects/projects-page.tsx"));

            assertTrue(servicesSql.contains("project_service"));
            assertFalse(servicesSql.contains("bindProjectRepository"));
            assertFalse(servicesSql.contains("switchProjectBranch"));
            assertTrue(tablesSql.contains("software_project"));
            assertFalse(tablesSql.contains("project_repository_binding"));
            assertFalse(projectsPage.contains("Bind repository"));
            assertFalse(projectsPage.contains("Switch branch"));
            assertTrue(projectsPage.contains("Repository binding workflows are not enabled for this derived application assembly."));
        } finally {
            deleteRecursively(outputDir);
        }
    }

    @Test
    void scaffoldProjectRepositoryIncludesRepositoryBindingWiring() throws Exception {
        AssemblyGenerator generator = new AssemblyGenerator(repoRoot);
        Path outputDir = Files.createTempDirectory("fsp-java-project-repository-");

        try {
            AssemblyGenerator.AssemblyResult result = generator.scaffold(
                    repoRoot.resolve("docs/ai/compatibility/fixtures/project-repository.manifest.json"),
                    outputDir);

            assertEquals(List.of(
                    "admin-shell",
                    "user-management",
                    "role-permission-management",
                    "project-management",
                    "project-repository-management"), result.selectedModules());

            String servicesSql = Files.readString(outputDir.resolve("backend/src/main/resources/sql/services.sql"));
            String tablesSql = Files.readString(outputDir.resolve("backend/src/main/resources/sql/tables.sql"));
            String projectsPage = Files.readString(outputDir.resolve("frontend/src/features/projects/projects-page.tsx"));

            assertTrue(servicesSql.contains("project_service"));
            assertTrue(servicesSql.contains("bindProjectRepository"));
            assertTrue(servicesSql.contains("switchProjectBranch"));
            assertTrue(tablesSql.contains("software_project"));
            assertTrue(tablesSql.contains("project_repository_binding"));
            assertTrue(projectsPage.contains("Bind repository"));
            assertTrue(projectsPage.contains("Switch branch"));
        } finally {
            deleteRecursively(outputDir);
        }
    }

    private static void deleteRecursively(Path path) throws Exception {
        if (!Files.exists(path)) {
            return;
        }
        try (var stream = Files.walk(path)) {
            stream.sorted((left, right) -> right.compareTo(left))
                    .forEach(current -> {
                        try {
                            Files.deleteIfExists(current);
                        } catch (Exception error) {
                            throw new IllegalStateException(error);
                        }
                    });
        }
    }
}
