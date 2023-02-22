package com.example.dailychallenge.controller.exception;

import com.example.dailychallenge.exception.AuthorizationException;
import com.example.dailychallenge.exception.CommonException;
import com.example.dailychallenge.exception.FileNotUpload;
import com.example.dailychallenge.exception.bookmark.BookmarkDuplicate;
import com.example.dailychallenge.exception.bookmark.BookmarkNotFound;
import com.example.dailychallenge.exception.challenge.ChallengeCategoryNotFound;
import com.example.dailychallenge.exception.challenge.ChallengeNotFound;
import com.example.dailychallenge.exception.comment.CommentDtoNotValid;
import com.example.dailychallenge.exception.comment.CommentImgNotFound;
import com.example.dailychallenge.exception.comment.CommentNotFound;
import com.example.dailychallenge.exception.hashtag.HashTagNotFound;
import com.example.dailychallenge.exception.hashtag.HashtagDtoBlank;
import com.example.dailychallenge.exception.userChallenge.UserChallengeDuplicate;
import com.example.dailychallenge.exception.users.UserDuplicateCheck;
import com.example.dailychallenge.exception.users.UserDuplicateNotCheck;
import com.example.dailychallenge.exception.users.UserImgNotFound;
import com.example.dailychallenge.exception.users.UserLoginFailure;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.exception.users.UserPasswordCheck;
import com.example.dailychallenge.vo.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonException.class)
    protected ResponseEntity<ResponseError> handlerCommonException(CommonException commonException) {
        final ResponseError responseError = ResponseError.builder()
                .code(commonException.getStatusCode())
                .message(commonException.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(ChallengeCategoryNotFound.class)
    protected ResponseEntity<ResponseError> handlerChallengeCategoryNotFound(ChallengeCategoryNotFound challengeCategoryNotFound) {
        final ResponseError responseError = ResponseError.builder()
                .code(challengeCategoryNotFound.getStatusCode())
                .message(challengeCategoryNotFound.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(FileNotUpload.class)
    protected ResponseEntity<ResponseError> handlerFileNotUpload(FileNotUpload fileNotUpload) {
        final ResponseError responseError = ResponseError.builder()
                .code(fileNotUpload.getStatusCode())
                .message(fileNotUpload.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(UserDuplicateCheck.class)
    protected ResponseEntity<ResponseError> handlerUserIdDuplicate(UserDuplicateCheck userDuplicateCheck) {
        final ResponseError responseError = ResponseError.builder()
                .code(userDuplicateCheck.getStatusCode())
                .message(userDuplicateCheck.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(UserDuplicateNotCheck.class)
    protected ResponseEntity<ResponseError> handlerUserDuplicateNotCheck(UserDuplicateNotCheck userDuplicateNotCheck) {
        final ResponseError responseError = ResponseError.builder()
                .code(userDuplicateNotCheck.getStatusCode())
                .message(userDuplicateNotCheck.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(UserLoginFailure.class)
    protected ResponseEntity<ResponseError> handlerUserLoginFailure(UserLoginFailure userLoginFailure) {
        final ResponseError responseError = ResponseError.builder()
                .code(userLoginFailure.getStatusCode())
                .message(userLoginFailure.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(UserPasswordCheck.class)
    protected ResponseEntity<ResponseError> handlerUserPasswordCheck(UserPasswordCheck userPasswordCheck) {
        final ResponseError responseError = ResponseError.builder()
                .code(userPasswordCheck.getStatusCode())
                .message(userPasswordCheck.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)  // 유효성 검증에 대한 예외처리
    public ResponseEntity<ResponseError> processValidationError(MethodArgumentNotValidException ex) {
        final ResponseError responseError = ResponseError.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .build();
        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(ChallengeNotFound.class)
    protected ResponseEntity<ResponseError> handlerChallengeNotFound(ChallengeNotFound challengeNotFound) {
        final ResponseError responseError = ResponseError.builder()
                .code(challengeNotFound.getStatusCode())
                .message(challengeNotFound.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(AuthorizationException.class)
    protected ResponseEntity<ResponseError> handlerAuthorizationException(
            AuthorizationException authorizationException) {
        final ResponseError responseError = ResponseError.builder()
                .code(authorizationException.getStatusCode())
                .message(authorizationException.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(BookmarkNotFound.class)
    protected ResponseEntity<ResponseError> handlerBookmarkNotFound(
            BookmarkNotFound bookmarkNotFound) {
        final ResponseError responseError = ResponseError.builder()
                .code(bookmarkNotFound.getStatusCode())
                .message(bookmarkNotFound.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(BookmarkDuplicate.class)
    protected ResponseEntity<ResponseError> handlerBookmarkDuplicate(
            BookmarkDuplicate bookmarkDuplicate) {
        final ResponseError responseError = ResponseError.builder()
                .code(bookmarkDuplicate.getStatusCode())
                .message(bookmarkDuplicate.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(CommentDtoNotValid.class)
    protected ResponseEntity<ResponseError> handlerCommentDtoNotValid(
            CommentDtoNotValid commentDtoNotValid) {
        final ResponseError responseError = ResponseError.builder()
                .code(commentDtoNotValid.getStatusCode())
                .message(commentDtoNotValid.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(CommentImgNotFound.class)
    protected ResponseEntity<ResponseError> handlerCommentImgNotFound(
            CommentImgNotFound commentImgNotFound) {
        final ResponseError responseError = ResponseError.builder()
                .code(commentImgNotFound.getStatusCode())
                .message(commentImgNotFound.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(CommentNotFound.class)
    protected ResponseEntity<ResponseError> handlerCommentNotFound(
            CommentNotFound commentNotFound) {
        final ResponseError responseError = ResponseError.builder()
                .code(commentNotFound.getStatusCode())
                .message(commentNotFound.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(HashTagNotFound.class)
    protected ResponseEntity<ResponseError> handlerHashTagNotFound(
            HashTagNotFound hashTagNotFound) {
        final ResponseError responseError = ResponseError.builder()
                .code(hashTagNotFound.getStatusCode())
                .message(hashTagNotFound.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(UserImgNotFound.class)
    protected ResponseEntity<ResponseError> handlerUserImgNotFound(
            UserImgNotFound userImgNotFound) {
        final ResponseError responseError = ResponseError.builder()
                .code(userImgNotFound.getStatusCode())
                .message(userImgNotFound.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(UserNotFound.class)
    protected ResponseEntity<ResponseError> handlerUserNotFound(
            UserNotFound userNotFound) {
        final ResponseError responseError = ResponseError.builder()
                .code(userNotFound.getStatusCode())
                .message(userNotFound.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(UserChallengeDuplicate.class)
    protected ResponseEntity<ResponseError> handlerUserChallengeDuplicate(
            UserChallengeDuplicate userChallengeDuplicate) {
        final ResponseError responseError = ResponseError.builder()
                .code(userChallengeDuplicate.getStatusCode())
                .message(userChallengeDuplicate.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }

    @ExceptionHandler(HashtagDtoBlank.class)
    protected ResponseEntity<ResponseError> handlerHashtagDtoBlank(
            HashtagDtoBlank hashtagDtoBlank) {
        final ResponseError responseError = ResponseError.builder()
                .code(hashtagDtoBlank.getStatusCode())
                .message(hashtagDtoBlank.getMessage())
                .build();

        return ResponseEntity.status(responseError.getCode()).body(responseError);
    }
}
