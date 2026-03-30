package com.fastservice.platform.backend.bootstrap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.demo.DemoDataSupport;
import com.lealone.db.SysProperties;
import com.lealone.main.Lealone;
import com.lealone.plugins.boot.LealoneApplication;

public final class BackendBootstrap {

    public static final String DEFAULT_DATABASE = "fsp_enterprise";
    public static final String DATABASE_PROPERTY = "fsp.database";
    public static final String JDBC_URL_TEMPLATE_PROPERTY = "fsp.jdbc-url-template";
    private static final int STARTUP_WAIT_ATTEMPTS = 50;
    private static final long STARTUP_WAIT_MILLIS = 200L;
    private static final Set<String> STARTED_DATABASES = ConcurrentHashMap.newKeySet();

    private BackendBootstrap() {
    }

    public static void start(String databaseName, Path baseDir, boolean loadDemoData) {
        try {
            Files.createDirectories(baseDir);
            System.setProperty(DATABASE_PROPERTY, databaseName);
            // Service implementations run in-process with the Lealone runtime, so they should
            // keep using embed JDBC instead of looping back through the TCP listener.
            System.setProperty(JDBC_URL_TEMPLATE_PROPERTY, "jdbc:lealone:embed:%s");
            SysProperties.setBaseDir(baseDir.toAbsolutePath().toString(), true);

            if (STARTED_DATABASES.add(databaseName)) {
                Path sqlBaseDir = baseDir.resolve("bootstrap-sql");
                String tablesSql = SqlResourceExtractor.extract("/sql/tables.sql", sqlBaseDir);
                String servicesSql = SqlResourceExtractor.extract("/sql/services.sql", sqlBaseDir);
                LealoneApplication app = new LealoneApplication();
                app.setBaseDir(baseDir.toAbsolutePath().toString());
                app.setDatabase(databaseName);
                app.start();

                awaitDatabaseConnection(databaseName);
                initializeSchema(databaseName, tablesSql, servicesSql);
                if (loadDemoData) {
                    DemoDataSupport.load(databaseName);
                }
            } else if (loadDemoData) {
                DemoDataSupport.load(databaseName);
            }
        } catch (Exception e) {
            STARTED_DATABASES.remove(databaseName);
            throw new IllegalStateException("Unable to bootstrap backend runtime", e);
        }
    }

    public static void initializeEmbedded(String databaseName, Path baseDir, boolean loadDemoData) {
        try {
            Files.createDirectories(baseDir);
            System.setProperty(DATABASE_PROPERTY, databaseName);
            System.setProperty(JDBC_URL_TEMPLATE_PROPERTY, "jdbc:lealone:embed:%s");
            SysProperties.setBaseDir(baseDir.toAbsolutePath().toString(), true);

            if (STARTED_DATABASES.add(databaseName)) {
                Path sqlBaseDir = baseDir.resolve("bootstrap-sql");
                String tablesSql = SqlResourceExtractor.extract("/sql/tables.sql", sqlBaseDir);
                String servicesSql = SqlResourceExtractor.extract("/sql/services.sql", sqlBaseDir);
                initializeSchema(databaseName, tablesSql, servicesSql);
            }

            if (loadDemoData) {
                DemoDataSupport.load(databaseName);
            }
        } catch (Exception e) {
            STARTED_DATABASES.remove(databaseName);
            throw new IllegalStateException("Unable to initialize embedded backend runtime", e);
        }
    }

    private static void initializeSchema(String databaseName, String tablesSql, String servicesSql) {
        Lealone.runScript("jdbc:lealone:embed:" + databaseName, tablesSql, servicesSql);
    }

    private static void awaitDatabaseConnection(String databaseName) throws InterruptedException {
        for (int attempt = 0; attempt < STARTUP_WAIT_ATTEMPTS; attempt++) {
            try (Connection connection = JdbcSupport.getConnection(databaseName);
                    PreparedStatement statement = connection.prepareStatement("SELECT 1")) {
                statement.executeQuery().close();
                return;
            } catch (Exception e) {
                Thread.sleep(STARTUP_WAIT_MILLIS);
            }
        }
        throw new IllegalStateException("Timed out waiting for backend database connection: " + databaseName);
    }
}
