package com.edu.ulab.app.web.request;

import lombok.Data;

@Data
public class BookRequest {
    private String title;
    private String author;
    private int pageCount;
}
