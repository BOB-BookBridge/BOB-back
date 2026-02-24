package com.bob.statistics.application.port.out;

import java.time.LocalDate;

public interface StatisticsBackupLockStore {

    boolean acquire(LocalDate snapshotDate, String owner);

    String currentOwner(LocalDate snapshotDate);

    void release(LocalDate snapshotDate);
}
