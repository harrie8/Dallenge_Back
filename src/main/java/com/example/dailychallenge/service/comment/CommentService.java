package com.example.dailychallenge.service.comment;

import com.example.dailychallenge.dto.CommentDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.comment.Comment.CommentBuilder;
import com.example.dailychallenge.entity.comment.CommentImg;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.AuthorizationException;
import com.example.dailychallenge.exception.comment.CommentNotFound;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.vo.ResponseChallengeComment;
import com.example.dailychallenge.vo.ResponseUserComment;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentImgService commentImgService;

    public Comment saveComment(CommentDto commentDto, User user, Challenge challenge) {

        CommentBuilder commentBuilder = Comment.builder();

        if (commentDto.isContentValid()) {
            commentBuilder
                    .content(commentDto.getContent());
        }
        Comment comment = commentBuilder
                .build();

        comment.saveCommentChallenge(challenge);
        comment.saveCommentUser(user);

        commentRepository.save(comment);

        if (commentDto.isCommentDtoImgValid()) {
            List<MultipartFile> commentImgFiles = commentDto.getCommentDtoImg();
            for (int i = 0; i < commentImgFiles.size(); i++) {
                CommentImg commentImg = new CommentImg();
                commentImg.saveComment(comment);
                commentImgService.saveCommentImg(commentImg, commentImgFiles.get(i));
            }
        }
        return comment;
    }

    public Comment updateComment(Long commentId, CommentDto commentDto, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFound::new);
        validateOwner(user, comment);

        comment.updateComment(commentDto.getContent());

        if (commentDto.isCommentDtoImgValid()) {
            List<MultipartFile> commentImgFiles = commentDto.getCommentDtoImg();
            List<CommentImg> commentImgs = comment.getCommentImgs();
            List<Long> deleteCommentImgIds = new ArrayList<>();
            int idx=0;
            for (CommentImg commentImg : commentImgs) { // 기존에 저장된 이미지가 더 많을 때
                if (commentImgFiles.size() <= idx) {
                    deleteCommentImgIds.add(commentImg.getId());
                    continue;
                }
                commentImgService.updateCommentImg(commentImg.getId(),commentImgFiles.get(idx++));
            }

            for (Long commentImgId : deleteCommentImgIds) {
                commentImgService.deleteCommentImg(commentImgId);
            }

            for (int i = idx; i < commentImgFiles.size(); i++) { // 추가한 이미지가 더 많을 때
                CommentImg commentImg = new CommentImg();
                commentImg.saveComment(comment);
                commentImgService.saveCommentImg(commentImg, commentImgFiles.get(i));
            }
        }

        if (!commentDto.isCommentDtoImgValid()) {
            List<CommentImg> commentImgs = comment.getCommentImgs();
            commentImgs.clear();
        }

        return comment;
    }

    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFound::new);
        validateOwner(user, comment);

        commentRepository.delete(comment);
    }

    public Integer likeUpdate(Long commentId, Integer isLike) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFound::new);
        if (isLike==1) {
            comment.updateLike(true);
        } else if(isLike == 0){
            comment.updateLike(false);
        }
        return comment.getLikes();
    }

    public Page<ResponseChallengeComment> searchCommentsByChallengeId(Challenge challenge, Pageable pageable) {

        Long challengeId = challenge.getId();
        return commentRepository.searchCommentsByChallengeId(challengeId, pageable);
    }

    public Page<ResponseUserComment> searchCommentsByUserId(Long userId, Pageable pageable) {

        return commentRepository.searchCommentsByUserId(userId, pageable);
    }

    private void validateOwner(User user, Comment comment) {
        if (!comment.isOwner(user.getId())) {
            throw new AuthorizationException();
        }
    }
}
