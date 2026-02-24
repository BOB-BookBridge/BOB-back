package com.bob.statistics.adapter.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static com.bob.support.fixture.trade.domain.TradeFixture.createTrade;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
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
import com.bob.support.annotation.BobApiTest;
import com.bob.support.util.AssertThatUtils;

@DisplayName("통계 API 테스트")
@BobApiTest
record StatisticsApiTest(
    MockMvcTester mvcTester,
    MemberRepository memberRepository,
    PostRepository postRepository,
    TradeRepository tradeRepository,
    StringRedisTemplate redisTemplate
) {

    @BeforeEach
    void setUp() {
        setAuthentication();
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
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

        var result = mvcTester.get()
            .uri("/statistics/basic")
            .exchange();

        assertThat(result).hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.dau", AssertThatUtils.equalsTo(3))
            .hasPathSatisfying("$.newMembers", AssertThatUtils.equalsTo(3))
            .hasPathSatisfying("$.newPosts", AssertThatUtils.equalsTo(4))
            .hasPathSatisfying("$.newTrades", AssertThatUtils.equalsTo(5))
            .hasPathSatisfying("$.totalMembers", AssertThatUtils.equalsTo((int)memberRepository.count()))
            .hasPathSatisfying("$.totalPosts", AssertThatUtils.equalsTo((int)postRepository.count()))
            .hasPathSatisfying("$.totalTrades", AssertThatUtils.equalsTo((int)tradeRepository.count()));
    }

    private void setAuthentication() {
        MemberDetails principal = new MemberDetails(MANAGER_ID, "ADMIN", true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
