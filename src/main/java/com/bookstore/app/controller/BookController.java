package com.bookstore.app.controller;

import com.bookstore.app.dao.BookDAO;
import com.bookstore.app.model.Book;
import com.bookstore.app.view.BookManagementView;

import java.util.List;

public class BookController {
    private BookDAO bookDAO;
    private Object viewObject;
      
    public BookController(Object view, BookDAO dao) {
        this.viewObject = view;
        this.bookDAO = dao;
    }
      
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }
    
    public Book getBookById(int id) {
        return bookDAO.getBookById(id);
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
}

