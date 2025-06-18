package com.bob.domain.member.service.port.out;

public interface MemberRedisPort {

  boolean isVerified(String email);

  void deleteVerified(String email);
}
