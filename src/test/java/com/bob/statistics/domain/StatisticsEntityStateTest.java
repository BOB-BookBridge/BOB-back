package com.bob.statistics.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("엔티티 상태 저장 데이터 도메인 테스트")
class StatisticsEntityStateTest {

    @Test
    void 상태_업데이트() {
        LocalDateTime now = LocalDateTime.now();

        StatisticsEntityState state = StatisticsEntityState.builder()
            .domain("trade")
            .entityId("trade-1")
            .cohortDate(LocalDate.of(2026, 2, 3))
            .currentStatus("REQUESTED")
            .createdCounted(false)
            .updatedAt(now.minusDays(1))
            .build();

        state.update(LocalDate.of(2026, 2, 4), "COMPLETED", true, now);

        assertThat(state.getDomain()).isEqualTo("trade");
        assertThat(state.getEntityId()).isEqualTo("trade-1");
        assertThat(state.getCohortDate()).isEqualTo(LocalDate.of(2026, 2, 4));
        assertThat(state.getCurrentStatus()).isEqualTo("COMPLETED");
        assertThat(state.isCreatedCounted()).isTrue();
        assertThat(state.getUpdatedAt()).isEqualTo(now);
    }
}
