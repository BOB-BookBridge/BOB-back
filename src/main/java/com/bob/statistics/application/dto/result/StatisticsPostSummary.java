package com.bob.statistics.application.dto.result;

import java.util.List;

public record StatisticsPostSummary(
    long registered,
    long deleted,
    List<CategoryDistribution> categoryDistribution,
    List<AreaDistribution> areaDistribution
) {

    public record CategoryDistribution(int categoryId, long count) {

    }

    public record AreaDistribution(int emdId, long count) {

    }
}
