package com.example.dailychallenge.controller;

import java.io.File;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImgController {

    @Value("${imgReadPath}")
    private String readPath;

    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
        ResponseEntity<byte[]> result;
        String filePathName = readPath + "/" + imageId;

        try {
            File file = new File(filePathName);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", Files.probeContentType(file.toPath()));
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}