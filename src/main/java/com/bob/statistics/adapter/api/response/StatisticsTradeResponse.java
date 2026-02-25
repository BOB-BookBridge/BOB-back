package com.bob.statistics.adapter.api.response;

import com.bob.statistics.application.dto.result.StatisticsTradeSummary;

public record StatisticsTradeResponse(Totals totals) {

    public static StatisticsTradeResponse of(StatisticsTradeSummary summary) {
        long total = summary.canceled()
            + summary.rejected()
            + summary.requested()
            + summary.accepted()
            + summary.reserved()
            + summary.completed();

        return new StatisticsTradeResponse(
            new Totals(
                total,
                summary.canceled(),
                summary.rejected(),
                summary.requested(),
                summary.accepted(),
                summary.reserved(),
                summary.completed()
            )
        );
    }

    public record Totals(
        long total,
        long canceled,
        long rejected,
        long requested,
        long accepted,
        long reserved,
        long completed
    ) {

    }
}
