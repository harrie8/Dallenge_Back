package com.example.dailychallenge.entity.comment;

import com.example.dailychallenge.entity.BaseEntity;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "comment")
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String content;
    private Integer likes;

    @Transient
    private List<Long> likePeople = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User users;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentImg> commentImgs = new ArrayList<>();


    @Builder
    public Comment(String content) {
        this.content = content;
        this.likes = 0;
    }

    public void saveCommentUser(User user) {
        this.users = user;
    }

    public void saveCommentChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public void addCommentImg(CommentImg commentImg) {
        this.commentImgs.add(commentImg);
    }

    public void updateComment(String content){
        this.content = content;
    }
    public void updateLike(boolean isLike){
        if(isLike) {
            this.likes += 1;
        } else{
            this.likes -= 1;
        }
    }

}
