package com.example.dailychallenge.service;

import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.user.UserFixture.createUser;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.dto.CommentDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.Heart;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.repository.HeartRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.comment.CommentService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ServiceTest;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class HeartServiceTest extends ServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private HeartService heartService;
    @Autowired
    private HeartRepository heartRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private User savedUser;
    private Challenge challenge;

    @BeforeEach
    void beforeEach() {
        savedUser = userService.saveUser(createUser(), passwordEncoder);
        challenge = challengeService.saveChallenge(createChallengeDto(), createChallengeImgFiles(), savedUser);
    }

    private MockMultipartFile createMultipartFiles() {
        String path = "commentDtoImg";
        String imageName = "commentDtoImg.jpg";
        return new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
    }

    @Test
    @DisplayName("좋아요 생성 테스트")
    void saveHeartTest() {
        CommentDto commentDto = CommentDto.builder()
                .content("댓글 내용")
                .build();
        List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());
        Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);

        heartService.updateHeart(1, saveComment.getId(), savedUser.getEmail());

        Heart heart = heartRepository.findByUsers_IdAndComment_Id(savedUser.getId(), saveComment.getId()).get();
        assertAll(() -> {
            assertEquals(saveComment, heart.getComment());
            assertEquals(savedUser, heart.getUsers());
        });
    }

    @Test
    @DisplayName("좋아요 삭제 테스트")
    void deleteHeartTest() {
        CommentDto commentDto = CommentDto.builder()
                .content("댓글 내용")
                .build();
        List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());
        Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);
        Long saveCommentId = saveComment.getId();
        heartService.saveHeart(saveCommentId, savedUser.getId());

        entityManager.clear();
        heartService.updateHeart(0, saveCommentId, savedUser.getEmail());

        assertTrue(heartRepository.findByUsers_IdAndComment_Id(savedUser.getId(), saveCommentId).isEmpty());
    }

    @Test
    @DisplayName("댓글을 삭제하면 좋아요도 삭제되는 테스트")
    void deleteCommentWithHeartTest() {
        CommentDto commentDto = CommentDto.builder()
                .content("댓글 내용")
                .build();
        List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());
        Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);
        Long challengeId = challenge.getId();
        Long saveCommentId = saveComment.getId();
        heartService.saveHeart(saveCommentId, savedUser.getId());

        entityManager.clear();
        commentService.deleteComment(challengeId, saveCommentId, savedUser);

        assertTrue(commentRepository.findById(saveCommentId).isEmpty());
        assertTrue(heartRepository.findByUsers_IdAndComment_Id(savedUser.getId(), saveCommentId).isEmpty());
    }

    @Test
    @DisplayName("댓글의 좋아요 개수 조회 테스트")
    void getHeartOfCommentTest() {
        CommentDto commentDto = CommentDto.builder()
                .content("댓글 내용")
                .build();
        List<MultipartFile> commentImgFiles = List.of(createMultipartFiles());
        Comment saveComment = commentService.saveComment(commentDto, commentImgFiles, savedUser, challenge);
        for (int i = 0; i < 10; i++) {
            UserDto userDto = new UserDto();
            userDto.setEmail("test" + i + "@test.com");
            userDto.setUserName("testName" + i);
            userDto.setInfo("testInfo");
            userDto.setPassword(PASSWORD);
            User user = userService.saveUser(userDto, passwordEncoder);
            heartService.updateHeart(1, saveComment.getId(), user.getEmail());
        }

        Long heartOfComment = heartService.getHeartOfComment(saveComment.getId());

        assertEquals(10L, heartOfComment);
    }
}