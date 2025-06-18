package com.bob.domain.member.service.port;

public interface MemberMailPort {

  void sendTempPasswordProcess(String email, String tempPassword);
}
