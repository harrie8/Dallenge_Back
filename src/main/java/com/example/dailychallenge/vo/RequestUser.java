package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class RequestUser {
    @NotBlank(message = "닉네임을 입력해주세요")
    private String userName;
    @NotBlank(message = "이메일을 입력해주세요")
    @Email
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @Builder
    public RequestUser(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
}
