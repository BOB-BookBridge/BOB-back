package com.bob.core.domain.trade.repository.dsl;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.repository.dsl.query.ReadTradesQuery;

public interface CustomTradeRepository {

    List<Trade> findTradesByQuery(ReadTradesQuery query, Pageable pageable);

    Long countTradesByQuery(ReadTradesQuery query);
}
