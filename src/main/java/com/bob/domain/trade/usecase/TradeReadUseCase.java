package com.bob.domain.trade.usecase;

import com.bob.domain.trade.service.dto.query.ReadPostTradesQuery;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.PostTradesResponse;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import org.springframework.data.domain.Pageable;

public interface TradeReadUseCase {

  PostTradesResponse readPostTradesProcess(ReadPostTradesQuery query);

  TradesResponse readTradesProcess(ReadTradesQuery query, Pageable pageable);

  TradeDetailResponse readTradeDetailProcess(ReadTradeDetailQuery query);
}
