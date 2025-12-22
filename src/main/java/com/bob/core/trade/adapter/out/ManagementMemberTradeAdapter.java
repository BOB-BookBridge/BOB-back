package com.bob.core.trade.adapter.out;

import static com.bob.core.trade.domain.status.Status.COMPLETED;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bob.admin.member.application.port.out.ManagementMemberTradePort;
import com.bob.admin.member.application.port.result.activity.ManagementMemberTrade;
import com.bob.core.trade.application.dto.result.TradeSummaries;
import com.bob.core.trade.application.dto.result.TradeSummary;
import com.bob.core.trade.application.port.in.TradeReader;
import com.bob.core.trade.domain.repository.dsl.query.ReadTradesQuery;
import com.bob.core.trade.domain.repository.dsl.query.SearchKey;

@Component
@RequiredArgsConstructor
public class ManagementMemberTradeAdapter implements ManagementMemberTradePort {

    private final TradeReader tradeReader;

    @Override
    public ManagementMemberTrade read(UUID traderId) {
        ReadTradesQuery query = new ReadTradesQuery(traderId, SearchKey.ALL, List.of(COMPLETED));

        TradeSummaries summaries = tradeReader.readTrades(query, Pageable.ofSize(999));

        Map<Boolean, List<TradeSummary>> partitioned = summaries.trades().stream()
            .collect(Collectors.partitioningBy(trade -> trade.seller().id().equals(traderId)));

        List<Long> sold = partitioned.get(true).stream().map(TradeSummary::id).toList();
        List<Long> bought = partitioned.get(false).stream().map(TradeSummary::id).toList();

        return new ManagementMemberTrade(summaries.totalCount(), sold, bought);
    }
}
