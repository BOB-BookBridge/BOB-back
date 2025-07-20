package com.bob.domain.trade.usecase;

import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradesResponse;

public interface TradeReadUseCase {

  TradesResponse readTradesProcess(ReadTradesQuery query);

  TradeDetailResponse readTradeDetailProcess(ReadTradeDetailQuery query);
}
