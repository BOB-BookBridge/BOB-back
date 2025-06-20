package com.bob.infra.redis.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@DisplayName("REDIS 저장소 테스트")
@ExtendWith(MockitoExtension.class)
class RedisRepositoryTest {

  @InjectMocks
  private RedisRepository redisRepository;

  @Mock
  private StringRedisTemplate redisTemplate;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @Test
  @DisplayName("Redis 값 저장 - 성공 테스트")
  void redis에_값을_저장할_수_있다() {
    // given
    String key = "test-key";
    String value = "test-value";
    Duration ttl = Duration.ofMinutes(3);
    given(redisTemplate.opsForValue()).willReturn(valueOperations);

    // when
    redisRepository.setValue(key, value, ttl);

    // then
    then(valueOperations).should().set(key, value, ttl);
  }

  @Test
  @DisplayName("Redis 값 조회 - 성공 테스트")
  void redis에서_값을_조회할_수_있다() {
    // given
    String key = "test-key";
    String value = "test-value";
    given(redisTemplate.opsForValue()).willReturn(valueOperations);
    given(valueOperations.get(key)).willReturn(value);

    // when
    Optional<String> result = redisRepository.getValue(key);

    // then
    assertThat(result).contains("test-value");
  }

  @Test
  @DisplayName("Redis 값 삭제 - 성공 테스트")
  void redis에서_값을_삭제할_수_있다() {
    // given
    String key = "test-key";

    // when
    redisRepository.delete(key);

    // then
    then(redisTemplate).should().delete(key);
  }
}