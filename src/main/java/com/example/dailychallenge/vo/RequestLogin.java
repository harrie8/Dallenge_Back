package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class RequestLogin {
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @Builder
    public RequestLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
