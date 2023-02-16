package com.example.dailychallenge.service.comment;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.dto.CommentDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.AuthorizationException;
import com.example.dailychallenge.exception.comment.CommentDtoNotValid;
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
                    .commentDtoImg(List.of(createMultipartFiles()))
                    .build();

            Comment saveComment = commentService.saveComment(commentDto, savedUser, challenge);

            assertAll(() -> {
                assertEquals(commentDto.getContent(), saveComment.getContent());
                assertEquals(0, saveComment.getLikes());
                assertEquals(challenge, saveComment.getChallenge());
                assertEquals(savedUser, saveComment.getUsers());
                assertFalse(saveComment.getCommentImgs().isEmpty());
            });
        }

        @Test
        @DisplayName("댓글 내용 또는 이미지가 존재하지 않는 경우 예외 발생")
        void failByCommentDtoIsNotValid() {
            Throwable exception = assertThrows(CommentDtoNotValid.class, () -> {
                CommentDto commentDto = CommentDto.builder()
                        .build();
                commentService.saveComment(commentDto, savedUser, challenge);
            });
            assertEquals("댓글은 내용 또는 이미지가 필요합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("댓글 내용이 빈 경우 예외 발생")
        void failByCommentContentIsEmpty() {
            Throwable exception = assertThrows(CommentDtoNotValid.class, () -> {
                CommentDto commentDto = CommentDto.builder()
                        .content("")
                        .build();
                commentService.saveComment(commentDto, savedUser, challenge);
            });
            assertEquals("댓글 내용은 비어서는 안 됩니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class updateComment {
        @Test
        void success() {
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용")
                    .commentDtoImg(List.of(createMultipartFiles()))
                    .build();
            Comment saveComment = commentService.saveComment(commentDto, savedUser, challenge);
            Long saveCommentId = saveComment.getId();

            CommentDto updateCommentDto = CommentDto.builder()
                    .content("수정된 댓글 내용")
                    .commentDtoImg(List.of(createMultipartFiles(), createMultipartFiles()))
                    .build();

            entityManager.flush();
            entityManager.clear();

            Comment updateComment = commentService.updateComment(saveCommentId, updateCommentDto, savedUser);

            assertAll(() -> {
                assertEquals(updateCommentDto.getContent(), updateComment.getContent());
                assertEquals(saveComment.getLikes(), updateComment.getLikes());
                assertEquals(challenge.getId(), updateComment.getChallenge().getId());
                assertEquals(savedUser.getId(), updateComment.getUsers().getId());
                assertNotEquals(saveComment.getCommentImgs(), updateComment.getCommentImgs());
                assertEquals(2, updateComment.getCommentImgs().size());
            });
        }

        @Test
        @DisplayName("내용만 변경하는 경우")
        void updateContent() {
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용")
                    .commentDtoImg(List.of(createMultipartFiles()))
                    .build();
            Comment saveComment = commentService.saveComment(commentDto, savedUser, challenge);
            Long saveCommentId = saveComment.getId();

            CommentDto updateCommentDto = CommentDto.builder()
                    .content("수정된 댓글 내용")
                    .build();

            entityManager.flush();
            entityManager.clear();

            Comment updateComment = commentService.updateComment(saveCommentId, updateCommentDto, savedUser);

            assertAll(() -> {
                assertEquals(updateCommentDto.getContent(), updateComment.getContent());
                assertEquals(saveComment.getLikes(), updateComment.getLikes());
                assertEquals(challenge.getId(), updateComment.getChallenge().getId());
                assertEquals(savedUser.getId(), updateComment.getUsers().getId());
                assertTrue(updateComment.getCommentImgs().isEmpty());
                assertEquals(0, commentImgRepository.count());
            });
        }

        @Test
        @DisplayName("이미지만 변경하는 경우")
        void updateImage() {
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용")
                    .commentDtoImg(List.of(createMultipartFiles()))
                    .build();
            Comment saveComment = commentService.saveComment(commentDto, savedUser, challenge);
            Long saveCommentId = saveComment.getId();

            CommentDto updateCommentDto = CommentDto.builder()
                    .commentDtoImg(List.of(createMultipartFiles(), createMultipartFiles()))
                    .build();

            entityManager.flush();
            entityManager.clear();

            Comment updateComment = commentService.updateComment(saveCommentId, updateCommentDto, savedUser);

            assertAll(() -> {
                assertEquals(updateCommentDto.getContent(), updateComment.getContent());
                assertEquals(saveComment.getLikes(), updateComment.getLikes());
                assertEquals(challenge.getId(), updateComment.getChallenge().getId());
                assertEquals(savedUser.getId(), updateComment.getUsers().getId());
                assertNotEquals(saveComment.getCommentImgs(), updateComment.getCommentImgs());
                assertEquals(2, updateComment.getCommentImgs().size());
            });
        }

        @Test
        @DisplayName("댓글 주인이 아닌 경우 예외 발생")
        void failByAuthorization() throws Exception {
            User otherSavedUser = userService.saveUser(createOtherUser(), passwordEncoder);
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용")
                    .commentDtoImg(List.of(createMultipartFiles()))
                    .build();
            Comment saveComment = commentService.saveComment(commentDto, savedUser, challenge);
            Long saveCommentId = saveComment.getId();

            CommentDto updateCommentDto = CommentDto.builder()
                    .content("수정된 댓글 내용")
                    .build();

            entityManager.flush();
            entityManager.clear();

            Throwable exception = assertThrows(AuthorizationException.class, () -> {
                commentService.updateComment(saveCommentId, updateCommentDto, otherSavedUser);
            });
            assertEquals("권한이 없습니다.", exception.getMessage());
        }
    }
}