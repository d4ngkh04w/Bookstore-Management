package com.bookstore.app.dao;

import com.bookstore.app.model.Book;
import com.bookstore.app.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private static final String SELECT_ALL_BOOKS = "SELECT * FROM books";
    private static final String SELECT_BOOK_BY_ID = "SELECT * FROM books WHERE book_id = ?";
    private static final String SELECT_BOOKS_PAGINATED = "SELECT * FROM books LIMIT ? OFFSET ?";
    private static final String INSERT_BOOK = "INSERT INTO books (title, author, published_date, price, stock, description, image, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_BOOK = "UPDATE books SET title = ?, author = ?, published_date = ?, price = ?, stock = ?, description = ?, image = ?, category_id = ? WHERE id = ?";
    private static final String DELETE_BOOK = "DELETE FROM books WHERE book_id = ?";
    private static final String COUNT_BOOKS = "SELECT COUNT(*) FROM books";

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BOOKS);
            ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                books.add(extractBookFromResultSet(resultSet));
            }
        }
        return books;
    }

    public Book getBookById(int id) throws SQLException {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_ID)
        ) {
            preparedStatement.setInt(1, id);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractBookFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    public boolean addBook(Book book) throws SQLException {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOOK)
        ) {
            setBookParameters(preparedStatement, book);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean updateBook(Book book) throws SQLException {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BOOK)
        ) {
            setBookParameters(preparedStatement, book);
            preparedStatement.setInt(8, book.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteBook(int id) throws SQLException {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOK)
        ) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Book> getBooksPaginated(int pageNumber, int pageSize) throws SQLException {
        List<Book> books = new ArrayList<>();
        
        int offset = (pageNumber - 1) * pageSize;
        
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOKS_PAGINATED)
        ) {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, offset);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(extractBookFromResultSet(resultSet));
                }
            }
        }
        return books;
    }
    
    public List<Book> getBooksPaginatedWithSortAndFilter(int pageNumber, int pageSize, String sortField, String sortOrder,
                                                         String filterField, String filterValue) throws SQLException {
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
                    books.add(extractBookFromResultSet(resultSet));
                }
            }
        }
        return books;
    }
    
    public int getTotalBooks() throws SQLException {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(COUNT_BOOKS);
            ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }
    
    public int getTotalFilteredBooks(String filterField, String filterValue) throws SQLException {
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
        }
        return 0;
    }

    private Book extractBookFromResultSet(ResultSet resultSet) throws SQLException {
        return new Book(
            resultSet.getInt("book_id"),
            resultSet.getString("title"),
            resultSet.getString("author"),
            resultSet.getDate("published_date"),
            resultSet.getDouble("price"),
            resultSet.getInt("stock"),
            resultSet.getString("description"),
            resultSet.getString("image"),
            resultSet.getInt("category_id")
        );
    }

    private void setBookParameters(PreparedStatement preparedStatement, Book book) throws SQLException {
        preparedStatement.setString(1, book.getTitle());
        preparedStatement.setString(2, book.getAuthor());
        preparedStatement.setDate(3, new Date(book.getPublishedDate().getTime()));
        preparedStatement.setDouble(4, book.getPrice());
        preparedStatement.setInt(5, book.getStock());
        preparedStatement.setString(6, book.getDescription());
        preparedStatement.setString(7, book.getImagePath());
        preparedStatement.setInt(8, book.getCategoryId());
    }
}
