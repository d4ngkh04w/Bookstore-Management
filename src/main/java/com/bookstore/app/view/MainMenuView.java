package com.bookstore.app.view;

import com.bookstore.app.model.User;
import com.bookstore.app.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuView extends JFrame {
    private JButton bookManagementBtn;
    private JButton customerManagementBtn;
    private JButton orderManagementBtn;
    private JButton reportBtn;
    private JButton userManagementBtn; // New button for admin
    private JButton logoutBtn;
    
    private User currentUser;    public MainMenuView() {
        this.currentUser = UserService.getInstance().getCurrentUser();
        initComponents();
    }

    private void initComponents() {
        setTitle("Quản Lý Cửa Hàng Sách");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ CỬA HÀNG SÁCH", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
          JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        String roleText = currentUser != null && currentUser.isAdmin() ? "Admin" : "Nhân viên";
        JLabel userLabel = new JLabel("Xin chào, " + (currentUser != null ? currentUser.getFullName() + " (" + roleText + ")" : ""));
        userLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        userPanel.add(userLabel);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Menu panel (center)
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);        // Create buttons with icons
        bookManagementBtn = createMenuButton("Quản Lý Sách", "icons/book.png");
        customerManagementBtn = createMenuButton("Quản Lý Khách Hàng", "icons/customer.png");
        orderManagementBtn = createMenuButton("Quản Lý Đơn Hàng", "icons/order.png");
        reportBtn = createMenuButton("Thống Kê Doanh Thu", "icons/report.png");
        userManagementBtn = createMenuButton("Quản Lý Nhân Viên", "icons/user.png"); // New button
        logoutBtn = createMenuButton("Đăng Xuất", "icons/logout.png");

        // Add action listeners
        bookManagementBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openBookManagement();
            }
        });

        customerManagementBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCustomerManagement();
            }
        });

        orderManagementBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openOrderManagement();
            }
        });

        reportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openReports();
            }
        });

        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }        });
        
        // User management button action
        userManagementBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserManagement();
            }
        });        // Add buttons to menu panel
        menuPanel.add(bookManagementBtn, gbc);
        menuPanel.add(customerManagementBtn, gbc);
        menuPanel.add(orderManagementBtn, gbc);
        
        // Chỉ hiển thị nút thống kê doanh thu và quản lý người dùng cho admin
        if (currentUser != null && currentUser.isAdmin()) {
            menuPanel.add(reportBtn, gbc);
            menuPanel.add(userManagementBtn, gbc);
        }
        
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)), gbc);
        menuPanel.add(logoutBtn, gbc);

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel footerLabel = new JLabel("© 2025 Hệ Thống Quản Lý Cửa Hàng Sách");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

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

    private JButton createMenuButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(300, 60));
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setIconTextGap(20);
        } catch (Exception e) {
            // If icon not found, just show text
        }
        
        return button;
    }

    private void openBookManagement() {
        SwingUtilities.invokeLater(() -> {
            BookManagementView bookView = new BookManagementView(this);
            bookView.setVisible(true);
            this.setVisible(false);
        });
    }

    private void openCustomerManagement() {
        SwingUtilities.invokeLater(() -> {
            CustomerManagementView customerView = new CustomerManagementView(this);
            customerView.setVisible(true);
            this.setVisible(false);
        });
    }

    private void openOrderManagement() {
        SwingUtilities.invokeLater(() -> {
            OrderManagementView orderView = new OrderManagementView(this);
            orderView.setVisible(true);
            this.setVisible(false);
        });
    }    private void openReports() {
        // Chỉ admin mới được phép xem thống kê doanh thu
        if (currentUser != null && currentUser.isAdmin()) {
            SwingUtilities.invokeLater(() -> {
                ReportsView reportsView = new ReportsView(this);
                reportsView.setVisible(true);
                this.setVisible(false);
            });
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Bạn không có quyền truy cập vào chức năng này!",
                "Từ chối truy cập",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void openUserManagement() {
        SwingUtilities.invokeLater(() -> {
            UserManagementView userView = new UserManagementView(this);
            userView.setVisible(true);
            this.setVisible(false);
        });
    }    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc chắn muốn đăng xuất?", 
            "Xác nhận đăng xuất", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Log out in the service
            UserService.getInstance().logout();
            
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                this.dispose();
            });
        }
    }
}
