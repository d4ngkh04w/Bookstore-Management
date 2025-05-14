package com.bookstore.app.dao;

import com.bookstore.app.model.Invoice;
import com.bookstore.app.model.InvoiceItem;
import com.bookstore.app.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    private static final String SELECT_ALL_INVOICES = "SELECT * FROM invoices";
    private static final String SELECT_INVOICE_BY_ID = "SELECT * FROM invoices WHERE id = ?";
    private static final String INSERT_INVOICE = "INSERT INTO invoices (customer_id, date, total_amount) VALUES (?, ?, ?)";
    private static final String DELETE_INVOICE = "DELETE FROM invoices WHERE id = ?";
    
    private static final String SELECT_INVOICE_ITEMS = "SELECT * FROM invoice_items WHERE invoice_id = ?";
    private static final String INSERT_INVOICE_ITEM = "INSERT INTO invoice_items (invoice_id, book_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
    private static final String DELETE_INVOICE_ITEMS = "DELETE FROM invoice_items WHERE invoice_id = ?";
    
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        
        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_INVOICES)
        ) {
            while (resultSet.next()) {
                invoices.add(extractInvoiceFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }
    
    public Invoice getInvoiceById(int id) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_INVOICE_BY_ID)
        ) {
            preparedStatement.setInt(1, id);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractInvoiceFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<InvoiceItem> getInvoiceItems(int invoiceId) {
        List<InvoiceItem> items = new ArrayList<>();
        
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_INVOICE_ITEMS)
        ) {
            preparedStatement.setInt(1, invoiceId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {                    items.add(new InvoiceItem(
                        resultSet.getInt("invoice_id"),
                        resultSet.getInt("book_id"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("unit_price")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
      public int addInvoice(Invoice invoice, List<InvoiceItem> items) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);
            
            // Insert invoice
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INVOICE, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, invoice.getCustomerId());
                preparedStatement.setTimestamp(2, new Timestamp(invoice.getDate().getTime()));
                preparedStatement.setDouble(3, invoice.getTotalAmount());
                  int affectedRows = preparedStatement.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int invoiceId = generatedKeys.getInt(1);
                            invoice.setId(invoiceId);
                            // Insert invoice items
                            try (PreparedStatement itemsStmt = connection.prepareStatement(INSERT_INVOICE_ITEM)) {
                                for (InvoiceItem item : items) {
                                    itemsStmt.setInt(1, invoiceId);
                                    itemsStmt.setInt(2, item.getBookId());
                                    itemsStmt.setInt(3, item.getQuantity());
                                    itemsStmt.setDouble(4, item.getUnitPrice());
                                    itemsStmt.addBatch();
                                }
                                itemsStmt.executeBatch();
                            }
                            
                            connection.commit();
                            return invoiceId;
                        }
                    }
                }
            }
            
            connection.rollback();
            return -1;
              } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            // Check for the specific error related to missing total_amount column
            if (e.getMessage().contains("total_amount")) {
                System.err.println("ERROR: The 'total_amount' column is missing in the invoices table.");
                System.err.println("Please restart the application to apply database migrations,");
                System.err.println("or manually run: ALTER TABLE invoices ADD COLUMN total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0;");
            }
            
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean deleteInvoice(int id) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);
            
            // Delete invoice items first (due to foreign key constraint)
            try (PreparedStatement itemsStmt = connection.prepareStatement(DELETE_INVOICE_ITEMS)) {
                itemsStmt.setInt(1, id);
                itemsStmt.executeUpdate();
            }
            
            // Delete invoice
            try (PreparedStatement invoiceStmt = connection.prepareStatement(DELETE_INVOICE)) {
                invoiceStmt.setInt(1, id);
                int affectedRows = invoiceStmt.executeUpdate();
                
                if (affectedRows > 0) {
                    connection.commit();
                    return true;
                }
            }
            
            connection.rollback();
            return false;
            
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
      private Invoice extractInvoiceFromResultSet(ResultSet resultSet) throws SQLException {
        return new Invoice(
            resultSet.getInt("id"),
            resultSet.getInt("customer_id"),
            resultSet.getTimestamp("date"),
            resultSet.getDouble("total_amount")
        );
    }
}
