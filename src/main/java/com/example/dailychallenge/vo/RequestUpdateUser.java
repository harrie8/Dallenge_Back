package com.example.dailychallenge.vo;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestUpdateUser {
    @NotBlank(message = "이름을 입력해주세요.")
    private String userName;
    @NotBlank(message = "자기소개글을 입력해주세요.")
    private String info;

    @Builder
    public RequestUpdateUser(String userName, String info) {
        this.userName = userName;
        this.info = info;
    }
}
