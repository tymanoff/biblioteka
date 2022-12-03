package com.edu.ulab.app.web.request;

import lombok.Data;

@Data
public class UserRequest {
    private Integer id;
    private String fullName;
    private String title;
    private int age;
}
