package com.edu.ulab.app.dto;

import lombok.Data;

@Data
public class BookDto {
    private Integer id;
    private Integer userId;
    private String title;
    private String author;
    private int pageCount;
}
