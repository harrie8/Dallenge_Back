package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class RequestLogin {
    private String email;
    private String password;

    @Builder
    public RequestLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
