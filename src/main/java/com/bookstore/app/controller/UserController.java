package com.bookstore.app.controller;

import com.bookstore.app.model.User;
import com.bookstore.app.service.UserService;

import java.util.List;

public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User login(String username, String password) {
        return userService.authenticate(username, password);
    }
    
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }
    
    public void logout() {
        userService.logout();
    }
    
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    public User getUserById(int id) {
        return userService.getUserById(id);
    }
    
    public boolean addUser(User user) {
        return userService.addUser(user);
    }
    
    public boolean updateUser(User user) {
        return userService.updateUser(user);
    }

    public boolean deleteUser(int id) {
        return userService.deleteUser(id);
    }
}
