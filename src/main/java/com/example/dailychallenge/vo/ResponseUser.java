package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.users.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseUser {
    private String userName;
    private String email;
    private Long userId;

    @Builder
    public ResponseUser(String userName, String email, Long userId) {
        this.userName = userName;
        this.email = email;
        this.userId = userId;
    }

    public static ResponseUser create(User user) {
        return ResponseUser.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .userId(user.getId())
                .build();
    }
}
