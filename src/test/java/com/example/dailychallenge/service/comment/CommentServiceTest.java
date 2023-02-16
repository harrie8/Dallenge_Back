package com.example.dailychallenge.service.comment;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.dailychallenge.dto.CommentDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.comment.CommentDtoNotValid;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ServiceTest;
import java.util.List;
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
}