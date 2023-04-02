package com.example.dailychallenge.service.users;

import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.user.UserFixture.createOtherUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.AuthorizationException;
import com.example.dailychallenge.exception.users.UserDuplicateNotCheck;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.repository.UserImgRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.ResponseChallengeByUserChallenge;
import com.example.dailychallenge.vo.challenge.ResponseInProgressChallenge;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Import({TestDataSetup.class})
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserImgRepository userImgRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${userImgLocation}")
    private String userImgLocation;

    @Autowired
    private TestDataSetup testDataSetup;


    public UserDto createUser(){
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    /** 프로필 이미지 추가한 후 **/
    private MultipartFile createMultipartFiles() {
        String path = userImgLocation+"/";
        String imageName = "editImage.jpg";
        MockMultipartFile multipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
        return multipartFile;
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveUserTest() {
        UserDto userDto = createUser();
        User savedUser = userService.saveUser(userDto,passwordEncoder);

        assertEquals(savedUser.getEmail(),userDto.getEmail());
        assertEquals(savedUser.getUserName(),userDto.getUserName());

        assertAll(
                () -> assertNotEquals(savedUser.getPassword(), userDto.getPassword()),
                () -> assertTrue(passwordEncoder.matches(userDto.getPassword(), savedUser.getPassword()))
        );
    }

    @Test
    @DisplayName("회원 정보 수정 테스트")
    public void updateUserTest() {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        MultipartFile multipartFile = createMultipartFiles();
        RequestUpdateUser requestUpdateUser = RequestUpdateUser.builder()
                .userName("editName")
                .info("editInfo")
                .build();

        userService.updateUser(savedUser, requestUpdateUser, multipartFile);

        User editUser = userService.findByEmail(savedUser.getEmail()).orElseThrow(UserNotFound::new);

        assertAll(() -> {
            assertEquals(editUser.getUserName(), requestUpdateUser.getUserName());
            assertEquals(editUser.getInfo(), requestUpdateUser.getInfo());
        });
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    public void success() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);

        userService.delete(savedUser);

        assertAll(() -> {
            assertTrue(userRepository.findById(savedUser.getId()).isEmpty());
            assertEquals(0, userRepository.count());
            assertEquals(0,userImgRepository.count());
        });
    }

    @Test
    @DisplayName("회원 중복 에러 테스트")
    public void duplicateUserTest() {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        assertThatThrownBy(() -> userService.validateDuplicateUser(savedUser.getEmail()))
                .isInstanceOf(UserDuplicateNotCheck.class)
                .hasMessage("아이디 중복체크를 해주세요.");
    }

    @Test
    @DisplayName("비밀번호 검증 테스트")
    public void checkUserPassword() {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);

        assertTrue(userService.checkPassword(savedUser.getId(), "1234", passwordEncoder));
        assertFalse(userService.checkPassword(savedUser.getId(), "12345", passwordEncoder));
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    public void changeUserPassword() {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        userService.changePassword(savedUser.getId(), "12345",passwordEncoder);

        assertAll(
                () -> assertTrue(passwordEncoder.matches("12345", savedUser.getPassword()))
        );
    }

    @Nested
    @DisplayName("회원 검증 테스트")
    class getValidateUser {
        @Test
        public void success() throws Exception {
            User savedUser = userService.saveUser(createUser(), passwordEncoder);
            String userEmil = savedUser.getEmail();
            Long userId = savedUser.getId();

            User validateUser = userService.getValidateUser(userEmil, userId);

            assertEquals(savedUser, validateUser);
        }

        @Test
        @DisplayName("EMAIL이 존재하지 않는 예외 발생")
        public void failByNotExistUser() {
            User savedUser = userService.saveUser(createUser(), passwordEncoder);
            String notExistUserEmail = savedUser.getEmail() + "error";
            Long userId = savedUser.getId();

            assertThatThrownBy(() -> userService.getValidateUser(notExistUserEmail, userId))
                    .isInstanceOf(UserNotFound.class)
                    .hasMessage("존재하지 않는 회원입니다.");
        }

        @Test
        @DisplayName("권한이 없는 경우 예외 발생")
        public void failByAuthorization() {
            User savedUser = userService.saveUser(createUser(), passwordEncoder);
            User otherSavedUser = userService.saveUser(createOtherUser(), passwordEncoder);
            String otherSavedUserEmail = otherSavedUser.getEmail();
            Long savedUserId = savedUser.getId();

            assertThatThrownBy(() -> userService.getValidateUser(otherSavedUserEmail, savedUserId))
                    .isInstanceOf(AuthorizationException.class)
                    .hasMessage("권한이 없습니다.");
        }
    }

    @Test
    @DisplayName("유저가 작성한 챌린지들 조회 테스트")
    void getChallengeByUserTest() {
        User user = userService.saveUser(createUser(), passwordEncoder);
        List<Challenge> challenges = new ArrayList<>();
        List<UserChallenge> userChallenges = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Challenge challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), user);
            UserChallenge userChallenge = testDataSetup.챌린지에_참가한다(challenge, user);
            Comment comment = testDataSetup.챌린지에_댓글을_단다(challenge, user);

            challenges.add(challenge);
            userChallenges.add(userChallenge);
            comments.add(comment);
        }
        Long userId = user.getId();

        List<ResponseChallengeByUserChallenge> result = userService.getChallengeByUser(userId);

        assertEquals(userId, result.get(0).getUserId());
        assertEquals(challenges.get(0).getId(), result.get(0).getChallengeId());
        assertEquals(challenges.get(0).getTitle(), result.get(0).getChallengeTitle());
        assertEquals(challenges.get(0).getContent(), result.get(0).getChallengeContent());
        assertEquals(userChallenges.get(0).getChallengeStatus(), result.get(0).getChallengeStatus());
        assertEquals(challenges.get(0).getCreated_at(), result.get(0).getCreatedAt());
        assertEquals(comments.get(0).getId(), result.get(0).getComments().get(0).getCommentId());
        assertEquals(comments.get(0).getContent(), result.get(0).getComments().get(0).getCommentContent());
        assertEquals(comments.get(0).getImgUrls(), result.get(0).getComments().get(0).getCommentImgs());
        assertEquals(comments.get(0).getMonthDayFormatCreatedAt(),
                result.get(0).getComments().get(0).getCommentCreatedAt());
    }

    @Test
    @DisplayName("유저가 참여한 챌린지들 조회 테스트")
    void getParticipateChallengeTest() {
        User user = userService.saveUser(createUser(), passwordEncoder);
        User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);

        List<Challenge> challenges = new ArrayList<>();
        List<UserChallenge> userChallenges = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Challenge otherUserChallenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), otherUser);
            testDataSetup.챌린지에_참가한다(otherUserChallenge, otherUser);
            UserChallenge userChallenge = testDataSetup.챌린지에_참가한다(otherUserChallenge, user);
            Comment comment = testDataSetup.챌린지에_댓글을_단다(otherUserChallenge, user);

            challenges.add(otherUserChallenge);
            userChallenges.add(userChallenge);
            comments.add(comment);
        }
        Long userId = user.getId();

        List<ResponseChallengeByUserChallenge> result = userService.getParticipateChallenge(userId);

        assertEquals(userId, result.get(0).getUserId());
        assertEquals(challenges.get(0).getId(), result.get(0).getChallengeId());
        assertEquals(challenges.get(0).getTitle(), result.get(0).getChallengeTitle());
        assertEquals(challenges.get(0).getContent(), result.get(0).getChallengeContent());
        assertEquals(userChallenges.get(0).getChallengeStatus(), result.get(0).getChallengeStatus());
        assertEquals(userChallenges.get(0).getCreated_at(), result.get(0).getCreatedAt());
        assertEquals(comments.get(0).getId(), result.get(0).getComments().get(0).getCommentId());
        assertEquals(comments.get(0).getContent(), result.get(0).getComments().get(0).getCommentContent());
        assertEquals(comments.get(0).getImgUrls(), result.get(0).getComments().get(0).getCommentImgs());
        assertEquals(comments.get(0).getMonthDayFormatCreatedAt(),
                result.get(0).getComments().get(0).getCommentCreatedAt());
    }

    @Test
    @DisplayName("유저가 진행중인 챌린지들 조회 테스트")
    void getInProgressChallengesTest() {
        User user = userService.saveUser(createUser(), passwordEncoder);
        User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);

        List<Challenge> challenges = new ArrayList<>();
        List<UserChallenge> userChallenges = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Challenge otherUserChallenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), otherUser);
            testDataSetup.챌린지에_참가한다(otherUserChallenge, otherUser);
            UserChallenge userChallenge = testDataSetup.챌린지에_참가한다(otherUserChallenge, user);
            Comment comment = testDataSetup.챌린지에_댓글을_단다(otherUserChallenge, user);

            challenges.add(otherUserChallenge);
            userChallenges.add(userChallenge);
            comments.add(comment);
        }
        Long userId = user.getId();

        List<ResponseInProgressChallenge> result = userService.getInProgressChallenges(userId);

        assertEquals(userId, result.get(0).getUserId());
        assertEquals(challenges.get(0).getId(), result.get(0).getChallengeId());
        assertEquals(challenges.get(0).getTitle(), result.get(0).getChallengeTitle());
        assertEquals(challenges.get(0).getContent(), result.get(0).getChallengeContent());
        assertEquals(userChallenges.get(0).getChallengeStatus(), result.get(0).getChallengeStatus());
        assertEquals(userChallenges.get(0).getCreated_at(), result.get(0).getCreatedAt());
        assertEquals(comments.get(0).getId(), result.get(0).getComments().get(0).getCommentId());
        assertEquals(comments.get(0).getContent(), result.get(0).getComments().get(0).getCommentContent());
        assertEquals(comments.get(0).getImgUrls(), result.get(0).getComments().get(0).getCommentImgs());
        assertEquals(comments.get(0).getMonthDayFormatCreatedAt(),
                result.get(0).getComments().get(0).getCommentCreatedAt());
        assertEquals(1L, result.get(0).getHowManyDaysInProgress());
    }

    @ParameterizedTest
    @CsvSource({
            "google, true",
            "kakao, true",
            " , false"
    })
    @DisplayName("소셜 유저인지 확인하는 테스트")
    void isSocialUserTest(String registrationId, boolean expect) {
        User user = User.builder()
                .email("test@test.com")
                .password("123")
                .userName("test")
                .registrationId(registrationId)
                .build();
        userRepository.save(user);

        boolean isSocialUser = userService.isSocialUser("test@test.com");

        assertEquals(expect, isSocialUser);
    }
}