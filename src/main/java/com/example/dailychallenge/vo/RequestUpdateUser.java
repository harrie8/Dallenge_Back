package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestUpdateUser {
    private String userName;
    private String info;

    @Builder
    public RequestUpdateUser(String userName, String info) {
        this.userName = userName;
        this.info = info;
    }
}
