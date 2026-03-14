package com.bob.statistics.application.recorder;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.bob.statistics.application.port.in.StatisticsVisitorRecorder;
import com.bob.statistics.application.port.out.StatisticsVisitorStore;

@Service
@RequiredArgsConstructor
public class StatisticsVisitorRecordService implements StatisticsVisitorRecorder {

    private final StatisticsVisitorStore visitorStore;

    @Override
    public void record(String ip, LocalDate date) {
        visitorStore.addDailyVisitor(date, ip);
    }
}
