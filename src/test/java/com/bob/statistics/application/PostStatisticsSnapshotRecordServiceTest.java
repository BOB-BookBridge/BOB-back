package com.bob.statistics.application;

import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.post.domain.Post;
import com.bob.statistics.application.port.out.StatisticsEntityStateStore;
import com.bob.statistics.application.port.out.StatisticsMetricStore;
import com.bob.statistics.domain.StatisticsMetricEvent;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 통계 스냅샷 변환 테스트")
class PostStatisticsSnapshotRecordServiceTest {

    @InjectMocks
    private StatisticsSnapshotRecordService recordService;

    @Mock
    private StatisticsMetricStore metricStore;

    @Mock
    private StatisticsEntityStateStore entityStateStore;

    @Mock
    private StatisticsCohortCurrentSnapshotService cohortCurrentSnapshotService;

    @Test
    void 게시글_생성_집계() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Post post = createPost();
        ReflectionTestUtils.setField(post, "id", 21L);
        ReflectionTestUtils.setField(post, "createdAt", txStartedAt.plusSeconds(1));

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(post, txStartedAt, false);

        assertThat(countMetric(events, "new_posts")).isEqualTo(1);
    }

    @Test
    void 비활성화_게시글_집계() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Post post = createPost();
        ReflectionTestUtils.setField(post, "id", 22L);
        ReflectionTestUtils.setField(post, "createdAt", txStartedAt.minusDays(1));
        post.deactivate();

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(post, txStartedAt, false);

        assertThat(countMetric(events, "new_posts")).isZero();
        assertThat(countMetric(events, "deleted_posts")).isEqualTo(1);
    }

    @Test
    void 제재_게시글_집계() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Post post = createPost();
        ReflectionTestUtils.setField(post, "id", 23L);
        ReflectionTestUtils.setField(post, "createdAt", txStartedAt.minusDays(1));
        post.ban();

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(post, txStartedAt, false);

        assertThat(countMetric(events, "banned_posts")).isEqualTo(1);
    }

    private static long countMetric(List<StatisticsMetricEvent> events, String metricName) {
        return events.stream().filter(event -> metricName.equals(event.metric())).count();
    }
}
