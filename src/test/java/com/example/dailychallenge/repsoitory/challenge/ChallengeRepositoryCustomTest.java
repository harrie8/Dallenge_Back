package com.example.dailychallenge.repsoitory.challenge;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.challenge.ChallengeNotFound;
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.util.RepositoryTest;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ChallengeRepositoryCustomTest extends RepositoryTest {

    @Autowired
    private UserChallengeRepository userChallengeRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private ChallengeImgRepository challengeImgRepository;
    @Autowired
    private UserRepository userRepository;

    private User savedUser;
    private Challenge challenge1;

    @BeforeEach
    void beforeEach() {
        initData();
    }

    private void initData() {
        savedUser = User.builder()
                .userName("홍길동")
                .email("test@test.com")
                .password("1234")
                .build();
        userRepository.save(savedUser);

        challenge1 = Challenge.builder()
                .title("제목입니다.1")
                .content("내용입니다.1")
                .challengeCategory(ChallengeCategory.STUDY)
                .challengeLocation(ChallengeLocation.INDOOR)
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
                .build();
        challenge1.setUser(savedUser);
        challengeRepository.save(challenge1);

        ChallengeImg challengeImg = new ChallengeImg();
        challengeImg.setImgUrl("imgUrl");
        challengeImg.setImgName("imgName");
        challengeImg.setOriImgName("oriImgName");
        challengeImg.setChallenge(challenge1);
        challengeImgRepository.save(challengeImg);
        challengeImgRepository.save(challengeImg);

        UserChallenge userChallenge1 = UserChallenge.builder()
                .challengeStatus(ChallengeStatus.TRYING)
                .users(savedUser)
                .challenge(challenge1)
                .build();
        userChallengeRepository.save(userChallenge1);

        Challenge challenge2 = Challenge.builder()
                .title("제목입니다.2")
                .content("내용입니다.2")
                .challengeCategory(ChallengeCategory.ECONOMY)
                .challengeLocation(ChallengeLocation.OUTDOOR)
                .challengeDuration(ChallengeDuration.OVER_ONE_HOUR)
                .build();
        challenge2.setUser(savedUser);
        challengeRepository.save(challenge2);

        UserChallenge userChallenge2 = UserChallenge.builder()
                .challengeStatus(ChallengeStatus.PAUSE)
                .users(savedUser)
                .challenge(challenge2)
                .build();
        userChallengeRepository.save(userChallenge2);

        Challenge challenge6 = null;

        for (int i = 3; i <= 10; i++) {
            Challenge challenge = Challenge.builder()
                    .title("제목입니다." + i)
                    .content("내용입니다." + i)
                    .challengeCategory(ChallengeCategory.WORKOUT)
                    .challengeLocation(ChallengeLocation.INDOOR)
                    .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
                    .build();
            challenge.setUser(savedUser);
            challengeRepository.save(challenge);

            UserChallenge userChallenge = UserChallenge.builder()
                    .challengeStatus(ChallengeStatus.TRYING)
                    .users(savedUser)
                    .challenge(challenge)
                    .build();
            userChallengeRepository.save(userChallenge);

            if (i == 6) {
                challenge6 = challenge;
            }
        }

        for (int i = 1; i <= 8; i++) {
            User user = User.builder()
                    .userName("홍길동" + i)
                    .email(i + "@test.com")
                    .password("1234")
                    .build();
            userRepository.save(user);
            if (i == 1) {
                UserChallenge userChallenge = UserChallenge.builder()
                        .challengeStatus(ChallengeStatus.TRYING)
                        .users(user)
                        .challenge(challenge1)
                        .build();
                userChallengeRepository.save(userChallenge);
            }
            if (2 <= i && i <= 5) {
                UserChallenge userChallenge = UserChallenge.builder()
                        .challengeStatus(ChallengeStatus.PAUSE)
                        .users(user)
                        .challenge(challenge2)
                        .build();
                userChallengeRepository.save(userChallenge);
            }

            if (i == 6) {
                UserChallenge userChallenge = UserChallenge.builder()
                        .challengeStatus(ChallengeStatus.TRYING)
                        .users(user)
                        .challenge(challenge6)
                        .build();
                userChallengeRepository.save(userChallenge);
            }
        }
    }

    @Nested
    @DisplayName("특정 챌린지 조회 테스트")
    class searchById {
        @Test
        void success() {
            Long challenge1Id = challenge1.getId();
            ResponseChallenge responseChallenge = challengeRepository.searchChallengeById(challenge1Id)
                    .orElseThrow(ChallengeNotFound::new);

            assertAll(() -> {
                assertEquals(challenge1Id, responseChallenge.getId());
                assertEquals(challenge1.getTitle(), responseChallenge.getTitle());
                assertEquals(challenge1.getContent(), responseChallenge.getContent());
                assertEquals(challenge1.getChallengeCategory().getDescription(), responseChallenge.getChallengeCategory());
                assertEquals(challenge1.getChallengeLocation().getDescription(), responseChallenge.getChallengeLocation());
                assertEquals(challenge1.getChallengeDuration().getDescription(), responseChallenge.getChallengeDuration());
                assertEquals(challenge1.getFormattedCreatedAt(), responseChallenge.getCreated_at());
                assertEquals(challenge1.getImgUrls(), responseChallenge.getChallengeImgUrls());
                assertEquals(2, responseChallenge.getHowManyUsersAreInThisChallenge());
                assertEquals(savedUser.getUserName(), responseChallenge.getChallengeOwnerUser().getUserName());
                assertEquals(savedUser.getEmail(), responseChallenge.getChallengeOwnerUser().getEmail());
                assertEquals(savedUser.getId(), responseChallenge.getChallengeOwnerUser().getUserId());
            });
        }

        @Test
        void failByChallengeNotFound() {
            Long notExistChallenge1Id = challenge1.getId() + 100L;

            Throwable exception = assertThrows(ChallengeNotFound.class,
                    () -> challengeRepository.searchChallengeById(notExistChallenge1Id)
                            .orElseThrow(ChallengeNotFound::new));
            assertEquals("챌린지를 찾을 수 없습니다.", exception.getMessage());
        }
    }
}
