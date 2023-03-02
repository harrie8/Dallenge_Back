package com.example.dailychallenge.repository.challenge;

import static com.example.dailychallenge.entity.challenge.QChallenge.challenge;
import static com.example.dailychallenge.entity.challenge.QUserChallenge.userChallenge;

import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.exception.CommonException;
import com.example.dailychallenge.vo.challenge.QResponseChallenge;
import com.example.dailychallenge.vo.challenge.QResponseRecommendedChallenge;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseRecommendedChallenge;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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

    @Override
    public List<ResponseRecommendedChallenge> searchChallengesByQuestion(ChallengeCategory challengeCategory,
                                                                         ChallengeDuration challengeDuration,
                                                                         ChallengeLocation challengeLocation) {

        return queryFactory
                .select(new QResponseRecommendedChallenge(challenge))
                .from(challenge)
                .where(
                        challengeCategoryEq(challengeCategory),
                        challengeDurationEq(challengeDuration),
                        challengeLocationEq(challengeLocation))
                .limit(4)
                .fetch();
    }

    private BooleanExpression challengeIdEq(Long challengeId) {
        if (challengeId == null) {
            throw new CommonException("challengeId is Null");
        }
        return userChallenge.challenge.id.eq(challengeId);
    }

    private BooleanExpression challengeCategoryEq(ChallengeCategory challengeCategory) {
        if (challengeCategory == null) {
            throw new CommonException("challengeCategory is Null");
        }
        return challenge.challengeCategory.eq(challengeCategory);
    }

    private BooleanExpression challengeDurationEq(ChallengeDuration challengeDuration) {
        if (challengeDuration == null) {
            throw new CommonException("challengeDuration is Null");
        }
        return challenge.challengeDuration.eq(challengeDuration);
    }

    private BooleanExpression challengeLocationEq(ChallengeLocation challengeLocation) {
        if (challengeLocation == null) {
            throw new CommonException("challengeLocation is Null");
        }
        return challenge.challengeLocation.eq(challengeLocation);
    }
}
