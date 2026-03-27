package com.fastservice.platform.backend.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.Test;

import com.fastservice.platform.backend.bootstrap.BackendBootstrap;
import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.support.BackendTestSupport;

class BackendBootstrapIntegrationTest {

    @Test
    void bootsWithOptionalDemoData() throws Exception {
        String databaseName = BackendTestSupport.uniqueDatabaseName("integration_test");
        BackendTestSupport.bootstrap(databaseName, true);

        try (Connection connection = JdbcSupport.getConnection(databaseName)) {
            assertTrue(rowCount(connection, "app_user") >= 1);
            assertTrue(rowCount(connection, "software_project") >= 1);
            assertTrue(rowCount(connection, "ticket") >= 1);
        }
    }

    @Test
    void reloadsDemoDataIdempotently() throws Exception {
        String databaseName = BackendTestSupport.uniqueDatabaseName("integration_demo");
        Path baseDir = BackendTestSupport.createBaseDir("integration-demo");

        BackendBootstrap.initializeEmbedded(databaseName, baseDir, true);
        BackendBootstrap.initializeEmbedded(databaseName, baseDir, true);

        try (Connection connection = JdbcSupport.getConnection(databaseName)) {
            assertTrue(rowCount(connection, "app_user") == 1);
            assertTrue(rowCount(connection, "software_project") == 1);
            assertTrue(rowCount(connection, "kanban_board") == 1);
            assertTrue(rowCount(connection, "ticket") == 2);
        }
    }

    private long rowCount(Connection connection, String tableName) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM " + tableName);
                ResultSet rs = statement.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }
}
