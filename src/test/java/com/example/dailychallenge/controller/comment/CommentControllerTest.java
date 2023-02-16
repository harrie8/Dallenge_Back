package com.example.dailychallenge.controller.comment;

import static com.example.dailychallenge.util.fixture.UserFixture.createOtherUser;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.CommentDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.comment.CommentService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class CommentControllerTest {

    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String AUTHORIZATION = "Authorization";
    private final static String EMAIL = "test1234@test.com";
    private final static String PASSWORD = "1234";

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static MockMultipartFile createMultipartFiles() {
        String path = "commentDtoImg";
        String imageName = "commentDtoImg.jpg";
        return new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
    }

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail(EMAIL);
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword(PASSWORD);
        return userDto;
    }
    public Challenge createChallenge() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
        MultipartFile challengeImg = createMultipartFiles();
        List<MultipartFile> challengeImgFiles = List.of(challengeImg);
        return challengeService.saveChallenge(challengeDto, challengeImgFiles, savedUser);
    }

    public Comment createComment() throws Exception {
        Challenge challenge = createChallenge();
        User user = userService.findByEmail(EMAIL);
        List<MultipartFile> commentDtoImg = new ArrayList<>();
        commentDtoImg.add(createMultipartFiles());
        CommentDto commentDto = CommentDto.builder()
                .content("댓글 내용")
                .commentDtoImg(commentDtoImg)
                .build();

        return commentService.saveComment(commentDto, user, challenge);
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    public void createCommentTest() throws Exception {
        Challenge challenge = createChallenge();
        CommentDto requestComment = CommentDto.builder()
                .content("댓글 내용")
                .build();

        MockMultipartFile commentDtoImg = createMultipartFiles();

        Long challengeId = challenge.getId();
        mockMvc.perform(multipart("/{challengeId}/comment/new", challengeId)
                        .file(commentDtoImg)
                        .param("content", requestComment.getContent())
                        .with(user(userService.loadUserByUsername(EMAIL)))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    public void updateCommentTest() throws Exception {
        Comment savedComment = createComment();

        CommentDto requestComment = CommentDto.builder()
                .content("댓글 수정")
                .build();
        MockMultipartFile commentDtoImg = createMultipartFiles();

        String json = objectMapper.writeValueAsString(requestComment);
        MockMultipartFile commentDto = new MockMultipartFile("commentDto",
                "commentDto",
                "application/json", json.getBytes(StandardCharsets.UTF_8));

        Long challengeId = savedComment.getChallenge().getId();
        String token = generateToken();
        mockMvc.perform(multipart("/{challengeId}/comment/{commentId}",challengeId,savedComment.getId())
                        .file(commentDto)
                        .file(commentDtoImg)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    public void deleteCommentTest() throws Exception {
        Comment savedComment = createComment();
        Long challengeId = savedComment.getChallenge().getId();
        String token = generateToken();
        mockMvc.perform(delete("/{challengeId}/comment/{commentId}",challengeId,savedComment.getId())
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("좋아요 테스트")
    public void isLikeTest() throws Exception {
        Comment savedComment = createComment();

        String token = generateToken();
        mockMvc.perform(post("/{commentId}/like", savedComment.getId())
                        .param("isLike", String.valueOf(1))
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("특정 챌린지의 댓글들 조회 테스트")
    public void searchCommentsByChallengeId() throws Exception {
        Challenge challenge = createChallenge();
        User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
        for (int i = 0; i < 5; i++) {
            List<MultipartFile> commentDtoImg = new ArrayList<>();
            commentDtoImg.add(createMultipartFiles());
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용" + i)
                    .commentDtoImg(commentDtoImg)
                    .build();
            commentService.saveComment(commentDto, otherUser, challenge);
        }
        Long challengeId = challenge.getId();

        mockMvc.perform(get("/{challengeId}/comment", challengeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[*].id").isNotEmpty())
                .andExpect(jsonPath("$.content[*].content", hasItems(startsWith("댓글 내용"))))
                .andExpect(jsonPath("$.content[*].likes", hasItems(0)))
                .andExpect(jsonPath("$.content[*].createdAt").isNotEmpty())
                .andExpect(jsonPath("$.content[*].commentImgUrls",
                        hasItems(hasItem(startsWith("/images/")))))
                .andExpect(jsonPath("$.content[*].commentOwnerUser.userName",
                        hasItems(otherUser.getUserName())))
                .andExpect(jsonPath("$.content[*].commentOwnerUser.email",
                        hasItems(otherUser.getEmail())))
                .andExpect(jsonPath("$.content[*].commentOwnerUser.userId",
                        hasItems(otherUser.getId().intValue())))
                .andDo(print());
    }

    @Test
    @DisplayName("유저가 작성한 챌린지의 댓글들 조회 테스트")
    public void searchCommentsByUserId() throws Exception {
        Challenge challenge = createChallenge();
        User savedUser = challenge.getUsers();
        for (int i = 0; i < 5; i++) {
            List<MultipartFile> commentDtoImg = new ArrayList<>();
            commentDtoImg.add(createMultipartFiles());
            CommentDto commentDto = CommentDto.builder()
                    .content("댓글 내용" + i)
                    .commentDtoImg(commentDtoImg)
                    .build();
            commentService.saveComment(commentDto, savedUser, challenge);
        }
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title("다른 제목입니다.")
                .content("다른 내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
        MultipartFile challengeImg = createMultipartFiles();
        List<MultipartFile> challengeImgFiles = List.of(challengeImg);
        Challenge otherChallenge = challengeService.saveChallenge(challengeDto, challengeImgFiles, savedUser);
        for (int i = 5; i < 8; i++) {
            List<MultipartFile> commentDtoImg = new ArrayList<>();
            commentDtoImg.add(createMultipartFiles());
            CommentDto commentDto = CommentDto.builder()
                    .content("다른 댓글 내용" + i)
                    .commentDtoImg(commentDtoImg)
                    .build();
            commentService.saveComment(commentDto, savedUser, otherChallenge);
        }
        Long userId = savedUser.getId();

        mockMvc.perform(get("/user/{userId}/comment", userId)
                        .with(user(userService.loadUserByUsername(savedUser.getEmail())))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(8)))
                .andExpect(jsonPath("$.content[*].id").isNotEmpty())
                .andExpect(jsonPath("$.content[*].content",
                        contains("다른 댓글 내용7", "다른 댓글 내용6", "다른 댓글 내용5", "댓글 내용4",
                                "댓글 내용3", "댓글 내용2", "댓글 내용1", "댓글 내용0")))
                .andExpect(jsonPath("$.content[*].likes", hasItems(0)))
                .andExpect(jsonPath("$.content[*].createdAt").isNotEmpty())
                .andExpect(jsonPath("$.content[*].commentImgUrls",
                        hasItems(hasItem(startsWith("/images/")))))
                .andExpect(jsonPath("$.content[*].challengeId",
                        hasItems(challenge.getId().intValue(), otherChallenge.getId().intValue())))
                .andExpect(jsonPath("$.content[*].challengeTitle",
                        hasItems(challenge.getTitle(), otherChallenge.getTitle())))
                .andDo(print());
    }


    private String generateToken() {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD));
        if (auth.isAuthenticated()) {
            UserDetails userDetails = userService.loadUserByUsername(EMAIL);
            return TOKEN_PREFIX + jwtTokenUtil.generateToken(userDetails);
        }

        throw new IllegalArgumentException("token 생성 오류");
    }

}