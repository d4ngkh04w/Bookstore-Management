package com.bookstore.app.service;

import com.bookstore.app.dao.BookDAO;
import com.bookstore.app.model.Book;

import java.util.List;

public class BookService {
    private static BookService instance;
    private final BookDAO bookDAO;
    
    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }
    
    public static BookService getInstance() {
        if (instance == null) {
            instance = new BookService(new BookDAO());
        }
        return instance;
    }
    
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }
    
    public boolean addBook(Book book) {
        return bookDAO.addBook(book);
    }
    
    public boolean updateBook(Book book) {
        return bookDAO.updateBook(book);
    }
    
    public boolean deleteBook(int id) {
        return bookDAO.deleteBook(id);
    }
    
    public Book getBookById(int id) {
        return bookDAO.getBookById(id);
    }
}
