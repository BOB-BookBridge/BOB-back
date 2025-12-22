package com.bob.core.trade.domain.repository.dsl;

import static com.bob.core.trade.domain.QTrade.trade;

import java.util.List;
import java.util.UUID;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.repository.dsl.query.ReadTradesQuery;
import com.bob.core.trade.domain.repository.dsl.query.SearchKey;
import com.bob.core.trade.domain.status.Status;

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
