package com.bob.statistics.application.port.out;

import java.time.LocalDate;
import java.time.LocalTime;

public interface StatisticsVisitorStore {

    void addDailyVisitor(LocalDate date, String ip);

    long countDailyVisitors(LocalDate date);

    long countTimeVisitors(LocalDate date, LocalTime timeBucket);

    void deleteDailyVisitors(LocalDate date);
}
