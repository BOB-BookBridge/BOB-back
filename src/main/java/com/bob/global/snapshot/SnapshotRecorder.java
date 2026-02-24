package com.bob.global.snapshot;

import java.time.LocalDateTime;

public interface SnapshotRecorder {

    void record(Object target, LocalDateTime txStartedAt);

    void record(Object target, LocalDateTime txStartedAt, boolean onlyCreate);
}
