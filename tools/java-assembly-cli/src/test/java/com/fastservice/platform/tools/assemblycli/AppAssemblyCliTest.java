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
            assertTrue(context.contains("\"selectedModules\":[\"admin-shell\",\"user-management\",\"role-permission-management\"]"));
            assertTrue(Files.exists(outputDir.resolve("scripts/app-assembly-lib.mjs")));
            assertTrue(Files.exists(outputDir.resolve("scripts/verify-derived-app.mjs")));
            assertTrue(Files.exists(outputDir.resolve("scripts/verify-derived-app.sh")));
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
