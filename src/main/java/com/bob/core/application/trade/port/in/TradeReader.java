package com.bob.core.application.trade.port.in;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.bob.core.application.trade.dto.query.ReadPostTradeQuery;
import com.bob.core.application.trade.dto.query.ReadPostTradesQuery;
import com.bob.core.application.trade.dto.query.ReadTradeDetailQuery;
import com.bob.core.application.trade.dto.query.ReadTradeStatusMapQuery;
import com.bob.core.application.trade.dto.result.TradeDetail;
import com.bob.core.application.trade.dto.result.TradeSummaries;
import com.bob.core.application.trade.dto.result.internal.PostTrade;
import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.repository.dsl.query.ReadTradesQuery;

public interface TradeReader {

    Trade read(Long id);

    Optional<Trade> readByPostAndMember(ReadPostTradeQuery query);

    List<PostTrade> readPostTradeSummaries(ReadPostTradesQuery query);

    TradeSummaries readTrades(ReadTradesQuery query, Pageable pageable);

    TradeDetail readTradeDetail(Long id, ReadTradeDetailQuery query);

    Map<Long, String> readTradeStatusMap(ReadTradeStatusMapQuery query);

    String readTradeStatus(Long id);
}
