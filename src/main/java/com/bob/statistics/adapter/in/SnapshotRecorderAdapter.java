package com.bob.statistics.adapter.in;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.global.snapshot.SnapshotRecorder;
import com.bob.statistics.application.port.in.StatisticsSnapshotRecorder;

@Component
@RequiredArgsConstructor
public class SnapshotRecorderAdapter implements SnapshotRecorder {

    private final StatisticsSnapshotRecorder snapshotRecorder;

    @Override
    public void record(Object target, LocalDateTime txStartedAt) {
        snapshotRecorder.record(target, txStartedAt);
    }

    @Override
    public void record(Object target, LocalDateTime txStartedAt, boolean onlyCreate) {
        snapshotRecorder.record(target, txStartedAt, onlyCreate);
    }
}
