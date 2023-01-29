package com.example.dailychallenge.dto;

import com.example.dailychallenge.entity.users.User;
import lombok.Data;

@Data
public class UserDto {
    private String userName;
    private String email;
    private String password;
    private String info;
    private String provider;

}
