package com.bob.statistics.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
@DisplayName("Redis 일일 저장소 어댑터 단위 테스트")
class RedisStatisticsDailyStoreAdapterUnitTest {

    @InjectMocks
    private RedisStatisticsDailyStoreAdapter adapter;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    void 숫자_값_해시_long_변환() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.entries(anyString())).willReturn(Map.of("metric:new_members", 3L));

        Map<String, Long> result = adapter.readDailyMetrics("member", date);

        assertThat(result.get("new_members")).isEqualTo(3L);
    }

    @Test
    void 락_획득_결과가_null이면_false_반환() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(anyString(), anyString(), any())).willReturn(null);

        boolean acquired = adapter.acquire(date, "owner");

        assertThat(acquired).isFalse();
    }
}
