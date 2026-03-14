package com.bob.statistics.application.port.in;

import java.time.LocalDate;

public interface StatisticsVisitorRecorder {

    void record(String ip, LocalDate date);
}
