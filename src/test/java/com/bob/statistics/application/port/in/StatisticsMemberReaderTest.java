package com.bob.statistics.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.bob.statistics.application.dto.result.StatisticsMemberTimeSeries;
import com.bob.statistics.domain.StatisticsMemberDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsMemberDailySnapshotRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("회원 통계 조회 테스트")
@ContainerTest
record StatisticsMemberReaderTest(
    StatisticsMemberReader statisticsMemberReader,
    StatisticsMemberDailySnapshotRepository memberDailySnapshotRepository,
    StringRedisTemplate redisTemplate
) {

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        memberDailySnapshotRepository.deleteAll();
    }

    @Test
    void 과거_회원_통계_조회() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        StatisticsMemberDailySnapshot yesterdaySnapshot = StatisticsMemberDailySnapshot.createEmpty(yesterday);
        yesterdaySnapshot.addDailyVisitors(11);
        yesterdaySnapshot.addNewMembers(4);
        yesterdaySnapshot.addDeactivatedMembers(2);
        yesterdaySnapshot.addBannedMembers(1);
        memberDailySnapshotRepository.save(yesterdaySnapshot);

        // 당일 데이터는 redis 조회, 다음 데이터(db) 조회 포함 X
        StatisticsMemberDailySnapshot todaySnapshot = StatisticsMemberDailySnapshot.createEmpty(today);
        todaySnapshot.addDailyVisitors(99);
        todaySnapshot.addNewMembers(99);
        todaySnapshot.addDeactivatedMembers(99);
        todaySnapshot.addBannedMembers(99);
        memberDailySnapshotRepository.save(todaySnapshot);

        String todayText = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        redisTemplate.opsForHash().putAll("stats:daily:member:" + todayText, Map.of(
            "metric:new_members", "3",
            "metric:status:DEACTIVATED", "1",
            "metric:status:BANNED", "2"
        ));
        redisTemplate.opsForSet().add("stats:visitors:" + todayText, "127.0.0.1", "127.0.0.2", "127.0.0.3");

        StatisticsMemberTimeSeries result = statisticsMemberReader.readMember(yesterday, today);

        assertThat(result.visitors()).isEqualTo(14);
        assertThat(result.newMembers()).isEqualTo(7);
        assertThat(result.deactivatedMembers()).isEqualTo(3);
        assertThat(result.bannedMembers()).isEqualTo(3);
        assertThat(result.points()).hasSize(2);
        assertThat(result.points().get(0).time()).isEqualTo(yesterday.atStartOfDay());
        assertThat(result.points().get(0).visitors()).isEqualTo(11);
        assertThat(result.points().get(1).time()).isEqualTo(today.atStartOfDay());
        assertThat(result.points().get(1).newMembers()).isEqualTo(3);
    }

    @Test
    void 당일_회원_통계_조회() {
        LocalDate today = LocalDate.now();
        String todayText = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        redisTemplate.opsForHash().putAll("stats:daily:member:" + todayText, Map.of(
            "metric:new_members", "5",
            "metric:status:DEACTIVATED", "2",
            "metric:status:BANNED", "1"
        ));
        redisTemplate.opsForSet().add("stats:visitors:" + todayText, "10.0.0.1", "10.0.0.2");

        LocalTime bucket = LocalTime.of(10, 0);
        redisTemplate.opsForHash().putAll("stats:time:member:" + todayText + ":1000", Map.of(
            "metric:new_members", "2",
            "metric:status:DEACTIVATED", "1",
            "metric:status:BANNED", "1"
        ));
        redisTemplate.opsForSet().add("stats:time:visitor:" + todayText + ":1000", "10.0.0.1", "10.0.0.2");

        StatisticsMemberTimeSeries result = statisticsMemberReader.readMember(today, today);

        assertThat(result.visitors()).isEqualTo(2);
        assertThat(result.newMembers()).isEqualTo(5);
        assertThat(result.deactivatedMembers()).isEqualTo(2);
        assertThat(result.bannedMembers()).isEqualTo(1);
        assertThat(result.points()).hasSize(24);

        int index = bucket.getHour();
        assertThat(result.points().get(index).time()).isEqualTo(today.atTime(10, 0));
        assertThat(result.points().get(index).visitors()).isEqualTo(2);
        assertThat(result.points().get(index).newMembers()).isEqualTo(2);
        assertThat(result.points().get(index).deactivatedMembers()).isEqualTo(1);
        assertThat(result.points().get(index).bannedMembers()).isEqualTo(1);
    }

    @Test
    void 빈_회원_통계_조회() {
        LocalDate from = LocalDate.now().minusMonths(2);
        LocalDate to = LocalDate.now().minusMonths(1);

        StatisticsMemberTimeSeries result = statisticsMemberReader.readMember(from, to);

        assertThat(result.visitors()).isZero();
        assertThat(result.newMembers()).isZero();
        assertThat(result.deactivatedMembers()).isZero();
        assertThat(result.bannedMembers()).isZero();
        assertThat(result.points()).hasSize(32);
    }
}
