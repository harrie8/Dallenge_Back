package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.hashtag.Hashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag,Long>{
    Hashtag findByContent(String content);

    List<Hashtag> findTop3ByOrderByTagCountDescContentAsc();
}
