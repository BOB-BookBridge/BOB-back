package com.bob.statistics.application.port.in;

import com.bob.statistics.application.dto.result.StatisticsBasicMetrics;

public interface StatisticsBasicReader {

    StatisticsBasicMetrics readBasic();
}
