package com.example.dailychallenge.service.comment;

import com.example.dailychallenge.entity.comment.CommentImg;
import com.example.dailychallenge.repository.CommentImgRepository;
import com.example.dailychallenge.service.FileService;
import com.querydsl.core.util.StringUtils;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service @Transactional
@RequiredArgsConstructor
public class CommentImgService {
    private final CommentImgRepository commentImgRepository;

    private final FileService fileService;

    public void saveCommentImg(CommentImg commentImg, MultipartFile commentImgFile) {

        String oriImgName = commentImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if (!StringUtils.isNullOrEmpty(oriImgName)) {
            imgName = fileService.uploadFile(commentImgFile);
            imgUrl = "/images/"+imgName;
        }

        // 이미지 정보 저장
        commentImg.updateCommentImg(oriImgName,imgName,imgUrl);
        commentImgRepository.save(commentImg);
        commentImg.getComment().addCommentImg(commentImg);
    }

    public void updateCommentImg(Long commentImgId, MultipartFile commentImgFile) {
        if(commentImgFile != null){
            CommentImg savedCommentImg = commentImgRepository.findById(commentImgId)
                    .orElseThrow(EntityNotFoundException::new);
            if(!StringUtils.isNullOrEmpty(savedCommentImg.getImgName())){
                fileService.deleteFile(savedCommentImg.getImgName());
            }

            String oriImgName = commentImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(commentImgFile);
            String imgUrl = "/images/"+imgName;
            savedCommentImg.updateCommentImg(oriImgName,imgName,imgUrl);
        }
    }

    public void deleteCommentImg(Long commentImgId) {
        CommentImg savedCommentImg = commentImgRepository.findById(commentImgId)
                .orElseThrow(EntityNotFoundException::new);
        savedCommentImg.getComment().getCommentImgs().remove(savedCommentImg);
        commentImgRepository.delete(savedCommentImg);
        fileService.deleteFile(savedCommentImg.getImgName());
    }
}
