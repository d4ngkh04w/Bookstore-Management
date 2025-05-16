package com.bookstore.app.view;

import com.bookstore.app.controller.BookController;
import com.bookstore.app.controller.CustomerController;
import com.bookstore.app.controller.InvoiceController;
import com.bookstore.app.model.Book;
import com.bookstore.app.model.Customer;
import com.bookstore.app.model.Invoice;
import com.bookstore.app.model.InvoiceItem;
import com.bookstore.app.service.BookService;
import com.bookstore.app.service.CustomerService;
import com.bookstore.app.service.InvoiceService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderManagementView extends JFrame {
    private final MainMenuView mainMenuView;
    private final BookController bookController;
    private final CustomerController customerController;
    private final InvoiceController invoiceController;

    private JTable invoiceTable;
    private DefaultTableModel invoiceTableModel;
    private DefaultTableModel invoiceItemTableModel;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;

    private JComboBox<Customer> customerComboBox;
    private JComboBox<Book> bookComboBox;
    private JSpinner quantitySpinner;
    private JLabel totalLabel;

    private final List<InvoiceItem> cartItems = new ArrayList<>();

    public OrderManagementView(MainMenuView mainMenuView) {
        this.mainMenuView = mainMenuView;
        this.bookController = new BookController(BookService.getInstance());
        this.customerController = new CustomerController(CustomerService.getInstance());
        this.invoiceController = new InvoiceController(InvoiceService.getInstance());
        initComponents();
        loadInvoiceData();
    }

    private void initComponents() {
        setTitle("Quản Lý Đơn Hàng");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Lịch Sử Đơn Hàng", createInvoiceHistoryPanel());

        tabbedPane.addTab("Tạo Đơn Hàng Mới", createNewOrderPanel());

        add(tabbedPane);

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

    private JPanel createInvoiceHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] invoiceColumns = {"ID", "Khách Hàng", "Ngày", "Tổng Tiền"};
        invoiceTableModel = new DefaultTableModel(invoiceColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        invoiceTable = new JTable(invoiceTableModel);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = invoiceTable.getSelectedRow();
                if (selectedRow != -1) {
                    int invoiceId = (int) invoiceTableModel.getValueAt(selectedRow, 0);
                    loadInvoiceItems(invoiceId);
                }
            }
        });

        String[] itemColumns = {"Sách", "Đơn Giá", "Số Lượng", "Thành Tiền"};
        invoiceItemTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable invoiceItemTable = new JTable(invoiceItemTableModel);
        invoiceItemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(invoiceTable),
            new JScrollPane(invoiceItemTable)
        );
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        panel.add(splitPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Làm Mới");
        JButton backButton = new JButton("Quay Lại Menu Chính");

        refreshButton.addActionListener(e -> loadInvoiceData());
        backButton.addActionListener(e -> returnToMainMenu());

        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createNewOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Customer selection panel
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Thông Tin Khách Hàng",
            TitledBorder.LEFT, TitledBorder.TOP
        ));

        customerComboBox = new JComboBox<>();
        JButton refreshCustomerButton = new JButton("Làm Mới");
        customerPanel.add(new JLabel("Khách hàng:"));
        customerPanel.add(customerComboBox);
        customerPanel.add(refreshCustomerButton);
        refreshCustomerButton.addActionListener(e -> loadCustomers());

        panel.add(customerPanel, BorderLayout.NORTH);

        // Book selection and cart panel
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Book selection
        JPanel bookPanel = new JPanel(new BorderLayout(5, 5));
        bookPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Thêm Sách Vào Đơn",
            TitledBorder.LEFT, TitledBorder.TOP
        ));

        JPanel bookSelectionPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        bookSelectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        bookComboBox = new JComboBox<>();
        JButton refreshBookButton = new JButton("Làm Mới");
        refreshBookButton.addActionListener(e -> loadBooks());

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton addToCartButton = new JButton("Thêm Vào Giỏ");
        addToCartButton.addActionListener(e -> addToCart());

        bookSelectionPanel.add(new JLabel("Chọn Sách:"));
        bookSelectionPanel.add(bookComboBox);
        bookSelectionPanel.add(new JLabel("Số Lượng:"));
        bookSelectionPanel.add(quantitySpinner);
        bookSelectionPanel.add(refreshBookButton);
        bookSelectionPanel.add(addToCartButton);

        bookPanel.add(bookSelectionPanel, BorderLayout.NORTH);
        centerPanel.add(bookPanel);

        // Cart panel
        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Giỏ Hàng",
            TitledBorder.LEFT, TitledBorder.TOP
        ));

        // Cart table
        String[] cartColumns = {"Sách", "Đơn Giá", "Số Lượng", "Thành Tiền"};
        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        JPanel cartButtonPanel = new JPanel(new BorderLayout());
        JPanel leftCartButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removeButton = new JButton("Xóa Khỏi Giỏ");
        removeButton.addActionListener(e -> removeFromCart());
        leftCartButtons.add(removeButton);

        JPanel rightCartSummary = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Tổng tiền: 0 VNĐ");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rightCartSummary.add(totalLabel);

        cartButtonPanel.add(leftCartButtons, BorderLayout.WEST);
        cartButtonPanel.add(rightCartSummary, BorderLayout.EAST);
        cartPanel.add(cartButtonPanel, BorderLayout.SOUTH);

        centerPanel.add(cartPanel);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createOrderButton = new JButton("Tạo Đơn Hàng");
        createOrderButton.addActionListener(e -> createOrder());
        JButton clearCartButton = new JButton("Xóa Giỏ Hàng");
        clearCartButton.addActionListener(e -> clearCart());
        JButton backButton = new JButton("Quay Lại Menu Chính");
        backButton.addActionListener(e -> returnToMainMenu());

        buttonPanel.add(clearCartButton);
        buttonPanel.add(createOrderButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadCustomers();
        loadBooks();

        return panel;
    }

    private void loadInvoiceData() {
        invoiceTableModel.setRowCount(0);
        invoiceItemTableModel.setRowCount(0);
        
        List<Invoice> invoices = invoiceController.getAllInvoices();
        DecimalFormat df = new DecimalFormat("#,### VNĐ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Invoice invoice : invoices) {
            Customer customer = customerController.getCustomerById(invoice.getCustomerId());
            String customerName = customer != null ? customer.getName() : "Unknown";
            
            invoiceTableModel.addRow(new Object[]{
                invoice.getId(),
                customerName,
                dateFormat.format(invoice.getDate()),
                df.format(invoice.getTotalAmount())
            });
        }
    }
    
    private void loadInvoiceItems(int invoiceId) {
        invoiceItemTableModel.setRowCount(0);
        
        List<InvoiceItem> items = invoiceController.getInvoiceItemsByInvoiceId(invoiceId);
        DecimalFormat df = new DecimalFormat("#,### VNĐ");
        
        for (InvoiceItem item : items) {
            Book book = bookController.getBookById(item.getBookId());
            String bookTitle = book != null ? book.getTitle() : "Unknown";
            
            invoiceItemTableModel.addRow(new Object[]{
                bookTitle,
                df.format(item.getUnitPrice()),
                item.getQuantity(),
                df.format(item.getUnitPrice() * item.getQuantity())
            });
        }
    }
    
    private void loadCustomers() {
        DefaultComboBoxModel<Customer> customerModel = new DefaultComboBoxModel<>();
        List<Customer> customers = customerController.getAllCustomers();
        
        for (Customer customer : customers) {
            customerModel.addElement(customer);
        }
        
        customerComboBox.setModel(customerModel);
    }
    
    private void loadBooks() {
        DefaultComboBoxModel<Book> bookModel = new DefaultComboBoxModel<>();
        List<Book> books = bookController.getAllBooks();
        
        for (Book book : books) {
            if (book.getQuantity() > 0) {
                bookModel.addElement(book);
            }
        }
        
        bookComboBox.setModel(bookModel);
    }
    
    private void addToCart() {
        Book selectedBook = (Book) bookComboBox.getSelectedItem();
        if (selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int quantity = (int) quantitySpinner.getValue();
        if (quantity <= 0 || quantity > selectedBook.getQuantity()) {
            JOptionPane.showMessageDialog(
                this, 
                "Số lượng không hợp lệ! Số lượng còn lại: " + selectedBook.getQuantity(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        for (InvoiceItem item : cartItems) {
            if (item.getBookId() == selectedBook.getId()) {
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity <= selectedBook.getQuantity()) {
                    item.setQuantity(newQuantity);
                    updateCartTable();
                } else {
                    JOptionPane.showMessageDialog(
                        this, 
                        "Không đủ số lượng! Số lượng còn lại: " + selectedBook.getQuantity(), 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
                return;
            }
        }

        InvoiceItem item = new InvoiceItem();
        item.setBookId(selectedBook.getId());
        item.setQuantity(quantity);
        item.setUnitPrice(selectedBook.getPrice());
        
        cartItems.add(item);
        updateCartTable();
    }
    
    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1) {
            cartItems.remove(selectedRow);
            updateCartTable();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearCart() {
        cartItems.clear();
        updateCartTable();
    }
      private void updateCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0;
        DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
        
        for (InvoiceItem item : cartItems) {
            Book book = bookController.getBookById(item.getBookId());
            String bookTitle = book != null ? book.getTitle() : "Unknown";
            double subtotal = item.getUnitPrice() * item.getQuantity();
            
            cartTableModel.addRow(new Object[]{
                bookTitle,
                df.format(item.getUnitPrice()),
                item.getQuantity(),
                df.format(subtotal)
            });
            
            total += subtotal;
        }
        
        totalLabel.setText("Tổng tiền: " + df.format(total));
    }
    
    private void createOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Invoice invoice = new Invoice();
        invoice.setCustomerId(selectedCustomer.getId());
        invoice.setDate(new Date());
        
        double totalAmount = 0;
        for (InvoiceItem item : cartItems) {
            totalAmount += item.getUnitPrice() * item.getQuantity();
        }
        invoice.setTotalAmount(totalAmount);

        int invoiceId = invoiceController.createInvoice(invoice, cartItems);
        
        if (invoiceId > 0) {
            for (InvoiceItem item : cartItems) {
                Book book = bookController.getBookById(item.getBookId());
                book.setQuantity(book.getQuantity() - item.getQuantity());
                bookController.updateBook(book);
            }
            
            JOptionPane.showMessageDialog(this, "Đơn hàng đã được tạo thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            clearCart();
            loadInvoiceData();
            loadBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void returnToMainMenu() {
        this.dispose();
        mainMenuView.setVisible(true);
    }
}
