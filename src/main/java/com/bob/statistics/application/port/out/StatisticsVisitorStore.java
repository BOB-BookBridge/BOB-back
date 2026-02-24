package com.bob.statistics.application.port.out;

import java.time.LocalDate;

public interface StatisticsVisitorStore {

    void addDailyVisitor(LocalDate date, String ip);

    long countDailyVisitors(LocalDate date);

    void deleteDailyVisitors(LocalDate date);
}
