package com.bob.domain.member.service.port.out;

public interface MemberMailPort {

  void sendTempPassword(String email, String tempPassword);
}
