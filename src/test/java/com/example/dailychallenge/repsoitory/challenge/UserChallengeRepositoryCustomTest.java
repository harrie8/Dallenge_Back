package com.example.dailychallenge.repsoitory.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.util.RepositoryTest;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class UserChallengeRepositoryCustomTest extends RepositoryTest {

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
    private User otherUser;

    @BeforeEach
    void beforeEach() throws InterruptedException {
        initData();
    }

    private void initData() throws InterruptedException {
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
            Thread.sleep(1);
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

        otherUser = User.builder()
                .userName("김철수")
                .email("a@a.com")
                .password("1234")
                .build();
        userRepository.save(otherUser);
    }

    @Nested
    @DisplayName("챌린지 Id와 유저 Id로 UserChallenge를 찾는 테스트")
    class findByChallengeIdAndUserId {
        @Test
        void present() {
            Long challenge1Id = challenge1.getId();
            Long userId = savedUser.getId();

            Optional<UserChallenge> findUserChallenge = userChallengeRepository.findByChallengeIdAndUserId(
                    challenge1Id, userId);

            assertTrue(findUserChallenge.isPresent());
        }

        @Test
        void empty() {
            Long challenge1Id = challenge1.getId();
            Long otherUserId = otherUser.getId();

            Optional<UserChallenge> findUserChallenge = userChallengeRepository.findByChallengeIdAndUserId(
                    challenge1Id, otherUserId);

            assertTrue(findUserChallenge.isEmpty());
        }
    }

    static Stream<Arguments> generateSortData() {
        return Stream.of(
                Arguments.of("popular",
                        List.of("제목입니다.2", "제목입니다.1", "제목입니다.6", "제목입니다.3", "제목입니다.4", "제목입니다.5",
                                "제목입니다.7", "제목입니다.8", "제목입니다.9", "제목입니다.10"),
                        List.of(5L, 2L, 2L, 1L, 1L, 1L, 1L, 1L, 1L, 1L)),
                Arguments.of("time",
                        List.of("제목입니다.10", "제목입니다.9", "제목입니다.8", "제목입니다.7", "제목입니다.6", "제목입니다.5",
                                "제목입니다.4", "제목입니다.3", "제목입니다.2", "제목입니다.1"),
                        List.of(1L, 1L, 1L, 1L, 2L, 1L, 1L, 1L, 5L, 2L))
        );
    }

    @ParameterizedTest
    @MethodSource("generateSortData")
    @DisplayName("모든 챌린지들을 찾는 테스트")
    void searchAllChallenges(String sortProperties, List<String> titleExpect,
                             List<Long> howManyUsersAreInThisChallengeExpect) {

        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(sortProperties));
        Page<ResponseChallenge> results = userChallengeRepository.searchAllChallenges(pageRequest);

        for (ResponseChallenge result : results) {
            System.out.println("result = " + result);
        }

        assertAll(() -> {
            assertThat(results).extracting("title").containsExactlyElementsOf(titleExpect);
            assertThat(results).extracting("howManyUsersAreInThisChallenge")
                    .containsExactlyElementsOf(howManyUsersAreInThisChallengeExpect);
            assertThat(results).extracting("challengeOwnerUser").extracting("userName")
                    .contains(savedUser.getUserName());
            if (sortProperties.equals("time")) {
                List<String> createdAts = results.getContent().stream()
                        .map(ResponseChallenge::getCreated_at)
                        .sorted()
                        .collect(Collectors.toList());
                assertThat(createdAts).isSorted();
            }
            if (sortProperties.equals("popular")) {
                List<Long> howManyUsersAreInThisChallenges = results.getContent().stream()
                        .map(ResponseChallenge::getHowManyUsersAreInThisChallenge)
                        .sorted()
                        .collect(Collectors.toList());
                assertThat(howManyUsersAreInThisChallenges).isSorted();
            }
        });
    }

    static Stream<Arguments> generateConditionData() {
        return Stream.of(
                Arguments.of(ChallengeSearchCondition.builder()
                                .title("1").category(null).build(),
                        "popular",
                        List.of("제목입니다.1", "제목입니다.10")),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title(null).category(ChallengeCategory.WORKOUT.getDescription()).build(),
                        "popular",
                        List.of("제목입니다.6", "제목입니다.3", "제목입니다.4", "제목입니다.5", "제목입니다.7", "제목입니다.8",
                                "제목입니다.9", "제목입니다.10")),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title(null).category(ChallengeCategory.WORKOUT.getDescription()).build(),
                        "time",
                        List.of("제목입니다.10", "제목입니다.9", "제목입니다.8", "제목입니다.7", "제목입니다.6", "제목입니다.5",
                                "제목입니다.4", "제목입니다.3")),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title("1").category(ChallengeCategory.STUDY.getDescription()).build(),
                        "popular",
                        List.of("제목입니다.1"))
        );
    }

    @ParameterizedTest
    @MethodSource("generateConditionData")
    @DisplayName("챌린지 검색 조건으로 챌린지들을 찾는 테스트")
    void searchChallengesByCondition(ChallengeSearchCondition condition, String sortProperties, List<String> expect) {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(sortProperties));
        Page<ResponseChallenge> results = userChallengeRepository.searchChallengesByCondition(condition, pageRequest);

        for (ResponseChallenge result : results) {
            System.out.println("result = " + result);
        }

        assertAll(() -> {
            assertThat(results).extracting("title").containsExactlyElementsOf(expect);
            assertThat(results).extracting("challengeOwnerUser").extracting("userName")
                    .contains(savedUser.getUserName());
        });
    }
}
