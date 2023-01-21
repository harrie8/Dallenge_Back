package com.example.dailychallenge.service;

import com.example.dailychallenge.entity.User;
import com.example.dailychallenge.entity.UserImg;
import com.example.dailychallenge.repository.UserImgRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.querydsl.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class UserImgService {

    private final UserImgRepository userImgRepository;
    private final FileService fileService;

    public void saveUserImg(UserImg userImg, MultipartFile userImgFile) throws Exception {
        String oriImgName = userImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if (!StringUtils.isNullOrEmpty(oriImgName)) {
            imgName = fileService.uploadFile(userImgFile);
            imgUrl = "/images/"+imgName;
        }

        // 이미지 정보 저장
        userImg.updateUserImg(oriImgName,imgName,imgUrl);
        userImgRepository.save(userImg);
        userImg.getUsers().setUserImg(userImg);

    }

    public void updateUserImg(Long userImgId, MultipartFile userImgFile) throws Exception {
        if(!userImgFile.isEmpty()){
            UserImg savedUserImg = userImgRepository.findById(userImgId)
                    .orElseThrow(EntityNotFoundException::new);
            if(!StringUtils.isNullOrEmpty(savedUserImg.getImgName())){
                fileService.deleteFile(savedUserImg.getImgName());
            }

            String oriImgName = userImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(userImgFile);
            String imgUrl = "/images/"+imgName;
            savedUserImg.updateUserImg(oriImgName,imgName,imgUrl);
        }
    }

}
