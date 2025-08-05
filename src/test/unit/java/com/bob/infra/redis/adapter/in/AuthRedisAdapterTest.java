package com.bob.infra.redis.adapter.in;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.verify;

import com.bob.infra.redis.repository.RedisRepository;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("인증 관련 redis adapter 테스트")
@ExtendWith(MockitoExtension.class)
class AuthRedisAdapterTest {

  @InjectMocks
  private AuthRedisAdapter authRedisAdapter;

  @Mock
  private RedisRepository redisRepository;

  private static final String OLD_KEY = "oldKey";
  private static final String NEW_KEY = "newKey";
  private static final String VALUE = "refresh-token-value";
  private static final int EXPIRE_DAYS = 14;

  @Test
  @DisplayName("refresh key 업데이트 테스트 - key 존재 시 삭제 후 새로운 key 저장")
  void 기존_key가_존재하면_삭제하고_새로운_key를_저장한다() {
    // given
    given(redisRepository.isExist("refresh:" + OLD_KEY)).willReturn(true);

    // when
    authRedisAdapter.updateRefreshKey(OLD_KEY, NEW_KEY, VALUE, EXPIRE_DAYS);

    // then
    verify(redisRepository).delete("refresh:" + OLD_KEY);
    verify(redisRepository).setValue(eq("refresh:" + NEW_KEY), eq(VALUE), eq(Duration.ofDays(EXPIRE_DAYS)));
  }

  @Test
  @DisplayName("refresh key 업데이트 테스트 - 기존 key가 null이면 새로운 key만 저장")
  void 기존_key가_null이면_새로운_key만_저장한다() {
    // when
    authRedisAdapter.updateRefreshKey(null, NEW_KEY, VALUE, EXPIRE_DAYS);

    // then
    verify(redisRepository, never()).delete(any());
    verify(redisRepository).setValue(eq("refresh:" + NEW_KEY), eq(VALUE), eq(Duration.ofDays(EXPIRE_DAYS)));
  }

  @Test
  @DisplayName("refresh key 업데이트 테스트 - 기존 key가 존재하지 않으면 새로운 key만 저장")
  void 기존_key가_존재하지_않으면_새로운_key만_저장한다() {
    // given
    given(redisRepository.isExist("refresh:" + OLD_KEY)).willReturn(false);

    // when
    authRedisAdapter.updateRefreshKey(OLD_KEY, NEW_KEY, VALUE, EXPIRE_DAYS);

    // then
    verify(redisRepository, never()).delete("refresh:" + OLD_KEY);
    verify(redisRepository).setValue(eq("refresh:" + NEW_KEY), eq(VALUE), eq(Duration.ofDays(EXPIRE_DAYS)));
  }

  @Test
  @DisplayName("refresh key 삭제 테스트 - key가 존재하면 삭제")
  void key가_존재하면_삭제한다() {
    // given
    given(redisRepository.isExist("refresh:" + NEW_KEY)).willReturn(true);

    // when
    authRedisAdapter.removeRefreshKey(NEW_KEY);

    // then
    verify(redisRepository).delete("refresh:" + NEW_KEY);
  }

  @Test
  @DisplayName("refresh key 삭제 테스트 - key가 존재하지 않으면 아무 동작 없음")
  void key가_존재하지_않으면_아무_동작도_하지_않는다() {
    // given
    given(redisRepository.isExist("refresh:" + NEW_KEY)).willReturn(false);

    // when
    authRedisAdapter.removeRefreshKey(NEW_KEY);

    // then
    verify(redisRepository, never()).delete(any());
  }
}
