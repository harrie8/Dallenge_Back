package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag,Long>{
    Hashtag findByContent(String content);

}
