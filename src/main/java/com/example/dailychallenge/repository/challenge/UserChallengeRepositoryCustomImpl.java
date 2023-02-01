package com.example.dailychallenge.repository.challenge;

import static com.example.dailychallenge.entity.challenge.QChallenge.challenge;
import static com.example.dailychallenge.entity.challenge.QChallengeImg.challengeImg;
import static com.example.dailychallenge.entity.challenge.QUserChallenge.userChallenge;
import static org.aspectj.util.LangUtil.isEmpty;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.vo.QResponseChallenge;
import com.example.dailychallenge.vo.ResponseChallenge;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class UserChallengeRepositoryCustomImpl implements
        UserChallengeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserChallengeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ResponseChallenge> searchAllChallengesSortByPopularWithPaging(Pageable pageable) {
        List<ResponseChallenge> content = queryFactory
                .select(new QResponseChallenge(userChallenge.challenge, userChallenge.count()))
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
                .leftJoin(userChallenge.challenge.challengeImg, challengeImg)
                .groupBy(userChallenge.challenge)
                .orderBy(userChallenge.count().desc()) // 먼저 생성된 챌린지로 정렬하기
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(userChallenge.challenge)
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
                .leftJoin(userChallenge.challenge.challengeImg, challengeImg)
                .groupBy(userChallenge.challenge)
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ResponseChallenge> searchChallengesByConditionSortByPopularWithPaging(
            ChallengeSearchCondition condition,
            Pageable pageable) {

        List<ResponseChallenge> content = queryFactory
                .select(new QResponseChallenge(userChallenge.challenge, userChallenge.count()))
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
                .leftJoin(userChallenge.challenge.challengeImg, challengeImg)
                .where(titleContains(condition.getTitle()),
                        categoryEq(condition.getCategory()))
                .groupBy(userChallenge.challenge)
                .orderBy(userChallenge.count().desc()) // 먼저 생성된 챌린지로 정렬하기
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(userChallenge.challenge)
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
                .leftJoin(userChallenge.challenge.challengeImg, challengeImg)
                .where(titleContains(condition.getTitle()),
                        categoryEq(condition.getCategory()))
                .groupBy(userChallenge.challenge)
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression titleContains(String title) {
        return isEmpty(title) ? null : userChallenge.challenge.title.contains(title);
    }

    private BooleanExpression categoryEq(String category) {
        if (category == null || isEmpty(category)) {
            return null;
        }
        ChallengeCategory challengeCategory = ChallengeCategory.findByDescription(category);
        return userChallenge.challenge.challengeCategory.eq(challengeCategory);
    }
}
