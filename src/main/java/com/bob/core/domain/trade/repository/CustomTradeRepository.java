package com.bob.core.domain.trade.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.bob.core.application.trade.dto.query.ReadTradesQuery;
import com.bob.core.domain.trade.Trade;

public interface CustomTradeRepository {

    List<Trade> findTradesByQuery(ReadTradesQuery query, Pageable pageable);

    Long countTradesByQuery(ReadTradesQuery query);
}
