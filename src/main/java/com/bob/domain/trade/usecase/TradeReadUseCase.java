package com.bob.domain.trade.usecase;

import com.bob.domain.trade.service.dto.query.ReadPostTradesQuery;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.response.PostTradesResponse;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;

public interface TradeReadUseCase {

  PostTradesResponse readPostTradesProcess(ReadPostTradesQuery query);

  TradeDetailResponse readTradeDetailProcess(ReadTradeDetailQuery query);
}
