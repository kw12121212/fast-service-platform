package com.fastservice.platform.backend.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.JsonStrings;

public class UserServiceImpl {

    // Lealone-generated service executors dispatch to lowercase method names.
    public long createuser(String username, String displayName, String email) {
        return createUser(username, displayName, email);
    }

    public String listusers() {
        return listUsers();
    }

    public long createUser(String username, String displayName, String email) {
        String sql = "INSERT INTO app_user(username, display_name, email, enabled) VALUES(?, ?, ?, true)";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, displayName);
            statement.setString(3, email);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new IllegalStateException("No generated key returned for user insert");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create user", e);
        }
    }

    public String listUsers() {
        String sql = "SELECT id, username, display_name, email, enabled FROM app_user ORDER BY id";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            StringBuilder builder = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                builder.append("{\"id\":").append(rs.getLong("id"));
                builder.append(",\"username\":").append(JsonStrings.quote(rs.getString("username")));
                builder.append(",\"displayName\":").append(JsonStrings.quote(rs.getString("display_name")));
                builder.append(",\"email\":").append(JsonStrings.quote(rs.getString("email")));
                builder.append(",\"enabled\":").append(rs.getBoolean("enabled")).append('}');
            }
            builder.append(']');
            return builder.toString();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list users", e);
        }
    }
}
