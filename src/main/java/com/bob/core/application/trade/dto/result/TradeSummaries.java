package com.bob.core.application.trade.dto.result;

import java.util.List;

public record TradeSummaries(Long totalCount, List<TradeSummary> trades) {

    public static TradeSummaries of(Long size, List<TradeSummary> tradeSummary) {
        return new TradeSummaries(size, tradeSummary);
    }
}
