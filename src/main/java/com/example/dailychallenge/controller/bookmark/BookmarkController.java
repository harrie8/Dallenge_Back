package com.example.dailychallenge.controller.bookmark;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.bookmark.BookmarkService;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.vo.bookmark.ResponseBookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;
    private final UserService userService;
    private final ChallengeService challengeService;

    @PostMapping(value = "/{challengeId}/bookmark/new")
    public ResponseEntity<ResponseBookmark> createBookmark(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable("challengeId") Long challengeId) {

        User findUser = userService.findByEmail(user.getUsername());
        Challenge challenge = challengeService.findById(challengeId);
        Bookmark bookmark = bookmarkService.saveBookmark(findUser, challenge);

        ResponseBookmark responseBookmark = ResponseBookmark.builder()
                .title(bookmark.getChallenge().getTitle())
                .createdAt(bookmark.getFormattedCreatedAt())
                .userId(bookmark.getUsers().getId())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBookmark);
    }

//    @PostMapping("/{challengeId}/comment/{commentId}")
//    public void updateComment(
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
//            @PathVariable("commentId") Long commentId,
//            @RequestPart @Valid CommentDto commentDto,
//            @RequestPart(required = false) List<MultipartFile> commentDtoImg) {
//
//        commentService.updateComment(commentId, commentDto, commentDtoImg);
//    }
//
//    @DeleteMapping("/{challengeId}/comment/{commentId}")
//    public ResponseEntity<?> deleteComment(
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
//            @PathVariable("commentId") Long commentId){
//
//        commentService.deleteComment(commentId);
//        return ResponseEntity.status(HttpStatus.OK).body("댓글이 삭제되었습니다.");
//    }
//
//    @PostMapping("/{commentId}/like")
//    public ResponseEntity<?> likeComment(
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
//            @PathVariable("commentId") Long commentId,
//            @RequestParam Integer isLike) {
//
//        Integer likeCount = commentService.likeUpdate(commentId, isLike);
//        HashMap<String, Integer> responseMap = new HashMap<>();
//        responseMap.put("isLike", likeCount);
//        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
//    }
//
//    @GetMapping("/{challengeId}/comment")
//    public ResponseEntity<Page<ResponseChallengeComment>> searchCommentsByChallengeId(
//            @PathVariable Long challengeId,
//            @PageableDefault(page = 0, size = 10, sort = "like", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        Challenge challenge = challengeService.findById(challengeId);
//        Page<ResponseChallengeComment> result = commentService.searchCommentsByChallengeId(challenge, pageable);
//
//        return ResponseEntity.status(HttpStatus.OK).body(result);
//    }
//
//    @GetMapping("/user/{userId}/comment")
//    public ResponseEntity<Page<ResponseUserComment>> searchCommentsByUserId(
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
//            @PathVariable Long userId,
//            @PageableDefault(page = 0, size = 10, sort = "time", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        String loginUserEmail = user.getUsername();
//        userService.validateUser(loginUserEmail, userId);
//
//        Page<ResponseUserComment> result = commentService.searchCommentsByUserId(userId, pageable);
//
//        return ResponseEntity.status(HttpStatus.OK).body(result);
//    }
}
