package com.bookstore.app.service;

import com.bookstore.app.dao.BookDAO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BookService {
    private static BookService instance;
    private final BookDAO bookDAO;
    
    public static BookService getInstance() {
        if (instance == null) {
            instance = new BookService(new BookDAO());
        }
        return instance;
    }
}
