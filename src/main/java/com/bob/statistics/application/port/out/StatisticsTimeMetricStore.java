package com.bob.statistics.application.port.out;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public interface StatisticsTimeMetricStore {

    Map<String, Long> readTimeMetrics(String domain, LocalDate date, LocalTime timeBucket);
}
