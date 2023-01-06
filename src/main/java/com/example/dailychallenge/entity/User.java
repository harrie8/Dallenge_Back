package com.example.dailychallenge.entity;

import lombok.Data;

import javax.persistence.*;

@Entity @Table(name = "users") // user 예약어라 users로 변경
@Data
public class User {

    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userName;
    private String email;
    private String info;
    private String password;

}
