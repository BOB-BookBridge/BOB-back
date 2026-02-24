package com.bob.statistics.application.dto.result;

public record StatisticsBasicMetrics(
    long visitor,
    long newMembers,
    long newPosts,
    long newTrades,
    long totalMembers,
    long totalPosts,
    long totalTrades
) {

}
