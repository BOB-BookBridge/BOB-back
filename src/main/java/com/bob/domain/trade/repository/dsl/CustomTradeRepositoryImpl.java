package com.bob.domain.trade.repository.dsl;

import static com.bob.domain.trade.entity.QTrade.trade;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.status.Status;
import com.bob.domain.trade.repository.CustomTradeRepository;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.query.SearchKey;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomTradeRepositoryImpl implements CustomTradeRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Trade> findTradesByQuery(ReadTradesQuery query, Pageable pageable) {
    return queryFactory
        .selectFrom(trade)
        .where(keyCondition(query.key(), query.memberId()), statusesCondition(query.statuses()))
        .orderBy(trade.updatedAt.desc(), trade.id.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }

  @Override
  public Long countTradesByQuery(ReadTradesQuery query) {
    return queryFactory
        .select(trade.count())
        .from(trade)
        .where(keyCondition(query.key(), query.memberId()), statusesCondition(query.statuses()))
        .fetchOne();
  }

  private BooleanExpression keyCondition(SearchKey key, UUID memberId) {
    return switch (key) {
      case SENT -> trade.buyerId.eq(memberId);
      case RECEIVED -> trade.sellerId.eq(memberId);
      case ALL -> trade.buyerId.eq(memberId).or(trade.sellerId.eq(memberId));
    };
  }

  private BooleanExpression statusesCondition(List<Status> statuses) {
    if (statuses == null)
      return null;

    return trade.status.in(statuses);
  }
}
