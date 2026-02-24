package com.bob.statistics.application.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.statistics.application.StatisticsDailySnapshotBackupService;

@ExtendWith(MockitoExtension.class)
@DisplayName("통계 일일 백업 스케줄러 테스트제 ")
class StatisticsDailySnapshotBackupSchedulerTest {

    @InjectMocks
    private StatisticsDailySnapshotBackupScheduler scheduler;

    @Mock
    private StatisticsDailySnapshotBackupService backupService;

    @Test
    void 백업_서비스_호출() {
        scheduler.backupYesterday();

        then(backupService).should().backup(any());
    }
}
