package com.example.dailychallenge.service.comment;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.dto.CommentDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.AuthorizationException;
import com.example.dailychallenge.exception.comment.CommentCreateNotValid;
import com.example.dailychallenge.repository.CommentImgRepository;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ServiceTest;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class CommentServiceTest extends ServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentImgRepository commentImgRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private User savedUser;
    private Challenge challenge;

    @BeforeEach
    void beforeEach() throws Exception {
        savedUser = userService.saveUser(createUser(), passwordEncoder);
        challenge = challengeService.saveChallenge(createChallengeDto(), createChallengeImgFiles(), savedUser);
    }

    private static MockMultipartFile createMultipartFiles() {
        String path = "commentDtoImg";
        String imageName = "commentDtoImg.jpg";
        return new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
    }

    @Nested
    @DisplayName("댓글 생성 테스트")
    class saveComment {
        @Test
        void success() {
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용")
                    .build();
            List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());

            Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);

            assertAll(() -> {
                assertEquals(commentDto.getContent(), saveComment.getContent());
                assertEquals(0, saveComment.getLikes());
                assertEquals(challenge, saveComment.getChallenge());
                assertEquals(savedUser, saveComment.getUsers());
                assertFalse(saveComment.getCommentImgs().isEmpty());
            });
        }

        @Test
        @DisplayName("댓글 내용만 있는 경우")
        void successByContentOnly() {
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용")
                    .build();
            List<MultipartFile> commentImgFiles = null;

            Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);

            assertAll(() -> {
                assertEquals(commentDto.getContent(), saveComment.getContent());
                assertEquals(0, saveComment.getLikes());
                assertEquals(challenge, saveComment.getChallenge());
                assertEquals(savedUser, saveComment.getUsers());
                assertTrue(saveComment.getCommentImgs().isEmpty());
            });
        }

        @Test
        @DisplayName("댓글 이미지들만 있는 경우")
        void successByImagesOnly() {
            CommentDto commentDto = null;
            List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());

            Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);

            assertAll(() -> {
                assertNull(saveComment.getContent());
                assertEquals(0, saveComment.getLikes());
                assertEquals(challenge, saveComment.getChallenge());
                assertEquals(savedUser, saveComment.getUsers());
                assertFalse(saveComment.getCommentImgs().isEmpty());
            });
        }

        @Test
        @DisplayName("댓글 내용 또는 이미지가 존재하지 않는 경우 예외 발생")
        void failByCommentContentOrImagesAreNull() {
            CommentDto commentDto = null;
            List<MultipartFile> commentImgFiles = null;

            Throwable exception = assertThrows(CommentCreateNotValid.class,
                    () -> commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge));
            assertEquals("댓글은 내용 또는 이미지가 필요합니다.", exception.getMessage());
        }
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateCommentTest() {
        CommentDto commentDto = CommentDto.builder()
                .content("댓글 내용")
                .build();
        List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());
        Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);
        Long saveCommentId = saveComment.getId();
        Long challengeId = challenge.getId();

        CommentDto updateCommentDto = CommentDto.builder()
                .content("수정된 댓글 내용")
                .build();

        entityManager.flush();
        entityManager.clear();

        Comment updateComment = commentService.updateComment(challengeId, saveCommentId, updateCommentDto, savedUser);

        assertAll(() -> {
            assertEquals(updateCommentDto.getContent(), updateComment.getContent());
            assertEquals(saveComment.getLikes(), updateComment.getLikes());
            assertEquals(challenge.getId(), updateComment.getChallenge().getId());
            assertEquals(savedUser.getId(), updateComment.getUsers().getId());
            assertEquals(saveComment.getImgUrls(), updateComment.getImgUrls());
        });
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteCommentTest() {
        CommentDto commentDto = CommentDto.builder()
                .content("댓글 내용")
                .build();
        List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());
        Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);
        Long challengeId = challenge.getId();
        Long saveCommentId = saveComment.getId();

        commentService.deleteComment(challengeId, saveCommentId, savedUser);

        assertTrue(commentRepository.findById(saveCommentId).isEmpty());
    }

    @Nested
    @DisplayName("댓글을 작성한 회원인지 확인하는 테스트")
    class validateOwner {
        @Test
        void success() {
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용")
                    .build();
            List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());
            Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);

            assertDoesNotThrow(() -> commentService.validateOwner(savedUser, saveComment));
        }

        @Test
        @DisplayName("댓글을 작성한 회원이 아닌 경우 예외 발생")
        void failByAuthorization() throws Exception {
            User otherSavedUser = userService.saveUser(createOtherUser(), passwordEncoder);
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용")
                    .build();
            List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());
            Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);

            Throwable exception = assertThrows(AuthorizationException.class,
                    () -> commentService.validateOwner(otherSavedUser, saveComment));
            assertEquals("권한이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("챌린지의 댓글인지 확인하는 테스트")
    class validateChallenge {
        @Test
        void success() {
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용")
                    .build();
            List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());
            Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);
            Long challengeId = challenge.getId();

            assertDoesNotThrow(() -> commentService.validateChallenge(challengeId, saveComment));
        }

        @Test
        @DisplayName("챌린지의 댓글이 아닌 경우 예외 발생")
        void failByAuthorization() {
            Challenge otherChallenge = challengeService.saveChallenge(createChallengeDto(), createChallengeImgFiles(),
                    savedUser);
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용")
                    .build();
            List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());
            Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);
            Long otherChallengeId = otherChallenge.getId();

            Throwable exception = assertThrows(AuthorizationException.class,
                    () -> commentService.validateChallenge(otherChallengeId, saveComment));
            assertEquals("권한이 없습니다.", exception.getMessage());
        }
    }
}