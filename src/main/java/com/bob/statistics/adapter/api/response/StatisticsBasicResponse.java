package com.bob.statistics.adapter.api.response;

import com.bob.statistics.application.dto.result.StatisticsBasicMetrics;

public record StatisticsBasicResponse(
    long newMembers,
    long newPosts,
    long newTrades,
    long totalMembers,
    long totalPosts,
    long totalTrades
) {

    public static StatisticsBasicResponse of(StatisticsBasicMetrics metrics) {
        return new StatisticsBasicResponse(
            metrics.newMembers(),
            metrics.newPosts(),
            metrics.newTrades(),
            metrics.totalMembers(),
            metrics.totalPosts(),
            metrics.totalTrades()
        );
    }
}
