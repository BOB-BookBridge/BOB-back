package com.bob.domain.member.service.port.out;

public interface MemberMailPort {

  void sendTempPasswordProcess(String email, String tempPassword);
}
