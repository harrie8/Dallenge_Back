package com.example.dailychallenge.entity;

import com.example.dailychallenge.dto.UserEditor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

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

    public UserEditor.UserEditorBuilder toEditor() {
        return UserEditor.builder()
                .userName(userName)
                .email(email)
                .info(info)
                .password(password);
    }

    public void update(UserEditor userEditor) {
        userName = userEditor.getUserName();
        email = userEditor.getEmail();
        info = userEditor.getInfo();
        password = userEditor.getPassword();
    }
}
