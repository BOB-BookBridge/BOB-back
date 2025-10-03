package com.bob.domain.trade.repository;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomTradeRepository {

  List<Trade> findTradesByQuery(ReadTradesQuery query, Pageable pageable);

  Long countTradesByQuery(ReadTradesQuery query);
}
