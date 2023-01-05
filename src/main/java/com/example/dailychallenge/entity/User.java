package com.example.dailychallenge.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity @Table(name = "user")
public class User {

    @Id @Column(name = "user_id")
    private Long id;

    private String userName;
    private String email;
    private String password;
}
