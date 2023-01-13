package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Data;

@Data
public class RequestLogin {
    private String email;
    private String password;

    public RequestLogin() {
    }

    @Builder
    public RequestLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
