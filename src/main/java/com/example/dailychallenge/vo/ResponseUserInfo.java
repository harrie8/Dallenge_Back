package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.comment.Comment;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseUserInfo {
    private String email;
    private String userName;
    private String info;
    private String imageUrl;

    @Builder
    public ResponseUserInfo(String email, String userName, String info, String imageUrl) {
        this.email = email;
        this.userName = userName;
        this.info = info;
        this.imageUrl = imageUrl;
    }
}
