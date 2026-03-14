package com.bob.statistics.application.port.in;

import java.time.LocalDate;

import com.bob.statistics.application.dto.result.StatisticsPostSummary;

public interface StatisticsPostReader {

    StatisticsPostSummary readPost(LocalDate from, LocalDate to);
}
