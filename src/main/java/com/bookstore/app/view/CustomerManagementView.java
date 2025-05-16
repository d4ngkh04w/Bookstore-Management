package com.bookstore.app.view;

import com.bookstore.app.controller.CustomerController;
import com.bookstore.app.model.Customer;
import com.bookstore.app.service.CustomerService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class CustomerManagementView extends JFrame {
    private final MainMenuView mainMenu;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;
    private final CustomerController customerController;
    public CustomerManagementView(MainMenuView mainMenu) {
        this.mainMenu = mainMenu;
        this.customerController = new CustomerController(CustomerService.getInstance());
        initComponents();
        loadCustomerData();
    }

    private void initComponents() {
        setTitle("Quản Lý Khách Hàng");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Tìm Kiếm", 
            TitledBorder.LEFT, TitledBorder.TOP
        ));

        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchTypeComboBox = new JComboBox<>(new String[]{"Tên Khách Hàng", "Số Điện Thoại"});
        JButton searchButton = new JButton("Tìm Kiếm");

        searchControlsPanel.add(new JLabel("Tìm theo:"));
        searchControlsPanel.add(searchTypeComboBox);
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(searchButton);

        searchPanel.add(searchControlsPanel, BorderLayout.CENTER);

        // Create Back button in the search panel
        JButton backButton = new JButton("Quay Lại Menu Chính");
        backButton.addActionListener(_ -> returnToMainMenu());
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(backButton);
        searchPanel.add(backButtonPanel, BorderLayout.EAST);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Tên Khách Hàng", "Số Điện Thoại"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.setAutoCreateRowSorter(true);

        customerTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Phone

        JScrollPane scrollPane = new JScrollPane(customerTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Thêm Khách Hàng");
        JButton editButton = new JButton("Sửa Thông Tin");
        JButton deleteButton = new JButton("Xóa Khách Hàng");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(_ -> showAddCustomerDialog());
        editButton.addActionListener(_ -> showEditCustomerDialog());
        deleteButton.addActionListener(_ -> deleteSelectedCustomer());

        searchButton.addActionListener(_ -> searchCustomers());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchCustomers();
                }
            }
        });

        add(mainPanel);

        // Set app icon
        try {
            Image appIcon = new ImageIcon(
                getClass().getClassLoader().getResource("icons/logo.png")
            ).getImage();
            setIconImage(appIcon);
        } catch (Exception e) {
            System.out.println("App icon not found");
        }
    }

    public void loadCustomerData() {

        tableModel.setRowCount(0);

        List<Customer> customers = customerController.getAllCustomers();
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getPhone()
            });
        }
    }
    
    private void searchCustomers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadCustomerData();
            customerTable.setRowSorter(null);
            return;
        }
        
        int searchType = searchTypeComboBox.getSelectedIndex();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(sorter);

        int columnToSearch = searchType + 1;
        
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm, columnToSearch));
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog(this, "Thêm Khách Hàng Mới", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();

        formPanel.add(new JLabel("Tên Khách Hàng:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Số Điện Thoại:"));
        formPanel.add(phoneField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(_ -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }                Customer newCustomer = new Customer();
            newCustomer.setName(name);
            newCustomer.setPhone(phone);

            customerController.addCustomer(newCustomer);
            dialog.dispose();
            loadCustomerData();
        });

        cancelButton.addActionListener(_ -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void showEditCustomerDialog() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = customerTable.getRowSorter() != null 
            ? customerTable.getRowSorter().convertRowIndexToModel(selectedRow) 
            : selectedRow;

        int customerId = (int) tableModel.getValueAt(modelRow, 0);
        Customer customer = customerController.getCustomerById(customerId);

        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Sửa Thông Tin Khách Hàng", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField(customer.getName());
        JTextField phoneField = new JTextField(customer.getPhone());

        formPanel.add(new JLabel("Tên Khách Hàng:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Số Điện Thoại:"));
        formPanel.add(phoneField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(_ -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            customer.setName(name);
            customer.setPhone(phone);
            customerController.updateCustomer(customer);
            dialog.dispose();
            loadCustomerData();
        });

        cancelButton.addActionListener(_ -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void deleteSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = customerTable.getRowSorter() != null 
            ? customerTable.getRowSorter().convertRowIndexToModel(selectedRow) 
            : selectedRow;

        int customerId = (int) tableModel.getValueAt(modelRow, 0);
        String customerName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa khách hàng '" + customerName + "'?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            customerController.deleteCustomer(customerId);
            loadCustomerData();
        }
    }

    private void returnToMainMenu() {
        this.dispose();
        mainMenu.setVisible(true);
    }
}
