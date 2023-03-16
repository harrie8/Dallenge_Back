package com.example.dailychallenge.service.comment;

import com.example.dailychallenge.dto.CommentDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.comment.Comment.CommentBuilder;
import com.example.dailychallenge.entity.comment.CommentImg;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.AuthorizationException;
import com.example.dailychallenge.exception.comment.CommentCreateNotValid;
import com.example.dailychallenge.exception.comment.CommentNotFound;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.vo.ResponseChallengeComment;
import com.example.dailychallenge.vo.ResponseChallengeCommentImg;
import com.example.dailychallenge.vo.ResponseUserComment;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentImgService commentImgService;

    public Comment findById(Long id){
        return commentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Comment saveComment(CommentDto commentDto, List<MultipartFile> commentImgFiles, User user,
                               Challenge challenge) {

        isCommentContentOrImagesNotNull(commentDto, commentImgFiles);

        CommentBuilder commentBuilder = Comment.builder();
        if (commentDto != null) {
            commentBuilder
                    .content(commentDto.getContent());
        }
        Comment comment = commentBuilder
                .build();

        comment.saveCommentChallenge(challenge);
        comment.saveCommentUser(user);

        commentRepository.save(comment);

        if (commentImgFiles != null) {
            for (MultipartFile commentImgFile : commentImgFiles) {
                CommentImg commentImg = new CommentImg();
                commentImg.saveComment(comment);
                commentImgService.saveCommentImg(commentImg, commentImgFile);
            }
        }
        return comment;
    }

    private void isCommentContentOrImagesNotNull(CommentDto commentDto, List<MultipartFile> commentImgFiles) {
        if (commentDto == null && commentImgFiles == null) {
            throw new CommentCreateNotValid();
        }
    }

    public Comment updateComment(Long challengeId, Long commentId, CommentDto commentDto,
                                 List<MultipartFile> commentImgFiles, User user) {

        isCommentContentOrImagesNotNull(commentDto, commentImgFiles);

        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFound::new);
        validateOwner(user, comment);
        validateChallenge(challengeId, comment);

        if (commentDto != null) {
            comment.updateComment(commentDto.getContent());
        }

        if (commentImgFiles != null) {
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

        return comment;
    }

    public void deleteComment(Long challengeId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFound::new);
        validateOwner(user, comment);
        validateChallenge(challengeId, comment);

        commentRepository.delete(comment);
    }


    public Page<ResponseChallengeComment> searchCommentsByChallengeId(Challenge challenge, Pageable pageable) {

        Long challengeId = challenge.getId();
        return commentRepository.searchCommentsByChallengeId(challengeId, pageable);
    }

    public Page<ResponseUserComment> searchCommentsByUserId(Long userId, Pageable pageable) {

        return commentRepository.searchCommentsByUserId(userId, pageable);
    }

    public Page<ResponseChallengeCommentImg> searchCommentsByUserIdByChallengeId(User user, Challenge challenge, Pageable pageable) {
        Long userId = user.getId();
        Long challengeId = challenge.getId();
        return commentRepository.searchCommentsByUserIdByChallengeId(userId, challengeId, pageable);
    }

    public void validateOwner(User user, Comment comment) {
        if (!comment.isOwner(user.getId())) {
            throw new AuthorizationException();
        }
    }

    public void validateChallenge(Long challengeId, Comment comment) {
        if (!comment.isValidChallenge(challengeId)) {
            throw new AuthorizationException();
        }
    }
}
