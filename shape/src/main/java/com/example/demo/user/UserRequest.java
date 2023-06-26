package com.example.demo.user;


import lombok.Data;

import java.util.Date;

@Data
public class UserRequest {
    private String login;
    private Date lastLoginDateFrom;
    private Date lastLoginDateTo;
    private int shapesCreatedFrom;
    private int shapesCreatedTo;
}
