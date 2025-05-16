package com.bookstore.app.view;

import com.bookstore.app.controller.UserController;
import com.bookstore.app.model.User;
import com.bookstore.app.service.UserService;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private final UserController userController;

    public LoginView() {
        userController = new UserController(UserService.getInstance());
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ Thống Quản Lý Cửa Hàng Sách");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login panel
        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        loginPanel.setMaximumSize(new Dimension(300, 60));
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameField = new JTextField(15);
        
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordField = new JPasswordField(15);

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        mainPanel.add(loginPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login button
        JButton loginButton = new JButton("Đăng Nhập");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(_ -> attemptLogin());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);

        getRootPane().setDefaultButton(loginButton);

        add(mainPanel);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = userController.login(username, password);
        
        if (user != null) {
            statusLabel.setText("Đăng nhập thành công!");
            statusLabel.setForeground(new Color(0, 128, 0));

            SwingUtilities.invokeLater(() -> {
                MainMenuView mainMenu = new MainMenuView();
                mainMenu.setVisible(true);
                this.dispose();
            });
        } else {
            statusLabel.setText("Sai tên đăng nhập hoặc mật khẩu!");
            statusLabel.setForeground(Color.RED);
            passwordField.setText("");
        }
    }
}
