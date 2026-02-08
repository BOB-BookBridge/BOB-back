package com.bob.core.post.domain.repository.dsl;

import static com.bob.core.post.domain.QPost.post;
import static com.bob.core.post.domain.QPostFavorite.postFavorite;

import java.util.List;
import java.util.UUID;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.dsl.query.ReadPostsQuery;
import com.bob.core.post.domain.repository.dsl.query.SearchManagementPostsQuery;
import com.bob.core.post.domain.repository.dsl.query.SearchPrice;
import com.bob.core.post.domain.repository.dsl.query.SortKey;
import com.bob.core.post.domain.status.BookStatus;
import com.bob.core.post.domain.status.Status;
import com.bob.core.post.domain.status.TradeProgress;

@RequiredArgsConstructor
@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findFilteredPosts(ReadPostsQuery query, Pageable pageable) {
        return queryFactory
            .selectFrom(post)
            .where(
                visibleCondition(query.authenticatorId(), query.memberId()),
                bookIdsCondition(query.bookIds()),
                memberIdCondition(query.memberId()),
                emdCondition(query.emdId()),
                categoryCondition(query.categoryIds()),
                priceCondition(query.price()),
                tradeStatusCondition(query.postStatus()),
                bookStatusCondition(query.bookStatus())
            )
            .orderBy(getSortKey(query.sortKey()), post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Long countFilteredPosts(ReadPostsQuery query) {
        return queryFactory
            .select(post.count())
            .from(post)
            .where(
                visibleCondition(query.authenticatorId(), query.memberId()),
                bookIdsCondition(query.bookIds()),
                memberIdCondition(query.memberId()),
                emdCondition(query.emdId()),
                categoryCondition(query.categoryIds()),
                priceCondition(query.price()),
                tradeStatusCondition(query.postStatus()),
                bookStatusCondition(query.bookStatus())
            )
            .fetchOne();
    }

    private BooleanExpression visibleCondition(UUID authenticatorId, UUID memberId) {
        boolean isOwnPosts = authenticatorId != null && authenticatorId.equals(memberId);

        if (isOwnPosts) {
            return post.status.notIn(Status.DEACTIVATED, Status.BANNED);
        }
        return post.status.notIn(Status.DEACTIVATED, Status.BANNED, Status.PENDING);
    }

    private BooleanExpression bookIdsCondition(List<Long> bookIds) {
        if (bookIds == null) {
            return null;
        }
        if (bookIds.isEmpty()) {
            return post.id.isNull();
        }
        return post.bookId.in(bookIds);
    }

    private BooleanExpression memberIdCondition(UUID memberId) {
        return memberId != null ? post.writerId.eq(memberId) : null;
    }

    private BooleanExpression emdCondition(Integer emdId) {
        return emdId == null ? null : post.registrationAreaId.eq(emdId);
    }

    private BooleanExpression categoryCondition(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        return post.categoryId.in(categoryIds);
    }

    private BooleanExpression priceCondition(SearchPrice price) {
        if (price == null)
            return null;
        return post.price.goe(price.getMinPrice()).and(post.price.loe(price.getMaxPrice()));
    }

    private BooleanExpression tradeStatusCondition(String status) {
        return !StringUtils.hasText(status) ? null : post.tradeProgress.eq(TradeProgress.valueOf(status));
    }

    private BooleanExpression bookStatusCondition(String status) {
        return !StringUtils.hasText(status) ? null : post.bookStatus.eq(BookStatus.valueOf(status));
    }

    private OrderSpecifier<?> getSortKey(SortKey sort) {
        if (sort == null) {
            return null;
        }

        return switch (sort) {
            case RECENT -> post.createdAt.desc();
            case OLD -> post.createdAt.asc();
            case LOW_PRICE -> post.price.asc();
            case HIGH_PRICE -> post.price.desc();
        };
    }

    @Override
    public List<Post> findPostsWithFavoriteByMemberId(UUID memberId, Pageable pageable) {
        return queryFactory
            .selectFrom(post)
            .join(post.favorites, postFavorite)
            .where(postFavorite.memberId.eq(memberId))
            .orderBy(postFavorite.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Long countPostsWithFavoriteByMemberId(UUID memberId) {
        return queryFactory
            .select(post.count())
            .from(post)
            .join(post.favorites, postFavorite)
            .where(postFavorite.memberId.eq(memberId))
            .fetchOne();
    }

    @Override
    public List<Post> searchPosts(SearchManagementPostsQuery query, Pageable pageable) {
        return queryFactory
            .selectFrom(post)
            .where(
                managementStatusCondition(query.status()),
                writerIdCondition(query.writerId())
            )
            .orderBy(
                post.status.when(Status.PENDING).then(0).otherwise(1).asc(),
                post.id.desc()
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Long countSearchedPosts(SearchManagementPostsQuery query) {
        return queryFactory
            .select(post.count())
            .from(post)
            .where(
                managementStatusCondition(query.status()),
                writerIdCondition(query.writerId())
            )
            .fetchOne();
    }

    private BooleanExpression managementStatusCondition(String status) {
        if (StringUtils.hasText(status))
            return post.status.eq(Status.valueOf(status));

        return post.status.in(Status.PENDING, Status.BANNED);
    }

    private BooleanExpression writerIdCondition(UUID writerId) {
        return writerId != null ? post.writerId.eq(writerId) : null;
    }
}
