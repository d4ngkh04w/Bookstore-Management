package com.bookstore.app.controller;

import com.bookstore.app.service.BookService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BookController {
    private final BookService bookService;
}
