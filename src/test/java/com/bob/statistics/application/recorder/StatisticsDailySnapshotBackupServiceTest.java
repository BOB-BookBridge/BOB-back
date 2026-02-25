package com.bob.statistics.application.recorder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.statistics.application.port.out.StatisticsBackupLockStore;
import com.bob.statistics.application.port.out.StatisticsDailyMetricStore;
import com.bob.statistics.application.port.out.StatisticsVisitorStore;
import com.bob.statistics.domain.StatisticsMemberDailySnapshot;
import com.bob.statistics.domain.StatisticsPostDailySnapshot;
import com.bob.statistics.domain.StatisticsTradeDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsMemberDailySnapshotRepository;
import com.bob.statistics.domain.repository.StatisticsPostDailySnapshotRepository;
import com.bob.statistics.domain.repository.StatisticsTradeDailySnapshotRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("통계 일일 백업 서비스 테스트")
class StatisticsDailySnapshotBackupServiceTest {

    @InjectMocks
    private StatisticsDailySnapshotBackupService backupService;

    @Mock
    private StatisticsDailyMetricStore dailyMetricStore;

    @Mock
    private StatisticsBackupLockStore backupLockStore;

    @Mock
    private StatisticsVisitorStore visitorStore;

    @Mock
    private StatisticsMemberDailySnapshotRepository memberRepository;

    @Mock
    private StatisticsPostDailySnapshotRepository postRepository;

    @Mock
    private StatisticsTradeDailySnapshotRepository tradeRepository;

    @Test
    void 락_획득에_실패_시_백업_미수행() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        given(backupLockStore.acquire(any(), any())).willReturn(false);

        backupService.backup(date);

        then(dailyMetricStore).should(never()).readDailyMetrics(any(), any());
        then(memberRepository).should(never()).save(any());
        then(postRepository).should(never()).save(any());
        then(tradeRepository).should(never()).save(any());
    }

    @Test
    void 회원_일일_지표_db적재() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        given(backupLockStore.acquire(any(), any())).willReturn(true);
        given(backupLockStore.currentOwner(any())).willReturn("different-owner");
        given(dailyMetricStore.readDailyMetrics("member", date)).willReturn(Map.of(
            "new_members", 3L,
            "status:DEACTIVATED", 2L,
            "status:BANNED", 1L
        ));
        given(visitorStore.countDailyVisitors(date)).willReturn(7L);
        given(dailyMetricStore.readDailyMetrics("post", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("trade", date)).willReturn(Map.of());
        given(memberRepository.findBySnapshotDate(date)).willReturn(Optional.empty());

        backupService.backup(date);

        ArgumentCaptor<StatisticsMemberDailySnapshot> captor = ArgumentCaptor.forClass(
            StatisticsMemberDailySnapshot.class);
        then(memberRepository).should().save(captor.capture());
        StatisticsMemberDailySnapshot saved = captor.getValue();
        assertThat(saved.getSnapshotDate()).isEqualTo(date);
        assertThat(saved.getNewMembers()).isEqualTo(3);
        assertThat(saved.getDeactivatedMembers()).isEqualTo(2);
        assertThat(saved.getBannedMembers()).isEqualTo(1);
        assertThat(saved.getDailyVisitors()).isEqualTo(7);

        then(dailyMetricStore).should().deleteDailyMetrics("member", date);
        then(visitorStore).should().deleteDailyVisitors(date);
    }

    @Test
    void 거래_상태_지표_db_적재() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        given(backupLockStore.acquire(any(), any())).willReturn(true);
        given(backupLockStore.currentOwner(any())).willReturn("different-owner");
        given(visitorStore.countDailyVisitors(date)).willReturn(0L);
        given(dailyMetricStore.readDailyMetrics("member", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("post", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("trade", date)).willReturn(Map.ofEntries(
            Map.entry("new_trades", 5L),
            Map.entry("status:REQUESTED", 1L),
            Map.entry("status:ACCEPTED", 2L),
            Map.entry("status:REJECTED", 3L),
            Map.entry("status:CANCELED", 4L),
            Map.entry("status:RESERVED", 5L),
            Map.entry("status:COMPLETED", 6L),
            Map.entry("unknown_metric", 99L)
        ));
        given(tradeRepository.findBySnapshotDate(date)).willReturn(Optional.empty());

        backupService.backup(date);

        ArgumentCaptor<StatisticsTradeDailySnapshot> captor = ArgumentCaptor.forClass(
            StatisticsTradeDailySnapshot.class);

        then(tradeRepository).should().save(captor.capture());

        StatisticsTradeDailySnapshot saved = captor.getValue();
        assertThat(saved.getNewTrades()).isEqualTo(5);
        assertThat(saved.getRequestedTrades()).isEqualTo(1);
        assertThat(saved.getAcceptedTrades()).isEqualTo(2);
        assertThat(saved.getRejectedTrades()).isEqualTo(3);
        assertThat(saved.getCanceledTrades()).isEqualTo(4);
        assertThat(saved.getReservedTrades()).isEqualTo(5);
        assertThat(saved.getCompletedTrades()).isEqualTo(6);

        then(dailyMetricStore).should().deleteDailyMetrics("trade", date);
    }

    @Test
    void 방문자수_존재_시_회원_일간_스냅샷_저장() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        given(backupLockStore.acquire(any(), any())).willReturn(true);
        given(backupLockStore.currentOwner(any())).willReturn("different-owner");
        given(visitorStore.countDailyVisitors(date)).willReturn(5L);
        given(dailyMetricStore.readDailyMetrics("member", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("post", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("trade", date)).willReturn(Map.of());
        given(memberRepository.findBySnapshotDate(date)).willReturn(Optional.empty());

        backupService.backup(date);

        ArgumentCaptor<StatisticsMemberDailySnapshot> captor = ArgumentCaptor.forClass(
            StatisticsMemberDailySnapshot.class);

        then(memberRepository).should().save(captor.capture());

        assertThat(captor.getValue().getDailyVisitors()).isEqualTo(5);

        then(visitorStore).should().deleteDailyVisitors(date);
    }

    @Test
    void 락_소유자가_다르면_해제하지_않는다() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        given(backupLockStore.acquire(any(), any())).willReturn(true);
        given(backupLockStore.currentOwner(any())).willReturn("another-owner");
        given(visitorStore.countDailyVisitors(date)).willReturn(0L);
        given(dailyMetricStore.readDailyMetrics("member", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("post", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("trade", date)).willReturn(Map.of());

        backupService.backup(date);

        then(backupLockStore).should(never()).release(date);
    }

    @Test
    void 락_소유자가_같으면_해제() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        AtomicReference<String> ownerRef = new AtomicReference<>();
        given(backupLockStore.acquire(any(), any())).willAnswer(invocation -> {
            ownerRef.set(invocation.getArgument(1));
            return true;
        });
        given(dailyMetricStore.readDailyMetrics("member", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("post", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("trade", date)).willReturn(Map.of());
        given(visitorStore.countDailyVisitors(date)).willReturn(0L);
        given(backupLockStore.currentOwner(date)).willAnswer(invocation -> ownerRef.get());

        backupService.backup(date);

        then(backupLockStore).should().release(date);
    }

    @Test
    void 게시글_일일_지표_db_적재() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        given(backupLockStore.acquire(any(), any())).willReturn(true);
        given(backupLockStore.currentOwner(any())).willReturn("different-owner");
        given(visitorStore.countDailyVisitors(date)).willReturn(0L);
        given(dailyMetricStore.readDailyMetrics("member", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("post", date)).willReturn(Map.ofEntries(
            Map.entry("new_posts", 2L),
            Map.entry("status:DEACTIVATED", 1L),
            Map.entry("status:BANNED", 3L),
            Map.entry("unknown_metric", 10L)
        ));
        given(dailyMetricStore.readDailyMetrics("trade", date)).willReturn(Map.of());
        given(postRepository.findBySnapshotDate(date)).willReturn(Optional.empty());

        backupService.backup(date);

        ArgumentCaptor<StatisticsPostDailySnapshot> captor = ArgumentCaptor.forClass(StatisticsPostDailySnapshot.class);
        then(postRepository).should().save(captor.capture());
        StatisticsPostDailySnapshot saved = captor.getValue();
        assertThat(saved.getNewPosts()).isEqualTo(2);
        assertThat(saved.getDeletedPosts()).isEqualTo(1);
        assertThat(saved.getBannedPosts()).isEqualTo(3);
        then(dailyMetricStore).should().deleteDailyMetrics("post", date);
    }

    @Test
    void 백업_중_예외_발생_락_해제() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        AtomicReference<String> ownerRef = new AtomicReference<>();
        given(backupLockStore.acquire(any(), any())).willAnswer(invocation -> {
            ownerRef.set(invocation.getArgument(1));
            return true;
        });
        given(backupLockStore.currentOwner(date)).willAnswer(invocation -> ownerRef.get());
        given(visitorStore.countDailyVisitors(date)).willReturn(0L);
        given(dailyMetricStore.readDailyMetrics("member", date)).willReturn(Map.of());
        given(dailyMetricStore.readDailyMetrics("post", date)).willReturn(Map.of("new_posts", 1L));
        given(postRepository.findBySnapshotDate(date)).willReturn(Optional.empty());
        given(postRepository.save(any())).willThrow(new RuntimeException("db error"));

        assertThatThrownBy(() -> backupService.backup(date)).isInstanceOf(RuntimeException.class);

        then(backupLockStore).should().release(date);
    }
}
