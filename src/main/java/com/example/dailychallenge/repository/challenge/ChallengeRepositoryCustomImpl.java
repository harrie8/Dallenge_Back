package com.example.dailychallenge.repository.challenge;

import static com.example.dailychallenge.entity.challenge.QChallenge.challenge;
import static com.example.dailychallenge.entity.challenge.QUserChallenge.userChallenge;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.QChallenge;
import com.example.dailychallenge.entity.challenge.QUserChallenge;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.hashtag.QChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.QHashtag;
import com.example.dailychallenge.exception.CommonException;
import com.example.dailychallenge.vo.challenge.QResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    public Page<ResponseChallenge> searchChallengeByHashtag(String content, Pageable pageable) {
        QueryResults<ResponseChallenge> results = queryFactory
                .select(new QResponseChallenge(challenge))
                .from(QChallengeHashtag.challengeHashtag)
                .where(QChallengeHashtag.challengeHashtag.hashtag.content.eq(content))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<ResponseChallenge> responseChallenges = results.getResults();
        return new PageImpl<>(responseChallenges,pageable,results.getTotal());
    }


    private BooleanExpression challengeIdEq(Long challengeId) {
        if (challengeId == null) {
            throw new CommonException("challengeId is Null");
        }
        return userChallenge.challenge.id.eq(challengeId);
    }
}
