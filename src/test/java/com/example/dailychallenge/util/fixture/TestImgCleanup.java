package com.example.dailychallenge.util.fixture;

import java.io.File;
import java.io.IOException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestImgCleanup implements InitializingBean {
    @Value("${userImgLocation}")
    private String imgLocationPath;

    @Override
    public void afterPropertiesSet() {
        File directory = new File(imgLocationPath);
        try {
            FileUtils.cleanDirectory(directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
