package com.bob.statistics.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원 일간 통계 데이터 도메인 테스트")
class StatisticsMemberDailySnapshotTest {

    @Test
    void 회원_일간_통계_데이터_생성() {
        LocalDate snapshotDate = LocalDate.of(2026, 2, 24);

        StatisticsMemberDailySnapshot snapshot = StatisticsMemberDailySnapshot.createEmpty(snapshotDate);

        assertThat(snapshot.getSnapshotDate()).isEqualTo(snapshotDate);
        assertThat(snapshot.getNewMembers()).isZero();
        assertThat(snapshot.getDeactivatedMembers()).isZero();
        assertThat(snapshot.getBannedMembers()).isZero();
    }

    @Test
    void 통계_수치_누적() {
        StatisticsMemberDailySnapshot snapshot = StatisticsMemberDailySnapshot.createEmpty(LocalDate.of(2026, 2, 24));

        snapshot.addNewMembers(2);
        snapshot.addDeactivatedMembers(1);
        snapshot.addBannedMembers(3);

        assertThat(snapshot.getNewMembers()).isEqualTo(2);
        assertThat(snapshot.getDeactivatedMembers()).isEqualTo(1);
        assertThat(snapshot.getBannedMembers()).isEqualTo(3);
    }
}
