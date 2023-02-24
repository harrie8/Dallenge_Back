package com.example.dailychallenge.entity.challenge;

import com.example.dailychallenge.dto.ChallengeEditor;
import com.example.dailychallenge.entity.BaseEntity;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.users.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge")
@Getter
@NoArgsConstructor
public class Challenge extends BaseEntity {

    @Id
    @Column(name = "challenge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Enumerated(value = EnumType.STRING)
    private ChallengeCategory challengeCategory;
    @Enumerated(value = EnumType.STRING)
    private ChallengeLocation challengeLocation;
    @Enumerated(value = EnumType.STRING)
    private ChallengeDuration challengeDuration;

    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChallengeImg> challengeImgs = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;

    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserChallenge> userChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge",cascade = CascadeType.ALL)
    private List<ChallengeHashtag> challengeHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Challenge(String title, String content, ChallengeCategory challengeCategory,
                     ChallengeLocation challengeLocation,
                     ChallengeDuration challengeDuration) {
        this.title = title;
        this.content = content;
        this.challengeCategory = challengeCategory;
        this.challengeLocation = challengeLocation;
        this.challengeDuration = challengeDuration;
    }

    public void addChallengeImg(ChallengeImg challengeImg) {
        if (!this.challengeImgs.contains(challengeImg)) {
            this.challengeImgs.add(challengeImg);
        }
    }

    public void setUser(User users) {
        if (users.getChallenges().contains(this)) {
            users.getChallenges().remove(this);
        }
        this.users = users;
        users.getChallenges().add(this);
    }

    public List<String> getImgUrls() {
        List<String> imgUrls = new ArrayList<>();
        for (ChallengeImg challengeImg : challengeImgs) {
            String imgUrl = challengeImg.getImgUrl();
            imgUrls.add(imgUrl);
        }
        return imgUrls;
    }

    public List<String> getHashtags() {
        List<String> hashtags = new ArrayList<>();
        for (ChallengeHashtag challengeHashtag : challengeHashtags) {
            String hashtagContent = challengeHashtag.getHashtag().getContent();
            hashtags.add(hashtagContent);
        }
        return hashtags;
    }

    public ChallengeEditor.ChallengeEditorBuilder toEditor() {
        return ChallengeEditor.builder()
                .title(title)
                .content(content)
                .category(challengeCategory.getDescription());
    }

    public void update(ChallengeEditor challengeEditor) {
        if (challengeEditor.getTitle() != null) {
            title = challengeEditor.getTitle();
        }
        if (challengeEditor.getContent() != null) {
            content = challengeEditor.getContent();
        }
        if (challengeEditor.getCategory() != null) {
            challengeCategory = ChallengeCategory.findByDescription(challengeEditor.getCategory());
        }
    }

    public boolean isOwner(Long userId) {
        return userId.equals(users.getId());
    }

    public void clearChallengeImgs() {
        this.challengeImgs.clear();
    }
}
