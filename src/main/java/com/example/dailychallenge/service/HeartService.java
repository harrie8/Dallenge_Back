package com.example.dailychallenge.service;

import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.Heart;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.repository.HeartRepository;
import com.example.dailychallenge.service.comment.CommentService;
import com.example.dailychallenge.service.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;
    private final UserService userService;
    private final CommentService commentService;

    public void updateHeart(Integer isLike, Long commentId, String userEmail) {
        User user = userService.findByEmail(userEmail).orElseThrow(UserNotFound::new);
        Optional<Heart> heart = heartRepository.findByUsers_IdAndComment_Id(user.getId(),commentId);

        if(isLike==1){
            if (heart.isPresent()) return; // 이후 예외 처리, 아니면 없애기
            saveHeart(commentId,user.getId());
        } else if(isLike==0){
            if (heart.isEmpty()) return;
            deleteHeart(commentId,user.getId());
        } else{
            /**
             *  1. isLike를 1/0 으로 받으면 이대로
             *  2. 좋아요 클릭할 때마다 상태 값 바뀌게
             *  + 1,0 말고 다른 값 예외 처리 할 지
             */
        }
    }

    private void deleteHeart(Long commentId, Long userId) {
        Optional<Heart> heart = heartRepository.findByUsers_IdAndComment_Id(userId,commentId); // > 이후 예외 처리
        heart.ifPresent(heartRepository::delete);
    }

    public void saveHeart(Long commentId, Long userId){
        User user = userService.findById(userId).orElseThrow(UserNotFound::new);
        Comment comment = commentService.findById(commentId);

        Heart heart = Heart.builder()
                .users(user)
                .comment(comment)
                .build();

        heartRepository.save(heart);
    }

    public Long getHeartOfComment(Long commentId) {
        return heartRepository.countByCommentId(commentId);
    }
}
