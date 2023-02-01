package com.example.dailychallenge.vo;

import javax.validation.constraints.NotBlank;

import lombok.*;

@Getter
@NoArgsConstructor
public class RequestUpdateUser {
    @NotBlank(message = "이름을 입력해주세요.")
    private String userName;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
    private String info;

    @Builder
    public RequestUpdateUser(String userName, String password, String info) {
        this.userName = userName;
        this.password = password;
        this.info = info;
    }
}
