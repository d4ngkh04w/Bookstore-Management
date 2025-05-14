package com.bookstore.app.view;

import com.bookstore.app.model.Invoice;
import com.bookstore.app.model.InvoiceItem;
import com.bookstore.app.service.BookService;
import com.bookstore.app.service.InvoiceService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ReportsView extends JFrame {
    private MainMenuView mainMenu;
    private InvoiceService invoiceService;
    private BookService bookService;

    private JTable dailyReportTable;
    private DefaultTableModel dailyReportTableModel;
    private JTable monthlyReportTable;
    private DefaultTableModel monthlyReportTableModel;
    private JTable topBooksTable;
    private DefaultTableModel topBooksTableModel;

    private JComboBox<Integer> yearComboBox;
    private JComboBox<String> monthComboBox;

    private DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

    public ReportsView(MainMenuView mainMenu) {
        this.mainMenu = mainMenu;
        this.invoiceService = InvoiceService.getInstance();
        this.bookService = BookService.getInstance();
        initComponents();
        loadReportData();
    }

    private void initComponents() {
        setTitle("Thống Kê Doanh Thu");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Date filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Lọc Theo Thời Gian",
            TitledBorder.LEFT, TitledBorder.TOP
        ));

        // Create year combo box with last 5 years
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[5];
        for (int i = 0; i < 5; i++) {
            years[i] = currentYear - i;
        }
        yearComboBox = new JComboBox<>(years);

        // Create month combo box
        String[] months = {
            "Tất cả các tháng", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
            "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        };
        monthComboBox = new JComboBox<>(months);

        JButton filterButton = new JButton("Lọc");
        filterButton.addActionListener(e -> loadReportData());

        filterPanel.add(new JLabel("Năm:"));
        filterPanel.add(yearComboBox);
        filterPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        filterPanel.add(new JLabel("Tháng:"));
        filterPanel.add(monthComboBox);
        filterPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        filterPanel.add(filterButton);

        // Back button
        JButton backButton = new JButton("Quay Lại Menu Chính");
        backButton.addActionListener(e -> returnToMainMenu());
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(backButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(backButtonPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Tabbed pane for reports
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Daily report panel
        JPanel dailyReportPanel = createDailyReportPanel();
        tabbedPane.addTab("Báo Cáo Theo Ngày", dailyReportPanel);
        
        // Monthly report panel
        JPanel monthlyReportPanel = createMonthlyReportPanel();
        tabbedPane.addTab("Báo Cáo Theo Tháng", monthlyReportPanel);
        
        // Top selling books panel
        JPanel topBooksPanel = createTopBooksPanel();
        tabbedPane.addTab("Sách Bán Chạy", topBooksPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
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

    private JPanel createDailyReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        String[] columns = {"Ngày", "Số Đơn Hàng", "Doanh Thu"};
        dailyReportTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        dailyReportTable = new JTable(dailyReportTableModel);
        
        // Center align for all columns except the first one (date)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < dailyReportTable.getColumnCount(); i++) {
            dailyReportTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(dailyReportTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Tổng doanh thu: 0 VNĐ");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(totalLabel);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createMonthlyReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        String[] columns = {"Tháng", "Số Đơn Hàng", "Doanh Thu"};
        monthlyReportTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        monthlyReportTable = new JTable(monthlyReportTableModel);
        
        // Center align for all columns except the first one (month)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < monthlyReportTable.getColumnCount(); i++) {
            monthlyReportTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(monthlyReportTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Chart panel (placeholder for future chart implementation)
        JPanel chartPanel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(0, 200));
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Biểu Đồ Doanh Thu",
            TitledBorder.LEFT, TitledBorder.TOP
        ));
        chartPanel.setLayout(new BorderLayout());
        JLabel chartLabel = new JLabel("Biểu đồ doanh thu sẽ được hiển thị ở đây", JLabel.CENTER);
        chartPanel.add(chartLabel, BorderLayout.CENTER);
        panel.add(chartPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createTopBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        String[] columns = {"STT", "Tên Sách", "Số Lượng Đã Bán", "Doanh Thu"};
        topBooksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        topBooksTable = new JTable(topBooksTableModel);
        
        // Center align for columns except the book title
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        topBooksTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT
        topBooksTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Quantity
        
        JScrollPane scrollPane = new JScrollPane(topBooksTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void loadReportData() {
        int selectedYear = (int) yearComboBox.getSelectedItem();
        int selectedMonth = monthComboBox.getSelectedIndex(); // 0 means all months
        
        // Load all invoices for the selected year
        List<Invoice> invoices = invoiceService.getAllInvoices();
        List<Invoice> filteredInvoices = new ArrayList<>();
        
        Calendar calendar = Calendar.getInstance();
        
        // Filter invoices by year and month
        for (Invoice invoice : invoices) {
            calendar.setTime(invoice.getDate());
            int invoiceYear = calendar.get(Calendar.YEAR);
            int invoiceMonth = calendar.get(Calendar.MONTH) + 1; // Calendar months start from 0
            
            if (invoiceYear == selectedYear && (selectedMonth == 0 || invoiceMonth == selectedMonth)) {
                filteredInvoices.add(invoice);
            }
        }
        
        loadDailyReport(filteredInvoices);
        loadMonthlyReport(selectedYear);
        loadTopBooks(filteredInvoices);
    }
    
    private void loadDailyReport(List<Invoice> invoices) {
        dailyReportTableModel.setRowCount(0);
        
        // Group invoices by date
        Map<String, List<Invoice>> invoicesByDate = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Invoice invoice : invoices) {
            String dateStr = dateFormat.format(invoice.getDate());
            if (!invoicesByDate.containsKey(dateStr)) {
                invoicesByDate.put(dateStr, new ArrayList<>());
            }
            invoicesByDate.get(dateStr).add(invoice);
        }
        
        // Sort dates
        List<String> dates = new ArrayList<>(invoicesByDate.keySet());
        Collections.sort(dates, (d1, d2) -> {
            try {
                return dateFormat.parse(d1).compareTo(dateFormat.parse(d2));
            } catch (Exception e) {
                return 0;
            }
        });
        
        // Calculate total revenue
        double totalRevenue = 0;
        
        // Add data to table model
        for (String date : dates) {
            List<Invoice> dateInvoices = invoicesByDate.get(date);
            int orderCount = dateInvoices.size();
            
            double revenue = 0;
            for (Invoice invoice : dateInvoices) {
                revenue += invoice.getTotalAmount();
            }
            
            totalRevenue += revenue;
            
            dailyReportTableModel.addRow(new Object[]{
                date,
                orderCount,
                currencyFormat.format(revenue)
            });
        }
        
        // Update total label if exists
        if (dailyReportTable.getParent().getParent() instanceof JPanel) {
            JPanel panel = (JPanel) dailyReportTable.getParent().getParent();
            Component[] components = panel.getComponents();
            for (Component c : components) {
                if (c instanceof JPanel && ((JPanel) c).getComponentCount() > 0) {
                    Component[] subComponents = ((JPanel) c).getComponents();
                    for (Component sc : subComponents) {
                        if (sc instanceof JLabel && ((JLabel) sc).getText().startsWith("Tổng doanh thu")) {
                            ((JLabel) sc).setText("Tổng doanh thu: " + currencyFormat.format(totalRevenue));
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void loadMonthlyReport(int year) {
        monthlyReportTableModel.setRowCount(0);
        
        List<Invoice> allInvoices = invoiceService.getAllInvoices();
        
        // Initialize monthly data
        int[] orderCounts = new int[12];
        double[] revenues = new double[12];
        
        // Group invoices by month
        Calendar calendar = Calendar.getInstance();
        for (Invoice invoice : allInvoices) {
            calendar.setTime(invoice.getDate());
            int invoiceYear = calendar.get(Calendar.YEAR);
            int invoiceMonth = calendar.get(Calendar.MONTH); // 0-11
            
            if (invoiceYear == year) {
                orderCounts[invoiceMonth]++;
                revenues[invoiceMonth] += invoice.getTotalAmount();
            }
        }
        
        // Add data to table model
        String[] monthNames = {
            "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
            "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        };
        
        for (int i = 0; i < 12; i++) {
            if (orderCounts[i] > 0) {
                monthlyReportTableModel.addRow(new Object[]{
                    monthNames[i],
                    orderCounts[i],
                    currencyFormat.format(revenues[i])
                });
            }
        }
    }
    
    private void loadTopBooks(List<Invoice> invoices) {
        topBooksTableModel.setRowCount(0);
        
        // Get all invoice items from the filtered invoices
        List<InvoiceItem> allItems = new ArrayList<>();
        for (Invoice invoice : invoices) {
            allItems.addAll(invoiceService.getInvoiceItemsByInvoiceId(invoice.getId()));
        }
        
        // Group items by book ID
        Map<Integer, Integer> bookQuantities = new HashMap<>();
        Map<Integer, Double> bookRevenues = new HashMap<>();
        
        for (InvoiceItem item : allItems) {
            int bookId = item.getBookId();
            int quantity = item.getQuantity();
            double revenue = item.getUnitPrice() * quantity;
            
            bookQuantities.put(bookId, bookQuantities.getOrDefault(bookId, 0) + quantity);
            bookRevenues.put(bookId, bookRevenues.getOrDefault(bookId, 0.0) + revenue);
        }
        
        // Sort books by quantity sold (descending)
        List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>(bookQuantities.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        // Add top books to table model (limit to top 10)
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : sortedEntries) {
            if (count >= 10) break;
            
            int bookId = entry.getKey();
            int quantity = entry.getValue();
            double revenue = bookRevenues.get(bookId);
            String bookTitle = bookService.getBookById(bookId).getTitle();
            
            topBooksTableModel.addRow(new Object[]{
                count + 1,
                bookTitle,
                quantity,
                currencyFormat.format(revenue)
            });
            
            count++;
        }
    }

    private void returnToMainMenu() {
        this.dispose();
        mainMenu.setVisible(true);
    }
}
