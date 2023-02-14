package com.example.dailychallenge.service.users;

import com.example.dailychallenge.entity.users.UserImg;
import com.example.dailychallenge.repository.UserImgRepository;
import com.example.dailychallenge.service.FileService;
import com.querydsl.core.util.StringUtils;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Transactional
public class UserImgService {

    private final UserImgRepository userImgRepository;
    private final FileService fileService;

    public void saveUserImg(UserImg userImg, MultipartFile userImgFile) {
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
        userImg.getUsers().saveDefaultImg(userImg);

    }

    public void updateUserImg(Long userImgId, MultipartFile userImgFile) {
        if(userImgFile != null){
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
