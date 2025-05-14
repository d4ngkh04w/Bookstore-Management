package com.bookstore.app.dao;

import com.bookstore.app.model.Book;
import com.bookstore.app.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private static final String SELECT_ALL_BOOKS = "SELECT * FROM books";
    private static final String SELECT_BOOK_BY_ID = "SELECT * FROM books WHERE id = ?";
    private static final String INSERT_BOOK = "INSERT INTO books (title, author, category, price, quantity) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_BOOK = "UPDATE books SET title = ?, author = ?, category = ?, price = ?, quantity = ? WHERE id = ?";
    private static final String DELETE_BOOK = "DELETE FROM books WHERE id = ?";
    private static final String COUNT_BOOKS = "SELECT COUNT(*) FROM books";

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        
        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_BOOKS)
        ) {
            while (resultSet.next()) {
                books.add(new Book(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getString("author"),
                    resultSet.getString("category"),
                    resultSet.getDouble("price"),
                    resultSet.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public Book getBookById(int id) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_ID)
        ) {
            preparedStatement.setInt(1, id);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addBook(Book book) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOOK, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getCategory());
            preparedStatement.setDouble(4, book.getPrice());
            preparedStatement.setInt(5, book.getQuantity());
            
            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateBook(Book book) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BOOK)
        ) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getCategory());
            preparedStatement.setDouble(4, book.getPrice());
            preparedStatement.setInt(5, book.getQuantity());
            preparedStatement.setInt(6, book.getId());
            
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteBook(int id) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOK)
        ) {
            preparedStatement.setInt(1, id);
            
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Book> getBooksPaginatedWithSortAndFilter(int pageNumber, int pageSize, String sortField, String sortOrder,
                                                         String filterField, String filterValue) {
        List<Book> books = new ArrayList<>();
        
        int offset = (pageNumber - 1) * pageSize;
        
        StringBuilder queryBuilder = new StringBuilder(SELECT_ALL_BOOKS);
        
        if (filterField != null && !filterField.isEmpty() && filterValue != null) {
            queryBuilder.append(" WHERE ").append(filterField).append(" LIKE ?");
        }
        
        if (sortField != null && !sortField.isEmpty()) {
            queryBuilder.append(" ORDER BY ").append(sortField);
            if (sortOrder != null && sortOrder.equalsIgnoreCase("DESC")) {
                queryBuilder.append(" DESC");
            } else {
                queryBuilder.append(" ASC");
            }
        }
        
        queryBuilder.append(" LIMIT ? OFFSET ?");
        
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())
        ) {
            int paramIndex = 1;
            
            if (filterField != null && !filterField.isEmpty() && filterValue != null) {
                preparedStatement.setString(paramIndex++, "%" + filterValue + "%");
            }
            
            preparedStatement.setInt(paramIndex++, pageSize);
            preparedStatement.setInt(paramIndex, offset);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public int getTotalBooks() {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(COUNT_BOOKS);
            ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTotalFilteredBooks(String filterField, String filterValue) {
        StringBuilder queryBuilder = new StringBuilder(COUNT_BOOKS);
        
        if (filterField != null && !filterField.isEmpty() && filterValue != null) {
            queryBuilder.append(" WHERE ").append(filterField).append(" LIKE ?");
        }
        
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())
        ) {
            if (filterField != null && !filterField.isEmpty() && filterValue != null) {
                preparedStatement.setString(1, "%" + filterValue + "%");
            }
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
