package com.bob.statistics.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("게시글 일간 통계 데이터 도메인 테스트")
class StatisticsPostDailySnapshotTest {

    @Test
    void 게시글_일간_통계_데이터_생성() {
        LocalDate snapshotDate = LocalDate.of(2026, 2, 24);

        StatisticsPostDailySnapshot snapshot = StatisticsPostDailySnapshot.createEmpty(snapshotDate);

        assertThat(snapshot.getSnapshotDate()).isEqualTo(snapshotDate);
        assertThat(snapshot.getNewPosts()).isZero();
        assertThat(snapshot.getDeletedPosts()).isZero();
        assertThat(snapshot.getBannedPosts()).isZero();
    }

    @Test
    void 통계_수치_누적() {
        StatisticsPostDailySnapshot snapshot = StatisticsPostDailySnapshot.createEmpty(LocalDate.of(2026, 2, 24));

        snapshot.addNewPosts(4);
        snapshot.addDeletedPosts(2);
        snapshot.addBannedPosts(1);

        assertThat(snapshot.getNewPosts()).isEqualTo(4);
        assertThat(snapshot.getDeletedPosts()).isEqualTo(2);
        assertThat(snapshot.getBannedPosts()).isEqualTo(1);
    }
}
