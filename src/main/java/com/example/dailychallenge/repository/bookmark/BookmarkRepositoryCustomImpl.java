package com.example.dailychallenge.repository.bookmark;

import static com.example.dailychallenge.entity.bookmark.QBookmark.bookmark;
import static com.example.dailychallenge.entity.challenge.QChallenge.challenge;
import static com.example.dailychallenge.entity.users.QUser.user;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.exception.CommonException;
import com.example.dailychallenge.repository.challenge.OrderByNull;
import com.example.dailychallenge.vo.bookmark.QResponseBookmark;
import com.example.dailychallenge.vo.bookmark.ResponseBookmark;
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

public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BookmarkRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ResponseBookmark> searchBookmarksByUserId(Long userId, Pageable pageable) {
        List<ResponseBookmark> content = queryFactory
                .select(new QResponseBookmark(bookmark))
                .from(bookmark)
                .leftJoin(bookmark.users, user)
                .leftJoin(bookmark.challenge, challenge)
                .where(userIdEq(userId))
                .orderBy(bookmarkSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(bookmark)
                .from(bookmark)
                .leftJoin(bookmark.users, user)
                .leftJoin(bookmark.challenge, challenge)
                .where(userIdEq(userId))
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<Bookmark> findByUserIdAndChallengeId(Long userId, Long challengeId) {
        Bookmark findBookmark = queryFactory
                .select(bookmark)
                .from(bookmark)
                .leftJoin(bookmark.users, user)
                .leftJoin(bookmark.challenge, challenge)
                .where(userIdEq(userId), challengeIdEq(challengeId))
                .fetchOne();

        return Optional.ofNullable(findBookmark);
    }

    private BooleanExpression userIdEq(Long userId) {
        if (userId == null) {
            throw new CommonException("userId is Null");
        }
        return bookmark.users.id.eq(userId);
    }

    private BooleanExpression challengeIdEq(Long challengeId) {
        if (challengeId == null) {
            throw new CommonException("challengeId is Null");
        }
        return bookmark.challenge.id.eq(challengeId);
    }

    private OrderSpecifier<?> bookmarkSort(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return OrderByNull.getDefault();
        }
        for (Sort.Order order : pageable.getSort()) {
//            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC; // 새로운 정렬 조건이 추가되면 처리하자
            if (order.getProperty().equals("time")) {
//                return new OrderSpecifier<>(direction, challenge.created_at);
                return new OrderSpecifier<>(Order.DESC, bookmark.created_at);
            }
        }
        return OrderByNull.getDefault();
    }
}
