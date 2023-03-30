package com.example.dailychallenge.vo;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestChangePassword {
    @NotBlank(message = "기존 비밀번호를 입력해주세요")
    private String oldPassword;
    @NotBlank(message = "새로운 비밀번호를 입력해주세요")
    private String newPassword;

    @Builder
    public RequestChangePassword(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
