package com.example.dailychallenge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserDto {
    private String userName;
    private String email;
    private String password;
    private String info;
    private String provider;
}
