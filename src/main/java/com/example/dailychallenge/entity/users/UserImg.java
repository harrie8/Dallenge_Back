package com.example.dailychallenge.entity.users;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_img")
@NoArgsConstructor
@Getter
public class UserImg {
    @Id @Column(name = "user_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgName; // 이미지 파일명
    private String oriImgName; // 원본 이미지 파일명
    private String imgUrl; // 이미지 조회 경로

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User users;

    public void saveUser(User user) {
        this.users = user;
    }

    public void updateUserImg(String oriImgName, String imgName, String imgUrl) {
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
