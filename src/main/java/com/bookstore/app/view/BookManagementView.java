package com.bookstore.app.view;

import com.bookstore.app.controller.BookController;
import com.bookstore.app.model.Book;
import com.bookstore.app.dao.CategoryDAO;
import com.bookstore.app.service.BookService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class BookManagementView extends JFrame {
    private final MainMenuView mainMenu;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;
    private final BookController bookController;
    private final CategoryDAO categoryDAO;

    public BookManagementView(MainMenuView mainMenu) {
        this.mainMenu = mainMenu;
        this.bookController = new BookController(BookService.getInstance());
        this.categoryDAO = new CategoryDAO();
        initComponents();
        loadBookData();
    }

    private void initComponents() {
        setTitle("Quản Lý Sách");
        setSize(900, 600);
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
        searchTypeComboBox = new JComboBox<>(new String[]{"Tên Sách", "Tác Giả", "Thể Loại"});
        JButton searchButton = new JButton("Tìm Kiếm");

        searchControlsPanel.add(new JLabel("Tìm theo:"));
        searchControlsPanel.add(searchTypeComboBox);
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(searchButton);

        searchPanel.add(searchControlsPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Quay Lại Menu Chính");
        backButton.addActionListener(_ -> returnToMainMenu());
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(backButton);
        searchPanel.add(backButtonPanel, BorderLayout.EAST);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Tên Sách", "Tác Giả", "Thể Loại", "Giá", "Số Lượng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Double.class; // Price column
                if (columnIndex == 5) return Integer.class; // Quantity column
                return String.class;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setAutoCreateRowSorter(true);
        
        // Set column widths
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Category
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Price
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Quantity

        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Thêm Sách");
        JButton editButton = new JButton("Sửa Sách");
        JButton deleteButton = new JButton("Xóa Sách");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(_ -> showAddBookDialog());
        editButton.addActionListener(_ -> showEditBookDialog());
        deleteButton.addActionListener(_ -> deleteSelectedBook());

        searchButton.addActionListener(e -> searchBooks());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBooks();
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

    public void loadBookData() {
        tableModel.setRowCount(0);

        List<Book> books = bookController.getAllBooks();
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getPrice(),
                book.getQuantity()
            });
        }
    }
    
    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadBookData();
            bookTable.setRowSorter(null);
            return;
        }
        
        int searchType = searchTypeComboBox.getSelectedIndex();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(sorter);

        // 0: Title, 1: Author, 2: Category
        int columnToSearch = searchType + 1;
        
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm, columnToSearch));
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "Thêm Sách Mới", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();

        List<String> categories = categoryDAO.getAllCategoryNames();
        JComboBox<String> categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));
        
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();

        formPanel.add(new JLabel("Tên Sách:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Tác Giả:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Thể Loại:"));
        formPanel.add(categoryComboBox);
        formPanel.add(new JLabel("Giá:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Số Lượng:"));
        formPanel.add(quantityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(_ -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String category = (String) categoryComboBox.getSelectedItem();
                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Book newBook = new Book();
                newBook.setTitle(title);
                newBook.setAuthor(author);
                newBook.setCategory(category);
                newBook.setPrice(price);
                newBook.setQuantity(quantity);

                bookController.addBook(newBook);
                dialog.dispose();
                loadBookData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá và số lượng phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(_ -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void showEditBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách để sửa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = bookTable.getRowSorter() != null 
            ? bookTable.getRowSorter().convertRowIndexToModel(selectedRow) 
            : selectedRow;

        int bookId = (int) tableModel.getValueAt(modelRow, 0);
        Book book = bookController.getBookById(bookId);

        if (book == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Sửa Thông Tin Sách", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField(book.getTitle());
        JTextField authorField = new JTextField(book.getAuthor());

        List<String> categories = categoryDAO.getAllCategoryNames();
        JComboBox<String> categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));
        categoryComboBox.setSelectedItem(book.getCategory());
        
        JTextField priceField = new JTextField(String.valueOf(book.getPrice()));
        JTextField quantityField = new JTextField(String.valueOf(book.getQuantity()));

        formPanel.add(new JLabel("Tên Sách:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Tác Giả:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Thể Loại:"));
        formPanel.add(categoryComboBox);
        formPanel.add(new JLabel("Giá:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Số Lượng:"));
        formPanel.add(quantityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(_ -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String category = (String) categoryComboBox.getSelectedItem();
                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                book.setTitle(title);
                book.setAuthor(author);
                book.setCategory(category);
                book.setPrice(price);
                book.setQuantity(quantity);

                bookController.updateBook(book);
                dialog.dispose();
                loadBookData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá và số lượng phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(_ -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách để xóa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = bookTable.getRowSorter() != null 
            ? bookTable.getRowSorter().convertRowIndexToModel(selectedRow) 
            : selectedRow;

        int bookId = (int) tableModel.getValueAt(modelRow, 0);
        String bookTitle = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa sách '" + bookTitle + "'?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            bookController.deleteBook(bookId);
            loadBookData();
        }
    }

    private void returnToMainMenu() {
        this.dispose();
        mainMenu.setVisible(true);
    }
}
