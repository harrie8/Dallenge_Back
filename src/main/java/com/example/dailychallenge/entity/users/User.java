package com.example.dailychallenge.entity.users;

import com.example.dailychallenge.dto.UserEditor;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name = "users") // user 예약어라 users로 변경
@Getter @NoArgsConstructor
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

    @OneToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Challenge> challenges = new ArrayList<>();

    @OneToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserChallenge> userChallenges = new ArrayList<>();

    @Builder
    public User(String userName, String email, String password, String registrationId) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.registrationId = registrationId;
    }

    public void saveDefaultImg(UserImg userImg) {
        this.userImg = userImg;
    }

    public UserEditor.UserEditorBuilder toEditor() {
        return UserEditor.builder()
                .userName(userName)
                .info(info)
                .password(password);
    }

    public void update(UserEditor userEditor) {
        userName = userEditor.getUserName();
        info = userEditor.getInfo();
        password = userEditor.getPassword();
    }
}
