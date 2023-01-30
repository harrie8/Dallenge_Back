package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseError {

    private Integer code;
    private String message;

    @Builder
    public ResponseError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
