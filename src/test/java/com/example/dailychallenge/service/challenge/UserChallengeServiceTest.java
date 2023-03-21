package com.example.dailychallenge.service.challenge;

import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallenge;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_EMAIL;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.userChallenge.ChallengeSuccessDuplicate;
import com.example.dailychallenge.exception.userChallenge.UserChallengeDuplicate;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.util.ServiceTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import com.example.dailychallenge.vo.ResponseChallengeByUserChallenge;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserChallengeServiceTest extends ServiceTest {

    @Autowired
    private UserChallengeService userChallengeService;
    @Autowired
    private UserChallengeRepository userChallengeRepository;
    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private Challenge challenge;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        challenge = createChallenge();
        challenge.setUser(user);
        challenge = challengeRepository.save(challenge);
    }

    @Test
    @DisplayName("유저 챌린지 생성 테스트 - 연관관계 테스트")
    void createUserChallenge() {
        UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, user);

        assertEquals(ChallengeStatus.TRYING, userChallenge.getChallengeStatus());
        assertEquals(challenge, userChallenge.getChallenge());
        assertEquals(user, userChallenge.getUsers());
    }

    @Nested
    @DisplayName("유저가 챌린지에 참가하는 테스트")
    class saveUserChallenge {
        @Test
        void success() {
            UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, user);

            assertEquals(ChallengeStatus.TRYING, userChallenge.getChallengeStatus());
            assertEquals(challenge, userChallenge.getChallenge());
            assertEquals(user, userChallenge.getUsers());
        }

        @Test
        @DisplayName("이미 존재하는 챌린지에 다른 유저가 참가하는 테스트")
        void successByOtherUser() {
            User otherUser = testDataSetup.saveUser(OTHER_USERNAME, OTHER_EMAIL, PASSWORD);
            userChallengeService.saveUserChallenge(challenge, user);

            UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, otherUser);

            assertEquals(ChallengeStatus.TRYING, userChallenge.getChallengeStatus());
            assertEquals(challenge, userChallenge.getChallenge());
            assertEquals(otherUser, userChallenge.getUsers());
        }

        @Test
        @DisplayName("중복된 챌린지 참가")
        void failByDuplicate() {
            userChallengeService.saveUserChallenge(challenge, user);

            Throwable exception = assertThrows(UserChallengeDuplicate.class,
                    () -> userChallengeService.saveUserChallenge(challenge, user));
            assertEquals("이미 참가한 챌린지입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("챌린지 달성")
        void succeedInChallenge(){
            userChallengeService.saveUserChallenge(challenge, user);
            UserChallenge userChallenge = userChallengeService.succeedInChallenge(user.getId(), challenge.getId());
            assertEquals(userChallenge.getChallengeStatus().getDescription(),"성공");
        }

        @Test
        @DisplayName("이미 달성한 챌린지를 달성하려고 하면 오류 발생")
        void failByDuplicateSucceedInChallenge(){
            userChallengeService.saveUserChallenge(challenge, user);
            userChallengeService.succeedInChallenge(user.getId(), challenge.getId());

            Throwable exception = assertThrows(ChallengeSuccessDuplicate.class,
                    () -> userChallengeService.succeedInChallenge(user.getId(), challenge.getId()));
            assertEquals("이미 달성한 챌린지입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("챌린지 중지")
        void pauseChallenge(){
            userChallengeService.saveUserChallenge(challenge, user);
            UserChallenge userChallenge = userChallengeService.pauseChallenge(user.getId(), challenge.getId());
            assertEquals(userChallenge.getChallengeStatus().getDescription(),"중지");
        }

        @Test
        @DisplayName("오늘 수행(성공)한 챌린지 조회")
        void getTodayUserChallenge(){
            userChallengeService.saveUserChallenge(challenge, user);
            userChallengeService.succeedInChallenge(user.getId(), challenge.getId());

            List<ResponseChallengeByUserChallenge> userChallenges = userChallengeService.getTodayUserChallenge(user.getId());
            assertEquals(userChallenges.get(0).getUserId(),user.getId());
            assertEquals(userChallenges.get(0).getChallengeId(),challenge.getId());
            assertEquals(userChallenges.get(0).getChallengeTitle(),challenge.getTitle());
            assertEquals(userChallenges.get(0).getChallengeContent(),challenge.getContent());
            assertEquals(userChallenges.get(0).getChallengeStatus().getDescription(),"성공");
            assertNotNull(userChallenges.get(0).getCreatedAt());
        }

    }
    @Test
    @DisplayName("참여한 챌린지에서 나가는 테스트")
    void challengeLeaveTest() {
        UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, user);

        userChallengeService.challengeLeave(challenge.getId(), user.getId());

        assertTrue(userChallengeRepository.findById(userChallenge.getId()).isEmpty());
    }

    @Test
    @DisplayName("챌린지를 달성하면 일주일 동안 챌린지 달성을 변경한다")
    void succeedInChallengeWithUpdateWeeklyAchievement(){
        userChallengeService.saveUserChallenge(challenge, user);

        UserChallenge userChallenge = userChallengeService.succeedInChallenge(user.getId(), challenge.getId());

        String weeklyAchievement = userChallenge.getWeeklyAchievement();
        String[] split = weeklyAchievement.split(",");
        int todayNumber = LocalDate.now().getDayOfWeek().getValue() - 1;
        for (int i = 0; i < split.length; i++) {
            String actual = split[i];
            if (i == todayNumber) {
                assertEquals("true", actual);
                continue;
            }
            assertEquals("false", actual);
        }
    }
}
