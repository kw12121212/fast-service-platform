package com.fastservice.platform.backend.common.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.fastservice.platform.backend.bootstrap.BackendBootstrap;

public final class JdbcSupport {

    private static final String DEFAULT_URL_TEMPLATE = "jdbc:lealone:embed:%s";

    private JdbcSupport() {
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(System.getProperty(BackendBootstrap.DATABASE_PROPERTY, BackendBootstrap.DEFAULT_DATABASE));
    }

    public static Connection getConnection(String databaseName) throws SQLException {
        return DriverManager.getConnection(resolveJdbcUrl(databaseName), "root", "");
    }

    private static String resolveJdbcUrl(String databaseName) {
        String template = System.getProperty(BackendBootstrap.JDBC_URL_TEMPLATE_PROPERTY, DEFAULT_URL_TEMPLATE);
        return template.formatted(databaseName);
    }
}
