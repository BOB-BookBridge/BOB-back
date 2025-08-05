package com.bob.infra.redis.adapter.in;

import com.bob.infra.auth.filter.port.AuthRedisPort;
import com.bob.infra.redis.repository.RedisRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthRedisAdapter implements AuthRedisPort {

  private static final String KEY_PREFIX = "refresh:";

  private final RedisRepository redisRepository;

  @Override
  public void updateRefreshKey(String oldKey, String newKey, String value, int expireDays) {
    if (oldKey != null && isExistKey(oldKey)) {
      removeKey(oldKey);
    }
    redisRepository.setValue(KEY_PREFIX + newKey, value, Duration.ofDays(expireDays));
  }

  @Override
  public void removeRefreshKey(String key) {
    if (isExistKey(key)) {
      removeKey(key);
    }
  }

  private boolean isExistKey(String key) {
    return redisRepository.isExist(KEY_PREFIX + key);
  }

  private void removeKey(String key) {
    redisRepository.delete(KEY_PREFIX + key);
  }
}
