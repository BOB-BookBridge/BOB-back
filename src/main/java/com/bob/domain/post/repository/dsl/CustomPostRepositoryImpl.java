package com.bob.domain.post.repository.dsl;

import static com.bob.domain.post.entity.QPost.post;

import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.status.BookStatus;
import com.bob.domain.post.entity.status.PostStatus;
import com.bob.domain.post.service.dto.query.ReadFilteredPostsQuery;
import com.bob.domain.post.service.dto.query.condition.SearchKey;
import com.bob.domain.post.service.dto.query.condition.SearchPrice;
import com.bob.domain.post.service.dto.query.condition.SortKey;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Post> findFilteredPosts(ReadFilteredPostsQuery query, Pageable pageable) {
    return queryFactory
        .selectFrom(post)
        .where(
            keywordCondition(query.key(), query.keyword()),
            memberIdCondition(query.memberId()),
            emdCondition(query.emdId()),
            categoryCondition(query.categoryId()),
            priceCondition(query.price()),
            tradeStatusCondition(query.postStatus()),
            bookStatusCondition(query.bookStatus())
        )
        .orderBy(getSortKey(query.sortKey()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }

  @Override
  public Long countFilteredPosts(ReadFilteredPostsQuery query) {
    return queryFactory
        .select(post.count())
        .from(post)
        .where(
            keywordCondition(query.key(), query.keyword()),
            memberIdCondition(query.memberId()),
            emdCondition(query.emdId()),
            categoryCondition(query.categoryId()),
            priceCondition(query.price()),
            tradeStatusCondition(query.postStatus()),
            bookStatusCondition(query.bookStatus())
        )
        .fetchOne();
  }

  private BooleanExpression keywordCondition(SearchKey key, String keyword) {
    if (!StringUtils.hasText(keyword)) {
      return null;
    }

    return switch (key) {
      case TITLE -> post.book.title.containsIgnoreCase(keyword);
      case AUTHOR -> post.book.author.containsIgnoreCase(keyword);
      case ALL -> post.book.title.containsIgnoreCase(keyword).or(post.book.author.containsIgnoreCase(keyword));
    };
  }

  private BooleanExpression memberIdCondition(UUID memberId) {
    return memberId != null ? post.sellerId.eq(memberId) : null;
  }

  private BooleanExpression emdCondition(Integer emdId) {
    return emdId == null ? null : post.registrationAreaId.eq(emdId);
  }

  private BooleanExpression categoryCondition(Integer category) {
    return category == null ? null : post.category.id.eq(category);
  }

  private BooleanExpression priceCondition(SearchPrice price) {
    if (price == null) return null;
    return post.sellPrice.goe(price.getMinPrice()).and(post.sellPrice.loe(price.getMaxPrice()));
  }

  private BooleanExpression tradeStatusCondition(String status) {
    return !StringUtils.hasText(status) ? null : post.postStatus.eq(PostStatus.valueOf(status));
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
      case LOW_PRICE -> post.sellPrice.asc();
      case HIGH_PRICE -> post.sellPrice.desc();
    };
  }
}
