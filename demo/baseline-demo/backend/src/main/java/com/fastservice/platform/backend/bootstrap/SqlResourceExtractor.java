package com.fastservice.platform.backend.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class SqlResourceExtractor {

    private SqlResourceExtractor() {
    }

    public static String extract(String resourcePath, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        String fileName = Path.of(resourcePath).getFileName().toString();
        Path output = outputDir.resolve(fileName);

        try (InputStream in = SqlResourceExtractor.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Missing classpath resource: " + resourcePath);
            }
            Files.copy(in, output, StandardCopyOption.REPLACE_EXISTING);
        }

        return output.toAbsolutePath().toString();
    }
}
