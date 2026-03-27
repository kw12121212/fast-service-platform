package com.fastservice.platform.backend.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import com.fastservice.platform.backend.bootstrap.BackendBootstrap;

public final class BackendTestSupport {

    private BackendTestSupport() {
    }

    public static String uniqueDatabaseName(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }

    public static Path createBaseDir(String prefix) {
        try {
            return Files.createTempDirectory(prefix + "-lealone-");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create test base directory", e);
        }
    }

    public static void bootstrap(String databaseName, boolean loadDemoData) {
        BackendBootstrap.initializeEmbedded(databaseName, createBaseDir(databaseName), loadDemoData);
    }
}
