package com.bob.statistics.application.port.in;

import java.time.LocalDate;

import com.bob.statistics.application.dto.result.StatisticsMemberTimeSeries;

public interface StatisticsMemberReader {

    StatisticsMemberTimeSeries readMember(LocalDate from, LocalDate to);
}
