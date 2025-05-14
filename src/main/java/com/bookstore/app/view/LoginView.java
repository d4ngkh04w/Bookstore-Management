package com.bookstore.app.view;

import com.bookstore.app.model.User;
import com.bookstore.app.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginView() {
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
        loginButton = new JButton("Đăng Nhập");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Status label for error messages
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);

        // Set the Enter key to trigger login
        getRootPane().setDefaultButton(loginButton);

        add(mainPanel);
    }    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        UserService userService = UserService.getInstance();
        
        // Kiểm tra xem tài khoản có bị khóa không
        User userCheck = userService.getUserDAO().getUserByUsername(username);
        if (userCheck != null && !userCheck.isActive() && password.equals(userCheck.getPassword())) {
            statusLabel.setText("Tài khoản đã bị khóa!");
            statusLabel.setForeground(Color.RED);
            passwordField.setText("");
            return;
        }
        
        User user = userService.authenticate(username, password);
        
        if (user != null) {
            statusLabel.setText("Đăng nhập thành công!");
            statusLabel.setForeground(new Color(0, 128, 0)); // Green color
            
            // Open main menu with the logged in user
            SwingUtilities.invokeLater(() -> {
                MainMenuView mainMenu = new MainMenuView();
                mainMenu.setVisible(true);
                this.dispose(); // Close login window
            });
        } else {
            statusLabel.setText("Sai tên đăng nhập hoặc mật khẩu!");
            statusLabel.setForeground(Color.RED);
            passwordField.setText("");
        }
    }
}
