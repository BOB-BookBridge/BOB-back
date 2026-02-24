package com.bob.statistics.application;

import java.time.LocalDate;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.member.domain.repository.MemberRepository;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.core.trade.domain.repository.TradeRepository;
import com.bob.statistics.application.dto.result.StatisticsBasicMetrics;
import com.bob.statistics.application.port.in.StatisticsBasicReader;
import com.bob.statistics.application.port.out.StatisticsDailyMetricStore;

@Service
@RequiredArgsConstructor
public class StatisticsBasicQueryService implements StatisticsBasicReader {

    private final StatisticsDailyMetricStore dailyMetricStore;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final TradeRepository tradeRepository;

    @Override
    @Transactional(readOnly = true)
    public StatisticsBasicMetrics readBasic() {
        LocalDate today = LocalDate.now();

        Map<String, Long> memberMetrics = dailyMetricStore.readDailyMetrics("member", today);
        Map<String, Long> postMetrics = dailyMetricStore.readDailyMetrics("post", today);
        Map<String, Long> tradeMetrics = dailyMetricStore.readDailyMetrics("trade", today);

        return new StatisticsBasicMetrics(
            metric(memberMetrics, "new_members"),
            metric(postMetrics, "new_posts"),
            metric(tradeMetrics, "new_trades"),
            memberRepository.count(),
            postRepository.count(),
            tradeRepository.count()
        );
    }

    private static long metric(Map<String, Long> metrics, String key) {
        return metrics.getOrDefault(key, 0L);
    }
}
