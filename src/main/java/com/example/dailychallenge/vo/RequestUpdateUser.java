package com.example.dailychallenge.vo;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestUpdateUser {
    @NotBlank(message = "이름을 입력해주세요.")
    private String userName;
    @NotBlank(message = "비밀번호를 입력해주세요.")

    private String password;
    @NotBlank(message = "소개글을 입력해주세요.")

    private String info;

    public RequestUpdateUser() {
    }

    @Builder
    public RequestUpdateUser(String userName, String password, String info) {
        this.userName = userName;
        this.password = password;
        this.info = info;
    }
}
