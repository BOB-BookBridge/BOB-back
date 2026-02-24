package com.bob.statistics.application.port.in;

import java.time.LocalDateTime;

public interface StatisticsSnapshotRecorder {

    void record(Object target, LocalDateTime txStartedAt);
}
