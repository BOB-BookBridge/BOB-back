package com.bob.infra.redis.adapter;

import com.bob.infra.mail.service.port.MailRedisPort;
import com.bob.infra.redis.repository.RedisRepository;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MailRedisAdapter implements MailRedisPort {

  private final RedisRepository redisRepository;

  @Override
  public void saveVerified(String email, String value, int expireMinutes) {
    redisRepository.setValue(emailVerifiedKey(email), value, Duration.ofMinutes(expireMinutes));
  }

  @Override
  public void saveCode(String email, String code, int expireMinutes) {
    redisRepository.setValue(emailCodeKey(email), code, Duration.ofMinutes(expireMinutes));
  }

  @Override
  public Optional<String> getCode(String email) {
    return redisRepository.getValue(emailCodeKey(email));
  }

  @Override
  public void deleteCode(String email) {
    redisRepository.delete(emailCodeKey(email));
  }

  private String emailCodeKey(String email) {
    return "email-code:" + email;
  }

  private String emailVerifiedKey(String email) {
    return "email-verified:" + email;
  }
}