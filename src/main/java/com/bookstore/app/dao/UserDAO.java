package com.bookstore.app.dao;

import com.bookstore.app.model.User;
import com.bookstore.app.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String SELECT_USER_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    private static final String INSERT_USER = "INSERT INTO users (username, password, full_name, role, active) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET username = ?, password = ?, full_name = ?, role = ?, active = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_LAST_LOGIN = "UPDATE users SET last_login = ? WHERE id = ?";

    // Get all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS)
        ) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapResultSetToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    // Get user by id
    public User getUserById(int id) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)
        ) {
            preparedStatement.setInt(1, id);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get user by username
    public User getUserByUsername(String username) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME)
        ) {
            preparedStatement.setString(1, username);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add a new user
    public boolean addUser(User user) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFullName());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.setBoolean(5, user.isActive());
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update an existing user
    public boolean updateUser(User user) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER)
        ) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFullName());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.setBoolean(5, user.isActive());
            preparedStatement.setInt(6, user.getId());
            
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Delete a user
    public boolean deleteUser(int id) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)
        ) {
            preparedStatement.setInt(1, id);
            
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update last login time
    public boolean updateLastLogin(int userId, Timestamp loginTime) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LAST_LOGIN)
        ) {
            preparedStatement.setTimestamp(1, loginTime);
            preparedStatement.setInt(2, userId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Helper method to map ResultSet to User
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(
            resultSet.getInt("id"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            resultSet.getString("full_name"),
            resultSet.getString("role"),
            resultSet.getBoolean("active"),
            resultSet.getTimestamp("created_at"),
            resultSet.getTimestamp("last_login")
        );
    }
}
