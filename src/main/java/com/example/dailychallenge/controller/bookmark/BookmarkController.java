package com.example.dailychallenge.controller.bookmark;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.bookmark.BookmarkService;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.vo.bookmark.ResponseBookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/user/{userId}/bookmark")
    public ResponseEntity<Page<ResponseBookmark>> searchBookmarksByUserId(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable Long userId,
            @PageableDefault(page = 0, size = 10, sort = "time", direction = Sort.Direction.DESC) Pageable pageable) {

        String loginUserEmail = user.getUsername();
        userService.validateUser(loginUserEmail, userId);

        Page<ResponseBookmark> result = bookmarkService.searchBookmarksByUserId(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
