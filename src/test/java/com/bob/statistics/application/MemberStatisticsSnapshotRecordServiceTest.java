package com.bob.statistics.application;

import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.Status;
import com.bob.statistics.domain.StatisticsMetricEvent;

@DisplayName("회원 통계 스냅샷 변환 테스트")
class MemberStatisticsSnapshotRecordServiceTest {

    final StatisticsSnapshotRecordService recordService = new StatisticsSnapshotRecordService();

    @Test
    void 회원_생성_집계() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Member member = createMember();
        ReflectionTestUtils.setField(member, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(member, "createdAt", txStartedAt.plusSeconds(1));
        ReflectionTestUtils.setField(member, "status", Status.ACTIVE);

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(member, txStartedAt, false);

        assertThat(countMetric(events, "new_members")).isEqualTo(1);
        assertThat(countMetric(events, "status:ACTIVE")).isZero();
    }

    @Test
    void 소셜_회원_생성_집계() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Member member = createMember();
        ReflectionTestUtils.setField(member, "createdAt", txStartedAt.plusSeconds(1));
        ReflectionTestUtils.setField(member, "status", Status.ACTIVE);

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(member, txStartedAt, true);

        assertThat(countMetric(events, "new_members")).isEqualTo(1);
        assertThat(events).noneMatch(e -> e.metric().startsWith("status:"));
    }

    @Test
    void 회원_비활성화_상태_집계() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Member member = createMember();
        ReflectionTestUtils.setField(member, "createdAt", txStartedAt.minusDays(1));
        ReflectionTestUtils.setField(member, "status", Status.DEACTIVATED);

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(member, txStartedAt, false);

        assertThat(countMetric(events, "new_members")).isZero();
        assertThat(countMetric(events, "status:DEACTIVATED")).isEqualTo(1);
    }

    @Test
    void 회원_제재_상태_집계() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Member member = createMember();
        ReflectionTestUtils.setField(member, "createdAt", txStartedAt.minusDays(1));
        ReflectionTestUtils.setField(member, "status", Status.BANNED);

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(member, txStartedAt, false);

        assertThat(countMetric(events, "new_members")).isZero();
        assertThat(countMetric(events, "status:BANNED")).isEqualTo(1);
    }

    private static long countMetric(List<StatisticsMetricEvent> events, String metricName) {
        return events.stream().filter(event -> metricName.equals(event.metric())).count();
    }
}
