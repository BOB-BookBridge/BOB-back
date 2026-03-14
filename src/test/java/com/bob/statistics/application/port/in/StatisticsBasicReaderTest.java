package com.bob.statistics.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static com.bob.support.fixture.trade.domain.TradeFixture.createTrade;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.bob.core.member.domain.repository.MemberRepository;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.core.trade.domain.repository.TradeRepository;
import com.bob.core.trade.domain.status.Status;
import com.bob.statistics.application.dto.result.StatisticsBasicMetrics;
import com.bob.support.annotation.ContainerTest;

@DisplayName("기본 통계 조회 테스트")
@ContainerTest
record StatisticsBasicReaderTest(
    StatisticsBasicReader statisticsBasicReader,
    MemberRepository memberRepository,
    PostRepository postRepository,
    TradeRepository tradeRepository,
    StringRedisTemplate redisTemplate
) {

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void 기본_통계_조회() {
        LocalDate today = LocalDate.now();
        String todayText = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));

        memberRepository.save(createMember("stats-basic-member@test.com"));
        postRepository.save(createPost());
        tradeRepository.save(createTrade(Status.REQUESTED));

        redisTemplate.opsForHash().putAll("stats:daily:member:" + todayText, Map.of("metric:new_members", "3"));
        redisTemplate.opsForHash().putAll("stats:daily:post:" + todayText, Map.of("metric:new_posts", "4"));
        redisTemplate.opsForHash().putAll("stats:daily:trade:" + todayText, Map.of("metric:new_trades", "5"));
        redisTemplate.opsForSet().add("stats:visitors:" + todayText, "127.0.0.1", "127.0.0.2", "127.0.0.3");

        StatisticsBasicMetrics result = statisticsBasicReader.readBasic();

        assertThat(result.visitor()).isEqualTo(3);
        assertThat(result.newMembers()).isEqualTo(3);
        assertThat(result.newPosts()).isEqualTo(4);
        assertThat(result.newTrades()).isEqualTo(5);
        assertThat(result.totalMembers()).isEqualTo(memberRepository.count());
        assertThat(result.totalPosts()).isEqualTo(postRepository.count());
        assertThat(result.totalTrades()).isEqualTo(tradeRepository.count());
    }

    @Test
    void 당일_신규_키가_없으면_0_반환() {
        StatisticsBasicMetrics result = statisticsBasicReader.readBasic();

        assertThat(result.visitor()).isZero();
        assertThat(result.newMembers()).isZero();
        assertThat(result.newPosts()).isZero();
        assertThat(result.newTrades()).isZero();
    }
}
