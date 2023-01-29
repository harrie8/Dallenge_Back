package com.example.dailychallenge.service.hashtag;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.repository.HashtagRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class HashtagServiceTest {

    @Autowired
    private HashtagService hashtagService;


    @Test
    @DisplayName("해시태그 생성 테스트")
    public void saveHashtagTest() {
        List<String> hashtagDto = List.of("tag1");
        List<Hashtag> hashtags = hashtagService.saveHashtag(hashtagDto);

        assertEquals(hashtagDto.get(0),hashtags.get(0).getContent());
        assertEquals(1,hashtags.get(0).getTagCount());
    }
}