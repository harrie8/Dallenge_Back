package com.example.dailychallenge.controller.exception;

import com.example.dailychallenge.exception.FileNotUpload;
import com.example.dailychallenge.exception.challenge.ChallengeCategoryNotFound;
import com.example.dailychallenge.vo.ResponseError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChallengeCategoryNotFound.class)
    protected ResponseEntity<ResponseError> handlerChallengeCategoryNotFound(ChallengeCategoryNotFound challengeCategoryNotFound) {
        final ResponseError responseError = ResponseError.builder()
                .code(challengeCategoryNotFound.getStatusCode())
                .message(challengeCategoryNotFound.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(FileNotUpload.class)
    protected ResponseEntity<ResponseError> handlerFileNotUpload(FileNotUpload fileNotUpload) {
        final ResponseError responseError = ResponseError.builder()
                .code(fileNotUpload.getStatusCode())
                .message(fileNotUpload.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }
}
