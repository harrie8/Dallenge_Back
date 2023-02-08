package com.example.dailychallenge.controller.comment;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
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
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.comment.CommentService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.dailychallenge.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class CommentControllerDocTest {

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
    private CommentRepository commentRepository;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private UserChallengeRepository userChallengeRepository;
    @Autowired
    private ChallengeImgRepository challengeImgRepository;

    @BeforeEach
    void beforeEach() throws Exception {
        userChallengeRepository.deleteAll();
        challengeImgRepository.deleteAll();
        challengeRepository.deleteAll();
        userRepository.deleteAll();
    }

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
        CommentDto commentDto = CommentDto.builder()
                .content("댓글 내용")
                .build();

        List<MultipartFile> commentDtoImg = new ArrayList<>();
        commentDtoImg.add(createMultipartFiles());
        return commentService.saveComment(commentDto, user, challenge, commentDtoImg);
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    public void createCommentTest() throws Exception {
        Challenge challenge = createChallenge();
        User user = challenge.getUsers();
        CommentDto requestComment = CommentDto.builder()
                .content("댓글 내용")
                .build();

        MockMultipartFile commentDtoImg = createMultipartFiles();

        String json = objectMapper.writeValueAsString(requestComment);
        MockMultipartFile commentDto = new MockMultipartFile("commentDto",
                "commentDto",
                "application/json", json.getBytes(StandardCharsets.UTF_8));

        Long challengeId = challenge.getId();
        String token = generateToken();
        mockMvc.perform(RestDocumentationRequestBuilders
                        .multipart("/{challengeId}/comment/new",challengeId)
                        .file(commentDto)
                        .file(commentDtoImg)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(requestComment.getContent()))
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andDo(print())
                .andDo(document("comment-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        pathParameters(
                                parameterWithName("challengeId").description("챌린지 아이디")
                        ),
                        requestParts(
                                partWithName("commentDto").description("댓글 정보 데이터(JSON)").attributes(key("type").value("JSON")),
                                partWithName("commentDtoImg").description("댓글 이미지 파일(FILE)").optional().attributes(key("type").value(".jpg"))
                        ),
                        requestPartFields("commentDto",
                                fieldWithPath("content").description("내용")
                        )
                ));
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
        Long commentId = savedComment.getId();
        String token = generateToken();
        mockMvc.perform(RestDocumentationRequestBuilders
                        .multipart("/{challengeId}/comment/{commentId}",challengeId,commentId)
                        .file(commentDto)
                        .file(commentDtoImg)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("comment-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        pathParameters(
                                parameterWithName("challengeId").description("챌린지 아이디"),
                                parameterWithName("commentId").description("댓글 아이디")
                        ),
                        requestParts(
                                partWithName("commentDto").description("댓글 수정 정보 데이터(JSON)").attributes(key("type").value("JSON")),
                                partWithName("commentDtoImg").description("댓글 수정 이미지 파일(FILE)").optional().attributes(key("type").value(".jpg"))
                        ),
                        requestPartFields("commentDto",
                                fieldWithPath("content").description("수정할 내용")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    public void deleteCommentTest() throws Exception {
        Comment savedComment = createComment();
        Long challengeId = savedComment.getChallenge().getId();
        String token = generateToken();
        mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/{challengeId}/comment/{commentId}",challengeId,savedComment.getId())
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("comment-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        pathParameters(
                                parameterWithName("challengeId").description("챌린지 아이디"),
                                parameterWithName("commentId").description("댓글 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("좋아요 테스트")
    public void isLikeTest() throws Exception {
        Comment savedComment = createComment();
        String token = generateToken();
        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/{commentId}/like", savedComment.getId())
                        .param("isLike", String.valueOf(1))
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLike").value(savedComment.getLikes()+1))
                .andDo(document("comment-like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId").description("댓글 아이디")
                        )
                ));
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
