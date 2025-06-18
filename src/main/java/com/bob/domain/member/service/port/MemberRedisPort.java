package com.bob.domain.member.service.port;

public interface MemberRedisPort {

  boolean isVerified(String email);

  void deleteVerified(String email);
}
