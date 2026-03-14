package com.bob.statistics.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

import com.bob.statistics.domain.StatisticsMetricEvent;

public interface StatisticsMetricStore {

    void saveAll(List<StatisticsMetricEvent> events, LocalDateTime txStartedAt);
}
