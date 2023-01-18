package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Data;

@Data
public class RequestUser {
    private String userName;
    private String email;
    private String password;

    @Builder
    public RequestUser(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
}
