package com.bookstore.app.service;

import com.bookstore.app.dao.UserDAO;
import com.bookstore.app.model.User;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

public class UserService {
    private static UserService instance;
    private final UserDAO userDAO;

    @Getter
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

    public User authenticate(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        
        // Kiểm tra người dùng tồn tại, mật khẩu đúng và tài khoản đang hoạt động (không bị khóa)
        if (user != null && password.equals(user.getPassword()) && user.isActive()) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            userDAO.updateLastLogin(user.getId(), now);
            user.setLastLogin(now);

            currentUser = user;
            return user;
        }
        
        return null;
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
            return false;
        }

        User existingUser = userDAO.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }
        
        return userDAO.addUser(user);
    }

    public boolean updateUser(User user) {
        if (user == null || user.getId() <= 0) {
            return false;
        }

        User existingUser = userDAO.getUserByUsername(user.getUsername());
        if (existingUser != null && existingUser.getId() != user.getId()) {
            return false;
        }
        
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int id) {
        if (currentUser != null && currentUser.getId() == id) {
            return false;
        }
        
        return userDAO.deleteUser(id);
    }

    public void logout() {
        currentUser = null;
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        if (user == null) {
            return false;
        }

        if (!currentPassword.equals(user.getPassword())) {
            return false;
        }

        user.setPassword(newPassword);
        return userDAO.updateUser(user);
    }

    public boolean isUsernameExists(String username) {
        User user = userDAO.getUserByUsername(username);
        return user != null;
    }
}
