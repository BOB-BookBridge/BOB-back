package com.bob.statistics.adapter.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static com.bob.support.fixture.trade.domain.TradeFixture.createTrade;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.bob.core.member.domain.repository.MemberRepository;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.core.trade.domain.repository.TradeRepository;
import com.bob.core.trade.domain.status.Status;
import com.bob.security.model.MemberDetails;
import com.bob.statistics.domain.StatisticsMemberDailySnapshot;
import com.bob.statistics.domain.StatisticsPostDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsMemberDailySnapshotRepository;
import com.bob.statistics.domain.repository.StatisticsPostDailySnapshotRepository;
import com.bob.support.annotation.BobApiTest;
import com.bob.support.util.AssertThatUtils;

@DisplayName("통계 API 테스트")
@BobApiTest
record StatisticsApiTest(
    MockMvcTester mvcTester,
    MemberRepository memberRepository,
    PostRepository postRepository,
    TradeRepository tradeRepository,
    StatisticsMemberDailySnapshotRepository memberDailySnapshotRepository,
    StatisticsPostDailySnapshotRepository postDailySnapshotRepository,
    StringRedisTemplate redisTemplate
) {

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        memberDailySnapshotRepository.deleteAll();
        postDailySnapshotRepository.deleteAll();
        setAuthentication();
    }

    @Test
    void 기본_통계_조회() {
        LocalDate today = LocalDate.now();
        String todayText = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        memberRepository.save(createMember("stats-api-member@test.com"));
        postRepository.save(createPost());
        tradeRepository.save(createTrade(Status.REQUESTED));

        redisTemplate.opsForHash().putAll("stats:daily:member:" + todayText, Map.of("metric:new_members", "3"));
        redisTemplate.opsForHash().putAll("stats:daily:post:" + todayText, Map.of("metric:new_posts", "4"));
        redisTemplate.opsForHash().putAll("stats:daily:trade:" + todayText, Map.of("metric:new_trades", "5"));
        redisTemplate.opsForSet().add("stats:visitors:" + todayText, "127.0.0.1", "127.0.0.2", "127.0.0.3");

        var result = mvcTester.get().uri("/statistics/basic").exchange();

        assertThat(result).hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.visitor", AssertThatUtils.equalsTo(3))
            .hasPathSatisfying("$.newMembers", AssertThatUtils.equalsTo(3))
            .hasPathSatisfying("$.newPosts", AssertThatUtils.equalsTo(4))
            .hasPathSatisfying("$.newTrades", AssertThatUtils.equalsTo(5))
            .hasPathSatisfying("$.totalMembers", AssertThatUtils.equalsTo((int)memberRepository.count()))
            .hasPathSatisfying("$.totalPosts", AssertThatUtils.equalsTo((int)postRepository.count()))
            .hasPathSatisfying("$.totalTrades", AssertThatUtils.equalsTo((int)tradeRepository.count()));
    }

    @Test
    void 과거_회원_통계_조회() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        String todayText = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        StatisticsMemberDailySnapshot snapshot = StatisticsMemberDailySnapshot.createEmpty(yesterday);
        snapshot.addDailyVisitors(10);
        snapshot.addNewMembers(2);
        snapshot.addDeactivatedMembers(1);
        snapshot.addBannedMembers(1);
        memberDailySnapshotRepository.save(snapshot);

        redisTemplate.opsForHash().putAll("stats:daily:member:" + todayText, Map.of(
            "metric:new_members", "3",
            "metric:status:DEACTIVATED", "2",
            "metric:status:BANNED", "1"
        ));
        redisTemplate.opsForSet().add("stats:visitors:" + todayText, "10.0.0.1", "10.0.0.2", "10.0.0.3");

        var result = mvcTester.get()
            .uri("/statistics/members?from={from}&to={to}", yesterday, today)
            .exchange();

        assertThat(result).hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.totals.visit", AssertThatUtils.equalsTo(13))
            .hasPathSatisfying("$.totals.new", AssertThatUtils.equalsTo(5))
            .hasPathSatisfying("$.totals.deactivated", AssertThatUtils.equalsTo(3))
            .hasPathSatisfying("$.totals.banned", AssertThatUtils.equalsTo(2))
            .hasPathSatisfying("$.points.length()", AssertThatUtils.equalsTo(2))
            .hasPathSatisfying("$.points[0].time", AssertThatUtils.equalsTo(yesterday + "T00:00:00"))
            .hasPathSatisfying("$.points[1].time", AssertThatUtils.equalsTo(today + "T00:00:00"));
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
        redisTemplate.opsForHash().putAll("stats:time:member:" + todayText + ":1000", Map.of(
            "metric:new_members", "2",
            "metric:status:DEACTIVATED", "1",
            "metric:status:BANNED", "1"
        ));
        redisTemplate.opsForSet().add("stats:time:visitor:" + todayText + ":1000", "10.0.0.1", "10.0.0.2");

        var result = mvcTester.get()
            .uri("/statistics/members")
            .exchange();

        assertThat(result).hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.totals.visit", AssertThatUtils.equalsTo(2))
            .hasPathSatisfying("$.totals.new", AssertThatUtils.equalsTo(5))
            .hasPathSatisfying("$.totals.deactivated", AssertThatUtils.equalsTo(2))
            .hasPathSatisfying("$.totals.banned", AssertThatUtils.equalsTo(1))
            .hasPathSatisfying("$.points.length()", AssertThatUtils.equalsTo(24))
            .hasPathSatisfying("$.points[10].time", AssertThatUtils.equalsTo(today + "T10:00:00"))
            .hasPathSatisfying("$.points[10].visit", AssertThatUtils.equalsTo(2))
            .hasPathSatisfying("$.points[10].new", AssertThatUtils.equalsTo(2))
            .hasPathSatisfying("$.points[10].deactivated", AssertThatUtils.equalsTo(1))
            .hasPathSatisfying("$.points[10].banned", AssertThatUtils.equalsTo(1));
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

        redisTemplate.opsForHash().putAll("stats:daily:post:" + todayText, Map.of(
            "metric:new_posts", "3",
            "metric:deleted_posts", "1",
            "metric:banned_posts", "0"
        ));

        var result = mvcTester.get()
            .uri("/statistics/posts?from={from}&to={to}", yesterday, today)
            .exchange();

        assertThat(result).hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.totals.registered", AssertThatUtils.equalsTo(8))
            .hasPathSatisfying("$.totals.deleted", AssertThatUtils.equalsTo(4))
            .hasPathSatisfying("$.categoryDistribution.length()", AssertThatUtils.equalsTo(1))
            .hasPathSatisfying("$.categoryDistribution[0].categoryId", AssertThatUtils.equalsTo(21))
            .hasPathSatisfying("$.categoryDistribution[0].count", AssertThatUtils.equalsTo(5))
            .hasPathSatisfying("$.areaDistribution.length()", AssertThatUtils.equalsTo(1))
            .hasPathSatisfying("$.areaDistribution[0].emdId", AssertThatUtils.equalsTo(213))
            .hasPathSatisfying("$.areaDistribution[0].count", AssertThatUtils.equalsTo(5));
    }

    @Test
    void 당일_게시글_통계_조회() {
        LocalDate today = LocalDate.now();
        String todayText = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        StatisticsPostDailySnapshot snapshot = StatisticsPostDailySnapshot.createEmpty(today.minusDays(1));
        postDailySnapshotRepository.save(snapshot);

        redisTemplate.opsForHash().putAll("stats:daily:post:" + todayText, Map.of(
            "metric:new_posts", "3",
            "metric:deleted_posts", "1",
            "metric:banned_posts", "0"
        ));

        var result = mvcTester.get().uri("/statistics/posts").exchange();

        assertThat(result).hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.totals.registered", AssertThatUtils.equalsTo(3))
            .hasPathSatisfying("$.totals.deleted", AssertThatUtils.equalsTo(1))
            .hasPathSatisfying("$.categoryDistribution.length()", AssertThatUtils.equalsTo(1))
            .hasPathSatisfying("$.categoryDistribution[0].categoryId", AssertThatUtils.equalsTo(21))
            .hasPathSatisfying("$.categoryDistribution[0].count", AssertThatUtils.equalsTo(5))
            .hasPathSatisfying("$.areaDistribution.length()", AssertThatUtils.equalsTo(1))
            .hasPathSatisfying("$.areaDistribution[0].emdId", AssertThatUtils.equalsTo(213))
            .hasPathSatisfying("$.areaDistribution[0].count", AssertThatUtils.equalsTo(5));
    }

    private void setAuthentication() {
        MemberDetails principal = new MemberDetails(MANAGER_ID, "ADMIN", true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
