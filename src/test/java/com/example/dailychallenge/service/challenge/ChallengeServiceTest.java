package com.example.dailychallenge.service.challenge;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.updateChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.assertj.core.api.Assertions.assertThat;
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
import com.example.dailychallenge.entity.challenge.ChallengeImg;
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
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ServiceTest;
import com.example.dailychallenge.vo.challenge.RequestUpdateChallenge;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class ChallengeServiceTest extends ServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
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
    void beforeEach() throws Exception {
        savedUser = userService.saveUser(createUser(), passwordEncoder);
        challengeDto = createChallengeDto();
    }

    @Nested
    @DisplayName("챌린지 생성 테스트")
    class createChallengeTest {
        @Test
        void success() {
            Challenge challenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);

           assertAll(() -> {
               assertEquals(challengeDto.getTitle(), challenge.getTitle());
               assertEquals(challengeDto.getContent(), challenge.getContent());
               assertEquals(challengeDto.getChallengeCategory(), challenge.getChallengeCategory().getDescription());
               assertEquals(challengeDto.getChallengeLocation(), challenge.getChallengeLocation().getDescription());
               assertEquals(challengeDto.getChallengeDuration(), challenge.getChallengeDuration().getDescription());
               assertEquals(savedUser, challenge.getUsers());
               assertThat(challenge.getChallengeImgs()).extracting("oriImgName")
                       .containsExactly("challengeImage0.jpg", "challengeImage1.jpg", "challengeImage2.jpg");
           });
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
    @DisplayName("특정 챌린지 조회 테스트")
    class searchById {
        @Test
        @DisplayName("존재하는 특정 챌린지 조회 테스트")
        void success() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            UserChallenge userChallenge = UserChallenge.builder()
                    .challengeStatus(ChallengeStatus.TRYING)
                    .challenge(savedChallenge)
                    .users(savedUser)
                    .build();
            userChallengeRepository.save(userChallenge);
            Long challengeId = savedChallenge.getId();

            ResponseChallenge responseChallenge = challengeService.searchById(challengeId);

            assertAll(() -> {
                assertEquals(savedChallenge.getId(), responseChallenge.getId());
                assertEquals(savedChallenge.getTitle(), responseChallenge.getTitle());
                assertEquals(savedChallenge.getContent(), responseChallenge.getContent());
                assertEquals(savedChallenge.getChallengeCategory().getDescription(),
                        responseChallenge.getChallengeCategory());
                assertEquals(savedChallenge.getChallengeLocation().getDescription(),
                        responseChallenge.getChallengeLocation());
                assertEquals(savedChallenge.getChallengeDuration().getDescription(),
                        responseChallenge.getChallengeDuration());
                assertEquals(savedChallenge.getFormattedCreatedAt(), responseChallenge.getCreated_at());
                assertEquals(savedChallenge.getImgUrls(), responseChallenge.getChallengeImgUrls());
                assertEquals(1L, responseChallenge.getHowManyUsersAreInThisChallenge());
                assertEquals(savedChallenge.getUsers().getUserName(),
                        responseChallenge.getChallengeOwnerUser().getUserName());
                assertEquals(savedChallenge.getUsers().getEmail(),
                        responseChallenge.getChallengeOwnerUser().getEmail());
                assertEquals(savedChallenge.getUsers().getId(),
                        responseChallenge.getChallengeOwnerUser().getUserId());
            });
        }

        @Test
        @DisplayName("존재하지 않는 특정 챌린지를 조회하려고 하면 예외 발생")
        void failByChallengeNotFound() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            UserChallenge userChallenge = UserChallenge.builder()
                    .challengeStatus(ChallengeStatus.TRYING)
                    .challenge(savedChallenge)
                    .users(savedUser)
                    .build();
            userChallengeRepository.save(userChallenge);
            Long notExistChallengeId = savedChallenge.getId() + 100L;

            Throwable exception = assertThrows(ChallengeNotFound.class,
                    () -> challengeService.searchById(notExistChallengeId));
            assertEquals("챌린지를 찾을 수 없습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("챌린지 수정 테스트")
    class update {
        @Test
        void success() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            RequestUpdateChallenge requestUpdateChallenge = RequestUpdateChallenge.builder()
                    .title("수정된 제목")
                    .content("수정된 내용")
                    .challengeCategory(ChallengeCategory.WORKOUT.getDescription())
                    .build();
            List<MultipartFile> updateChallengeImgFiles = updateChallengeImgFiles();

            entityManager.flush();
            entityManager.clear();

            Long challengeId = savedChallenge.getId();
            Challenge updatedChallenge = challengeService.updateChallenge(challengeId, requestUpdateChallenge,
                    updateChallengeImgFiles, savedUser);

            assertAll(() -> {
                assertEquals(requestUpdateChallenge.getTitle(), updatedChallenge.getTitle());
                assertEquals(requestUpdateChallenge.getContent(), updatedChallenge.getContent());
                assertEquals(requestUpdateChallenge.getChallengeCategory(),
                        updatedChallenge.getChallengeCategory().getDescription());
                assertNotEquals(savedChallenge.getUpdated_at(), updatedChallenge.getUpdated_at());
                List<ChallengeImg> all = challengeImgRepository.findAll(); // 이게 없으면 같은 데이터가 두 번 저장되는 오류 발생
                assertThat(all).extracting("oriImgName")
                        .containsExactly("updatedChallengeImage0.jpg", "updatedChallengeImage1.jpg");
                assertThat(updatedChallenge.getChallengeImgs()).extracting("oriImgName")
                        .containsExactly("updatedChallengeImage0.jpg", "updatedChallengeImage1.jpg");
            });
        }

        @Test
        @DisplayName("존재하지 않는 챌린지를 수정하려고 하면 예외 발생")
        void failByNotFoundChallengeUpdate() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            RequestUpdateChallenge requestUpdateChallenge = RequestUpdateChallenge.builder()
                    .title("수정된 제목")
                    .content("수정된 내용")
                    .challengeCategory(ChallengeCategory.WORKOUT.getDescription())
                    .build();
            List<MultipartFile> updateChallengeImgFiles = updateChallengeImgFiles();
//        List<String> updateChallengeHashtags = List.of("editTag1", "editTag2");

            entityManager.flush();
            entityManager.clear();

            Long notFoundChallengeId = savedChallenge.getId() + 100L;
            Throwable exception = assertThrows(ChallengeNotFound.class,
                    () -> challengeService.updateChallenge(notFoundChallengeId, requestUpdateChallenge,
                            updateChallengeImgFiles, savedUser));
            assertEquals("챌린지를 찾을 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("챌린지 카테고리 값이 올바르지 않다면 예외 발생")
        void failByCategoryNotFound() {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            RequestUpdateChallenge requestUpdateChallenge = RequestUpdateChallenge.builder()
                    .title("title")
                    .content("content")
                    .challengeCategory("error")
                    .build();
            List<MultipartFile> updateChallengeImgFiles = updateChallengeImgFiles();
//        List<String> updateChallengeHashtags = List.of("editTag1", "editTag2");

            entityManager.flush();
            entityManager.clear();

            Long challengeId = savedChallenge.getId();
            Throwable exception = assertThrows(ChallengeCategoryNotFound.class,
                    () -> challengeService.updateChallenge(challengeId, requestUpdateChallenge,
                            updateChallengeImgFiles, savedUser));
            assertEquals("존재하지 않는 챌린지 카테고리입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("챌린지 소유자가 아닌 사용자가 챌린지를 수정하려고 하면 예외 발생")
        void failByAuthorization() throws Exception {
            Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
            RequestUpdateChallenge requestUpdateChallenge = RequestUpdateChallenge.builder()
                    .title("수정된 제목")
                    .content("수정된 내용")
                    .challengeCategory(ChallengeCategory.WORKOUT.getDescription())
                    .build();
            List<MultipartFile> updateChallengeImgFiles = updateChallengeImgFiles();
//        List<String> updateChallengeHashtags = List.of("editTag1", "editTag2");
            UserDto userDto = new UserDto();
            userDto.setEmail("a@a.com");
            userDto.setUserName("김철수");
            userDto.setInfo("aInfo");
            userDto.setPassword("1234");
            User otherUser = userService.saveUser(userDto, passwordEncoder);

            entityManager.flush();
            entityManager.clear();

            Long challengeId = savedChallenge.getId();
            Throwable exception = assertThrows(AuthorizationException.class,
                    () -> challengeService.updateChallenge(challengeId, requestUpdateChallenge,
                            updateChallengeImgFiles, otherUser));
            assertEquals("권한이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("챌린지 삭제 테스트")
    class delete {

        @Test
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

            assertAll(() -> {
                assertEquals(challengeImgFiles.size(), challengeImgRepository.count());
                assertEquals(savedUserChallenge,
                        userChallengeRepository.findById(savedUserChallenge.getId()).orElseThrow());
                assertEquals(savedChallenge,
                        challengeRepository.findById(savedChallenge.getId()).orElseThrow());
                assertEquals(savedChallengeHashtag,
                        challengeHashtagRepository.findById(savedChallengeHashtag.getId()).orElseThrow());
                assertEquals(savedComment, commentRepository.findById(savedComment.getId()).orElseThrow());
                assertEquals(savedHashTag, hashtagRepository.findById(savedHashTag.getId()).orElseThrow());
            });

            entityManager.flush();
            entityManager.clear();

            Long challengeId = savedChallenge.getId();
            challengeService.deleteChallenge(challengeId, savedUser);

            assertAll(() -> {
                assertEquals(0L, challengeImgRepository.count());
                assertTrue(userChallengeRepository.findById(savedUserChallenge.getId()).isEmpty());
                assertTrue(challengeRepository.findById(savedChallenge.getId()).isEmpty());
                assertTrue(commentRepository.findById(savedComment.getId()).isEmpty());
//                assertTrue(hashtagRepository.findById(savedHashTag.getId()).isEmpty()); // 챌린지 해시태그를 삭제해도 챌린지는 존재한다
                assertTrue(challengeHashtagRepository.findById(savedChallengeHashtag.getId()).isEmpty());
            });
        }
    }
}
