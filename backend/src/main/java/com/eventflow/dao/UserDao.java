package com.eventflow.dao;

import com.eventflow.model.User;
import com.eventflow.util.HikariCPDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public User createUser(User user) {
        String sql = "INSERT INTO public.users (full_name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole() != null ? user.getRole() : "attendee");
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create user", e);
        }
        return user;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM public.users WHERE email = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get user by email", e);
        }
        return null;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM public.users WHERE user_id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get user by ID", e);
        }
        return null;
    }
}
