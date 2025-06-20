package com.bob.infra.mail.adapter.in;

import com.bob.domain.member.service.port.out.MemberMailPort;
import com.bob.infra.mail.service.GoogleMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberMailAdapter implements MemberMailPort {

  private final GoogleMailService mailService;

  @Override
  public void sendTempPassword(String email, String tempPassword) {
    mailService.sendTempPasswordProcess(email, tempPassword);
  }
}
