package com.bookstore.app.service;

import com.bookstore.app.dao.UserDAO;
import com.bookstore.app.model.User;

import java.sql.Timestamp;
import java.util.List;

public class UserService {
    private static UserService instance;
    private UserDAO userDAO;
    
    // Current logged-in user
    private User currentUser = null;
    
    private UserService() {
        userDAO = new UserDAO();
    }
    
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
      // Authentication method
    public User authenticate(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        
        // Kiểm tra người dùng tồn tại, mật khẩu đúng và tài khoản đang hoạt động (không bị khóa)
        if (user != null && password.equals(user.getPassword()) && user.isActive()) {
            // Update last login time
            Timestamp now = new Timestamp(System.currentTimeMillis());
            userDAO.updateLastLogin(user.getId(), now);
            user.setLastLogin(now);
            
            // Set current user
            currentUser = user;
            return user;
        }
        
        return null;
    }
    
    // Get all users
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    // Get user by ID
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }
    
    // Add a new user
    public boolean addUser(User user) {
        // Basic validation
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
            return false;
        }
        
        // Check if username already exists
        User existingUser = userDAO.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }
        
        return userDAO.addUser(user);
    }
    
    // Update an existing user
    public boolean updateUser(User user) {
        if (user == null || user.getId() <= 0) {
            return false;
        }
        
        // Check if username is already taken by another user
        User existingUser = userDAO.getUserByUsername(user.getUsername());
        if (existingUser != null && existingUser.getId() != user.getId()) {
            return false;
        }
        
        return userDAO.updateUser(user);
    }
    
    // Delete a user
    public boolean deleteUser(int id) {
        // Don't allow deletion of current user
        if (currentUser != null && currentUser.getId() == id) {
            return false;
        }
        
        return userDAO.deleteUser(id);
    }
    
    // Get current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }    
    // Set current user to null (for logout)
    public void logout() {
        currentUser = null;
    }
    
    // Check if current user is admin
    public boolean isCurrentUserAdmin() {
        return currentUser != null && User.ROLE_ADMIN.equals(currentUser.getRole());
    }
    
    // Get UserDAO instance for advanced operations
    public UserDAO getUserDAO() {
        return userDAO;
    }
}
