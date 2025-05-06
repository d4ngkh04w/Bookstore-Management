package com.bookstore.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private int id;
    private String title;
    private String author;
    private Date publishedDate;
    private double price;
    private int stock;
    private String description;
    private String imagePath;
    private int categoryId;
}
