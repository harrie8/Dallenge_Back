package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import java.util.List;

public interface ChallengeHashtagRepositoryCustom {

    List<ChallengeHashtag> searchByHashtags(List<Hashtag> hashtags);
}
