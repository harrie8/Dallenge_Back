package com.example.dailychallenge.service;

import com.example.dailychallenge.exception.FileNotUpload;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service @Log
public class FileService {

    @Value("${userImgLocation}")
    private String userImgLocation;

    public String getFullPath(String filename) {
        return userImgLocation + "/" + filename;
    }

    public String uploadFile(MultipartFile multipartFile) {

        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename(); // image.png

        // 서버에 저장하는 파일명
        String savedFileName = createStoreFileName(originalFilename);
        try {
            multipartFile.transferTo(new File(getFullPath(savedFileName))); // 저장
        } catch (IOException | IllegalStateException e) {
            throw new FileNotUpload();
        }

        return savedFileName;
    }

    private String  createStoreFileName(String originalFilename) { // 서버에 저장되는 파일명
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) { // 확장자 추출
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    public void deleteFile(String filePath){ // 파일 삭제
        File deleteFile = new File(getFullPath(filePath));

        if (deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
