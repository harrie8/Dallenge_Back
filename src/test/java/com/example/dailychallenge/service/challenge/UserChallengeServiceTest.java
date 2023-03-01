package com.example.dailychallenge.service.challenge;

import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallenge;
import static com.example.dailychallenge.util.fixture.user.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.user.UserFixture.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.userChallenge.UserChallengeDuplicate;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ServiceTest;
import com.example.dailychallenge.vo.ResponseChallengeByUserChallenge;
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
    private UserService userService;
    @Autowired
    private ChallengeRepository challengeRepository;

    private User savedUser;
    private Challenge challenge;
    private User otherUser;

    @BeforeEach
    void beforeEach() throws Exception {
        savedUser = userService.saveUser(createUser(), passwordEncoder);
        challenge = createChallenge();
        challenge.setUser(savedUser);
        challengeRepository.save(createChallenge());
        otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
    }

    @Test
    @DisplayName("유저 챌린지 생성 테스트 - 연관관계 테스트")
    void createUserChallenge() {
        UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, savedUser);

        assertEquals(ChallengeStatus.TRYING, userChallenge.getChallengeStatus());
        assertEquals(challenge, userChallenge.getChallenge());
        assertEquals(savedUser, userChallenge.getUsers());
    }

    @Nested
    @DisplayName("유저가 챌린지에 참가하는 테스트")
    class saveUserChallenge {
        @Test
        void success() {
            UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, savedUser);

            assertEquals(ChallengeStatus.TRYING, userChallenge.getChallengeStatus());
            assertEquals(challenge, userChallenge.getChallenge());
            assertEquals(savedUser, userChallenge.getUsers());
        }

        @Test
        @DisplayName("이미 존재하는 챌린지에 다른 유저가 참가하는 테스트")
        void successByOtherUser() {
            userChallengeService.saveUserChallenge(challenge, savedUser);

            UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, otherUser);

            assertEquals(ChallengeStatus.TRYING, userChallenge.getChallengeStatus());
            assertEquals(challenge, userChallenge.getChallenge());
            assertEquals(otherUser, userChallenge.getUsers());
        }

        @Test
        @DisplayName("중복된 챌린지 참가")
        void failByDuplicate() {
            userChallengeService.saveUserChallenge(challenge, savedUser);

            Throwable exception = assertThrows(UserChallengeDuplicate.class,
                    () -> userChallengeService.saveUserChallenge(challenge, savedUser));
            assertEquals("이미 참가한 챌린지입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("챌린지 달성")
        void succeedInChallenge(){
            userChallengeService.saveUserChallenge(challenge, savedUser);
            UserChallenge userChallenge = userChallengeService.succeedInChallenge(savedUser.getId(), challenge.getId());
            assertEquals(userChallenge.getChallengeStatus().getDescription(),"성공");
        }

        @Test
        @DisplayName("오늘 수행(성공)한 챌린지 조회")
        void getTodayUserChallenge(){
            userChallengeService.saveUserChallenge(challenge, savedUser);
            userChallengeService.succeedInChallenge(savedUser.getId(), challenge.getId());

            List<ResponseChallengeByUserChallenge> userChallenges = userChallengeService.getTodayUserChallenge(savedUser.getId());
            assertEquals(userChallenges.get(0).getChallengeId(),challenge.getId());
            assertEquals(userChallenges.get(0).getChallengeTitle(),challenge.getTitle());
            assertEquals(userChallenges.get(0).getChallengeContent(),challenge.getContent());
            assertEquals(userChallenges.get(0).getChallengeStatus().getDescription(),"성공");
        }

    }
    @Test
    @DisplayName("참여한 챌린지에서 나가는 테스트")
    void challengeLeaveTest() {
        UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, savedUser);

        userChallengeService.challengeLeave(challenge.getId(), savedUser.getId());

        assertTrue(userChallengeRepository.findById(userChallenge.getId()).isEmpty());
    }
}
