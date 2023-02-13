package com.example.dailychallenge.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserEditor {

    // 수정할 수 있는 필드들만 정의
    private final String userName;
    private final String info;

    @Builder
    public UserEditor(String userName, String info) {
        this.userName = userName;
        this.info = info;
    }
}
