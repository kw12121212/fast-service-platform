package com.fastservice.platform.tools.generatedappverifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class GeneratedAppVerifierTest {

    private final Path repoRoot = Path.of(System.getProperty("fsp.repo-root")).toAbsolutePath().normalize();

    @Test
    void verifierReportsMissingVerificationContract() throws Exception {
        GeneratedAppVerifier verifier = new GeneratedAppVerifier();
        Path tempDir = Files.createTempDirectory("fsp-java-verifier-missing-");

        try {
            GeneratedAppVerifier.VerificationResult result = verifier.verify(tempDir);
            assertFalse(result.ok());
            assertEquals(List.of("Missing required file: docs/ai/generated-app-verification-contract.json"), result.issues());
            assertEquals("unavailable", result.contractVersion());
            assertEquals("java-generated-app-verifier-cli", result.verifierId());
        } finally {
            deleteRecursively(tempDir);
        }
    }

    @Test
    void verifierSucceedsForGeneratedCoreAdminApp() throws Exception {
        Path outputDir = Files.createTempDirectory("fsp-java-generated-app-");
        scaffoldCoreAdminApp(outputDir);

        try {
            GeneratedAppVerifier verifier = new GeneratedAppVerifier();
            GeneratedAppVerifier.VerificationResult result = verifier.verify(outputDir);

            assertTrue(result.ok());
            assertEquals(List.of("admin-shell", "user-management", "role-permission-management"), result.selectedModules());
            assertEquals("fsp-generated-app-verification-contract/v1", result.contractVersion());
            assertEquals("java-generated-app-verifier-cli", result.verifierId());
        } finally {
            deleteRecursively(outputDir);
        }
    }

    private void scaffoldCoreAdminApp(Path outputDir) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(
                repoRoot.resolve("scripts/scaffold-derived-app-java.sh").toString(),
                repoRoot.resolve("docs/ai/manifests/core-admin-app.json").toString(),
                outputDir.toString())
                .directory(repoRoot.toFile())
                .redirectErrorStream(true)
                .start();

        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("Unable to scaffold generated app for verifier test:\n" + output);
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
