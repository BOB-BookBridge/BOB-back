package com.bob.statistics.application.port.in;

import java.time.LocalDate;

import com.bob.statistics.application.dto.result.StatisticsTradeSummary;

public interface StatisticsTradeReader {

    StatisticsTradeSummary readTrade(LocalDate from, LocalDate to);
}
