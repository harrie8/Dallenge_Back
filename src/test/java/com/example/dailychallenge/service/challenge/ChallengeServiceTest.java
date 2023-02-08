package com.example.dailychallenge.service.challenge;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.AuthorizationException;
import com.example.dailychallenge.exception.challenge.ChallengeCategoryNotFound;
import com.example.dailychallenge.exception.challenge.ChallengeNotFound;
import com.example.dailychallenge.repository.ChallengeHashtagRepository;
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.repository.HashtagRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.vo.RequestUpdateChallenge;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class ChallengeServiceTest {

    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${userImgLocation}")
    private String challengeImgLocation;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private UserChallengeRepository userChallengeRepository;
    @Autowired
    private ChallengeImgRepository challengeImgRepository;
    @Autowired
    private HashtagRepository hashtagRepository;
    @Autowired
    private ChallengeHashtagRepository challengeHashtagRepository;
    @Autowired
    private CommentRepository commentRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private User savedUser;
    private ChallengeDto challengeDto;

    @BeforeEach
    void beforeEach() {
        try {
            savedUser = userService.saveUser(createUser(), passwordEncoder);
            challengeDto = createChallengeDto();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    MultipartFile createMultipartFiles() {
        String path = challengeImgLocation +"/";
        String imageName = "challengeImage.jpg";
        return new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
    }

    private ChallengeDto createChallengeDto() {
        return ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
    }

    private List<MultipartFile> createChallengeImgFiles() {
        List<MultipartFile> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String path = challengeImgLocation +"/";
            String imageName = "challengeImage" + i + ".jpg";
            result.add(new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4}));
        }
        return result;
    }

    @Nested
    @DisplayName("챌린지 생성 테스트")
    class createChallengeTest {
        @Test
        @DisplayName("순수 챌린지 생성 테스트")
        void success() {
            Challenge challenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);

            assertEquals(challengeDto.getTitle(), challenge.getTitle());
            assertEquals(challengeDto.getContent(), challenge.getContent());
            assertEquals(challengeDto.getChallengeCategory(), challenge.getChallengeCategory().getDescription());
            assertEquals(challengeDto.getChallengeLocation(), challenge.getChallengeLocation().getDescription());
            assertEquals(challengeDto.getChallengeDuration(), challenge.getChallengeDuration().getDescription());
            assertEquals(savedUser, challenge.getUsers());
        }

        @Test
        @DisplayName("존재하지 않는 카테고리로 챌린지를 생성하려고 하면 예외 발생")
        void failByCategoryNotFound() {
            ChallengeDto challengeDto = ChallengeDto.builder()
                    .title("제목입니다.")
                    .content("내용입니다.")
                    .challengeCategory("error")
                    .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                    .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                    .build();

            Throwable exception = assertThrows(ChallengeCategoryNotFound.class,
                    () -> challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser));
            assertEquals("존재하지 않는 챌린지 카테고리입니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("특정 챌린지 조회 테스트")
    class findById {
        @Test
        @DisplayName("존재하는 특정 챌린지 조회 테스트")
        void success() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            Long challengeId = savedChallenge.getId();

            Challenge findChallenge = challengeService.findById(challengeId);

            assertEquals(savedChallenge, findChallenge);
        }

        @Test
        @DisplayName("존재하지 않는 특정 챌린지를 조회하려고 하면 예외 발생")
        void failByChallengeNotFound() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            Long challengeId = savedChallenge.getId();

            Throwable exception = assertThrows(ChallengeNotFound.class,
                    () -> challengeService.findById(challengeId + 100L));
            assertEquals("챌린지를 찾을 수 없습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("챌린지 수정 테스트")
    class update {
        @Test
        @DisplayName("순수 챌린지 수정 테스트")
        @Disabled
        void success() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            RequestUpdateChallenge requestUpdateChallenge = RequestUpdateChallenge.builder()
                    .title("수정된 제목")
                    .content("수정된 내용")
                    .challengeCategory(ChallengeCategory.WORKOUT.getDescription())
                    .build();
            List<MultipartFile> updateChallengeImgFiles = List.of(createMultipartFiles(), createMultipartFiles());

            entityManager.flush();
            entityManager.clear();

            Long challengeId = savedChallenge.getId();
            Challenge updatedChallenge = challengeService.updateChallenge(challengeId, requestUpdateChallenge,
                    updateChallengeImgFiles);

            assertAll(
                    () -> assertEquals(requestUpdateChallenge.getTitle(), updatedChallenge.getTitle()),
                    () -> assertEquals(requestUpdateChallenge.getContent(), updatedChallenge.getContent()),
                    () -> assertEquals(requestUpdateChallenge.getChallengeCategory(), updatedChallenge.getChallengeCategory().getDescription()),
                    () -> assertNotEquals(savedChallenge.getUpdated_at(), updatedChallenge.getUpdated_at())
//                    () -> assertEquals(updateChallengeImgFiles.get(0).getOriginalFilename(), updatedChallenge.getImgUrls().get(0))
            );
        }

        @Test
        @DisplayName("존재하지 않는 챌린지를 수정하려고 하면 예외 발생")
        @Disabled
        void fail() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            RequestUpdateChallenge requestUpdateChallenge = RequestUpdateChallenge.builder()
                    .title("수정된 제목")
                    .content("수정된 내용")
                    .challengeCategory(ChallengeCategory.WORKOUT.getDescription())
                    .build();
            List<MultipartFile> updateChallengeImgFiles = List.of(createMultipartFiles(), createMultipartFiles());
//        List<String> updateChallengeHashtags = List.of("editTag1", "editTag2");

            entityManager.flush();
            entityManager.clear();

            Long notFoundChallengeId = savedChallenge.getId() + 100L;
            Throwable exception = assertThrows(ChallengeNotFound.class,
                    () -> challengeService.updateChallenge(notFoundChallengeId, requestUpdateChallenge,
                            updateChallengeImgFiles));
            assertEquals("챌린지를 찾을 수 없습니다.", exception.getMessage());
        }

        @DisplayName("챌린지 카테고리 값이 올바르지 않다면 예외 발생")
        @Disabled
        @Test
        void failByCategoryNotFound() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            RequestUpdateChallenge requestUpdateChallenge = RequestUpdateChallenge.builder()
                    .title("title")
                    .content("content")
                    .challengeCategory("fail")
                    .build();
            List<MultipartFile> updateChallengeImgFiles = List.of(createMultipartFiles(), createMultipartFiles());
//        List<String> updateChallengeHashtags = List.of("editTag1", "editTag2");

            entityManager.flush();
            entityManager.clear();

            Long challengeId = savedChallenge.getId();
            Throwable exception = assertThrows(ChallengeCategoryNotFound.class,
                    () -> challengeService.updateChallenge(challengeId, requestUpdateChallenge,
                            updateChallengeImgFiles));
            assertEquals("존재하지 않는 챌린지 카테고리입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("챌린지의 연관관계 엔티티들을 수정 테스트")
        @Disabled
        void successWithPersistence() throws InterruptedException {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            RequestUpdateChallenge requestUpdateChallenge = RequestUpdateChallenge.builder()
                    .title("수정된 제목")
                    .content("수정된 내용")
                    .challengeCategory(ChallengeCategory.WORKOUT.getDescription())
                    .build();
            List<MultipartFile> updateChallengeImgFiles = List.of(createMultipartFiles(), createMultipartFiles());
//        List<String> updateChallengeHashtags = List.of("editTag1", "editTag2");

            Thread.sleep(1000L);
            Long challengeId = savedChallenge.getId();
            challengeService.updateChallenge(challengeId, requestUpdateChallenge, updateChallengeImgFiles);
            entityManager.flush();
            entityManager.clear();
            Challenge editChallenge = challengeRepository.findById(challengeId).orElseThrow();

            assertAll(
                    () -> assertEquals(requestUpdateChallenge.getTitle(), editChallenge.getTitle()),
                    () -> assertEquals(requestUpdateChallenge.getContent(), editChallenge.getContent()),
                    () -> assertEquals(requestUpdateChallenge.getChallengeCategory(), editChallenge.getChallengeCategory().getDescription()),
                    () -> assertNotEquals(savedChallenge.getUpdated_at(), editChallenge.getUpdated_at()),
                    () -> assertEquals(updateChallengeImgFiles.get(0).getOriginalFilename(), editChallenge.getImgUrls().get(0))
            );
        }
    }

    @Nested
    @DisplayName("챌린지 삭제 테스트")
    class delete {

        @Test
        @DisplayName("순수 챌린지 삭제 테스트")
        void success() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            Long challengeId = savedChallenge.getId();

            challengeService.deleteChallenge(challengeId, savedUser);

            assertTrue(challengeRepository.findById(challengeId).isEmpty());
        }

        @Test
        @DisplayName("존재하지 않는 챌린지를 삭제하려고 하면 예외 발생")
        void failByChallengeNotFound() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            Long notFoundChallengeId = savedChallenge.getId() + 100L;

            Throwable exception = assertThrows(ChallengeNotFound.class,
                    () -> challengeService.deleteChallenge(notFoundChallengeId, savedUser));
            assertEquals("챌린지를 찾을 수 없습니다.", exception.getMessage());
        }

        @DisplayName("챌린지 소유자가 아닌 유저가 챌린지를 삭제하면 예외 발생")
        @Test
        void failByAuthorization() throws Exception {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            Long challengeId = savedChallenge.getId();
            UserDto userDto = new UserDto();
            userDto.setEmail("a@a.com");
            userDto.setUserName("김철수");
            userDto.setInfo("aInfo");
            userDto.setPassword("1234");
            User otherUser = userService.saveUser(userDto, passwordEncoder);

            Throwable exception = assertThrows(AuthorizationException.class,
                    () -> challengeService.deleteChallenge(challengeId, otherUser));
            assertEquals("권한이 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("챌린지를 삭제하면 연관관계 엔티티들도 삭제되는 테스트")
        void successWithPersistence() {
            List<MultipartFile> challengeImgFiles = createChallengeImgFiles();
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, challengeImgFiles, savedUser);

            UserChallenge userChallenge = UserChallenge.builder()
                    .challengeStatus(ChallengeStatus.TRYING)
                    .challenge(savedChallenge)
                    .users(savedUser)
                    .build();
            UserChallenge savedUserChallenge = userChallengeRepository.save(userChallenge);

            Comment comment = Comment.builder()
                    .content("content")
                    .build();
            comment.saveCommentChallenge(savedChallenge);
            Comment savedComment = commentRepository.save(comment);

            Hashtag hashtag = Hashtag.builder()
                    .content("content")
                    .build();
            Hashtag savedHashTag = hashtagRepository.save(hashtag);

            ChallengeHashtag challengeHashtag = ChallengeHashtag.builder()
                    .hashtag(hashtag)
                    .challenge(savedChallenge)
                    .build();
            savedChallenge.getChallengeHashtags().add(challengeHashtag);
            hashtag.getChallengeHashtags().add(challengeHashtag);
            ChallengeHashtag savedChallengeHashtag = challengeHashtagRepository.save(challengeHashtag);

            assertAll(
                    () -> assertEquals(challengeImgFiles.size(), challengeImgRepository.count()),
                    () -> assertEquals(savedUserChallenge,
                            userChallengeRepository.findById(savedUserChallenge.getId()).orElseThrow()),
                    () -> assertEquals(savedChallenge, challengeRepository.findById(savedChallenge.getId()).orElseThrow()),
                    () -> assertEquals(savedComment, commentRepository.findById(savedComment.getId()).orElseThrow()),
                    () -> assertEquals(savedHashTag, hashtagRepository.findById(savedHashTag.getId()).orElseThrow()),
                    () -> assertEquals(savedChallengeHashtag,
                            challengeHashtagRepository.findById(savedChallengeHashtag.getId()).orElseThrow())
            );

            entityManager.flush();
            entityManager.clear();

            Long challengeId = savedChallenge.getId();
            challengeService.deleteChallenge(challengeId, savedUser);

            assertAll(
                    () -> assertEquals(0L, challengeImgRepository.count()),
                    () -> assertTrue(userChallengeRepository.findById(savedUserChallenge.getId()).isEmpty()),
                    () -> assertTrue(challengeRepository.findById(savedChallenge.getId()).isEmpty()),
                    () -> assertTrue(commentRepository.findById(savedComment.getId()).isEmpty()),
//                    () -> assertTrue(hashtagRepository.findById(savedHashTag.getId()).isEmpty()), // 챌린지 해시태그를 삭제해도 챌린지는 존재한다
                    () -> assertTrue(challengeHashtagRepository.findById(savedChallengeHashtag.getId()).isEmpty())
            );
        }
    }
}
