package com.example.dailychallenge.repository;

import static com.example.dailychallenge.entity.challenge.QChallengeImg.challengeImg;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;

public class ChallengeImgRepositoryCustomImpl implements
        ChallengeImgRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ChallengeImgRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public void deleteChallengeImgsByChallengeId(Long challengeId) {
        queryFactory
                .delete(challengeImg)
                .where(challengeIdEq(challengeId))
                .execute();
    }

    private BooleanExpression challengeIdEq(Long challengeId) {
        if (challengeId == null) {
            throw new IllegalArgumentException();
        }
        return challengeImg.challenge.id.eq(challengeId);
    }
}
