package com.bookstore.app.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private int id;
    private String name;
    private String description;
}
