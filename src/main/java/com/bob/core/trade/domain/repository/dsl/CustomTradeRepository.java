package com.bob.core.trade.domain.repository.dsl;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.repository.dsl.query.ReadTradesQuery;

public interface CustomTradeRepository {

    List<Trade> findTradesByQuery(ReadTradesQuery query, Pageable pageable);

    Long countTradesByQuery(ReadTradesQuery query);
}
