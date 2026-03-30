package com.fastservice.platform.backend.common.sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SqlScriptExecutor {

    private SqlScriptExecutor() {
    }

    public static void executeClasspathResource(Connection connection, String resourcePath) throws IOException, SQLException {
        try (InputStream in = SqlScriptExecutor.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Missing classpath resource: " + resourcePath);
            }
            execute(connection, readAll(in));
        }
    }

    public static void execute(Connection connection, String script) throws SQLException {
        String normalized = stripComments(script);
        for (String statementSql : normalized.split(";")) {
            String trimmed = statementSql.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute(trimmed);
            }
        }
    }

    private static String readAll(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    private static String stripComments(String script) {
        StringBuilder builder = new StringBuilder();
        for (String line : script.split("\n")) {
            int commentStart = line.indexOf("--");
            if (commentStart >= 0) {
                line = line.substring(0, commentStart);
            }
            builder.append(line).append('\n');
        }
        return builder.toString();
    }
}
