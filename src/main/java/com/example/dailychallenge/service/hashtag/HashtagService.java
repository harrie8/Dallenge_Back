package com.example.dailychallenge.service.hashtag;

import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public List<Hashtag> saveHashtag(List<String> hashtagDto){

        List<Hashtag> hashtags = new ArrayList<>();

        for (String tag : hashtagDto) {
            Hashtag hashtag = new Hashtag();
            try {                               // 이미 태그 내용이 존재하는 경우
                hashtag = hashtagRepository.findByContent(tag);
                hashtag.updateTagCount();
            } catch (NullPointerException e) {
                hashtag = Hashtag.builder()
                            .content(tag)
                            .build();
                hashtagRepository.save(hashtag);
            } finally {
                hashtags.add(hashtag);
            }
        }
        return hashtags;
    }

}
