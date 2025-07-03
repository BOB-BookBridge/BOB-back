package com.bob.domain.trade.usecase;

import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;

public interface TradeReadUseCase {

  TradeDetailResponse readTradeDetailProcess(ReadTradeDetailQuery query);
}
