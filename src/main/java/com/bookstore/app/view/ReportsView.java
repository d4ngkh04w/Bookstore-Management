package com.bookstore.app.view;

import com.bookstore.app.controller.BookController;
import com.bookstore.app.controller.InvoiceController;
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
    private final MainMenuView mainMenu;
    private final InvoiceController invoiceController;
    private final BookController bookController;

    private JTable dailyReportTable;
    private DefaultTableModel dailyReportTableModel;
    private DefaultTableModel monthlyReportTableModel;
    private DefaultTableModel topBooksTableModel;

    private JComboBox<Integer> yearComboBox;
    private JComboBox<String> monthComboBox;

    private final DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

    public ReportsView(MainMenuView mainMenu) {
        this.mainMenu = mainMenu;
        this.invoiceController = new InvoiceController(InvoiceService.getInstance());
        this.bookController = new BookController(BookService.getInstance());
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

        // Tạo combo box 5 năm gần nhất
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[5];
        for (int i = 0; i < 5; i++) {
            years[i] = currentYear - i;
        }
        yearComboBox = new JComboBox<>(years);

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

        JButton backButton = new JButton("Quay Lại Menu Chính");
        backButton.addActionListener(e -> returnToMainMenu());
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(backButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(backButtonPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel dailyReportPanel = createDailyReportPanel();
        tabbedPane.addTab("Báo Cáo Theo Ngày", dailyReportPanel);

        JPanel monthlyReportPanel = createMonthlyReportPanel();
        tabbedPane.addTab("Báo Cáo Theo Tháng", monthlyReportPanel);

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

        String[] columns = {"Ngày", "Số Đơn Hàng", "Doanh Thu"};
        dailyReportTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        dailyReportTable = new JTable(dailyReportTableModel);

        // Căn giữa cho tất cả các cột ngoại trừ cột đầu tiên (ngày)
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

        String[] columns = {"Tháng", "Số Đơn Hàng", "Doanh Thu"};
        monthlyReportTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable monthlyReportTable = new JTable(monthlyReportTableModel);

        // Căn giữa cho tất cả các cột ngoại trừ cột đầu tiên (tháng)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < monthlyReportTable.getColumnCount(); i++) {
            monthlyReportTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(monthlyReportTable);
        panel.add(scrollPane, BorderLayout.CENTER);

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

        String[] columns = {"STT", "Tên Sách", "Số Lượng Đã Bán", "Doanh Thu"};
        topBooksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable topBooksTable = new JTable(topBooksTableModel);

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
        int selectedMonth = monthComboBox.getSelectedIndex(); // 0 có nghĩa là tất cả các tháng

        List<Invoice> invoices = invoiceController.getAllInvoices();
        List<Invoice> filteredInvoices = new ArrayList<>();
        
        Calendar calendar = Calendar.getInstance();

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
        dates.sort((d1, d2) -> {
            try {
                return dateFormat.parse(d1).compareTo(dateFormat.parse(d2));
            } catch (Exception e) {
                return 0;
            }
        });
        
        // Tổng doanh thu
        double totalRevenue = 0;
        
        // Add data
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

        // Cập nhật tổng doanh thu
        if (dailyReportTable.getParent().getParent() instanceof JPanel panel) {
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
        
        List<Invoice> allInvoices = invoiceController.getAllInvoices();

        int[] orderCounts = new int[12];
        double[] revenues = new double[12];

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

        List<InvoiceItem> allItems = new ArrayList<>();
        for (Invoice invoice : invoices) {
            allItems.addAll(invoiceController.getInvoiceItemsByInvoiceId(invoice.getId()));
        }

        Map<Integer, Integer> bookQuantities = new HashMap<>();
        Map<Integer, Double> bookRevenues = new HashMap<>();
        
        for (InvoiceItem item : allItems) {
            int bookId = item.getBookId();
            int quantity = item.getQuantity();
            double revenue = item.getUnitPrice() * quantity;
            
            bookQuantities.put(bookId, bookQuantities.getOrDefault(bookId, 0) + quantity);
            bookRevenues.put(bookId, bookRevenues.getOrDefault(bookId, 0.0) + revenue);
        }

        List<Map.Entry<Integer, Integer>> sortedBooks = new ArrayList<>(bookQuantities.entrySet());
        sortedBooks.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Hiển thị tối đa 10 sách
        int count = 0;
        int limit = Math.min(sortedBooks.size(), 10);
        
        for (int i = 0; i < limit; i++) {
            Map.Entry<Integer, Integer> entry = sortedBooks.get(i);
            int bookId = entry.getKey();
            int quantity = entry.getValue();
            double revenue = bookRevenues.get(bookId);
            String bookTitle = bookController.getBookById(bookId).getTitle();
            
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
