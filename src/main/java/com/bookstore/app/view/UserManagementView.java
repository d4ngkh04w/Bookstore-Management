package com.bookstore.app.view;

import com.bookstore.app.model.User;
import com.bookstore.app.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class UserManagementView extends JFrame {
    private MainMenuView mainMenuView;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, backButton;
    private JTextField searchField;
    private JButton searchButton;
    
    // Selected user for editing/deleting
    private User selectedUser;
    
    // Column names for the table
    private final String[] columnNames = {"ID", "Tên đăng nhập", "Họ tên", "Vai trò", "Trạng thái", "Ngày tạo", "Đăng nhập lần cuối"};

    public UserManagementView(MainMenuView mainMenuView) {
        this.mainMenuView = mainMenuView;
        initComponents();
        loadUserData();
    }

    private void initComponents() {
        setTitle("Quản Lý Nhân Viên");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Tìm kiếm");
        
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Thêm");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");
        backButton = new JButton("Quay lại");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        
        // Add components to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Event listeners
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = (int)userTable.getValueAt(selectedRow, 0);
                    selectedUser = UserService.getInstance().getUserById(userId);
                } else {
                    selectedUser = null;
                }
                
                updateButtonState();
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserDialog(null); // null means adding a new user
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedUser != null) {
                    showUserDialog(selectedUser);
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchUsers();
            }
        });
        
        // Initial button state
        updateButtonState();
        
        // Set app icon
        try {
            Image appIcon = new ImageIcon(
                getClass().getClassLoader().getResource("icons/logo.png")
            ).getImage();
            setIconImage(appIcon);
        } catch (Exception e) {
            System.out.println("App icon not found");
        }
        
        add(mainPanel);
    }
    
    private void updateButtonState() {
        // Only enable edit/delete if a user is selected and it's not the current admin
        boolean userSelected = selectedUser != null;
        boolean isSameUser = userSelected && selectedUser.getId() == UserService.getInstance().getCurrentUser().getId();
        
        editButton.setEnabled(userSelected);
        deleteButton.setEnabled(userSelected && !isSameUser); // Can't delete current user
    }
    
    private void loadUserData() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load user data
        List<User> users = UserService.getInstance().getAllUsers();
        for (User user : users) {
            tableModel.addRow(new Object[] {
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().equals(User.ROLE_ADMIN) ? "Admin" : "Nhân viên",
                user.isActive() ? "Hoạt động" : "Khóa",
                user.getCreatedAt(),
                user.getLastLogin()
            });
        }
    }
    
    private void searchUsers() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadUserData();
            return;
        }
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Search users and display results
        List<User> users = UserService.getInstance().getAllUsers();
        for (User user : users) {
            if (user.getUsername().toLowerCase().contains(keyword) ||
                user.getFullName().toLowerCase().contains(keyword) ||
                user.getRole().toLowerCase().contains(keyword)) {
                
                tableModel.addRow(new Object[] {
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole().equals(User.ROLE_ADMIN) ? "Admin" : "Nhân viên",
                    user.isActive() ? "Hoạt động" : "Khóa",
                    user.getCreatedAt(),
                    user.getLastLogin()
                });
            }
        }
    }
    
    private void showUserDialog(User user) {
        // Create a dialog to add/edit user
        JDialog dialog = new JDialog(this, user == null ? "Thêm Nhân Viên" : "Sửa Thông Tin Nhân Viên", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        if (user != null) {
            usernameField.setText(user.getUsername());
        }
        formPanel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Full name field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Họ và tên:"), gbc);
        
        gbc.gridx = 1;
        JTextField fullNameField = new JTextField(20);
        if (user != null) {
            fullNameField.setText(user.getFullName());
        }
        formPanel.add(fullNameField, gbc);
        
        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        
        gbc.gridx = 1;
        String[] roles = {"Admin", "Nhân viên"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        if (user != null) {
            roleComboBox.setSelectedItem(user.getRole().equals(User.ROLE_ADMIN) ? "Admin" : "Nhân viên");
        } else {
            roleComboBox.setSelectedIndex(1); // Default to staff
        }
        formPanel.add(roleComboBox, gbc);
        
        // Active status
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox activeCheckBox = new JCheckBox("Hoạt động");
        activeCheckBox.setSelected(user == null || user.isActive());
        formPanel.add(activeCheckBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Event listeners
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate inputs
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String fullName = fullNameField.getText().trim();
                String role = roleComboBox.getSelectedItem().equals("Admin") ? User.ROLE_ADMIN : User.ROLE_STAFF;
                boolean active = activeCheckBox.isSelected();
                
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Tên đăng nhập không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (user == null && password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Mật khẩu không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Họ và tên không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create or update user
                if (user == null) {
                    // Create new user
                    User newUser = new User(username, password, fullName, role, active);
                    if (UserService.getInstance().addUser(newUser)) {
                        JOptionPane.showMessageDialog(dialog, "Thêm nhân viên thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadUserData();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Tên đăng nhập đã tồn tại hoặc có lỗi khác xảy ra", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Update existing user
                    user.setUsername(username);
                    // Only update password if a new one is provided
                    if (!password.isEmpty()) {
                        user.setPassword(password);
                    }
                    user.setFullName(fullName);
                    user.setRole(role);
                    user.setActive(active);
                    
                    if (UserService.getInstance().updateUser(user)) {
                        JOptionPane.showMessageDialog(dialog, "Cập nhật thông tin thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadUserData();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Tên đăng nhập đã tồn tại hoặc có lỗi khác xảy ra", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        // Show dialog
        dialog.setVisible(true);
    }
    
    private void deleteUser() {
        if (selectedUser == null) {
            return;
        }
        
        // Check if it's the current user
        if (selectedUser.getId() == UserService.getInstance().getCurrentUser().getId()) {
            JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản đang đăng nhập", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa người dùng " + selectedUser.getUsername() + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (UserService.getInstance().deleteUser(selectedUser.getId())) {
                JOptionPane.showMessageDialog(this, "Xóa người dùng thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                selectedUser = null;
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa người dùng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void goBack() {
        mainMenuView.setVisible(true);
        this.dispose();
    }
}
