package com.bob.statistics.application.dto.result;

public record StatisticsTradeSummary(
    long canceled,
    long rejected,
    long requested,
    long accepted,
    long reserved,
    long completed
) {

}
