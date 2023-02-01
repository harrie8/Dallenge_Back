package com.example.dailychallenge.repsoitory.challenge;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.vo.ResponseChallenge;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserChallengeRepositoryCustomTest {

    @Autowired
    private UserChallengeRepository userChallengeRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("인기 많은 순서대로 모든 챌리지 찾는 테스트")
    void searchAllChallengesByPopular() {
        User savedUser = User.builder()
                .userName("홍길동")
                .email("test@test.com")
                .password("1234")
                .build();
        userRepository.save(savedUser);
        Challenge challenge1 = Challenge.builder()
                .title("제목입니다.1")
                .content("내용입니다.1")
                .challengeCategory(ChallengeCategory.STUDY)
                .challengeLocation(ChallengeLocation.INDOOR)
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
                .build();
        challenge1.setUser(savedUser);
        Challenge challenge2 = Challenge.builder()
                .title("제목입니다.2")
                .content("내용입니다.2")
                .challengeCategory(ChallengeCategory.ECONOMY)
                .challengeLocation(ChallengeLocation.OUTDOOR)
                .challengeDuration(ChallengeDuration.OVER_ONE_HOUR)
                .build();
        challenge2.setUser(savedUser);
        challengeRepository.save(challenge1);
        challengeRepository.save(challenge2);
        UserChallenge userChallenge = UserChallenge.builder()
                .challengeStatus(ChallengeStatus.TRYING)
                .users(savedUser)
                .challenge(challenge1)
                .build();
        userChallengeRepository.save(userChallenge);
        userChallenge = UserChallenge.builder()
                .challengeStatus(ChallengeStatus.PAUSE)
                .users(savedUser)
                .challenge(challenge2)
                .build();
        userChallengeRepository.save(userChallenge);
        for (int i = 1; i <= 8; i++) {
            User user = User.builder()
                    .userName("홍길동" + i)
                    .email(i + "@test.com")
                    .password("1234")
                    .build();
            userRepository.save(user);
            if (i == 1) {
                userChallenge = UserChallenge.builder()
                        .challengeStatus(ChallengeStatus.TRYING)
                        .users(user)
                        .challenge(challenge1)
                        .build();
                userChallengeRepository.save(userChallenge);
            }
            if (2 <= i && i <= 5) {
                userChallenge = UserChallenge.builder()
                        .challengeStatus(ChallengeStatus.PAUSE)
                        .users(user)
                        .challenge(challenge2)
                        .build();
                userChallengeRepository.save(userChallenge);
            }
        }

        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<ResponseChallenge> results = userChallengeRepository.searchAllChallengesSortByPopularWithPaging(pageRequest);

        assertThat(results).extracting("title").containsExactly("제목입니다.2", "제목입니다.1");
        assertThat(results).extracting("howManyUsersAreInThisChallenge").containsExactly(5L, 2L);
        assertThat(results).extracting("challengeOwnerUser").extracting("userName").containsExactly("홍길동", "홍길동");
    }

    static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of("1", null, List.of("제목입니다.1", "제목입니다.10")),
                Arguments.of(null, "경제", List.of("제목입니다.6", "제목입니다.7", "제목입니다.8", "제목입니다.9", "제목입니다.10"))
        );
    }

    @ParameterizedTest
    @MethodSource("generateData")
    @DisplayName("이름으로 챌리지들을 찾고 인기순으로 정렬하는 테스트")
    void searchChallengesByConditionByPopular(String title, String category, List<String> expect) {
        User user = User.builder()
                .userName("홍길동")
                .email("test@test.com")
                .password("1234")
                .build();
        userRepository.save(user);
        for (int i = 1; i <= 10; i++) {
            ChallengeCategory challengeCategory = ChallengeCategory.STUDY;
            if (i >= 6) {
                challengeCategory = ChallengeCategory.ECONOMY;
            }
            Challenge challenge = Challenge.builder()
                    .title("제목입니다." + i)
                    .content("내용입니다." + i)
                    .challengeCategory(challengeCategory)
                    .challengeLocation(ChallengeLocation.INDOOR)
                    .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
                    .build();
            challenge.setUser(user);
            challengeRepository.save(challenge);
            UserChallenge userChallenge = UserChallenge.builder()
                    .challengeStatus(ChallengeStatus.TRYING)
                    .users(user)
                    .challenge(challenge)
                    .build();
            userChallengeRepository.save(userChallenge);
        }

        ChallengeSearchCondition condition = ChallengeSearchCondition.builder()
                .title(title)
                .category(category)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<ResponseChallenge> results = userChallengeRepository.searchChallengesByConditionSortByPopularWithPaging(
                condition, pageRequest);

        assertThat(results).extracting("title").containsExactlyElementsOf(expect);
        assertThat(results).extracting("challengeOwnerUser").extracting("userName").contains(user.getUserName());
    }
}
