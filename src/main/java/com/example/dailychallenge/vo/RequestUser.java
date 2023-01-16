package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Data;

@Data
public class RequestUser {
    private String userName;
    private String email;
    private String password;
//    private String info;

    @Builder
    public RequestUser(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
//        this.info = info;
    }
}
