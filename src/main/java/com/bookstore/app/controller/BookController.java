package com.bookstore.app.controller;

import com.bookstore.app.model.Book;
import com.bookstore.app.service.BookService;

import java.util.List;

public class BookController {
    private final BookService bookService;
      
    public BookController(BookService service) {
        this.bookService = service;
    }
      
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }
    
    public Book getBookById(int id) {
        return bookService.getBookById(id);
    }
    
    public void addBook(Book book) {
        bookService.addBook(book);
    }
    
    public void updateBook(Book book) {
        bookService.updateBook(book);
    }
    
    public void deleteBook(int id) {
        bookService.deleteBook(id);
    }    
}

