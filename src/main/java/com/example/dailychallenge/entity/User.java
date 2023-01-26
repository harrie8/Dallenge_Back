package com.example.dailychallenge.entity;

import com.example.dailychallenge.dto.UserEditor;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "users") // user 예약어라 users로 변경
@Getter @Setter // Test 시 @toString 때문에 StackOverflow 발생하여 변경
public class User {

    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String email;
    private String info;
    private String password;

    private String registrationId;

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
    private UserImg userImg;

    @OneToMany(mappedBy = "challenge")
    private List<UserChallenge> userChallenges = new ArrayList<>();

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
