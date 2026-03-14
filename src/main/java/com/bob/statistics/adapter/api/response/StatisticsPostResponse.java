package com.bob.statistics.adapter.api.response;

import java.util.List;

import com.bob.statistics.application.dto.result.StatisticsPostSummary;

public record StatisticsPostResponse(
    Totals totals,
    List<CategoryDistribution> categoryDistribution,
    List<AreaDistribution> areaDistribution
) {

    public static StatisticsPostResponse of(StatisticsPostSummary summary) {
        return new StatisticsPostResponse(
            new Totals(summary.registered(), summary.deleted()),
            summary.categoryDistribution().stream()
                .map(c -> new CategoryDistribution(c.categoryId(), c.count()))
                .toList(),
            summary.areaDistribution().stream()
                .map(a -> new AreaDistribution(a.emdId(), a.count()))
                .toList()
        );
    }

    public record Totals(long registered, long deleted) {

    }

    public record CategoryDistribution(int categoryId, long count) {

    }

    public record AreaDistribution(int emdId, long count) {

    }
}
