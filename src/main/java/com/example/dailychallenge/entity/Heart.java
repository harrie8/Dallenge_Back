package com.example.dailychallenge.entity;

import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.users.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
        this.comment = comment;
        this.users = users;
    }
}
