package com.bob.infra.redis.adapter.in;

import static com.bob.global.exception.response.ApplicationError.UNVERIFIED_EMAIL;

import com.bob.domain.member.service.port.out.MemberRedisPort;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.infra.redis.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberRedisAdapter implements MemberRedisPort {

  private final RedisRepository redisRepository;

  @Override
  public boolean isVerified(String email) {
    String value = redisRepository.getValue(emailVerifiedKey(email))
        .orElseThrow(() -> new ApplicationException(UNVERIFIED_EMAIL));
    return Boolean.parseBoolean(value);
  }

  @Override
  public void deleteVerified(String email) {
    redisRepository.delete(emailVerifiedKey(email));
  }

  private String emailVerifiedKey(String email) {
    return "email-verified:" + email;
  }
}
