package com.example.dailychallenge.repository.challenge;

import static com.example.dailychallenge.entity.challenge.QChallenge.challenge;
import static com.example.dailychallenge.entity.challenge.QUserChallenge.userChallenge;

import com.example.dailychallenge.exception.CommonException;
import com.example.dailychallenge.vo.challenge.QResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import javax.persistence.EntityManager;

public class ChallengeRepositoryCustomImpl implements
        ChallengeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ChallengeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<ResponseChallenge> searchChallengeById(Long challengeId) {
        ResponseChallenge responseChallenge = queryFactory
                .select(new QResponseChallenge(userChallenge.challenge, userChallenge.count()))
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
//                .leftJoin(userChallenge.challenge.challengeImgs, challengeImg) // cascade refresh로 하위 데이터를 가져옴
                .where(challengeIdEq(challengeId))
                .groupBy(userChallenge.challenge)
                .fetchOne();
        return Optional.ofNullable(responseChallenge);
    }

    private BooleanExpression challengeIdEq(Long challengeId) {
        if (challengeId == null) {
            throw new CommonException("challengeId is Null");
        }
        return userChallenge.challenge.id.eq(challengeId);
    }
}
