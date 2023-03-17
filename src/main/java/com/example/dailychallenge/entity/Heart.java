package com.example.dailychallenge.entity;

import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.users.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "heart")
@Getter
@NoArgsConstructor
public class Heart {

    @Id
    @Column(name = "heart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User users;

    @Builder
    public Heart(Long id, Comment comment, User users) {
        saveComment(comment);
        this.users = users;
    }

    public void saveComment(Comment comment) {
        if (comment.getHearts().contains(this)) {
            comment.getHearts().remove(this);
        }
        this.comment = comment;
        comment.getHearts().add(this);
    }
}
