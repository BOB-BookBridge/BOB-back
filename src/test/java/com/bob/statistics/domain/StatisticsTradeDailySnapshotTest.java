package com.bob.statistics.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("거래 일간 통계 데이터 도메인 테스트")
class StatisticsTradeDailySnapshotTest {

    @Test
    void 거래_일간_통계_데이터_생성() {
        LocalDate snapshotDate = LocalDate.of(2026, 2, 24);

        StatisticsTradeDailySnapshot snapshot = StatisticsTradeDailySnapshot.createEmpty(snapshotDate);

        assertThat(snapshot.getSnapshotDate()).isEqualTo(snapshotDate);
        assertThat(snapshot.getNewTrades()).isZero();
        assertThat(snapshot.getRequestedTrades()).isZero();
        assertThat(snapshot.getAcceptedTrades()).isZero();
        assertThat(snapshot.getRejectedTrades()).isZero();
        assertThat(snapshot.getCanceledTrades()).isZero();
        assertThat(snapshot.getReservedTrades()).isZero();
        assertThat(snapshot.getCompletedTrades()).isZero();
    }

    @Test
    void 통계_수치_누적() {
        StatisticsTradeDailySnapshot snapshot = StatisticsTradeDailySnapshot.createEmpty(LocalDate.of(2026, 2, 24));

        snapshot.addNewTrades(5);
        snapshot.addRequestedTrades(1);
        snapshot.addAcceptedTrades(2);
        snapshot.addRejectedTrades(3);
        snapshot.addCanceledTrades(4);
        snapshot.addReservedTrades(5);
        snapshot.addCompletedTrades(6);

        assertThat(snapshot.getNewTrades()).isEqualTo(5);
        assertThat(snapshot.getRequestedTrades()).isEqualTo(1);
        assertThat(snapshot.getAcceptedTrades()).isEqualTo(2);
        assertThat(snapshot.getRejectedTrades()).isEqualTo(3);
        assertThat(snapshot.getCanceledTrades()).isEqualTo(4);
        assertThat(snapshot.getReservedTrades()).isEqualTo(5);
        assertThat(snapshot.getCompletedTrades()).isEqualTo(6);
    }
}
