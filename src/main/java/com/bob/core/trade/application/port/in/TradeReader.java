package com.bob.core.trade.application.port.in;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.bob.core.trade.application.dto.query.ReadPostTradeQuery;
import com.bob.core.trade.application.dto.query.ReadPostTradesQuery;
import com.bob.core.trade.application.dto.query.ReadTradeDetailQuery;
import com.bob.core.trade.application.dto.query.ReadTradeStatusMapQuery;
import com.bob.core.trade.application.dto.result.TradeDetail;
import com.bob.core.trade.application.dto.result.TradeSummaries;
import com.bob.core.trade.application.dto.result.internal.PostTrade;
import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.repository.dsl.query.ReadTradesQuery;

public interface TradeReader {

    Trade read(Long id);

    Optional<Trade> readByPostAndMember(ReadPostTradeQuery query);

    List<PostTrade> readPostTradeSummaries(ReadPostTradesQuery query);

    TradeSummaries readTrades(ReadTradesQuery query, Pageable pageable);

    TradeDetail readTradeDetail(Long id, ReadTradeDetailQuery query);

    Map<Long, String> readTradeStatusMap(ReadTradeStatusMapQuery query);

    String readTradeStatus(Long id);
}
