package com.example.dailychallenge.repository;

import static com.example.dailychallenge.entity.hashtag.QChallengeHashtag.challengeHashtag;
import static com.example.dailychallenge.entity.hashtag.QHashtag.hashtag;

import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;

public class ChallengeHashtagRepositoryCustomImpl implements
        ChallengeHashtagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ChallengeHashtagRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ChallengeHashtag> searchByHashtags(List<Hashtag> hashtagIds) {
        return queryFactory
                .select(challengeHashtag)
                .from(challengeHashtag)
                .leftJoin(challengeHashtag.hashtag, hashtag)
                .where(challengeHashtag.hashtag.in(hashtagIds))
                .fetch();
    }
}
