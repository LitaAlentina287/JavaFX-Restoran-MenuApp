package com.mycompany.javafx_dashboard_lita;

import java.sql.*;

public class UserOperations {
    private Connection connection;

    public UserOperations() throws SQLException {
        connection = DatabaseConnection.getConnection();
        ensureDefaultAdmin(); // Tambahkan pengecekan admin default saat objek dibuat
    }

    // Menambahkan akun Admin default jika belum ada
    private void ensureDefaultAdmin() {
        String defaultAdminUsername = "admin";
        String defaultAdminPassword = "admin123";
        String defaultAdminRole = "Admin";

        try {
            String checkSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, defaultAdminUsername);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                String insertSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                insertStmt.setString(1, defaultAdminUsername);
                insertStmt.setString(2, defaultAdminPassword);
                insertStmt.setString(3, defaultAdminRole);
                insertStmt.executeUpdate();
                System.out.println("Admin default berhasil dibuat (admin / admin123).");
            } else {
                System.out.println("Admin default sudah tersedia.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal memastikan admin default: " + e.getMessage());
        }
    }

    // Register User
    public boolean registerUser(String username, String password, String role) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
            System.out.println("User registered successfully!");
            return true;
        } catch (SQLException e) {
            System.out.println("Register failed: " + e.getMessage());
            return false;
        }
    }

    // Login User
    public boolean loginUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Login successful!");
                    return true;
                } else {
                    System.out.println("Invalid username or password.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Logout User
    public void logoutUser() {
        System.out.println("User logged out successfully!");
    }

    // Get User Profile
    public User getProfile(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update Password
    public void updatePassword(String username, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password updated successfully!");
            } else {
                System.out.println("Failed to update password. User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
