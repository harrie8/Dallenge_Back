package com.example.dailychallenge.util.fixture;

import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.createChallengeImgFiles;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.hashtag.ChallengeHashtagService;
import com.example.dailychallenge.service.hashtag.HashtagService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestDataSetup {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private UserChallengeService userChallengeService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private HashtagService hashtagService;
    @Autowired
    private ChallengeHashtagService challengeHashtagService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User saveUser(String userName, String email, String password) {
        return userRepository.save(User.builder()
                .userName(userName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build());
    }

    @Transactional
    public Challenge 챌린지를_생성한다(String title, String content, String challengeCategoryDescription,
                                String challengeLocationDescription, String challengeDurationDescription,
                                User user) {
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title(title)
                .content(content)
                .challengeCategory(challengeCategoryDescription)
                .challengeLocation(challengeLocationDescription)
                .challengeDuration(challengeDurationDescription)
                .build();

        return challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), user);
    }

    @Transactional
    public Challenge 챌린지를_생성한다(ChallengeDto challengeDto, User user) {
        return challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), user);
    }

    @Transactional
    public void 챌린지에_참가한다(Challenge challenge, User user) {
        userChallengeService.saveUserChallenge(challenge, user);
    }

    @Transactional
    public void 챌린지예_댓글을_단다(Challenge challenge) {
        Comment comment = Comment.builder()
                .content("content")
                .build();
        comment.saveCommentChallenge(challenge);
        commentRepository.save(comment);
    }

    @Transactional
    public void 챌린지에_해시태그를_단다(Challenge challenge) {
        List<String> hashtagDto = List.of("tag1", "tag2");
        List<Hashtag> hashtags = hashtagService.saveHashtag(hashtagDto);
        challengeHashtagService.saveChallengeHashtag(challenge, hashtags);
    }
}
