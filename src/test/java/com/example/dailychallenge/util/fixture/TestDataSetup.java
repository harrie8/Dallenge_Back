package com.example.dailychallenge.util.fixture;

import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.createChallengeImgFiles;

import com.example.dailychallenge.dto.BadgeDto;
import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadgeEvaluation;
import com.example.dailychallenge.entity.badge.type.AchievementBadgeType;
import com.example.dailychallenge.entity.badge.type.ChallengeCreateBadgeType;
import com.example.dailychallenge.entity.badge.type.CommentWriteBadgeType;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.comment.CommentImg;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.CommentImgRepository;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.repository.badge.UserBadgeEvaluationRepository;
import com.example.dailychallenge.service.badge.BadgeService;
import com.example.dailychallenge.service.badge.UserBadgeEvaluationService;
import com.example.dailychallenge.service.badge.UserBadgeService;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.hashtag.ChallengeHashtagService;
import com.example.dailychallenge.service.hashtag.HashtagService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
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
    private CommentImgRepository commentImgRepository;
    @Autowired
    private HashtagService hashtagService;
    @Autowired
    private ChallengeHashtagService challengeHashtagService;
    @Autowired
    private UserBadgeEvaluationRepository userBadgeEvaluationRepository;
    @Autowired
    private UserBadgeEvaluationService userBadgeEvaluationService;
    @Autowired
    private BadgeService badgeService;
    @Autowired
    private UserBadgeService userBadgeService;

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
    public UserChallenge 챌린지에_참가한다(Challenge challenge, User user) {
        return userChallengeService.saveUserChallenge(challenge, user);
    }

    @Transactional
    public Comment 챌린지에_댓글을_단다(Challenge challenge, User user) {
        Comment comment = Comment.builder()
                .content("content")
                .build();
        comment.saveCommentChallenge(challenge);
        comment.saveCommentUser(user);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment 챌린지에_댓글을_단다(Challenge challenge, User user, String content) {
        Comment comment = Comment.builder()
                .content(content)
                .build();
        comment.saveCommentChallenge(challenge);
        comment.saveCommentUser(user);
        return commentRepository.save(comment);
    }

    @Transactional
    public CommentImg 댓글에_이미지를_추가한다(Comment comment) {
        CommentImg commentImg = CommentImg.builder()
                .imgName("imgName")
                .oriImgName("oriImgName")
                .imgUrl("images/abcdefg.jpg")
                .build();
        commentImg.saveComment(comment);
        comment.addCommentImg(commentImg);

        return commentImgRepository.save(commentImg);
    }

    @Transactional
    public void 챌린지에_해시태그를_단다(Challenge challenge, List<String> hashtagDto) {
        List<Hashtag> hashtags = hashtagService.saveHashtag(hashtagDto);
        challengeHashtagService.saveChallengeHashtag(challenge, hashtags);
    }

    @Transactional
    public void 챌린지를_달성한다(UserChallenge userChallenge) {
        User user = userChallenge.getUsers();
        Challenge challenge = userChallenge.getChallenge();
        userChallengeService.succeedInChallenge(user.getId(), challenge.getId());
    }

    @Transactional
    public UserBadgeEvaluation saveUserBadgeEvaluation(User user) {
        return userBadgeEvaluationRepository.save(UserBadgeEvaluation.builder()
                .users(user)
                .build());
    }

    @Transactional
    public void 챌린지_달성_뱃지를_만들_수_있으면_만든다(User user) {
        userBadgeEvaluationService.createAchievementBadgeIfFollowStandard(user);
    }

    @Transactional
    public void 챌린지_생성_뱃지를_만들_수_있으면_만든다(User user) {
        userBadgeEvaluationService.createChallengeCreateBadgeIfFollowStandard(user);
    }

    @Transactional
    public void 후기_작성_뱃지를_만들_수_있으면_만든다(User user) {
        userBadgeEvaluationService.createCommentWriteBadgeIfFollowStandard(user);
    }

    @Transactional
    public void saveBadgesAndUserBadges(User user) {
        List<BadgeDto> challengeCreateBadgeDtos = ChallengeCreateBadgeType.getBadgeDtos();
        List<BadgeDto> achievementBadgeDtos = AchievementBadgeType.getBadgeDtos();
        List<BadgeDto> commentWriteBadgeDtos = CommentWriteBadgeType.getBadgeDtos();
        List<Badge> challengeCreateBadges = badgeService.createBadges(challengeCreateBadgeDtos);
        List<Badge> achievementBadges = badgeService.createBadges(achievementBadgeDtos);
        List<Badge> commentWriteBadges = badgeService.createBadges(commentWriteBadgeDtos);
        userBadgeService.createUserBadges(user, challengeCreateBadges);
        userBadgeService.createUserBadges(user, achievementBadges);
        userBadgeService.createUserBadges(user, commentWriteBadges);
    }
}
