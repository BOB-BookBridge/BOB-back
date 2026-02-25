package com.bob.statistics.application.port.in;

import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.statistics.application.dto.result.StatisticsPostSummary;
import com.bob.statistics.domain.StatisticsPostDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsPostDailySnapshotRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("Post Statistics Reader Test")
@ContainerTest
record StatisticsPostReaderTest(
    StatisticsPostReader statisticsPostReader,
    StatisticsPostDailySnapshotRepository postDailySnapshotRepository,
    PostRepository postRepository,
    StringRedisTemplate redisTemplate
) {

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        postDailySnapshotRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void 과거_게시글_통계_조회() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        String todayText = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        StatisticsPostDailySnapshot snapshot = StatisticsPostDailySnapshot.createEmpty(yesterday);
        snapshot.addNewPosts(5);
        snapshot.addDeletedPosts(2);
        snapshot.addBannedPosts(1);
        postDailySnapshotRepository.save(snapshot);

        redisTemplate.opsForHash().putAll("stats:daily:post:" + todayText, java.util.Map.of(
            "metric:new_posts", "3",
            "metric:deleted_posts", "1",
            "metric:banned_posts", "1"
        ));

        Post active = createPost(UUID.randomUUID(), 1, 213, "LOW");
        Post deactivated = createPost(UUID.randomUUID(), 1, 213, "LOW");
        deactivated.deactivate();
        Post banned = createPost(UUID.randomUUID(), 2, 214, "LOW");
        banned.ban();

        Post outOfRange = createPost(UUID.randomUUID(), 3, 215, "LOW");
        ReflectionTestUtils.setField(outOfRange, "createdAt", yesterday.minusDays(1).atTime(23, 59, 59));

        postRepository.saveAll(List.of(active, deactivated, banned, outOfRange));

        StatisticsPostSummary result = statisticsPostReader.readPost(yesterday, today);

        assertThat(result.registered()).isEqualTo(8);
        assertThat(result.deleted()).isEqualTo(5);

        assertThat(result.categoryDistribution()).hasSize(2);
        assertThat(result.categoryDistribution().get(0).categoryId()).isEqualTo(1);
        assertThat(result.categoryDistribution().get(0).count()).isEqualTo(2);
        assertThat(result.categoryDistribution().get(1).categoryId()).isEqualTo(2);
        assertThat(result.categoryDistribution().get(1).count()).isEqualTo(1);

        assertThat(result.areaDistribution()).hasSize(2);
        assertThat(result.areaDistribution().get(0).emdId()).isEqualTo(213);
        assertThat(result.areaDistribution().get(0).count()).isEqualTo(2);
        assertThat(result.areaDistribution().get(1).emdId()).isEqualTo(214);
        assertThat(result.areaDistribution().get(1).count()).isEqualTo(1);
    }

    @Test
    void 당일_게시글_통계_조회() {
        LocalDate today = LocalDate.now();
        String todayText = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        redisTemplate.opsForHash().putAll("stats:daily:post:" + todayText, java.util.Map.of(
            "metric:new_posts", "4",
            "metric:deleted_posts", "2",
            "metric:banned_posts", "1"
        ));

        Post active = createPost(UUID.randomUUID(), 10, 213, "LOW");
        Post banned = createPost(UUID.randomUUID(), 11, 214, "LOW");
        banned.ban();
        postRepository.saveAll(List.of(active, banned));

        StatisticsPostSummary result = statisticsPostReader.readPost(today, today);

        assertThat(result.registered()).isEqualTo(4);
        assertThat(result.deleted()).isEqualTo(3);
        assertThat(result.categoryDistribution()).hasSize(2);
        assertThat(result.areaDistribution()).hasSize(2);
    }

    @Test
    void 빈_게시글_통계_조회() {
        LocalDate from = LocalDate.now().minusMonths(2);
        LocalDate to = LocalDate.now().minusMonths(1);

        StatisticsPostSummary result = statisticsPostReader.readPost(from, to);

        assertThat(result.registered()).isZero();
        assertThat(result.deleted()).isZero();
        assertThat(result.categoryDistribution()).isEmpty();
        assertThat(result.areaDistribution()).isEmpty();
    }
}
