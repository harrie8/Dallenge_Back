package com.example.dailychallenge.repository.challenge;

import static com.example.dailychallenge.entity.challenge.QChallenge.challenge;
import static com.example.dailychallenge.entity.challenge.QUserChallenge.userChallenge;

import com.example.dailychallenge.vo.QResponseChallenge;
import com.example.dailychallenge.vo.ResponseChallenge;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Pageable;

public class UserChallengeRepositoryCustomImpl implements
        UserChallengeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserChallengeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ResponseChallenge> searchAllChallengesByPopularWithPaging(Pageable pageable) {
        return queryFactory
                .select(new QResponseChallenge(userChallenge.challenge, userChallenge.count()))
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
                .groupBy(userChallenge.challenge)
                .orderBy(userChallenge.count().desc(), userChallenge.challenge.title.asc()) // 먼저 생성된 챌린지로 정렬하기
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

}
