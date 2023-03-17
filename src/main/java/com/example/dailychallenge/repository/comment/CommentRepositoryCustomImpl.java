package com.example.dailychallenge.repository.comment;

import static com.example.dailychallenge.entity.challenge.QChallenge.challenge;
import static com.example.dailychallenge.entity.comment.QComment.comment;
import static com.example.dailychallenge.entity.users.QUser.user;

import com.example.dailychallenge.exception.CommonException;
import com.example.dailychallenge.repository.challenge.OrderByNull;
import com.example.dailychallenge.vo.QResponseChallengeComment;
import com.example.dailychallenge.vo.QResponseChallengeCommentImg;
import com.example.dailychallenge.vo.QResponseUserComment;
import com.example.dailychallenge.vo.ResponseChallengeComment;
import com.example.dailychallenge.vo.ResponseChallengeCommentImg;
import com.example.dailychallenge.vo.ResponseUserComment;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ResponseChallengeComment> searchCommentsByChallengeId(Long challengeId, Pageable pageable) {

        List<ResponseChallengeComment> content = queryFactory
                .select(new QResponseChallengeComment(comment))
                .from(comment)
                .leftJoin(comment.challenge, challenge)
                .where(challengeIdEq(challengeId))
//                .groupBy(userChallenge.challenge)
                .orderBy(commentSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(comment)
                .from(comment)
                .leftJoin(comment.challenge, challenge)
                .where(challengeIdEq(challengeId))
//                .groupBy(userChallenge.challenge)
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ResponseUserComment> searchCommentsByUserId(Long userId, Pageable pageable) {
        List<ResponseUserComment> content = queryFactory
                .select(new QResponseUserComment(comment))
                .from(comment)
                .leftJoin(comment.users, user)
                .where(userIdEq(userId))
                .orderBy(commentSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(comment)
                .from(comment)
                .leftJoin(comment.users, user)
                .where(userIdEq(userId))
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ResponseChallengeCommentImg> searchCommentsByUserIdByChallengeId(Long userId, Long challengeId,
                                                                                 Pageable pageable) {

        List<ResponseChallengeCommentImg> content = queryFactory
                .select(new QResponseChallengeCommentImg(comment))
                .from(comment)
                .leftJoin(comment.challenge, challenge)
                .leftJoin(comment.users, user)
                .where(
                        challengeIdEq(challengeId),
                        userIdEq(userId)
                )
//                .groupBy(userChallenge.challenge)
                .orderBy(commentSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(comment)
                .from(comment)
                .leftJoin(comment.challenge, challenge)
                .leftJoin(comment.users, user)
                .where(
                        challengeIdEq(challengeId),
                        userIdEq(userId)
                )
//                .groupBy(userChallenge.challenge)
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression challengeIdEq(Long challengeId) {
        if (challengeId == null) {
            throw new CommonException("challengeId is Null");
        }
        return comment.challenge.id.eq(challengeId);
    }

    private BooleanExpression userIdEq(Long userId) {
        if (userId == null) {
            throw new CommonException("userId is Null");
        }
        return comment.users.id.eq(userId);
    }

    private OrderSpecifier<?> commentSort(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return OrderByNull.getDefault();
        }
        for (Sort.Order order : pageable.getSort()) {
//            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC; // 새로운 정렬 조건이 추가되면 처리하자
            if (order.getProperty().equals("time")) {
//                return new OrderSpecifier<>(direction, challenge.created_at);
                return new OrderSpecifier<>(Order.DESC, comment.created_at);
            }
            if (order.getProperty().equals("likes")) {
                return new OrderSpecifier<>(Order.DESC, comment.hearts.size());
            }
        }
        return OrderByNull.getDefault();
    }
}
