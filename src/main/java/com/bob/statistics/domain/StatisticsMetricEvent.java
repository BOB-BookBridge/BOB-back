package com.bob.statistics.domain;

import java.time.LocalDate;

public record StatisticsMetricEvent(
    LocalDate eventDate,
    String domain,
    String metric,
    long value,
    String entityId,
    LocalDate cohortDate
) {
}
