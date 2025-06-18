package com.bob.infra.redis.adapter.in;

import com.bob.domain.member.service.port.out.MemberRedisPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberRedisAdapter implements MemberRedisPort {

  private final StringRedisTemplate redisTemplate;

  @Override
  public boolean isVerified(String email) {
    return Boolean.parseBoolean(redisTemplate.opsForValue().get(emailVerifiedKey(email)));
  }

  @Override
  public void deleteVerified(String email) {
    redisTemplate.delete(emailVerifiedKey(email));
  }

  private String emailVerifiedKey(String email) {
    return "email-verified:" + email;
  }
}
