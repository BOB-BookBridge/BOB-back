package com.bob.statistics.application.port.out;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticsDailyMetricStore {

    Map<String, Long> readDailyMetrics(String domain, LocalDate snapshotDate);

    void deleteDailyMetrics(String domain, LocalDate snapshotDate);
}
