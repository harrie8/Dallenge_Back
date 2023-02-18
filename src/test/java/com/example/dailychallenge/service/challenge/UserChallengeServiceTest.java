package com.example.dailychallenge.service.challenge;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallenge;
import static com.example.dailychallenge.util.fixture.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.userChallenge.UserChallengeDuplicate;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserChallengeServiceTest extends ServiceTest {

    @Autowired
    private UserChallengeService userChallengeService;
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
            assertTrue(userChallenge.isSuccess());
        }

    }
}
