package com.fastservice.platform.backend.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class EntityExistence {

    private EntityExistence() {
    }

    public static void requireExists(String tableName, long id, String entityName) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                if (rs.getLong(1) == 0) {
                    throw new IllegalArgumentException(entityName + " not found: " + id);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to validate " + entityName + " existence", e);
        }
    }
}
