package com.bob.infra.redis.adapter.in;

import static com.bob.global.exception.response.AuthenticationError.FAILED_GET_AUTHENTICATION_INFORMATION;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.infra.auth.filter.port.AuthRedisPort;
import com.bob.infra.redis.repository.RedisRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthRedisAdapter implements AuthRedisPort {

  private final RedisRepository redisRepository;

  private static final String KEY_PREFIX = "refresh:";

  @Override
  public String updateRefreshKey(String oldKey, String newKey, String value) {
    if (oldKey != null && isExistKey(oldKey)) {
      value = getValue(oldKey);
      removeKey(oldKey);
    }
    checkValue(value);
    redisRepository.setValue(KEY_PREFIX + newKey, value, Duration.ofDays(14));
    return value;
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

  private String getValue(String key) {
    return redisRepository.getValue(KEY_PREFIX + key)
        .orElseThrow(() -> new ApplicationAuthenticationException(FAILED_GET_AUTHENTICATION_INFORMATION));
  }

  private static void checkValue(String value) {
    if (value == null) {
      throw new ApplicationAuthenticationException(FAILED_GET_AUTHENTICATION_INFORMATION);
    }
  }
}
