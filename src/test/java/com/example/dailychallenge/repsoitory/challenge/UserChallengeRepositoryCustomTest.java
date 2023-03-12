package com.example.dailychallenge.repsoitory.challenge;

import static com.example.dailychallenge.entity.challenge.ChallengeCategory.ECONOMY;
import static com.example.dailychallenge.entity.challenge.ChallengeCategory.STUDY;
import static com.example.dailychallenge.entity.challenge.ChallengeCategory.WORKOUT;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.OVER_ONE_HOUR;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.WITHIN_TEN_MINUTES;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.INDOOR;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.OUTDOOR;
import static com.example.dailychallenge.entity.challenge.ChallengeStatus.PAUSE;
import static com.example.dailychallenge.entity.challenge.ChallengeStatus.TRYING;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createSpecificChallenge;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.createSpecificChallengeImgs;
import static com.example.dailychallenge.util.fixture.challengeHashtag.ChallengeHashtagFixture.createSpecificChallengeHashtags;
import static com.example.dailychallenge.util.fixture.hashtag.HashtagFixture.createSpecificHashtags;
import static com.example.dailychallenge.util.fixture.user.UserFixture.createSpecificUser;
import static com.example.dailychallenge.util.fixture.userChallenge.UserChallengeFixture.createSpecificUserChallenge;
import static java.util.Collections.emptyList;
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
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeHashtagRepository;
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.HashtagRepository;
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
    @Autowired
    private ChallengeHashtagRepository challengeHashtagRepository;
    @Autowired
    private HashtagRepository hashtagRepository;

    private User savedUser;
    private Challenge challenge1;

    @BeforeEach
    void beforeEach() throws InterruptedException {
        initData();
    }

    private void initData() throws InterruptedException {
        savedUser = saveUser("홍길동", EMAIL);

        challenge1 = saveChallenge("제목입니다.1", "내용입니다.1", STUDY, INDOOR, WITHIN_TEN_MINUTES, savedUser);
        saveChallengeImgs("imgUrl", "imgName", "oriImgName", challenge1, 2);
        saveChallengeParticipate(TRYING, savedUser, challenge1);
        saveChallengeHashtags(List.of("tag1", "tag2", "tag3"), challenge1);

        Challenge challenge2 = saveChallenge("제목입니다.2", "내용입니다.2", ECONOMY, OUTDOOR, OVER_ONE_HOUR, savedUser);
        saveChallengeParticipate(PAUSE, savedUser, challenge2);

        Challenge challenge6 = null;

        for (int i = 3; i <= 10; i++) {
            Thread.sleep(1);
            Challenge challenge = saveChallenge("제목입니다." + i, "내용입니다." + i, WORKOUT, INDOOR,
                    WITHIN_TEN_MINUTES, savedUser);
            saveChallengeParticipate(TRYING, savedUser, challenge);

            if (i == 6) {
                challenge6 = challenge;
            }
        }

        for (int i = 1; i <= 8; i++) {
            User user = saveUser("홍길동" + i, i + "@test.com");
            if (i == 1) {
                saveChallengeParticipate(TRYING, user, challenge1);
            }
            if (2 <= i && i <= 5) {
                saveChallengeParticipate(PAUSE, user, challenge2);
            }
            if (i == 6) {
                saveChallengeParticipate(TRYING, user, challenge6);
            }
        }
    }

    private User saveUser(String name, String email) {
        User user = createSpecificUser(name, email);
        userRepository.save(user);

        return user;
    }

    private Challenge saveChallenge(String title, String content, ChallengeCategory challengeCategory,
                                    ChallengeLocation challengeLocation, ChallengeDuration challengeDuration, User user) {

        Challenge challenge = createSpecificChallenge(title, content, challengeCategory, challengeLocation,
                challengeDuration, user);
        challengeRepository.save(challenge);

        return challenge;
    }

    private void saveChallengeImgs(String imgUrl, String imgName, String oriImgName, Challenge challenge,
                                   int repeatCount) {
        List<ChallengeImg> specificChallengeImgs = createSpecificChallengeImgs(imgUrl, imgName, oriImgName, challenge,
                repeatCount);
        challengeImgRepository.saveAll(specificChallengeImgs);
    }

    private void saveChallengeParticipate(ChallengeStatus challengeStatus, User user, Challenge challenge) {
        UserChallenge userChallenge = createSpecificUserChallenge(challengeStatus, user, challenge);
        userChallengeRepository.save(userChallenge);
    }

    private void saveChallengeHashtags(List<String> hashtagDto, Challenge challenge) {
        List<Hashtag> hashtags = createSpecificHashtags(hashtagDto);
        hashtagRepository.saveAll(hashtags);

        List<ChallengeHashtag> challengeHashtags = createSpecificChallengeHashtags(hashtags, challenge);
        challengeHashtagRepository.saveAll(challengeHashtags);
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
            User otherUser = saveUser("김철수", "a@a.com");
            Long otherUserId = otherUser.getId();

            Optional<UserChallenge> findUserChallenge = userChallengeRepository.findByChallengeIdAndUserId(
                    challenge1Id, otherUserId);

            assertTrue(findUserChallenge.isEmpty());
        }
    }

    @Nested
    @DisplayName("모든 챌린지들을 찾는 테스트")
    class searchAllChallenges {
        @Test
        @DisplayName("인기순으로 정렬")
        void sortByPopular() {
            PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("popular"));
            Page<ResponseChallenge> results = userChallengeRepository.searchAllChallenges(pageRequest);

            for (ResponseChallenge result : results) {
                System.out.println("result = " + result);
            }

            assertAll(() -> {
                assertThat(results).extracting("title").containsExactlyElementsOf(
                        List.of("제목입니다.2", "제목입니다.1", "제목입니다.6", "제목입니다.3", "제목입니다.4", "제목입니다.5",
                                "제목입니다.7", "제목입니다.8", "제목입니다.9", "제목입니다.10"));
                assertThat(results).extracting("content").containsExactlyElementsOf(
                        List.of("내용입니다.2", "내용입니다.1", "내용입니다.6", "내용입니다.3", "내용입니다.4", "내용입니다.5",
                                "내용입니다.7", "내용입니다.8", "내용입니다.9", "내용입니다.10"));
                assertThat(results).extracting("challengeCategory").containsExactlyElementsOf(
                        List.of("경제", "공부", "운동", "운동", "운동", "운동", "운동", "운동", "운동", "운동"));
                assertThat(results).extracting("created_at").isNotEmpty();
                assertThat(results).extracting("challengeImgUrls").containsExactlyElementsOf(
                        List.of(emptyList(), List.of("imgUrl", "imgUrl"), emptyList(), emptyList(), emptyList(), emptyList(),
                                emptyList(), emptyList(), emptyList(), emptyList()));
                assertThat(results).extracting("challengeHashtags").containsExactlyElementsOf(
                        List.of(emptyList(), List.of("tag1", "tag2", "tag3"), emptyList(), emptyList(), emptyList(), emptyList(),
                                emptyList(), emptyList(), emptyList(), emptyList()));
                assertThat(results).extracting("howManyUsersAreInThisChallenge")
                        .containsExactlyElementsOf(List.of(5L, 2L, 2L, 1L, 1L, 1L, 1L, 1L, 1L, 1L));
                List<Long> howManyUsersAreInThisChallenges = results.getContent().stream()
                        .map(ResponseChallenge::getHowManyUsersAreInThisChallenge)
                        .sorted()
                        .collect(Collectors.toList());
                assertThat(howManyUsersAreInThisChallenges).isSorted();
                assertThat(results).extracting("challengeOwnerUser").extracting("userName")
                        .contains(savedUser.getUserName());
            });
        }

        @Test
        @DisplayName("생성순으로 정렬")
        void sortByTime() {
            PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("time"));
            Page<ResponseChallenge> results = userChallengeRepository.searchAllChallenges(pageRequest);

            for (ResponseChallenge result : results) {
                System.out.println("result = " + result);
            }

            assertAll(() -> {
                assertThat(results).extracting("title").containsExactlyElementsOf(
                        List.of("제목입니다.10", "제목입니다.9", "제목입니다.8", "제목입니다.7", "제목입니다.6", "제목입니다.5",
                                "제목입니다.4", "제목입니다.3", "제목입니다.2", "제목입니다.1"));
                assertThat(results).extracting("content").containsExactlyElementsOf(
                        List.of("내용입니다.10", "내용입니다.9", "내용입니다.8", "내용입니다.7", "내용입니다.6", "내용입니다.5",
                                "내용입니다.4", "내용입니다.3", "내용입니다.2", "내용입니다.1"));
                assertThat(results).extracting("challengeCategory").containsExactlyElementsOf(
                        List.of("운동", "운동", "운동", "운동", "운동", "운동", "운동", "운동", "경제", "공부"));
                assertThat(results).extracting("created_at").isNotEmpty();
                List<String> createdAts = results.getContent().stream()
                        .map(ResponseChallenge::getCreated_at)
                        .sorted()
                        .collect(Collectors.toList());
                assertThat(createdAts).isSorted();
                assertThat(results).extracting("challengeImgUrls").containsExactlyElementsOf(
                        List.of(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
                                emptyList(), emptyList(), emptyList(), List.of("imgUrl", "imgUrl")));
                assertThat(results).extracting("challengeHashtags").containsExactlyElementsOf(
                        List.of(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
                                emptyList(), emptyList(), emptyList(), List.of("tag1", "tag2", "tag3")));
                assertThat(results).extracting("howManyUsersAreInThisChallenge")
                        .containsExactlyElementsOf(List.of(1L, 1L, 1L, 1L, 2L, 1L, 1L, 1L, 5L, 2L));
                assertThat(results).extracting("challengeOwnerUser").extracting("userName")
                        .contains(savedUser.getUserName());
            });
        }
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
