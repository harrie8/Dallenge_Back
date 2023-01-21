package com.example.dailychallenge.vo;

import lombok.Data;

@Data
public class ResponseLoginUser {
    private Long userId;
    private String token;
    private String userName;
}
