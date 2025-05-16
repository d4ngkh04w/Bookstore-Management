package com.bookstore.app.view;

import com.bookstore.app.model.User;
import com.bookstore.app.service.UserService;
import com.bookstore.app.controller.UserController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class UserManagementView extends JFrame {
    private final MainMenuView mainMenuView;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton editButton;
    private JButton deleteButton;
    private JTextField searchField;
    private final UserController userController;
    
    // Người dùng được chọn để sửa/xóa
    private User selectedUser;
    
    private final String[] columnNames = {"ID", "Tên đăng nhập", "Họ tên", "Vai trò", "Trạng thái", "Ngày tạo", "Đăng nhập lần cuối"};

    public UserManagementView(MainMenuView mainMenuView) {
        this.mainMenuView = mainMenuView;
        this.userController = new UserController(UserService.getInstance());
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
        JButton searchButton = new JButton("Tìm kiếm");
        
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Thêm");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");
        JButton backButton = new JButton("Quay lại");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = (int)userTable.getValueAt(selectedRow, 0);
                    selectedUser = userController.getUserById(userId);
                } else {
                    selectedUser = null;
                }
                
                updateButtonState();
            }
        });
        
        addButton.addActionListener(_ -> showUserDialog(null));
        
        editButton.addActionListener(_ -> {
            if (selectedUser != null) {
                showUserDialog(selectedUser);
            }
        });
        
        deleteButton.addActionListener(_ -> deleteUser());
        
        backButton.addActionListener(_ -> goBack());
        
        searchButton.addActionListener(_ -> searchUsers());
        
        // Khởi tạo nút trạng thái
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
        boolean userSelected = selectedUser != null;
        boolean isSameUser = userSelected && selectedUser.getId() == userController.getCurrentUser().getId();
        
        editButton.setEnabled(userSelected);
        deleteButton.setEnabled(userSelected && !isSameUser);
    }
    
    private void loadUserData() {
        tableModel.setRowCount(0);
        
        List<User> users = userController.getAllUsers();
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
        
        tableModel.setRowCount(0);
        
        List<User> users = userController.getAllUsers();
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
        JDialog dialog = new JDialog(this, user == null ? "Thêm Nhân Viên" : "Sửa Thông Tin Nhân Viên", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        if (user != null) {
            usernameField.setText(user.getUsername());
        }
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Họ và tên:"), gbc);
        
        gbc.gridx = 1;
        JTextField fullNameField = new JTextField(20);
        if (user != null) {
            fullNameField.setText(user.getFullName());
        }
        formPanel.add(fullNameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        
        gbc.gridx = 1;
        String[] roles = {"Admin", "Nhân viên"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        if (user != null) {
            roleComboBox.setSelectedItem(user.getRole().equals(User.ROLE_ADMIN) ? "Admin" : "Nhân viên");
        } else {
            roleComboBox.setSelectedIndex(1);
        }
        formPanel.add(roleComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox activeCheckBox = new JCheckBox("Hoạt động");
        activeCheckBox.setSelected(user == null || user.isActive());
        formPanel.add(activeCheckBox, gbc);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
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
                
                // Tạo hoặc cập nhật người dùng
                if (user == null) {
                    // Tạo người dùng mới
                    User newUser = new User(username, password, fullName, role, active);
                    if (userController.addUser(newUser)) {
                        JOptionPane.showMessageDialog(dialog, "Thêm nhân viên thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadUserData();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Tên đăng nhập đã tồn tại hoặc có lỗi khác xảy ra", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Cập nhật người dùng hiện tại
                    user.setUsername(username);
                    if (!password.isEmpty()) {
                        user.setPassword(password);
                    }
                    user.setFullName(fullName);
                    user.setRole(role);
                    user.setActive(active);
                    
                    if (userController.updateUser(user)) {
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
        
        dialog.setVisible(true);
    }
    
    private void deleteUser() {
        if (selectedUser == null) {
            return;
        }
        
        if (selectedUser.getId() == userController.getCurrentUser().getId()) {
            JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản đang đăng nhập", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa người dùng " + selectedUser.getUsername() + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userController.deleteUser(selectedUser.getId())) {
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
