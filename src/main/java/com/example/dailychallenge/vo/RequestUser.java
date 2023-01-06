package com.example.dailychallenge.vo;

import lombok.Data;

@Data
public class RequestUser {
    private String userName;
    private String email;
    private String password;
    private String info;
}
