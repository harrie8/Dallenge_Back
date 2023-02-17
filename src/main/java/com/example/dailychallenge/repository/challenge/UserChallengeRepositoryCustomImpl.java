package com.example.dailychallenge.repository.challenge;

import static com.example.dailychallenge.entity.challenge.QChallenge.challenge;
import static com.example.dailychallenge.entity.challenge.QUserChallenge.userChallenge;
import static org.aspectj.util.LangUtil.isEmpty;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.exception.CommonException;
import com.example.dailychallenge.vo.challenge.QResponseChallenge;
import com.example.dailychallenge.vo.challenge.QResponseUserChallenge;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseUserChallenge;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class UserChallengeRepositoryCustomImpl implements
        UserChallengeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserChallengeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<UserChallenge> findByChallengeIdAndUserId(Long challengeId, Long userId) {
        UserChallenge findUserChallenge = queryFactory
                .selectFrom(userChallenge)
                .where(
                        challengeIdEq(challengeId),
                        userIdEq(userId)
                )
                .fetchOne();

        return Optional.ofNullable(findUserChallenge);
    }

    @Override
    public List<ResponseUserChallenge> searchUserChallengeByChallengeId(Long challengeId) {
        return queryFactory
                .select(new QResponseUserChallenge(userChallenge))
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
                .where(challengeIdEq(challengeId))
                .fetch();
    }

    @Override
    public Page<ResponseChallenge> searchAllChallenges(Pageable pageable) {
        List<ResponseChallenge> content = queryFactory
                .select(new QResponseChallenge(userChallenge.challenge, userChallenge.count()))
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
//                .leftJoin(userChallenge.challenge.challengeImgs, challengeImg)
                .groupBy(userChallenge.challenge)
//                .orderBy(userChallenge.count().desc(), userChallenge.challenge.created_at.asc())
                .orderBy(challengesSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(userChallenge.challenge)
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
//                .leftJoin(userChallenge.challenge.challengeImgs, challengeImg)
                .groupBy(userChallenge.challenge)
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ResponseChallenge> searchChallengesByCondition(ChallengeSearchCondition condition, Pageable pageable) {

        List<ResponseChallenge> content = queryFactory
                .select(new QResponseChallenge(userChallenge.challenge, userChallenge.count()))
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
//                .leftJoin(userChallenge.challenge.challengeImgs, challengeImg)
                .where(titleContains(condition.getTitle()),
                        categoryEq(condition.getCategory()))
                .groupBy(userChallenge.challenge)
//                .orderBy(userChallenge.count().desc(), userChallenge.challenge.created_at.asc())
                .orderBy(challengesSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(userChallenge.challenge)
                .from(userChallenge)
                .leftJoin(userChallenge.challenge, challenge)
//                .leftJoin(userChallenge.challenge.challengeImgs, challengeImg)
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

    private BooleanExpression challengeIdEq(Long challengeId) {
        if (challengeId == null) {
            throw new CommonException("challengeId is Null");
        }
        return userChallenge.challenge.id.eq(challengeId);
    }

    private BooleanExpression userIdEq(Long userId) {
        if (userId == null) {
            throw new CommonException("userId is Null");
        }
        return userChallenge.users.id.eq(userId);
    }

    private OrderSpecifier<?> challengesSort(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return OrderByNull.getDefault();
        }
        for (Sort.Order order : pageable.getSort()) {
//            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC; // 새로운 정렬 조건이 추가되면 처리하자
            if (order.getProperty().equals("time")) {
//                return new OrderSpecifier<>(direction, challenge.created_at);
                return new OrderSpecifier<>(Order.DESC, challenge.created_at);
            }
            if (order.getProperty().equals("popular")) {
                return new OrderSpecifier<>(Order.DESC, userChallenge.count());
            }
        }
        return OrderByNull.getDefault();
    }
}
