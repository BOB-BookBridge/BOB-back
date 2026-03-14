package com.bob.statistics.application.scheduler;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bob.statistics.application.recorder.StatisticsDailySnapshotBackupService;

@Component
@RequiredArgsConstructor
public class StatisticsDailySnapshotBackupScheduler {

    private final StatisticsDailySnapshotBackupService backupService;

    @Scheduled(cron = "${statistics.backup.cron:0 5 0 * * *}")
    public void backupYesterday() {
        backupService.backup(LocalDate.now().minusDays(1));
    }
}
