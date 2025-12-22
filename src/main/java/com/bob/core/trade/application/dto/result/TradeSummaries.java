package com.bob.core.trade.application.dto.result;

import java.util.List;

public record TradeSummaries(Long totalCount, List<TradeSummary> trades) {

    public static TradeSummaries of(Long size, List<TradeSummary> tradeSummary) {
        return new TradeSummaries(size, tradeSummary);
    }
}
