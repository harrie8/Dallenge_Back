package com.example.dailychallenge.entity.comment;

import com.example.dailychallenge.entity.BaseEntity;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name = "comment")
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentImg> commentImgs = new ArrayList<>();


    @Builder
    public Comment(String content) {
        this.content = content;
        this.likes = 0;
    }

    public void saveCommentUser(User user) {
        if (user.getComments().contains(this)) {
            user.getComments().remove(this);
        }
        this.users = user;
        user.getComments().add(this);
    }

    public void saveCommentChallenge(Challenge challenge) {
        if (challenge.getComments().contains(this)) {
            challenge.getComments().remove(this);
        }
        this.challenge = challenge;
        challenge.getComments().add(this);
    }

    public void addCommentImg(CommentImg commentImg) {
        if (!this.commentImgs.contains(commentImg)) {
            this.commentImgs.add(commentImg);
        }
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

    public List<String> getImgUrls() {
        List<String> imgUrls = new ArrayList<>();
        for (CommentImg commentImg : commentImgs) {
            String imgUrl = commentImg.getImgUrl();
            imgUrls.add(imgUrl);
        }
        return imgUrls;
    }

    public boolean isOwner(Long userId) {
        return userId.equals(users.getId());
    }

    public boolean isValidChallenge(Long challengeId) {
        return challengeId.equals(challenge.getId());
    }
}
