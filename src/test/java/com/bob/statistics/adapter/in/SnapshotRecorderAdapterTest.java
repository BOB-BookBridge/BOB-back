package com.bob.statistics.adapter.in;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.statistics.application.port.in.StatisticsSnapshotRecorder;

@ExtendWith(MockitoExtension.class)
@DisplayName("스냅샷 기록 어댑터 테스트")
class SnapshotRecorderAdapterTest {

    @Mock
    private StatisticsSnapshotRecorder snapshotRecorder;

    @Test
    void record_호출() {
        SnapshotRecorderAdapter adapter = new SnapshotRecorderAdapter(snapshotRecorder);
        Object target = new Object();
        LocalDateTime txStartedAt = LocalDateTime.now();

        adapter.record(target, txStartedAt);

        then(snapshotRecorder).should().record(target, txStartedAt);
    }

    @Test
    void onlyCreate_record_호출() {
        SnapshotRecorderAdapter adapter = new SnapshotRecorderAdapter(snapshotRecorder);
        Object target = new Object();
        LocalDateTime txStartedAt = LocalDateTime.now();

        adapter.record(target, txStartedAt, true);

        then(snapshotRecorder).should().record(eq(target), any(LocalDateTime.class), eq(true));
    }
}
